//
//package com.realtime.consumer.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "bookings")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Booking {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "event_id", unique = true)
//    private String eventId;
//
//    @Column(name = "customer_name")
//    private String customerName;
//
//    @Column(name = "sport_type")
//    private String sportType;
//
//    @Column(name = "venue")
//    private String venue;
//
//    @Column(name = "slot_date_time")
//    private LocalDateTime slotDateTime;
//
//    @Column(name = "duration_hours")
//    private Integer durationHours;
//
//    @Column(name = "amount")
//    private Double amount;
//
//    @Column(name = "status")
//    private String status; // PENDING_PAYMENT, CONFIRMED, PAYMENT_FAILED, CANCELLED
//
//    @Column(name = "payment_status")
//    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED
//
//    @Column(name = "payment_intent_id")
//    private String paymentIntentId;
//
//    @Column(name = "error_message")
//    private String errorMessage;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "message")
//    private String message;
//
//    @Column(name = "processed_at")
//    private LocalDateTime processedAt;
//}



package com.realtime.consumer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", unique = true)
    private String eventId;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "sport_type")
    private String sportType;

    @Column(name = "venue")
    private String venue;

    @Column(name = "slot_date_time")
    private LocalDateTime slotDateTime;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    private String status; // PENDING_PAYMENT, CONFIRMED, CANCELLED, PAYMENT_FAILED

    @Column(name = "payment_status")
    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED, CANCELLED

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "message")
    private String message;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}