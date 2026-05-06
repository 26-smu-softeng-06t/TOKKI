# TOKKI Database Routing Notes

## Frontend

The frontend does not connect to MySQL directly.

- Runtime API client: `frontend/src/lib/axios.ts`
- Local development API path: `/api`, proxied by Vite
- Local OAuth path: `/oauth2`, proxied by Vite for browser navigation
- Config source: `VITE_API_BASE_URL`

For normal data operations, React pages call service classes in `frontend/src/services`, and those service classes call backend REST APIs.

## Backend

The backend is the only application layer that connects to MySQL.

- Runtime config: `backend/src/main/resources/application.yml`
- JDBC URL: `SPRING_DATASOURCE_URL`
- Username: `SPRING_DATASOURCE_USERNAME`, falling back to `MYSQL_USERNAME`
- Password: `SPRING_DATASOURCE_PASSWORD`, falling back to `MYSQL_PASSWORD`
- Default JPA mode: `SPRING_JPA_HIBERNATE_DDL_AUTO`, defaulting to `validate`

## Current Database Check

The current development database metadata was checked through JDBC metadata and `SHOW CREATE TABLE`.

- Database: `defaultdb`
- Product: MySQL 8.0.45
- Tables: `users`, `stages`, `words`, `user_progress`, `incorrect_words`, `word_relations`, `quiz_sessions`, `quiz_answers`, `rankings`, `pvp_rooms`, `pvp_results`

The live schema uses `BIGINT AUTO_INCREMENT` entity IDs for content/gameplay tables and `users.uid` as the OAuth-backed user identifier. `backend/src/main/resources/schema.sql` is aligned to that live/JPA shape.

## Operation Queries

Use `docs/database-operations.sql` for metadata inspection, role management, and drift checks.
