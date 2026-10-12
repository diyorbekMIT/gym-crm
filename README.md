# Gym CRM — Spring Core

A Spring Core module for managing gym trainees, trainers, and training sessions.

## Features

- Create, update, delete, and select trainee profiles
- Create, update, and select trainer profiles
- Create and select training sessions
- In-memory `ConcurrentHashMap` storage
- Separate storage namespace for trainees, trainers, and trainings
- JSON seed-data initialization at application startup
- Spring `BeanPostProcessor` used to initialize storage
- Automatic username generation:
    - `John.Smith`
    - `John.Smith1`
    - `John.Smith2`
- Random 10-character alphanumeric password generation
- Constructor injection in `GymFacade`
- Setter injection for DAOs, services, and utilities
- Unit and Spring integration tests
- SLF4J and Logback logging
- JaCoCo code coverage report

## Technologies

- Java 21
- Spring Framework Core
- Maven
- Jackson
- SLF4J and Logback
- JUnit 5
- Mockito
- AssertJ
- JaCoCo

## Project structure

```text
com.gym.crm
├── config     # Spring Java configuration
├── model      # Domain entities
├── storage    # JSON storage initialization
├── dao        # In-memory data access objects
├── util       # Username and password generators
├── service    # Business logic
└── facade     # Single application entry facade
```

## Run tests

```bash
mvn clean test
```

## Run the application

```bash
mvn clean compile exec:java
```

## Run verification and coverage

```bash
mvn clean verify
open target/site/jacoco/index.html
```

## Seed data

Initial data is located at:

```text
src/main/resources/data/init-data.json
```

The initialization-file path is configured in:

```text
src/main/resources/application.properties
```