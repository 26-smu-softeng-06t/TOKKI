package com.tokki.auth.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("auth-test")
public class AuthTestPageController {

    @GetMapping(value = "/auth-test/login", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> loginPage() {
        return ResponseEntity.ok("""
                <!doctype html>
                <html lang="ko">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>TOKKI OAuth Test</title>
                  <style>
                    body { margin: 0; min-height: 100vh; display: grid; place-items: center; font-family: system-ui, sans-serif; background: #f8fafc; color: #0f172a; }
                    main { width: min(100% - 32px, 420px); background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 28px; box-shadow: 0 12px 30px rgba(15, 23, 42, .08); }
                    h1 { margin: 0 0 8px; font-size: 28px; }
                    p { color: #475569; line-height: 1.6; }
                    a, button { width: 100%; display: inline-flex; justify-content: center; border: 0; border-radius: 8px; padding: 12px 16px; background: #4f46e5; color: white; font-weight: 700; text-decoration: none; cursor: pointer; }
                    code { display: block; margin-top: 16px; padding: 12px; border-radius: 8px; background: #f1f5f9; color: #334155; white-space: pre-wrap; }
                  </style>
                </head>
                <body>
                  <main>
                    <h1>TOKKI OAuth Test</h1>
                    <p>Google OAuth2 로그인 왕복과 Spring Security 세션 생성을 확인합니다.</p>
                    <a href="/oauth2/authorization/google">Google로 로그인</a>
                    <code>redirect: /login/oauth2/code/google
callback: /auth-test/callback</code>
                  </main>
                </body>
                </html>
                """);
    }

    @GetMapping(value = "/auth-test/callback", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callbackPage() {
        return ResponseEntity.ok("""
                <!doctype html>
                <html lang="ko">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>TOKKI OAuth Callback</title>
                  <style>
                    body { margin: 0; min-height: 100vh; display: grid; place-items: center; font-family: system-ui, sans-serif; background: #f8fafc; color: #0f172a; }
                    main { width: min(100% - 32px, 460px); background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 28px; box-shadow: 0 12px 30px rgba(15, 23, 42, .08); }
                    h1 { margin: 0 0 8px; font-size: 28px; }
                    p { color: #475569; line-height: 1.6; }
                    button { border: 0; border-radius: 8px; padding: 10px 14px; background: #475569; color: white; font-weight: 700; cursor: pointer; }
                    pre { overflow: auto; padding: 12px; border-radius: 8px; background: #f1f5f9; color: #334155; }
                  </style>
                </head>
                <body>
                  <main>
                    <h1>로그인 세션 확인 중</h1>
                    <p id="summary">/api/auth/me 응답을 확인하고 있습니다.</p>
                    <pre id="result">{}</pre>
                    <button id="logout" type="button">로그아웃 테스트</button>
                  </main>
                  <script>
                    async function verify() {
                      const response = await fetch('/api/auth/me', { credentials: 'include' });
                      const json = await response.json();
                      document.querySelector('#result').textContent = JSON.stringify(json, null, 2);
                      document.querySelector('#summary').textContent = json.data?.authenticated
                        ? 'Google OAuth2 로그인과 서버 세션 확인에 성공했습니다.'
                        : '서버 세션을 찾지 못했습니다.';
                    }
                    document.querySelector('#logout').addEventListener('click', async () => {
                      await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                      location.href = '/auth-test/login';
                    });
                    verify().catch((error) => {
                      document.querySelector('#summary').textContent = '세션 확인 중 오류가 발생했습니다.';
                      document.querySelector('#result').textContent = String(error);
                    });
                  </script>
                </body>
                </html>
                """);
    }
}
