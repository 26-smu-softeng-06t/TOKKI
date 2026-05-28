import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider } from 'firebase/auth';
import type { Auth } from 'firebase/auth';

const configured = !!import.meta.env.VITE_FIREBASE_API_KEY;

export const firebaseConfigured = configured;

export const auth: Auth = configured
  ? getAuth(initializeApp({
      apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
      authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
      projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
    }))
  : {} as Auth;

export const googleProvider: GoogleAuthProvider = configured
  ? new GoogleAuthProvider()
  : {} as GoogleAuthProvider;
