package com.example.hotelbooking.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.service.BookingService;

@Controller  // ADD THIS ANNOTATION
@RequestMapping("/payment")
public class PaymentController {

    private final BookingService bookingService;

    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String paymentPage(@RequestParam("bookingId") @NonNull Long bookingId, Model model) {
        System.out.println("🎯 PAYMENT PAGE CALLED - Booking ID: " + bookingId);
        
        try {
            Booking booking = bookingService.findById(bookingId);
            System.out.println("📊 Booking found: " + (booking != null ? "YES" : "NO"));
            
            if (booking != null) {
                System.out.println("💰 Booking details:");
                System.out.println("   - ID: " + booking.getId());
                System.out.println("   - Hotel ID: " + booking.getHotelId());
                System.out.println("   - Total Price: " + booking.getTotalPrice());
                System.out.println("   - Check-in: " + booking.getCheckInDate());
                System.out.println("   - Check-out: " + booking.getCheckOutDate());
                System.out.println("   - Guests: " + booking.getGuests());
                System.out.println("   - Status: " + booking.getStatus());
                
                // Add null checks
                if (booking.getTotalPrice() == null) {
                    System.out.println("⚠️ WARNING: Total price is null!");
                    booking.setTotalPrice(BigDecimal.ZERO);
                }
                
                model.addAttribute("amount", booking.getTotalPrice());
                model.addAttribute("bookingId", bookingId);
                model.addAttribute("hotelId", booking.getHotelId());
                model.addAttribute("checkInDate", booking.getCheckInDate());
                model.addAttribute("checkOutDate", booking.getCheckOutDate());
                model.addAttribute("guests", booking.getGuests());
                
                System.out.println("✅ Model attributes set successfully");
            } else {
                System.out.println("❌ Booking not found for ID: " + bookingId);
                model.addAttribute("error", "Booking not found.");
                return "redirect:/booking/list";
            }
            
            System.out.println("🔄 Returning payment template");
            return "payment";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in paymentPage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/booking/list";
        }
    }

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam Map<String, String> paymentDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💳 PAYMENT CONFIRMATION CALLED");
            
            Long bookingId = Long.valueOf(paymentDetails.get("bookingId"));
            String paymentMethod = paymentDetails.get("paymentMethod");
            
            System.out.println("🔍 Processing payment for booking: " + bookingId);
            
            // Verify booking exists before payment
            Booking booking = bookingService.findById(bookingId);
            if (booking == null) {
                System.out.println("❌ Booking not found: " + bookingId);
                redirectAttributes.addFlashAttribute("error", "Booking not found.");
                return "redirect:/payment?bookingId=" + bookingId;
            }
            
            System.out.println("📊 Booking before payment - Status: " + booking.getStatus());
            
            boolean paymentSuccess = processPayment(paymentDetails);
            System.out.println("💳 Payment success: " + paymentSuccess);
            
            if (paymentSuccess) {
                // Update booking with payment info
                System.out.println("🔄 Calling bookingService.confirmPayment...");
                Booking confirmedBooking = bookingService.confirmPayment(bookingId, paymentMethod);
                
                System.out.println("📊 Booking after payment - " + (confirmedBooking != null ? "NOT NULL" : "NULL"));
                
                if (confirmedBooking != null) {
                    System.out.println("✅ Payment successful for booking: " + bookingId);
                    System.out.println("📊 Confirmed booking status: " + confirmedBooking.getStatus());
                    System.out.println("📊 Confirmed booking hotel: " + confirmedBooking.getHotelName());
                    
                    // Add ALL necessary attributes for confirmation page
                    redirectAttributes.addFlashAttribute("success", true);
                    redirectAttributes.addFlashAttribute("booking", confirmedBooking);
                    redirectAttributes.addFlashAttribute("message", "Payment successful! Your booking has been confirmed.");
                    
                    System.out.println("🔀 Redirecting to: /booking/confirmation?bookingId=" + bookingId);
            
                    return "redirect:/booking/confirmation?bookingId=" + bookingId;
                } else {
                    System.out.println("❌ Booking confirmation failed - confirmedBooking is null");
                    redirectAttributes.addFlashAttribute("error", "Booking confirmation failed after payment.");
                    return "redirect:/payment?bookingId=" + bookingId;
                }
            }
            
            // Payment failed
            redirectAttributes.addFlashAttribute("error", "Payment failed. Please check your card details and try again.");
            return "redirect:/payment?bookingId=" + bookingId;
            
        } catch (Exception e) {
            System.out.println("❌ Payment processing error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Payment error: " + e.getMessage());
            return "redirect:/booking/list";
        }
    }

    private boolean processPayment(Map<String, String> paymentDetails) {
        try {
            Thread.sleep(1000);
            String cardNumber = paymentDetails.get("cardNumber");
            String expiryDate = paymentDetails.get("expiryDate");
            String cvv = paymentDetails.get("cvv");
            String cardHolder = paymentDetails.get("cardHolder");
            
            return cardNumber != null && cardNumber.replace(" ", "").length() == 16 &&
                   expiryDate != null && expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}") &&
                   cvv != null && cvv.length() == 3 &&
                   cardHolder != null && !cardHolder.trim().isEmpty();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @GetMapping("/debug")
    @ResponseBody
    public String debugInfo() {
        return """
            <h1>PaymentController Debug</h1>
            <ul>
                <li>✅ <a href="/payment?bookingId=1">/payment?bookingId=1</a></li>
                <li>✅ <a href="/payment/test">/payment/test</a></li>
                <li>✅ <a href="/payment/debug">/payment/debug</a></li>
                <li>✅ <a href="/payment/test-booking?bookingId=1">/payment/test-booking?bookingId=1</a></li>
            </ul>
            """;
    }

    @GetMapping("/test")
    @ResponseBody
    public String testEndpoint() {
        return "✅ PaymentController is working!";
    }
    
    @GetMapping("/test-booking")
    @ResponseBody
    public String testBooking(@RequestParam Long bookingId) {
        try {
            System.out.println("🔍 TESTING BOOKING LOOKUP - ID: " + bookingId);
            
            Booking booking = bookingService.findById(bookingId);
            
            if (booking != null) {
                return "✅ Booking FOUND: " + 
                    "ID: " + booking.getId() + ", " +
                    "Hotel: " + booking.getHotelId() + ", " +
                    "Total: $" + booking.getTotalPrice() + ", " +
                    "Status: " + booking.getStatus();
            } else {
                return "❌ Booking NOT FOUND with ID: " + bookingId;
            }
            
        } catch (Exception e) {
            return "❌ ERROR: " + e.getMessage() + " - " + e.getClass().getSimpleName();
        }
    }
    
    @GetMapping("/test-flow")
    @ResponseBody
    public String testPaymentFlow(@RequestParam Long bookingId) {
        try {
            Booking booking = bookingService.findById(bookingId);
            if (booking == null) {
                return "❌ Booking not found with ID: " + bookingId;
            }
            
            return "✅ Booking ready for payment:<br>" +
                   "ID: " + booking.getId() + "<br>" +
                   "Hotel: " + booking.getHotelName() + "<br>" +
                   "Total: $" + booking.getTotalPrice() + "<br>" +
                   "Status: " + booking.getStatus() + "<br>" +
                   "<a href='/payment?bookingId=" + bookingId + "'>Go to Payment</a>";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}