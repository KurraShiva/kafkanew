package com.realtime.producer.service;

import com.realtime.producer.dto.NotificationEvent;
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
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.notification-events}")
    private String topic;

    public String sendNotificationEvent(NotificationEvent event) {
        String eventId = UUID.randomUUID().toString();
        event.setEventId(eventId);
        
        String key = event.getNotificationType() + "-" + System.currentTimeMillis();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification event sent successfully: eventId={}, partition={}, offset={}",
                        eventId,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send notification event: eventId={}, error={}",
                        eventId, ex.getMessage(), ex);
            }
        });

        return eventId;
    }

    public void sendBookingNotification(String recipient, String customerName, String sportType,
                                         String venue, Double amount, String priority) {
        NotificationEvent event = NotificationEvent.builder()
                .notificationType("BOOKING_CONFIRMATION")
                .recipient(recipient)
                .customerName(customerName)
                .sportType(sportType)
                .venue(venue)
                .amount(amount)
                .priority(priority != null ? priority : "HIGH")
                .message(String.format("New booking: %s booked %s at %s for Rs.%.2f",
                        customerName, sportType, venue, amount))
                .build();

        sendNotificationEvent(event);
    }

    public void sendDashboardNotification(String recipient, String message, String priority) {
        NotificationEvent event = NotificationEvent.builder()
                .notificationType("DASHBOARD_UPDATE")
                .recipient(recipient)
                .message(message)
                .priority(priority != null ? priority : "MEDIUM")
                .build();

        sendNotificationEvent(event);
    }
}
