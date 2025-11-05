package com.example.hotelbooking.repository;

import com.example.hotelbooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
