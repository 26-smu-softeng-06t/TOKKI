import axios from 'axios';
import { auth } from './firebase';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

apiClient.interceptors.request.use(async (config) => {
  const user = auth.currentUser;
  if (user) {
    const idToken = await user.getIdToken();
    config.headers.Authorization = `Bearer ${idToken}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response.data?.data ?? response.data,
  (error) => {
    const code = error.response?.data?.error?.code ?? error.message;
    return Promise.reject(new Error(code));
  },
);

export default apiClient;