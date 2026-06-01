<script setup>
import { computed, onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import { passportApi } from '@/api/passportApi'
import { useI18nStore } from '@/stores/i18nStore'
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

const i18n = useI18nStore()
const cities = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const sortKey = ref('totalDistanceKm')
const sortDirection = ref('desc')

const columns = [
  { key: 'cityName', labelKey: 'cities.city', type: 'text' },
  { key: 'regionName', labelKey: 'cities.region', type: 'text' },
  { key: 'countryName', labelKey: 'cities.country', type: 'text' },
  { key: 'activityCount', labelKey: 'cities.activities', type: 'number' },
  { key: 'totalDistanceKm', labelKey: 'cities.distance', type: 'number' },
  { key: 'firstActivityAt', labelKey: 'cities.firstRun', type: 'date' },
  { key: 'lastActivityAt', labelKey: 'cities.latestRun', type: 'date' },
]

const sortedCities = computed(() => {
  const column = columns.find((item) => item.key === sortKey.value)
  const direction = sortDirection.value === 'asc' ? 1 : -1

  return [...cities.value].sort((left, right) => {
    const compared = compareValues(left[sortKey.value], right[sortKey.value], column?.type)

    if (compared !== 0) {
      return compared * direction
    }

    const countryCompared = compareValues(left.countryName, right.countryName, 'text')
    return countryCompared === 0 ? compareValues(left.cityName, right.cityName, 'text') : countryCompared
  })
})

async function loadCities() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const data = await passportApi.getCities()
    cities.value = data.cities || []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || i18n.t('cities.loadError')
  } finally {
    isLoading.value = false
  }
}

function setSort(key) {
  if (sortKey.value === key) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
    return
  }

  sortKey.value = key
  sortDirection.value = ['cityName', 'regionName', 'countryName', 'firstActivityAt'].includes(key)
    ? 'asc'
    : 'desc'
}

function compareValues(left, right, type = 'text') {
  if (type === 'number') {
    return Number(left || 0) - Number(right || 0)
  }

  if (type === 'date') {
    return toTime(left) - toTime(right)
  }

  return String(left || '').localeCompare(String(right || ''), 'ko-KR')
}

function toTime(value) {
  return value ? new Date(value).getTime() : 0
}

function sortIcon(key) {
  if (sortKey.value !== key) {
    return '↕'
  }

  return sortDirection.value === 'asc' ? '↑' : '↓'
}

onMounted(loadCities)
</script>

<template>
  <AppLayout>
    <PageHeader :title="i18n.t('cities.title')" :description="i18n.t('cities.description')" />

    <p v-if="isLoading" class="text-sm text-ink/60">{{ i18n.t('common.loading') }}</p>
    <p v-else-if="errorMessage" class="rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </p>

    <div v-else class="overflow-x-auto rounded border border-black/10 bg-white">
      <table class="min-w-[980px] text-left text-sm">
        <thead class="bg-mist text-xs uppercase text-ink/55">
          <tr>
            <th v-for="column in columns" :key="column.key" class="px-4 py-3">
              <button
                type="button"
                class="inline-flex items-center gap-1 font-semibold hover:text-ink"
                @click="setSort(column.key)"
              >
                <span>{{ i18n.t(column.labelKey) }}</span>
                <span class="text-[10px] text-ink/45">{{ sortIcon(column.key) }}</span>
              </button>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-black/10">
          <tr v-for="city in sortedCities" :key="city.visitedPlaceId">
            <td class="px-4 py-3 font-medium text-ink">{{ city.cityName }}</td>
            <td class="px-4 py-3 text-ink/70">{{ city.regionName || '-' }}</td>
            <td class="px-4 py-3 text-ink/70">{{ city.countryName }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatInteger(city.activityCount) }}</td>
            <td class="px-4 py-3 text-ink">{{ formatKm(city.totalDistanceKm) }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatDate(city.firstActivityAt) }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatDate(city.lastActivityAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppLayout>
</template>
