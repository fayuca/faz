# Overview — faz

> Personal expense tracker — CV demo. Scope here; canonical design in [`DESIGN.md`](../DESIGN.md); continuity in HANDOFF.

## Goal

Full-stack vertical slice (DB → Spring Boot → React) centered on **Transaction**. Shareable deploy + create/list/filter for interviews. See **[DESIGN.md](../DESIGN.md)** for product identity, MVP, and deployment notes.

## Stack

| Layer | Path | Tech |
|-------|------|------|
| Backend | `backend/` | Java 22, Spring Boot 4, Spring Data JPA, HSQLDB |
| Frontend | `frontend/` | React 19, Vite 8, TypeScript, axios |

## Run locally

**Backend** (port 8080): `cd backend` → `./mvnw spring-boot:run` (Windows: `mvnw.cmd`)

**Frontend:** `cd frontend` → `npm install` → `npm run dev` (proxies `/api` to backend)

**Tests:** `cd backend` → `./mvnw test`

**Docker Compose** (Postgres + backend + nginx frontend):

```bash
docker compose up --build
```

Open http://localhost:8080 — UI and `/api` share one origin via nginx.

## Current task

**MVP deploy** — next milestone; TASK cleared at wrap. Next boot seeds Docker Compose work per **[DESIGN.md](../DESIGN.md)** deployment section.

## Scope boundary

**In scope:** Personal expense tracker; transaction API + UI; local dev; MVP deploy (host TBD); double-click session workflow.

**Out of scope:** Auth, multi-user, business ledger, budgeting, double-entry — unless explicitly promoted from DESIGN backlog.

## Session files

| File | Role |
|------|------|
| **DESIGN** | Canonical project design |
| **OVERVIEW** | Project scope (this file) |
| **CHECKLIST** | Progress tracker |
| **TASK** | Current task steps |
| **DRAFT** | Pre-session redrafts; align input |

## Reference

Root [`README.md`](../../README.md) · [`DESIGN.md`](../DESIGN.md) · [`CONCEPT.md`](CONCEPT.md)
