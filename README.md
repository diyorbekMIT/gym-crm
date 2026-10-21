# Gym CRM — Hibernate / Spring Data JPA

A Java console-oriented CRM application implementing trainee, trainer,
training, authentication, profile management, trainer assignment, and
training-management features using Spring and Hibernate/JPA.

## Technologies

- Java 21
- Spring Framework
- Hibernate / Spring Data JPA
- H2 database
- JUnit 5
- Mockito
- AssertJ
- Maven

## Architecture

- `entity` — JPA entities
- `dao` — Spring Data JPA repositories
- `service.jpa` — transactional business services
- `facade` — authenticated API facade
- `config` — Spring and persistence configuration
- `exception` — application exceptions

## Key services

- `AuthenticationService` — credential matching
- `ProfileRegistrationService` — trainee and trainer registration
- `ProfileAccountService` — profile retrieval and password changes
- `ProfileManagementService` — profile updates and activation status
- `TrainingManagementService` — training creation
- `TrainingQueryService` — filtered training searches
- `TrainerAssignmentService` — trainer assignment management
- `TraineeDeletionService` — authenticated trainee deletion

## Security model

Every operation requires the user’s username and current password.
Credentials are checked before business logic runs.

Passwords are stored as plain text only because this assignment does not
require hashing. In production, passwords must be hashed with a modern
password encoder such as BCrypt.

## Database

The application uses H2 with Hibernate-managed schema generation.

Training types are initialized automatically at startup.

## Build and test

```bash
mvn clean verify
```

## Run

```bash
mvn clean compile exec:java
```

## API

`JpaGymFacade` exposes the application operations:

- Create trainee or trainer
- Retrieve profile
- Change password
- Update profile
- Activate or deactivate profile
- Add training
- Search trainee or trainer trainings
- List and replace assigned trainers
- Delete trainee