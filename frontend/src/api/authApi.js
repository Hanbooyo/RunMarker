import { http } from './http'

export const authApi = {
  getLoginUrl() {
    return `${http.defaults.baseURL}/api/auth/strava/login`
  },

  async getMe() {
    const { data } = await http.get('/api/auth/me')
    return data
  },

  async logout() {
    await http.post('/api/auth/logout')
  },
}
