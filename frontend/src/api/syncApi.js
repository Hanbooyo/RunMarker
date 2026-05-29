import { http } from './http'
import { isDemoMode } from './demoData'

export const syncApi = {
  async syncActivities(mode = 'recent') {
    if (isDemoMode) {
      return {
        mode,
        requestedCount: 100,
        syncedCount: 2,
        geocodedCount: 2,
        geocodingFailedCount: 0,
        skippedCount: 98,
        status: 'SUCCESS',
        rateLimitLimit: '200,2000',
        rateLimitUsage: '1,1',
      }
    }

    const { data } = await http.post('/api/sync/activities', null, {
      params: { mode },
      timeout: 10 * 60 * 1000,
    })
    return data
  },

  async startActivitiesSyncJob(mode = 'full') {
    if (isDemoMode) {
      return {
        syncLogId: 1,
        mode,
        status: 'STARTED',
      }
    }

    const { data } = await http.post('/api/sync/activities/jobs', null, {
      params: { mode },
      timeout: 15000,
    })
    return data
  },

  async getActivitiesSyncJob(syncLogId) {
    if (isDemoMode) {
      return {
        syncLogId,
        mode: 'full',
        status: 'SUCCESS',
        requestedCount: 100,
        syncedCount: 2,
        geocodedCount: 2,
        geocodingFailedCount: 0,
        skippedCount: 98,
        rateLimitLimit: '200,2000',
        rateLimitUsage: '1,1',
        errorMessage: null,
      }
    }

    const { data } = await http.get(`/api/sync/activities/jobs/${syncLogId}`)
    return data
  },
}
