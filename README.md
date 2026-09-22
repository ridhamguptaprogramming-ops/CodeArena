# CodeArena

Secure online coding assessment platform, structured as a modular Spring Boot monolith.

## Frontend

The premium Next.js frontend lives in [`frontend`](frontend). It contains the responsive CodeArena landing page and an interactive student dashboard/problem explorer prototype.

```bash
cd frontend
npm install
npm run dev
```

Visit `http://localhost:3000`; use **Get started** to enter the dashboard and its sidebar to explore screen states. Configure `NEXT_PUBLIC_API_URL` when connecting it to the Spring API.

## Modules currently established

- JWT registration/login with BCrypt passwords and role claims
- Student submission API with immutable queued submission records
- Teacher-gated problem creation and public problem discovery
- PostgreSQL schema migrations, RabbitMQ submission queue/DLQ, Redis-ready configuration
- Explicit isolated-worker boundary: the web application never executes user code

## Run locally

Start infrastructure with `docker compose up -d`, then run `mvn spring-boot:run` with Java 21 and Maven installed. Copy `.env.example` values into your environment first. RabbitMQ management is at `http://localhost:15672`.

## Security boundary

`submission.execute` jobs must be consumed by a separate worker deployed with no application credentials and a hardened sandbox runtime (network disabled, read-only filesystem, cgroup CPU/memory/pid limits, per-job temporary container). Do not add code execution to this API process.

## Next modules

Contest lifecycle/registration, refresh-token rotation and OAuth/email flows, per-endpoint Redis rate limiting, WebSocket notifications, audit trails, hidden test cases, worker result callbacks, analytics, and cheating signals are intentionally separated modules to build on this baseline.
