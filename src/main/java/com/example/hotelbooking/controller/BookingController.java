package com.example.hotelbooking.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.service.BookingService;
import com.example.hotelbooking.service.HotelService;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final HotelService hotelService;

    public BookingController(BookingService bookingService, HotelService hotelService) {
        this.bookingService = bookingService;
        this.hotelService = hotelService;
    }

    @GetMapping("/book")
    public String showBookingPage(@RequestParam(required = false) String hotelId, 
                                 @RequestParam(required = false) String hotelName,
                                 @RequestParam(required = false) Double pricePerNight,
                                 Model model) {
        
        System.out.println("🎯 BOOKING PAGE CALLED!");
        System.out.println("🏨 Hotel ID: " + hotelId);
        
        if (hotelId != null && !hotelId.trim().isEmpty()) {
            try {
                Long hotelIdLong = Long.parseLong(hotelId);
                Hotel hotel = hotelService.getHotelById(hotelIdLong);
                
                model.addAttribute("hotelId", hotel.getId().toString());
                model.addAttribute("hotelName", hotel.getName());
                model.addAttribute("pricePerNight", hotel.getPricePerNight());
                model.addAttribute("location", hotel.getLocation());
                model.addAttribute("hotel", hotel);
                
                System.out.println("✅ Fetched from DB - Hotel Name: " + hotel.getName());
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid hotel ID format: " + hotelId);
                model.addAttribute("error", "Invalid hotel ID format");
            } catch (RuntimeException e) {
                System.out.println("❌ Error fetching hotel data: " + e.getMessage());
                model.addAttribute("hotelId", hotelId);
                model.addAttribute("hotelName", hotelName != null ? hotelName : "Selected Hotel");
                model.addAttribute("pricePerNight", pricePerNight != null ? pricePerNight : 0);
                model.addAttribute("location", "Location not specified");
                model.addAttribute("warning", "Hotel details loaded from backup data");
            }
        } else {
            System.out.println("❌ No hotel ID provided!");
            model.addAttribute("error", "No hotel selected. Please go back and select a hotel.");
        }
        
        model.addAttribute("defaultCheckIn", LocalDate.now().plusDays(1));
        model.addAttribute("defaultCheckOut", LocalDate.now().plusDays(3));
        
        return "booking";
    }

    @PostMapping("/create")
    public String createBooking(@RequestParam String hotelId,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
                            @RequestParam Integer guests,
                            @RequestParam String totalPrice,
                            RedirectAttributes redirectAttributes) {
        
        System.out.println("🎯 CREATE BOOKING ENDPOINT CALLED!");
        System.out.println("📝 Parameters received:");
        System.out.println("🏨 Hotel ID: " + hotelId);
        System.out.println("📅 Check-in: " + checkInDate);
        System.out.println("📅 Check-out: " + checkOutDate);
        System.out.println("👥 Guests: " + guests);
        System.out.println("💰 Total Price: " + totalPrice);
        
        try {
            BigDecimal totalPriceDecimal = new BigDecimal(totalPrice);
            String username = getCurrentUsername();
            System.out.println("👤 Username: " + username);
            
            if (checkInDate.isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error", "Check-in date cannot be in the past");
                return "redirect:/booking/book?hotelId=" + hotelId;
            }
            
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                redirectAttributes.addFlashAttribute("error", "Check-out date must be after check-in date");
                return "redirect:/booking/book?hotelId=" + hotelId;
            }
            
            // Get hotel name from the hotel service
            String hotelName = "Hotel";
            try {
                Long hotelIdLong = Long.parseLong(hotelId);
                Hotel hotel = hotelService.getHotelById(hotelIdLong);
                if (hotel != null) {
                    hotelName = hotel.getName();
                    System.out.println("🏨 Hotel name found: " + hotelName);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not fetch hotel name: " + e.getMessage());
            }
            
            Booking booking = new Booking();
            booking.setHotelId(hotelId);
            booking.setHotelName(hotelName);
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);
            booking.setGuests(guests);
            booking.setTotalPrice(totalPriceDecimal);
            booking.setUsername(username);
            booking.setStatus(BookingStatus.PENDING);
            booking.setBookingDate(LocalDate.now());

            System.out.println("💾 Saving booking to database...");
            Booking savedBooking = bookingService.save(booking);
            System.out.println("✅ Booking saved with ID: " + savedBooking.getId());
            
            if (savedBooking == null || savedBooking.getId() == null) {
                System.out.println("❌ Booking save failed - savedBooking is null or has no ID");
                redirectAttributes.addFlashAttribute("error", "Failed to create booking. Please try again.");
                return "redirect:/booking/book?hotelId=" + hotelId;
            }
            
            System.out.println("🔗 Redirecting to: /payment?bookingId=" + savedBooking.getId());
            return "redirect:/payment?bookingId=" + savedBooking.getId();
            
        } catch (Exception e) {
            System.out.println("❌ Error creating booking: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to create booking: " + e.getMessage());
            return "redirect:/booking/book?hotelId=" + hotelId;
        }
    }

    @GetMapping("/fix-existing-booking")
    @ResponseBody
    public String fixExistingBooking(@RequestParam Long bookingId) {
        try {
            Booking booking = bookingService.findById(bookingId);
            if (booking != null) {
                System.out.println("🔧 Fixing booking ID: " + bookingId);
                System.out.println("📊 Current hotel name: " + booking.getHotelName());
                
                // Get proper hotel name
                try {
                    Long hotelIdLong = Long.parseLong(booking.getHotelId());
                    Hotel hotel = hotelService.getHotelById(hotelIdLong);
                    if (hotel != null) {
                        booking.setHotelName(hotel.getName());
                        bookingService.save(booking);
                        return "✅ Booking " + bookingId + " fixed! Hotel name set to: " + hotel.getName();
                    }
                } catch (Exception e) {
                    booking.setHotelName("Hotel " + booking.getHotelId());
                    bookingService.save(booking);
                    return "✅ Booking " + bookingId + " fixed with default name: Hotel " + booking.getHotelId();
                }
            }
            return "❌ Booking not found with ID: " + bookingId;
        } catch (Exception e) {
            return "❌ Error fixing booking: " + e.getMessage();
        }
    }

    @PostMapping("/confirm")
    public String confirmBookingDirect(@RequestParam Long bookingId,
                                      RedirectAttributes redirectAttributes) {
        try {
            String username = getCurrentUsername();
            System.out.println("✅ Direct confirmation for booking: " + bookingId);
            
            Booking existingBooking = bookingService.findByIdAndUsername(bookingId, username);
            
            if (existingBooking != null) {
                Booking booking = bookingService.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
                
                if (booking != null) {
                    redirectAttributes.addFlashAttribute("success", true);
                    redirectAttributes.addFlashAttribute("booking", booking);
                    redirectAttributes.addFlashAttribute("message", "Booking confirmed successfully!");
                    return "redirect:/booking/confirmation?bookingId=" + bookingId;
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Booking not found or you don't have permission.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error confirming booking: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to confirm booking: " + e.getMessage());
        }
        
        return "redirect:/booking/list";
    }

  @GetMapping("/confirmation")
public String bookingConfirmation(@RequestParam(value = "bookingId", required = false) Long bookingId,
                                 Model model) {
    
    System.out.println("🎯 CONFIRMATION PAGE - Booking ID: " + bookingId);
    
    try {
        // If no booking ID provided
        if (bookingId == null) {
            System.out.println("❌ No booking ID provided");
            model.addAttribute("error", "No booking ID provided. Please check your booking confirmation link.");
            return "booking-confirmation"; // Always return booking-confirmation, NOT "error"
        }
        
        // Find the booking
        Booking booking = bookingService.findById(bookingId);
        
        if (booking == null) {
            System.out.println("❌ Booking not found: " + bookingId);
            model.addAttribute("error", "Booking not found with ID: " + bookingId);
            return "booking-confirmation"; // Always return booking-confirmation, NOT "error"
        }
        
        System.out.println("✅ Booking found: " + booking.getId());
        model.addAttribute("booking", booking);
        return "booking-confirmation"; // Always return booking-confirmation, NOT "error"
        
    } catch (Exception e) {
        System.out.println("❌ ERROR in confirmation: " + e.getMessage());
        model.addAttribute("error", "System error: " + e.getMessage());
        return "booking-confirmation"; // Always return booking-confirmation, NOT "error"
    }
}
@GetMapping("/confirmation-simple")
public String confirmationSimple(@RequestParam Long bookingId, Model model) {
    System.out.println("🔧 SIMPLE CONFIRMATION TEST - Booking ID: " + bookingId);
    
    try {
        // Just test if we can get the booking
        Booking booking = bookingService.findById(bookingId);
        System.out.println("✅ Booking found: " + booking.getId());
        
        // Create a safe booking object with only basic fields
        Booking safeBooking = new Booking();
        safeBooking.setId(booking.getId());
        safeBooking.setHotelId("1");
        safeBooking.setHotelName("Test Hotel");
        safeBooking.setCheckInDate(LocalDate.now());
        safeBooking.setCheckOutDate(LocalDate.now().plusDays(2));
        safeBooking.setGuests(2);
        safeBooking.setTotalPrice(new BigDecimal("100.00"));
        safeBooking.setStatus(BookingStatus.CONFIRMED);
        
        model.addAttribute("booking", safeBooking);
        return "booking-confirmation";
        
    } catch (Exception e) {
        System.out.println("❌ ERROR: " + e.getMessage());
        model.addAttribute("error", "Error: " + e.getMessage());
        return "booking-confirmation";
    }
}
@GetMapping("/debug-confirmation")
@ResponseBody
public String debugConfirmation(@RequestParam Long bookingId) {
    try {
        System.out.println("🔍 DEBUG CONFIRMATION - Booking ID: " + bookingId);
        
        // Check if booking exists
        Booking booking = bookingService.findById(bookingId);
        if (booking == null) {
            return "❌ Booking not found with ID: " + bookingId;
        }
        
        // Check all booking fields
        StringBuilder result = new StringBuilder();
        result.append("✅ Booking FOUND:<br>");
        result.append("ID: ").append(booking.getId()).append("<br>");
        result.append("Status: ").append(booking.getStatus()).append("<br>");
        result.append("Hotel ID: ").append(booking.getHotelId()).append("<br>");
        result.append("Hotel Name: ").append(booking.getHotelName()).append("<br>");
        result.append("Total Price: ").append(booking.getTotalPrice()).append("<br>");
        result.append("Check-in: ").append(booking.getCheckInDate()).append("<br>");
        result.append("Check-out: ").append(booking.getCheckOutDate()).append("<br>");
        result.append("Guests: ").append(booking.getGuests()).append("<br>");
        result.append("Username: ").append(booking.getUsername()).append("<br>");
        
        // Check for null values
        result.append("<br>🔍 NULL CHECKS:<br>");
        result.append("Hotel ID is null: ").append(booking.getHotelId() == null).append("<br>");
        result.append("Hotel Name is null: ").append(booking.getHotelName() == null).append("<br>");
        result.append("Total Price is null: ").append(booking.getTotalPrice() == null).append("<br>");
        
        result.append("<br>🔗 <a href='/booking/confirmation?bookingId=").append(bookingId).append("'>Try Confirmation Page</a>");
        
        return result.toString();
        
    } catch (Exception e) {
        return "❌ ERROR: " + e.getMessage() + "<br>Stack Trace: " + getStackTrace(e);
    }
}

private String getStackTrace(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
}
@GetMapping("/test-db-connection")
@ResponseBody
public String testDbConnection() {
    try {
        System.out.println("🔍 TESTING DATABASE CONNECTION...");
        
        // Test 1: Count all bookings
        List<Booking> allBookings = bookingService.findAll();
        System.out.println("📊 Total bookings in database: " + allBookings.size());
        
        // Test 2: Try to find booking with ID 8
        System.out.println("🔍 Looking for booking ID: 8");
        Booking booking8 = bookingService.findById(8L);
        System.out.println("📦 Booking 8 found: " + (booking8 != null ? "YES" : "NO"));
        
        // Test 3: Try to find any booking
        Long firstBookingId = null;
        if (!allBookings.isEmpty()) {
            firstBookingId = allBookings.get(0).getId();
            System.out.println("🔍 Looking for first booking ID: " + firstBookingId);
            Booking firstBooking = bookingService.findById(firstBookingId);
            System.out.println("📦 First booking found: " + (firstBooking != null ? "YES" : "NO"));
        }
        
        StringBuilder result = new StringBuilder();
        result.append("<h1>Database Test Results</h1>");
        result.append("Total bookings: ").append(allBookings.size()).append("<br>");
        result.append("Booking ID 8 found: ").append(booking8 != null ? "YES" : "NO").append("<br>");
        
        if (!allBookings.isEmpty()) {
            result.append("First booking ID: ").append(firstBookingId).append("<br>");
            result.append("<h2>All Bookings:</h2><ul>");
            for (Booking b : allBookings) {
                result.append("<li>ID: ").append(b.getId())
                      .append(" | Hotel: ").append(b.getHotelId())
                      .append(" | Status: ").append(b.getStatus())
                      .append(" | User: ").append(b.getUsername())
                      .append("</li>");
            }
            result.append("</ul>");
            
            if (firstBookingId != null) {
                result.append("<a href='/booking/confirmation?bookingId=").append(firstBookingId).append("'>Test Confirmation with ID ").append(firstBookingId).append("</a>");
            }
        } else {
            result.append("<p>No bookings found in database.</p>");
        }
        
        return result.toString();
        
    } catch (Exception e) {
        System.out.println("❌ DATABASE ERROR: " + e.getMessage());
        e.printStackTrace();
        return "❌ DATABASE ERROR: " + e.getMessage();
    }
}
@GetMapping("/create-test-booking")
@ResponseBody
public String createTestBooking() {
    try {
        System.out.println("🎯 CREATING TEST BOOKING...");
        
        Booking booking = new Booking();
        booking.setHotelId("1");
        booking.setHotelName("Test Hotel");
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setGuests(2);
        booking.setTotalPrice(new BigDecimal("199.99"));
        booking.setUsername("testuser");
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDate.now());
        
        System.out.println("💾 Saving test booking...");
        Booking saved = bookingService.save(booking);
        System.out.println("✅ Test booking created with ID: " + saved.getId());
        
        return "✅ Test booking created with ID: " + saved.getId() + 
               "<br><a href='/booking/confirmation?bookingId=" + saved.getId() + "'>View Confirmation</a>" +
               "<br><a href='/payment?bookingId=" + saved.getId() + "'>Go to Payment</a>";
               
    } catch (Exception e) {
        System.out.println("❌ ERROR creating test booking: " + e.getMessage());
        e.printStackTrace();
        return "❌ ERROR creating test booking: " + e.getMessage();
    }
}
@GetMapping("/debug-db")
@ResponseBody
public String debugDatabase() {
    try {
        System.out.println("🔍 DEBUGGING DATABASE...");
        
        // Get all bookings
        List<Booking> allBookings = bookingService.findAll();
        System.out.println("📊 Total bookings found: " + allBookings.size());
        
        // Check if booking ID 8 exists
        System.out.println("🔍 Checking for booking ID: 8");
        boolean booking8Exists = bookingService.existsById(8L);
        System.out.println("📦 Booking 8 exists: " + booking8Exists);
        
        StringBuilder result = new StringBuilder();
        result.append("<h1>Database Debug</h1>");
        result.append("<p>Total bookings: ").append(allBookings.size()).append("</p>");
        result.append("<p>Booking ID 8 exists: ").append(booking8Exists).append("</p>");
        
        if (allBookings.isEmpty()) {
            result.append("<div style='color: red; font-weight: bold;'>❌ NO BOOKINGS FOUND IN DATABASE!</div>");
            result.append("<p><a href='/booking/create-test-booking'>Create a test booking first</a></p>");
        } else {
            result.append("<h2>All Bookings in Database:</h2>");
            result.append("<table border='1' style='border-collapse: collapse;'>");
            result.append("<tr><th>ID</th><th>Hotel ID</th><th>Hotel Name</th><th>Status</th><th>Username</th><th>Total Price</th></tr>");
            
            for (Booking booking : allBookings) {
                result.append("<tr>");
                result.append("<td>").append(booking.getId()).append("</td>");
                result.append("<td>").append(booking.getHotelId()).append("</td>");
                result.append("<td>").append(booking.getHotelName()).append("</td>");
                result.append("<td>").append(booking.getStatus()).append("</td>");
                result.append("<td>").append(booking.getUsername()).append("</td>");
                result.append("<td>$").append(booking.getTotalPrice()).append("</td>");
                result.append("</tr>");
            }
            result.append("</table>");
            
            // Create links for each booking
            result.append("<h2>Test Links:</h2>");
            for (Booking booking : allBookings) {
                result.append("<p>");
                result.append("<a href='/booking/confirmation?bookingId=").append(booking.getId()).append("'>")
                      .append("Test Confirmation for Booking ").append(booking.getId())
                      .append("</a>");
                result.append("</p>");
            }
        }
        
        return result.toString();
        
    } catch (Exception e) {
        System.out.println("❌ ERROR in debug: " + e.getMessage());
        e.printStackTrace();
        return "❌ ERROR: " + e.getMessage();
    }
}

    @GetMapping("/details/{id}")
    public String bookingDetails(@PathVariable Long id, Model model) {
        String currentUsername = getCurrentUsername();
        Booking booking = bookingService.findByIdAndUsername(id, currentUsername);
        
        if (booking != null) {
            model.addAttribute("booking", booking);
            return "booking-details";
        } else {
            model.addAttribute("error", "Booking not found.");
            return "redirect:/booking/list";
        }
    }

    @GetMapping("/list")
    public String bookingList(Model model) {
        String currentUsername = getCurrentUsername();
        List<Booking> bookings = bookingService.findByUsername(currentUsername);
        model.addAttribute("bookings", bookings);
        return "booking-list";
    }

    @GetMapping("/list-all")
    @ResponseBody
    public String listAllBookings() {
        try {
            List<Booking> allBookings = bookingService.findAll();
            
            if (allBookings.isEmpty()) {
                return "📭 No bookings found in database";
            }
            
            StringBuilder result = new StringBuilder("<h1>All Bookings in Database:</h1><ul>");
            for (Booking booking : allBookings) {
                result.append("<li>ID: ").append(booking.getId())
                    .append(" | Hotel: ").append(booking.getHotelId())
                    .append(" | Total: $").append(booking.getTotalPrice())
                    .append(" | Status: ").append(booking.getStatus())
                    .append("</li>");
            }
            result.append("</ul>");
            
            return result.toString();
            
        } catch (Exception e) {
            return "❌ ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String currentUsername = getCurrentUsername();
        try {
            Booking booking = bookingService.cancelBooking(id, currentUsername);
            
            if (booking != null) {
                redirectAttributes.addFlashAttribute("message", "Booking cancelled successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Unable to cancel booking. Booking not found or already cancelled.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling booking: " + e.getMessage());
        }
        
        return "redirect:/booking/list";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testEndpoint() {
        return "✅ BookingController is working! Available endpoints: /book, /create, /list, /confirmation";
    }

    @GetMapping("/debug")
    @ResponseBody
    public String debugInfo() {
        return """
            <h1>BookingController Debug</h1>
            <ul>
                <li>✅ <a href="/booking/book?hotelId=3">/booking/book?hotelId=3</a></li>
                <li>✅ <a href="/booking/list">/booking/list</a></li>
                <li>✅ <a href="/booking/test">/booking/test</a></li>
                <li>🔗 Will redirect to: /payment?bookingId=XXX</li>
            </ul>
            """;
    }

    @GetMapping("/test-redirect")
    public String testRedirect() {
        System.out.println("🔗 TEST: Redirecting to /payment?bookingId=999");
        return "redirect:/payment?bookingId=999";
    }

    @GetMapping("/test-confirmation")
    @ResponseBody
    public String testConfirmation(@RequestParam Long bookingId) {
        try {
            Booking booking = bookingService.findById(bookingId);
            if (booking == null) {
                return "❌ Booking not found: " + bookingId;
            }
            
            return "✅ Confirmation test - Booking found:<br>" +
                   "ID: " + booking.getId() + "<br>" + 
                   "Status: " + booking.getStatus() + "<br>" +
                   "Hotel Name: " + booking.getHotelName() + "<br>" +
                   "<a href='/booking/confirmation?bookingId=" + bookingId + "'>View Confirmation</a>";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    @GetMapping("/test-all-endpoints")
@ResponseBody
public String testAllEndpoints() {
    return """
        <h1>Test All Endpoints</h1>
        <ul>
            <li><a href="/">Home Page</a></li>
            <li><a href="/booking/book?hotelId=1">Booking Page</a></li>
            <li><a href="/booking/create-test-booking">Create Test Booking</a></li>
            <li><a href="/booking/list">My Bookings</a></li>
            <li><a href="/booking/test">Basic Test</a></li>
        </ul>
        """;
}

@GetMapping("/health-check")
@ResponseBody
public String healthCheck() {
    return "✅ Application is running! BookingController is working.";
}

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }
}