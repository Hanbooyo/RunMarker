<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <div class="mb-6">
    <h1 class="text-2xl font-semibold text-ink">{{ title }}</h1>
    <p v-if="description" class="mt-1 max-w-3xl text-sm leading-6 text-ink/65">
      {{ description }}
    </p>
  </div>
</template>
