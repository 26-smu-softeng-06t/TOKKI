import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck } from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../hooks/useAuth';
import { AuthService } from '../services/AuthService';
import LoadingSpinner from '../components/LoadingSpinner';
import type { AppUser } from '../types';

export default function LoginCallbackPage() {
  const navigate = useNavigate();
  const { setAuthenticatedUser } = useAuth();
  const [user, setUser] = useState<AppUser | null>(null);
  const [adminKey, setAdminKey] = useState('');
  const [registering, setRegistering] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let ignore = false;

    AuthService.issueToken()
      .then((currentUser) => {
        if (ignore) return;
        setUser(currentUser);
        setAuthenticatedUser(currentUser);
      })
      .catch(() => {
        if (!ignore) {
          toast.error('로그인 세션 확인에 실패했습니다');
          navigate('/login?error=session_not_found', { replace: true });
        }
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });

    return () => {
      ignore = true;
    };
  }, [navigate, setAuthenticatedUser]);

  const handleAdminRegister = async () => {
    if (!adminKey.trim()) {
      toast.error('관리자 코드를 입력해주세요');
      return;
    }

    setRegistering(true);
    try {
      const adminUser = await AuthService.registerAdmin(adminKey);
      setUser(adminUser);
      setAuthenticatedUser(adminUser);
      toast.success('관리자 인증이 완료되었습니다');
      navigate('/admin', { replace: true });
    } catch (err) {
      toast.error(err instanceof Error ? err.message : '관리자 코드가 올바르지 않습니다');
    } finally {
      setRegistering(false);
    }
  };

  if (loading || !user) return <LoadingSpinner />;

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-10">
        <h1 className="text-3xl font-bold text-slate-900">로그인 완료</h1>
        <p className="text-slate-500 mt-2 mb-6">{user.email}</p>

        <button
          onClick={() => navigate('/', { replace: true })}
          className="bg-indigo-600 hover:bg-indigo-700 text-white w-full rounded-lg py-3 font-medium transition-colors"
        >
          시작하기
        </button>

        {user.role !== 'admin' && (
          <div className="mt-8 border-t border-slate-100 pt-6">
            <div className="flex items-center gap-2 mb-4">
              <ShieldCheck className="text-indigo-600 w-5 h-5" />
              <h2 className="text-lg font-bold text-slate-900">관리자 코드 입력</h2>
            </div>
            <input
              type="password"
              value={adminKey}
              onChange={(event) => setAdminKey(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === 'Enter') void handleAdminRegister();
              }}
              placeholder="관리자 코드를 입력하세요"
              className="w-full border border-slate-200 rounded-lg px-4 py-2 mb-3 outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <button
              onClick={handleAdminRegister}
              disabled={registering}
              className="w-full rounded-lg bg-slate-900 px-4 py-2 text-white font-medium hover:bg-slate-800 disabled:opacity-60"
            >
              {registering ? '확인 중...' : '관리자 인증'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
