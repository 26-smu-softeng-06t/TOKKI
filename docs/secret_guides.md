# TOKKI 시크릿 관리 가이드

시크릿 값은 Discord로 별도 배포한다. `.env`, `.env.cicd`, Firebase 서비스 계정 JSON, `.pem`, `.pub`, 개인키 파일은 커밋하지 않는다.

## GitHub Secrets

배포 연결 정보는 GitHub repository secret으로 분리해서 관리한다.

- `HOST`: VPS 공개 호스트 또는 IP.
- `USERNAME`: VPS SSH 사용자.
- `SSH_KEY`: 배포용 SSH private key.
- `TOKKI_ENV`: 운영 앱 런타임 `.env` 전체 내용.

`TOKKI_ENV`에는 앱 실행에 필요한 환경변수만 넣는다. `HOST`, `USERNAME`, `SSH_KEY`, `VPS_*`, `DESIRED_PATH_URL`, `NAMECHEAP_URL`, `FIREBASE_SERVICE_ACCOUNT`, `FIREBASE_SERVICE_ACCOUNT_PATH` 같은 배포 연결 정보나 파일 경로는 넣지 않는다.

## TOKKI_ENV 구성

`TOKKI_ENV`에는 아래 범주의 키만 포함한다.

- MySQL: `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`, `MYSQL_SSL_MODE`.
- Spring datasource: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_JPA_HIBERNATE_DDL_AUTO`.
- 인증: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`, `JWT_EXPIRATION`, `ADMIN_SECRET_KEY`.
- 프론트엔드 및 Firebase Web SDK: `VITE_API_BASE_URL`, `VITE_FIREBASE_*`.
- Firebase Admin SDK: `FIREBASE_PROJECT_ID`, `FIREBASE_CLIENT_EMAIL`, `FIREBASE_PRIVATE_KEY`.

Codex가 알 수 없는 값은 비워둔다. 실제 값은 Discord로 전달받은 뒤 로컬 `.env` 또는 GitHub Secrets에만 입력한다.

## Firebase Admin SDK

운영 배포와 로컬 실행 모두 JSON 파일을 추가로 전달하지 않고 환경변수 기반 설정을 사용한다.

- `FIREBASE_PROJECT_ID`: 서비스 계정 JSON의 `project_id`.
- `FIREBASE_CLIENT_EMAIL`: 서비스 계정 JSON의 `client_email`.
- `FIREBASE_PRIVATE_KEY`: 서비스 계정 JSON의 `private_key`.

`.env`와 `TOKKI_ENV` 안의 `FIREBASE_PRIVATE_KEY`는 줄바꿈을 `\n` 형태로 보관한다. 백엔드는 실행 시 이를 실제 줄바꿈으로 변환한다.

`FIREBASE_SERVICE_ACCOUNT_PATH`는 사용하지 않는다. Firebase 서비스 계정 JSON을 받은 경우 위 세 값으로 변환해 `.env` 또는 `TOKKI_ENV`에 넣고, JSON 파일 자체는 저장소와 배포 서버에 남기지 않는다.
