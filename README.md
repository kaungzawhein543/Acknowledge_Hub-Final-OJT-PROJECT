# Acknowledge Hub

Internal company announcement and acknowledgement platform built with **Angular 18** and **Spring Boot 3**.

## Repository structure

- `acKnowledgeHub_angular(frontend)` — Angular web app
- `acKnowledgeHub(backend)` — Spring Boot REST API
- `PROJECT_FEATURES.md` — full feature list, APIs, routes, and setup notes

## Quick start

### Prerequisites

- Java 17+
- Maven
- Node.js and npm

MySQL is **not** required for local development. The backend uses an embedded H2 database by default.

### Backend

```bash
cd "acKnowledgeHub(backend)"
cp .env.example .env
# Edit .env, then export variables (Spring also loads .env automatically)
mvn spring-boot:run
```

Backend: `http://localhost:8080`

### Frontend

```bash
cd "acKnowledgeHub_angular(frontend)"
npm install
npm start
```

Frontend: `http://localhost:4200`

## Demo logins

| Role | Staff ID | Default password |
|------|----------|------------------|
| Admin | `ADMIN001` | `adminPassword` |
| HR user | `EMP001` | `acknowledgeHub` |
| HR Main (Groups menu) | `HRMAIN001` | `acknowledgeHub` |

Passwords come from `DEFAULT_ADMIN_PASSWORD` and `DEFAULT_USER_PASSWORD` in `.env`. First login may require a password change.

## Configuration

Copy `acKnowledgeHub(backend)/.env.example` to `.env` and set at least:

- `JWT_SECRET`
- `DEFAULT_USER_PASSWORD` / `DEFAULT_ADMIN_PASSWORD`
- Mail, Cloudinary, and Telegram values if you use those features

Do **not** commit `.env`.

## Documentation

See [PROJECT_FEATURES.md](./PROJECT_FEATURES.md) for:

- Roles and permissions
- Frontend routes
- Backend API overview
- Database model and seeded demo data
- Known issues and deployment notes

## Branch

AI-assisted local-dev improvements are on the `AI-Modifications` branch (H2 database, env-based config, expanded seed data, build fixes).
