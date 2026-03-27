import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 스프링 서버 주소
        changeOrigin: true,
        secure: false,
        // rewrite: (path) => path.replace(/^\/api/, ''), // '/api' 제거
      },
      '/schedule': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/pjtMng': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/board': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/approval': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/employee': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/login': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/logout': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/notification': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/mypage': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
