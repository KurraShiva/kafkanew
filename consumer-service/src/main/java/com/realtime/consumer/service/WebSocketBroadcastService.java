package com.realtime.consumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketBroadcastService {

    private final KafkaAdminService kafkaAdminService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedDelay = 5000)
    public void broadcastKafkaInfo() {
        try {
            Map<String, Object> clusterInfo = kafkaAdminService.getKafkaClusterInfo();
            messagingTemplate.convertAndSend("/topic/kafka-info", clusterInfo);
            log.debug("Broadcasted Kafka cluster info to WebSocket");
        } catch (Exception e) {
            log.error("Error broadcasting Kafka info", e);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void broadcastBookingUpdates() {
        try {
            Map<String, Object> topicsInfo = kafkaAdminService.getTopicsInfo();
            Map<String, Object> consumerGroupsInfo = kafkaAdminService.getConsumerGroupsInfo();
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("topics", topicsInfo);
            updates.put("consumerGroups", consumerGroupsInfo);
            
            messagingTemplate.convertAndSend("/topic/kafka-updates", updates);
            log.debug("Broadcasted Kafka updates to WebSocket");
        } catch (Exception e) {
            log.error("Error broadcasting Kafka updates", e);
        }
    }

    public void broadcastMessage(String topic, Object message) {
        messagingTemplate.convertAndSend(topic, message);
    }
}