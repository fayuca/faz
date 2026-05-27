# LESSON — faz deployment & Docker Compose

> Didactic reference while learning with the agent. Updated in place (not a chronological log). Canonical design stays in [`DESIGN.md`](../DESIGN.md).

---

## 1. Mental model: app-server → containers

| Day job (corporate banking) | faz today | faz with Docker Compose |
|-----------------------------|-----------|-------------------------|
| EAR/WAR → JBoss/Tomcat | `mvnw spring-boot:run` | `docker compose up` |
| App server owns the JVM | Spring Boot **is** the runtime (embedded Tomcat) | Same executable JAR, inside a container |
| DB on another host / JNDI | HSQLDB in-memory in the same JVM | **Postgres** in its own container |
| Static assets sometimes in WAR | React via **Vite dev server** | `npm run build` → static files served by **nginx** |

**Container** — lightweight isolated environment running one main process (plus its filesystem and network).

**Image** — recipe to build a container (artifact + runtime, like a packaged deployable before you “drop” it).

**Docker Compose** — one YAML file declaring several services (containers), how they connect, ports, env vars, volumes, and startup order. Replaces “copy three things to the server and restart three services” with `docker compose up --build`.

Old habit: copy artifact → restart app server.  
New habit: `docker compose up --build` → Compose builds images if needed, starts **db → backend → frontend** on a shared Docker network.

---

## 2. faz today (local dev)

```
Browser → Vite (dev) ──proxy /api──► Spring Boot :8080 ──► HSQLDB (in-memory)
```

- **HSQLDB in-memory** (`application.properties`) — data is lost when the JVM stops. Good for dev and tests; **not** for deploy.
- **Vite proxy** (`frontend/vite.config.ts`) — forwards `/api` to `localhost:8080`. Exists **only in dev**; production has no Vite.

Local run (unchanged after Compose work): backend `mvnw spring-boot:run`, frontend `npm run dev`, tests `mvnw test`.

---

## 3. faz target (MVP deploy with Compose)

Two problems to solve for deploy:

1. **Persistent database** — replace in-memory HSQLDB with PostgreSQL.
2. **Single public entry** — browser must reach UI and `/api` without Vite; typically **nginx** serves static React and proxies API to Spring Boot (**single origin**, avoids CORS setup).

---

## 4. The three services

```
                    ┌─────────────────────────────────────┐
  Browser :HOST  ──►│  frontend (nginx)                   │
                    │  • serves React static (dist/)      │
                    │  • proxies /api → backend:8080    │
                    └──────────────┬──────────────────────┘
                                   │  Docker internal network
                    ┌──────────────▼──────────────────────┐
                    │  backend (Spring Boot JAR)          │
                    │  • profile prod → Postgres          │
                    │  • :8080 (internal; not always      │
                    │    published to host)               │
                    └──────────────┬──────────────────────┘
                                   │  jdbc:postgresql://db:5432/...
                    ┌──────────────▼──────────────────────┐
                    │  db (Postgres)                      │
                    │  • named volume → data survives     │
                    │    container restart                │
                    └─────────────────────────────────────┘
```

On a Docker network, the **service name** in `docker-compose.yml` (e.g. `db`, `backend`) is a **hostname** other containers use (`db:5432`, `http://backend:8080`).

---

## 5. Why nginx replaces the Vite proxy

| Environment | Who serves the UI | Who handles `/api` |
|-------------|-------------------|---------------------|
| Dev | Vite | Vite proxy → `localhost:8080` |
| Deploy | nginx (static `dist/`) | nginx `proxy_pass` → `backend:8080` |

The React app uses **relative** API URLs (`baseURL: "/api"` in `frontend/src/api/http.ts`) — e.g. `GET /api/transactions`. That works in dev because Vite forwards `/api` to Spring Boot. In deploy, **nginx** must do the same forwarding.

Deep-dive: request flow, full `nginx.conf`, and pitfalls — **§15**.

---

## 6. Files to add (repo has none yet)

| File | Role |
|------|------|
| `docker-compose.yml` (repo root) | Wires `db`, `backend`, `frontend`; ports, env, volumes, `depends_on` |
| `backend/Dockerfile` | Multi-stage: Maven → JAR → slim JRE image |
| `frontend/Dockerfile` | Multi-stage: `npm run build` → nginx with static files |
| `frontend/nginx.conf` | `location /api/` → `backend:8080`; SPA `try_files` |
| `backend/.../application-prod.properties` | Postgres defaults (overridable via env) |
| `pom.xml` | PostgreSQL driver added; HSQLDB kept for dev/tests |

