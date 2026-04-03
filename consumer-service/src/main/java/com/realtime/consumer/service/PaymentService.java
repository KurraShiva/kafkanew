//package com.realtime.consumer.service;
//
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.PaymentIntent;
//import com.stripe.model.Refund;
//import com.stripe.param.PaymentIntentCancelParams;
//import com.stripe.param.PaymentIntentCreateParams;
//import com.stripe.param.RefundCreateParams;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentService {
//
//    @Value("${stripe.api.key}")
//    private String stripeApiKey;
//
//    @Value("${stripe.currency:inr}")
//    private String currency;
//
//    @PostConstruct
//    public void init() {
//        Stripe.apiKey = stripeApiKey;
//        log.info("Stripe initialized");
//    }
//
//    public Map<String, Object> createPaymentIntent(String bookingEventId, String customerName, 
//                                                    String customerEmail, Double amount, 
//                                                    String currency, String description) 
//            throws StripeException {
//        
//        // Convert amount to smallest currency unit (paise for INR)
//        long amountInSmallestUnit = (long) (amount * 100);
//
//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("bookingEventId", bookingEventId);
//        metadata.put("customerName", customerName);
//        metadata.put("customerEmail", customerEmail != null ? customerEmail : "");
//
//        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                .setAmount(amountInSmallestUnit)
//                .setCurrency(currency.toLowerCase())
//                .setDescription(description)
//                .addPaymentMethodType("card")
//                .putAllMetadata(metadata)
//                .setReceiptEmail(customerEmail)
//                .build();
//
//        PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("clientSecret", paymentIntent.getClientSecret());
//        response.put("paymentIntentId", paymentIntent.getId());
//        response.put("amount", amount);
//        response.put("currency", currency);
//        response.put("status", paymentIntent.getStatus());
//
//        log.info("Created PaymentIntent: id={}, amount={}, bookingId={}", 
//                paymentIntent.getId(), amount, bookingEventId);
//        
//        return response;
//    }
//
//    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
//        return PaymentIntent.retrieve(paymentIntentId);
//    }
//
//    public Map<String, Object> confirmPayment(String paymentIntentId) throws StripeException {
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
//        
//        Map<String, Object> result = new HashMap<>();
//        result.put("paymentIntentId", paymentIntentId);
//        result.put("status", paymentIntent.getStatus());
//        result.put("amount", paymentIntent.getAmount() / 100.0);
//        result.put("currency", paymentIntent.getCurrency());
//        result.put("success", "succeeded".equals(paymentIntent.getStatus()));
//        
//        if (paymentIntent.getMetadata() != null) {
//            result.put("bookingEventId", paymentIntent.getMetadata().get("bookingEventId"));
//        }
//        
//        return result;
//    }
//    
//    // CORRECTED METHOD - Use cancel() instead of update()
//    public Map<String, Object> cancelPaymentIntent(String paymentIntentId) throws StripeException {
//        // Retrieve the PaymentIntent
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
//        
//        // Create cancellation parameters
//        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder()
//                .setCancellationReason(PaymentIntentCancelParams.CancellationReason.REQUESTED_BY_CUSTOMER)
//                .build();
//        
//        // Cancel the PaymentIntent
//        PaymentIntent canceledIntent = paymentIntent.cancel(params);
//        
//        Map<String, Object> result = new HashMap<>();
//        result.put("paymentIntentId", paymentIntentId);
//        result.put("status", canceledIntent.getStatus());
//        result.put("cancellationReason", canceledIntent.getCancellationReason());
//        result.put("success", "canceled".equals(canceledIntent.getStatus()));
//        
//        log.info("PaymentIntent cancelled: id={}, reason={}", paymentIntentId, canceledIntent.getCancellationReason());
//        
//        return result;
//    }
//    
//    // Alternative simpler cancel method without parameters
//    public Map<String, Object> cancelPaymentIntentSimple(String paymentIntentId) throws StripeException {
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
//        PaymentIntent canceledIntent = paymentIntent.cancel();
//        
//        Map<String, Object> result = new HashMap<>();
//        result.put("paymentIntentId", paymentIntentId);
//        result.put("status", canceledIntent.getStatus());
//        result.put("success", "canceled".equals(canceledIntent.getStatus()));
//        
//        return result;
//    }
//    
//    public Map<String, Object> refundPayment(String paymentIntentId, Long amount) throws StripeException {
//        RefundCreateParams params = RefundCreateParams.builder()
//                .setPaymentIntent(paymentIntentId)
//                .setAmount(amount)
//                .build();
//        
//        Refund refund = Refund.create(params);
//        
//        Map<String, Object> result = new HashMap<>();
//        result.put("refundId", refund.getId());
//        result.put("status", refund.getStatus());
//        result.put("amount", refund.getAmount() / 100.0);
//        result.put("success", "succeeded".equals(refund.getStatus()));
//        
//        return result;
//    }
//    
//    // Handle webhook events
//    public Map<String, Object> handleWebhookEvent(String payload, String signatureHeader, String webhookSecret) {
//        Map<String, Object> result = new HashMap<>();
//        
//        try {
//            com.stripe.model.Event event = com.stripe.net.Webhook.constructEvent(
//                    payload, signatureHeader, webhookSecret);
//            
//            log.info("Received webhook event: type={}", event.getType());
//            
//            switch (event.getType()) {
//                case "payment_intent.succeeded":
//                    PaymentIntent succeededIntent = (PaymentIntent) event.getDataObjectDeserializer()
//                            .getObject()
//                            .orElse(null);
//                    if (succeededIntent != null) {
//                        result.put("status", "success");
//                        result.put("paymentIntentId", succeededIntent.getId());
//                        result.put("bookingEventId", succeededIntent.getMetadata().get("bookingEventId"));
//                    }
//                    break;
//                    
//                case "payment_intent.payment_failed":
//                    PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer()
//                            .getObject()
//                            .orElse(null);
//                    if (failedIntent != null) {
//                        result.put("status", "failed");
//                        result.put("paymentIntentId", failedIntent.getId());
//                        result.put("error", failedIntent.getLastPaymentError() != null ? 
//                                failedIntent.getLastPaymentError().getMessage() : "Unknown error");
//                    }
//                    break;
//                    
//                default:
//                    result.put("status", "ignored");
//            }
//            
//            result.put("eventType", event.getType());
//            result.put("success", true);
//            
//        } catch (Exception e) {
//            log.error("Error handling webhook", e);
//            result.put("success", false);
//            result.put("error", e.getMessage());
//        }
//        
//        return result;
//    }
//}



