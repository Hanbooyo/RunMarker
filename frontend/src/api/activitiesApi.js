import { http } from './http'
import { demoActivities, isDemoMode } from './demoData'

export const activitiesApi = {
  async list({ page = 0, size = 20 } = {}) {
    if (isDemoMode) {
      const start = page * size
      return {
        activities: demoActivities.slice(start, start + size),
        page,
        size,
      }
    }

    const { data } = await http.get('/api/activities', {
      params: { page, size },
    })
    return data
  },

  async get(id) {
    if (isDemoMode) {
      return demoActivities.find((activity) => String(activity.id) === String(id)) || demoActivities[0]
    }

    const { data } = await http.get(`/api/activities/${id}`)
    return data
  },
}
