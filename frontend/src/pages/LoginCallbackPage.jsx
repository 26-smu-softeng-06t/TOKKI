import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';

export default function LoginCallbackPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState(null);
  const [sessionStatus, setSessionStatus] = useState('checking');

  const email = searchParams.get('email');
  const name = searchParams.get('name');
  const picture = searchParams.get('picture');

  useEffect(() => {
    let ignore = false;

    async function verifySession() {
      try {
        const response = await api.get('/auth/me');
        const currentUser = response.data?.data;

        if (ignore) {
          return;
        }

        if (!currentUser?.authenticated) {
          navigate('/login?error=session_not_found', { replace: true });
          return;
        }

        setUserInfo({
          email: currentUser.email || email || '',
          name: currentUser.name || name || '',
          picture: currentUser.picture || picture || '',
        });
        setSessionStatus('verified');
        console.info('[LoginCallback] OAuth2 session verified', {
          email: currentUser.email,
          provider: currentUser.provider,
        });
      } catch (requestError) {
        if (!ignore) {
          console.error('[LoginCallback] Session verification failed', requestError);
          setSessionStatus('failed');
        }
      }
    }

    verifySession();

    return () => {
      ignore = true;
    };
  }, [email, name, picture, navigate]);

  const handleLogout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (requestError) {
      console.error('[Logout Error]', requestError);
    } finally {
      navigate('/login');
    }
  };

  if (!userInfo) {
    return (
      <main className="login-page">
        <div className="login-card">
          <div className="callback-loading">
            <span className="spinner" />
            <p>
              {sessionStatus === 'failed'
                ? '세션 확인에 실패했습니다.'
                : '로그인 세션 확인 중...'}
            </p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="login-page">
      <div className="login-card">
        <div className="callback-header">
          <h2 className="callback-title">로그인 완료</h2>
          <p className="callback-subtitle">구글 계정 연동과 세션 확인에 성공했습니다.</p>
        </div>

        <div className="callback-user-info">
          {userInfo.picture && (
            <img
              className="callback-avatar"
              src={userInfo.picture}
              alt={`${userInfo.name}의 프로필`}
            />
          )}
          {!userInfo.picture && (
            <div className="callback-avatar-skeleton" />
          )}
          <div className="callback-user-details">
            <p className="callback-user-name">
              {userInfo.name || (
                <span className="skeleton-text skeleton-text-short">이름</span>
              )}
            </p>
            <p className="callback-user-email">{userInfo.email}</p>
          </div>
        </div>

        <div className="callback-actions">
          <button
            className="callback-btn"
            onClick={() => navigate('/')}
            type="button"
          >
            시작하기
          </button>
        </div>

        <div className="callback-footer">
          <p>
            {/* TODO: Issue #39 범위 외 - JWT 기반 인증 연동 후 실제 로그아웃 구현 */}
            <button
              className="callback-logout-btn"
              onClick={handleLogout}
              type="button"
            >
              다른 계정으로 로그인
            </button>
          </p>
        </div>
      </div>
    </main>
  );
}
