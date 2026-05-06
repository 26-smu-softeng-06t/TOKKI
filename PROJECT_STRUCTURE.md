# TOKKI Project Structure

```text
TOKKI/
  .github/                  GitHub issue and PR templates
  assets/                   Shared static project assets
  backend/                  Spring Boot API server
    src/main/java/com/tokki/
      admin/                Admin-only vocabulary management
      auth/                 Google OAuth2, JWT, role checks
      common/               Shared errors, responses, utilities
      config/               Spring configuration
      controller/           REST controllers
      quiz/                 Quiz sessions, answers, scoring
      user/                 User profile and role domain
      word/                 Vocabulary domain
      wrongnote/            Wrong-answer note domain
    src/main/resources/     Spring application configuration
    src/test/java/com/tokki/ Backend tests
  frontend/                 React client
    public/                 Static public assets
    src/
      components/           Reusable UI components
      context/              Authentication context
      hooks/                Shared React hooks
      lib/                  Axios/API client and browser integrations
      layouts/              Shared page layouts
      pages/                Route-level pages
      routes/               Router definitions
      styles/               Global styles
      utils/                Frontend utilities
  scripts/                  Local helper scripts
```

The requirements and design documents currently live in the sibling `../SoftwareEngineering_Docs` repository.

## Implementation boundaries

- Frontend implementation should keep backend communication behind `frontend/src/lib/axios.ts` and service classes in `frontend/src/services`.
- Feature-specific React state and screens should live under `frontend/src/features` and `frontend/src/pages`.
- Backend API responses should use the shared `com.tokki.common.api` response wrappers.
- OAuth provider-specific parsing belongs under `com.tokki.auth.oauth`, so adding another provider does not require changing controller code.
- Environment-backed settings should be exposed through `com.tokki.config.properties` instead of direct `@Value` fields.

