<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const message = computed(() => {
  switch (route.query.error) {
    case 'missing_strava_config':
      return '활동 계정 연동 설정이 완료되지 않았습니다.'
    case 'strava_denied':
      return '활동 계정 권한 승인이 취소되었습니다.'
    case 'auth_failed':
      return '활동 계정 로그인 처리 중 오류가 발생했습니다.'
    default:
      return '로그인에 실패했습니다.'
  }
})
</script>

<template>
  <main class="grid min-h-screen bg-mist px-5 py-10">
    <section class="mx-auto grid w-full max-w-md content-center">
      <div class="rounded border border-black/10 bg-white p-6 shadow-sm">
        <p class="text-sm font-semibold text-red-600">Login Error</p>
        <h1 class="mt-2 text-2xl font-semibold text-ink">로그인을 완료하지 못했습니다</h1>
        <p class="mt-3 text-sm leading-6 text-ink/65">{{ message }}</p>

        <RouterLink
          to="/login"
          class="mt-6 inline-flex w-full justify-center rounded bg-trail px-4 py-3 text-sm font-semibold text-white"
        >
          로그인 화면으로 돌아가기
        </RouterLink>
      </div>
    </section>
  </main>
</template>
