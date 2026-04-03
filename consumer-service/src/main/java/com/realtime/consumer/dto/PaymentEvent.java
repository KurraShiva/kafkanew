package com.realtime.consumer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String eventId;
    private String bookingEventId;
    private String paymentIntentId;
    private String customerName;
    private String customerEmail;
    private Double amount;
    private String currency;
    private String status; // SUCCEEDED, FAILED, PENDING
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String paymentMethodType;
    private String receiptUrl;
}