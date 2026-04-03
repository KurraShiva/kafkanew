package com.realtime.consumer.controller;

import com.realtime.consumer.dto.BookingEvent;
import com.realtime.consumer.entity.Booking;
import com.realtime.consumer.repository.BookingRepository;
import com.realtime.consumer.service.KafkaAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class BookingQueryController {

    private final BookingRepository bookingRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaAdminService kafkaAdminService;

    @Value("${kafka.topic.booking-events}")
    private String bookingTopic;

    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingEvent event) {
        log.info("Consumer Service: Received booking request: customer={}, sportType={}, venue={}",
                event.getCustomerName(), event.getSportType(), event.getVenue());

        event.setStatus("PENDING");
        event.setCreatedAt(LocalDateTime.now());

        kafkaTemplate.send(bookingTopic, event.getCustomerName(), event);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Booking event sent to Kafka");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        log.info("Retrieved all bookings: count={}", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bookings/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/customer/{customerName}")
    public ResponseEntity<List<Booking>> getBookingsByCustomer(@PathVariable String customerName) {
        List<Booking> bookings = bookingRepository.findByCustomerName(customerName);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/count")
    public ResponseEntity<Map<String, Long>> getBookingCount() {
        long count = bookingRepository.count();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookings/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "consumer-service");
        health.put("bookings", String.valueOf(bookingRepository.count()));
        return ResponseEntity.ok(health);
    }

//    @GetMapping("/bookings/dashboard")
//    public ResponseEntity<Map<String, Object>> getDashboardStats() {
//        List<Booking> allBookings = bookingRepository.findAll();
//        long totalBookings = allBookings.size();
//        long confirmedBookings = allBookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
//        long pendingBookings = allBookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
//        long cancelledBookings = allBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
//        double totalRevenue = allBookings.stream()
//                .filter(b -> b.getAmount() != null)
//                .mapToDouble(Booking::getAmount)
//                .sum();
//        
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("totalBookings", totalBookings);
//        stats.put("confirmedBookings", confirmedBookings);
//        stats.put("pendingBookings", pendingBookings);
//        stats.put("cancelledBookings", cancelledBookings);
//        stats.put("totalRevenue", totalRevenue);
//        stats.put("recentBookings", allBookings.stream().sorted((a, b) -> {
//            LocalDateTime aTime = a.getCreatedAt() != null ? a.getCreatedAt() : LocalDateTime.MIN;
//            LocalDateTime bTime = b.getCreatedAt() != null ? b.getCreatedAt() : LocalDateTime.MIN;
//            return bTime.compareTo(aTime);
//        }).limit(20).toList());
//        
//        return ResponseEntity.ok(stats);
//    }
    
    @GetMapping("/bookings/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        List<Booking> allBookings = bookingRepository.findAll();
        long totalBookings = allBookings.size();
        long confirmedBookings = allBookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long pendingBookings = allBookings.stream().filter(b -> "PENDING_PAYMENT".equals(b.getStatus()) || "PENDING".equals(b.getStatus())).count();
        long cancelledBookings = allBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
        long paymentFailedBookings = allBookings.stream().filter(b -> "PAYMENT_FAILED".equals(b.getStatus())).count();
        
        double totalRevenue = allBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()) && b.getAmount() != null)
                .mapToDouble(Booking::getAmount)
                .sum();
        
        // Sport breakdown
        Map<String, Long> sportBreakdown = allBookings.stream()
                .collect(Collectors.groupingBy(Booking::getSportType, Collectors.counting()));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", totalBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("paymentFailedBookings", paymentFailedBookings);
        stats.put("totalRevenue", totalRevenue);
        stats.put("sportBreakdown", sportBreakdown);
        stats.put("recentBookings", allBookings.stream()
                .sorted((a, b) -> {
                    LocalDateTime aTime = a.getCreatedAt() != null ? a.getCreatedAt() : LocalDateTime.MIN;
                    LocalDateTime bTime = b.getCreatedAt() != null ? b.getCreatedAt() : LocalDateTime.MIN;
                    return bTime.compareTo(aTime);
                })
                .limit(20)
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboardHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "consumer-service");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/notifications/health")
    public ResponseEntity<Map<String, String>> notificationsHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "consumer-service");
        return ResponseEntity.ok(health);
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        return bookingRepository.findById(id)
                .map(booking -> {
                    booking.setStatus(status);
                    booking.setProcessedAt(LocalDateTime.now());
                    bookingRepository.save(booking);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Booking status updated to " + status);
                    response.put("booking", booking);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/kafka/info")
    public ResponseEntity<Map<String, Object>> getKafkaInfo() {
        Map<String, Object> info = kafkaAdminService.getKafkaClusterInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/kafka/topics")
    public ResponseEntity<Map<String, Object>> getTopicsInfo() {
        Map<String, Object> topics = kafkaAdminService.getTopicsInfo();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/kafka/consumer-groups")
    public ResponseEntity<Map<String, Object>> getConsumerGroupsInfo() {
        Map<String, Object> groups = kafkaAdminService.getConsumerGroupsInfo();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/kafka/offsets/{topic}")
    public ResponseEntity<Map<String, Object>> getTopicOffsets(@PathVariable String topic) {
        Map<String, Object> offsets = kafkaAdminService.getTopicOffsets(topic);
        return ResponseEntity.ok(offsets);
    }

    @GetMapping("/kafka/consumer-group/{groupId}/offsets")
    public ResponseEntity<Map<String, Object>> getConsumerGroupOffsets(@PathVariable String groupId) {
        Map<String, Object> offsets = kafkaAdminService.getConsumerGroupOffsets(groupId);
        return ResponseEntity.ok(offsets);
    }

    @GetMapping("/kafka/all-offsets")
    public ResponseEntity<Map<String, Object>> getAllTopicsOffsets() {
        Map<String, Object> allOffsets = new HashMap<>();
        try {
            Map<String, Object> topics = kafkaAdminService.getTopicsInfo();
            for (String topicName : topics.keySet()) {
                allOffsets.put(topicName, kafkaAdminService.getTopicOffsets(topicName));
            }
        } catch (Exception e) {
            log.error("Error getting all offsets", e);
        }
        return ResponseEntity.ok(allOffsets);
    }

    @PostMapping("/bookings/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveBooking(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(booking -> {
                    booking.setStatus("CONFIRMED");
                    booking.setProcessedAt(java.time.LocalDateTime.now());
                    bookingRepository.save(booking);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Booking approved");
                    response.put("booking", booking);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bookings/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectBooking(@PathVariable Long id, 
            @RequestBody(required = false) Map<String, String> request) {
        return bookingRepository.findById(id)
                .map(booking -> {
                    booking.setStatus("CANCELLED");
                    booking.setProcessedAt(java.time.LocalDateTime.now());
                    if (request != null && request.containsKey("reason")) {
                        booking.setMessage(request.get("reason"));
                    }
                    bookingRepository.save(booking);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Booking rejected");
                    response.put("booking", booking);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    
}
