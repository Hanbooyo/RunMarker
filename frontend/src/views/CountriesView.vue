<script setup>
import { computed, onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import { passportApi } from '@/api/passportApi'
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

const countries = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const sortKey = ref('totalDistanceKm')
const sortDirection = ref('desc')

const columns = [
  { key: 'countryName', label: '국가', type: 'text' },
  { key: 'cityCount', label: '도시', type: 'number' },
  { key: 'activityCount', label: '활동', type: 'number' },
  { key: 'totalDistanceKm', label: '거리', type: 'number' },
  { key: 'firstActivityAt', label: '첫 러닝', type: 'date' },
  { key: 'lastActivityAt', label: '최근 러닝', type: 'date' },
]

const sortedCountries = computed(() => {
  const column = columns.find((item) => item.key === sortKey.value)
  const direction = sortDirection.value === 'asc' ? 1 : -1

  return [...countries.value].sort((left, right) => {
    const compared = compareValues(left[sortKey.value], right[sortKey.value], column?.type)
    return compared === 0
      ? compareValues(left.countryName, right.countryName, 'text')
      : compared * direction
  })
})

async function loadCountries() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const data = await passportApi.getCountries()
    countries.value = data.countries || []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '국가 목록을 불러오지 못했습니다.'
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
  sortDirection.value = ['countryName', 'firstActivityAt'].includes(key) ? 'asc' : 'desc'
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

onMounted(loadCountries)
</script>

<template>
  <AppLayout>
    <PageHeader title="Countries" description="국가별 누적 거리와 활동 수를 비교합니다." />

    <p v-if="isLoading" class="text-sm text-ink/60">불러오는 중입니다.</p>
    <p v-else-if="errorMessage" class="rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </p>

    <div v-else class="overflow-hidden rounded border border-black/10 bg-white">
      <table class="w-full min-w-[760px] text-left text-sm">
        <thead class="bg-mist text-xs uppercase text-ink/55">
          <tr>
            <th v-for="column in columns" :key="column.key" class="px-4 py-3">
              <button
                type="button"
                class="inline-flex items-center gap-1 font-semibold hover:text-ink"
                @click="setSort(column.key)"
              >
                <span>{{ column.label }}</span>
                <span class="text-[10px] text-ink/45">{{ sortIcon(column.key) }}</span>
              </button>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-black/10">
          <tr v-for="country in sortedCountries" :key="country.countryCode || country.countryName">
            <td class="px-4 py-3 font-medium text-ink">{{ country.countryName }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatInteger(country.cityCount) }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatInteger(country.activityCount) }}</td>
            <td class="px-4 py-3 text-ink">{{ formatKm(country.totalDistanceKm) }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatDate(country.firstActivityAt) }}</td>
            <td class="px-4 py-3 text-ink/70">{{ formatDate(country.lastActivityAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppLayout>
</template>
