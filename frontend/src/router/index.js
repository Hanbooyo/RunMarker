import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import PassportView from '@/views/PassportView.vue'
import CountriesView from '@/views/CountriesView.vue'
import CitiesView from '@/views/CitiesView.vue'
import ActivitiesView from '@/views/ActivitiesView.vue'
import ActivityDetailView from '@/views/ActivityDetailView.vue'
import SyncView from '@/views/SyncView.vue'

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
    name: 'passport',
    component: PassportView,
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
