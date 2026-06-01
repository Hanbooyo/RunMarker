<script setup>
import { onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatCard from '@/components/StatCard.vue'
import { activitiesApi } from '@/api/activitiesApi'
import { formatDate, formatKm } from '@/utils/formatters'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})

const activity = ref(null)
const isLoading = ref(false)
const errorMessage = ref('')

async function loadActivity() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    activity.value = await activitiesApi.get(props.id)
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Failed to load activity details.'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadActivity)
</script>

<template>
  <AppLayout>
    <PageHeader
      title="Activity Detail"
      description="Review basic activity information and the place derived from the start location."
    />

    <p v-if="isLoading" class="text-sm text-ink/60">Loading.</p>
    <p v-else-if="errorMessage" class="rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ errorMessage }}
    </p>

    <section v-if="activity" class="space-y-6">
      <div class="rounded border border-black/10 bg-white p-5">
        <p class="text-sm text-ink/60">{{ formatDate(activity.startDate) }}</p>
        <h2 class="mt-1 text-2xl font-semibold text-ink">{{ activity.name }}</h2>
        <p class="mt-2 text-sm text-ink/65">
          {{ activity.cityName || 'Unknown city' }}, {{ activity.countryName || 'Unknown country' }}
        </p>
      </div>

      <div class="grid gap-4 md:grid-cols-4">
        <StatCard label="Distance" :value="formatKm(activity.distanceKilometers)" />
        <StatCard label="Moving Time" :value="`${activity.movingTimeSeconds || 0}s`" />
        <StatCard label="Latitude" :value="activity.startLatitude || '-'" />
        <StatCard label="Longitude" :value="activity.startLongitude || '-'" />
      </div>
    </section>
  </AppLayout>
</template>
