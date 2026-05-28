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
      <p class="text-xs font-semibold uppercase text-ink/50">달린 국가</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatInteger(summary?.totalCountries) }}
      </p>
    </div>

    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">달린 도시</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatInteger(summary?.totalCities) }}
      </p>
    </div>

    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">총 거리</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatKm(summary?.totalDistanceKm) }}
      </p>
    </div>

    <div class="rounded border border-black/10 bg-white p-4">
      <p class="text-xs font-semibold uppercase text-ink/50">총 활동</p>
      <p class="mt-2 text-3xl font-semibold text-ink">
        {{ formatInteger(summary?.totalActivities) }}
      </p>
    </div>
  </section>
</template>
