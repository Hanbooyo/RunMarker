import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  withCredentials: true,
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const debugUserId = localStorage.getItem('stravamate.debugUserId')

  if (debugUserId) {
    config.headers['X-User-Id'] = debugUserId
  }

  return config
})
