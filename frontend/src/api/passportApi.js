import { http } from './http'

export const passportApi = {
  async getSummary() {
    const { data } = await http.get('/api/passport/summary')
    return data
  },

  async getCountries() {
    const { data } = await http.get('/api/passport/countries')
    return data
  },

  async getCities() {
    const { data } = await http.get('/api/passport/cities')
    return data
  },

  async getMapMarkers() {
    const { data } = await http.get('/api/passport/map-markers')
    return data
  },

  async getRecentPlaces(limit = 10) {
    const { data } = await http.get('/api/passport/recent-places', {
      params: { limit },
    })
    return data
  },
}
