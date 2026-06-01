<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useI18nStore } from '@/stores/i18nStore'

const authStore = useAuthStore()
const i18n = useI18nStore()
const router = useRouter()
const now = ref(Date.now())
let timerId = null

const navItems = [
  { to: '/dashboard', labelKey: 'nav.dashboard' },
  { to: '/passport', labelKey: 'nav.passport' },
  { to: '/countries', labelKey: 'nav.countries' },
  { to: '/cities', labelKey: 'nav.cities' },
  { to: '/activities', labelKey: 'nav.activities' },
  { to: '/sync', labelKey: 'nav.sync' },
]

const remainingSeconds = computed(() => {
  const expiresAt = authStore.session?.expiresAtEpochMillis || 0
  return Math.max(0, Math.ceil((expiresAt - now.value) / 1000))
})

const sessionTimeText = computed(() => {
  const seconds = remainingSeconds.value
  const minutes = Math.floor(seconds / 60)
  const restSeconds = seconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(restSeconds).padStart(2, '0')}`
})

const showSessionStatus = computed(() => Boolean(authStore.session?.authenticated))

async function logout() {
  await authStore.logoutLocal()
  router.push('/login')
}

async function refreshSession() {
  await authStore.refreshSession()
  now.value = Date.now()
}

onMounted(async () => {
  await authStore.fetchSession()
  timerId = window.setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onBeforeUnmount(() => {
  if (timerId) {
    window.clearInterval(timerId)
  }
})
</script>

<template>
  <div class="min-h-screen bg-mist">
    <header class="border-b border-black/10 bg-white">
      <div class="mx-auto flex max-w-7xl flex-col gap-3 px-4 py-4 sm:px-5 md:flex-row md:items-center md:justify-between">
        <div class="flex w-full items-center justify-between gap-3 md:w-auto">
          <RouterLink to="/dashboard" class="flex min-w-0 items-center gap-3">
            <img
              src="/app-icon.png"
              alt=""
              class="h-9 w-9 shrink-0 rounded object-cover"
            />
            <div class="min-w-0">
              <p class="truncate text-sm font-semibold text-ink">StravaMate</p>
              <p class="truncate text-xs text-ink/60">Running Passport</p>
            </div>
          </RouterLink>

          <button
            type="button"
            class="shrink-0 rounded border border-black/10 px-3 py-2 text-xs font-semibold text-ink/70 md:hidden"
            :aria-label="i18n.t('common.language')"
            @click="i18n.toggleLocale"
          >
            {{ i18n.locale === 'ko' ? 'EN' : 'KO' }}
          </button>
        </div>

        <nav class="hidden items-center gap-1 md:flex">
          <RouterLink
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="rounded px-3 py-2 text-sm font-medium text-ink/70 hover:bg-mist hover:text-ink"
            active-class="bg-mist text-ink"
          >
            {{ i18n.t(item.labelKey) }}
          </RouterLink>
        </nav>

        <div class="flex w-full flex-wrap items-center gap-2 md:w-auto md:justify-end">
          <button
            type="button"
            class="hidden rounded border border-black/10 px-3 py-2 text-xs font-semibold text-ink/70 md:inline-flex"
            :aria-label="i18n.t('common.language')"
            @click="i18n.toggleLocale"
          >
            {{ i18n.locale === 'ko' ? 'EN' : 'KO' }}
          </button>

          <div
            v-if="showSessionStatus"
            class="flex min-w-0 flex-1 items-center justify-between gap-2 rounded border border-black/10 px-3 py-2 text-xs text-ink/65 sm:flex-none"
          >
            <span class="whitespace-nowrap">{{ i18n.t('common.session') }} {{ sessionTimeText }}</span>
            <button type="button" class="shrink-0 font-semibold text-trail" @click="refreshSession">
              {{ i18n.t('common.refresh') }}
            </button>
          </div>

          <button
            type="button"
            class="shrink-0 rounded border border-black/10 px-3 py-2 text-sm text-ink/70"
            @click="logout"
          >
            {{ i18n.t('common.logout') }}
          </button>
        </div>
      </div>

      <nav class="mx-auto flex max-w-7xl gap-1 overflow-x-auto px-4 pb-3 sm:px-5 md:hidden">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="shrink-0 rounded px-3 py-2 text-sm font-medium text-ink/70"
          active-class="bg-mist text-ink"
        >
          {{ i18n.t(item.labelKey) }}
        </RouterLink>
      </nav>
    </header>

    <main class="mx-auto min-w-0 max-w-7xl px-4 py-5 sm:px-5 sm:py-6">
      <slot />
    </main>
  </div>
</template>
