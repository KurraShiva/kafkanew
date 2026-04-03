//package com.realtime.consumer.service;
//
//import com.realtime.consumer.dto.BookingEvent;
//import com.realtime.consumer.entity.Booking;
//import com.realtime.consumer.repository.BookingRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BookingConsumer {
//
//    private final BookingRepository bookingRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @KafkaListener(
//            topics = "${kafka.topic.booking-events}",
//            groupId = "${spring.kafka.consumer.booking-group}",
//            containerFactory = "bookingKafkaListenerContainerFactory"
//    )
//    @Transactional
//    public void consume(BookingEvent event, Acknowledgment acknowledgment) {
//        log.info("BookingConsumer: Received booking event: eventId={}, customer={}, sportType={}, venue={}",
//                event.getEventId(), event.getCustomerName(), event.getSportType(), event.getVenue());
//
//        try {
//            if (bookingRepository.existsByEventId(event.getEventId())) {
//                log.warn("BookingConsumer: Duplicate event detected, skipping: eventId={}", event.getEventId());
//                acknowledgment.acknowledge();
//                return;
//            }
//
//            Booking booking = mapToEntity(event);
//            booking.setProcessedAt(LocalDateTime.now());
//            booking.setStatus("PENDING");
//            
//            Booking savedBooking = bookingRepository.save(booking);
//            
//            log.info("BookingConsumer: Booking saved to database: id={}, eventId={}, status={}", 
//                    savedBooking.getId(), savedBooking.getEventId(), savedBooking.getStatus());
//
//            messagingTemplate.convertAndSend("/topic/bookings", savedBooking);
//            log.info("BookingConsumer: Booking broadcast to WebSocket: eventId={}", event.getEventId());
//
//            acknowledgment.acknowledge();
//            log.info("BookingConsumer: Message acknowledged: eventId={}", event.getEventId());
//
//        } catch (Exception e) {
//            log.error("BookingConsumer: Error processing booking event: eventId={}, error={}",
//                    event.getEventId(), e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    private Booking mapToEntity(BookingEvent event) {
//        return Booking.builder()
//                .eventId(event.getEventId())
//                .customerName(event.getCustomerName())
//                .sportType(event.getSportType())
//                .venue(event.getVenue())
//                .slotDateTime(event.getSlotDateTime())
//                .durationHours(event.getDurationHours())
//                .amount(event.getAmount())
//                .status(event.getStatus())
//                .createdAt(event.getCreatedAt())
//                .message(event.getMessage())
//                .build();
//    }
//}



package com.realtime.consumer.service;

import com.realtime.consumer.dto.BookingEvent;
import com.realtime.consumer.entity.Booking;
import com.realtime.consumer.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingConsumer {

    private final BookingRepository bookingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.dashboard-events}")
    private String dashboardTopic;

    @KafkaListener(
            topics = "${kafka.topic.booking-events}",
            groupId = "${spring.kafka.consumer.booking-group}",
            containerFactory = "bookingKafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(BookingEvent event, Acknowledgment acknowledgment) {
        log.info("BookingConsumer: Received booking event: eventId={}, customer={}, sportType={}, venue={}",
                event.getEventId(), event.getCustomerName(), event.getSportType(), event.getVenue());

        try {
            Booking savedBooking = saveBookingIdempotently(event);
            
            if (savedBooking != null) {
                log.info("BookingConsumer: Booking saved to database: id={}, eventId={}, status={}", 
                        savedBooking.getId(), savedBooking.getEventId(), savedBooking.getStatus());

                Map<String, Object> broadcast = new HashMap<>();
                broadcast.put("type", "NEW_BOOKING");
                broadcast.put("booking", savedBooking);
                broadcast.put("timestamp", LocalDateTime.now());
                messagingTemplate.convertAndSend("/topic/bookings", broadcast);
                
                log.info("BookingConsumer: New booking broadcast to WebSocket: eventId={}", event.getEventId());
                updateDashboardStats();
            } else {
                log.info("BookingConsumer: Booking already exists, skipping broadcast: eventId={}", event.getEventId());
            }

            acknowledgment.acknowledge();
            log.info("BookingConsumer: Message acknowledged: eventId={}", event.getEventId());

        } catch (Exception e) {
            log.error("BookingConsumer: Error processing booking event: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }
    
    private Booking saveBookingIdempotently(BookingEvent event) {
        if (bookingRepository.existsByEventId(event.getEventId())) {
            log.warn("BookingConsumer: Booking already exists, skipping: eventId={}", event.getEventId());
            return null;
        }
        
        try {
            Booking booking = mapToEntity(event);
            booking.setProcessedAt(LocalDateTime.now());
            booking.setPaymentStatus("PENDING");
            return bookingRepository.save(booking);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                log.warn("BookingConsumer: Duplicate entry detected (race condition), fetching existing: eventId={}", event.getEventId());
                return bookingRepository.findByEventId(event.getEventId()).orElse(null);
            }
            throw e;
        }
    }
    
    private void updateDashboardStats() {
        try {
            long totalBookings = bookingRepository.count();
            long pendingPayments = bookingRepository.findByStatus("PENDING_PAYMENT").size();
            long confirmedBookings = bookingRepository.findByStatus("CONFIRMED").size();
            double totalRevenue = bookingRepository.findByStatus("CONFIRMED").stream()
                    .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                    .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBookings", totalBookings);
            stats.put("pendingPayments", pendingPayments);
            stats.put("confirmedBookings", confirmedBookings);
            stats.put("totalRevenue", totalRevenue);
            stats.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/dashboard-stats", stats);
            
        } catch (Exception e) {
            log.error("Error updating dashboard stats", e);
        }
    }

    private Booking mapToEntity(BookingEvent event) {
        return Booking.builder()
                .eventId(event.getEventId())
                .customerName(event.getCustomerName())
                .sportType(event.getSportType())
                .venue(event.getVenue())
                .slotDateTime(event.getSlotDateTime())
                .durationHours(event.getDurationHours())
                .amount(event.getAmount())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .message(event.getMessage())
                .build();
    }
}