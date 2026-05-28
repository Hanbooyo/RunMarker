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
          class="w-full rounded bg-trail px-4 py-3 text-sm font-semibold text-white"
          @click="authStore.loginWithStrava"
        >
          Strava로 로그인
        </button>

        <div class="mt-6 border-t border-black/10 pt-5">
          <label class="text-sm font-medium text-ink" for="debug-user-id">Local User ID</label>
          <div class="mt-2 flex gap-2">
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
        </div>
      </div>
    </section>
  </main>
</template>
