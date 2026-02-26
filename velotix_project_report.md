# Velotix: Microservice-based Event Management & Ticket Booking System

## Project Overview

Velotix is a microservice-based event management and ticket booking system designed to demonstrate modern distributed system architecture using Spring Boot and Spring Cloud. The project implements a scalable backend that allows users to browse events, book tickets, and receive booking confirmations. The system solves the problem of monolithic event management platforms becoming bottlenecks by adopting a microservices architecture where each service handles a distinct responsibility—event management, booking logic, and notifications—allowing independent scaling, deployment, and maintenance. This project is built for a microservices lab to showcase practical implementation of service discovery, inter-service communication, containerization, and API gateway patterns.

## Implementation Approach

The project was implemented incrementally using Spring Boot 3.x and Spring Cloud frameworks, starting with individual microservices developed and tested locally on separate ports, followed by integration with service discovery and finally containerization using Docker and Docker Compose. The development workflow involved: (1) Creating each service as an independent Maven module with its own dependencies and configurations; (2) Setting up Eureka Server for service registry; (3) Implementing synchronous REST-based communication between services using Spring Cloud OpenFeign; (4) Adding an API Gateway to provide a single entry point and route traffic to services; (5) Configuring each service's `application.properties` to register with Eureka and connect to required databases or services; (6) Creating Dockerfiles for each service and a docker-compose.yml to orchestrate all containers; and (7) Testing the entire flow via Postman to verify end-to-end functionality. This approach allowed us to validate each component independently before integrating them into the full system.

## Microservices Architecture

The Velotix system comprises four core microservices plus supporting infrastructure. The **Event Service** (port 8081) manages event details including name, venue, date, pricing, and seat availability; it connects to a PostgreSQL database and provides REST endpoints for CRUD operations and seat deduction. The **Booking Service** (port 8082) handles ticket booking requests, using Spring Cloud Feign to call the Event Service to validate seat availability and reduce seat counts, then triggering the Notification Service to send confirmations. The **Notification Service** (port 8083) provides a simple endpoint that logs booking confirmations to the console (or can be extended for email/SMS); it is stateless and does not require a database. The **API Gateway** (port 8080) serves as the single entry point for all client requests, using Spring Cloud Gateway to automatically discover services registered with Eureka and route requests based on URL paths (e.g., `/event-service/**` routes to Event Service). Additionally, **Eureka Server** (port 8761) acts as the service registry, allowing all services to register themselves and discover each other dynamically at runtime.

## Project Structure

The project is organized as a Maven parent-child module structure with a root `pom.xml` managing common Spring Boot and Spring Cloud dependencies. Each microservice resides in its own folder (`eureka-server`, `event-service`, `booking-service`, `notification-service`, `api-gateway`) containing its own `pom.xml` with service-specific dependencies. Within each service, Java classes are organized into logical packages: `model` (JPA entities and DTOs), `repository` (Spring Data JPA interfaces for database access), `service` (business logic layer), and `controller` (REST endpoints). The Event Service additionally includes database configuration in `application.properties` pointing to PostgreSQL. At the project root, `docker-compose.yml` orchestrates all containers and a `Dockerfile` in each service module enables containerization. This modular structure ensures clear separation of concerns, independent deployments, and easy scalability.

## Eureka Service Discovery

Eureka Server acts as the central service registry in Velotix, enabling dynamic service discovery without hardcoding IP addresses or ports. Each microservice registers itself with Eureka on startup by including the `spring-cloud-starter-netflix-eureka-client` dependency and configuring `eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/` in its `application.properties`. The Booking Service and API Gateway use this registry to resolve service locations at runtime—for example, when Booking Service needs to call Event Service via Feign, it looks up "event-service" in Eureka rather than using a hardcoded URL, providing resilience against service restarts or relocations. The Eureka dashboard (accessible at `http://localhost:8761`) provides visual confirmation that all services are UP and running, displaying their instance IDs, availability zones, and health status. This dynamic discovery pattern allows the system to scale horizontally: if multiple instances of Event Service are deployed, Eureka tracks all of them and the gateway can load-balance requests across them automatically.

## API Gateway Role and Routing

