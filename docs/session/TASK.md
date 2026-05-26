# Task — initial cleanup



> Scope: [OVERVIEW.md](OVERVIEW.md) · [CHECKLIST.md](CHECKLIST.md) · [DESIGN.md](../DESIGN.md) · [HANDOFF](../HANDOFF.md)



## What this task is



First dev task after conceptual alignment. Fix known small bugs and obvious debt while the codebase is still small. See DESIGN **Known debt**.



**Current step:** complete — wrap or next task



---



## Steps



### 1 · Bug fixes



- ✅ `TransactionService.update()` — persist `category` from request

- ✅ Remove stray `console.log("abc")` in `useTransactions.ts`



### 2 · Date field decision



- ✅ Wire `Transaction.date` through DTOs, create/update, validation, specs, UI



### 3 · Docs hygiene



- ✅ Replace `frontend/README.md` — brief faz pointer + run commands



### 4 · Verify



- ✅ `./mvnw test` passes

- ✅ Smoke: create, list, filter via API (2026-05-26)



---



## Done



| Milestone | Status |

|-----------|--------|

| Clone + audit | ✅ (2026-05-26) |

| Reconcile layout | ✅ (2026-05-26) |

| Conceptual alignment → DESIGN.md | ✅ (2026-05-26) |

| Initial analysis (layout + run cmds) | ✅ (landed in OVERVIEW during alignment) |


