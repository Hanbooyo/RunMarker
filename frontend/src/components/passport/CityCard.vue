<script setup>
import { formatDate, formatInteger, formatKm } from '@/utils/formatters'

defineProps({
  city: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="rounded border border-black/10 bg-white p-4">
    <div class="flex items-start justify-between gap-3">
      <div>
        <p class="text-xs font-semibold uppercase text-moss">{{ city.countryName }}</p>
        <h3 class="mt-1 text-lg font-semibold text-ink">{{ city.cityName }}</h3>
        <p class="mt-1 text-xs text-ink/55">{{ city.regionName || city.countryCode || '-' }}</p>
      </div>
      <div class="grid h-14 w-14 place-items-center rounded-full border border-dashed border-moss/50 text-sm font-semibold text-moss">
        {{ city.countryCode || '--' }}
      </div>
    </div>

    <dl class="mt-5 grid grid-cols-2 gap-3 text-sm">
      <div>
        <dt class="text-ink/50">누적 거리</dt>
        <dd class="mt-1 font-semibold text-ink">{{ formatKm(city.totalDistanceKm) }}</dd>
      </div>
      <div>
        <dt class="text-ink/50">활동 수</dt>
        <dd class="mt-1 font-semibold text-ink">{{ formatInteger(city.activityCount) }}</dd>
      </div>
      <div class="col-span-2">
        <dt class="text-ink/50">최근 러닝 날짜</dt>
        <dd class="mt-1 font-semibold text-ink">{{ formatDate(city.lastActivityAt) }}</dd>
      </div>
    </dl>
  </article>
</template>
