////package com.realtime.producer.controller;
////
////import com.realtime.producer.dto.BookingEvent;
////import com.realtime.producer.service.BookingProducer;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.time.LocalDateTime;
////import java.util.HashMap;
////import java.util.Map;
////
////@RestController
////@RequestMapping("/api/bookings")
////@RequiredArgsConstructor
////@Slf4j
////public class BookingController {
////
////    private final BookingProducer bookingProducer;
////
////    @PostMapping
////    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingEvent event) {
////        log.info("Received booking request: customer={}, sportType={}, venue={}",
////                event.getCustomerName(), event.getSportType(), event.getVenue());
////
////        event.setStatus("PENDING");
////        event.setCreatedAt(LocalDateTime.now());
////
////        String eventId = bookingProducer.sendBookingEvent(event);
////
////        Map<String, Object> response = new HashMap<>();
////        response.put("success", true);
////        response.put("message", "Booking event sent to Kafka");
////        response.put("eventId", eventId);
////        response.put("timestamp", LocalDateTime.now());
////
////        return ResponseEntity.ok(response);
////    }
////
////    @PostMapping("/sync")
////    public ResponseEntity<Map<String, Object>> createBookingSync(@RequestBody BookingEvent event) {
////        log.info("Received synchronous booking request: customer={}, sportType={}, venue={}",
////                event.getCustomerName(), event.getSportType(), event.getVenue());
////
////        event.setStatus("PENDING");
////        event.setCreatedAt(LocalDateTime.now());
////
////        bookingProducer.sendBookingEventSync(event);
////
////        Map<String, Object> response = new HashMap<>();
////        response.put("success", "true");
////        response.put("message", "Booking event sent to Kafka synchronously");
////
////        return ResponseEntity.ok(response);
////    }
////
////    @GetMapping("/health")
////    public ResponseEntity<Map<String, String>> health() {
////        Map<String, String> health = new HashMap<>();
////        health.put("status", "UP");
////        health.put("service", "producer-service");
////        return ResponseEntity.ok(health);
////    }
////}
//
//
//package com.realtime.producer.controller;
//
//import com.realtime.producer.dto.BookingEvent;
//import com.realtime.producer.dto.PaymentInitiateRequest;
//import com.realtime.producer.service.BookingProducer;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/bookings")
//@RequiredArgsConstructor
//@Slf4j
//public class BookingController {
//
//    private final BookingProducer bookingProducer;
//
//    @PostMapping
//    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingEvent event) {
//        log.info("Received booking request: customer={}, sportType={}, venue={}",
//                event.getCustomerName(), event.getSportType(), event.getVenue());
//
//        event.setStatus("PENDING_PAYMENT");
//        event.setCreatedAt(LocalDateTime.now());
//
//        String eventId = bookingProducer.sendBookingEvent(event);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Booking created, pending payment");
//        response.put("eventId", eventId);
//        response.put("bookingStatus", "PENDING_PAYMENT");
//        response.put("timestamp", LocalDateTime.now());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/with-payment")
//    public ResponseEntity<Map<String, Object>> createBookingWithPayment(@RequestBody PaymentInitiateRequest request) {
//        log.info("Received booking with payment request: customer={}, amount={}",
//                request.getCustomerName(), request.getAmount());
//
//        // Create booking event
//        BookingEvent event = BookingEvent.builder()
//                .customerName(request.getCustomerName())
//                .sportType(request.getSportType())
//                .venue(request.getVenue())
//                .slotDateTime(request.getSlotDateTime())
//                .durationHours(request.getDurationHours())
//                .amount(request.getAmount())
//                .status("PENDING_PAYMENT")
//                .createdAt(LocalDateTime.now())
//                .message(request.getMessage())
//                .build();
//
//        String eventId = bookingProducer.sendBookingEvent(event);
//
//        // Initiate payment intent
//        Map<String, Object> paymentResponse = bookingProducer.initiatePayment(eventId, request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("eventId", eventId);
//        response.put("clientSecret", paymentResponse.get("clientSecret"));
//        response.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
//        response.put("bookingStatus", "PENDING_PAYMENT");
//        response.put("timestamp", LocalDateTime.now());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/payment-webhook")
//    public ResponseEntity<Map<String, Object>> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
//        log.info("Received payment webhook: {}", payload);
//        
//        String paymentIntentId = (String) payload.get("paymentIntentId");
//        String status = (String) payload.get("status");
//        String eventId = (String) payload.get("eventId");
//        
//        if ("succeeded".equals(status)) {
//            bookingProducer.sendPaymentSuccessEvent(eventId, paymentIntentId);
//        } else if ("failed".equals(status)) {
//            bookingProducer.sendPaymentFailureEvent(eventId, paymentIntentId, (String) payload.get("error"));
//        }
//        
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Webhook processed");
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/sync")
//    public ResponseEntity<Map<String, Object>> createBookingSync(@RequestBody BookingEvent event) {
//        log.info("Received synchronous booking request: customer={}, sportType={}, venue={}",
//                event.getCustomerName(), event.getSportType(), event.getVenue());
//
//        event.setStatus("PENDING_PAYMENT");
//        event.setCreatedAt(LocalDateTime.now());
//
//        bookingProducer.sendBookingEventSync(event);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", "true");
//        response.put("message", "Booking event sent to Kafka synchronously");
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/health")
//    public ResponseEntity<Map<String, String>> health() {
//        Map<String, String> health = new HashMap<>();
//        health.put("status", "UP");
//        health.put("service", "producer-service");
//        return ResponseEntity.ok(health);
//    }
//}


package com.realtime.producer.controller;

import com.realtime.producer.dto.BookingEvent;
import com.realtime.producer.dto.PaymentIntentRequest;
import com.realtime.producer.service.BookingProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingProducer bookingProducer;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingEvent event) {
        log.info("Received booking request: customer={}, sportType={}, venue={}",
                event.getCustomerName(), event.getSportType(), event.getVenue());

        event.setStatus("PENDING_PAYMENT");
        event.setCreatedAt(LocalDateTime.now());

        String eventId = bookingProducer.sendBookingEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Booking created, pending payment");
        response.put("eventId", eventId);
        response.put("bookingStatus", "PENDING_PAYMENT");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/with-payment")
    public ResponseEntity<Map<String, Object>> createBookingWithPayment(@RequestBody PaymentIntentRequest request) {
        log.info("Received booking with payment request: customer={}, amount={}",
                request.getCustomerName(), request.getAmount());

        // Create booking event
        BookingEvent event = BookingEvent.builder()
                .customerName(request.getCustomerName())
                .sportType(request.getSportType())
                .venue(request.getVenue())
                .slotDateTime(request.getSlotDateTime())
                .durationHours(request.getDurationHours())
                .amount(request.getAmount())
                .status("PENDING_PAYMENT")
                .createdAt(LocalDateTime.now())
                .message(request.getMessage())
                .build();

        String eventId = bookingProducer.sendBookingEvent(event);
        
        // Create payment intent via consumer service
        Map<String, Object> paymentResponse = bookingProducer.initiatePayment(eventId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("eventId", eventId);
        response.put("clientSecret", paymentResponse.get("clientSecret"));
        response.put("paymentIntentId", paymentResponse.get("paymentIntentId"));
        response.put("bookingStatus", "PENDING_PAYMENT");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "producer-service");
        return ResponseEntity.ok(health);
    }
}
