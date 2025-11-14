package com.example.hotelbooking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setHotelId("1");
        testBooking.setHotelName("Test Hotel");
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setGuests(2);
        testBooking.setTotalPrice(new BigDecimal("199.99"));
        testBooking.setUsername("testuser");
        testBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    void testSaveBooking() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking savedBooking = bookingService.save(testBooking);

        assertNotNull(savedBooking);
        assertEquals("Test Hotel", savedBooking.getHotelName());
        assertEquals(BookingStatus.PENDING, savedBooking.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testFindById() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        Booking foundBooking = bookingService.findById(1L);

        assertNotNull(foundBooking);
        assertEquals(1L, foundBooking.getId());
        assertEquals("Test Hotel", foundBooking.getHotelName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        Booking foundBooking = bookingService.findById(999L);

        assertNull(foundBooking);
        verify(bookingRepository, times(1)).findById(999L);
    }

    @Test
    void testFindByUsername() {
        List<Booking> userBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByUsername("testuser")).thenReturn(userBookings);

        List<Booking> foundBookings = bookingService.findByUsername("testuser");

        assertEquals(1, foundBookings.size());
        verify(bookingRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testUpdateBookingStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking updatedBooking = bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED);

        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.CONFIRMED, updatedBooking.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testCancelBooking() {
        when(bookingRepository.findByIdAndUsername(1L, "testuser"))
            .thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking cancelledBooking = bookingService.cancelBooking(1L, "testuser");

        assertNotNull(cancelledBooking);
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        verify(bookingRepository, times(1)).findByIdAndUsername(1L, "testuser");
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testConfirmPayment() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking confirmedBooking = bookingService.confirmPayment(1L, "CREDIT_CARD");

        assertNotNull(confirmedBooking);
        assertEquals(BookingStatus.CONFIRMED, confirmedBooking.getStatus());
        assertEquals("CREDIT_CARD", confirmedBooking.getPaymentMethod());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testFindAll() {
        List<Booking> allBookings = Arrays.asList(testBooking);
        when(bookingRepository.findAllOrderByBookingDateDesc()).thenReturn(allBookings);

        List<Booking> foundBookings = bookingService.findAll();

        assertEquals(1, foundBookings.size());
        verify(bookingRepository, times(1)).findAllOrderByBookingDateDesc();
    }

    @Test
    void testGetBookingStats() {
        when(bookingRepository.count()).thenReturn(10L);
        when(bookingRepository.countByStatus(BookingStatus.PENDING)).thenReturn(2L);
        when(bookingRepository.countByStatus(BookingStatus.CONFIRMED)).thenReturn(7L);
        when(bookingRepository.countByStatus(BookingStatus.CANCELLED)).thenReturn(1L);

        BookingService.BookingStats stats = bookingService.getBookingStats();

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalBookings());
        assertEquals(2L, stats.getPendingBookings());
        assertEquals(7L, stats.getConfirmedBookings());
        assertEquals(1L, stats.getCancelledBookings());
    }
}