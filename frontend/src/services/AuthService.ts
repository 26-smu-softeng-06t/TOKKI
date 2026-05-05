import { auth, googleProvider } from '../lib/firebase';
import { signInWithPopup, signOut, onAuthStateChanged } from 'firebase/auth';
import type { NextOrObserver, User as FirebaseUser } from 'firebase/auth';
import { UserService } from './UserService';
import type { AppUser } from '../types';

const DEV = !import.meta.env.VITE_FIREBASE_API_KEY;

export class AuthService {
  static async signInWithGoogle(): Promise<AppUser> {
    if (DEV) return { uid: 'dev-uid', email: 'dev@localhost', role: 'user' };
    const result = await signInWithPopup(auth, googleProvider);
    const { uid, email } = result.user;
    await UserService.upsertUser(uid, email ?? '');
    const userData = await UserService.getUser(uid);
    return { uid, email: email ?? '', role: userData.role };
  }

  static verifyAdminKey(inputKey: string): boolean {
    return inputKey === import.meta.env.VITE_ADMIN_SECRET_KEY;
  }

  static async signOut(): Promise<void> {
    if (DEV) return;
    await signOut(auth);
  }

  static onAuthStateChanged(callback: NextOrObserver<FirebaseUser>): () => void {
    if (DEV) {
      if (typeof callback === 'function') setTimeout(() => callback(null), 0);
      return () => {};
    }
    return onAuthStateChanged(auth, callback);
  }
}
