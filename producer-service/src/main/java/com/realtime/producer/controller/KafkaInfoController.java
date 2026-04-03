package com.realtime.producer.controller;

import com.realtime.producer.config.KafkaProducerConfig.KafkaAdminInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaInfoController {

    private final KafkaAdminInfo kafkaAdminInfo;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getKafkaInfo() {
        Map<String, Object> info = kafkaAdminInfo.getClusterInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> getTopics() {
        Map<String, Object> info = kafkaAdminInfo.getClusterInfo();
        Map<String, Object> topics = new HashMap<>();
        if (info.containsKey("topics")) {
            topics.put("topics", info.get("topics"));
        }
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/booking-topic")
    public ResponseEntity<Map<String, Object>> getBookingTopicInfo() {
        Map<String, Object> info = kafkaAdminInfo.getClusterInfo();
        Map<String, Object> bookingTopic = new HashMap<>();
        if (info.containsKey("bookingTopic")) {
            bookingTopic.put("bookingTopic", info.get("bookingTopic"));
        }
        return ResponseEntity.ok(bookingTopic);
    }

    @GetMapping("/notification-topic")
    public ResponseEntity<Map<String, Object>> getNotificationTopicInfo() {
        Map<String, Object> info = kafkaAdminInfo.getClusterInfo();
        Map<String, Object> notificationTopic = new HashMap<>();
        if (info.containsKey("notificationTopic")) {
            notificationTopic.put("notificationTopic", info.get("notificationTopic"));
        }
        return ResponseEntity.ok(notificationTopic);
    }

    @GetMapping("/dashboard-topic")
    public ResponseEntity<Map<String, Object>> getDashboardTopicInfo() {
        Map<String, Object> info = kafkaAdminInfo.getClusterInfo();
        Map<String, Object> dashboardTopic = new HashMap<>();
        if (info.containsKey("dashboardTopic")) {
            dashboardTopic.put("dashboardTopic", info.get("dashboardTopic"));
        }
        return ResponseEntity.ok(dashboardTopic);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "producer-service");
        return ResponseEntity.ok(health);
    }
}
