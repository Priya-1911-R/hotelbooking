package com.example.hotelbooking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find overlapping bookings for a hotel
    List<Booking> findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long hotelId, LocalDate checkOut, LocalDate checkIn);

    // Find all bookings made by a specific user
    List<Booking> findByUser(User user);
}
