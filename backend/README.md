# Job Application Tracker

Fullstack application for tracking job applications, interview stages, and application history.

## Demo

<img width="720" height="448" alt="Screen Recording 2026-05-17 at 12 09 18-2" src="https://github.com/user-attachments/assets/a190f2f2-7ab9-406d-9de0-e4152c8b28f8" />


## Features

- Create and manage job applications
- Track interview stages and application events
- Event-based application history
- Dashboard with yearly statistics
- Monthly application analytics
- Current status derived from latest event
- REST API with Swagger documentation
- Dockerized fullstack setup

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- React
- Vite
- JavaScript

### DevOps
- Docker
- Docker Compose

## Application Overview

The application uses an event-based architecture.

Each job application contains a list of events such as:
- APPLIED
- HR_INTERVIEW
- TECH_INTERVIEW
- FINAL_INTERVIEW
- TASK
- OFFER
- REJECTED

The current application status is derived from the latest event.

## Running the Application

### Prerequisites

- Docker Desktop
- Git

### Start the full application

bash git clone <your-repository-url> cd job-application-tracker docker compose up --build 

## Application URLs

Frontend:
text http://localhost:5173 

Backend:
text http://localhost:8080 

Swagger UI:
text http://localhost:8080/swagger-ui.html 

## Example Features

- Add new applications
- Add interview events
- Track application history
- View yearly statistics
- View monthly application chart
- Analyze response progress

## Testing

The backend contains controller tests using:
- JUnit 5
- Spring MockMvc
- Mockito

## Project Status

MVP version completed.

Future improvements may include:
- CV version tracking
- Search and filtering
- Deployment
- Authentication
- Advanced analytics
