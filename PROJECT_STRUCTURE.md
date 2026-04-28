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
      quiz/                 Quiz sessions, answers, scoring
      user/                 User profile and role domain
      word/                 Vocabulary domain
      wrongnote/            Wrong-answer note domain
    src/main/resources/     Spring application configuration
    src/test/java/com/tokki/ Backend tests
  frontend/                 React client
    public/                 Static public assets
    src/
      api/                  Axios/API client layer
      components/           Reusable UI components
      features/             Feature-level frontend modules
      layouts/              Shared page layouts
      pages/                Route-level pages
      routes/               Router definitions
      styles/               Global styles and Tailwind entry
      utils/                Frontend utilities
  scripts/                  Local helper scripts
```

The requirements and design documents currently live in the sibling `../SoftwareEngineering_Docs` repository.

