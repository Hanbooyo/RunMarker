import { http } from './http'

export const activitiesApi = {
  async list({ page = 0, size = 20 } = {}) {
    const { data } = await http.get('/api/activities', {
      params: { page, size },
    })
    return data
  },

  async get(id) {
    const { data } = await http.get(`/api/activities/${id}`)
    return data
  },
}
