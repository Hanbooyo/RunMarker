<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import L from 'leaflet'
import { passportApi } from '@/api/passportApi'
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

const DEFAULT_CENTER = [37.5665, 126.978]
const DEFAULT_ZOOM = 3

const mapElement = ref(null)
const markers = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

let map = null
let markerLayer = null

const passportIcon = L.divIcon({
  className: '',
  html: `
    <div class="grid h-8 w-8 place-items-center rounded-full border-2 border-white bg-[#d86635] text-[10px] font-bold text-white shadow">
      RUN
    </div>
  `,
  iconSize: [32, 32],
  iconAnchor: [16, 16],
  popupAnchor: [0, -18],
})

function initializeMap() {
  if (!mapElement.value || map) {
    return
  }

  map = L.map(mapElement.value, {
    zoomControl: true,
    scrollWheelZoom: false,
  }).setView(DEFAULT_CENTER, DEFAULT_ZOOM)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 18,
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map)

  markerLayer = L.layerGroup().addTo(map)
}

function isValidMarker(marker) {
  return marker.latitude !== null
    && marker.latitude !== undefined
    && marker.longitude !== null
    && marker.longitude !== undefined
}

function buildPopupHtml(marker) {
  return `
    <div style="min-width: 200px">
      <div style="font-weight: 700; margin-bottom: 6px;">${escapeHtml(marker.cityName)}</div>
      <div style="color: #4b5563; margin-bottom: 8px;">${escapeHtml(marker.countryName)}</div>
      <div>누적 거리: <strong>${formatKm(marker.totalDistanceKm)}</strong></div>
      <div>활동 수: <strong>${formatInteger(marker.activityCount)}</strong></div>
      <div>첫 러닝: <strong>${formatDate(marker.firstActivityAt)}</strong></div>
      <div>최근 러닝: <strong>${formatDate(marker.lastActivityAt)}</strong></div>
    </div>
  `
}

function escapeHtml(value) {
  return String(value || '-')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;')
}

function renderMarkers() {
  if (!map || !markerLayer) {
    return
  }

  markerLayer.clearLayers()

  const validMarkers = markers.value.filter(isValidMarker)

  validMarkers.forEach((marker) => {
    L.marker([Number(marker.latitude), Number(marker.longitude)], {
      icon: passportIcon,
    })
      .bindPopup(buildPopupHtml(marker))
      .addTo(markerLayer)
  })

  if (validMarkers.length === 0) {
    map.setView(DEFAULT_CENTER, DEFAULT_ZOOM)
    return
  }

  if (validMarkers.length === 1) {
    map.setView([Number(validMarkers[0].latitude), Number(validMarkers[0].longitude)], 10)
    return
  }

  const bounds = L.latLngBounds(
    validMarkers.map((marker) => [Number(marker.latitude), Number(marker.longitude)]),
  )

  map.fitBounds(bounds, {
    padding: [32, 32],
    maxZoom: 10,
  })
}

async function loadMarkers() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const data = await passportApi.getMapMarkers()
    markers.value = data.markers || []
    await nextTick()
    initializeMap()
    renderMarkers()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '지도 마커를 불러오지 못했습니다.'
    markers.value = []
    renderMarkers()
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await nextTick()
  initializeMap()
  await loadMarkers()
})

onBeforeUnmount(() => {
  if (map) {
    map.remove()
    map = null
    markerLayer = null
  }
})

defineExpose({
  reloadMarkers: loadMarkers,
})
</script>

<template>
  <section class="rounded border border-black/10 bg-white">
    <div class="flex items-center justify-between border-b border-black/10 px-4 py-3">
      <div>
        <h2 class="font-semibold text-ink">방문 도시 지도</h2>
        <p class="mt-1 text-xs text-ink/55">러닝 시작 위치 기준으로 도시 마커를 표시합니다.</p>
      </div>
      <span class="text-xs font-medium text-ink/50">{{ markers.length }} markers</span>
    </div>

    <div v-if="errorMessage" class="border-b border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
      {{ errorMessage }}
    </div>

    <div class="relative">
      <div ref="mapElement" class="h-[360px] w-full md:h-[440px]"></div>

      <div
        v-if="isLoading"
        class="absolute inset-0 grid place-items-center bg-white/70 text-sm font-medium text-ink/60"
      >
        지도 데이터를 불러오는 중입니다.
      </div>

      <div
        v-else-if="!markers.length"
        class="pointer-events-none absolute inset-0 grid place-items-center bg-white/75 text-sm text-ink/60"
      >
        표시할 도시 마커가 없습니다.
      </div>
    </div>
  </section>
</template>
