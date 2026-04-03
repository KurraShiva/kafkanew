// import { defineConfig } from 'vite'
// import react from '@vitejs/plugin-react'

// export default defineConfig({
//   plugins: [react()],
//   define: {
//     global: 'globalThis',
//   },
//   server: {
//     port: 5173,
//     proxy: {
//       '/api/bookings': {
//         target: 'http://localhost:8082',
//         changeOrigin: true,
//       },
//       '/api/dashboard': {
//         target: 'http://localhost:8082',
//         changeOrigin: true,
//       },
//       '/api/notifications': {
//         target: 'http://localhost:8082',
//         changeOrigin: true,
//       },
//       '/ws': {
//         target: 'http://localhost:8082',
//         ws: true,
//         changeOrigin: true,
//       },
//     },
//   },
// })


// import { defineConfig } from 'vite'
// import react from '@vitejs/plugin-react'

// export default defineConfig({
//   plugins: [react()],
//   server: {
//     port: 5173,
//     proxy: {
//       '/api': {
//         target: 'http://localhost:8082',
//         changeOrigin: true,
//         rewrite: (path) => path
//       },
//       '/producer-api': {
//         target: 'http://localhost:8081',
//         changeOrigin: true,
//         rewrite: (path) => path.replace(/^\/producer-api/, '/api')
//       }
//     }
//   }
// })


import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/producer-api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/producer-api/, '/api')
      },
      '/ws': {
        target: 'ws://localhost:8082',
        ws: true,
        changeOrigin: true,
      }
    }
  },
  define: {
    global: 'globalThis',
    'process.env': {}
  }
})