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

| Idea | Status |
|------|--------|
| Update/delete in UI | Planned |
| Lite API explorer in frontend | Planned |
| Budgeting (monthly caps per category) | Backlog |
| Double-entry bookkeeping | Backlog |

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

**API base:** `/api/transactions` — CRUD, pagination, filter by description / amount range / category / date range.

**OpenAPI:** springdoc (`springdoc-openapi-starter-webmvc-ui`); Swagger UI on the backend when running.

**Stack:** Java 22, Spring Boot 4.0.6, Spring Data JPA, HSQLDB (in-memory dev), PostgreSQL (prod), JUnit 5, Mockito, MockMvc.

**Tests:** `FazApplicationTests`, `TransactionServiceTest`, `TransactionControllerTest`, `TransactionIntegrationTest`.

### Frontend (`frontend/`)

| Area | Path | Role |
|------|------|------|
| Entry | `src/main.tsx`, `App.tsx` | Single-page shell |
| Page | `pages/TransactionsPage.tsx` | List, filter, create |
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
