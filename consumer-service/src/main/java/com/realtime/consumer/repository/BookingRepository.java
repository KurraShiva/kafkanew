//package com.realtime.consumer.repository;
//
//import com.realtime.consumer.entity.Booking;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface BookingRepository extends JpaRepository<Booking, Long> {
//    boolean existsByEventId(String eventId);
//    Optional<Booking> findByEventId(String eventId);
//    List<Booking> findByStatus(String status);
//    List<Booking> findByCustomerName(String customerName);
//    List<Booking> findByPaymentStatus(String paymentStatus);
//    Optional<Booking> findByPaymentIntentId(String paymentIntentId);
//}


package com.realtime.consumer.repository;

import com.realtime.consumer.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByEventId(String eventId);
    Optional<Booking> findByEventId(String eventId);
    List<Booking> findByStatus(String status);
    List<Booking> findByCustomerName(String customerName);
    List<Booking> findByPaymentStatus(String paymentStatus);
    Optional<Booking> findByPaymentIntentId(String paymentIntentId);
    
    @Modifying
    @Query(value = "INSERT INTO bookings (event_id, customer_name, customer_email, sport_type, venue, slot_date_time, duration_hours, amount, status, payment_status, created_at, message) " +
            "SELECT :eventId, :customerName, :customerEmail, :sportType, :venue, :slotDateTime, :durationHours, :amount, :status, :paymentStatus, :createdAt, :message " +
            "WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE event_id = :eventId)", nativeQuery = true)
    int upsertIfNotExists(@Param("eventId") String eventId,
                          @Param("customerName") String customerName,
                          @Param("customerEmail") String customerEmail,
                          @Param("sportType") String sportType,
                          @Param("venue") String venue,
                          @Param("slotDateTime") java.time.LocalDateTime slotDateTime,
                          @Param("durationHours") Integer durationHours,
                          @Param("amount") Double amount,
                          @Param("status") String status,
                          @Param("paymentStatus") String paymentStatus,
                          @Param("createdAt") java.time.LocalDateTime createdAt,
                          @Param("message") String message);
}