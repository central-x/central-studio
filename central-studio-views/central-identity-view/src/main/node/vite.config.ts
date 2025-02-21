import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  base: "./",
  server: {
    proxy: {
      "/identity/api": {
        target: "http://localhost:8080",
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        },
      }
    },
  },
  plugins: [
    vue(),
    vueDevTools(),
    tailwindcss()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  build: {
    outDir: '../resources/identity',
    emptyOutDir: true
  }
})
