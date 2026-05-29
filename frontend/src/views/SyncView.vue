<script setup>
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatCard from '@/components/StatCard.vue'
import { useSyncStore } from '@/stores/syncStore'
import { formatInteger } from '@/utils/formatters'

const syncStore = useSyncStore()

async function syncActivities(mode) {
  await syncStore.syncActivities(mode)
}
</script>

<template>
  <AppLayout>
    <PageHeader
      title="Sync"
      description="Strava에서 러닝 활동을 가져오고 도시/국가 정보를 갱신합니다."
    />

    <div class="grid gap-4 lg:grid-cols-2">
      <section class="rounded border border-black/10 bg-white p-5">
        <h2 class="text-base font-semibold text-ink">최근 활동 동기화</h2>
        <p class="mt-2 text-sm leading-6 text-ink/60">
          최신 활동 100개만 확인합니다. 평소에는 이 옵션을 사용하세요.
        </p>
        <button
          type="button"
          class="mt-4 rounded bg-trail px-4 py-3 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="syncStore.isSyncing"
          @click="syncActivities('recent')"
        >
          {{ syncStore.isSyncing ? '동기화 중' : '최근 100개 동기화' }}
        </button>
      </section>

      <section class="rounded border border-black/10 bg-white p-5">
        <h2 class="text-base font-semibold text-ink">전체 기간 동기화</h2>
        <p class="mt-2 text-sm leading-6 text-ink/60">
          Strava 활동 페이지를 끝까지 조회합니다. 활동 수와 geocoding 호출 수에 따라 몇 분 이상 걸릴 수 있습니다.
        </p>
        <button
          type="button"
          class="mt-4 rounded border border-trail px-4 py-3 text-sm font-semibold text-trail disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="syncStore.isSyncing"
          @click="syncActivities('full')"
        >
          {{ syncStore.isSyncing ? '동기화 중' : '전체 기간 동기화' }}
        </button>
      </section>
    </div>

    <p v-if="syncStore.errorMessage" class="mt-4 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ syncStore.errorMessage }}
    </p>

    <div v-if="syncStore.lastResult" class="mt-6 grid gap-4 md:grid-cols-4">
      <StatCard label="Requested" :value="formatInteger(syncStore.lastResult.requestedCount)" />
      <StatCard label="Synced" :value="formatInteger(syncStore.lastResult.syncedCount)" />
      <StatCard label="Geocoded" :value="formatInteger(syncStore.lastResult.geocodedCount)" />
      <StatCard label="Skipped" :value="formatInteger(syncStore.lastResult.skippedCount)" />
    </div>

    <div v-if="syncStore.lastResult" class="mt-6 rounded border border-black/10 bg-white p-4 text-sm text-ink/70">
      <p>
        Mode:
        <span class="font-semibold text-ink">{{ syncStore.lastResult.mode }}</span>
      </p>
      <p class="mt-1">
        Status:
        <span class="font-semibold text-ink">{{ syncStore.lastResult.status }}</span>
      </p>
      <p class="mt-1">
        Rate limit:
        {{ syncStore.lastResult.rateLimitUsage || '-' }} /
        {{ syncStore.lastResult.rateLimitLimit || '-' }}
      </p>
      <pre class="mt-4 overflow-x-auto rounded bg-mist p-3 text-xs">{{ syncStore.lastResult }}</pre>
    </div>
  </AppLayout>
</template>
