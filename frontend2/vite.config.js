import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 3000,
    // You can use a wildcard pattern for greater flexibility
    allowedHosts: [
      '*.southeastasia.azurecontainerapps.io'
    ]
  }
})
