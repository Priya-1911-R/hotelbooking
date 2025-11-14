package com.example.hotelbooking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.repository.BookingRepository;

@Service
@SuppressWarnings("NonnullTypeParameter")
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Save a booking
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Find booking by ID
    public Booking findById(@NonNull Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElse(null);
    }

    // Find booking by ID and username (for security)
    public Booking findByIdAndUsername(@NonNull Long id, String username) {
        Optional<Booking> booking = bookingRepository.findByIdAndUsername(id, username);
        return booking.orElse(null);
    }

    // Find all bookings by username
    public List<Booking> findByUsername(String username) {
        return bookingRepository.findByUsername(username);
    }

    // Find bookings by hotelId (use String)
    public List<Booking> findByHotelId(String hotelId) {
        return bookingRepository.findByHotelId(hotelId);
    }

    // Update booking status
    public Booking updateBookingStatus(@NonNull Long id, BookingStatus status) {
        Booking booking = findById(id);
        if (booking != null) {
            booking.setStatus(status);
            return bookingRepository.save(booking);
        }
        return null;
    }

    // Cancel a booking
    public Booking cancelBooking(@NonNull Long id, String username) {
        Booking booking = findByIdAndUsername(id, username);
        if (booking != null) {
            booking.setStatus(BookingStatus.CANCELLED);
            return bookingRepository.save(booking);
        }
        return null;
    }

    // Confirm payment for a booking
    public Booking confirmPayment(Long bookingId, String paymentMethod) {
        Booking booking = findById(bookingId);
        if (booking != null) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaymentMethod(paymentMethod);
            return save(booking);
        }
        return null;
    }

    // Get all bookings (admin use)
    public List<Booking> findAll() {
        return bookingRepository.findAllOrderByBookingDateDesc();
    }

    // Get active bookings (not cancelled)
    public List<Booking> findActiveBookings() {
        return bookingRepository.findActiveBookings();
    }

    // Check if booking exists
    public boolean existsById(@NonNull Long id) {
        return bookingRepository.existsById(id);
    }

    // Delete booking (admin use)
    public void deleteBooking(@NonNull Long id) {
        bookingRepository.deleteById(id);
    }

    // Get booking statistics
    public BookingStats getBookingStats() {
        Long totalBookings = bookingRepository.count();
        Long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        Long confirmedBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        Long cancelledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        
        return new BookingStats(totalBookings, pendingBookings, confirmedBookings, cancelledBookings);
    }

    // Check if user has booking for specific hotel
    public boolean hasBookingForHotel(String username, String hotelId) {
        List<Booking> bookings = bookingRepository.findByHotelIdAndUsername(hotelId, username);
        return bookings.stream()
                .anyMatch(booking -> booking.getStatus() != BookingStatus.CANCELLED);
    }

    // Get upcoming bookings for user
    public List<Booking> getUpcomingBookings(String username) {
        List<Booking> userBookings = bookingRepository.findByUsername(username);
        LocalDate today = LocalDate.now();
        
        return userBookings.stream()
                .filter(booking -> booking.getCheckInDate().isAfter(today) || 
                                  booking.getCheckInDate().isEqual(today))
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }

    // Statistics class
    public static class BookingStats {
        private final Long totalBookings;
        private final Long pendingBookings;
        private final Long confirmedBookings;
        private final Long cancelledBookings;

        public BookingStats(Long totalBookings, Long pendingBookings, Long confirmedBookings, Long cancelledBookings) {
            this.totalBookings = totalBookings;
            this.pendingBookings = pendingBookings;
            this.confirmedBookings = confirmedBookings;
            this.cancelledBookings = cancelledBookings;
        }

        // Getters
        public Long getTotalBookings() { return totalBookings; }
        public Long getPendingBookings() { return pendingBookings; }
        public Long getConfirmedBookings() { return confirmedBookings; }
        public Long getCancelledBookings() { return cancelledBookings; }
    }
}