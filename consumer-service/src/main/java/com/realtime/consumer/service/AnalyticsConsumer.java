package com.realtime.consumer.service;

import com.realtime.consumer.dto.BookingEvent;
import com.realtime.consumer.dto.DashboardEvent;
import com.realtime.consumer.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumer {

    private final BookingRepository bookingRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.dashboard-events}")
    private String dashboardTopic;

    @KafkaListener(
            topics = "${kafka.topic.booking-events}",
            groupId = "${spring.kafka.consumer.analytics-group}",
            containerFactory = "analyticsKafkaListenerContainerFactory"
    )
    @Transactional(readOnly = true)
    public void consume(BookingEvent event, Acknowledgment acknowledgment) {
        log.info("AnalyticsConsumer: Processing analytics for eventId={}, customer={}",
                event.getEventId(), event.getCustomerName());

        try {
            long totalBookings = bookingRepository.count();
            double totalRevenue = bookingRepository.findAll().stream()
                    .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                    .sum();

            DashboardEvent dashboardEvent = DashboardEvent.builder()
                    .eventId(event.getEventId())
                    .eventType("ANALYTICS_UPDATE")
                    .customerName(event.getCustomerName())
                    .sportType(event.getSportType())
                    .amount(event.getAmount())
                    .status(event.getStatus())
                    .totalBookings(totalBookings)
                    .totalRevenue(totalRevenue)
                    .action("BOOKING_CREATED")
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(dashboardTopic, "analytics-" + System.currentTimeMillis(), dashboardEvent);
            log.info("AnalyticsConsumer: Dashboard update sent to Kafka: totalBookings={}, totalRevenue={}",
                    totalBookings, totalRevenue);

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("AnalyticsConsumer: Error processing analytics: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
        }
    }
}
