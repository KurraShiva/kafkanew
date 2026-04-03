package com.realtime.consumer.service;

import com.realtime.consumer.dto.DashboardEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "${kafka.topic.dashboard-events}",
            groupId = "${spring.kafka.consumer.dashboard-group}",
            containerFactory = "dashboardKafkaListenerContainerFactory"
    )
    public void consume(DashboardEvent event, Acknowledgment acknowledgment) {
        log.info("DashboardConsumer: Received dashboard event: eventId={}, eventType={}, action={}",
                event.getEventId(), event.getEventType(), event.getAction());

        try {
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("eventId", event.getEventId());
            dashboardData.put("eventType", event.getEventType());
            dashboardData.put("customerName", event.getCustomerName());
            dashboardData.put("sportType", event.getSportType());
            dashboardData.put("status", event.getStatus());
            dashboardData.put("amount", event.getAmount());
            dashboardData.put("totalBookings", event.getTotalBookings());
            dashboardData.put("totalRevenue", event.getTotalRevenue());
            dashboardData.put("action", event.getAction());
            dashboardData.put("timestamp", event.getTimestamp());
            
            messagingTemplate.convertAndSend("/topic/dashboard", dashboardData);
            log.info("DashboardConsumer: Dashboard update broadcast to WebSocket: eventId={}", event.getEventId());

            acknowledgment.acknowledge();
            log.info("DashboardConsumer: Message acknowledged: eventId={}", event.getEventId());

        } catch (Exception e) {
            log.error("DashboardConsumer: Error processing dashboard event: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }
}
