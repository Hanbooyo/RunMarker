import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import SummaryCards from './SummaryCards.vue'

describe('SummaryCards', () => {
  it('renders marker summary values', () => {
    const wrapper = mount(SummaryCards, {
      props: {
        summary: {
          totalCountries: 3,
          totalCities: 12,
          totalDistanceKm: 123.45,
          totalActivities: 30,
        },
      },
    })

    expect(wrapper.text()).toContain('Countries run')
    expect(wrapper.text()).toContain('3')
    expect(wrapper.text()).toContain('12')
    expect(wrapper.text()).toContain('123.5 km')
    expect(wrapper.text()).toContain('30')
  })
})
