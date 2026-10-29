# Gym CRM — Spring MVC, Hibernate/JPA, REST API

A Gym CRM application implementing trainee, trainer, training, authentication,
profile management, trainer assignment, and training-management functionality.

The application uses a layered Spring MVC architecture with Hibernate/JPA
persistence and a PostgreSQL database.

## Technologies

- Java 21
- Spring Framework 6
- Spring MVC
- Hibernate 6 / JPA
- PostgreSQL
- Embedded Tomcat
- Jackson
- Hibernate Validator
- JUnit 5
- Mockito
- AssertJ
- JaCoCo
- Maven

## Architecture

```text
RestController
  → DTO validation / mapping
    → JpaGymFacade / service layer
      → JPA DAO / Hibernate
        → PostgreSQL
```

### Packages

- `controller` — REST endpoints and HTTP request handling
- `dto` — request and response contracts
- `mapper` — entity-to-DTO conversion
- `service.jpa` — transactional business services
- `dao.jpa` — JPA data-access components
- `entity` — JPA domain entities
- `exception` — application and API error handling
- `logging` — transaction and REST request logging
- `config` — Spring, persistence, and web configuration

## REST API

Base URL:

```text
http://localhost:8080/api/v1
```

### Public endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/trainees/register` | Register trainee |
| POST | `/trainers/register` | Register trainer |
| GET | `/training-types` | Get training types |

### Authenticated endpoints

All other endpoints require:

```text
X-Auth-Username: <username>
X-Auth-Password: <password>
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/auth/login` | Login |
| PUT | `/auth/password` | Change password |
| GET | `/trainees/{username}` | Get trainee profile |
| PUT | `/trainees/{username}` | Update trainee profile |
| DELETE | `/trainees/{username}` | Delete trainee |
| PATCH | `/trainees/{username}/status` | Activate/deactivate trainee |
| GET | `/trainees/{username}/trainers` | Get unassigned active trainers |
| PUT | `/trainees/{username}/trainers` | Replace assigned trainers |
| GET | `/trainees/{username}/trainings` | Get trainee trainings |
| GET | `/trainers/{username}` | Get trainer profile |
| PUT | `/trainers/{username}` | Update trainer profile |
| PATCH | `/trainers/{username}/status` | Activate/deactivate trainer |
| GET | `/trainers/{username}/trainings` | Get trainer trainings |
| POST | `/trainings` | Add training |

## Business rules

- Usernames are generated from first and last name.
- Duplicate names receive a numeric suffix.
- Passwords are randomly generated.
- Username cannot be changed.
- All operations except registration require authentication.
- Trainee deletion is a hard delete and removes related trainings.
- Training types are fixed reference data.
- Training creation, update, and deletion are not exposed except training creation.
- Activation/deactivation is not idempotent.

## Error handling

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-10-29T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed.",
  "details": {
    "firstName": "First name must not be blank."
  }
}
```

## Logging

Each HTTP request receives a generated `transactionId`.

The application logs:

- Request method and URI.
- Response status and duration.
- Transaction ID for correlation.
- Errors without exposing passwords.

## Build and test

```bash
mvn clean verify
```

## Run

```bash
mvn clean compile exec:java
```

The application starts on:

```text
http://localhost:8080
```

## Example: register trainee

```bash
curl -X POST http://localhost:8080/api/v1/trainees/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "dateOfBirth": "1995-05-10",
    "address": "123 Main Street"
  }'
```

## Example: get trainee profile

```bash
curl http://localhost:8080/api/v1/trainees/john.smith \
  -H "X-Auth-Username: john.smith" \
  -H "X-Auth-Password: generated-password"
```