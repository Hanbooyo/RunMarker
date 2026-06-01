import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import PassportView from '@/views/PassportView.vue'
import PrivacyPolicyView from '@/views/PrivacyPolicyView.vue'
import TermsView from '@/views/TermsView.vue'
import DataDeletionView from '@/views/DataDeletionView.vue'
import CountriesView from '@/views/CountriesView.vue'
import CitiesView from '@/views/CitiesView.vue'
import ActivitiesView from '@/views/ActivitiesView.vue'
import ActivityDetailView from '@/views/ActivityDetailView.vue'
import SyncView from '@/views/SyncView.vue'
import LoginErrorView from '@/views/LoginErrorView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { public: true },
  },
  {
    path: '/login/success',
    redirect: '/dashboard',
    meta: { public: true },
  },
  {
    path: '/login/error',
    name: 'login-error',
    component: LoginErrorView,
    meta: { public: true },
  },
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
  },
  {
    path: '/passport',
    redirect: '/markers',
  },
  {
    path: '/markers',
    name: 'markers',
    component: PassportView,
  },
  {
    path: '/privacy',
    name: 'privacy',
    component: PrivacyPolicyView,
    meta: { public: true },
  },
  {
    path: '/terms',
    name: 'terms',
    component: TermsView,
    meta: { public: true },
  },
  {
    path: '/data-deletion',
    name: 'data-deletion',
    component: DataDeletionView,
    meta: { public: true },
  },
  {
    path: '/countries',
    name: 'countries',
    component: CountriesView,
  },
  {
    path: '/cities',
    name: 'cities',
    component: CitiesView,
  },
  {
    path: '/activities',
    name: 'activities',
    component: ActivitiesView,
  },
  {
    path: '/activities/:id',
    name: 'activity-detail',
    component: ActivityDetailView,
    props: true,
  },
  {
    path: '/sync',
    name: 'sync',
    component: SyncView,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (!to.meta.public && !authStore.isAuthenticated) {
    await authStore.fetchMe()
  }

  if (!to.meta.public && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.name === 'login' && authStore.isAuthenticated) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
