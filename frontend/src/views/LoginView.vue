<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

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
          <h1 class="mt-2 text-3xl font-semibold text-ink">Running Passport</h1>
          <p class="mt-3 text-sm leading-6 text-ink/65">
            Strava 러닝 활동을 도시와 국가 단위의 러닝 여권으로 정리합니다.
          </p>
        </div>

        <button
          type="button"
          class="w-full rounded bg-trail px-4 py-3 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="authStore.isLoading"
          @click="authStore.loginWithStrava"
        >
          Strava로 로그인
        </button>

        <button
          type="button"
          class="mt-3 w-full rounded border border-black/15 px-4 py-3 text-sm font-semibold text-ink disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="authStore.isLoading"
          @click="continueWithLocalDevelopmentUser"
        >
          로컬 개발용 로그인
        </button>

        <p v-if="authStore.errorMessage" class="mt-4 rounded bg-red-50 px-3 py-2 text-sm text-red-700">
          {{ authStore.errorMessage }}
        </p>

        <details class="mt-6 border-t border-black/10 pt-5">
          <summary class="cursor-pointer text-sm font-medium text-ink">
            기존 Local User ID로 접속
          </summary>

          <div class="mt-3 flex gap-2">
            <input
              id="debug-user-id"
              v-model="debugUserIdModel"
              class="min-w-0 flex-1 rounded border border-black/15 px-3 py-2 text-sm outline-none focus:border-trail"
              placeholder="예: 1"
            />
            <button
              type="button"
              class="rounded border border-black/15 px-3 py-2 text-sm font-medium text-ink"
              @click="continueWithDebugUser"
            >
              이동
            </button>
          </div>
        </details>
      </div>
    </section>
  </main>
</template>
