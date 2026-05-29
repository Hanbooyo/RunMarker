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
      <div class="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-3 px-5 py-4">
        <RouterLink to="/dashboard" class="flex items-center gap-3">
          <div class="grid h-9 w-9 place-items-center rounded bg-trail text-sm font-bold text-white">
            SM
          </div>
          <div>
            <p class="text-sm font-semibold text-ink">StravaMate</p>
            <p class="text-xs text-ink/60">Running Passport</p>
          </div>
        </RouterLink>

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

        <div class="flex items-center gap-2">
          <button
            type="button"
            class="rounded border border-black/10 px-3 py-2 text-xs font-semibold text-ink/70"
            :aria-label="i18n.t('common.language')"
            @click="i18n.toggleLocale"
          >
            {{ i18n.locale === 'ko' ? 'EN' : 'KO' }}
          </button>

          <div
            v-if="showSessionStatus"
            class="flex items-center gap-2 rounded border border-black/10 px-3 py-2 text-xs text-ink/65"
          >
            <span>{{ i18n.t('common.session') }} {{ sessionTimeText }}</span>
            <button type="button" class="font-semibold text-trail" @click="refreshSession">
              {{ i18n.t('common.refresh') }}
            </button>
          </div>

          <button
            type="button"
            class="rounded border border-black/10 px-3 py-2 text-sm text-ink/70"
            @click="logout"
          >
            {{ i18n.t('common.logout') }}
          </button>
        </div>
      </div>

      <nav class="mx-auto flex max-w-7xl gap-1 overflow-x-auto px-5 pb-3 md:hidden">
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

    <main class="mx-auto max-w-7xl px-5 py-6">
      <slot />
    </main>
  </div>
</template>
