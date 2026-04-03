package com.realtime.producer.service;

import com.realtime.producer.dto.DashboardEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.dashboard-events}")
    private String topic;

    public String sendDashboardEvent(DashboardEvent event) {
        String eventId = UUID.randomUUID().toString();
        event.setEventId(eventId);
        
        String key = event.getEventType() + "-" + System.currentTimeMillis();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Dashboard event sent successfully: eventId={}, partition={}, offset={}",
                        eventId,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send dashboard event: eventId={}, error={}",
                        eventId, ex.getMessage(), ex);
            }
        });

        return eventId;
    }

    public void sendDashboardUpdate(String eventType, String customerName, String sportType, 
                                    String status, Double amount, Long totalBookings, 
                                    Double totalRevenue, String action) {
        DashboardEvent event = DashboardEvent.builder()
                .eventType(eventType)
                .customerName(customerName)
                .sportType(sportType)
                .status(status)
                .amount(amount)
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue)
                .action(action)
                .build();

        sendDashboardEvent(event);
    }
}
