package com.velotix.booking_service.controller;

import com.velotix.booking_service.exception.BookingException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.velotix.booking_service.model.BookingRequest;
import com.velotix.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookEvent(@RequestBody BookingRequest request) {
        try {
            return ResponseEntity.ok(bookingService.bookEvent(request));
        } catch (BookingException be) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(be.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Not enough seats available.");
        }
    }
}
