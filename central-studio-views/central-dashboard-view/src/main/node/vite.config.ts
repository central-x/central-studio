import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  base: './',
  server: {
    proxy: {
      '/identity': {
        target: 'http://localhost:8080',
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        }
      },
      '/dashboard/api': {
        target: 'http://localhost:8080',
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        }
      },
      '/dashboard/__logout': {
        target: 'http://localhost:8080',
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        }
      },
      '/storage/api': {
        target: 'http://localhost:8080',
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        }
      },
      '/multicast/api': {
        target: 'http://localhost:8080',
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        }
      },
      '/logging/api': {
        target: 'http://localhost:8080',
        configure: (proxy, options) => {
          options.headers = { 'X-Forwarded-Tenant': 'master' }
        }
      }
    }
  },
  plugins: [
    vue(),
    vueDevTools()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '@centralx': fileURLToPath(new URL('./library', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/styles/global.scss" as *;`
      }
    }
  },
  build: {
    outDir: '../resources/dashboard',
    emptyOutDir: true
  }
})
