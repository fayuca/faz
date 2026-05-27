# faz

Personal expense tracker — CV demo. Full-stack vertical slice: Spring Boot API + React UI.

## Run with Docker Compose

Requires Docker Desktop (or Docker Engine + Compose).

```bash
docker compose up --build
```

Open http://localhost:8080 — nginx serves the React app and proxies `/api` to Spring Boot; Postgres persists data in a Docker volume.

## Run locally (development)

**Backend** (port 8080):

```bash
cd backend
./mvnw spring-boot:run    # Windows: mvnw.cmd spring-boot:run
```

**Frontend** (Vite dev server, proxies `/api` to backend):

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
- Validation and error handling
- Unit, controller, and integration tests

## Stack

- Java 22, Spring Boot 4, Spring Data JPA
- React 19, Vite 8, TypeScript
- HSQLDB (local dev) · PostgreSQL (Compose deploy)

Design and session docs: [`docs/DESIGN.md`](docs/DESIGN.md)

## License

MIT License
