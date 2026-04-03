package com.realtime.producer.controller;

import com.realtime.producer.dto.DashboardEvent;
import com.realtime.producer.service.DashboardProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardProducer dashboardProducer;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendDashboardEvent(@RequestBody DashboardEvent event) {
        log.info("Received dashboard event request: eventType={}", event.getEventType());

        event.setTimestamp(LocalDateTime.now());

        String eventId = dashboardProducer.sendDashboardEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dashboard event sent to Kafka");
        response.put("eventId", eventId);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> sendDashboardUpdate(
            @RequestParam String eventType,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String sportType,
            @RequestParam String status,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) Long totalBookings,
            @RequestParam(required = false) Double totalRevenue,
            @RequestParam String action) {

        log.info("Received dashboard update request: eventType={}, action={}", eventType, action);

        dashboardProducer.sendDashboardUpdate(eventType, customerName, sportType, status, 
                amount, totalBookings, totalRevenue, action);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dashboard update sent to Kafka");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "producer-service");
        return ResponseEntity.ok(health);
    }
}
