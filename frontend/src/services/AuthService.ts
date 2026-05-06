import axios from 'axios';
import http, { sessionApiBaseUrl, sessionBackendBaseUrl, setStoredAccessToken } from '../lib/axios';
import type { AppUser, UserRole } from '../types';

interface AuthMeResponse {
  authenticated: boolean;
  provider?: string;
  providerId?: string;
  email?: string;
  role?: UserRole;
}

interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

const sessionHttp = axios.create({
  baseURL: sessionApiBaseUrl,
  withCredentials: true,
});

sessionHttp.interceptors.response.use((response) => response.data?.data ?? response.data);

export class AuthService {
  static async startGoogleSignIn(): Promise<void> {
    const response = (await http.get('/auth/google-url')) as unknown as { authorizationUrl: string };
    const authorizationUrl = response.authorizationUrl || '/oauth2/authorization/google';
    window.location.href = authorizationUrl.startsWith('http')
      ? authorizationUrl
      : `${sessionBackendBaseUrl}${authorizationUrl}`;
  }

  static async getCurrentUser(): Promise<AppUser | null> {
    const currentUser = (await http.get('/auth/me')) as unknown as AuthMeResponse;
    if (!currentUser.authenticated || !currentUser.providerId) return null;
    return {
      uid: currentUser.providerId,
      email: currentUser.email ?? '',
      role: currentUser.role ?? 'user',
    };
  }

  static async issueToken(): Promise<AppUser> {
    const token = (await sessionHttp.post('/auth/token')) as unknown as TokenResponse;
    setStoredAccessToken(token.accessToken);
    const user = await this.getCurrentUser();
    if (!user) throw new Error('AUTH_SESSION_NOT_FOUND');
    return user;
  }

  static async registerAdmin(adminSecretKey: string): Promise<AppUser> {
    const token = (await http.post('/auth/admin/register', { adminSecretKey })) as unknown as TokenResponse;
    setStoredAccessToken(token.accessToken);
    const user = await this.getCurrentUser();
    if (!user) throw new Error('AUTH_SESSION_NOT_FOUND');
    return user;
  }

  static async signOut(): Promise<void> {
    try {
      await http.post('/auth/logout');
    } finally {
      setStoredAccessToken(null);
    }
  }
}
