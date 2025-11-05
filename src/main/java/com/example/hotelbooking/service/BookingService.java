package com.example.hotelbooking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.repository.BookingRepository;
import com.example.hotelbooking.repository.HotelRepository;
import com.example.hotelbooking.repository.UserRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public BookingService(BookingRepository br, UserRepository ur, HotelRepository hr) {
        this.bookingRepository = br;
        this.userRepository = ur;
        this.hotelRepository = hr;
    }

    public Booking createBooking(
            @NonNull Long userId,
            @NonNull Long hotelId,
            @NonNull LocalDate checkIn,
            @NonNull LocalDate checkOut,
            @NonNull Double totalPrice) {

        // find user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // find hotel
        var hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // Check if rooms are available for given dates
        List<Booking> overlapping = bookingRepository
                .findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                        hotelId, checkOut, checkIn);

        if (overlapping.size() >= hotel.getTotalRooms()) {
            throw new RuntimeException("No rooms available for selected dates");
        }

        // Create new booking
        Booking b = new Booking();
        b.setUser(user);
        b.setHotel(hotel);
        b.setCheckInDate(checkIn);
        b.setCheckOutDate(checkOut);
        b.setTotalPrice(totalPrice);
        b.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(b);
    }

    public List<Booking> getBookingsForUser(@NonNull Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUser(user);
    }

    public Optional<Booking> findById(@NonNull Long id) {
        return bookingRepository.findById(id);
    }

    public Booking cancelBooking(@NonNull Long id, @NonNull Long userId) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
