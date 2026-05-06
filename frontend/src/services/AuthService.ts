import axios from 'axios';
import { onAuthStateChanged, signInWithPopup, signOut as firebaseSignOut } from 'firebase/auth';
import http, { sessionApiBaseUrl, setStoredAccessToken } from '../lib/axios';
import { auth, firebaseConfigured, googleProvider } from '../lib/firebase';
import type { AppUser, UserRole } from '../types';

interface AuthMeResponse {
  authenticated: boolean;
  provider?: string;
  providerId?: string;
  email?: string;
  role?: UserRole;
}

const sessionHttp = axios.create({
  baseURL: sessionApiBaseUrl,
  withCredentials: true,
});

sessionHttp.interceptors.response.use((response) => response.data?.data ?? response.data);

export class AuthService {
  static async startGoogleSignIn(): Promise<AppUser> {
    if (!firebaseConfigured) {
      throw new Error('FIREBASE_NOT_CONFIGURED');
    }

    const credential = await signInWithPopup(auth, googleProvider);
    const token = await credential.user.getIdToken();
    setStoredAccessToken(token);

    await http.put('/users/me', {
      nickname: credential.user.displayName ?? credential.user.email ?? credential.user.uid,
      email: credential.user.email ?? '',
    });

    const user = await this.getCurrentUser();
    if (!user) throw new Error('AUTH_SESSION_NOT_FOUND');
    return user;
  }

  static async getCurrentUser(): Promise<AppUser | null> {
    if (firebaseConfigured && auth.currentUser) {
      setStoredAccessToken(await auth.currentUser.getIdToken());
    }

    const currentUser = (await http.get('/auth/me')) as unknown as AuthMeResponse;
    if (!currentUser.authenticated || !currentUser.providerId) return null;
    return {
      uid: currentUser.providerId,
      email: currentUser.email ?? '',
      role: currentUser.role ?? 'user',
    };
  }

  static async issueToken(): Promise<AppUser> {
    if (firebaseConfigured && auth.currentUser) {
      setStoredAccessToken(await auth.currentUser.getIdToken());
    } else {
      await sessionHttp.post('/auth/token');
    }
    const user = await this.getCurrentUser();
    if (!user) throw new Error('AUTH_SESSION_NOT_FOUND');
    return user;
  }

  static async registerAdmin(adminSecretKey: string): Promise<AppUser> {
    return (await http.post('/auth/admin/register', { adminSecretKey })) as unknown as AppUser;
  }

  static async signOut(): Promise<void> {
    try {
      await http.post('/auth/logout');
      if (firebaseConfigured) await firebaseSignOut(auth);
    } finally {
      setStoredAccessToken(null);
    }
  }

  static waitForFirebaseUser(): Promise<void> {
    if (!firebaseConfigured) return Promise.resolve();
    if (auth.currentUser) return Promise.resolve();

    return new Promise((resolve) => {
      const unsubscribe = onAuthStateChanged(auth, () => {
        unsubscribe();
        resolve();
      });
    });
  }
}
