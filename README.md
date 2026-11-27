# TutorBridge

A tuition-matching platform for Khulna built with Java 21, Spring Boot 3.5, PostgreSQL 16, and Spring Security.

## Roles
- **ADMIN**: Verifies tutors, suspends and restores accounts, views platform stats and audit logs.
- **TUTOR** (seller): Completes a tuition profile, browses matched posts, applies, attends demos.
- **GUARDIAN** (buyer): Posts tuition requirements, reviews matched applicants, shortlists, schedules demos, accepts tutors, and writes reviews.

## Tech Stack
- Java 21, Spring Boot 3.5, Maven (`./mvnw`)
- Spring Web, Spring Data JPA, Flyway, PostgreSQL 16
- Spring Security (Stateless HS256 JWT + BCrypt)
- Springdoc OpenAPI (`/swagger-ui.html`) & Actuator
- JUnit 5, Mockito, Testcontainers, JaCoCo (70% line coverage gate)

## Getting Started (Local Development)
1. Copy `.env.example` to `.env` and fill in local values, or use the `dev` Spring profile with a local PostgreSQL 16 database.
2. Run tests and coverage verification:
   ```bash
   ./mvnw verify
   ```

## Git Workflow & Branch Protection
- `main`: Protected release branch.
- `develop`: Protected integration branch (PRs require 1 approval and green CI).
- Feature work uses short-lived branches (`feature/*`, `chore/*`, `fix/*`, `docs/*`) merged via PRs into `develop`.
- See [Branch Protection Rules](docs/branch-protection.md) for full details.
