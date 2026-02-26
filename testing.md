### 1\. Run All Services with Docker

Start all microservices from the project root (where `docker-compose.yml` is located):

```bash
docker compose up --build
```

**Wait for all services to start.** Confirm this when the logs show all components have started and the Eureka UI is accessible.

### 2\. Basic Health Checks (Browser)

1.  **Eureka Dashboard:**
    `http://localhost:8761`
2.  **Event Service through Gateway:**
    `http://localhost:8080/event-service/events`
---

### 3\. Full API Gateway Test Flow (Postman)

Use the actual `eventId` (e.g., `1`) returned after creating the first event for subsequent tests.

#### 3.1. Create an Event (C in CRUD)

  * **Method:** `POST`
  * **URL:** `http://localhost:8080/event-service/events`
  * **Headers:** `Content-Type: application/json`
  * **Body (raw JSON):**
    ```json
    {
      "eventId": 1,
      "eventName": "Sample Event",
      "venue": "College Auditorium",
      "date": "2025-12-01",
      "price": 100,
      "totalSeats": 50
    }
    ```

#### 3.2. List All Events (R in CRUD)

  * **Method:** `GET`
  * **URL:** `http://localhost:8080/event-service/events`
  * **Verification:** Check that the event created in step 3.1 appears in the list.

#### 3.3. Update an Event (U in CRUD)

  * **Method:** `PUT`
  * **URL:** `http://localhost:8080/event-service/events/1` (Use the actual `eventId`)
  * **Headers:** `Content-Type: application/json`
  * **Body (raw JSON):**
    ```json
    {
      "eventName": "Sample Event Updated",
      "venue": "Main Hall",
      "date": "2025-12-05",
      "price": 150,
      "totalSeats": 80,
      "availableSeats": 50
    }
    ```
  * **Verification:** Call `GET http://localhost:8080/event-service/events/1` to verify the changes (Name, Venue, Price).

#### 3.4. Decrease Seats (Event Service Specific Logic)

  * **Method:** `PUT`
  * **URL:** `http://localhost:8080/event-service/events/1/decrease?qty=5` (Use the actual `eventId`)
  * **Verification:** Call `GET http://localhost:8080/event-service/events/1` and confirm that `availableSeats` has reduced by 5.

#### 3.5. Book Tickets (Booking Service via Gateway)

  * **Method:** `POST`
  * **URL:** `http://localhost:8080/booking-service/bookings`
  * **Headers:** `Content-Type: application/json`
  * **Body (raw JSON):**
    ```json
    {
      "eventId": 1,
      "quantity": 2
    }
    ```
  * **Verification:**
      * This should trigger a successful booking response.
      * Check the logs for `notification-service` to confirm a notification message was triggered.
      * Call `GET http://localhost:8080/event-service/events/1` to confirm `availableSeats` has reduced by 2 more seats.

#### 3.6. Delete One Event (D in CRUD)

  * **Method:** `DELETE`
  * **URL:** `http://localhost:8080/event-service/events/1` (Use the actual `eventId`)
  * **Verification:**
      * Call `GET http://localhost:8080/event-service/events/1` (should return HTTP 404).
      * Call `GET http://localhost:8080/event-service/events` (should no longer list the event).

#### 3.7. Delete All Events

  * **Method:** `DELETE`
  * **URL:** `http://localhost:8080/event-service/events`
  * **Verification:** Call `GET http://localhost:8080/event-service/events` (should return an empty list `[]`).

   
#### 4. Stop the Services
When the demonstration is complete, stop all running containers and clean up the resources:

```bash
docker compose down
```
