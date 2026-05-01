import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

export default function LoginCallbackPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState(null);

  const email = searchParams.get('email');
  const name = searchParams.get('name');
  const picture = searchParams.get('picture');

  useEffect(() => {
    if (!email) {
      navigate('/login?error=no_email', { replace: true });
      return;
    }

    setUserInfo({ email, name: name || '', picture: picture || '' });

    // TODO: Issue #39 범위 외 - JWT 토큰 발급 및 저장
    console.info('[LoginCallback] OAuth2 success', { email, name, picture });
  }, [email, name, picture, navigate]);

  if (!userInfo) {
    return (
      <main className="login-page">
        <div className="login-card">
          <div className="callback-loading">
            <span className="spinner" />
            <p>로그인 처리 중...</p>
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
          <p className="callback-subtitle">구글 계정 연동에 성공했습니다.</p>
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
              onClick={() => navigate('/login')}
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