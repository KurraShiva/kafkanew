package com.realtime.consumer.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtime.consumer.dto.PaymentEvent;
import com.realtime.consumer.entity.Booking;
import com.realtime.consumer.repository.BookingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final BookingRepository bookingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.dashboard-events}")
    private String dashboardTopic;
    
    @Value("${kafka.topic.notification-events}")
    private String notificationTopic;

    @KafkaListener(
            topics = "${kafka.topic.payment-events}",
            groupId = "${spring.kafka.consumer.payment-group}",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(PaymentEvent event, Acknowledgment acknowledgment) {
        log.info("PaymentConsumer: Processing payment event: eventId={}, bookingId={}, status={}",
                event.getEventId(), event.getBookingEventId(), event.getStatus());

        try {
            Optional<Booking> bookingOpt = bookingRepository.findByEventId(event.getBookingEventId());
            
            if (bookingOpt.isEmpty()) {
                log.error("PaymentConsumer: Booking not found for eventId: {}", event.getBookingEventId());
                acknowledgment.acknowledge();
                return;
            }
            
            Booking booking = bookingOpt.get();
            
            if ("SUCCEEDED".equals(event.getStatus())) {
                booking.setStatus("CONFIRMED");
                booking.setPaymentStatus("PAID");
                booking.setProcessedAt(LocalDateTime.now());
                booking.setPaymentIntentId(event.getPaymentIntentId());
                bookingRepository.save(booking);
                
                log.info("PaymentConsumer: Booking confirmed: id={}, eventId={}", 
                        booking.getId(), booking.getEventId());
                
                // Send confirmation notification via WebSocket
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "BOOKING_CONFIRMED");
                notification.put("bookingId", booking.getId());
                notification.put("customerName", booking.getCustomerName());
                notification.put("sportType", booking.getSportType());
                notification.put("venue", booking.getVenue());
                notification.put("amount", booking.getAmount());
                notification.put("message", String.format("Your booking for %s at %s has been confirmed!", 
                        booking.getSportType(), booking.getVenue()));
                notification.put("timestamp", LocalDateTime.now());
                
                messagingTemplate.convertAndSend("/topic/notifications", notification);
                messagingTemplate.convertAndSend("/topic/bookings", Map.of(
                    "type", "BOOKING_CONFIRMED",
                    "booking", booking
                ));
                
                // Update dashboard stats
                updateDashboardStats();
                
            } else if ("FAILED".equals(event.getStatus())) {
                booking.setStatus("PAYMENT_FAILED");
                booking.setPaymentStatus("FAILED");
                booking.setErrorMessage(event.getErrorMessage());
                booking.setProcessedAt(LocalDateTime.now());
                bookingRepository.save(booking);
                
                log.warn("PaymentConsumer: Payment failed for booking: id={}, error={}", 
                        booking.getId(), event.getErrorMessage());
                
                // Send failure notification
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "PAYMENT_FAILED");
                notification.put("bookingId", booking.getId());
                notification.put("error", event.getErrorMessage());
                notification.put("message", "Payment failed. Please try again.");
                notification.put("timestamp", LocalDateTime.now());
                
                messagingTemplate.convertAndSend("/topic/notifications", notification);
                messagingTemplate.convertAndSend("/topic/bookings", Map.of(
                    "type", "PAYMENT_FAILED",
                    "booking", booking
                ));
            }
            
            acknowledgment.acknowledge();
            log.info("PaymentConsumer: Payment event processed successfully: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("PaymentConsumer: Error processing payment event: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }
    
    private void updateDashboardStats() {
        try {
            long totalBookings = bookingRepository.count();
            long confirmedBookings = bookingRepository.findByStatus("CONFIRMED").size();
            double totalRevenue = bookingRepository.findByStatus("CONFIRMED").stream()
                    .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                    .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBookings", totalBookings);
            stats.put("confirmedBookings", confirmedBookings);
            stats.put("totalRevenue", totalRevenue);
            stats.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/dashboard-stats", stats);
            
            // Send to Kafka for analytics
            kafkaTemplate.send(dashboardTopic, "stats-update", stats);
            
        } catch (Exception e) {
            log.error("Error updating dashboard stats", e);
        }
    }
}