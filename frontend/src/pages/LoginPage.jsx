import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

export default function LoginPage() {
  const [searchParams] = useSearchParams();
  const error = searchParams.get('error');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (error) {
      console.error('[Login Error]', error);
    }
  }, [error]);

  const handleGoogleLogin = () => {
    setLoading(true);
    window.location.href = '/oauth2/authorization/google';
  };

  return (
    <main className="login-page">
      <div className="login-card">
        <div className="login-header">
          <p className="login-kicker">TOEIC Optimized Knowledge & Keyword Index</p>
          <h1 className="login-title">TOKKI</h1>
          <p className="login-subtitle">
            구글 계정으로 간편하게 로그인하세요.
          </p>
        </div>

        {error && (
          <div className="login-error" role="alert">
            <span className="login-error-icon">⚠</span>
            <span>로그인에 실패했습니다. 다시 시도해주세요.</span>
          </div>
        )}

        <div className="login-actions">
          <button
            className="google-login-btn"
            onClick={handleGoogleLogin}
            disabled={loading}
            type="button"
          >
            {loading ? (
              <span className="google-login-btn-loading">
                <span className="spinner" />
                연동 중...
              </span>
            ) : (
              <span className="google-login-btn-content">
                <svg
                  className="google-icon"
                  width="20"
                  height="20"
                  viewBox="0 0 48 48"
                  aria-hidden="true"
                >
                  <path
                    fill="#FFC107"
                    d="M43.6 20.1H42V20H24v8h11.3C33.9 33.5 29.3 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.8 1.2 8 3l5.7-5.7C34 6 29.3 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.2-2.7-.4-3.9z"
                  />
                  <path
                    fill="#FF3D00"
                    d="M6.3 14.7l6.6 4.8C14.3 15.5 18.8 12 24 12c3.1 0 5.8 1.2 8 3l5.7-5.7C34 6 29.3 4 24 4 16.3 4 9.7 8.3 6.3 14.7z"
                  />
                  <path
                    fill="#4CAF50"
                    d="M24 44c5.2 0 9.9-2 13.4-5.2l-6.2-5.2C29.1 35.1 26.7 36 24 36c-5.3 0-9.8-3.5-11.4-8.3l-6.5 5C9.5 39.6 16.2 44 24 44z"
                  />
                  <path
                    fill="#1976D2"
                    d="M43.6 20.1H42V20H24v8h11.3c-.8 2.2-2.2 4.1-4 5.5l.1-.1 6.2 5.2C36.9 39.2 44 34 44 24c0-1.3-.2-2.7-.4-3.9z"
                  />
                </svg>
                Google로 로그인
              </span>
            )}
          </button>
        </div>

        <div className="login-footer">
          <p>로그인하면 이용약관 및 개인정보처리방침에 동의하게 됩니다.</p>
        </div>
      </div>
    </main>
  );
}