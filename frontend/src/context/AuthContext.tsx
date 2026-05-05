import { createContext, useEffect, useState, type ReactNode } from 'react';
import { AuthService } from '../services/AuthService';
import { UserService } from '../services/UserService';
import type { AppUser } from '../types';

interface AuthContextType {
  user: AppUser | null;
  loading: boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

const DEV = !import.meta.env.VITE_FIREBASE_API_KEY;
const DEV_USER: AppUser = { uid: 'dev-uid', email: 'dev@localhost', role: 'admin' };

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AppUser | null>(DEV ? DEV_USER : null);
  const [loading, setLoading] = useState(!DEV);

  useEffect(() => {
    if (DEV) return;

    const unsubscribe = AuthService.onAuthStateChanged(async (firebaseUser) => {
      if (firebaseUser) {
        try {
          const userData = await UserService.getUser(firebaseUser.uid);
          setUser({ uid: firebaseUser.uid, email: firebaseUser.email ?? '', role: userData.role });
        } catch {
          setUser({ uid: firebaseUser.uid, email: firebaseUser.email ?? '', role: 'user' });
        }
      } else {
        setUser(null);
      }
      setLoading(false);
    });
    return unsubscribe;
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading }}>
      {children}
    </AuthContext.Provider>
  );
}
