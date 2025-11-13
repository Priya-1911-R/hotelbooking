// src/main/java/com/example/hotelbooking/repository/PaymentRepository.java
package com.example.hotelbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByBookingId(Long bookingId);
}