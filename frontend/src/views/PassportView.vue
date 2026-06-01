<script setup>
import { computed, onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import CityCard from '@/components/passport/CityCard.vue'
import CountryCard from '@/components/passport/CountryCard.vue'
import PassportMap from '@/components/passport/PassportMap.vue'
import SummaryCards from '@/components/passport/SummaryCards.vue'
import { passportApi } from '@/api/passportApi'
import { useSyncStore } from '@/stores/syncStore'
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

const syncStore = useSyncStore()

const summary = ref(null)
const countries = ref([])
const cities = ref([])
const recentPlaces = ref([])
const passportMapRef = ref(null)
const isLoading = ref(false)
const errorMessage = ref('')

const topCountries = computed(() => countries.value.slice(0, 6))
const topCities = computed(() => cities.value.slice(0, 8))

async function loadPassport() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const [summaryData, countriesData, citiesData, recentData] = await Promise.all([
      passportApi.getSummary(),
      passportApi.getCountries(),
      passportApi.getCities(),
      passportApi.getRecentPlaces(8),
    ])

    summary.value = summaryData
    countries.value = countriesData.countries || summaryData.countries || []
    cities.value = citiesData.cities || summaryData.cities || []
    recentPlaces.value = recentData.places || []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'RunMarker 데이터를 불러오지 못했습니다.'
  } finally {
    isLoading.value = false
  }
}

async function syncAndReload() {
  try {
    await syncStore.syncActivities()
    await loadPassport()
    await passportMapRef.value?.reloadMarkers()
  } catch {
    // syncStore가 화면에 표시할 오류 메시지를 관리합니다.
  }
}

onMounted(loadPassport)
</script>

<template>
  <AppLayout>
    <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
      <PageHeader
        title="RunMarker"
        description="러닝 활동을 국가와 도시 단위의 마커와 스탬프로 정리합니다."
      />

      <button
        type="button"
        class="inline-flex h-11 items-center justify-center rounded bg-trail px-4 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
        :disabled="syncStore.isSyncing"
        @click="syncAndReload"
      >
        {{ syncStore.isSyncing ? '동기화 중' : '활동 동기화' }}
      </button>
    </div>

    <div v-if="errorMessage" class="mb-5 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </div>

    <div
      v-if="syncStore.errorMessage"
      class="mb-5 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700"
    >
      {{ syncStore.errorMessage }}
    </div>

    <div
      v-if="syncStore.lastResult"
      class="mb-5 rounded border border-trail/25 bg-white p-3 text-sm text-ink/70"
    >
      요청 {{ formatInteger(syncStore.lastResult.requestedCount) }}개,
      저장 {{ formatInteger(syncStore.lastResult.syncedCount) }}개,
      위치 변환 {{ formatInteger(syncStore.lastResult.geocodedCount) }}개
    </div>

    <div v-if="isLoading" class="space-y-5">
      <div class="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <div v-for="item in 4" :key="item" class="h-28 animate-pulse rounded bg-white"></div>
      </div>
      <div class="h-[440px] animate-pulse rounded bg-white"></div>
    </div>

    <div v-else class="space-y-8">
      <SummaryCards :summary="summary" />

      <div class="grid gap-6 xl:grid-cols-[minmax(0,1fr)_360px]">
        <PassportMap ref="passportMapRef" />

        <section class="rounded border border-black/10 bg-white">
          <div class="border-b border-black/10 px-4 py-3">
            <h2 class="font-semibold text-ink">최근 방문 도시</h2>
            <p class="mt-1 text-xs text-ink/55">최근 러닝 기록 기준입니다.</p>
          </div>

          <div v-if="recentPlaces.length" class="divide-y divide-black/10">
            <div
              v-for="place in recentPlaces"
              :key="place.visitedPlaceId"
              class="flex items-center justify-between gap-3 px-4 py-3"
            >
              <div class="min-w-0">
                <p class="truncate text-sm font-medium text-ink">{{ place.cityName }}</p>
                <p class="truncate text-xs text-ink/55">{{ place.countryName }}</p>
              </div>
              <div class="shrink-0 text-right">
                <p class="text-sm font-semibold text-ink">{{ formatKm(place.totalDistanceKm) }}</p>
                <p class="text-xs text-ink/50">{{ formatDate(place.lastActivityAt) }}</p>
              </div>
            </div>
          </div>

          <div v-else class="px-4 py-8 text-sm text-ink/60">
            최근 방문 도시가 없습니다.
          </div>
        </section>
      </div>

      <section>
        <div class="mb-4 flex items-end justify-between gap-3">
          <div>
            <h2 class="text-lg font-semibold text-ink">국가 스탬프</h2>
            <p class="mt-1 text-sm text-ink/60">누적 거리 기준 상위 국가입니다.</p>
          </div>
          <p class="text-sm text-ink/50">{{ countries.length }} countries</p>
        </div>

        <div v-if="topCountries.length" class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          <CountryCard
            v-for="country in topCountries"
            :key="country.countryCode || country.countryName"
            :country="country"
          />
        </div>
        <div v-else class="rounded border border-black/10 bg-white p-6 text-sm text-ink/60">
          국가 기록이 없습니다.
        </div>
      </section>

      <section>
        <div class="mb-4 flex items-end justify-between gap-3">
          <div>
            <h2 class="text-lg font-semibold text-ink">도시 스탬프</h2>
            <p class="mt-1 text-sm text-ink/60">도시별 러닝 마커입니다.</p>
          </div>
          <p class="text-sm text-ink/50">{{ cities.length }} cities</p>
        </div>

        <div v-if="topCities.length" class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <CityCard v-for="city in topCities" :key="city.visitedPlaceId" :city="city" />
        </div>
        <div v-else class="rounded border border-black/10 bg-white p-6 text-sm text-ink/60">
          도시 기록이 없습니다.
        </div>
      </section>
    </div>
  </AppLayout>
</template>
