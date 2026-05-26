# Handoff

> Last updated: 2026-05-26 (conceptual alignment). Read at next boot; rewritten each wrap.

## Where we are

**Phase:** Personal expense tracker CV demo — Spring Boot + React ([fayuca/faz](https://github.com/fayuca/faz)).

**Alignments:** Technical ✅ · Conceptual ✅ — [`docs/DESIGN.md`](DESIGN.md) landed 2026-05-26.

**Current work:** Initial cleanup complete — ready for wrap or next task ([`docs/session/TASK.md`](session/TASK.md)).

## Continue here

- [`docs/DESIGN.md`](DESIGN.md) — canonical design (product, MVP, architecture, deploy primer)
- [`docs/session/TASK.md`](session/TASK.md) — initial cleanup
- [`docs/session/OVERVIEW.md`](session/OVERVIEW.md)

## Path convention

**/** = cursor workspace root (`c:/work/cursor/`). From `/faz/`, `/authority/` → `../authority/`.

Bare paths (e.g. `docs/…`) = open workspace.

## Boot training

Read this section at **every boot** before other work. HANDOFF = **project-aware** training; generic rules load **only when this section says so**.

**Profile (MVP):** Read the **full** file:

`/authority/facts/profile/PROFILE.md`

**Generic wiring (on demand):**

- `/authority/rules/boot-wrap.mdc` (installed catalog)
- `/authority/rules/profile-read.mdc` (installed catalog)
- DEV fallbacks: `/double-click/stubs/rules/boot-wrap.stub`, `profile-read.stub`

**Boot command spec:** `/authority/commands/boot-up.md` (installed); DEV: `/double-click/stubs/commands/boot-up.stub`

**Align:** Non-empty `docs/session/DRAFT.md` → align per `/double-click/stubs/align.stub`.

**Context:** Long session or heavy chat → prefer **wrap** then **new chat + boot**; minutes + this HANDOFF carry state.

## Wrap training

**Wrap command spec:** `/authority/commands/wrap-up.md` (installed); DEV: `/double-click/stubs/commands/wrap-up.stub`

**Minutes:** append to `docs/minutes/YYYY-MM-DD.md` per `/double-click/stubs/minutes.stub`; rewrite this HANDOFF (keep boot/wrap training).

**Volatile files:** clear `DRAFT` when resolved; clear `TASK` when slice task completes; keep `OVERVIEW` + `CHECKLIST` while project scope is open.

## Session files (this repo)

| File | Path |
|------|------|
| Design | `docs/DESIGN.md` |
| Handoff | `docs/HANDOFF.md` |
| Overview | `docs/session/OVERVIEW.md` |
| Checklist | `docs/session/CHECKLIST.md` |
| Task | `docs/session/TASK.md` |
| Draft | `docs/session/DRAFT.md` |
| Minutes | `docs/minutes/YYYY-MM-DD.md` |

## Authority paths

| Item | Path |
|------|------|
| Profile | `/authority/facts/profile/PROFILE.md` |
| Commands | `/authority/commands/` |
| Rules | `/authority/rules/` |

## Decisions this session (summary)

- Product: personal expense tracker (not business ledger).
- MVP: vertical slice minimum (deploy + create/list/filter + run story).
- Deploy host TBD; primer saved in DESIGN.
- First dev task: initial cleanup.
- Initial cleanup complete 2026-05-26 (bugs, date field, frontend README).
- Budgeting / double-entry: post-MVP backlog.

## Open questions

- **Deploy vendor:** pick after Docker Compose learning step (DESIGN deployment section)

## Profile updates
