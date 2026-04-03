//
//package com.realtime.consumer.controller;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.realtime.consumer.entity.Booking;
//import com.realtime.consumer.repository.BookingRepository;
//import com.realtime.consumer.service.PaymentService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@RequestMapping("/api/payments")
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentController {
//
//    private final PaymentService paymentService;
//    private final BookingRepository bookingRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Value("${kafka.topic.payment-events}")
//    private String paymentTopic;
//    
//    @Value("${stripe.publishable.key}")
//    private String publishableKey;
//
////    @PostMapping("/create-payment-intent")
////    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
////        try {
////            String bookingEventId = (String) request.get("bookingEventId");
////            String customerName = (String) request.get("customerName");
////            String customerEmail = (String) request.get("customerEmail");
////            Double amount = Double.parseDouble(String.valueOf(request.get("amount")));
////            String currency = (String) request.getOrDefault("currency", "inr");
////            String description = (String) request.get("description");
////
////            // Verify booking exists
////            Optional<Booking> bookingOpt = bookingRepository.findByEventId(bookingEventId);
////            if (bookingOpt.isEmpty()) {
////                return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
////            }
////
////            Booking booking = bookingOpt.get();
////            
////            // Create payment intent with Stripe
////            Map<String, Object> paymentResponse = paymentService.createPaymentIntent(
////                    bookingEventId, customerName, customerEmail, amount, currency, description);
////
////            // Update booking with payment intent ID
////            booking.setPaymentIntentId((String) paymentResponse.get("paymentIntentId"));
////            booking.setStatus("PENDING_PAYMENT");
////            booking.setPaymentStatus("PENDING");
////            bookingRepository.save(booking);
////
////            // Broadcast payment intent created
////            Map<String, Object> broadcast = new HashMap<>();
////            broadcast.put("type", "PAYMENT_INTENT_CREATED");
////            broadcast.put("bookingId", booking.getId());
////            broadcast.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
////            broadcast.put("clientSecret", paymentResponse.get("clientSecret"));
////            messagingTemplate.convertAndSend("/topic/payments", broadcast);
////
////            Map<String, Object> response = new HashMap<>();
////            response.put("success", true);
////            response.put("clientSecret", paymentResponse.get("clientSecret"));
////            response.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
////            response.put("amount", amount);
////            response.put("currency", currency);
////            response.put("bookingId", booking.getId());
////            response.put("bookingEventId", bookingEventId);
////
////            return ResponseEntity.ok(response);
////            
////        } catch (Exception e) {
////            log.error("Error creating payment intent", e);
////            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
////        }
////    }
////
////    @PostMapping("/confirm/{paymentIntentId}")
////    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable String paymentIntentId) {
////        try {
////            Map<String, Object> result = paymentService.confirmPayment(paymentIntentId);
////            
////            if (Boolean.TRUE.equals(result.get("success"))) {
////                String bookingEventId = (String) result.get("bookingEventId");
////                
////                // Update booking status
////                Optional<Booking> bookingOpt = bookingRepository.findByEventId(bookingEventId);
////                if (bookingOpt.isPresent()) {
////                    Booking booking = bookingOpt.get();
////                    booking.setStatus("CONFIRMED");
////                    booking.setPaymentStatus("PAID");
////                    booking.setProcessedAt(LocalDateTime.now());
////                    bookingRepository.save(booking);
////                    
////                    // Send payment success event to Kafka
////                    Map<String, Object> paymentEvent = new HashMap<>();
////                    paymentEvent.put("eventId", java.util.UUID.randomUUID().toString());
////                    paymentEvent.put("bookingEventId", bookingEventId);
////                    paymentEvent.put("paymentIntentId", paymentIntentId);
////                    paymentEvent.put("status", "SUCCEEDED");
////                    paymentEvent.put("timestamp", LocalDateTime.now().toString());
////                    
////                    kafkaTemplate.send(paymentTopic, paymentIntentId, paymentEvent);
////                    
////                    // Broadcast to WebSocket
////                    messagingTemplate.convertAndSend("/topic/bookings", Map.of(
////                        "type", "BOOKING_CONFIRMED",
////                        "bookingId", booking.getId(),
////                        "status", "CONFIRMED"
////                    ));
////                    
////                    result.put("bookingId", booking.getId());
////                    result.put("bookingStatus", "CONFIRMED");
////                }
////                
////                result.put("message", "Payment confirmed successfully");
////            }
////            
////            return ResponseEntity.ok(result);
////            
////        } catch (Exception e) {
////            log.error("Error confirming payment", e);
////            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
////        }
////    }
//    
//    @PostMapping("/create-payment-intent")
//    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
//        log.info("=== CREATE PAYMENT INTENT REQUEST ===");
//        log.info("Request body: {}", request);
//        
//        try {
//            String bookingEventId = (String) request.get("bookingEventId");
//            String customerName = (String) request.get("customerName");
//            String customerEmail = (String) request.get("customerEmail");
//            Double amount = Double.parseDouble(String.valueOf(request.get("amount")));
//            String currency = (String) request.getOrDefault("currency", "inr");
//            String description = (String) request.get("description");
//            String sportType = (String) request.getOrDefault("sportType", "Unknown");
//            String venue = (String) request.getOrDefault("venue", "Unknown");
//
//            log.info("Processing payment for: bookingId={}, customer={}, amount={}", 
//                    bookingEventId, customerName, amount);
//
//            // Check if booking exists, if not create it immediately
//            Optional<Booking> bookingOpt = bookingRepository.findByEventId(bookingEventId);
//            Booking booking;
//            
//            if (bookingOpt.isEmpty()) {
//                log.info("Booking not found, creating new booking for eventId: {}", bookingEventId);
//                booking = Booking.builder()
//                        .eventId(bookingEventId)
//                        .customerName(customerName)
//                        .customerEmail(customerEmail)
//                        .sportType(sportType)
//                        .venue(venue)
//                        .amount(amount)
//                        .status("PENDING_PAYMENT")
//                        .paymentStatus("PENDING")
//                        .createdAt(LocalDateTime.now())
//                        .message("Created during payment initialization")
//                        .build();
//                booking = bookingRepository.save(booking);
//                log.info("Created new booking with id: {}", booking.getId());
//            } else {
//                booking = bookingOpt.get();
//                log.info("Found existing booking with id: {}", booking.getId());
//            }
//            
//            // Create payment intent with Stripe
//            Map<String, Object> paymentResponse = paymentService.createPaymentIntent(
//                    bookingEventId, customerName, customerEmail, amount, currency, description);
//
//            // Update booking with payment intent ID
//            booking.setPaymentIntentId((String) paymentResponse.get("paymentIntentId"));
//            booking.setStatus("PENDING_PAYMENT");
//            booking.setPaymentStatus("PENDING");
//            bookingRepository.save(booking);
//
//            // Broadcast payment intent created
//            Map<String, Object> broadcast = new HashMap<>();
//            broadcast.put("type", "PAYMENT_INTENT_CREATED");
//            broadcast.put("bookingId", booking.getId());
//            broadcast.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
//            broadcast.put("clientSecret", paymentResponse.get("clientSecret"));
//            messagingTemplate.convertAndSend("/topic/payments", broadcast);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("clientSecret", paymentResponse.get("clientSecret"));
//            response.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
//            response.put("amount", amount);
//            response.put("currency", currency);
//            response.put("bookingId", booking.getId());
//            response.put("bookingEventId", bookingEventId);
//
//            log.info("Payment intent created successfully: {}", response);
//            return ResponseEntity.ok(response);
//            
//        } catch (Exception e) {
//            log.error("Error creating payment intent: ", e);
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("error", e.getMessage());
//            errorResponse.put("success", false);
//            return ResponseEntity.internalServerError().body(errorResponse);
//        }
//    }
//
//    @PostMapping("/confirm/{paymentIntentId}")
//    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable String paymentIntentId) {
//        try {
//            Map<String, Object> result = paymentService.confirmPayment(paymentIntentId);
//            
//            if (Boolean.TRUE.equals(result.get("success"))) {
//                String bookingEventId = (String) result.get("bookingEventId");
//                
//                // Update booking status
//                Optional<Booking> bookingOpt = bookingRepository.findByEventId(bookingEventId);
//                if (bookingOpt.isPresent()) {
//                    Booking booking = bookingOpt.get();
//                    booking.setStatus("CONFIRMED");
//                    booking.setPaymentStatus("PAID");
//                    booking.setProcessedAt(LocalDateTime.now());
//                    bookingRepository.save(booking);
//                    
//                    // Send payment success event to Kafka
//                    Map<String, Object> paymentEvent = new HashMap<>();
//                    paymentEvent.put("eventId", UUID.randomUUID().toString	());
//                    paymentEvent.put("bookingEventId", bookingEventId);
//                    paymentEvent.put("paymentIntentId", paymentIntentId);
//                    paymentEvent.put("status", "SUCCEEDED");
//                    paymentEvent.put("timestamp", LocalDateTime.now().toString());
//                    
//                    kafkaTemplate.send(paymentTopic, paymentIntentId, paymentEvent);
//                    
//                    // Broadcast to WebSocket
//                    messagingTemplate.convertAndSend("/topic/bookings", Map.of(
//                        "type", "BOOKING_CONFIRMED",
//                        "bookingId", booking.getId(),
//                        "status", "CONFIRMED",
//                        "booking", booking
//                    ));
//                    
//                    result.put("bookingId", booking.getId());
//                    result.put("bookingStatus", "CONFIRMED");
//                }
//                
//                result.put("message", "Payment confirmed successfully");
//            }
//            
//            return ResponseEntity.ok(result);
//            
//        } catch (Exception e) {
//            log.error("Error confirming payment", e);
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    @GetMapping("/publishable-key")
//    public ResponseEntity<Map<String, String>> getPublishableKey() {
//        Map<String, String> response = new HashMap<>();
//        response.put("publishableKey", publishableKey);
//        return ResponseEntity.ok(response);
//    }
//    
//    @GetMapping("/health")
//    public ResponseEntity<Map<String, String>> health() {
//        Map<String, String> health = new HashMap<>();
//        health.put("status", "UP");
//        health.put("service", "payment-service");
//        return ResponseEntity.ok(health);
//    }
//
//    @PostMapping("/cancel/{paymentIntentId}")
//    public ResponseEntity<Map<String, Object>> cancelPayment(@PathVariable String paymentIntentId) {
//        try {
//            Map<String, Object> result = paymentService.cancelPaymentIntent(paymentIntentId);
//            
//            // Find and update booking
//            Optional<Booking> bookingOpt = bookingRepository.findByPaymentIntentId(paymentIntentId);
//            if (bookingOpt.isPresent()) {
//                Booking booking = bookingOpt.get();
//                booking.setStatus("CANCELLED");
//                booking.setPaymentStatus("CANCELLED");
//                booking.setProcessedAt(LocalDateTime.now());
//                bookingRepository.save(booking);
//                
//                // Broadcast cancellation
//                messagingTemplate.convertAndSend("/topic/payments", Map.of(
//                    "type", "PAYMENT_CANCELLED",
//                    "bookingId", booking.getId(),
//                    "paymentIntentId", paymentIntentId
//                ));
//                
//                result.put("bookingId", booking.getId());
//                result.put("bookingStatus", "CANCELLED");
//            }
//            
//            return ResponseEntity.ok(result);
//            
//        } catch (Exception e) {
//            log.error("Error cancelling payment", e);
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    @PostMapping("/refund/{paymentIntentId}")
//    public ResponseEntity<Map<String, Object>> refundPayment(
//            @PathVariable String paymentIntentId,
//            @RequestParam(required = false) Long amount) {
//        try {
//            Map<String, Object> result = paymentService.refundPayment(paymentIntentId, amount);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("Error refunding payment", e);
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    @GetMapping("/status/{paymentIntentId}")
//    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String paymentIntentId) {
//        try {
//            Map<String, Object> result = paymentService.confirmPayment(paymentIntentId);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("Error getting payment status", e);
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }
//
////    @GetMapping("/publishable-key")
////    public ResponseEntity<Map<String, String>> getPublishableKey() {
////        Map<String, String> response = new HashMap<>();
////        response.put("publishableKey", publishableKey);
////        return ResponseEntity.ok(response);
////    }
////    
////    @GetMapping("/health")
////    public ResponseEntity<Map<String, String>> health() {
////        Map<String, String> health = new HashMap<>();
////        health.put("status", "UP");
////        health.put("service", "payment-service");
////        return ResponseEntity.ok(health);
////    }
//}


