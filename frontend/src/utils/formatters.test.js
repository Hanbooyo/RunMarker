import { describe, expect, it } from 'vitest'
import { formatInteger, formatKm } from './formatters'

describe('formatters', () => {
  it('formats kilometers with one decimal place', () => {
    expect(formatKm(12.345)).toBe('12.3 km')
    expect(formatKm(null)).toBe('0.0 km')
  })

  it('formats integers for Korean locale', () => {
    expect(formatInteger(12345)).toBe('12,345')
    expect(formatInteger(undefined)).toBe('0')
  })
})
