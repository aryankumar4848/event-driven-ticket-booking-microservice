## Step 1: Start the Services

Open your terminal in the project root directory (where `docker-compose.yml` is located) and run the following command:

```bash
docker compose up
```

> **Note:** Use `docker compose up --build` **only** if you have modified the code, rebuilt the service JAR files, and need to refresh the Docker images.

-----

## Step 2: Verify and Access Endpoints

Once the services have started, you can access the following UIs to confirm health and documentation:

  * **Service Discovery (Eureka):** `http://localhost:8761`
      * **Goal:** Confirm the Event, Booking, and Notification services are registered.
  * **Event Service API (Swagger UI):** `http://localhost:8081/swagger-ui.html`
  * **Booking Service API (Swagger UI):** `http://localhost:8082/swagger-ui.html`

-----

## Step 3: Execute the Core Booking Transaction

This step demonstrates the end-to-end flow: **Booking Service** calls **Event Service** (to decrease seats) and then calls **Notification Service**.

### **Verification**

Crucially, check the console logs in the original terminal running `docker compose up`:

  * Confirm logs show the **Event Service** receiving and processing the seat decrease request.
  * Confirm logs show the **Notification Service** receiving the request and logging the notification "sent" (or simulated send).

-----

## Step 4: Stop the Services

When the demonstration is complete, stop all running containers and clean up the resources:

```bash
docker compose down
```
