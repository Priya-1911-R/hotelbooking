package com.example.hotelbooking.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.Payment;
import com.example.hotelbooking.model.PaymentStatus;
import com.example.hotelbooking.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setHotelName("Test Hotel");

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setBooking(testBooking); // FIXED: Use setBooking() not setBookingId()
        testPayment.setAmount(199.99);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setStatus(PaymentStatus.PENDING);
        testPayment.setTransactionId("TXN12345");
        testPayment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    void testProcessPayment_Success() {
        // Given
        testPayment.setAmount(199.99);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        Payment processedPayment = paymentService.processPayment(testPayment);

        // Then
        assertNotNull(processedPayment);
        assertEquals(PaymentStatus.COMPLETED, processedPayment.getStatus());
        assertEquals(199.99, processedPayment.getAmount());
        verify(paymentRepository, times(1)).save(testPayment);
    }

    @Test
    void testProcessPayment_Failed() {
        // Given
        testPayment.setAmount(0.0);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        Payment processedPayment = paymentService.processPayment(testPayment);

        // Then
        assertNotNull(processedPayment);
        assertEquals(PaymentStatus.FAILED, processedPayment.getStatus());
        assertEquals(0.0, processedPayment.getAmount());
        verify(paymentRepository, times(1)).save(testPayment);
    }

    @Test
    void testGetPaymentById_Found() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When
        Payment foundPayment = paymentService.getPaymentById(1L);

        // Then
        assertNotNull(foundPayment);
        assertEquals(1L, foundPayment.getId());
        assertEquals(testBooking, foundPayment.getBooking()); // FIXED: Use getBooking() not getBookingId()
        assertEquals("CREDIT_CARD", foundPayment.getPaymentMethod());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Given
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Payment foundPayment = paymentService.getPaymentById(999L);

        // Then
        assertNull(foundPayment);
        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    void testGetPaymentByBookingId_Found() {
        // Given
        when(paymentRepository.findByBookingId(1L)).thenReturn(testPayment);

        // When
        Payment foundPayment = paymentService.getPaymentByBookingId(1L);

        // Then
        assertNotNull(foundPayment);
        assertEquals(testBooking, foundPayment.getBooking()); // FIXED: Use getBooking() not getBookingId()
        assertEquals(199.99, foundPayment.getAmount());
        verify(paymentRepository, times(1)).findByBookingId(1L);
    }

    @Test
    void testGetPaymentByBookingId_NotFound() {
        // Given
        when(paymentRepository.findByBookingId(999L)).thenReturn(null);

        // When
        Payment foundPayment = paymentService.getPaymentByBookingId(999L);

        // Then
        assertNull(foundPayment);
        verify(paymentRepository, times(1)).findByBookingId(999L);
    }

    @Test
    void testGetAllPayments() {
        // Given
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findAll()).thenReturn(payments);

        // When
        List<Payment> foundPayments = paymentService.getAllPayments();

        // Then
        assertNotNull(foundPayments);
        assertEquals(1, foundPayments.size());
        assertEquals(1L, foundPayments.get(0).getId());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        Payment updatedPayment = paymentService.updatePaymentStatus(1L, PaymentStatus.COMPLETED);

        // Then
        assertNotNull(updatedPayment);
        assertEquals(PaymentStatus.COMPLETED, updatedPayment.getStatus());
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(testPayment);
    }

    @Test
    void testDeletePayment() {
        // When
        paymentService.deletePayment(1L);

        // Then
        verify(paymentRepository, times(1)).deleteById(1L);
    }
}