import axios from 'axios';
import { auth, firebaseConfigured } from './firebase';

const ACCESS_TOKEN_KEY = 'tokki.accessToken';
const rawApiBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/+$/, '');
const shouldUseDevProxy = import.meta.env.DEV;
const shouldUseDevAuth = import.meta.env.DEV && import.meta.env.VITE_AUTH_DEV_USER === 'true';
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

const AUTH_EXPIRED_EVENT = 'tokki:auth-expired';

export function emitAuthExpired(): void {
  window.dispatchEvent(new CustomEvent(AUTH_EXPIRED_EVENT));
}

export function onAuthExpired(callback: () => void): () => void {
  const handler = () => callback();
  window.addEventListener(AUTH_EXPIRED_EVENT, handler);
  return () => window.removeEventListener(AUTH_EXPIRED_EVENT, handler);
}

const apiClient = axios.create({
  baseURL: apiBaseUrl,
  withCredentials: true,
});

apiClient.interceptors.request.use(async (config) => {
  if (shouldUseDevAuth) {
    config.headers['X-TOKKI-DEV-USER'] = 'true';
    config.headers['X-TOKKI-DEV-ROLE'] = 'admin';
    return config;
  }

  const token = firebaseConfigured && auth.currentUser
    ? await auth.currentUser.getIdToken()
    : getStoredAccessToken();
  if (token) {
    setStoredAccessToken(token);
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response.data?.data ?? response.data,
  (error) => {
    const status = error.response?.status;
    const errorCode = error.response?.data?.error?.code;

    const isAuthError =
      status === 401 ||
      errorCode === 'AUTH_REQUIRED' ||
      errorCode === 'TOKEN_EXPIRED' ||
      errorCode === 'TOKEN_INVALID' ||
      errorCode === 'UNAUTHORIZED';

    if (isAuthError && !shouldUseDevAuth) {
      setStoredAccessToken(null);
      emitAuthExpired();
    }

    const code = errorCode ?? error.message;
    return Promise.reject(new Error(code));
  },
);

export default apiClient;
