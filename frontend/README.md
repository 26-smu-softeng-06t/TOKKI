# TOKKI Frontend Notes

This package is ready for route-level React implementation without coupling UI code to backend internals.

## Runtime contract

- API calls should go through `src/api/axios.js`.
- In local development, Vite proxies `/api` and `/oauth2` to `VITE_API_BASE_URL`.
- OAuth login starts at `GET /api/auth/google-url` and redirects to `/oauth2/authorization/google`.
- Auth status is available at `GET /api/auth/me`.
- Logout uses `POST /api/auth/logout`.

## Firebase environment

Firebase web config is read from the root `.env` through Vite's `envDir: '../'`.

Required client-side keys:

- `VITE_FIREBASE_API_KEY`
- `VITE_FIREBASE_AUTH_DOMAIN`
- `VITE_FIREBASE_PROJECT_ID`
- `VITE_FIREBASE_STORAGE_BUCKET`
- `VITE_FIREBASE_MESSAGING_SENDER_ID`
- `VITE_FIREBASE_APP_ID`
- `VITE_FIREBASE_MEASUREMENT_ID`

Keep feature implementation inside `src/features`, shared shells in `src/layouts`, reusable UI in `src/components`, and route screens in `src/pages`.