package com.realtime.consumer.controller;

import com.realtime.consumer.entity.Booking;
import com.realtime.consumer.repository.BookingRepository;
import com.realtime.consumer.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.payment-events}")
    private String paymentTopic;
    
    @Value("${stripe.publishable.key}")
    private String publishableKey;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        log.info("=== CREATE PAYMENT INTENT REQUEST ===");
        log.info("Request body: {}", request);
        
        String bookingEventId = null;
        try {
            bookingEventId = (String) request.get("bookingEventId");
            String customerName = (String) request.get("customerName");
            String customerEmail = (String) request.get("customerEmail");
            Double amount = Double.parseDouble(String.valueOf(request.get("amount")));
            String currency = (String) request.getOrDefault("currency", "inr");
            String description = (String) request.get("description");
            String sportType = (String) request.getOrDefault("sportType", "Unknown");
            String venue = (String) request.getOrDefault("venue", "Unknown");
            
            // Parse slotDateTime if provided
            LocalDateTime slotDateTime = null;
            if (request.get("slotDateTime") != null) {
                try {
                    String dateStr = (String) request.get("slotDateTime");
                    slotDateTime = LocalDateTime.parse(dateStr.replace(' ', 'T'));
                } catch (Exception e) {
                    log.warn("Could not parse slotDateTime: {}", request.get("slotDateTime"));
                }
            }
            
            Integer durationHours = request.get("durationHours") != null ? 
                    Integer.parseInt(String.valueOf(request.get("durationHours"))) : 1;

            log.info("Processing payment for: bookingId={}, customer={}, amount={}", 
                    bookingEventId, customerName, amount);

            Booking booking = saveOrGetBookingIdempotently(bookingEventId, customerName, customerEmail, 
                    sportType, venue, amount, slotDateTime, durationHours);
            
            log.info("Booking ready for payment: id={}, status={}", booking.getId(), booking.getStatus());
            
            // Create payment intent with Stripe
            Map<String, Object> paymentResponse = paymentService.createPaymentIntent(
                    bookingEventId, customerName, customerEmail, amount, currency, description);

            // Update booking with payment intent ID
            booking.setPaymentIntentId((String) paymentResponse.get("paymentIntentId"));
            booking.setStatus("PENDING_PAYMENT");
            booking.setPaymentStatus("PENDING");
            bookingRepository.save(booking);

            // Broadcast payment intent created
            Map<String, Object> broadcast = new HashMap<>();
            broadcast.put("type", "PAYMENT_INTENT_CREATED");
            broadcast.put("bookingId", booking.getId());
            broadcast.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
            broadcast.put("clientSecret", paymentResponse.get("clientSecret"));
            messagingTemplate.convertAndSend("/topic/payments", broadcast);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("clientSecret", paymentResponse.get("clientSecret"));
            response.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
            response.put("amount", amount);
            response.put("currency", currency);
            response.put("bookingId", booking.getId());
            response.put("bookingEventId", bookingEventId);

            log.info("Payment intent created successfully: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation (likely duplicate): {}", e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                log.warn("Booking already exists for eventId: {}, returning existing booking", bookingEventId);
                Optional<Booking> existingBooking = bookingRepository.findByEventId(bookingEventId);
                if (existingBooking.isPresent()) {
                    Booking booking = existingBooking.get();
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("clientSecret", booking.getPaymentIntentId() != null ? "reuse_existing" : null);
                    response.put("paymentIntentId", booking.getPaymentIntentId());
                    response.put("amount", booking.getAmount());
                    response.put("currency", "inr");
                    response.put("bookingId", booking.getId());
                    response.put("bookingEventId", bookingEventId);
                    response.put("message", "Booking already exists");
                    return ResponseEntity.ok(response);
                }
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        } catch (Exception e) {
            log.error("Error creating payment intent: ", e);
            
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                log.warn("Booking already exists for eventId: {}, returning existing booking", bookingEventId);
                Optional<Booking> existingBooking = bookingRepository.findByEventId(bookingEventId);
                if (existingBooking.isPresent()) {
                    Booking booking = existingBooking.get();
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("clientSecret", booking.getPaymentIntentId() != null ? "reuse_existing" : null);
                    response.put("paymentIntentId", booking.getPaymentIntentId());
                    response.put("amount", booking.getAmount());
                    response.put("currency", "inr");
                    response.put("bookingId", booking.getId());
                    response.put("bookingEventId", bookingEventId);
                    response.put("message", "Booking already exists");
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/confirm/{paymentIntentId}")
    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable String paymentIntentId) {
        try {
            Map<String, Object> result = paymentService.confirmPayment(paymentIntentId);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                String bookingEventId = (String) result.get("bookingEventId");
                
                // Update booking status
                Optional<Booking> bookingOpt = bookingRepository.findByEventId(bookingEventId);
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    booking.setStatus("CONFIRMED");
                    booking.setPaymentStatus("PAID");
                    booking.setProcessedAt(LocalDateTime.now());
                    bookingRepository.save(booking);
                    
                    // Send payment success event to Kafka
                    Map<String, Object> paymentEvent = new HashMap<>();
                    paymentEvent.put("eventId", UUID.randomUUID().toString());
                    paymentEvent.put("bookingEventId", bookingEventId);
                    paymentEvent.put("paymentIntentId", paymentIntentId);
                    paymentEvent.put("status", "SUCCEEDED");
                    paymentEvent.put("timestamp", LocalDateTime.now().toString());
                    
                    kafkaTemplate.send(paymentTopic, paymentIntentId, paymentEvent);
                    
                    // Broadcast to WebSocket
                    messagingTemplate.convertAndSend("/topic/bookings", Map.of(
                        "type", "BOOKING_CONFIRMED",
                        "bookingId", booking.getId(),
                        "status", "CONFIRMED",
                        "booking", booking
                    ));
                    
                    result.put("bookingId", booking.getId());
                    result.put("bookingStatus", "CONFIRMED");
                }
                
                result.put("message", "Payment confirmed successfully");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error confirming payment", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/publishable-key")
    public ResponseEntity<Map<String, String>> getPublishableKey() {
        Map<String, String> response = new HashMap<>();
        response.put("publishableKey", publishableKey);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "payment-service");
        return ResponseEntity.ok(health);
    }
    
    private Booking saveOrGetBookingIdempotently(String eventId, String customerName, String customerEmail,
            String sportType, String venue, Double amount, LocalDateTime slotDateTime, Integer durationHours) {
        try {
            Optional<Booking> existing = bookingRepository.findByEventId(eventId);
            if (existing.isPresent()) {
                log.info("Booking already exists for eventId: {}, returning existing", eventId);
                return existing.get();
            }
            
            Booking booking = Booking.builder()
                    .eventId(eventId)
                    .customerName(customerName)
                    .customerEmail(customerEmail)
                    .sportType(sportType)
                    .venue(venue)
                    .amount(amount)
                    .slotDateTime(slotDateTime)
                    .durationHours(durationHours)
                    .status("PENDING_PAYMENT")
                    .paymentStatus("PENDING")
                    .createdAt(LocalDateTime.now())
                    .message("Created during payment initialization")
                    .build();
            return bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition detected, fetching existing booking for eventId: {}", eventId);
            return bookingRepository.findByEventId(eventId).orElseThrow();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                log.warn("Race condition detected, fetching existing booking for eventId: {}", eventId);
                return bookingRepository.findByEventId(eventId).orElseThrow();
            }
            throw e;
        }
    }
}