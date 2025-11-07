# Jeel Sojitra — Portfolio

Spring Boot 3.5 app that ships a polished personal portfolio with SEO-friendly routing, Flyway migrations, AI-powered recommendations, and production-ready Docker + CI.

## Highlights

- ✅ SEO, canonical tags, sitemap/robots, and structured metadata per project.
- 🔎 Server search + client-side tag filtering for the projects grid.
- 📬 Validated contact form with CSRF, honeypot, time guard, and rate limiting stored in Postgres/H2.
- 📊 Analytics toggle (Plausible/Umami) controlled via config—no invasive cookies.
- 🤖 Local “AI” recommender + Q&A widget that ranks projects without external APIs.
- 🚀 Flyway migrations, dev/prod profiles, Dockerfile + docker-compose, and GitHub Actions CI.

## Run It

```bash
# Dev profile (H2, demo data, analytics off)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

```bash
# Production-like stack (Docker, Postgres)
docker compose up --build
```

The Docker Compose run publishes the app on [http://localhost:8080](http://localhost:8080) and exposes Postgres for persistence.

## Configuration

| Variable | Description | Default |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `dev` (H2) or `prod` (Postgres) | `dev` |
| `PROD_DB_URL` | JDBC URL for prod profile | `jdbc:postgresql://db:5432/portfolio` |
| `PROD_DB_USER` / `PROD_DB_PASSWORD` | Postgres credentials | `portfolio` / `portfolio` |
| `ANALYTICS_ENABLED` | `true`/`false` toggle for analytics snippet | `false` |
| `ANALYTICS_PROVIDER` | `plausible`, `umami`, or `none` | `none` |
| `ANALYTICS_HOST` | Script host (e.g., `https://plausible.io`) | `https://plausible.io` |
| `ANALYTICS_SITE_ID` | Required for Umami deployments | _empty_ |

## Testing & CI

```bash
./mvnw clean verify
```

JaCoCo enforces ≥65 % line coverage. The GitHub Actions workflow at `.github/workflows/ci.yml` runs the same checks plus a packaging step on every push/PR.

## Deploy Notes

1. Ensure Flyway migrations run (auto on startup).
2. Set `SPRING_PROFILES_ACTIVE=prod` and point DB env vars at your managed Postgres.
3. Enable analytics only after configuring `ANALYTICS_*` vars.
4. Health endpoint lives at `/actuator/health` for uptime checks.
