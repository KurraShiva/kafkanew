package com.realtime.producer.controller;

import com.realtime.producer.dto.NotificationEvent;
import com.realtime.producer.service.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationProducer notificationProducer;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody NotificationEvent event) {
        log.info("Received notification request: notificationType={}", event.getNotificationType());

        event.setTimestamp(LocalDateTime.now());

        String eventId = notificationProducer.sendNotificationEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Notification event sent to Kafka");
        response.put("eventId", eventId);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/booking")
    public ResponseEntity<Map<String, Object>> sendBookingNotification(
            @RequestParam String recipient,
            @RequestParam String customerName,
            @RequestParam String sportType,
            @RequestParam String venue,
            @RequestParam Double amount,
            @RequestParam(defaultValue = "HIGH") String priority) {

        log.info("Received booking notification request: customer={}", customerName);

        notificationProducer.sendBookingNotification(recipient, customerName, sportType, venue, amount, priority);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Booking notification sent to Kafka");
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
