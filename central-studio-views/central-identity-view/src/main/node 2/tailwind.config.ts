import type { Config } from 'tailwindcss'

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  theme: {
    colors: {
      'primary': '#0ea5e9',
      'secondary': '#64748b',
      'danger': '#ef4444',
      'warning': '#f59e0b',
      'success': '#22c55e',
      'info': '#3b82f6',
      'light': '#f9fafb',
      'dark': '#1f2937',
      'white': '#ffffff',
      'black': '#000000',
      'transparent': 'transparent'
    },
    fontFamily: {
      'sans': ['ui-sans-serif', 'system-ui', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'Roboto', '"Helvetica Neue"', 'Arial', '"Noto Sans"', 'sans-serif', '"Apple Color Emoji"', '"Segoe UI Emoji"', '"Segoe UI Symbol"', '"Noto Color Emoji"'],
      'serif': ['ui-serif', 'Georgia', 'Cambria', '"Times New Roman"', 'Times', 'serif'],
    },
    extend: {}
  },
  plugins: []
} satisfies Config
