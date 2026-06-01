<script setup>
import { formatInteger, formatKm } from '@/utils/formatters'

defineProps({
  summary: {
    type: Object,
    default: null,
  },
})
</script>

<template>
  <section class="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">Countries run</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatInteger(summary?.totalCountries) }}
      </p>
    </div>

    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">Cities run</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatInteger(summary?.totalCities) }}
      </p>
    </div>

    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">Total distance</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatKm(summary?.totalDistanceKm) }}
      </p>
    </div>

    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">Total activities</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatInteger(summary?.totalActivities) }}
      </p>
    </div>
  </section>
</template>
