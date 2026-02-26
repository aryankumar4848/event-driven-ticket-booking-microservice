package com.velotix.booking_service.client;

public class NotificationRequest {

    private String email;
    private String eventName;
    private int quantity;

    public NotificationRequest() {
    }

    public NotificationRequest(String email, String eventName, int quantity) {
        this.email = email;
        this.eventName = eventName;
        this.quantity = quantity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
