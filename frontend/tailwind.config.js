/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js}'],
  theme: {
    extend: {
      colors: {
        ink: '#17201b',
        moss: '#4f6f52',
        trail: '#d86635',
        mist: '#f4f7f2',
      },
    },
  },
  plugins: [],
}
