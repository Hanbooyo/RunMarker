<script setup>
import { onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatCard from '@/components/StatCard.vue'
import { passportApi } from '@/api/passportApi'
import { formatInteger, formatKm } from '@/utils/formatters'

const summary = ref(null)
const isLoading = ref(false)
const errorMessage = ref('')

async function loadSummary() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    summary.value = await passportApi.getSummary()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '요약 정보를 불러오지 못했습니다.'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadSummary)
</script>

<template>
  <AppLayout>
    <PageHeader title="Dashboard" description="러닝 마커의 전체 진행 상황을 확인합니다." />

    <p v-if="isLoading" class="text-sm text-ink/60">불러오는 중입니다.</p>
    <p v-else-if="errorMessage" class="rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </p>

    <div v-if="summary" class="grid gap-4 md:grid-cols-4">
      <StatCard label="Countries" :value="formatInteger(summary.totalCountries)" />
      <StatCard label="Cities" :value="formatInteger(summary.totalCities)" />
      <StatCard label="Activities" :value="formatInteger(summary.totalActivities)" />
      <StatCard label="Distance" :value="formatKm(summary.totalDistanceKm)" />
    </div>

    <section v-if="summary" class="mt-8 grid gap-6 lg:grid-cols-2">
      <div class="rounded border border-black/10 bg-white">
        <div class="border-b border-black/10 px-4 py-3">
          <h2 class="font-semibold text-ink">상위 국가</h2>
        </div>
        <div class="divide-y divide-black/10">
          <div v-for="country in summary.countries.slice(0, 5)" :key="country.countryCode" class="flex items-center justify-between px-4 py-3">
            <span class="text-sm text-ink">{{ country.countryName }}</span>
            <span class="text-sm font-medium text-ink">{{ formatKm(country.totalDistanceKm) }}</span>
          </div>
        </div>
      </div>

      <div class="rounded border border-black/10 bg-white">
        <div class="border-b border-black/10 px-4 py-3">
          <h2 class="font-semibold text-ink">상위 도시</h2>
        </div>
        <div class="divide-y divide-black/10">
          <div v-for="city in summary.cities.slice(0, 5)" :key="city.visitedPlaceId" class="flex items-center justify-between px-4 py-3">
            <span class="text-sm text-ink">{{ city.cityName }}, {{ city.countryName }}</span>
            <span class="text-sm font-medium text-ink">{{ formatKm(city.totalDistanceKm) }}</span>
          </div>
        </div>
      </div>
    </section>
  </AppLayout>
</template>