---

## 7. Spring profiles and Postgres

**Split by profile** (keep local workflow intact):

| Profile | When | Database |
|---------|------|----------|
| default (no profile) | `mvnw spring-boot:run`, `mvnw test` | HSQLDB — current `application.properties` |
| `prod` | Docker Compose sets `SPRING_PROFILES_ACTIVE=prod` | PostgreSQL at hostname `db` |

Deep-dive mechanics, `pom.xml`, and property files: **§14**.

At a glance: add PostgreSQL driver to `pom.xml`; add `application-prod.properties`; Compose activates `prod` and passes datasource env vars. Dev and tests stay on HSQLDB unchanged.

---

## 8. `docker-compose.yml` essentials (conceptual)

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: faz
      POSTGRES_USER: faz
      POSTGRES_PASSWORD: faz
    volumes:
      - faz-data:/var/lib/postgresql/data

  backend:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/faz
    depends_on:
      - db

  frontend:
    build: ./frontend
    ports:
      - "8080:80"   # host:container — pick what you expose
    depends_on:
      - backend

volumes:
  faz-data:
```

`depends_on` expresses start order; for production hardening you may later add healthchecks so the backend waits until Postgres accepts connections.

---

## 9. Build and run flow

1. **Postgres** starts; volume `faz-data` persists data across `docker compose down` / `up`.
2. **Backend image** — build stage runs `mvn package`; run stage copies JAR into JRE (e.g. Temurin 22), `java -jar`.
3. **Frontend image** — build stage `npm ci && npm run build`; run stage copies `dist/` into nginx image + config.
4. User opens `http://localhost:<mapped-port>` — nginx serves SPA and proxies `/api`.

Command: `docker compose up --build` from repo root (after files exist).

---

## 10. Implementation order (when building)

1. Postgres driver + `application-prod.properties`.
2. Backend Dockerfile; smoke-test JAR against Postgres (optional: db container alone first).
3. Frontend Dockerfile + `nginx.conf`.
4. Root `docker-compose.yml`.
5. README / OVERVIEW: document `docker compose up --build`.

---

## 11. Curriculum: DESIGN learning sequence

From [`DESIGN.md`](../DESIGN.md) deployment section — **whole-project** path (not only Docker):

| Step | Topic | Status (faz) |
|------|--------|----------------|
| 1 | Local run docs | ✅ OVERVIEW + DESIGN |
| 2 | **Docker Compose** (backend + frontend + Postgres) | ✅ local smoke 2026-05-26 |
| 3 | Pick **one** free PaaS; deploy; shareable URL | ⬜ **current focus** |
| 4 | Post-MVP polish | ⬜ |

**Deployment families** (simplest → more cloud-native), same doc:

1. **JAR on a VM** — closest to “drop and restart”; you manage OS, Java, nginx, SSL.
2. **Docker Compose** — recommended **first learning step** for side projects; same compose locally and on a cheap VPS.
3. **PaaS** (Render, Railway, Fly.io, …) — GitHub connect; less ops; learn env vars and health checks.

Vendor for public URL: **TBD** until after step 2.

---

## 12. Deep-dive branches (pick with agent)

| Id | Topic | Status |
|----|--------|--------|
| A | Multi-stage **Dockerfiles** (backend or frontend) | covered — §13 |
| B | Spring **profiles** + Postgres + `pom.xml` | covered — §14 |
| C | **nginx** proxy vs Vite | covered — §15 |
| D | **Implement** — seed `TASK.md`, execute §10 | ✅ Compose done 2026-05-26 |

---

## 13. Multi-stage Dockerfiles (deep-dive A)

### Why multi-stage?

A **single-stage** Dockerfile could install Maven, compile, and run the JAR in one image. That works, but the final image would still contain Maven, all of `src/`, and the `.m2` cache — hundreds of MB you never need at runtime.

**Multi-stage** = two (or more) `FROM` lines in one Dockerfile:

1. **Build stage** — full toolchain (JDK, Maven / Node) produces the artifact.
2. **Run stage** — slim runtime (JRE, nginx) receives **only** the artifact via `COPY --from=...`.

What ships to deploy resembles your old world: one JAR or one static bundle — not the whole build farm.

