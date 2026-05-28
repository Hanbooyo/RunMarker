<script setup>
import { onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import { passportApi } from '@/api/passportApi'
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

const countries = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

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

onMounted(loadCountries)
</script>

<template>
  <AppLayout>
    <PageHeader title="Countries" description="국가별 누적 거리와 활동 수를 비교합니다." />

    <p v-if="isLoading" class="text-sm text-ink/60">불러오는 중입니다.</p>
    <p v-else-if="errorMessage" class="rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </p>

    <div class="overflow-hidden rounded border border-black/10 bg-white">
      <table class="w-full min-w-[760px] text-left text-sm">
        <thead class="bg-mist text-xs uppercase text-ink/55">
          <tr>
            <th class="px-4 py-3">국가</th>
            <th class="px-4 py-3">도시</th>
            <th class="px-4 py-3">활동</th>
            <th class="px-4 py-3">거리</th>
            <th class="px-4 py-3">첫 러닝</th>
            <th class="px-4 py-3">최근 러닝</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-black/10">
          <tr v-for="country in countries" :key="country.countryCode || country.countryName">
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
