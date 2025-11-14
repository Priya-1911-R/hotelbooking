package com.example.hotelbooking.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.model.Payment;
import com.example.hotelbooking.model.PaymentStatus;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private Booking testBooking;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // Create and persist a Booking first
        testBooking = new Booking();
        testBooking.setHotelId("1");
        testBooking.setHotelName("Test Hotel");
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setGuests(2);
        testBooking.setTotalPrice(new BigDecimal("199.99"));
        testBooking.setUsername("testuser");
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setBookingDate(LocalDate.now());

        entityManager.persist(testBooking);
        entityManager.flush();

        // Create and persist a Payment
        testPayment = new Payment();
        testPayment.setBooking(testBooking); // FIXED: Use setBooking() not setBookingId()
        testPayment.setAmount(199.99);
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setTransactionId("TXN12345");
        testPayment.setPaymentDate(LocalDateTime.now());

        entityManager.persist(testPayment);
        entityManager.flush();
    }

    @Test
    void testFindByBookingId() {
        // When
        Payment foundPayment = paymentRepository.findByBookingId(testBooking.getId());

        // Then
        assertNotNull(foundPayment);
        assertEquals(testBooking.getId(), foundPayment.getBooking().getId()); // FIXED: Use getBooking().getId()
        assertEquals(199.99, foundPayment.getAmount());
        assertEquals("CREDIT_CARD", foundPayment.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, foundPayment.getStatus());
    }

    @Test
    void testFindByBookingId_NotFound() {
        // When
        Payment foundPayment = paymentRepository.findByBookingId(999L);

        // Then
        assertNull(foundPayment);
    }

    @Test
    void testSavePayment() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setHotelId("2");
        newBooking.setHotelName("New Hotel");
        newBooking.setCheckInDate(LocalDate.now().plusDays(5));
        newBooking.setCheckOutDate(LocalDate.now().plusDays(7));
        newBooking.setGuests(1);
        newBooking.setTotalPrice(new BigDecimal("99.99"));
        newBooking.setUsername("newuser");
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setBookingDate(LocalDate.now());

        entityManager.persist(newBooking);
        entityManager.flush();

        Payment newPayment = new Payment();
        newPayment.setBooking(newBooking); // FIXED: Use setBooking() not setBookingId()
        newPayment.setAmount(299.99);
        newPayment.setStatus(PaymentStatus.PENDING);
        newPayment.setPaymentMethod("DEBIT_CARD");
        newPayment.setTransactionId("TXN67890");
        newPayment.setPaymentDate(LocalDateTime.now());

        // When
        Payment savedPayment = paymentRepository.save(newPayment);

        // Then
        assertNotNull(savedPayment.getId());
        assertEquals(newBooking.getId(), savedPayment.getBooking().getId()); // FIXED: Use getBooking().getId()
        assertEquals(299.99, savedPayment.getAmount());
    }

    @Test
    void testFindById() {
        // When
        Optional<Payment> foundPayment = paymentRepository.findById(testPayment.getId());

        // Then
        assertTrue(foundPayment.isPresent());
        assertEquals(testPayment.getId(), foundPayment.get().getId());
        assertEquals(testBooking.getId(), foundPayment.get().getBooking().getId());
    }
}