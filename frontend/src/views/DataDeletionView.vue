<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()
const isDeleting = ref(false)
const deleteMessage = ref('')
const deleteError = ref('')
const canDelete = computed(() => authStore.isAuthenticated)

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    await authStore.fetchMe()
  }
})

async function deleteMyData() {
  const confirmed = window.confirm(
    'This permanently deletes your RunMarker account data, imported activities, tokens, places, and sync logs. Continue?'
  )
  if (!confirmed) {
    return
  }

  isDeleting.value = true
  deleteMessage.value = ''
  deleteError.value = ''

  try {
    await authStore.deleteAccount()
    deleteMessage.value = 'Your RunMarker data has been deleted.'
    router.push({ name: 'login' })
  } catch (error) {
    deleteError.value = error.response?.data?.message || 'Deletion failed. Please try again or contact support.'
  } finally {
    isDeleting.value = false
  }
}
</script>

<template>
  <main class="min-h-screen bg-mist px-5 py-10">
    <section class="mx-auto max-w-3xl rounded border border-black/10 bg-white p-6 shadow-sm">
      <p class="text-sm font-semibold text-trail">RunMarker</p>
      <h1 class="mt-2 text-3xl font-semibold text-ink">Data Deletion</h1>
      <p class="mt-2 text-sm text-ink/55">Last updated: June 1, 2026</p>

      <div class="mt-6 space-y-5 text-sm leading-6 text-ink/70">
        <p>
          Users can revoke app access from their connected activity account settings at any time. After access is
          revoked, RunMarker can no longer refresh tokens or import new activity data.
        </p>

        <section>
          <h2 class="font-semibold text-ink">Self-Service Deletion</h2>
          <p class="mt-1">
            If you are signed in, you can permanently delete your stored RunMarker data from this page.
            This deletes your profile record, encrypted tokens, imported activity metadata, derived places,
            aggregate markers, and sync logs.
          </p>

          <div class="mt-4 rounded border border-red-200 bg-red-50 p-4">
            <p class="text-sm text-red-800">
              This action cannot be undone. It does not delete data from your original activity account.
              Revoke app access from that provider separately if you also want to stop future authorization.
            </p>
            <button
              type="button"
              class="mt-4 rounded bg-red-700 px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
              :disabled="!canDelete || isDeleting || authStore.isLoading"
              @click="deleteMyData"
            >
              {{ isDeleting ? 'Deleting...' : 'Delete my RunMarker data' }}
            </button>
            <p v-if="!canDelete" class="mt-2 text-xs text-red-700">
              Sign in first to use self-service deletion.
            </p>
            <p v-if="deleteMessage" class="mt-2 text-xs text-green-700">{{ deleteMessage }}</p>
            <p v-if="deleteError" class="mt-2 text-xs text-red-700">{{ deleteError }}</p>
          </div>
        </section>

        <section>
          <h2 class="font-semibold text-ink">What We Delete</h2>
          <p class="mt-1">
            We delete stored user records, encrypted tokens, imported activity metadata, derived city/country data,
            sync logs, and aggregate marker records associated with the user.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">Manual Request</h2>
          <p class="mt-1">
            If you cannot sign in, contact the project owner through
            <a class="font-medium text-trail underline" href="https://github.com/Hanbooyo/RunMarker/issues" target="_blank" rel="noreferrer">
              GitHub Issues
            </a>
            and include enough account context to identify the record.
          </p>
        </section>
      </div>
    </section>
  </main>
</template>
