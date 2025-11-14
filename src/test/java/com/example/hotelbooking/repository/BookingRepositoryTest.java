package com.example.hotelbooking.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;

@ExtendWith(MockitoExtension.class)
class BookingRepositoryTest {

    @Mock
    private BookingRepository bookingRepository;

    @Test
    void testFindByUsername() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUsername("testuser");

        when(bookingRepository.findByUsername("testuser"))
            .thenReturn(Arrays.asList(booking));

        List<Booking> bookings = bookingRepository.findByUsername("testuser");

        assertEquals(1, bookings.size());
        assertEquals("testuser", bookings.get(0).getUsername());
        verify(bookingRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByIdAndUsername() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUsername("testuser");

        when(bookingRepository.findByIdAndUsername(1L, "testuser"))
            .thenReturn(Optional.of(booking));

        Optional<Booking> foundBooking = bookingRepository.findByIdAndUsername(1L, "testuser");

        assertTrue(foundBooking.isPresent());
        assertEquals(1L, foundBooking.get().getId());
        assertEquals("testuser", foundBooking.get().getUsername());
        verify(bookingRepository, times(1)).findByIdAndUsername(1L, "testuser");
    }

    @Test
    void testFindByStatus() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findByStatus(BookingStatus.PENDING))
            .thenReturn(Arrays.asList(booking));

        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.PENDING);

        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.PENDING, bookings.get(0).getStatus());
        verify(bookingRepository, times(1)).findByStatus(BookingStatus.PENDING);
    }

    @Test
    void testExistsById() {
        when(bookingRepository.existsById(1L)).thenReturn(true);

        boolean exists = bookingRepository.existsById(1L);

        assertTrue(exists);
        verify(bookingRepository, times(1)).existsById(1L);
    }
}