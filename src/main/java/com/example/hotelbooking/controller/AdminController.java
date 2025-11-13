package com.example.hotelbooking.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.service.BookingService;
import com.example.hotelbooking.service.HotelService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    public AdminController(HotelService hotelService, BookingService bookingService) {
        this.hotelService = hotelService;
        this.bookingService = bookingService;
    }
    
    // FIXED: Removed duplicate "/admin" prefix and added Authentication import
    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("title", "Admin Dashboard");
        
        // Add stats for the dashboard
        List<Hotel> hotels = hotelService.getAllHotels();
        List<Booking> bookings = bookingService.findAll();
        
        model.addAttribute("totalHotels", hotels.size());
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("recentHotels", hotels.stream().limit(5).toList());
        
        return "admin-dashboard";
    }

    // FIXED: Removed duplicate "/admin" prefix
    @GetMapping("/users")
    public String manageUsers() {
        return "admin-users";
    }

    // FIXED: Removed duplicate "/admin" prefix
    @GetMapping("/hotels")
    public String manageHotels(Model model) {
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        return "admin-hotels";
    }

    @PostMapping("/addHotel")
    public String addHotel(@RequestParam String name,
                          @RequestParam String location,
                          @RequestParam Double pricePerNight,
                          @RequestParam(required = false) Integer rating,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) String amenities) {
        
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setPricePerNight(BigDecimal.valueOf(pricePerNight));
        hotel.setRating(rating != null ? rating : 3);
        hotel.setDescription(description);
        hotel.setAmenities(amenities);
        hotel.setFeatured(false);
        
        hotelService.saveHotel(hotel);
        return "redirect:/admin/dashboard?success=Hotel added successfully";
    }

    @PostMapping("/deleteHotel")
    public String deleteHotel(@RequestParam Long id) {
        try {
            hotelService.deleteHotel(id);
            return "redirect:/admin/dashboard?success=Hotel deleted successfully";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=Error deleting hotel: " + e.getMessage();
        }
    }

    @GetMapping("/editHotel")
    public String editHotelForm(@RequestParam Long id, Model model) {
        try {
            Hotel hotel = hotelService.getHotelById(id);
            model.addAttribute("hotel", hotel);
            return "admin-edit-hotel";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=Hotel not found";
        }
    }

    @PostMapping("/updateHotel")
    public String updateHotel(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam String location,
                             @RequestParam Double pricePerNight,
                             @RequestParam(required = false) Integer rating,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String amenities,
                             @RequestParam(required = false) Boolean featured) {
        
        try {
            Hotel hotel = hotelService.getHotelById(id);
            hotel.setName(name);
            hotel.setLocation(location);
            hotel.setPricePerNight(BigDecimal.valueOf(pricePerNight));
            hotel.setRating(rating != null ? rating : hotel.getRating());
            hotel.setDescription(description);
            hotel.setAmenities(amenities);
            hotel.setFeatured(featured != null ? featured : false);
            
            hotelService.saveHotel(hotel);
            return "redirect:/admin/dashboard?success=Hotel updated successfully";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=Hotel not found";
        }
    }

    // Add this method to your existing AdminController
@GetMapping("/bookings")
public String manageBookings(Model model) {
    List<Booking> allBookings = bookingService.findAll();
    BookingService.BookingStats stats = bookingService.getBookingStats();
    
    model.addAttribute("bookings", allBookings);
    model.addAttribute("stats", stats);
    return "admin-bookings";
}

@PostMapping("/updateBookingStatus")
public String updateBookingStatus(@RequestParam Long bookingId, 
                                @RequestParam BookingStatus status) {
    try {
        Booking booking = bookingService.updateBookingStatus(bookingId, status);
        if (booking != null) {
            return "redirect:/admin/bookings?success=Booking status updated successfully";
        }
        return "redirect:/admin/bookings?error=Booking not found";
    } catch (Exception e) {
        return "redirect:/admin/bookings?error=Error updating booking: " + e.getMessage();
    }
}
    // Quick actions for testing
    @GetMapping("/test-data")
    public String addTestData() {
        // Add some test hotels if database is empty
        if (hotelService.getAllHotels().isEmpty()) {
            Hotel hotel1 = new Hotel();
            hotel1.setName("Grand Plaza Hotel");
            hotel1.setLocation("New York");
            hotel1.setPricePerNight(new BigDecimal("199.99"));
            hotel1.setRating(5);
            hotel1.setDescription("Luxury hotel in downtown New York");
            hotel1.setAmenities("Pool, Spa, Gym, Restaurant");
            hotel1.setFeatured(true);
            
            Hotel hotel2 = new Hotel();
            hotel2.setName("Beach Resort");
            hotel2.setLocation("Miami");
            hotel2.setPricePerNight(new BigDecimal("149.99"));
            hotel2.setRating(4);
            hotel2.setDescription("Beautiful beachfront resort");
            hotel2.setAmenities("Beach Access, Pool, Bar");
            hotel2.setFeatured(true);
            
            hotelService.saveHotel(hotel1);
            hotelService.saveHotel(hotel2);
        }
        return "redirect:/admin/dashboard?success=Test data added";
    }
    
    @GetMapping("/test-auth")
    @ResponseBody
    public String testAuth() {
        return """
            <h1>Authentication Test</h1>
            <div style="background: #e8f5e8; padding: 20px; border-radius: 10px; margin: 20px 0;">
                <h3>✅ Use these credentials to login:</h3>
                <p><strong>Admin:</strong> username: <code>admin</code> | password: <code>admin123</code></p>
                <p><strong>User:</strong> username: <code>user</code> | password: <code>user123</code></p>
            </div>
            <p><a href="/login" style="background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Go to Login Page</a></p>
            """;
    }
}