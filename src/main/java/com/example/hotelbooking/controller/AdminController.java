package com.example.hotelbooking.controller;

import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.UserRepository;
import com.example.hotelbooking.service.BookingService;
import com.example.hotelbooking.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final HotelService hotelService;
    private final BookingService bookingService;

    public AdminController(UserRepository ur, HotelService hs, BookingService bs) {
        this.userRepository = ur;
        this.hotelService = hs;
        this.bookingService = bs;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/hotels")
    public ResponseEntity<?> allHotels() {
        return ResponseEntity.ok(hotelService.getAll());
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> allBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
