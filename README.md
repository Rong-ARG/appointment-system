# Appointment System API

[![CI](https://github.com/Rong-ARG/appointment-system/actions/workflows/ci.yml/badge.svg)](https://github.com/Rong-ARG/appointment-system/actions/workflows/ci.yml)
![Tests](https://img.shields.io/badge/Tests-170%2B%20passing-success)
![Last commit](https://img.shields.io/github/last-commit/Rong-ARG/appointment-system)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)

A REST API for scheduling appointments between clients and professionals.
Built as a personal project to practice Java and Spring Boot while preparing
for my first job as a backend developer :)

## Key results

- **170+ automated tests** (JUnit 5, Mockito, MockMvc and Testcontainers), with CI on GitHub Actions
- **19 bugs and security issues** found and fixed during development ([see the list](#bugs-and-security-fixes))
- **25 REST endpoints** across auth, users, professionals and appointments
- **JWT authentication** with ownership checks, so users can only access their own data

## Features

- JWT authentication with Spring Security and two roles (USER and ADMIN)
- One account can be both a client and a professional
- Clients can search professionals by specialty or last name, book appointments and cancel them
- Professionals can confirm or cancel the appointments booked with them
- Appointments are cancelled, not deleted: both sides keep the history, and physical deletion is ADMIN-only
- Ownership checks: you can only see or modify appointments you're involved in
- Business rules: a user or professional can't be deleted while they still have appointments; a professional's time slot can't be double-booked, and appointments can't be made with an unavailable professional or with oneself
- Validation and error handling that return clear JSON messages
- Swagger documentation and a test suite with JUnit 5 and Mockito

## Design decisions

- **One DTO per operation** (Request, Response, Patch), so the API contract doesn't expose entity internals
- **Shared `Account` model:** a person can be both a client and a professional without duplicating credentials
- **Ownership checks in the service layer**, not only in the controllers
- **Status transitions defined in the `AppointmentStatus` enum**, so the rules live next to the states
- **A cancelled appointment frees its time slot:** the double-booking check only counts `PENDING` and `CONFIRMED` appointments, so a slot can be booked again after a cancellation
- **Cancel, don't delete:** clients and professionals cancel through `PATCH` (status `CANCELLED`), so the state rules apply and the history is kept. `DELETE` on appointments is ADMIN-only, enforced both in `SecurityConfig` and in the service layer
- **An empty search is not an error:** `GET /api/professionals?specialty=...&lastName=...` returns `200` with an empty list when nothing matches; `404` is reserved for resources that don't exist
- **The database schema is versioned with Flyway:** migrations live in `src/main/resources/db/migration`, Hibernate only validates the schema (`ddl-auto=validate`) and the integration tests apply every migration to an empty MySQL container
- **Double booking is prevented twice:** by a pessimistic lock in the service and by a unique index in the database (`professional_id` plus a generated `active_slot` column that is `NULL` for cancelled appointments, so a cancelled appointment still frees its slot)
- **One place for the email-uniqueness rule:** `AccountEmailValidator` is shared by sign-up and by the user and professional updates

## Technologies

| Layer | Stack | Details |
|-------|-------|---------|
| Language and framework | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) | Java 21 and Spring Boot 4.1.0. REST API built with Spring Web MVC and Spring Data JPA, with Lombok and Jakarta Validation (`@NotBlank`, `@Email`, `@Valid`) for the DTOs |
| Security | ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white) | Spring Security with JWT (auth0 java-jwt 4.6.0): authentication, role-based access and ownership checks |
| Database | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) | MySQL 8 accessed through Hibernate (the JPA implementation behind Spring Data JPA), run locally with Docker Compose. The schema is versioned with Flyway migrations and Hibernate only validates it |
| API documentation | ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black) | springdoc-openapi 3.1.1, serving Swagger UI |
| Testing | ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI%2BPHBhdGggZmlsbD0iI2ZmZiIgZD0iTTMgMTlWNWgzLjJMMTIgMTMuMiAxNy44IDVIMjF2MTRoLTNWMTAuNGwtNiA4LjYtNi04LjZWMTl6Ii8%2BPC9zdmc%2BCg%3D%3D) | JUnit 5 and Mockito for service tests, MockMvc with Spring Security Test for controllers and access rules, and Testcontainers (MySQL) for integration tests against a real database |
| Build and CI | ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-0db7ed?style=for-the-badge&logo=docker&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) | Maven Wrapper for the build, a multi-stage Dockerfile and Docker Compose to run the app together with its database, and GitHub Actions running the tests (the integration tests start their own MySQL container with Testcontainers) |
| Test frontend | ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black) | Minimal client (plain HTML, CSS and JavaScript) to exercise the login and appointment flows |