The API Gateway in Velotix provides a unified entry point for all client requests and handles intelligent routing to backend microservices. Clients send all HTTP requests to the gateway on port 8080 using service-aware paths (e.g., `http://localhost:8080/event-service/events`), which shields clients from knowing individual service ports or locations. The gateway is configured with `spring.cloud.gateway.discovery.locator.enabled=true`, which enables automatic route discovery from Eureka: service names are converted to route prefixes (Event Service becomes `/event-service/**`, Booking Service becomes `/booking-service/**`), eliminating the need for hardcoded route configurations. When a request arrives, the gateway queries Eureka to locate the target service, forwards the request, and returns the response to the client. This architecture decouples clients from service topology, enables centralized request/response handling for cross-cutting concerns like logging and authentication, and simplifies client code—clients only need to know the gateway URL, not every individual service endpoint.

## Docker and Containerization

All microservices and supporting infrastructure are containerized using Docker to ensure consistency, portability, and easy deployment. Each service folder contains an identical, minimal `Dockerfile` that builds a Java 17 Alpine image, copies the service's compiled JAR file, and runs it. The root `docker-compose.yml` file orchestrates all containers: it defines services for PostgreSQL (database), Eureka Server, Event Service, Booking Service, Notification Service, and API Gateway, with explicit `depends_on` directives to control startup order (e.g., Event Service waits for PostgreSQL and Eureka Server to be ready). Container-to-container communication is enabled via Docker's default network, using service names (e.g., `eureka-server`, `postgres`) instead of `localhost`. Environment variables in compose override local properties, enabling separate configurations for local development vs. containerized deployment (e.g., `spring.datasource.url=jdbc:postgresql://postgres:5432/eventdb` uses the Postgres container name). The full stack is launched with a single command: `mvn clean package -DskipTests && docker compose up --build`, demonstrating the convenience and reproducibility of containerized microservices.

## Results and Testing

The complete Velotix system was successfully implemented and tested end-to-end, with all four microservices plus Eureka Server and API Gateway registering correctly and communicating seamlessly. Testing was performed via Postman using the API Gateway as the entry point, demonstrating the full booking workflow: (1) **Create Event** – POST to `http://localhost:8080/event-service/events` with event details, successfully persisting data to PostgreSQL and returning an `eventId`; (2) **List Events** – GET to `http://localhost:8080/event-service/events` returning all stored events; (3) **Book Tickets** – POST to `http://localhost:8080/booking-service/bookings` with `eventId` and quantity, which internally calls Event Service to validate and reduce available seats, then triggers Notification Service to log a confirmation message; (4) **Update Event** – PUT to `http://localhost:8080/event-service/events/{id}` to modify event details; (5) **Delete Event** – DELETE to `http://localhost:8080/event-service/events/{id}` to remove an event. The Eureka dashboard at `http://localhost:8761` confirmed all services registered and healthy, demonstrating successful service discovery. Docker logs showed Notification Service console outputs confirming message dispatch. The system demonstrates core microservices concepts: service discovery and registration via Eureka, inter-service communication via REST and Feign, API Gateway routing and load balancing, containerization with Docker, and database-backed persistence for event data.

## Key Endpoints and Usage

The following endpoints are available through the API Gateway (`http://localhost:8080`):

- **Event Service:**
  - `GET /event-service/events` – List all events
  - `GET /event-service/events/{id}` – Get event by ID
  - `POST /event-service/events` – Create new event
  - `PUT /event-service/events/{id}` – Update event
  - `PUT /event-service/events/{id}/decrease?qty=N` – Reduce available seats
  - `DELETE /event-service/events/{id}` – Delete event

- **Booking Service:**
  - `POST /booking-service/bookings` – Book tickets for an event

- **Notification Service:**
  - `POST /notification-service/notifications` – Send notification (internal use)

- **Service Health & Discovery:**
  - Eureka UI: `http://localhost:8761` – View registered services and health status
  - Swagger UI (Event Service): `http://localhost:8080/event-service/swagger-ui.html`
  - Swagger UI (Booking Service): `http://localhost:8080/booking-service/swagger-ui.html`

All endpoints accept and return JSON payloads, with the gateway transparently routing requests to the appropriate microservice based on the URL path prefix. This unified interface simplifies client interactions while maintaining the benefits of a distributed, scalable backend architecture.