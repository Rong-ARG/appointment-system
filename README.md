# Appointment System API

A REST API for scheduling appointments between clients and professionals.
Built as a personal project to practice Java and Spring Boot while preparing
for my first job as a backend developer :)

## Technologies

- Java 21
- Spring Boot 4.1.0
- Spring Data JPA
- MySQL 8
- Docker
- Lombok
- Jakarta Validation
- springdoc-openapi 2.8.9

## Notes

- Spring Boot version was set to 4.1.0 to ensure compatibility with springdoc-openapi 2.8.9
- Database credentials are not included in the repository for security reasons — use the example properties file

## API Documentation

Swagger UI available at: http://localhost:8080/swagger-ui/index.html

## Getting Started

### Prerequisites

- Java 21
- Docker

### Setup

1. Clone the repository
   git clone https://github.com/Rong-ARG/appointment-system.git
   cd appointment-system

2. Copy the example properties file
   cp src/main/resources/application.properties.example src/main/resources/application.properties

3. Start the database
   docker compose up -d

4. Run the application
   ./mvnw spring-boot:run

5. Access Swagger UI at http://localhost:8080/swagger-ui/index.html

## API Endpoints

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users | Get all users |
| GET | /api/users/{id} | Get user by ID |
| GET | /api/users/email/{email} | Get user by email |
| GET | /api/users/lastname/{lastName} | Get users by last name |
| POST | /api/users | Create user |
| PUT | /api/users/{id} | Update user |
| PATCH | /api/users/{id} | Partially update user |
| DELETE | /api/users/{id} | Delete user |

### Professionals
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/professionals | Get all professionals |
| GET | /api/professionals/{id} | Get professional by ID |
| GET | /api/professionals/email/{email} | Get professional by email |
| GET | /api/professionals/lastname/{lastName} | Get professionals by last name |
| POST | /api/professionals | Create professional |
| PUT | /api/professionals/{id} | Update professional |
| PATCH | /api/professionals/{id} | Partially update professional |
| DELETE | /api/professionals/{id} | Delete professional |

### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/appointments | Get all appointments |
| GET | /api/appointments/{id} | Get appointment by ID |
| POST | /api/appointments | Create appointment |
| PATCH | /api/appointments/{id} | Update appointment status |
| DELETE | /api/appointments/{id} | Delete appointment |

## Roadmap

- [x] Exception handling
- [x] Swagger / OpenAPI documentation
- [ ] Spring Security + JWT
- [ ] Flyway migrations
- [ ] Unit testing (JUnit)