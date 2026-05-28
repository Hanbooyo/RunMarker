<script setup>
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatCard from '@/components/StatCard.vue'
import { useSyncStore } from '@/stores/syncStore'
import { formatInteger } from '@/utils/formatters'

const syncStore = useSyncStore()

async function syncActivities() {
  await syncStore.syncActivities()
}
</script>

<template>
  <AppLayout>
    <PageHeader title="Sync" description="Strava에서 최근 러닝 활동을 가져오고 도시/국가 정보를 갱신합니다." />

    <div class="rounded border border-black/10 bg-white p-5">
      <button
        type="button"
        class="rounded bg-trail px-4 py-3 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
        :disabled="syncStore.isSyncing"
        @click="syncActivities"
      >
        {{ syncStore.isSyncing ? '동기화 중' : '활동 동기화' }}
      </button>

      <p v-if="syncStore.errorMessage" class="mt-4 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
        {{ syncStore.errorMessage }}
      </p>
    </div>

    <div v-if="syncStore.lastResult" class="mt-6 grid gap-4 md:grid-cols-4">
      <StatCard label="Requested" :value="formatInteger(syncStore.lastResult.requestedCount)" />
      <StatCard label="Synced" :value="formatInteger(syncStore.lastResult.syncedCount)" />
      <StatCard label="Geocoded" :value="formatInteger(syncStore.lastResult.geocodedCount)" />
      <StatCard label="Skipped" :value="formatInteger(syncStore.lastResult.skippedCount)" />
    </div>

    <pre v-if="syncStore.lastResult" class="mt-6 overflow-x-auto rounded border border-black/10 bg-white p-4 text-xs text-ink/75">{{ syncStore.lastResult }}</pre>
  </AppLayout>
</template>
