In this project, each service registers with Eureka using its internal container address and port.

* The instance links shown in the **Eureka UI** simply point to the root URL of that service (e.g., `/`).
* However, these services only expose **REST endpoints** like `/events` or `/bookings`, and **no HTML page at the root**.
* This is why clicking the UI links returns a **Whitelabel Error Page** instead of a response.

This behavior is still completely correct, because Eureka’s main job is **service discovery** (keeping track of which services are **UP** and where they live) rather than providing user-friendly, clickable URLs for browsers. 

In a real system:

* Clients never call those raw instance links directly.
* Instead, they talk to the **API Gateway**, which uses **Eureka** to resolve service locations and then routes requests to the correct REST paths (e.g., `/event-service/events` or `/booking-service/bookings`).

Therefore, having the Eureka UI links "work" is not necessary as long as the gateway and Postman/Swagger calls work correctly.