## Notes

- `application.properties` is not committed (it's in `.gitignore`), use `application.properties.example` as a starting point
- The JWT key is read from the `JWT_SECRET` environment variable (Docker Compose takes it from your `.env` file). The example properties file only has a demo fallback so the project runs out of the box: use your own value anywhere real.

## API Documentation

Swagger UI available at: http://localhost:8080/swagger-ui/index.html

![Swagger UI](docs/swagger.png)

## Getting Started

### Prerequisites

- Docker
- Java 21 (only if you run the app or the tests outside Docker)

### Quick start with Docker (app + database)

```
cp .env.example .env
docker compose up --build
```

Then open http://localhost:8080/login.html or the Swagger UI. The `.env` file holds the `JWT_SECRET` used to sign tokens: put your own value there (for example the output of `openssl rand -hex 32`). The app waits for MySQL to be healthy and Flyway creates the schema on the first run.

### Run from source (development)

1. Clone the repository
   ```
   git clone https://github.com/Rong-ARG/appointment-system.git
   cd appointment-system
   ```
2. Copy the example properties file
   ```
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
3. Start only the database (the first time, give MySQL about 20 seconds to initialise)
   ```
   docker compose up -d mysql
   ```
4. Run the application
   ```
   ./mvnw spring-boot:run
   ```
5. Access Swagger UI at http://localhost:8080/swagger-ui/index.html
6. A small test frontend (plain HTML/CSS/JS) is included under `src/main/resources/static`. Once the app is running, open http://localhost:8080/login.html to try the full login flow. There's also a `profile.html` page to edit your info or delete your account, and a `professional-appointments.html` page for professionals to confirm/cancel appointments booked with them.

This is what the test frontend looks like once you log in:

![Test frontend dashboard](docs/dashboard.png)

### Running the tests

```
./mvnw test
```

Do the setup steps above first (at least the properties file) and make sure Docker is running: the integration tests (`AppointmentSystemApplicationTests` and the concurrency test in `integration/`) run against a temporary MySQL container started by Testcontainers, so you don't need `docker compose up` for the tests. The rest of the tests use mocks.

## API Endpoints

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /auth/login | Log in with email and password, returns a JWT |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users | Get all users (ADMIN only) |
| GET | /api/users/me | Get the logged-in user's own profile |
| GET | /api/users/{id} | Get user by ID (the user themself or an ADMIN) |
| GET | /api/users/email/{email} | Get user by email (ADMIN only) |
| GET | /api/users/lastname/{lastName} | Get users by last name (ADMIN only) |
| POST | /api/users | Create user (public, this is the sign-up) |
| PUT | /api/users/{id} | Update user |
| PATCH | /api/users/{id} | Partially update user |
| DELETE | /api/users/{id} | Delete user |

### Professionals

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/professionals | Get all professionals, or search with the optional `specialty` and `lastName` query params (empty list if nothing matches) |
| GET | /api/professionals/{id} | Get professional by ID |
| GET | /api/professionals/email/{email} | Get professional by email |
| POST | /api/professionals | Create professional (ADMIN only) |
| POST | /api/professionals/me | Become a professional yourself (self-service, requires an existing user account) |
| PUT | /api/professionals/{id} | Update professional |
| PATCH | /api/professionals/{id} | Partially update professional |
| DELETE | /api/professionals/{id} | Delete professional |

### Appointments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/appointments | Get all appointments (ADMIN only) |
| GET | /api/appointments/{id} | Get appointment by ID |
| GET | /api/appointments/mine | Get the logged-in user's own appointments |
| GET | /api/appointments/mine/professional | Get appointments booked with the logged-in professional |
| POST | /api/appointments | Create appointment (the client is the logged-in user; it starts as `PENDING`) |
| PATCH | /api/appointments/{id} | Update appointment status (either side can cancel, only the professional can confirm) |
| DELETE | /api/appointments/{id} | Delete appointment (ADMIN only; clients and professionals cancel via PATCH) |

## Roadmap

- [x] Exception handling
- [x] Swagger / OpenAPI documentation
- [x] Spring Security + JWT
- [x] Shared Account/roles model (a person can be both a user and a professional)
- [x] Flyway migrations (versioned schema, Hibernate only validates it)
- [x] Dockerfile and the full stack (app + database) with Docker Compose
- [x] Testcontainers: the Spring context test runs against a throwaway MySQL container
- [x] Concurrency test for the double-booking lock, run against a real MySQL container (Testcontainers)
- [x] Unit testing — UserServiceImpl, ProfessionalServiceImpl, AppointmentServiceImpl fully covered (68 tests)
- [x] Unit testing — AuthService / JwtService fully covered (6 tests)
- [x] Unit testing — CurrentUserService
- [x] Controller tests (MockMvc) — Auth, User, Professional and Appointment controllers (170+ tests in total across the project)

## Bugs and security fixes

Things I found and fixed while building the project:

<details>
<summary>Show the 19 fixes</summary>

- Users could create appointments on behalf of other users — `userId` in the request body wasn't checked against the logged-in user. The field was later removed from the DTO: the client is now always taken from the token.
- A `Professional` could be created without an existing `User` account — broke the "user first, professional later" model.
- Anyone could turn someone else's account into a professional — `POST /api/professionals` is now ADMIN-only, self-service is done through `/me`.
- `GET /api/appointments/{id}` (and delete/patch) didn't check if the logged-in user was actually involved in the appointment — added an ownership check (`verifyOwner`) shared across those methods.
- Confusing error message when a professional-only account hits an endpoint that expects a user profile — now says clearly that the account has no user profile.
- A professional could delete their account while still having pending appointments, leaving them orphaned — `deleteProfessional` now blocks the deletion if the professional still has appointments.
- `deleteUser` still left a `Professional` profile orphaned (no `User`) if that account also had a professional profile — `deleteUser` now blocks the deletion if the account still has a professional profile (must delete that first) or if the user has appointments as a client.
- Backend error messages weren't showing up on the frontend (always showed a generic "Error 409" instead of the real message) — exception handlers in `GlobalExceptionHandler` were returning plain text instead of JSON, but the frontend's `apiFetch` only parses the response body when the `Content-Type` is `application/json`. Now every handler returns `{ "message": "..." }`.
- Removing a professional profile could throw a raw Hibernate error (`ObjectDeletedException`) straight to the user — `Account` still held an in-memory reference to the already-deleted `Professional` (cascade tried to re-save it). Fixed by clearing the reference (`account.setProfessional(null)`) before saving. Also added a catch-all exception handler so any future unexpected error returns a safe generic message instead of leaking internal details.
- Any authenticated user could look up another user's full profile by id (`GET /api/users/{id}`) — added an ownership check so only the user themself or an ADMIN can access it.
- JWT expiration time was hardcoded in `JwtService` — moved to `application.properties`.
- `JwtAuthFilter` silently swallowed all token validation errors with no logging — added a debug log so failures are traceable without exposing details to the client.
- Appointments could be double-booked, created with a professional who was not available, or booked by a professional with themselves — `createAppointment` now rejects these cases with a 409.
- Any participant of an appointment could hard-delete it through `DELETE /api/appointments/{id}`, skipping the status rules (and the client's Cancel button erased the row, so the professional lost the history) — deletion is now ADMIN-only (service layer + `SecurityConfig`) and the frontend cancels through `PATCH` instead.
- Any participant of an appointment could set any status on it (a client could confirm their own appointment, or reopen a cancelled one) — `AppointmentStatus` now defines the allowed transitions, only the professional can confirm, and invalid changes return 409.
- Two simultaneous requests could book the same time slot — `createAppointment` checked availability and then saved, so both requests could pass the check. The professional's row is now locked (`PESSIMISTIC_WRITE`, `findByIdForUpdate`) at the start of the transaction, so bookings for the same professional are processed one at a time. Covered by an integration test that fires 5 simultaneous bookings for the same slot against a real MySQL container: exactly one succeeds (without the lock, all 5 did). The rule is also enforced by the database itself with a unique index (Flyway migration V2), so no other code path can create a duplicate.
- Unknown routes, unsupported HTTP methods and malformed path values (like `/api/users/abc`) returned a generic 500 — the catch-all exception handler was swallowing Spring MVC's own exceptions. Added specific handlers that return 404, 405 and 400.
- Changing an email to one that already belongs to another account ended in a 500 (the database unique constraint rejected it) — added an `AccountEmailValidator` that returns a clear 409 before saving, plus a handler for `DataIntegrityViolationException` as a safety net for race conditions.
- `POST /api/users` (the public sign-up) modified an existing account without authentication when the email existed but had no user profile — sign-up now always returns 409 if the email is already registered.

</details>

## Contact

<p>
  <a href="https://github.com/Rong-ARG"><img alt="GitHub" src="https://img.shields.io/badge/GitHub-121011?style=for-the-badge&logo=github&logoColor=white" /></a>
  <a href="https://www.linkedin.com/in/pablo-abucewicz-223169334/"><img alt="LinkedIn" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" /></a>
  <a href="mailto:abucewiczpablo@gmail.com"><img alt="Gmail" src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" /></a>
</p>