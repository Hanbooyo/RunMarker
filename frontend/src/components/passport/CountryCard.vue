<script setup>
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

defineProps({
  country: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="rounded border border-black/10 bg-white p-4">
    <div class="flex items-start justify-between gap-3">
      <div>
        <p class="text-xs font-semibold uppercase text-trail">{{ country.countryCode || 'N/A' }}</p>
        <h3 class="mt-1 text-lg font-semibold text-ink">{{ country.countryName }}</h3>
      </div>
      <div class="rounded border border-dashed border-trail/50 px-3 py-2 text-center">
        <p class="text-lg font-semibold text-trail">{{ formatInteger(country.activityCount) }}</p>
        <p class="text-[11px] text-ink/50">runs</p>
      </div>
    </div>

    <dl class="mt-5 grid grid-cols-2 gap-3 text-sm">
      <div>
        <dt class="text-ink/50">누적 거리</dt>
        <dd class="mt-1 font-semibold text-ink">{{ formatKm(country.totalDistanceKm) }}</dd>
      </div>
      <div>
        <dt class="text-ink/50">도시 수</dt>
        <dd class="mt-1 font-semibold text-ink">{{ formatInteger(country.cityCount) }}</dd>
      </div>
      <div class="col-span-2">
        <dt class="text-ink/50">첫 러닝 날짜</dt>
        <dd class="mt-1 font-semibold text-ink">{{ formatDate(country.firstActivityAt) }}</dd>
      </div>
    </dl>
  </article>
</template>
