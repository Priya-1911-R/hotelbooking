package com.example.hotelbooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotelbooking.model.Payment;
import com.example.hotelbooking.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService ps) { this.paymentService = ps; }

    // Simulate payment (for testing). In real integration, use provider hooks/callbacks.
    @PostMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestParam Long bookingId, @RequestParam Double amount, @RequestParam(defaultValue = "true") boolean success) {
        Payment p = paymentService.processPayment(bookingId, amount, "SIM-TXN-" + System.currentTimeMillis(), success);
        return ResponseEntity.ok(p);
    }
}
