export function formatKm(value) {
  const numberValue = Number(value || 0)
  return `${numberValue.toLocaleString('ko-KR', {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  })} km`
}

export function formatDate(value) {
  if (!value) {
    return '-'
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))
}

export function formatInteger(value) {
  return Number(value || 0).toLocaleString('ko-KR')
}
