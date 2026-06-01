export function formatKm(value) {
  const numberValue = Number(value || 0)
  return `${numberValue.toLocaleString('en-US', {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  })} km`
}

export function formatDate(value) {
  if (!value) {
    return '-'
  }

  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))
}

export function formatInteger(value) {
  return Number(value || 0).toLocaleString('en-US')
}
