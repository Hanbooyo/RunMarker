import { http } from './http'
import { demoUser, isDemoMode } from './demoData'

export const authApi = {
  getLoginUrl() {
    return `${http.defaults.baseURL}/api/auth/strava/login`
  },

  async getMe() {
    if (isDemoMode) {
      return demoUser
    }

    const { data } = await http.get('/api/auth/me')
    return data
  },

  async logout() {
    if (isDemoMode) {
      return
    }

    await http.post('/api/auth/logout')
  },

  async devLogin() {
    const { data } = await http.post('/api/auth/dev/login')
    return data
  },
}
