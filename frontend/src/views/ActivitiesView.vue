<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import { activitiesApi } from '@/api/activitiesApi'
import { formatDate, formatKm } from '@/utils/formatters'

const activities = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

async function loadActivities() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const data = await activitiesApi.list({ page: 0, size: 50 })
    activities.value = data.activities || []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '활동 목록을 불러오지 못했습니다.'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadActivities)
</script>

<template>
  <AppLayout>
    <PageHeader title="Activities" description="저장된 Strava 러닝 활동 목록입니다." />

    <p v-if="isLoading" class="text-sm text-ink/60">불러오는 중입니다.</p>
    <p v-else-if="errorMessage" class="rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </p>

    <div class="overflow-hidden rounded border border-black/10 bg-white">
      <table class="w-full min-w-[860px] text-left text-sm">
        <thead class="bg-mist text-xs uppercase text-ink/55">
          <tr>
            <th class="px-4 py-3">활동</th>
            <th class="px-4 py-3">날짜</th>
            <th class="px-4 py-3">거리</th>
            <th class="px-4 py-3">도시</th>
            <th class="px-4 py-3">국가</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-black/10">
          <tr v-for="activity in activities" :key="activity.id">
            <td class="px-4 py-3">
              <RouterLink :to="`/activities/${activity.id}`" class="font-medium text-trail">
                {{ activity.name }}
              </RouterLink>
            </td>
            <td class="px-4 py-3 text-ink/70">{{ formatDate(activity.startDate) }}</td>
            <td class="px-4 py-3 text-ink">{{ formatKm(activity.distanceKilometers) }}</td>
            <td class="px-4 py-3 text-ink/70">{{ activity.cityName || '-' }}</td>
            <td class="px-4 py-3 text-ink/70">{{ activity.countryName || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppLayout>
</template>
