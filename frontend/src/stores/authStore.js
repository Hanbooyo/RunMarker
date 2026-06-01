import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const debugUserId = ref(localStorage.getItem('stravamate.debugUserId') || '')
  const isLoading = ref(false)
  const errorMessage = ref('')
  const session = ref(null)

  const isAuthenticated = computed(() => Boolean(user.value || debugUserId.value))

  function loginWithActivityProvider() {
    window.location.href = authApi.getLoginUrl()
  }

  async function loginForLocalDevelopment() {
    isLoading.value = true
    errorMessage.value = ''

    try {
      user.value = await authApi.devLogin()
      await fetchSession()
      setDebugUserId('')
    } catch (error) {
      user.value = null
      errorMessage.value = error.response?.data?.message || 'Local login failed.'
      throw error
    } finally {
      isLoading.value = false
    }
  }

  function setDebugUserId(value) {
    debugUserId.value = value

    if (value) {
      localStorage.setItem('stravamate.debugUserId', value)
    } else {
      localStorage.removeItem('stravamate.debugUserId')
    }
  }

  async function fetchMe() {
    isLoading.value = true
    errorMessage.value = ''

    try {
      user.value = await authApi.getMe()
    } catch (error) {
      user.value = null
      errorMessage.value = error.response?.data?.message || ''
    } finally {
      isLoading.value = false
    }
  }

  async function fetchSession() {
    try {
      session.value = await authApi.getSession()
      return session.value
    } catch {
      session.value = null
      return null
    }
  }

  async function refreshSession() {
    session.value = await authApi.refreshSession()
    return session.value
  }

  async function logoutLocal() {
    try {
      await authApi.logout()
    } catch {
      // Always clear local state even if the backend logout request fails.
    }

    user.value = null
    session.value = null
    setDebugUserId('')
  }

  async function deleteAccount() {
    isLoading.value = true
    errorMessage.value = ''

    try {
      await authApi.deleteAccount()
      user.value = null
      session.value = null
      setDebugUserId('')
    } catch (error) {
      errorMessage.value = error.response?.data?.message || 'Account deletion failed.'
      throw error
    } finally {
      isLoading.value = false
    }
  }

  return {
    user,
    debugUserId,
    session,
    isLoading,
    errorMessage,
    isAuthenticated,
    loginWithActivityProvider,
    loginForLocalDevelopment,
    setDebugUserId,
    fetchMe,
    fetchSession,
    refreshSession,
    logoutLocal,
    deleteAccount,
  }
})
