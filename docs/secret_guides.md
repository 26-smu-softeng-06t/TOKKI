# TOKKI Secret Guide

Secrets are distributed out of band through Discord. Do not commit `.env`, `.env.cicd`, Firebase service-account JSON files, `.pem`, `.pub`, or private key material.

## GitHub Secrets

Use these repository secrets for deployment transport only:

- `HOST`: VPS public host or IP.
- `USERNAME`: SSH user for the VPS.
- `SSH_KEY`: private SSH key for deployment.
- `TOKKI_ENV`: full production app runtime `.env` content.

Do not place `HOST`, `USERNAME`, `SSH_KEY`, `VPS_*`, `DESIRED_PATH_URL`, `NAMECHEAP_URL`, `FIREBASE_SERVICE_ACCOUNT`, or `FIREBASE_SERVICE_ACCOUNT_PATH` inside `TOKKI_ENV`.

## TOKKI_ENV Contents

`TOKKI_ENV` should contain only app runtime keys, including:

- MySQL keys: `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`, `MYSQL_SSL_MODE`.
- Spring datasource keys: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_JPA_HIBERNATE_DDL_AUTO`.
- Auth keys: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`, `JWT_EXPIRATION`, `ADMIN_SECRET_KEY`.
- Frontend URLs and Firebase web keys: `VITE_API_BASE_URL`, `VITE_FIREBASE_*`.
- Firebase Admin env keys: `FIREBASE_PROJECT_ID`, `FIREBASE_CLIENT_EMAIL`, `FIREBASE_PRIVATE_KEY`.

Leave unknown values empty in local `.env` until the current value is shared through Discord.

## Firebase Admin Credentials

Preferred production format is env-based:

- `FIREBASE_PROJECT_ID`: service-account JSON `project_id`.
- `FIREBASE_CLIENT_EMAIL`: service-account JSON `client_email`.
- `FIREBASE_PRIVATE_KEY`: service-account JSON `private_key`.

Keep newline escapes in `FIREBASE_PRIVATE_KEY` as `\n` inside `.env`/`TOKKI_ENV`. The backend normalizes them at runtime.

Do not use `FIREBASE_SERVICE_ACCOUNT_PATH` in CI/CD unless the JSON file is also provisioned on the VPS by a separate secure process. Local development may still use `FIREBASE_SERVICE_ACCOUNT_PATH` with an ignored local JSON file.

## Local Cleanup Checklist

After importing secrets into local `.env` or GitHub Secrets:

1. Delete `.env.cicd`.
2. Delete local SSH key files such as `*.pem` and `*.pub`.
3. Delete Firebase service-account JSON files.
4. Run `git status --short` and confirm only intended tracked docs/config changes remain.
5. Run a secret scan before opening a PR.
