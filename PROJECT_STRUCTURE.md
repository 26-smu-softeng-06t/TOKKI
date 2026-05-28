# TOKKI Project Structure

```text
TOKKI/
  .github/                  GitHub issue and PR templates
  assets/                   Shared static project assets
  backend/                  Spring Boot API server
    build.gradle            Backend Gradle module build
    src/main/java/com/tokki/
      auth/                 Google OAuth2, JWT, role checks
      batch/                Scheduled ranking jobs
      common/api/           Shared API response wrappers
      config/               Spring configuration
      config/properties/    Environment-backed configuration properties
      controller/           REST controllers
      domain/               JPA entities and enums
      dto/                  Request/response DTOs
      exception/            Application error handling
      repository/           Spring Data repositories
      security/             JWT/Firebase security integration
      service/              Application services
      websocket/            PvP WebSocket handler
    src/main/resources/     Spring application configuration
    src/test/java/com/tokki/ Backend tests
  frontend/                 React client
    package.json            Frontend npm package
    public/                 Static public assets
    src/
      api/                  Frontend-only mock data and API helpers
      assets/               Frontend image/static imports
      components/           Reusable UI components
      context/              Authentication context
      hooks/                Shared React hooks
      lib/                  Axios/API client and browser integrations
      pages/                Route-level pages
      services/             Backend API service classes
      types/                Shared frontend TypeScript types
  gradle/                   Root Gradle wrapper files
  scripts/                  Local helper scripts
  build.gradle              Root Gradle task orchestration
  settings.gradle           Includes the backend module
```

The requirements and design documents currently live in the sibling `../SoftwareEngineering_Docs` repository.

## Implementation boundaries

- Frontend implementation should keep backend communication behind `frontend/src/lib/axios.ts` and service classes in `frontend/src/services`.
- Feature-specific React state and screens should live under `frontend/src/pages`, `frontend/src/components`, and `frontend/src/services` unless a new feature module is introduced deliberately.
- Backend API responses should use the shared `com.tokki.common.api` response wrappers.
- OAuth provider-specific parsing belongs under `com.tokki.auth.oauth`, so adding another provider does not require changing controller code.
- Environment-backed settings should be exposed through `com.tokki.config.properties` instead of direct `@Value` fields.

