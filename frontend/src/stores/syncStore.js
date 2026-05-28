import { ref } from 'vue'
import { defineStore } from 'pinia'
import { syncApi } from '@/api/syncApi'

export const useSyncStore = defineStore('sync', () => {
  const isSyncing = ref(false)
  const lastResult = ref(null)
  const errorMessage = ref('')

  async function syncActivities() {
    isSyncing.value = true
    errorMessage.value = ''

    try {
      lastResult.value = await syncApi.syncActivities()
      return lastResult.value
    } catch (error) {
      errorMessage.value = error.response?.data?.message || '동기화에 실패했습니다.'
      throw error
    } finally {
      isSyncing.value = false
    }
  }

  return {
    isSyncing,
    lastResult,
    errorMessage,
    syncActivities,
  }
})
