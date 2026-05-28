<script setup>
defineProps({
  label: {
    type: String,
    required: true,
  },
  value: {
    type: [String, Number],
    required: true,
  },
  hint: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <div class="rounded border border-black/10 bg-white p-4">
    <p class="text-xs font-medium uppercase tracking-wide text-ink/50">{{ label }}</p>
    <p class="mt-2 text-2xl font-semibold text-ink">{{ value }}</p>
    <p v-if="hint" class="mt-1 text-xs text-ink/55">{{ hint }}</p>
  </div>
</template>
