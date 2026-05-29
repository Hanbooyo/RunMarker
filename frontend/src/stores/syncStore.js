import { ref } from 'vue'
import { defineStore } from 'pinia'
import { syncApi } from '@/api/syncApi'

export const useSyncStore = defineStore('sync', () => {
  const isSyncing = ref(false)
  const lastResult = ref(null)
  const errorMessage = ref('')
  const currentJobId = ref(null)
  let pollingTimer = null

  async function syncActivities(mode = 'recent') {
    isSyncing.value = true
    errorMessage.value = ''

    try {
      if (mode === 'full') {
        return await startFullSyncJob()
      }

      lastResult.value = await syncApi.syncActivities(mode)
      return lastResult.value
    } catch (error) {
      errorMessage.value = error.response?.data?.message || '동기화에 실패했습니다.'
      throw error
    } finally {
      if (mode !== 'full') {
        isSyncing.value = false
      }
    }
  }

  async function startFullSyncJob() {
    const job = await syncApi.startActivitiesSyncJob('full')
    currentJobId.value = job.syncLogId
    lastResult.value = {
      ...job,
      requestedCount: 0,
      syncedCount: 0,
      geocodedCount: 0,
      geocodingFailedCount: 0,
      skippedCount: 0,
    }
    startPolling(job.syncLogId)
    return lastResult.value
  }

  function startPolling(syncLogId) {
    stopPolling()
    pollingTimer = window.setInterval(() => pollJob(syncLogId), 3000)
    pollJob(syncLogId)
  }

  async function pollJob(syncLogId) {
    try {
      const status = await syncApi.getActivitiesSyncJob(syncLogId)
      lastResult.value = status

      if (status.status !== 'STARTED') {
        stopPolling()
        isSyncing.value = false
        currentJobId.value = null

        if (status.status === 'FAILED') {
          errorMessage.value = status.errorMessage || '전체 동기화에 실패했습니다.'
        }
      }
    } catch (error) {
      stopPolling()
      isSyncing.value = false
      errorMessage.value = error.response?.data?.message || '동기화 상태 조회에 실패했습니다.'
    }
  }

  function stopPolling() {
    if (pollingTimer) {
      window.clearInterval(pollingTimer)
      pollingTimer = null
    }
  }

  return {
    isSyncing,
    lastResult,
    errorMessage,
    currentJobId,
    syncActivities,
  }
})
