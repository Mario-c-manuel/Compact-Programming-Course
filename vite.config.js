import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'

export default defineConfig({
  plugins: [react()],
  root: resolve(__dirname, 'src'),
  build: {
    outDir: resolve(__dirname, 'src/main/resources/static'),
    emptyOutDir: true,
    rollupOptions: {
      input: resolve(__dirname, 'src/index.tsx')
    }
  },
  server: {
    port: 5173
  }
})