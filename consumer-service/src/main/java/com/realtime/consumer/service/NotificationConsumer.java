package com.realtime.consumer.service;

import com.realtime.consumer.dto.NotificationEvent;
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
public class NotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "${kafka.topic.notification-events}",
            groupId = "${spring.kafka.consumer.notification-group}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationEvent event, Acknowledgment acknowledgment) {
        log.info("NotificationConsumer: Received notification event: eventId={}, notificationType={}, priority={}",
                event.getEventId(), event.getNotificationType(), event.getPriority());

        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("eventId", event.getEventId());
            notification.put("notificationType", event.getNotificationType());
            notification.put("recipient", event.getRecipient());
            notification.put("message", event.getMessage());
            notification.put("priority", event.getPriority());
            notification.put("timestamp", event.getTimestamp());
            notification.put("customerName", event.getCustomerName());
            notification.put("sportType", event.getSportType());
            notification.put("venue", event.getVenue());
            notification.put("amount", event.getAmount());
            
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            log.info("NotificationConsumer: Notification broadcast to WebSocket: eventId={}", event.getEventId());

            acknowledgment.acknowledge();
            log.info("NotificationConsumer: Message acknowledged: eventId={}", event.getEventId());

        } catch (Exception e) {
            log.error("NotificationConsumer: Error processing notification event: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }
}
