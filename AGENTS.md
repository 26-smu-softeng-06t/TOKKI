# TOKKI Agent Constitution

This file is the tracked Constitution for AI agents working on TOKKI. It defines the durable rules for how agents should read project material, split work, and change the implementation. It is not the place for full implementation prompts, API tables, ERD details, screen specs, or step-by-step build scripts.

## 1. Authority And Source Order

Follow this order when making decisions:

1. Newest explicit human instruction in the current task.
2. Existing implementation, tests, runtime behavior, and deployment state.
3. This `AGENTS.md` Constitution.
4. Local reference material under `Prompts/` and `Diagram&ERD&명세서/`.
5. Tool-specific wrappers such as `CLAUDE.md`, `GEMINI.md`, and `.github/copilot-instructions.md`.

If sources conflict, do not blend incompatible rules. Pick the highest-authority source, keep the change scoped, and note the discrepancy in the task summary or PR body. If the conflict changes product scope, auth, schema, security, or phase boundaries, stop and surface it before implementing.

## 2. Reference Document Map

Detailed implementation guidance lives outside this Constitution:

- `Prompts/README.md`: prompt module index, prompt usage rules, and conflict-resolution notes.
- `Prompts/backend_prompts.md`: backend implementation prompt modules.
- `Prompts/frontend_prompts.md`: frontend implementation prompt modules.
- `Diagram&ERD&명세서/README.md`: requirements/specification index and precedence notes.
- `Diagram&ERD&명세서/요구사항명세서/요구사항명세서.md`: FR/NFR and phase scope.
- `Diagram&ERD&명세서/API 명세서/Api.md`: API contracts.
- `Diagram&ERD&명세서/ERD/ERD.md`: logical data model and DDL references.
- `Diagram&ERD&명세서/Diagram/*.md`: use case, sequence, and class diagrams.
- `Diagram&ERD&명세서/UI명세서/Ui.md`: screen and interaction specs.

`Prompts/` and `Diagram&ERD&명세서/` are local AI reference folders and are intentionally ignored by Git. Do not move their long-form prompt/spec contents into this file. If a remote clone does not contain those ignored folders, use the tracked code and docs first, then ask for the missing reference material only when it is needed for a high-risk or spec-heavy change.

## 3. Prompt Modularity Rules

Agents must modularize prompts before implementation:

- One prompt module should target one layer, bounded context, phase, or user flow.
- Do not paste a full 10-step frontend/backend prompt into one implementation pass.
- For each prompt module, state the target paths, source documents, phase, acceptance criteria, verification command, and out-of-scope items.
- Keep frontend, backend, database, deployment, and documentation prompts separate unless the task explicitly crosses a contract boundary.
- When a prompt changes an API or schema, pair it with the relevant API/ERD/spec reference and update affected consumers in the same task or open a follow-up.
- Treat old prompt text as a bootstrap aid. Current code and this Constitution override stale scaffolding details.

## 4. Implementation Modularity Rules

Agents must keep code changes modular:

- Backend work should preserve the local package boundaries for config/security, domain, repository, service, controller, DTO, exception, migration/seed, and tests.
- Frontend work should preserve local boundaries for routes/pages, components, context/hooks, services/API clients, types, and UI state.
- Keep auth/user/role/admin concerns separate from vocabulary/stage/progress/quiz/wrong-note concerns.
- Avoid broad rewrites when a focused module change satisfies the task.
- Add shared abstractions only when they remove real duplication or match an existing local pattern.
- Do not silently implement Phase 2 or Phase 3 behavior while closing Phase 1/MVP work.

## 5. Product And Security Invariants

These are Constitution-level constraints. Detailed field lists and endpoint specs belong in the reference folders.

- TOKKI is an SSO-only TOEIC vocabulary learning web app.
- Do not add local password login or password storage.
- Firebase Authentication ID tokens are the authentication basis for REST APIs.
- Admin authorization must be enforced server-side through role and secret validation.
- Admin secrets, Firebase private keys, OAuth tokens, JWTs, database passwords, and service account material must never be committed, logged, or copied into PR/issue text.
- MySQL domain ownership remains separated by bounded context: auth/user/role/admin data belongs to auth ownership; vocabulary/stage/progress/quiz/wrong-note data belongs to vocabulary ownership.
- Difficulty values are `easy`, `medium`, and `hard`; do not reintroduce `low`/`high` as canonical API values.
- Quiz answers are subjective/free-text unless an approved requirement change says otherwise.
- MVP work should stay inside the approved MVP scope. Ranking, Excel upload, TTS, quiz resume, word relations, PvP, business examples, and offline progress are later-phase work unless explicitly requested.

## 6. Workflow Rules

- Start from the real repo state. Read the relevant code before editing.
- For issue-driven work, read the issue/PBI context and map the change to the related requirement or user flow when practical.
- Keep PR-sized work focused and reviewable.
- Do not commit, amend, rebase, reset, force-push, or open a PR unless a human asks for that publish step.
- If local `.env` or secret-bearing files are touched, preserve secret values locally and never echo them back.
- Documentation updates belong close to the changed behavior. Keep Constitution changes rare; put detailed implementation prompts in `Prompts/` and detailed specs in `Diagram&ERD&명세서/`.

## 7. Verification Rules

- Run the most relevant real project checks available for the changed module.
- Never claim a check passed unless it actually ran.
- If a check cannot run because dependencies, credentials, network, or tooling are missing, state the exact blocker and the next concrete step.
- For UI changes, capture screenshots or record a manual browser verification note.
- For auth, schema, deployment, or migration changes, prefer concrete runtime evidence over static inspection alone.

## 8. Tool-Specific Files

`CLAUDE.md`, `GEMINI.md`, and `.github/copilot-instructions.md` are thin tool adapters. They must point back to this Constitution and should not duplicate detailed project rules. Shared guidance belongs here; implementation prompts and spec references belong in the ignored reference folders.