```
┌──────────────── build stage ────────────────┐
│  JDK 22 + Maven  →  target/faz-*.jar        │
│  (or Node + npm   →  dist/)                 │
└──────────────────────┬──────────────────────┘
                       │ COPY --from=build (artifact only)
┌──────────────────────▼──────────────────────┐
│  run stage: JRE 22  OR  nginx:alpine        │
│  small image, fast pull, smaller attack     │
│  surface                                    │
└─────────────────────────────────────────────┘
```

### Dockerfile vocabulary (used in both services)

| Instruction | Role |
|-------------|------|
| `FROM image AS build` | Start a named stage (`build` is conventional) |
| `WORKDIR /app` | Default directory for later `RUN` / `COPY` |
| `COPY src ./src` | Send host files into the **build context** |
| `RUN ...` | Execute during **image build** (not each container start) |
| `COPY --from=build /path/artifact ...` | Pull **only** that path from an earlier stage |
| `EXPOSE 8080` | Documents which port the process uses (Compose still maps ports) |
| `ENTRYPOINT ["java", "-jar", "app.jar"]` | Main process when the container starts |

**Build context** — the folder Docker sends to the daemon (`build: ./backend` in Compose → context is `backend/`). A **`.dockerignore`** (like `.gitignore`) keeps `target/`, `node_modules/`, etc. out of the context so builds stay fast.

**Layer caching** — each instruction creates a layer. If `pom.xml` / `package-lock.json` change rarely, copy them **before** `src/` and run dependency download first; source edits then reuse cached dependency layers.

### Backend Dockerfile (faz-specific sketch)

Artifact: Spring Boot fat JAR from `spring-boot-maven-plugin` → `target/faz-0.0.1-SNAPSHOT.jar`.

```dockerfile
# --- build stage ---
FROM eclipse-temurin:22-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -B dependency:go-offline -DskipTests

COPY src src
RUN ./mvnw -B package -DskipTests

# --- run stage ---
FROM eclipse-temurin:22-jre
WORKDIR /app

COPY --from=build /app/target/faz-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Notes:

- **JDK in build, JRE in run** — compile needs `javac`; runtime only needs the JVM.
- **`dependency:go-offline`** — optional cache-friendly step; `-B` = batch (non-interactive Maven).
- **`-DskipTests`** — tests still run in CI / locally; image build stays fast. Use `-Dmaven.test.skip=true` if you also want to skip compilation of tests.
- **Wildcard JAR name** — version bumps do not require editing the Dockerfile.
- **Windows:** ensure `mvnw` has Unix line endings inside the image (Git `autocrlf` can bite); if `RUN ./mvnw` fails with “not found”, check that first.

Local smoke (before Compose): from `backend/`, `docker build -t faz-backend .` then run with env pointing at Postgres.

### Frontend Dockerfile (faz-specific sketch)

Artifact: Vite output in `dist/` (HTML, JS, CSS — no Node at runtime).

```dockerfile
# --- build stage ---
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# --- run stage ---
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
```

Notes:

- **`npm ci`** — reproducible install from lockfile (prefer over `npm install` in Docker).
- **nginx default doc root** — `/usr/share/nginx/html`; `index.html` + assets land there.
- **`nginx.conf`** is copied in the **run** stage (small config file, not part of Node build).
- React **API calls stay relative** (`/api/...`); nginx config (deep-dive C) handles routing.

Local smoke: from `frontend/`, `docker build -t faz-frontend .` then `docker run -p 8080:80 faz-frontend` — UI loads; `/api` will fail until backend + proxy config exist.

### Single-stage anti-pattern (why we avoid it)

```dockerfile
FROM eclipse-temurin:22-jdk
COPY . .
RUN ./mvnw package -DskipTests
ENTRYPOINT ["java", "-jar", "target/faz-0.0.1-SNAPSHOT.jar"]
```

Runs, but the running container still carries JDK, Maven wrapper, and sources — larger image, more to patch, not how you would ship to production.

### How Compose uses these

```yaml
backend:
  build: ./backend    # reads backend/Dockerfile; context = backend/

frontend:
  build: ./frontend
