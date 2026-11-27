# Employee Service

REST microservice for managing employees (Babel/Invex technical test).

## Stack

- Java 17
- Spring Boot 2.7.18 (Web, Data JPA, Validation, Actuator)
- MySQL 8.0 (runtime) + H2 (tests only) + Flyway migrations
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + AssertJ + MockMvc
- Docker / Docker Compose

## Prerequisites

- JDK 17
- Maven 3.8+ (or use a wrapper; this project was built with `scoop install maven` on Windows)
- Docker + Docker Compose (needed to run the app locally unless you already have a MySQL instance; `mvn test` does **not** need Docker — it runs against H2)

## Running locally (recommended: Docker Compose)

```bash
docker compose up -d --build
```

This starts MySQL 8.0 (with a `healthcheck`) and the service (waits for MySQL to be healthy before starting), builds the image as a multi-stage Maven build + `eclipse-temurin:17-jre-alpine` runtime, and exposes the app on port 8080 with a container `HEALTHCHECK` hitting `/actuator/health`.

To build/run manually without compose:

```bash
docker build -t employee-service:local .
docker run -d -p 8080:8080 --name employee-service employee-service:local
```

## Running locally without Docker

Start just the database (`docker compose up -d mysql`), then `mvn spring-boot:run`. `DB_HOST`/`DB_PORT`/`DB_NAME`/`DB_USERNAME`/`DB_PASSWORD` env vars override the connection if your MySQL isn't the `docker-compose.yml` one (defaults: `localhost:3306/employeedb`, user/password `employee`).

The app starts on `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`

## Postman collection

Import `postman/employee-service.postman_collection.json` into Postman. It covers all 6 endpoints plus error cases (404, 400) and the health check, using a `baseUrl` collection variable (defaults to `http://localhost:8080`).

## Tests

```bash
mvn test
```

Includes:
- `EmployeeServiceImplTest` (Mockito) — business rules: batch creation, partial update, search, error cases.
- `EmployeeControllerTest` (`@WebMvcTest` + MockMvc) — HTTP status codes, payload validation.
- `EmployeeRepositoryTest` (`@DataJpaTest`) — partial name search, automatic timestamp.
- `EmployeeServiceApplicationTests` — context load.

## "Employee" model

| Field | Type | Notes |
|---|---|---|
| id | Long | auto-generated |
| firstName | String | required |
| middleName | String | optional |
| paternalLastName | String | required |
| maternalLastName | String | optional |
| age | Integer | required, 0-120 |
| sex | enum `MALE/FEMALE/OTHER/UNSPECIFIED` | optional, defaults to `UNSPECIFIED` |
| birthDate | LocalDate | required, `dd-MM-yyyy` format, must be in the past |
| position | String | required |
| registrationDate | LocalDateTime | set automatically on creation (Hibernate `@CreationTimestamp`) |
| active | boolean | defaults to `true` when not provided |

## Endpoints

Base path: `/v1/employees`

| Method | Path | Description |
|---|---|---|
| GET | `/v1/employees?page=&size=` | List employees, paginated (default `size=20`, max `size=100`) |
| GET | `/v1/employees/{id}` | Get employee by id (404 if not found) |
| POST | `/v1/employees` | Create one or more employees (JSON array body, 201) |
| PUT | `/v1/employees/{id}` | Update all or some fields (merges non-null fields) |
| DELETE | `/v1/employees/{id}` | Delete by id (204) |
| GET | `/v1/employees/search?name=&page=&size=` | Case-insensitive partial match across all name fields, paginated |

`GET` list/search responses use Spring Data's standard `Page` JSON shape: `{ "content": [...], "totalElements": N, "totalPages": N, "number": 0, "size": 20, ... }`.

### Examples

**Create (one or many, always as an array):**
```bash
curl -X POST http://localhost:8080/v1/employees \
  -H "Content-Type: application/json" \
  -d '[
    {"firstName":"Maria","paternalLastName":"Gonzalez","age":32,"sex":"FEMALE","birthDate":"10-01-1994","position":"Developer"},
    {"firstName":"Juan","paternalLastName":"Martinez","age":42,"sex":"MALE","birthDate":"20-03-1984","position":"Manager"}
  ]'
```

**Partial update** (omit a field to leave it unchanged; send `middleName`/`maternalLastName` as `null` to explicitly clear them):
```bash
curl -X PUT http://localhost:8080/v1/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"position":"Senior Developer","middleName":null}'
```

**Search (page 2, 10 per page):**
```bash
curl "http://localhost:8080/v1/employees/search?name=mar&page=1&size=10"
```

## Project structure

```
src/main/java/com/babel/employeeservice/
  config/            OpenAPI, Jackson (JsonNullableModule), request logging, body size limit filter, GlobalExceptionHandler
  constants/         validation/format constants
  controller/        EmployeeController (REST)
  dto/request/       EmployeeRequest, EmployeeUpdateRequest
  dto/response/      EmployeeResponse
  dto/exception/     ApiError (error response DTO)
  exception/         custom domain exceptions (EmployeeNotFoundException, InvalidEmployeeDataException)
  entity/            Employee (JPA entity), Sex (enum)
  mapper/            EmployeeMapper (entity <-> DTO)
  repository/        EmployeeRepository (Spring Data JPA)
  service/           EmployeeService + impl
  util/              AgeBirthDateConsistency
```