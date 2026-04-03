////package com.realtime.producer.service;
////
////import com.realtime.producer.dto.BookingEvent;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.kafka.core.KafkaTemplate;
////import org.springframework.kafka.support.SendResult;
////import org.springframework.stereotype.Service;
////
////import java.util.UUID;
////import java.util.concurrent.CompletableFuture;
////
////@Service
////@RequiredArgsConstructor
////@Slf4j
////public class BookingProducer {
////
////    private final KafkaTemplate<String, Object> kafkaTemplate;
////
////    @Value("${kafka.topic.booking-events}")
////    private String topic;
////
////    public String sendBookingEvent(BookingEvent event) {
////        String eventId = UUID.randomUUID().toString();
////        event.setEventId(eventId);
////
////        String key = event.getCustomerName() + "-" + System.currentTimeMillis();
////
////        CompletableFuture<SendResult<String, Object>> future =
////                kafkaTemplate.send(topic, key, event);
////
////        future.whenComplete((result, ex) -> {
////            if (ex == null) {
////                log.info("Booking event sent successfully: eventId={}, partition={}, offset={}",
////                        eventId,
////                        result.getRecordMetadata().partition(),
////                        result.getRecordMetadata().offset());
////            } else {
////                log.error("Failed to send booking event: eventId={}, error={}",
////                        eventId, ex.getMessage(), ex);
////            }
////        });
////
////        return eventId;
////    }
////
////    public void sendBookingEventSync(BookingEvent event) {
////        String eventId = UUID.randomUUID().toString();
////        event.setEventId(eventId);
////
////        String key = event.getCustomerName() + "-" + System.currentTimeMillis();
////
////        try {
////            SendResult<String, Object> result = kafkaTemplate.send(topic, key, event).get();
////            log.info("Booking event sent synchronously: eventId={}, partition={}, offset={}",
////                    eventId,
////                    result.getRecordMetadata().partition(),
////                    result.getRecordMetadata().offset());
////        } catch (Exception e) {
////            log.error("Failed to send booking event: eventId={}, error={}",
////                    eventId, e.getMessage(), e);
////            throw new RuntimeException("Failed to send booking event", e);
////        }
////    }
////}
//
//package com.realtime.producer.service;
//
//import com.realtime.producer.dto.BookingEvent;
//import com.realtime.producer.dto.PaymentEvent;
//import com.realtime.producer.dto.PaymentInitiateRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BookingProducer {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final RestTemplate restTemplate;
//
//    @Value("${kafka.topic.booking-events}")
//    private String bookingTopic;
//    
//    @Value("${kafka.topic.payment-events}")
//    private String paymentTopic;
//    
//    @Value("${consumer.service.url:http://localhost:8082}")
//    private String consumerServiceUrl;
//
//    public String sendBookingEvent(BookingEvent event) {
//        String eventId = UUID.randomUUID().toString();
//        event.setEventId(eventId);
//
//        String key = event.getCustomerName() + "-" + System.currentTimeMillis();
//
//        CompletableFuture<SendResult<String, Object>> future =
//                kafkaTemplate.send(bookingTopic, key, event);
//
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                log.info("Booking event sent successfully: eventId={}, partition={}, offset={}",
//                        eventId,
//                        result.getRecordMetadata().partition(),
//                        result.getRecordMetadata().offset());
//            } else {
//                log.error("Failed to send booking event: eventId={}, error={}",
//                        eventId, ex.getMessage(), ex);
//            }
//        });
//
//        return eventId;
//    }
//    
//    public Map<String, Object> initiatePayment(String bookingEventId, PaymentInitiateRequest request) {
//        try {
//            // Call consumer service to create payment intent
//            String url = consumerServiceUrl + "/api/payments/create-payment-intent";
//            
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            
//            Map<String, Object> paymentRequest = new HashMap<>();
//            paymentRequest.put("bookingEventId", bookingEventId);
//            paymentRequest.put("customerName", request.getCustomerName());
//            paymentRequest.put("customerEmail", request.getCustomerEmail());
//            paymentRequest.put("amount", request.getAmount());
//            paymentRequest.put("currency", "inr");
//            paymentRequest.put("description", "Booking payment for " + request.getSportType());
//            
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentRequest, headers);
//            
//            @SuppressWarnings("unchecked")
//            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
//            
//            log.info("Payment intent created: {}", response);
//            return response;
//            
//        } catch (Exception e) {
//            log.error("Error initiating payment", e);
//            throw new RuntimeException("Failed to initiate payment", e);
//        }
//    }
//    
//    public void sendPaymentSuccessEvent(String bookingEventId, String paymentIntentId) {
//        PaymentEvent paymentEvent = PaymentEvent.builder()
//                .eventId(UUID.randomUUID().toString())
//                .bookingEventId(bookingEventId)
//                .paymentIntentId(paymentIntentId)
//                .status("SUCCEEDED")
//                .timestamp(java.time.LocalDateTime.now())
//                .build();
//        
//        String key = "payment-" + paymentIntentId;
//        
//        CompletableFuture<SendResult<String, Object>> future =
//                kafkaTemplate.send(paymentTopic, key, paymentEvent);
//        
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                log.info("Payment success event sent: bookingId={}, paymentIntentId={}",
//                        bookingEventId, paymentIntentId);
//            } else {
//                log.error("Failed to send payment success event", ex);
//            }
//        });
//    }
//    
//    public void sendPaymentFailureEvent(String bookingEventId, String paymentIntentId, String error) {
//        PaymentEvent paymentEvent = PaymentEvent.builder()
//                .eventId(UUID.randomUUID().toString())
//                .bookingEventId(bookingEventId)
//                .paymentIntentId(paymentIntentId)
//                .status("FAILED")
//                .errorMessage(error)
//                .timestamp(java.time.LocalDateTime.now())
//                .build();
//        
//        String key = "payment-" + paymentIntentId;
//        
//        CompletableFuture<SendResult<String, Object>> future =
//                kafkaTemplate.send(paymentTopic, key, paymentEvent);
//        
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                log.info("Payment failure event sent: bookingId={}, paymentIntentId={}",
//                        bookingEventId, paymentIntentId);
//            } else {
//                log.error("Failed to send payment failure event", ex);
//            }
//        });
//    }
//
//    public void sendBookingEventSync(BookingEvent event) {
//        String eventId = UUID.randomUUID().toString();
//        event.setEventId(eventId);
//
//        String key = event.getCustomerName() + "-" + System.currentTimeMillis();
//
//        try {
//            SendResult<String, Object> result = kafkaTemplate.send(bookingTopic, key, event).get();
//            log.info("Booking event sent synchronously: eventId={}, partition={}, offset={}",
//                    eventId,
//                    result.getRecordMetadata().partition(),
//                    result.getRecordMetadata().offset());
//        } catch (Exception e) {
//            log.error("Failed to send booking event: eventId={}, error={}",
//                    eventId, e.getMessage(), e);
//            throw new RuntimeException("Failed to send booking event", e);
//        }
//    }
//}



package com.realtime.producer.service;

import com.realtime.producer.dto.BookingEvent;
import com.realtime.producer.dto.PaymentIntentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RestTemplate restTemplate;

    @Value("${kafka.topic.booking-events}")
    private String bookingTopic;
    
    @Value("${kafka.topic.payment-events}")
    private String paymentTopic;
    
    @Value("${consumer.service.url:http://localhost:8082}")
    private String consumerServiceUrl;

    public String sendBookingEvent(BookingEvent event) {
        String eventId = UUID.randomUUID().toString();
        event.setEventId(eventId);

        String key = event.getCustomerName() + "-" + System.currentTimeMillis();

        try {
            SendResult<String, Object> result = kafkaTemplate.send(bookingTopic, key, event).get();
            log.info("Booking event sent successfully: eventId={}, partition={}, offset={}",
                    eventId,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Failed to send booking event: eventId={}, error={}",
                    eventId, e.getMessage(), e);
            throw new RuntimeException("Failed to send booking event", e);
        }

        return eventId;
    }
    
//    public Map<String, Object> initiatePayment(String bookingEventId, PaymentIntentRequest request) {
//        try {
//            String url = consumerServiceUrl + "/api/payments/create-payment-intent";
//            
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            
//            Map<String, Object> paymentRequest = new HashMap<>();
//            paymentRequest.put("bookingEventId", bookingEventId);
//            paymentRequest.put("customerName", request.getCustomerName());
//            paymentRequest.put("customerEmail", request.getCustomerEmail() != null ? 
//                    request.getCustomerEmail() : request.getCustomerName() + "@example.com");
//            paymentRequest.put("amount", request.getAmount());
//            paymentRequest.put("currency", "inr");
//            paymentRequest.put("description", "Booking payment for " + request.getSportType());
//            
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentRequest, headers);
//            
//            @SuppressWarnings("unchecked")
//            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
//            
//            log.info("Payment intent created: {}", response);
//            return response;
//            
//        } catch (Exception e) {
//            log.error("Error initiating payment", e);
//            throw new RuntimeException("Failed to initiate payment", e);
//        }
//    }
    
    public Map<String, Object> initiatePayment(String bookingEventId, PaymentIntentRequest request) {
        try {
            String url = consumerServiceUrl + "/api/payments/create-payment-intent";
            log.info("Calling payment API at: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> paymentRequest = new HashMap<>();
            paymentRequest.put("bookingEventId", bookingEventId);
            paymentRequest.put("customerName", request.getCustomerName());
            paymentRequest.put("customerEmail", request.getCustomerEmail() != null ? 
                    request.getCustomerEmail() : request.getCustomerName() + "@example.com");
            paymentRequest.put("amount", request.getAmount());
            paymentRequest.put("currency", "inr");
            paymentRequest.put("description", "Booking payment for " + request.getSportType());
            // Add these fields for creating booking if not exists
            paymentRequest.put("sportType", request.getSportType());
            paymentRequest.put("venue", request.getVenue());
            paymentRequest.put("slotDateTime", request.getSlotDateTime() != null ? 
                    request.getSlotDateTime().toString().replace('T', ' ') : null);
            paymentRequest.put("durationHours", request.getDurationHours());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentRequest, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            log.info("Payment API response status: {}", response.getStatusCode());
            
            if (response.getBody() == null) {
                throw new RuntimeException("No response from payment service");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();
            log.info("Payment intent created: {}", responseBody);
            return responseBody;
            
        } catch (Exception e) {
            log.error("Error initiating payment: ", e);
            throw new RuntimeException("Failed to initiate payment: " + e.getMessage(), e);
        }
    }

    public void sendBookingEventSync(BookingEvent event) {
        String eventId = UUID.randomUUID().toString();
        event.setEventId(eventId);

        String key = event.getCustomerName() + "-" + System.currentTimeMillis();

        try {
            SendResult<String, Object> result = kafkaTemplate.send(bookingTopic, key, event).get();
            log.info("Booking event sent synchronously: eventId={}, partition={}, offset={}",
                    eventId,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Failed to send booking event: eventId={}, error={}",
                    eventId, e.getMessage(), e);
            throw new RuntimeException("Failed to send booking event", e);
        }
    }
}