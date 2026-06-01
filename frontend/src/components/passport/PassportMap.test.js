import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import PassportMap from './PassportMap.vue'
import { passportApi } from '@/api/passportApi'

const addTo = vi.fn()
const clearLayers = vi.fn()
const setView = vi.fn()
const fitBounds = vi.fn()
const remove = vi.fn()
const bindPopup = vi.fn(() => ({ addTo }))
const mapMock = {
  setView,
  fitBounds,
  remove,
}
setView.mockReturnValue(mapMock)
const markerLayerMock = {
  addTo: vi.fn(() => markerLayerMock),
  clearLayers,
}

vi.mock('leaflet', () => ({
  default: {
    divIcon: vi.fn(() => ({ icon: true })),
    map: vi.fn(() => mapMock),
    tileLayer: vi.fn(() => ({ addTo })),
    layerGroup: vi.fn(() => markerLayerMock),
    marker: vi.fn(() => ({ bindPopup })),
    latLngBounds: vi.fn((points) => ({ points })),
  },
}))

vi.mock('@/api/passportApi', () => ({
  passportApi: {
    getMapMarkers: vi.fn(),
  },
}))

describe('PassportMap', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads markers and fits bounds when multiple markers exist', async () => {
    passportApi.getMapMarkers.mockResolvedValue({
      markers: [
        {
          cityName: 'Seoul',
          countryName: 'South Korea',
          latitude: 37.5665,
          longitude: 126.978,
          totalDistanceKm: 10,
          activityCount: 2,
          firstActivityAt: '2026-01-01T00:00:00Z',
          lastActivityAt: '2026-02-01T00:00:00Z',
        },
        {
          cityName: 'Tokyo',
          countryName: 'Japan',
          latitude: 35.6762,
          longitude: 139.6503,
          totalDistanceKm: 20,
          activityCount: 3,
          firstActivityAt: '2026-03-01T00:00:00Z',
          lastActivityAt: '2026-04-01T00:00:00Z',
        },
      ],
    })

    const wrapper = mount(PassportMap)
    await vi.waitFor(() => {
      expect(passportApi.getMapMarkers).toHaveBeenCalled()
      expect(fitBounds).toHaveBeenCalled()
    })

    expect(wrapper.text()).toContain('2 markers')
    expect(bindPopup).toHaveBeenCalled()
  })

  it('shows empty state and keeps default map view when there are no markers', async () => {
    passportApi.getMapMarkers.mockResolvedValue({ markers: [] })

    const wrapper = mount(PassportMap)
    await vi.waitFor(() => {
      expect(passportApi.getMapMarkers).toHaveBeenCalled()
      expect(setView).toHaveBeenCalled()
    })

    expect(wrapper.text()).toContain('No city markers to display.')
  })

  it('removes leaflet instance on destroy', async () => {
    passportApi.getMapMarkers.mockResolvedValue({ markers: [] })

    const wrapper = mount(PassportMap)
    await vi.waitFor(() => expect(passportApi.getMapMarkers).toHaveBeenCalled())
    wrapper.unmount()

    expect(remove).toHaveBeenCalled()
  })
})
