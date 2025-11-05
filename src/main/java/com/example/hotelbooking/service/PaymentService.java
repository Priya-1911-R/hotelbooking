package com.example.hotelbooking.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.model.Payment;
import com.example.hotelbooking.repository.BookingRepository;
import com.example.hotelbooking.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository pr, BookingRepository br) {
        this.paymentRepository = pr;
        this.bookingRepository = br;
    }

    // Simulate payment processing (replace with Razorpay/Stripe integration later)
    public Payment processPayment(@NonNull Long bookingId, @NonNull Double amount,
                                  @NonNull String providerTxnId, boolean succeed) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment p = new Payment();
        p.setBookingId(bookingId);
        p.setAmount(amount);
        p.setProviderTxnId(providerTxnId);
        p.setPaymentStatus(succeed ? "SUCCESS" : "FAILED");

        Payment saved = paymentRepository.save(p);

        if (succeed) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.PENDING);
        }

        bookingRepository.save(booking);
        return saved;
    }
}
