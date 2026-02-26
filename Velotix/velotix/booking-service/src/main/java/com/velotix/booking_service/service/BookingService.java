package com.velotix.booking_service.service;

import com.velotix.booking_service.client.EventServiceClient;
import com.velotix.booking_service.client.NotificationRequest;
import com.velotix.booking_service.client.NotificationServiceClient;
import com.velotix.booking_service.model.BookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private EventServiceClient eventServiceClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Map<String, Object> bookEvent(BookingRequest request) {
        // 1. Get event details from Event Service
        Map<String, Object> event = eventServiceClient.getEventById(request.getEventId());

        // 2. Read availableSeats safely
        Object seatsObj = event.get("availableSeats");
        int availableSeats = (seatsObj instanceof Integer)
                ? (Integer) seatsObj
                : Integer.parseInt(seatsObj.toString());

        // 3. Simple validation
        if (availableSeats < request.getQuantity()) {
            throw new RuntimeException("Not enough seats available for booking.");
        }

        // 4. Decrease seats via Event Service
        Map<String, Object> updatedEvent = eventServiceClient.decreaseSeats(request.getEventId(),
                request.getQuantity());

        // 5. Send notification via Notification Service over RabbitMQ
        NotificationRequest notificationRequest = new NotificationRequest(
                "your-email@example.com", // change to your email if using email notifications
                updatedEvent.get("eventName").toString(),
                request.getQuantity());

        rabbitTemplate.convertAndSend(
                com.velotix.booking_service.config.RabbitMQConfig.EXCHANGE,
                com.velotix.booking_service.config.RabbitMQConfig.ROUTING_KEY,
                notificationRequest);

        // 6. Return updated event details as booking response
        return updatedEvent;
    }
}
