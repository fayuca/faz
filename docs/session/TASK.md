# Task — public deploy (Render)

> DESIGN step 3. **Step** = next ⬜ only.

## Steps

- ✅ Pick host → **Render**
- ⬜ Push `render.yaml` + Render-ready config to `fayuca/faz` on GitHub
- ⬜ Render Dashboard → **New** → **Blueprint** → connect repo → apply
- ⬜ Wait for deploy (db → backend → frontend)
- ⬜ Smoke: live frontend URL — create/list transaction
- ⬜ README demo link

## Render apply (after push)

1. [render.com](https://render.com) → sign in with GitHub
2. **New** → **Blueprint**
3. Select **fayuca/faz** → Apply
4. Public URL = **faz-frontend** service (https://faz-frontend-xxxx.onrender.com)

## Notes

- Free web services **spin down** after ~15 min idle; first request may take ~30s.
- Free Postgres expires after 90 days (renew or upgrade for long-lived demo).
