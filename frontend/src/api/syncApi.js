import { http } from './http'
import { isDemoMode } from './demoData'

export const syncApi = {
  async syncActivities() {
    if (isDemoMode) {
      return {
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

    const { data } = await http.post('/api/sync/activities')
    return data
  },
}
