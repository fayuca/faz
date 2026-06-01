# faz

> Portfolio demo

Personal expense tracker: record spending, filter by description, browse by category.

**Live app:** https://faz-frontend.onrender.com

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

- Meta shell with **budget planner** and **API explorer** apps
- Create, update, delete, list, and filter transactions (v1 and v2 API)
- Validation and structured API errors
- Backend tests (unit, controller, integration)
- Categories for personal spending (food, transport, etc.)

## Stack

- Java 22 · Spring Boot 4 · Spring Data JPA · PostgreSQL / HSQLDB
- React 19 · Vite 8 · TypeScript

See [`docs/DESIGN.md`](docs/DESIGN.md) for architecture and API detail.

## License

Source code and documentation (including `docs/`): [MIT](LICENSE).
