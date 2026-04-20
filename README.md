# ClinicPro

ClinicPro is a clean rebuild of the medical clinic management platform using:

- **Backend:** Spring Boot (Java 17), Spring Security (JWT), Spring Data JPA, OpenAPI/Swagger
- **Frontend:** Angular (standalone architecture), SCSS
- **Database:** PostgreSQL

## Repository structure

- `backend/` — Spring Boot API
- `frontend/` — Angular web application
- `docs/` — architecture and technical documentation
- `.github/workflows/` — CI pipelines (to be added)

## Current phase

This repository is in **clean implementation mode**:

1. Build a stable backend contract first
2. Build frontend features against stable APIs
3. Apply polished UI system inspired by `Medical Clinic Management UI_UX`
4. Ship with clean Git history and CI quality gates

## Quick start (local)

### Backend

1. Go to `backend/`
2. Configure `.env` (already scaffolded)
3. Start PostgreSQL (local service or docker compose)
4. Run the app with Maven Wrapper

### Frontend

1. Go to `frontend/`
2. Run `npm install`
3. Run `npm start`

## Git workflow

- Small atomic commits
- Conventional commit messages
- Feature slices (backend first, then frontend)

See `checklist.md` for the full implementation roadmap.
