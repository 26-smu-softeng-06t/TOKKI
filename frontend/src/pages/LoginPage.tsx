import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { useAuth } from '../hooks/useAuth';
import { AuthService } from '../services/AuthService';
import LoadingSpinner from '../components/LoadingSpinner';

export default function LoginPage() {
  const { user, loading } = useAuth();
  const navigate = useNavigate();
  const [signingIn, setSigningIn] = useState(false);

  useEffect(() => {
    if (!loading && user && !signingIn) {
      navigate(user.role === 'admin' ? '/admin' : '/', { replace: true });
    }
  }, [user, loading, navigate, signingIn]);

  if (loading) return <LoadingSpinner />;

  const handleGoogleSignIn = async () => {
    setSigningIn(true);
    try {
      await AuthService.startGoogleSignIn();
      navigate('/login/callback', { replace: true });
    } catch (err) {
      toast.error(err instanceof Error ? err.message : '로그인에 실패했습니다');
      setSigningIn(false);
    } finally {
      if (document.visibilityState === 'visible') setSigningIn(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-10">
        <h1 className="text-3xl font-bold text-slate-900">Welcome Back</h1>
        <p className="text-slate-500 mt-2 mb-8">Sign in to continue</p>
        <button
          onClick={handleGoogleSignIn}
          disabled={signingIn}
          className="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white w-full rounded-lg py-3 flex items-center justify-center gap-3 font-medium transition-colors"
        >
          {!signingIn && (
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="w-5 h-5 shrink-0">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
            </svg>
          )}
          {signingIn ? 'Signing in...' : 'Sign in with Google'}
        </button>
      </div>
    </div>
  );
}
