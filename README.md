# SweepShare

A REST API for distributing household chores in shared apartments (Wohngemeinschaften - WGs). The project came from a real coordination problem I ran into while living in a WG in Germany: who does the dishes this week? Who vacuums?

The goal was to automate that distribution fairly and expose it all through a well-structured API.

> This project is still actively being developed.

---

## What it does

- Register shared apartments and their residents
- Three cleaning modes to choose from when setting up a WG:
  - **Fixed** — one person is permanently responsible for each room
  - **Room Rotation** — responsibility for each room rotates among residents every week
  - **Task Distribution** — chores are spread across residents based on each task's difficulty weight
- Automatic scheduling via background jobs
- Residents can mark tasks or rooms as completed through a dedicated endpoint
- Cleaning history per room and per task — tracks who cleaned what and when, so nothing gets conveniently "forgotten"
- Email notifications via **Brevo API**:
  - New schedule summary sent to each resident when assignments change (listing all their rooms or tasks)
  - Mid-week reminder for residents who haven't completed their chores yet
  - Birthday reminders for WG members
- Authentication with JWT + refresh token rotation

---

## Stack

- **Java 21** + **Spring Boot 3** (MVC, Security, Data JPA)
- **MySQL 8** with migrations via **Flyway**
- **Docker** + **Docker Compose**
- **MapStruct** and **Lombok** to cut down on boilerplate
- API docs via **OpenAPI / Swagger UI**
- Tests with **JUnit 5**, **MockMvc** and **AssertJ**

---

## Running locally

The only requirement is having **Docker Desktop** installed (with WSL 2 on Windows).

```bash
# 1. Clone the repository
git clone https://github.com/lucasazevedolc/SweepShare.git
cd SweepShare

# 2. Set up environment variables
cp .env.example .env
# Fill in your local credentials in the .env file

# 3. Start everything
docker compose up --build
```

This single command compiles the app, starts the database, runs the migrations, and boots the server.

Once you see `Started SweepShareApplication` in the console, the API is live at:

- **Base URL:** http://localhost:8080/
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html

---

## Project structure

```
src/main/java/project/sweepshare/
│
├── config/         # Swagger and WebClient configuration
├── controller/     # REST endpoints
├── database/
│   ├── model/      # JPA entities
│   └── repository/ # Spring Data repositories
├── dto/            # Request/response objects
├── enums/          # Domain constants
├── exception/      # Global error handling
├── mapper/         # Entity <-> DTO conversions (MapStruct)
├── scheduler/      # Automatic scheduling jobs
├── security/       # JWT filters and Spring Security config
└── service/        # Business logic
```

---

*Developed by **Lucas Azevedo***