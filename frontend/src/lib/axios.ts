import axios from 'axios';

const ACCESS_TOKEN_KEY = 'tokki.accessToken';
const rawApiBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/+$/, '');
const shouldUseDevProxy = import.meta.env.DEV;
const normalizedApiBaseUrl = rawApiBaseUrl
  ? rawApiBaseUrl.endsWith('/api') ? rawApiBaseUrl : `${rawApiBaseUrl}/api`
  : '/api';

export const apiBaseUrl = rawApiBaseUrl
  ? shouldUseDevProxy ? '/api' : normalizedApiBaseUrl
  : '/api';

export const backendBaseUrl = apiBaseUrl === '/api' ? '' : apiBaseUrl.replace(/\/api$/, '');
export const sessionApiBaseUrl = normalizedApiBaseUrl;
export const sessionBackendBaseUrl = sessionApiBaseUrl === '/api' ? '' : sessionApiBaseUrl.replace(/\/api$/, '');

export function getStoredAccessToken(): string | null {
  return window.localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function setStoredAccessToken(token: string | null): void {
  if (token) {
    window.localStorage.setItem(ACCESS_TOKEN_KEY, token);
  } else {
    window.localStorage.removeItem(ACCESS_TOKEN_KEY);
  }
}

const apiClient = axios.create({
  baseURL: apiBaseUrl,
  withCredentials: true,
});

apiClient.interceptors.request.use((config) => {
  const token = getStoredAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
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
