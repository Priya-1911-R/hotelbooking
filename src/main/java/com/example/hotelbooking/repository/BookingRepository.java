package com.example.hotelbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by username
    List<Booking> findByUsername(String username);
    
    // Find booking by ID and username (for security)
    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.username = :username")
    Optional<Booking> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);
    
    // Find by status
    List<Booking> findByStatus(BookingStatus status);
    
    boolean existsById(Long id);
    
    // Find active bookings (not cancelled)
    @Query("SELECT b FROM Booking b WHERE b.status != 'CANCELLED'")
    List<Booking> findActiveBookings();
    
    // Find bookings by hotel ID
    List<Booking> findByHotelId(String hotelId);
    
    // Find bookings by hotel ID and username
    @Query("SELECT b FROM Booking b WHERE b.hotelId = :hotelId AND b.username = :username")
    List<Booking> findByHotelIdAndUsername(@Param("hotelId") String hotelId, @Param("username") String username);
    
    // Count bookings by status
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") BookingStatus status);
    
    // Find recent bookings (ordered by booking date descending)
    @Query("SELECT b FROM Booking b ORDER BY b.bookingDate DESC")
    List<Booking> findAllOrderByBookingDateDesc();
}