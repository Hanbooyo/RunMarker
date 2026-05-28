import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const debugUserId = ref(localStorage.getItem('stravamate.debugUserId') || '')
  const isLoading = ref(false)
  const errorMessage = ref('')

  const isAuthenticated = computed(() => Boolean(user.value || debugUserId.value))

  function loginWithStrava() {
    window.location.href = authApi.getLoginUrl()
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

  async function logoutLocal() {
    try {
      await authApi.logout()
    } catch {
      // 로컬 상태 정리는 백엔드 로그아웃 실패와 무관하게 수행합니다.
    }

    user.value = null
    setDebugUserId('')
  }

  return {
    user,
    debugUserId,
    isLoading,
    errorMessage,
    isAuthenticated,
    loginWithStrava,
    setDebugUserId,
    fetchMe,
    logoutLocal,
  }
})
