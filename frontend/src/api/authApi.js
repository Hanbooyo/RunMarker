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

  async getSession() {
    if (isDemoMode) {
      return {
        authenticated: true,
        maxInactiveIntervalSeconds: 3600,
        remainingSeconds: 3600,
        expiresAtEpochMillis: Date.now() + 3600 * 1000,
      }
    }

    const { data } = await http.get('/api/auth/session')
    return data
  },

  async refreshSession() {
    if (isDemoMode) {
      return {
        authenticated: true,
        maxInactiveIntervalSeconds: 3600,
        remainingSeconds: 3600,
        expiresAtEpochMillis: Date.now() + 3600 * 1000,
      }
    }

    const { data } = await http.post('/api/auth/session/refresh')
    return data
  },

  async devLogin() {
    const { data } = await http.post('/api/auth/dev/login')
    return data
  },
}
