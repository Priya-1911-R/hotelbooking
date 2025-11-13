// src/main/java/com/example/hotelbooking/service/PaymentService.java
package com.example.hotelbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.Payment;
import com.example.hotelbooking.model.PaymentStatus;
import com.example.hotelbooking.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Process payment
    public Payment processPayment(Payment payment) {
        // Simulate payment processing logic
        if (payment.getAmount() > 0) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        return paymentRepository.save(payment);
    }

    // Get payment by ID
    public Payment getPaymentById(Long id) {
        if (id == null) {
            return null;
        }
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.orElse(null);
    }

    // Get payment by booking ID
    public Payment getPaymentByBookingId(Long bookingId) {
        if (bookingId == null) {
            return null;
        }
        return paymentRepository.findByBookingId(bookingId);
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Update payment status
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null) {
            payment.setStatus(status);
            return paymentRepository.save(payment);
        }
        return null;
    }

    // Delete payment
    public void deletePayment(Long id) {
        if (id != null) {
            paymentRepository.deleteById(id);
        }
    }
}