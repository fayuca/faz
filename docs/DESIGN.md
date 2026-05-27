# faz — design

> Product and technical design. Entry point: [`README.md`](../README.md).

## Overview

**faz** is a **personal expense tracker**: record spending lines, browse and filter them, and see totals over time.

**Repository:** [fayuca/faz](https://github.com/fayuca/faz)

## Goals

- Track personal expenses with amount, description, category, and date.
- Provide a simple web UI for create, list, and filter.
- Expose a REST API suitable for future clients or integrations.
- Run locally for development and in Docker / cloud for a hosted instance.

## Non-goals (current)

- Business or vendor ledger semantics.
- Authentication, multi-user accounts, or roles.
- Full accounting (double-entry, chart of accounts).
- Production operations (monitoring, CI/CD) beyond basic deploy.

## Product identity

**What this is:** an individual **personal expense tracker**. A transaction is a spending line: amount, description, category, and date.

**What this is not:** a small-business P&L tool or double-entry bookkeeping system.

**Categories** (`FOOD`, `TRANSPORT`, `ENTERTAINMENT`, `UTILITIES`, `OTHER`) fit personal spending. Amounts are unsigned positives (expense-only semantics).

### Roadmap (post-MVP)

Deliver **one slice per TASK**. Order is priority, not strict serial — except versioning and shared API wiring, which cut across slices.

| P | Slice | Notes |
|---|--------|--------|
| **0** | **Solid CRUD** | Update/delete in UI + anything else needed for trustworthy create/read/update/delete. **Introduce API path versioning** (`/api/v1/...`) here or immediately after — see [Versioning](#versioning). |
| **1** | **App shell** | `<App />` = meta layer + two apps. Corner **app switch** (meta only; not owned by planner or explorer). |
| **2a** | API explorer — UI baseline | Shared style and components. |
| **2b** | API explorer — API bridge | Wire UI to calls; **shared client/hooks** with budget planner. |
| **2c** | API explorer — **manifest** | Describes resources, verbs, and **contract version** (aligned with `/api/v1`). |
| **3** | Repetitive work | One item at a time: explorer **verbs**, planner **CRUD screens**, etc. Often finish one app before the other. |
| **4** | Planner — expense reports | |
| **5** | Planner — bookkeeping | Large; after reports. |

**Two in-app products:** **budget planner** (transactions today → reports → bookkeeping) and **API explorer** (manifest-driven). MVP UI is planner-only; explorer and shell follow the table.

**Backlog (unchanged intent):** monthly category caps — folded into planner work, not a separate MVP line.

### Versioning

Versioning is cross-cutting: paths, manifest, and visible app release.

| Layer | Convention | Consumer |
|-------|------------|----------|
| **API contract** | Path prefix `/api/v1/` (e.g. `/api/v1/transactions`). Breaking HTTP or payload changes → new `v2`; keep `v1` until clients migrate. | Backend controllers, OpenAPI, nginx/Vite proxy (still `/api/*`), shared `frontend/src/api/` base URL. |
| **Manifest** | `version: "v1"` (and per-resource metadata) in the explorer manifest slice. Single source for explorer UI labels and verb lists. | API explorer only; generated or hand-maintained to match OpenAPI. |
| **App release** | Semver aligned across `backend/pom.xml` and `frontend/package.json` (e.g. `0.1.0` post-MVP). Injected at **build** (Vite `define`, Spring `info` or resource). | Meta shell (switcher corner): show **faz** version, not per-child-app. |
| **MVP today** | Unversioned `/api/transactions` | Migrate to `v1` in P0; optional short-lived redirect `/api/transactions` → `/api/v1/transactions` if needed for bookmarks. |

OpenAPI `info.version` tracks **contract** (e.g. `1.0.0` for v1 surface), not the Maven/npm artifact version.

## Architecture

```
┌─────────────┐     /api/*      ┌──────────────────────────────────┐
│   React     │ ──────────────► │  Spring Boot (embedded Tomcat)   │
│   Vite      │   axios + proxy │  Controller → Service → JPA      │
│  frontend/  │   (dev only)    │  HSQLDB (dev) · Postgres (prod)  │
└─────────────┘                 └──────────────────────────────────┘
```

Production adds **nginx** in front of static assets and proxies `/api` to the backend (see Deployment).

### Backend (`backend/`)

| Layer | Package / path | Role |
|-------|----------------|------|
| Entry | `FazApplication.java` | Spring Boot main |
| Entity | `entity/Transaction` | JPA model |
| DTOs | `dto/` | Request, response, criteria, pagination |
| Repository | `repository/TransactionRepository` | Spring Data JPA |
| Specifications | `specification/TransactionSpecifications` | Dynamic filters |
| Service | `service/TransactionService` | Business logic |
| Controller | `controller/TransactionController` | REST + OpenAPI annotations |
| Errors | `exception/` | Global handler, 404, validation errors |

**API base (MVP):** `/api/transactions` — CRUD, pagination, filter by description / amount range / category / date range. **Target:** `/api/v1/transactions` ([Versioning](#versioning)).

**OpenAPI:** springdoc (`springdoc-openapi-starter-webmvc-ui`); Swagger UI on the backend when running.

**Stack:** Java 22, Spring Boot 4.0.6, Spring Data JPA, HSQLDB (in-memory dev), PostgreSQL (prod), JUnit 5, Mockito, MockMvc.

**Tests:** `FazApplicationTests`, `TransactionServiceTest`, `TransactionControllerTest`, `TransactionIntegrationTest`.

### Frontend (`frontend/`)

| Area | Path | Role |
|------|------|------|
| Entry | `src/main.tsx`, `App.tsx` | Shell → meta switch + planner + explorer (post-MVP) |
| Page | `pages/TransactionsPage.tsx` | Planner: list, filter, create (CRUD completion in P0) |
| Components | `components/` | Form, table |
| Data | `hooks/useTransactions.ts`, `api/` | Axios client, criteria |

**Stack:** React 19, Vite 8, TypeScript, axios. Dev server proxies `/api` → `localhost:8080`.

**UI today:** create, list, paginate, description filter. Update/delete exist in the API only.

## MVP (shipped)

| Area | Status |
|------|--------|
| REST API (CRUD, filters) | Done |
| Frontend create + list + filter | Done |
| Local dev (HSQLDB) | Done |
| Docker Compose (Postgres + nginx) | Done |
| Hosted deploy (Render) | Done |

## Local development

**Backend** (port 8080):

```bash
cd backend
./mvnw spring-boot:run    # Windows: mvnw.cmd spring-boot:run
```

**Frontend** (Vite dev server, proxies `/api`):

```bash
cd frontend
npm install
npm run dev
```

**Tests:**

```bash
cd backend
./mvnw test
```

**Docker Compose** (Postgres + backend + nginx frontend):

```bash
docker compose up --build
```

Open http://localhost:8080 — UI and `/api` share one origin via nginx.

## Deployment

### Hosted (Render)

| Service | URL |
|---------|-----|
| **App (public)** | https://faz-frontend.onrender.com |
| Backend API | https://faz-backend.onrender.com |

Infrastructure: `render.yaml` — managed Postgres, Docker backend, Docker frontend (nginx). Spring **`prod`** profile uses PostgreSQL; frontend proxies `/api` to the backend public URL.

Free-tier services may spin down after idle; first request after sleep can take ~30s.

### Local production-like stack

`docker-compose.yml` at repo root — same images as Render, with Compose service names for internal DNS (`backend`, `db`).

### Profiles

| Profile | Database | When |
|---------|----------|------|
| default | HSQLDB in-memory | Local `mvnw spring-boot:run`, tests |
| `prod` | PostgreSQL | Compose and Render |

Config: `application.properties` (dev), `application-prod.properties` (prod, env overrides via `DB_*` / `SPRING_DATASOURCE_*`).

## Decisions log

| Date | Decision |
|------|----------|
| 2026-05-26 | Product: personal expense tracker (not business ledger) |
| 2026-05-26 | MVP scope: core CRUD + filter + deploy |
| 2026-05-27 | Deploy: Render; Compose for local prod-like stack |
| 2026-05-26 | Budgeting / double-entry: backlog |
| 2026-05-27 | Post-MVP: two apps (planner + API explorer), meta shell switch, phased roadmap |
| 2026-05-27 | Versioning: `/api/v1` contract, manifest `v1`, semver in meta shell |
