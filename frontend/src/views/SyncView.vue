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
  if (!syncStore.isSyncing) {
    return ''
  }

  if (!syncStore.lastResult) {
    return syncStore.activeMode === 'full'
      ? 'Starting the full sync job.'
      : 'Requesting recent activity information.'
  }

  const requested = formatInteger(syncStore.lastResult.requestedCount || 0)
  const synced = formatInteger(syncStore.lastResult.syncedCount || 0)
  const geocoded = formatInteger(syncStore.lastResult.geocodedCount || 0)

  return `${requested} activities received. Saved ${synced}, geocoded ${geocoded}.`
})

const activeSyncLabel = computed(() => (
  syncStore.activeMode === 'full' ? 'Full sync in progress' : 'Recent sync in progress'
))

const statDescriptions = [
  {
    labelKey: 'sync.requested',
    text: 'Total activities read from the connected activity account. This may include non-run activities.',
  },
  {
    labelKey: 'sync.synced',
    text: 'New running activities saved to RunMarker. Activities already stored are not duplicated.',
  },
  {
    labelKey: 'sync.geocoded',
    text: 'Activities whose start coordinates were converted into city and country information.',
  },
  {
    labelKey: 'sync.skipped',
    text: 'Activities skipped because they were not runs, had no start coordinates, or were already saved.',
  },
]

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
        <p class="mt-3 rounded bg-mist px-3 py-2 text-xs leading-5 text-ink/60">
          Full-history sync can take a while depending on activity count and geocoding volume. Keep this page open to
          watch progress.
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
        <div class="flex min-w-0 items-start gap-3">
          <div
            class="mt-0.5 h-5 w-5 shrink-0 animate-spin rounded-full border-2 border-trail/25 border-t-trail"
            aria-hidden="true"
          ></div>
          <div class="min-w-0">
            <p class="font-medium text-ink/60">
              {{ activeSyncLabel }}
            </p>
            <p class="mt-1">{{ progressText }}</p>
          </div>
        </div>
        <div class="h-2 w-full overflow-hidden rounded bg-mist sm:w-56">
          <div class="h-full w-1/2 animate-pulse rounded bg-trail/40"></div>
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

    <section v-if="syncStore.lastResult" class="mt-4 rounded border border-black/10 bg-white p-4">
      <h2 class="text-sm font-semibold text-ink">Sync metric definitions</h2>
      <dl class="mt-3 grid gap-3 text-sm sm:grid-cols-2">
        <div
          v-for="item in statDescriptions"
          :key="item.labelKey"
          class="rounded bg-mist px-3 py-2"
        >
          <dt class="text-xs font-semibold uppercase tracking-wide text-ink/50">
            {{ i18n.t(item.labelKey) }}
          </dt>
          <dd class="mt-1 leading-5 text-ink/70">{{ item.text }}</dd>
        </div>
      </dl>
    </section>

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
    </div>
  </AppLayout>
</template>
