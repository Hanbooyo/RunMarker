<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useI18nStore } from '@/stores/i18nStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const i18n = useI18nStore()
const localDevAuthEnabled = import.meta.env.VITE_LOCAL_DEV_AUTH_ENABLED !== 'false'
const debugUserHeaderEnabled = import.meta.env.VITE_DEBUG_USER_HEADER_ENABLED === 'true'

const debugUserIdModel = computed({
  get: () => authStore.debugUserId,
  set: (value) => authStore.setDebugUserId(value),
})

async function continueWithLocalDevelopmentUser() {
  const target = route.query.redirect || '/dashboard'
  try {
    await authStore.loginForLocalDevelopment()
    router.push(target)
  } catch {
    // Error state is rendered from the auth store.
  }
}

function continueWithDebugUser() {
  const target = route.query.redirect || '/dashboard'
  router.push(target)
}
</script>

<template>
  <main class="grid min-h-screen bg-mist px-5 py-10">
    <section class="mx-auto grid w-full max-w-md content-center">
      <div class="rounded border border-black/10 bg-white p-6 shadow-sm">
        <div class="mb-8">
          <p class="text-sm font-semibold text-trail">StravaMate</p>
          <h1 class="mt-2 text-3xl font-semibold text-ink">{{ i18n.t('login.title') }}</h1>
          <p class="mt-3 text-sm leading-6 text-ink/65">
            {{ i18n.t('login.description') }}
          </p>
        </div>

        <button
          v-if="localDevAuthEnabled"
          type="button"
          class="w-full rounded bg-trail px-4 py-3 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="authStore.isLoading"
          @click="authStore.loginWithStrava"
        >
          {{ i18n.t('login.strava') }}
        </button>

        <button
          type="button"
          class="mt-3 w-full rounded border border-black/15 px-4 py-3 text-sm font-semibold text-ink disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="authStore.isLoading"
          @click="continueWithLocalDevelopmentUser"
        >
          {{ i18n.t('login.local') }}
        </button>

        <p v-if="authStore.errorMessage" class="mt-4 rounded bg-red-50 px-3 py-2 text-sm text-red-700">
          {{ authStore.errorMessage }}
        </p>

        <details v-if="debugUserHeaderEnabled" class="mt-6 border-t border-black/10 pt-5">
          <summary class="cursor-pointer text-sm font-medium text-ink">
            {{ i18n.t('login.debug') }}
          </summary>

          <div class="mt-3 flex gap-2">
            <input
              id="debug-user-id"
              v-model="debugUserIdModel"
              class="min-w-0 flex-1 rounded border border-black/15 px-3 py-2 text-sm outline-none focus:border-trail"
              :placeholder="i18n.t('login.debugPlaceholder')"
            />
            <button
              type="button"
              class="rounded border border-black/15 px-3 py-2 text-sm font-medium text-ink"
              @click="continueWithDebugUser"
            >
              {{ i18n.t('login.go') }}
            </button>
          </div>
        </details>
      </div>
    </section>
  </main>
</template>
