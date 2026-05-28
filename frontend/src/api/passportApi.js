import { http } from './http'
import { demoCities, demoCountries, demoMarkers, demoSummary, isDemoMode } from './demoData'

export const passportApi = {
  async getSummary() {
    if (isDemoMode) {
      return demoSummary
    }

    const { data } = await http.get('/api/passport/summary')
    return data
  },

  async getCountries() {
    if (isDemoMode) {
      return { countries: demoCountries }
    }

    const { data } = await http.get('/api/passport/countries')
    return data
  },

  async getCities() {
    if (isDemoMode) {
      return { cities: demoCities }
    }

    const { data } = await http.get('/api/passport/cities')
    return data
  },

  async getMapMarkers() {
    if (isDemoMode) {
      return { markers: demoMarkers }
    }

    const { data } = await http.get('/api/passport/map-markers')
    return data
  },

  async getRecentPlaces(limit = 10) {
    if (isDemoMode) {
      return { places: demoCities.slice(0, limit) }
    }

    const { data } = await http.get('/api/passport/recent-places', {
      params: { limit },
    })
    return data
  },
}
