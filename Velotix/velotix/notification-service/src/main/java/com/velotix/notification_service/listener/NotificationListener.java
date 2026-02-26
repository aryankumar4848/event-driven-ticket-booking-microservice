package com.velotix.notification_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotificationListener {

    @RabbitListener(queues = "notification_queue")
    public void receiveNotification(Map<String, Object> notificationRequest) {
        String email = (String) notificationRequest.get("email");
        String eventName = (String) notificationRequest.get("eventName");
        Object quantityObj = notificationRequest.get("quantity");

        int quantity = (quantityObj instanceof Integer) ? (Integer) quantityObj
                : Integer.parseInt(quantityObj.toString());

        System.out.println("Processing Notification via RabbitMQ...");
        System.out.println("Notification sent to " + email + ": Booking confirmed for event '" + eventName
                + "', quantity: " + quantity);
    }
}
