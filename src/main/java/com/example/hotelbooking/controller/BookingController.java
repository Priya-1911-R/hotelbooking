package com.example.hotelbooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotelbooking.dto.BookingRequest;
import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.service.BookingService;
import com.example.hotelbooking.service.UserService;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bs, UserService us) {
        this.bookingService = bs;
        this.userService = us;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookingRequest req, Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Booking b = bookingService.createBooking(user.getId(), req.getHotelId(), req.getCheckIn(), req.getCheckOut(), req.getTotalPrice());
        return ResponseEntity.ok(b);
    }

    @GetMapping("/me")
    public ResponseEntity<?> myBookings(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> list = bookingService.getBookingsForUser(user.getId());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        var canceled = bookingService.cancelBooking(id, user.getId());
        return ResponseEntity.ok(canceled);
    }

    @GetMapping
    public ResponseEntity<?> allBookings(Authentication auth) {
        // admin or user: only admin can get all
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(bookingService.getAllBookings());
        } else {
            return ResponseEntity.status(403).body("Forbidden");
        }
    }
}
