import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const STORAGE_KEY = 'runmarker.locale'

const englishMessages = {
  nav: {
    dashboard: 'Dashboard',
    passport: 'Markers',
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
    korean: 'Korean',
    english: 'English',
  },
  login: {
    title: 'RunMarker',
    description: 'Organize your runs into city and country markers.',
    strava: 'Connect with Strava',
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
    description: 'Import runs and update city/country information.',
    recentTitle: 'Recent Sync',
    recentDescription: 'Checks the latest 100 activities. Use this for normal updates.',
    recentButton: 'Sync latest 100',
    fullTitle: 'Full History Sync',
    fullDescription:
      'Reads activity pages until the end. This can take several minutes depending on activity and geocoding volume.',
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
}

const messages = {
  en: englishMessages,
  ko: englishMessages,
}

function resolvePath(source, path) {
  return path.split('.').reduce((value, key) => value?.[key], source)
}

export const useI18nStore = defineStore('i18n', () => {
  const locale = ref(localStorage.getItem(STORAGE_KEY) || localStorage.getItem('stravamate.locale') || 'en')

  const currentLocaleLabel = computed(() => messages[locale.value]?.common?.[locale.value === 'ko' ? 'korean' : 'english'])

  function setLocale(value) {
    locale.value = value === 'ko' ? 'ko' : 'en'
    localStorage.setItem(STORAGE_KEY, locale.value)
    localStorage.removeItem('stravamate.locale')
  }

  function toggleLocale() {
    setLocale(locale.value === 'ko' ? 'en' : 'ko')
  }

  function t(path) {
    return resolvePath(messages[locale.value], path) || resolvePath(messages.en, path) || path
  }

  return {
    locale,
    currentLocaleLabel,
    setLocale,
    toggleLocale,
    t,
  }
})