package com.realtime.consumer.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.currency:inr}")
    private String currency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe initialized with API key: {}", stripeApiKey.substring(0, 10) + "...");
    }

    public Map<String, Object> createPaymentIntent(String bookingEventId, String customerName, 
                                                    String customerEmail, Double amount, 
                                                    String currency, String description) 
            throws StripeException {
        
        // Convert amount to smallest currency unit (paise for INR)
        long amountInSmallestUnit = (long) (amount * 100);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("bookingEventId", bookingEventId);
        metadata.put("customerName", customerName);
        metadata.put("customerEmail", customerEmail != null ? customerEmail : "");

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInSmallestUnit)
                .setCurrency(currency.toLowerCase())
                .setDescription(description)
                .addPaymentMethodType("card")
                .putAllMetadata(metadata)
                .setReceiptEmail(customerEmail)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("amount", amount);
        response.put("currency", currency);
        response.put("status", paymentIntent.getStatus());

        log.info("Created PaymentIntent: id={}, amount={}, bookingId={}", 
                paymentIntent.getId(), amount, bookingEventId);
        
        return response;
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    public Map<String, Object> confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("paymentIntentId", paymentIntentId);
        result.put("status", paymentIntent.getStatus());
        result.put("amount", paymentIntent.getAmount() / 100.0);
        result.put("currency", paymentIntent.getCurrency());
        result.put("success", "succeeded".equals(paymentIntent.getStatus()));
        
        if (paymentIntent.getMetadata() != null) {
            result.put("bookingEventId", paymentIntent.getMetadata().get("bookingEventId"));
        }
        
        return result;
    }
    
    public Map<String, Object> cancelPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder()
                .setCancellationReason(PaymentIntentCancelParams.CancellationReason.REQUESTED_BY_CUSTOMER)
                .build();
        
        PaymentIntent canceledIntent = paymentIntent.cancel(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("paymentIntentId", paymentIntentId);
        result.put("status", canceledIntent.getStatus());
        result.put("cancellationReason", canceledIntent.getCancellationReason());
        result.put("success", "canceled".equals(canceledIntent.getStatus()));
        
        log.info("PaymentIntent cancelled: id={}, reason={}", paymentIntentId, canceledIntent.getCancellationReason());
        
        return result;
    }
    
    public Map<String, Object> refundPayment(String paymentIntentId, Long amount) throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount(amount)
                .build();
        
        Refund refund = Refund.create(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("refundId", refund.getId());
        result.put("status", refund.getStatus());
        result.put("amount", refund.getAmount() / 100.0);
        result.put("success", "succeeded".equals(refund.getStatus()));
        
        return result;
    }
}