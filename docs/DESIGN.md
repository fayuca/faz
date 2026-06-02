# faz — design

> Portfolio demo — domain-driven REST API, personal expense tracker, and in-app API explorer in one shell.

Technical how-to: [`README.md`](../README.md).

## Overview

**faz** is a **portfolio demo**: a versioned, domain-driven REST API with a **budget planner** for spending lines (record, browse, filter) and an in-app **API explorer**, delivered in one client shell and a single deploy.

**Repository:** [fayuca/faz](https://github.com/fayuca/faz)

## Product identity

An individual **personal expense tracker**. A transaction is a **spending line**: amount, description, category, date, and (v2) currency. Categories are personal (`FOOD`, `TRANSPORT`, `ENTERTAINMENT`, `UTILITIES`, `OTHER`). Amounts are unsigned positives—expense-only semantics.

### Apps in the shell

| App | Role |
| --- | --- |
| **Budget planner** | Product UI — CRUD, filters, sort, pagination |
| **API explorer** | Manifest-driven playground for calling and inspecting the API |

The planner and explorer share one deploy, one API client, generated types, validation conventions, and UI components. The explorer supports maintenance and review; it is not a separate product or host.

Future work: [ROADMAP.md](../.team/session/ROADMAP.md) *(session)*.

## Architecture

Architecture is documented here; README lists commands and stack.

### System shape

The React **shell** switches between **budget planner** and **API explorer** in the client—no separate routes or hosts. Both apps use the same origin, API client, generated types, validation conventions, and UI components.

```
┌─────────────┐     /api/*      ┌──────────────────────────────────┐
│   React     │ ──────────────► │  Spring Boot                     │
│   Vite      │   axios + proxy │  Controller → Service → JPA      │
│  (shell)    │   (dev only)    │  HSQLDB (dev) · Postgres (prod)  │
└─────────────┘                 └──────────────────────────────────┘
                                      ▲
                              nginx (Compose / Render)
```

In development the Vite dev server proxies `/api` to the backend. In Compose and on Render, nginx serves static assets and proxies `/api` to the backend so the UI and API share one origin.

### Domain-driven API

Business rules live in the Java domain layer, not in controllers or OpenAPI annotations.

- OpenAPI describes JSON on the wire; defaults, version-specific field rules, and persistence mapping live in services and entities.
- Each API version maps only the fields its DTO exposes (e.g. a v1 update must not clobber v2-only columns).
- Planner form defaults (currency, category) satisfy UX; they are not part of the API contract.

### Versioned HTTP contract

Breaking changes get a new path prefix (`/api/v1`, `/api/v2`, …). Non-breaking changes extend the current prefix. New endpoints do not require a new prefix. Older versions stay available until clients migrate.

See [Versioning](#versioning) for manifest, app release, and OpenAPI doc version.

### OpenAPI and manifest

OpenAPI and the explorer **manifest** answer different questions. OpenAPI is the wire contract (codegen, Swagger, committed snapshot for production frontend builds). The manifest lists resources, verbs, and per-version request/query shapes for the explorer. Keep them aligned when the contract changes; tests guard manifest completeness per declared version.

Production frontend images build from the committed `openapi.json` and generated `api.ts`, without a live backend at build time. After contract changes, regen locally and commit the snapshot and generated files together (see README).

### Backend (design view)

Layering is Controller → Service → JPA repository. List filters use JPA specifications. Errors are structured (validation, not-found).

| Concern | Approach |
| ------- | -------- |
| Persistence | HSQLDB in-memory for local dev and tests; PostgreSQL in Compose and Render (`prod` profile) |
| API surface | springdoc OpenAPI; controllers annotated for wire docs, logic in services |
| Schema drift | Hibernate `ddl-auto=update` in dev; prod Postgres may need migration hook for breaking column changes |

### Frontend (design view)

The React shell switches between planner and explorer. The planner provides list and form views, filters, pagination, and Zod validation on forms. The explorer drives requests from the manifest (verb bar, request panels); a coverage guard warns when the manifest lags OpenAPI.

API types are generated from OpenAPI; DTO aliases are hand-maintained where codegen names are awkward.

### Validation

Request DTOs use Jakarta validation on the server; forms use Zod on the client, aligned by convention. Both apps surface structured errors from the API.

### Delivery

| Environment | Shape |
| ----------- | ----- |
| **Local dev** | Backend `:8080` + Vite `:5173` with proxy |
| **Compose** | Postgres, backend, and nginx frontend on `:8080` (prod-like, single origin) |
| **Render** | Managed Postgres, Docker backend, Docker frontend (nginx); push to `main` deploys |

Commands, profiles, and URLs: README.

## Versioning

| Layer | Convention | Notes |
| ----- | ---------- | ----- |
| **API contract** | Path prefix `/api/v1`, `/api/v2`, … | Breaking HTTP or payload changes → new prefix; keep older until clients migrate |
| **Manifest** | Per-resource metadata aligned with OpenAPI | Explorer only |
| **App release** | Semver in `pom.xml` / `package.json` | Shown in meta shell (`v0.1.0` or current) |
| **OpenAPI `info.version`** | Contract doc version | Not the Maven/npm artifact version |

## Shipped baseline

*What exists on `main` today.*

| Area | State |
| ---- | ----- |
| REST API | v1 + v2 transactions; CRUD, filters, pagination |
| Budget planner | List, add/update/delete, filters, sort, pagination; USD/EUR |
| API explorer | Manifest-driven; v1/v2 verbs; coverage guard |
| Meta shell | App switcher, version label, per-app theme |
| Local dev | HSQLDB + Vite proxy |
| Prod-shaped stack | Docker Compose (Postgres + nginx) |
| Hosted | Render — [faz-frontend](https://faz-frontend.onrender.com) |
| Domain hygiene | Book currency centralized; OSIV off; Postgres schema migration hook for prod volumes |

## Decisions log

| Date | Decision |
| ---- | -------- |
| 2026-05-26 | Product: personal expense tracker (not business ledger) |
| 2026-05-26 | Core scope: CRUD + filter + deploy |
| 2026-05-27 | Deploy: Render; Compose for local prod-like stack |
| 2026-05-27 | Two apps (planner + explorer), meta shell, shared frontend API layer |
| 2026-05-27 | Versioning: path prefixes, manifest for explorer, semver in shell |
| 2026-05-31 | Package namespace: `systems.redtape` |
| 2026-06-01 | v2 API: currency on transactions; v1 preserves currency on partial update |
| 2026-06-02 | Domain defaults centralized; `open-in-view=false` |
| 2026-06-02 | README = technical how-to; architecture in DESIGN; ROADMAP session-only |
| 2026-06-02 | Doc model: ROADMAP informs arcs; CHECKLIST retired; CONCEPT session-only |
