<script setup>
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const router = useRouter()

const navItems = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/passport', label: 'Passport' },
  { to: '/countries', label: 'Countries' },
  { to: '/cities', label: 'Cities' },
  { to: '/activities', label: 'Activities' },
  { to: '/sync', label: 'Sync' },
]

async function logout() {
  await authStore.logoutLocal()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-mist">
    <header class="border-b border-black/10 bg-white">
      <div class="mx-auto flex max-w-7xl items-center justify-between px-5 py-4">
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
            {{ item.label }}
          </RouterLink>
        </nav>

        <button
          type="button"
          class="rounded border border-black/10 px-3 py-2 text-sm text-ink/70"
          @click="logout"
        >
          로그아웃
        </button>
      </div>

      <nav class="mx-auto flex max-w-7xl gap-1 overflow-x-auto px-5 pb-3 md:hidden">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="shrink-0 rounded px-3 py-2 text-sm font-medium text-ink/70"
          active-class="bg-mist text-ink"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
    </header>

    <main class="mx-auto max-w-7xl px-5 py-6">
      <slot />
    </main>
  </div>
</template>
