# Attendance Management System

A Quarkus-based attendance management backend using **hexagonal architecture**, **SmallRye GraphQL**, **PostgreSQL**, and **JWT security**.

## Architecture

```
domain/          → Pure business models, enums, ports (framework-free)
application/     → Use cases, commands, queries, DTOs, exceptions
infrastructure/  → JPA entities, repository adapters, mappers, security, config
presentation/    → GraphQL resolvers, inputs, outputs
```

## Roles

- **ADMIN** – Full backoffice: manage users, projects, view dashboards
- **PROJECT_MANAGER** – Manage team attendance, review justifications, alerts
- **TEAM_MEMBER** – Register attendance, manage absences, submit justifications

## Running

```bash
./mvnw quarkus:dev
```

GraphQL UI: http://localhost:8080/q/graphql-ui

## Configuration

Edit `src/main/resources/application.properties` with your PostgreSQL credentials and JWT keys.

## Database Migrations

Flyway migrations in `src/main/resources/db/migration/`.
