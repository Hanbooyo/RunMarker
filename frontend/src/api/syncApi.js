import { http } from './http'

export const syncApi = {
  async syncActivities() {
    const { data } = await http.post('/api/sync/activities')
    return data
  },
}
