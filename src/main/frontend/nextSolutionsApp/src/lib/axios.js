import axios from 'axios';

const instance = axios.create({
  baseURL: '/api', // Vite 프록시 or 스프링 같은 도메인일 때 공통 prefix
  withCredentials: true, // JSESSIONID 전달
  headers: {
    'Content-Type': 'application/json',
  },
});

export default instance;
