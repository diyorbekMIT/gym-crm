# Gym CRM — Spring Boot Microservices & Security

A Gym CRM microservices system implementing trainee/trainer profile management, training scheduling, Spring Security (BCrypt + JWT + Brute Force protection + CORS), Eureka Service Discovery, Resilience4j Circuit Breaker, two-level distributed transaction logging, and full Docker Compose containerization.

## Services Overview

| Service | Directory | Port | Role |
|---|---|---|---|
| **Gym CRM (`gym-crm`)** | `.` (root) | `8080` | Main microservice (REST API, PostgreSQL/JPA, JWT auth, Circuit Breaker client) |
| **Trainer Workload (`trainer-workload-service`)** | `./trainer-workload-service` | `8081` | Secondary microservice (in-memory DB of trainer monthly working hours, JWT validation) |
| **Discovery Server (`discovery-service`)** | `./discovery-service` | `8761` | Netflix Eureka Server for service registration & discovery |
| **PostgreSQL (`postgres`)** | Docker image `postgres:16-alpine` | `5432` | Relational database for `gym-crm` |

## Technologies

- Java 21
- Spring Boot 3.5.6 & Spring Cloud 2025.0.0
- Spring Security (BCrypt hashing, JWT Bearer tokens, brute-force protection, CORS)
- Spring Cloud Netflix Eureka (Server & Client) & LoadBalancer
- Spring Cloud Circuit Breaker (Resilience4j)
- Hibernate 6 / JPA & PostgreSQL
- Spring Boot Actuator & Micrometer / Prometheus
- Springdoc OpenAPI (Swagger UI)
- Docker & Docker Compose
- JUnit 5, Mockito, AssertJ

## Architecture

```text
Client
  │
  ▼ (JWT Bearer Token)
gym-crm (:8080) ──► PostgreSQL (:5432)
  │
  ├──► discovery-service / Eureka (:8761)  [service lookup]
  │
  ▼ (@LoadBalanced RestClient + Resilience4j Circuit Breaker + JWT + X-Transaction-Id)
trainer-workload-service (:8081)  [in-memory monthly summary]
```

## Authentication & Security (Spring Security + JWT)

- Passwords are salted and hashed with **BCrypt** in the database (`plainPassword` is returned once during registration).
- **Brute-force protection:** 3 consecutive failed login attempts block the user for **5 minutes** (`403 Forbidden`).
- **Logout** blacklists the JWT token so subsequent requests with that token are rejected.
- **Inter-service security:** `gym-crm` sends a signed JWT Bearer token to `trainer-workload-service`, which validates it statelessly using the shared secret.

### Public Endpoints (`gym-crm`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/trainees/register` | Register trainee (returns generated username & password) |
| POST | `/api/v1/trainers/register` | Register trainer (returns generated username & password) |
| POST | `/api/v1/auth/login` | Authenticate with username/password and receive JWT |
| GET | `/actuator/**` | Health & metrics endpoints |
| GET | `/swagger-ui.html` | OpenAPI / Swagger UI |

### Protected Endpoints

All other endpoints require `Authorization: Bearer <jwt-token>`:

| Service | Method | Endpoint | Description |
|---|---|---|---|
| `gym-crm` | POST | `/api/v1/auth/logout` | Invalidate current JWT token |
| `gym-crm` | PUT | `/api/v1/auth/password` | Change password |
| `gym-crm` | GET/PUT/DELETE | `/api/v1/trainees/{username}` | Get, update, or hard-delete trainee (cascade-deletes trainings & notifies workload service) |
| `gym-crm` | PATCH | `/api/v1/trainees/{username}/status` | Activate/deactivate trainee |
| `gym-crm` | GET/PUT | `/api/v1/trainees/{username}/trainers` | Get unassigned trainers / update assignments |
| `gym-crm` | GET | `/api/v1/trainees/{username}/trainings` | Get trainee trainings |
| `gym-crm` | GET/PUT | `/api/v1/trainers/{username}` | Get or update trainer profile |
| `gym-crm` | PATCH | `/api/v1/trainers/{username}/status` | Activate/deactivate trainer |
| `gym-crm` | GET | `/api/v1/trainers/{username}/trainings` | Get trainer trainings |
| `gym-crm` | POST | `/api/v1/trainings` | Add training (notifies workload service `ADD`) |
| `trainer-workload-service` | POST | `/api/v1/trainer-workload` | Accept workload event (`ADD` / `DELETE`) |
| `trainer-workload-service` | GET | `/api/v1/trainer-workload/{username}` | Get trainer monthly training duration summary |

## Logging & Transaction Correlation

Two logging levels are implemented across services:
1. **Transaction level** (`RestLoggingFilter` / `TransactionLoggingFilter`): generates (or reads from `X-Transaction-Id`) a `transactionId`, logs HTTP method, URI, status, and duration.
2. **Operation level** (service layer): logs business operations tagged with the same `transactionId` via SLF4J `MDC`.

## Build & Run Locally (Maven)

Run tests in `gym-crm`:
```bash
mvn test
```

Start all 3 services in separate terminals (requires local PostgreSQL on `5432`):
```bash
# 1. Eureka Discovery Server
cd discovery-service && mvn spring-boot:run

# 2. Trainer Workload Service
cd trainer-workload-service && mvn spring-boot:run

# 3. Main Gym CRM Service
mvn spring-boot:run
```

## Docker & Containerization

All 4 containers (`postgres`, `discovery-service`, `trainer-workload-service`, `app`) are configured in `docker-compose.yml` with health checks, non-root runtime users, and Docker Compose service-name networking.

### 1. Build & Start Everything
```bash
docker compose up -d --build
```

### 2. Verify Container Health & Eureka Registration
```bash
# Check container status (all 4 should show healthy)
docker compose ps

# Check Actuator health endpoints
curl -s http://localhost:8761/actuator/health
curl -s http://localhost:8081/actuator/health
curl -s http://localhost:8080/actuator/health
```
Open the Eureka Dashboard at `http://localhost:8761` — both `GYM-CRM` and `TRAINER-WORKLOAD-SERVICE` appear as `UP`.

### 3. Inspect Logs
```bash
docker compose logs -f app
docker compose logs -f trainer-workload-service
docker compose logs -f discovery-service
```

### 4. Demonstrate Resilience (Circuit Breaker & Eureka Recovery)
```bash
# 1. Stop trainer-workload-service while gym-crm stays up
docker compose stop trainer-workload-service

# Adding a training on :8080 triggers RestClient timeout + Resilience4j fallback cleanly
docker compose logs --tail 20 app

# 2. Restart trainer-workload-service and verify automatic re-registration
docker compose start trainer-workload-service

# 3. Restart Eureka and verify both services recover and re-register
docker compose restart discovery-service
```

### 5. Stop Stack
```bash
docker compose down
```

## Quick API Demo (`curl`)

```bash
# 1. Register a trainee
curl -X POST http://localhost:8080/api/v1/trainees/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "dateOfBirth": "1995-05-10",
    "address": "123 Main Street"
  }'

# 2. Login to get JWT Bearer token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"John.Smith","password":"<generated-password>"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

# 3. Query trainer workload summary (using JWT)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/v1/trainer-workload/<trainerUsername>
```
