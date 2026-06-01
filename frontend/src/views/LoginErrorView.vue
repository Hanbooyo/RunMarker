<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const message = computed(() => {
  switch (route.query.error) {
    case 'missing_strava_config':
      return 'The activity account connection is not configured yet.'
    case 'strava_denied':
      return 'The activity account authorization was cancelled.'
    case 'auth_failed':
      return 'An error occurred while processing the login.'
    default:
      return 'Login failed.'
  }
})
</script>

<template>
  <main class="grid min-h-screen bg-mist px-5 py-10">
    <section class="mx-auto grid w-full max-w-md content-center">
      <div class="rounded border border-black/10 bg-white p-6 shadow-sm">
        <p class="text-sm font-semibold text-red-600">Login Error</p>
        <h1 class="mt-2 text-2xl font-semibold text-ink">Login could not be completed</h1>
        <p class="mt-3 text-sm leading-6 text-ink/65">{{ message }}</p>

        <RouterLink
          to="/login"
          class="mt-6 inline-flex w-full justify-center rounded bg-trail px-4 py-3 text-sm font-semibold text-white"
        >
          Back to login
        </RouterLink>
      </div>
    </section>
  </main>
</template>
