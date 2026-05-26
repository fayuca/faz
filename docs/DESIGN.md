# faz — design

> Canonical project design. Session continuity in [`HANDOFF.md`](HANDOFF.md). Root [`README.md`](../README.md) stays a short entry point.

## Overview

**faz** is a **personal expense tracker** — a CV demo repo showing a full vertical slice: database → Spring Boot API → React UI.

Primary audience: interviewers and the author (re-learning modern Spring/React fundamentals with standard practices, not banking-era habits).

**Repository:** [fayuca/faz](https://github.com/fayuca/faz)

## Goals

- Demonstrate a **working full-stack slice** you can run locally and eventually share via URL.
- Keep the backend centered on one domain object — **`Transaction`** — touching DTOs, validation, repository, specifications, service, controller, exception handling, and tests.
- Present a **relatable product story** (personal spending), not a generic business ledger.
- Learn and apply **standard practices** deliberately; treat the demo as permanent realignment, not a one-off hack.

## Non-goals (for now)

- Business / vendor ledger semantics (methods vary by company; wrong story for a CV demo).
- Auth, multi-user, roles.
- Full accounting (double-entry, chart of accounts) — backlog only unless it clearly serves the demo.
- Production-grade ops (monitoring, CI/CD pipeline) — post-MVP unless needed for deploy.

## Product identity

**What this is:** an individual **personal expense tracker**. A transaction is a spending line: amount, description, category (and eventually date).

**What this is not:** a small-business P&L tool or double-entry bookkeeping system.

**Categories today** (`FOOD`, `TRANSPORT`, `ENTERTAINMENT`, `UTILITIES`, `OTHER`) fit personal spending. Amounts are unsigned positives (expense-only semantics).

### Backlog (explicit maybe)

| Idea | Status |
|------|--------|
| Budgeting (monthly caps per category, simple over-budget indicator) | Post-MVP stretch |
| Double-entry bookkeeping | Post-MVP stretch; only if it still serves the demo without over-engineering |

## Architecture

```
┌─────────────┐     /api/*      ┌──────────────────────────────────┐
│   React     │ ──────────────► │  Spring Boot (embedded Tomcat)   │
│   Vite      │   axios + proxy │  Controller → Service → JPA      │
│  frontend/  │   (dev only)    │  HSQLDB (in-memory, dev)         │
└─────────────┘                 └──────────────────────────────────┘
```

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

**API base:** `/api/transactions` — CRUD, pagination, filter by description / amount range / category.

**OpenAPI:** springdoc (`springdoc-openapi-starter-webmvc-ui`); Swagger UI on backend when running.

**Stack:** Java 22, Spring Boot 4.0.6, Spring Data JPA, HSQLDB (in-memory), JUnit 5, Mockito, MockMvc.

**Tests:** `FazApplicationTests`, `TransactionServiceTest`, `TransactionControllerTest`, `TransactionIntegrationTest`.

### Frontend (`frontend/`)

| Area | Path | Role |
|------|------|------|
| Entry | `src/main.tsx`, `App.tsx` | Single-page shell |
| Page | `pages/TransactionsPage.tsx` | List, filter, create |
| Components | `components/` | Form, table |
| Data | `hooks/useTransactions.ts`, `api/` | Axios client, criteria |

**Stack:** React 19, Vite 8, TypeScript, axios. Dev server proxies `/api` → `localhost:8080`.

**UI today:** create + list + paginate + description filter. No update/delete UI yet (API supports both).

## MVP scope

**Target:** vertical slice minimum — shareable deploy + create/list/filter + clear run story. Polish deferred.

| In MVP | Post-MVP |
|--------|----------|
| Deployed app (URL to share) | Full update/delete UI |
| Backend CRUD API (exists) | Lite API explorer section in frontend |
| Frontend create + list + filter | UI polish, branding, copy |
| Run instructions (README + OVERVIEW) | Budget caps / alerts |
| **Initial cleanup** (known small bugs/debt) | Double-entry / accounts |
| Persistent DB for deploy (replace in-memory HSQLDB) | |

## Known debt

Initial cleanup completed 2026-05-26 (category update bug, debug log, date wiring, frontend README).

Future small fixes land with related work.

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

**Backend tests:**

```bash
cd backend
./mvnw test
```

## Deployment

**Status:** TBD — no host chosen yet. Author background: EAR/WAR drop on app-server filesystem + restart (later Jenkins); new to cloud/PaaS/Docker for side projects.

### Primer — EAR/WAR → this stack

Study reference (landed 2026-05-26 conceptual alignment).

**What maps from day-job deployment**

| Old (corporate banking) | faz (this demo) |
|-------------------------|-----------------|
| EAR/WAR → JBoss/Tomcat | **Executable JAR** (`spring-boot-maven-plugin`) — `java -jar faz.jar` |
| App server owns the JVM | Spring Boot **is** the runtime (embedded server) |
| DB on another host / JNDI datasource | **HSQLDB in-memory today** — fine for dev/tests, **not** for real deploy (data lost on restart) |
| Static assets sometimes bundled in WAR | React → **static files** (`npm run build` → `dist/`) — served separately or via nginx |

**Three deployment families** (simplest → more “cloud-native”)

1. **JAR on a VM** (closest to “drop and restart”)  
   Build JAR, copy to Linux, run as a service, nginx in front. You manage OS, Java, SSL, restarts. Same mental model as before, minus installing an app server.

2. **Docker Compose** (recommended **first learning step**)  
   Dockerfile for backend; frontend built to static files served by nginx (or similar); `docker-compose.yml` wires ports, env, and eventually Postgres. “Drop and restart” becomes `docker compose up`. Same compose runs locally and on a cheap VPS.

3. **PaaS** (Render, Railway, Fly.io, …)  
   Connect GitHub; platform builds and runs. Less ops; learn env vars, health checks, free-tier limits. Typical layout: backend service + static frontend (or one container with nginx proxying `/api`).

**What MVP deploy will also need**

- **Persistent DB** — PostgreSQL is the usual HSQLDB replacement (`dev` profile = HSQLDB, `prod` = Postgres).
- **Single origin or CORS** — Vite dev proxy does not exist in production; nginx reverse proxy or explicit CORS.
- **Demo link in README** — live URL + local run section.

**Suggested learning sequence** (when ready)

1. Local run docs (above).  
2. Docker Compose (backend + frontend + Postgres).  
3. Pick **one** free PaaS; deploy; obtain shareable URL.  
4. Post-MVP polish.

Vendor choice deferred — document decisions here when made.

## Learning approach

Continue building with deliberate fundamentals: understand *why* each layer exists, prefer standard Spring/React patterns over one-off shortcuts, and keep the codebase presentable in an interview walkthrough.

CONCEPT and session notes live in [`docs/session/CONCEPT.md`](session/CONCEPT.md) until promoted or trimmed.

## Session workflow

This repo uses [double-click](https://github.com/greyar/double-click) session files under `docs/session/` and `.cursor/` commands (`/boot-up`, `/wrap-up`). Technical and conceptual import alignments completed 2026-05-26.

## Decisions log

| Date | Decision |
|------|----------|
| 2026-05-26 | Product: personal expense tracker (not business ledger) |
| 2026-05-26 | MVP: vertical slice minimum |
| 2026-05-26 | Deploy host: TBD; primer saved in this doc |
| 2026-05-26 | First dev task after alignment: initial cleanup |
| 2026-05-26 | Budgeting / double-entry: post-MVP backlog |
