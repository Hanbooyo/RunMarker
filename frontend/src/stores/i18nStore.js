import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const STORAGE_KEY = 'stravamate.locale'

const messages = {
  ko: {
    nav: {
      dashboard: '대시보드',
      passport: '여권',
      countries: '국가',
      cities: '도시',
      activities: '활동',
      sync: '동기화',
    },
    common: {
      loading: '불러오는 중입니다.',
      logout: '로그아웃',
      session: '세션',
      refresh: '갱신',
      language: '언어',
      korean: '한국어',
      english: 'English',
    },
    login: {
      title: 'Running Passport',
      description: 'Strava 러닝 활동을 도시와 국가 단위의 러닝 여권으로 정리합니다.',
      strava: 'Strava로 로그인',
      local: '로컬 개발용 로그인',
      debug: '기존 Local User ID로 접속',
      debugPlaceholder: '예: 1',
      go: '이동',
    },
    countries: {
      title: '국가',
      description: '국가별 누적 거리와 활동 수를 비교합니다.',
      loadError: '국가 목록을 불러오지 못했습니다.',
      country: '국가',
      cities: '도시',
      activities: '활동',
      distance: '거리',
      firstRun: '첫 러닝',
      latestRun: '최근 러닝',
    },
    cities: {
      title: '도시',
      description: '도시별 러닝 기록을 정렬해서 확인합니다.',
      loadError: '도시 목록을 불러오지 못했습니다.',
      city: '도시',
      region: '지역',
      country: '국가',
      activities: '활동',
      distance: '거리',
      firstRun: '첫 러닝',
      latestRun: '최근 러닝',
    },
    sync: {
      title: '동기화',
      description: 'Strava에서 러닝 활동을 가져오고 도시/국가 정보를 갱신합니다.',
      recentTitle: '최근 활동 동기화',
      recentDescription: '최신 활동 100개만 확인합니다. 평소에는 이 옵션을 사용하세요.',
      recentButton: '최근 100개 동기화',
      fullTitle: '전체 기간 동기화',
      fullDescription: 'Strava 활동 페이지를 끝까지 조회합니다. 활동 수와 geocoding 호출 수에 따라 몇 분 이상 걸릴 수 있습니다.',
      fullButton: '전체 기간 동기화',
      syncing: '동기화 중',
      requested: '요청',
      synced: '저장',
      geocoded: '위치 변환',
      skipped: '스킵',
      mode: '모드',
      status: '상태',
      rateLimit: 'Rate limit',
    },
  },
  en: {
    nav: {
      dashboard: 'Dashboard',
      passport: 'Passport',
      countries: 'Countries',
      cities: 'Cities',
      activities: 'Activities',
      sync: 'Sync',
    },
    common: {
      loading: 'Loading.',
      logout: 'Logout',
      session: 'Session',
      refresh: 'Refresh',
      language: 'Language',
      korean: '한국어',
      english: 'English',
    },
    login: {
      title: 'Running Passport',
      description: 'Organize your Strava runs into a running passport by city and country.',
      strava: 'Log in with Strava',
      local: 'Local development login',
      debug: 'Use existing Local User ID',
      debugPlaceholder: 'e.g. 1',
      go: 'Go',
    },
    countries: {
      title: 'Countries',
      description: 'Compare total distance and activity counts by country.',
      loadError: 'Failed to load countries.',
      country: 'Country',
      cities: 'Cities',
      activities: 'Activities',
      distance: 'Distance',
      firstRun: 'First run',
      latestRun: 'Latest run',
    },
    cities: {
      title: 'Cities',
      description: 'Sort and review your running records by city.',
      loadError: 'Failed to load cities.',
      city: 'City',
      region: 'Region',
      country: 'Country',
      activities: 'Activities',
      distance: 'Distance',
      firstRun: 'First run',
      latestRun: 'Latest run',
    },
    sync: {
      title: 'Sync',
      description: 'Import Strava runs and update city/country information.',
      recentTitle: 'Recent Sync',
      recentDescription: 'Checks the latest 100 activities. Use this for normal updates.',
      recentButton: 'Sync latest 100',
      fullTitle: 'Full History Sync',
      fullDescription: 'Reads Strava activity pages until the end. This can take several minutes depending on activity and geocoding volume.',
      fullButton: 'Sync full history',
      syncing: 'Syncing',
      requested: 'Requested',
      synced: 'Synced',
      geocoded: 'Geocoded',
      skipped: 'Skipped',
      mode: 'Mode',
      status: 'Status',
      rateLimit: 'Rate limit',
    },
  },
}

function resolvePath(source, path) {
  return path.split('.').reduce((value, key) => value?.[key], source)
}

export const useI18nStore = defineStore('i18n', () => {
  const locale = ref(localStorage.getItem(STORAGE_KEY) || 'ko')

  const currentLocaleLabel = computed(() => messages[locale.value]?.common?.[locale.value === 'ko' ? 'korean' : 'english'])

  function setLocale(value) {
    locale.value = value === 'en' ? 'en' : 'ko'
    localStorage.setItem(STORAGE_KEY, locale.value)
  }

  function toggleLocale() {
    setLocale(locale.value === 'ko' ? 'en' : 'ko')
  }

  function t(path) {
    return resolvePath(messages[locale.value], path) || resolvePath(messages.ko, path) || path
  }

  return {
    locale,
    currentLocaleLabel,
    setLocale,
    toggleLocale,
    t,
  }
})
