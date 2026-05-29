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

  function loginWithStrava() {
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
      errorMessage.value = error.response?.data?.message || '로컬 로그인에 실패했습니다.'
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
      // 로컬 상태 정리는 백엔드 로그아웃 실패와 무관하게 수행합니다.
    }

    user.value = null
    session.value = null
    setDebugUserId('')
  }

  return {
    user,
    debugUserId,
    session,
    isLoading,
    errorMessage,
    isAuthenticated,
    loginWithStrava,
    loginForLocalDevelopment,
    setDebugUserId,
    fetchMe,
    fetchSession,
    refreshSession,
    logoutLocal,
  }
})
