# Job Application Tracker

Backend application for tracking job applications, interview stages and application history.

## Features

- Create and manage job applications
- Track application status
- Store application history
- Manage interview stages
- Analyze application progress and response rates
- Overview application statistics by year or month

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Docker

## Project Status

Early development.

## Planned Features


- Application analytics
- CV version tracking


## Run Locally

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the app:

```bash
./mvnw spring-boot:run
```

By default the app connects to:

```text
jdbc:postgresql://localhost:5432/job_application_tracker
username: postgres
password: postgres
```

Override those values with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.
