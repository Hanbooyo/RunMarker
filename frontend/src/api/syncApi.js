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
}
