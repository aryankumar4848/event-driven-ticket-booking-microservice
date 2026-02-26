# Velotix 🎟️ - A Microservice-based Event Management & Ticket Booking System

A simple Spring Boot microservices project that demonstrates service discovery, inter-service communication, database-backed CRUD, Swagger documentation, and Docker Compose deployment.

***

## 1. Project Overview

This project is an event management and ticket booking system built using a microservices architecture.  
It consists of four Spring Boot services:

- **Eureka Server** – service registry.  
- **Event Service** – manages events and seat availability (CRUD + PostgreSQL).  
- **Booking Service** – handles ticket booking and talks to Event + Notification services via Feign.  
- **Notification Service** – logs booking confirmations (and can be extended to send emails/SMS).

The goal is educational: to showcase core microservice concepts with minimal complexity, suitable for a college lab/demo.

***

## 2. Features & Use Case

- **Event management with DB (CRUD):**
  - Create, read, update, and delete events.
  - Store event details and real-time `availableSeats` in PostgreSQL.
- **Ticket booking flow:**
  - Booking Service fetches event details from Event Service via Feign.
  - Validates requested quantity against `availableSeats`.
  - Calls Event Service to decrease seats.
- **Notification flow:**
  - After a successful booking, Booking Service calls Notification Service.
  - Notification Service logs a confirmation message (can be extended to real email/SMS).
- **Service discovery & communication:**
  - All services register with Eureka.
  - Service-to-service calls use application names instead of hard-coded URLs.
- **API documentation:**
  - Swagger UI for Event and Booking services.
- **Containerized deployment:**
  - All services + PostgreSQL run via `docker compose up` for easy demo.

**Typical use case:**  
Open Eureka to see registered services, then use Swagger UI or curl to create events, book tickets, see seat counts decrease, and view notification logs.

***

## 3. Endpoints & Testing

### 3.1 Event Service (port 8081 from host)

Base URL: `http://localhost:8081`

- **Swagger UI:**  
  `http://localhost:8081/swagger-ui.html`

- **Get all events**  
  `GET /events`  
  Example (browser or curl):  
  ```bash
  curl http://localhost:8081/events
  ```

- **Get event by ID**  
  `GET /events/{id}`  
  ```bash
  curl http://localhost:8081/events/1
  ```

- **Create event**  
  `POST /events`  
  Body (JSON):  
  ```json
  {
    "eventName": "Sample Event",
    "venue": "College Auditorium",
    "date": "2025-12-01",
    "price": 100,
    "totalSeats": 50,
    "availableSeats": 50
  }
  ```
  Curl (Windows one line):  
  ```bash
  curl -X POST "http://localhost:8081/events" -H "Content-Type: application/json" -d "{\"eventName\":\"Sample Event\",\"venue\":\"College Auditorium\",\"date\":\"2025-12-01\",\"price\":100,\"totalSeats\":50,\"availableSeats\":50}"
  ```

- **Update full event**  
  `PUT /events/{id}`  
  Body: same structure as create.

- **Delete event by ID**  
  `DELETE /events/{id}`

- **Delete all events**  
  `DELETE /events`

- **Decrease seats (used by Booking Service)**  
  `PUT /events/{id}/decrease?qty={n}`  
  ```bash
  curl -X PUT "http://localhost:8081/events/1/decrease?qty=2"
  ```

***

### 3.2 Booking Service (port 8082 from host)

Base URL: `http://localhost:8082`  
Swagger UI: `http://localhost:8082/swagger-ui.html`

- **Create booking (main flow)**  
  `POST /bookings`  
  Body:  
  ```json
  {
    "eventId": 1,
    "quantity": 2
  }
  ```
  Curl (Windows):  
  ```bash
  curl -X POST "http://localhost:8082/bookings" -H "Content-Type: application/json" -d "{\"eventId\":1,\"quantity\":2}"
  ```
  Behaviour:
  - Calls Event Service to fetch event.
  - Validates seats and decreases `availableSeats`.
  - Triggers Notification Service.
  - Returns updated event JSON with new `availableSeats`.

*(Optional, if implemented)*

- **Get all bookings**  
  `GET /bookings`

- **Get booking by ID**  
  `GET /bookings/{id}`

***

### 3.3 Notification Service (port 8083 from host)

Base URL: `http://localhost:8083`

- **Log notification**  
  `POST /notifications`  
  Example body:  
  ```json
  {
    "email": "your-email@example.com",
    "eventName": "Sample Event",
    "quantity": 2
  }
  ```
  You should see a log like:  
  `Notification sent to your-email@example.com: Booking confirmed for event 'Sample Event', quantity: 2`

(For the lab, this service is usually tested indirectly via a booking.)

***

### 3.4 Eureka Server (port 8761 from host)

- **Dashboard:** `http://localhost:8761`  
  Shows `eureka-server`, `event-service`, `booking-service`, `notification-service` as registered instances.

***

## 4. Tools & Technologies

- **Language & Frameworks**
  - Java 17  
  - Spring Boot (Web, Data JPA, Actuator)  
  - Spring Cloud Netflix Eureka (server + client)  
  - Spring Cloud OpenFeign (inter-service REST calls)

- **Database**
  - PostgreSQL (event data, seat availability)

- **API Documentation**
  - springdoc-openapi + Swagger UI

- **Containerization & Orchestration**
  - Docker  
  - Docker Compose

- **Build & VCS**
  - Maven 
  - Git & GitHub

***

## 5. How to Clone & Run

### 5.1 Prerequisites

- Git  
- Docker & Docker Compose installed  
- (Optional) Java 17 + Maven if you want to run without Docker or rebuild locally.

### 5.2 Clone the repository

### 5.3 Run with Docker (recommended for demo)

From the project root (where `docker-compose.yml` is):

```bash
docker compose up --build
```

This will:

- Build images for:
  - `eureka-server`
  - `event-service`
  - `booking-service`
  - `notification-service`
- Start a PostgreSQL database.
- Expose ports:
  - Eureka: `localhost:8761`
  - Event Service: `localhost:8081`
  - Booking Service: `localhost:8082`
  - Notification Service: `localhost:8083`

Wait until logs show all services started, then:

- Open **Eureka**: `http://localhost:8761`
- Open **Event Swagger**: `http://localhost:8081/swagger-ui.html`
- Open **Booking Swagger**: `http://localhost:8082/swagger-ui.html`
- Create an event via Event Swagger.
- Create a booking via Booking Swagger and observe:
  - `availableSeats` decreases in Event Service.
  - Notification log appears in `notification-service` container logs.

To stop:

```bash
docker compose down
```
<img width="1816" height="756" alt="image" src="https://github.com/user-attachments/assets/e6e95199-d145-4e2d-af57-93e8ef08d7ac" />
<img width="1765" height="545" alt="image" src="https://github.com/user-attachments/assets/fb13e6db-92a5-4241-96d9-7bd4fe8bb4db" />
<img width="1810" height="479" alt="image" src="https://github.com/user-attachments/assets/4142150b-e33e-4970-9c11-b9350e100bda" />

## If you have any doubts or want help extending this project further, feel free to reach out anytime 💻
