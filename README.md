# faz

> Portfolio demo — domain-driven REST API, personal expense tracker, and in-app API explorer in one shell.
>
> **Live:** https://faz-frontend.onrender.com

## Quick start (Docker)

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine + Compose).

```bash
docker compose up --build
```

Open http://localhost:8080

## Development

**Backend** (port 8080):

```bash
cd backend
./mvnw spring-boot:run    # Windows: mvnw.cmd spring-boot:run
```

**Frontend** (proxies `/api` to backend):

```bash
cd frontend
npm install
npm run dev
```

**API types** (after backend contract changes):

```bash
cd frontend
npm run codegen:api:live          # refresh openapi/openapi.json + src/api/generated/api.ts from /v3/api-docs
# or offline: npm run codegen:api  # regen api.ts from committed openapi.json only
```

Commit **`frontend/openapi/openapi.json`** and **`src/api/generated/api.ts`** together when the contract changes (Docker/Render build uses the committed files; no codegen in the image). Hand-maintained aliases live in `src/api/generated/dtos.ts`.

**Tests:**

```bash
cd backend
./mvnw test

cd ../frontend
npm run build
```

## Features

- Two apps in one **shell**: **budget planner** and **API explorer**
- Create, update, delete, list, and filter transactions (v1 and v2 API)
- Validation and structured API errors
- Backend tests (unit, controller, integration)

## Architecture

faz is a deployed full-stack app: one repo, a versioned REST API, and **two apps**—**budget planner** and **API explorer**—in one **shell** for demo convenience.

### Domain-driven API

Business rules live in the Java domain layer, not in controllers or OpenAPI annotations.

- **Contract vs domain** — OpenAPI describes JSON on the wire. Defaults, version-specific rules, and persistence mapping live in services and entities.
- **Version-scoped updates** — each API version maps only the fields its DTO exposes.
- **UI defaults are separate** — they extend the domain to satisfy UX needs.

### Versioned HTTP contract

Breaking changes get a new path prefix (`/api/v1`, `/api/v2`, …). Non-breaking changes extend the current prefix. New endpoints do not require a new prefix. Older versions stay available until clients migrate.

### OpenAPI and manifest

Two documents, two jobs:

| Document | Role |
| -------- | ---- |
| **OpenAPI** | Wire contract; TypeScript codegen; Swagger; committed snapshot for production frontend builds |
| **Manifest** | API explorer: resources, verbs, per-version request/query shapes |

Both must stay aligned when the contract changes. Tests guard manifest completeness per declared version.

### Two apps, one shell

| App | Role |
| --- | ---- |
| **Budget planner** | Product UI — create, list, filter expenses |
| **API explorer** | Call and inspect the API from the same origin |

One shell, one deploy. The shell switches between them in the client—no separate routes or hosts per app. The split is logical (product vs API surface), not enforced by the router.

Shared across both: API client, generated types, validation conventions, and UI components.

### Validation (cross-cutting)

Server-side Jakarta validation; client-side Zod, aligned by convention.

### Backend layout

Controller → Service → JPA repository. Dynamic filters via specifications. Structured error responses. HSQLDB for local dev and tests; PostgreSQL in Compose and on Render.

### Delivery

Docker Compose matches production shape. Render deploys from `main`. Committed OpenAPI snapshot so the frontend image builds without a running backend.

Further detail: [`docs/DESIGN.md`](docs/DESIGN.md).

## Stack

- Java 22 · Spring Boot 4 · Spring Data JPA · PostgreSQL / HSQLDB
- React 19 · Vite 8 · TypeScript

## License

Source code and documentation (including `docs/`): [MIT](LICENSE).
