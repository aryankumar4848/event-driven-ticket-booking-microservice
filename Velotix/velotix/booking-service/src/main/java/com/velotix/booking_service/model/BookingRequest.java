package com.velotix.booking_service.model;

public class BookingRequest {
    private Long eventId;
    private int quantity;
    // add getters and setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long id) { this.eventId = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
