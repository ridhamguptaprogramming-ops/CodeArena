# CodeArena API

CodeArena is a Java 21 / Spring Boot modular monolith for programming assessments. Its API persists to Supabase PostgreSQL, uses Redis for shared ephemeral state and RabbitMQ for asynchronous work. Student programs are never executed in the API process.

## Stack and modules

- Spring Web, Validation, Security, Data JPA, Flyway, Actuator and Prometheus metrics
- PostgreSQL schema for users, roles, refresh tokens, problems, tags, hidden test cases, contests, submissions, notifications, audit events, suspicious activity and AI reviews
- RabbitMQ queues for execution, email, notifications and analytics, with a dead letter queue
- Redis rate limit foundation and STOMP WebSocket endpoint for user submission updates
- Teacher problem and contest APIs, visible-only student test-case APIs, student submission and contest registration APIs

## Configure

Copy `.env.example` to `.env` and supply real server-side values. The database settings must come from Supabase's PostgreSQL connection details. Enable SSL in the Supabase-provided connection URL. Never commit `.env` or share `DATABASE_PASSWORD`, `JWT_SECRET`, RabbitMQ credentials, worker callback tokens or any Supabase service-role credential. `SUPABASE_PUBLISHABLE_KEY` is optional metadata for trusted clients; it is not a database credential.

The backend requires `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`, and RabbitMQ credentials. Set `REDIS_PASSWORD` for Compose. The database URL must point to Supabase or another explicitly configured PostgreSQL service; no local database is started by the production Compose file.

## Run

```sh
cp .env.example .env
# Edit .env with real Supabase, JWT, Redis and RabbitMQ values
docker compose up --build
```

For local development, run Redis and RabbitMQ with Compose and start the API using Java 21 and Maven. Flyway applies `src/main/resources/db/migration` on startup; Hibernate validates the mapped schema. The root Compose stack deliberately contains no PostgreSQL service.

## API outline

- `POST /api/v1/auth/register`, `/login`, `/refresh`, `/logout`
- `GET /api/v1/problems`, `/api/v1/problems/{id}`
- `GET/POST /api/v1/problems/{problemId}/test-cases` (student reads return visible cases only)
- `GET/POST /api/v1/contests`, `POST /api/v1/contests/{id}/register`
- `POST /api/v1/submissions`, `GET /api/v1/submissions?page=0&size=20`, `GET /api/v1/submissions/{id}`
- `/ws` STOMP endpoint; submission events are sent to `/user/queue/submissions`
- `PUT /internal/v1/worker/submissions/{id}` is for trusted workers only and requires `X-Worker-Token` matching `WORKER_CALLBACK_SECRET`

Teacher and administrator access is checked in Spring Security. Registration creates an ordinary student account; role grants must be made through a trusted administrative process. Public responses for submissions do not include source code. Hidden case data is only available to trusted worker infrastructure and teacher management access must be further narrowed to ownership in deployments exposing those operations.

## Execution security boundary

The API persists a queued submission and publishes a job containing the submission identifier. The execution service must be a separate deployment with no API/database credentials and must fetch only the job's required inputs via a scoped worker interface. A production worker should create a per-job sandbox with network disabled, read-only base filesystem, isolated temporary storage, CPU/memory/process limits, hard wall timeout, syscall restrictions, and guaranteed teardown. No compiler or student process belongs in the API container. Worker callbacks use a separate high-entropy shared secret; production should rotate it and restrict callback network ingress as well.

The repository establishes the API and worker boundary; it does not include a production sandbox executor, email provider, Supabase Storage provisioning, full refresh-token reuse detection, verification/reset email workflows, complete contest problem management, or full analytics/adaptive-learning services. Those integrations require environment-specific deployment services and should be implemented before describing this as a complete production platform.

## Storage and monitoring

Create private Supabase Storage buckets for contest and problem assets, and a restricted profile image bucket using Supabase policies. Do not store hidden test inputs in public buckets. `/actuator/health`, metrics, and Prometheus export are enabled; protect operational endpoints at the ingress and connect Prometheus/Grafana in deployment.

## Frontend

The Next.js frontend remains in [`frontend`](frontend). Dependencies are installed separately with `npm ci` in that directory; generated `node_modules` and `.next` output are ignored by Git.
