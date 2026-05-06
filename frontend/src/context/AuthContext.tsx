import { createContext, useEffect, useState, type ReactNode } from 'react';
import { AuthService } from '../services/AuthService';
import type { AppUser } from '../types';

interface AuthContextType {
  user: AppUser | null;
  loading: boolean;
  setAuthenticatedUser: (user: AppUser | null) => void;
  refreshUser: () => Promise<AppUser | null>;
  logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

const DEV = import.meta.env.DEV && import.meta.env.VITE_AUTH_DEV_USER === 'true';
const DEV_USER: AppUser = { uid: 'dev-uid', email: 'dev@localhost', role: 'admin' };

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AppUser | null>(DEV ? DEV_USER : null);
  const [loading, setLoading] = useState(!DEV);

  const refreshUser = async () => {
    if (DEV) return DEV_USER;
    await AuthService.waitForFirebaseUser();
    const currentUser = await AuthService.getCurrentUser();
    setUser(currentUser);
    return currentUser;
  };

  const logout = async () => {
    await AuthService.signOut();
    setUser(null);
  };

  useEffect(() => {
    if (DEV) return;

    refreshUser()
      .catch(() => {
        setUser(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, setAuthenticatedUser: setUser, refreshUser, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
