package com.velotix.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/events/{id}")
    Map<String, Object> getEventById(@PathVariable("id") Long id);

    @PutMapping("/events/{id}/decrease")
    Map<String, Object> decreaseSeats(@PathVariable("id") Long id, @RequestParam("qty") int qty);

}
