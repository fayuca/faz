# faz — frontend

React UI for the personal expense tracker. Canonical design: [`../docs/DESIGN.md`](../docs/DESIGN.md).

## Run

Requires the backend on port 8080 (`../backend`).

```bash
npm install
npm run dev
```

Vite proxies `/api` → `http://localhost:8080`.

## Scripts

| Command | Purpose |
|---------|---------|
| `npm run dev` | Dev server with HMR |
| `npm run build` | Production build |
| `npm run preview` | Preview production build |
| `npm run lint` | ESLint |
