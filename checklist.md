# ClinicPro Implementation Checklist

## Foundation

- [x] Create `ClinicPro/` root folder
- [x] Create `backend/` and `frontend/`
- [x] Initialize git and set remote to `https://github.com/MahdiGH10/ClinicalPRO.git`
- [x] Scaffold backend baseline
- [x] Scaffold frontend baseline
- [x] Add root documentation

## Backend (contract-first)

- [ ] Create clean package structure (`config`, `security`, `entity`, `repository`, `service`, `controller`, `dto`, `exception`)
- [ ] Implement auth model (`User`, roles)
- [ ] Implement JWT auth flow (`AuthController`, `JwtUtils`, `JwtAuthFilter`, `SecurityConfig`)
- [ ] Implement Patients vertical slice + tests
- [ ] Implement Medecins vertical slice + tests
- [ ] Implement RendezVous vertical slice + tests
- [ ] Implement Consultations vertical slice + tests
- [ ] Implement Factures vertical slice + tests
- [ ] Implement Notifications vertical slice + tests
- [ ] Freeze OpenAPI contract (`/swagger-ui.html`)

## Frontend (Angular clean architecture)

- [ ] Implement `core/shared/features/layout` folder architecture
- [ ] Implement auth flow (login, guard, interceptor, 401 handling)
- [ ] Add API models/services by feature
- [ ] Build dashboard feature against real API
- [ ] Build patients feature against real API
- [ ] Build medecins feature against real API
- [ ] Build rendez-vous feature against real API
- [ ] Build consultations feature against real API
- [ ] Build factures feature against real API
- [ ] Add notifications and detail pages

## UI System Migration

- [ ] Create design tokens in Angular styles (colors, spacing, typography)
- [ ] Build reusable UI primitives (status badge, stat card, table, filter bar)
- [ ] Build final layout shell (sidebar + header)
- [ ] Apply responsive UX polish

## Quality & Delivery

- [ ] Add backend CI checks (build + tests)
- [ ] Add frontend CI checks (lint + build + tests)
- [ ] Validate end-to-end flow on PostgreSQL
- [ ] Finalize docs (`docs/architecture.md`, `docs/runbook.md`)
- [ ] Tag initial stable milestone

## Commit plan (suggested)

- [ ] `chore(repo): initialize ClinicPro monorepo skeleton`
- [ ] `chore(backend): scaffold spring boot base + profiles`
- [ ] `feat(backend-auth): jwt + users + roles + security config`
- [ ] `feat(backend-patients): complete vertical slice + tests`
- [ ] `feat(backend-medecins): complete vertical slice + tests`
- [ ] `feat(backend-rendezvous): constraints/status flow + tests`
- [ ] `feat(backend-consultations): complete slice + tests`
- [ ] `feat(backend-factures): billing flow + tests`
- [ ] `feat(backend-notifications): complete slice + scheduler tests`
- [ ] `docs(api): openapi contract freeze v1`
- [ ] `chore(frontend): clean angular architecture shell`
- [ ] `feat(frontend-auth): login/guard/interceptor/error flow`
- [ ] `feat(frontend-design-system): tokens + shared layout/components`
- [ ] `feat(frontend-dashboard): kpi + appointments view`
- [ ] `feat(frontend-patients): list/form/detail against api`
- [ ] `feat(frontend-medecins): list/form/detail against api`
- [ ] `feat(frontend-rendezvous): scheduling/status against api`
- [ ] `feat(frontend-consultations): consultation flow against api`
- [ ] `feat(frontend-factures): invoice/payment flow against api`
- [ ] `feat(frontend-notifications): notifications center`
- [ ] `ci: add backend/frontend pipelines + quality gates`
- [ ] `docs: finalize architecture, runbook, contribution guide`
