<script setup>
import { computed } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatCard from '@/components/StatCard.vue'
import { useI18nStore } from '@/stores/i18nStore'
import { useSyncStore } from '@/stores/syncStore'
import { formatInteger } from '@/utils/formatters'

const i18n = useI18nStore()
const syncStore = useSyncStore()

const progressText = computed(() => {
  if (!syncStore.lastResult || !syncStore.isSyncing) {
    return ''
  }

  const requested = formatInteger(syncStore.lastResult.requestedCount || 0)
  const synced = formatInteger(syncStore.lastResult.syncedCount || 0)
  const geocoded = formatInteger(syncStore.lastResult.geocodedCount || 0)

  return `${requested}건의 Activities 정보를 받아왔습니다. 저장 ${synced}건, 위치 변환 ${geocoded}건`
})

async function syncActivities(mode) {
  await syncStore.syncActivities(mode)
}
</script>

<template>
  <AppLayout>
    <PageHeader
      :title="i18n.t('sync.title')"
      :description="i18n.t('sync.description')"
    />

    <div class="grid gap-4 lg:grid-cols-2">
      <section class="rounded border border-black/10 bg-white p-5">
        <h2 class="text-base font-semibold text-ink">{{ i18n.t('sync.recentTitle') }}</h2>
        <p class="mt-2 text-sm leading-6 text-ink/60">
          {{ i18n.t('sync.recentDescription') }}
        </p>
        <button
          type="button"
          class="mt-4 rounded bg-trail px-4 py-3 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="syncStore.isSyncing"
          @click="syncActivities('recent')"
        >
          {{ syncStore.isSyncing ? i18n.t('sync.syncing') : i18n.t('sync.recentButton') }}
        </button>
      </section>

      <section class="rounded border border-black/10 bg-white p-5">
        <h2 class="text-base font-semibold text-ink">{{ i18n.t('sync.fullTitle') }}</h2>
        <p class="mt-2 text-sm leading-6 text-ink/60">
          {{ i18n.t('sync.fullDescription') }}
        </p>
        <button
          type="button"
          class="mt-4 rounded border border-trail px-4 py-3 text-sm font-semibold text-trail disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="syncStore.isSyncing"
          @click="syncActivities('full')"
        >
          {{ syncStore.isSyncing ? i18n.t('sync.syncing') : i18n.t('sync.fullButton') }}
        </button>
      </section>
    </div>

    <div
      v-if="progressText"
      class="mt-5 rounded border border-trail/20 bg-white/60 p-4 text-sm text-ink/50 shadow-sm"
    >
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p class="font-medium text-ink/60">전체 동기화 진행 중</p>
          <p class="mt-1">{{ progressText }}</p>
        </div>
        <div class="h-2 w-full overflow-hidden rounded bg-mist sm:w-56">
          <div class="h-full w-2/3 animate-pulse rounded bg-trail/40"></div>
        </div>
      </div>
    </div>

    <p v-if="syncStore.errorMessage" class="mt-4 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
      {{ syncStore.errorMessage }}
    </p>

    <div v-if="syncStore.lastResult" class="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <StatCard :label="i18n.t('sync.requested')" :value="formatInteger(syncStore.lastResult.requestedCount)" />
      <StatCard :label="i18n.t('sync.synced')" :value="formatInteger(syncStore.lastResult.syncedCount)" />
      <StatCard :label="i18n.t('sync.geocoded')" :value="formatInteger(syncStore.lastResult.geocodedCount)" />
      <StatCard :label="i18n.t('sync.skipped')" :value="formatInteger(syncStore.lastResult.skippedCount)" />
    </div>

    <div v-if="syncStore.lastResult" class="mt-6 rounded border border-black/10 bg-white p-4 text-sm text-ink/70">
      <p>
        {{ i18n.t('sync.mode') }}:
        <span class="font-semibold text-ink">{{ syncStore.lastResult.mode }}</span>
      </p>
      <p class="mt-1">
        {{ i18n.t('sync.status') }}:
        <span class="font-semibold text-ink">{{ syncStore.lastResult.status }}</span>
      </p>
      <p class="mt-1">
        {{ i18n.t('sync.rateLimit') }}:
        {{ syncStore.lastResult.rateLimitUsage || '-' }} /
        {{ syncStore.lastResult.rateLimitLimit || '-' }}
      </p>
      <pre class="mt-4 max-w-full overflow-x-auto rounded bg-mist p-3 text-xs">{{ syncStore.lastResult }}</pre>
    </div>
  </AppLayout>
</template>