```

`docker compose up --build` builds both images, then starts containers from the **run** stages only.

### Suggested `.dockerignore` snippets

**`backend/.dockerignore`:** `target/`, `.idea/`, `*.iml`

**`frontend/.dockerignore`:** `node_modules/`, `dist/`

---

## 14. Spring profiles & Postgres (deep-dive B)

### Bridge from Java EE

| Java EE habit | Spring Boot (faz) |
|---------------|-------------------|
| JNDI lookup `java:comp/env/jdbc/...` | `spring.datasource.url` (+ user/password) |
| App-server connection pool | **HikariCP** — auto-configured by Spring Boot |
| `persistence.xml` / container-managed JPA | `@Entity` scan + `spring.jpa.*` in properties |
| Different DS per environment (dev vs prod XML) | **Profiles** — `application.properties` + `application-{profile}.properties` |

You still configure *a* datasource; Spring Boot binds it without JNDI unless you choose JNDI explicitly (not needed here).

### What is a Spring profile?

A **named slice of configuration**. When active, Spring loads:

1. `application.properties` (always — **shared defaults**)
2. `application-{profile}.properties` (overrides/additions for that profile)

Activate with:

- Env var: `SPRING_PROFILES_ACTIVE=prod`
- Or CLI: `java -jar app.jar --spring.profiles.active=prod`

**faz rule:** default profile = HSQLDB for local dev and tests. **`prod` profile only in Docker** (or when you explicitly test against Postgres).

### Current default config (unchanged for dev)

`backend/src/main/resources/application.properties` today:

```properties
spring.datasource.url=jdbc:hsqldb:mem:faz
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Leave this file as-is so `mvnw spring-boot:run` and `mvnw test` keep working with zero Postgres setup.

### `pom.xml` — add PostgreSQL driver

Both drivers can sit on the classpath; **which DB you hit is determined by the JDBC URL**, not by removing HSQLDB.

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Keep existing HSQLDB dependency (`runtime` scope) for dev/tests.

Spring Boot 4 picks the PostgreSQL driver automatically when the URL starts with `jdbc:postgresql:` — you usually **do not** need `spring.datasource.driver-class-name` for Postgres.

### New file: `application-prod.properties`

Path: `backend/src/main/resources/application-prod.properties`

```properties
spring.datasource.url=jdbc:postgresql://db:5432/faz
spring.datasource.username=faz
spring.datasource.password=faz
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

Notes:

- Hostname **`db`** = Postgres **service name** in `docker-compose.yml` (Docker DNS on the internal network).
- **`driver-class-name` required in prod** — default `application.properties` sets the HSQLDB driver; profile merge does not remove it unless prod overrides. Without this, Hibernate fails at startup in Compose.
- **`ddl-auto=update`** — Hibernate creates/updates tables on startup. Fine for MVP demo; use Flyway/Liquibase later if the project grows.
- **`show-sql=false`** — avoid noisy SQL logs in deploy (dev keeps `true` in default properties).

For a CV demo, matching Compose Postgres credentials here is acceptable. Real production would inject secrets via env only (see below).

### Environment variables override properties

Spring Boot **relaxed binding** — env vars override file values without rebuilding the JAR:

| Property | Environment variable |
|----------|------------------------|
| `spring.profiles.active` | `SPRING_PROFILES_ACTIVE` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` |

Typical Compose snippet for `backend`:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod
  SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/faz
  SPRING_DATASOURCE_USERNAME: faz
  SPRING_DATASOURCE_PASSWORD: faz
