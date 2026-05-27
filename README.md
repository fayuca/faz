# faz

> Portfolio demo — personal side project, not a production service.

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

**Tests:**

```bash
cd backend
./mvnw test
```

## Features

- Create, update, delete, list, and filter transactions
- Categories for personal spending (food, transport, etc.)
- Validation and structured API errors
- Backend tests (unit, controller, integration)

## Stack

- Java 22 · Spring Boot 4 · Spring Data JPA · PostgreSQL / HSQLDB
- React 19 · Vite 8 · TypeScript

See [`docs/DESIGN.md`](docs/DESIGN.md) for architecture and API detail.

## License

MIT License