```

Order of precedence (highest wins): env vars → profile-specific properties → `application.properties`.

You can ship sensible defaults in `application-prod.properties` and override only secrets on a PaaS later.

### How Postgres fits the Compose stack

```
Postgres container          Spring Boot (prod profile)
POSTGRES_DB=faz        ←→   jdbc:postgresql://db:5432/faz
POSTGRES_USER=faz      ←→   username faz
POSTGRES_PASSWORD=faz  ←→   password faz
```

Data persists in a **named volume** (`faz-data`); restarting the `db` container does not wipe the database file on disk.

### Tests stay on HSQLDB

`src/test` has no separate `application-test.properties` today — tests load the **default** profile and in-memory HSQLDB. Adding `prod` + Postgres does **not** require Postgres for `mvnw test` unless you later add integration tests with `@ActiveProfiles("prod")` or Testcontainers.

### Hibernate dialect

Spring Boot auto-detects dialect from the JDBC URL. No need to set `spring.jpa.database-platform` for PostgreSQL in MVP.

### Checklist (when implementing §10 step 1)

1. Add `postgresql` dependency to `pom.xml`.
2. Create `application-prod.properties` as above.
3. Do **not** change default `application.properties` to Postgres.
4. Wire `SPRING_PROFILES_ACTIVE=prod` + datasource env in Compose.
5. Run `mvnw test` locally — should still pass on HSQLDB.
6. Smoke: `docker compose up` — backend connects to `db`, tables appear, API responds.

---

## 15. nginx proxy vs Vite (deep-dive C)

### Bridge from Java EE

Apache or nginx in front of JBoss/Tomcat is the same **reverse proxy** role: browser talks to one host/port; the proxy forwards API traffic to the app server and serves static files itself. faz deploy replaces Vite with nginx in that front slot.

### What the browser sees (dev vs deploy)

**Dev** — two processes, one origin *from the browser’s view* thanks to Vite:

```
Browser  http://localhost:5173/
         http://localhost:5173/api/transactions
              │
              ▼
         Vite dev server
              ├── /*.js, index.html     → Vite (React HMR)
              └── /api/*                → proxy → http://localhost:8080/api/*
```

**Deploy** — one container is the public face:

```
Browser  http://localhost:8080/
         http://localhost:8080/api/transactions
              │
              ▼
         nginx (:80 inside container, mapped to host)
              ├── /api/*                → proxy → http://backend:8080/api/*
              └── everything else         → static files from /usr/share/nginx/html (dist/)
```

Spring Boot stays on the Docker network as `backend:8080`; it does not need a published host port for MVP (only nginx is exposed).

### Why faz needs no frontend code changes

`frontend/src/api/http.ts`:

```typescript
export const http = axios.create({
    baseURL: "/api",
});
```

Calls like `http.get("/transactions")` become **`/api/transactions`** in the browser — same path the Spring controller uses (`@RequestMapping("/api/transactions")`). Dev proxy and nginx must both preserve the **`/api` prefix** when forwarding.

### Vite proxy (dev only) — line by line

From `frontend/vite.config.ts`:

```typescript
proxy: {
    "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
    },
},
```

| Setting | Meaning |
|---------|---------|
| `"/api"` | If request path starts with `/api`, proxy it |
| `target` | Forward to Spring Boot on the host |
| `changeOrigin` | Host header matches target (helps some backends) |
| `secure: false` | Allow HTTP target (local dev) |

This block is **never used in production** — it exists only while `npm run dev` runs. Built `dist/` has no proxy.

### nginx config for faz (sketch)

File: `frontend/nginx.conf` (copied into the image in the Dockerfile run stage).

```nginx
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    # API → Spring Boot (Compose service name)
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Static assets from Vite build
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

**`location /api/`** — requests starting with `/api/` go to Spring Boot.

**`proxy_pass http://backend:8080`** — no trailing slash on the URL. nginx forwards the **full** URI (`/api/transactions` → `http://backend:8080/api/transactions`). That matches Vite’s behavior and faz’s controller paths.

### Trailing-slash trap (common bug)

| `proxy_pass` | Request | Upstream receives |
|--------------|---------|---------------------|
| `http://backend:8080` | `/api/transactions` | `/api/transactions` ✅ |
| `http://backend:8080/` | `/api/transactions` | `/transactions` ❌ (404) |

Use the form **without** trailing slash unless you intentionally want to strip the matched prefix.

### SPA fallback: `try_files`

faz is a single page today (`App.tsx` → `TransactionsPage`). If you add client-side routes later (React Router), direct hits to `/some/route` must return `index.html`, not nginx 404. `try_files $uri $uri/ /index.html` serves the file if it exists, otherwise falls back to the SPA shell.

### Single origin vs CORS (why proxy wins for MVP)

**Alternative:** frontend on `https://app.example.com`, API on `https://api.example.com` — browser **cross-origin** rules apply; Spring must send CORS headers and you configure axios with an absolute API URL.

**faz approach:** UI and `/api` share one origin (nginx). Browser sees one host; no CORS setup in Spring for MVP. Same pattern as Vite dev proxy.

### What nginx does *not* expose (unless you add it)

Springdoc Swagger UI lives on the backend (`/swagger-ui.html` or similar). With only `/api/` proxied, Swagger is **not** reachable through the frontend URL unless you add another `location` or expose backend port for debugging. Fine for MVP demo; optional later.

### Wiring in the frontend Dockerfile

```dockerfile
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
```

The default nginx image includes `conf.d/*.conf` inside the `http` block — one `server { }` in `default.conf` is enough.

### Quick verification (after implement)

1. Open `http://localhost:<mapped-port>/` — React UI loads (static JS/CSS).
2. Create/list transactions — network tab shows `GET/POST` to `/api/transactions` on the **same host**, status 200.
3. `docker compose ps` — backend port not required on host if only frontend publishes `8080:80`.

---

## 16. Public deploy — PaaS (DESIGN step 3)

### What changes from Compose

| Compose (local) | PaaS (public) |
|-----------------|---------------|
| You run `docker compose up` | Platform builds from GitHub and runs containers |
| Service name `db` | Managed Postgres **host URL** from provider dashboard |
| `localhost:8080` | **HTTPS URL** to share in CV/README |
| Secrets in `docker-compose.yml` | **Env vars** in provider UI (or `render.yaml` / etc.) |

**Reuse:** same `backend/Dockerfile`, `frontend/Dockerfile`, `nginx.conf`, `application-prod.properties`. Only connection strings and public routing change.

### Typical faz layout on a PaaS

```
Internet → https://faz-xxxx.onrender.com (frontend / nginx)
              └── /api/* → internal backend URL
           backend service (Spring Boot, not always public)
           managed PostgreSQL
```

nginx keeps **single origin** — same pattern as local Compose. Backend may get a private `.internal` hostname (Render) or public subdomain (Railway); nginx `proxy_pass` target becomes an **env var** at deploy time if the hostname is not `backend`.

### What you learn here (vs Compose)

- Connect GitHub repo → auto-deploy on push
- Set env vars in a UI (no secrets in git)
- Free-tier limits (sleep on idle, DB size, build minutes)
- HTTPS and DNS handled by platform

### VPS alternative (not PaaS)

Same `docker-compose.yml` on a cheap Linux VPS — closest to “drop and restart” with what you already built. You manage SSH, firewall, and optional domain. More ops; no platform magic.

### Render (chosen host)

**Blueprint:** root `render.yaml` — Postgres + `faz-backend` + `faz-frontend` (all Docker, free plan).

| Piece | Render service | Notes |
|-------|----------------|-------|
| Postgres | `faz-db` | `DB_*` env wired via `fromDatabase` |
| Spring Boot | `faz-backend` | `server.port=${PORT}`; health `/api/transactions` |
| nginx + React | `faz-frontend` | `BACKEND_HOSTPORT` from `fromService` (private network); public CV URL |

**Local vs Render:** `application-prod.properties` uses `${DB_HOST:db}` defaults (Compose) or Render-injected `DB_*`. Frontend `nginx.conf.template` + entrypoint: `${PORT}` and `${BACKEND_HOSTPORT}` (default `backend:8080` for Compose).

**Apply:** push to GitHub → Render Dashboard → New → Blueprint → connect repo.

**Free-tier caveats:** idle spin-down; Postgres free DB 90-day limit — see Render docs.

---

## Meta — LESSON.md procedure (for double-click / STUDY MODE)

**Purpose:** Capture **teaching** from agent sessions in a **didactic** file the user can re-read while learning, separate from chronological minutes and volatile `TASK.md`.

**How the agent maintains it**

- **Create** when the user asks to persist teaching (or agent discretion when a teach block is substantial).
- **Structure:** sections by concept (mental model → current state → target → mechanics → curriculum), not by chat date.
- **Integrate:** new explanations merge into the right section; extend tables/diagrams; mark deep-dive rows in §12 when covered.
- **Do not duplicate:** canonical product/architecture decisions stay in `DESIGN.md`; LESSON is the learner’s guide and bridges to DESIGN.
- **Volatile work** stays in `TASK.md` / `CHECKLIST.md`; LESSON does not replace task steps.

**Suggested STUDY MODE use case (for manager)**

- User runs boot (or study command) → agent reads `LESSON.md` + `TASK.md` + DESIGN deployment slice.
- Mode = **explain and quiz**, not implement, unless user opts into branch D.
- Agent appends/updates LESSON after each teach segment; user reviews offline.
- Exit: user chooses deep-dive (§12) or implementation; wrap can note LESSON sections touched.

**This repo:** `docs/session/LESSON.md` — add to session file table in OVERVIEW when promoted from experiment.

---

*Last integrated: 2026-05-27 — §16 PaaS intro; Compose complete.*
