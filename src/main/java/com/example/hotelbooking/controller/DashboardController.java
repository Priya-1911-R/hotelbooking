package com.example.hotelbooking.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        
        model.addAttribute("username", username);
        model.addAttribute("title", "My Dashboard");
        model.addAttribute("recommendedHotels", getFallbackRecommendations());
        model.addAttribute("profileCompletion", 75);
        model.addAttribute("specialOffers", getSpecialOffers());
        
        return "dashboard";
    }

    // REMOVE THIS METHOD - It's conflicting with AdminController
    // @GetMapping("/admin/dashboard")
    // public String adminDashboard(Authentication authentication, Model model) {
    //     String username = authentication.getName();
    //     model.addAttribute("username", username);
    //     model.addAttribute("title", "Admin Dashboard");
    //     return "admin-dashboard";
    // }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    
    private List<SpecialOffer> getSpecialOffers() {
        return Arrays.asList(
            new SpecialOffer("First-time booking", "15% off your first stay", "tag"),
            new SpecialOffer("Weekend getaway", "Free breakfast included", "coffee"),
            new SpecialOffer("Last minute", "30% off same-day bookings", "bolt")
        );
    }
    
    private List<Hotel> getFallbackRecommendations() {
        Hotel hotel1 = new Hotel();
        hotel1.setName("Mountain Retreat");
        hotel1.setLocation("Colorado");
        hotel1.setPricePerNight(new BigDecimal("179.00"));
        hotel1.setRating(4);
        hotel1.setDescription("Cozy mountain getaway with stunning views");
        hotel1.setAmenities("Hot tub, Fireplace, Hiking trails");
        
        Hotel hotel2 = new Hotel();
        hotel2.setName("Coastal Escape");
        hotel2.setLocation("San Diego");
        hotel2.setPricePerNight(new BigDecimal("219.00"));
        hotel2.setRating(5);
        hotel2.setDescription("Luxury beachfront resort");
        hotel2.setAmenities("Private beach, Sunset views, Spa");
        
        return Arrays.asList(hotel1, hotel2);
    }
    
    public static class Hotel {
        private String name;
        private String location;
        private BigDecimal pricePerNight;
        private int rating;
        private String description;
        private String amenities;
        
        public Hotel() {}
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public BigDecimal getPricePerNight() { return pricePerNight; }
        public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAmenities() { return amenities; }
        public void setAmenities(String amenities) { this.amenities = amenities; }
    }
    
    public static class SpecialOffer {
        private final String title;
        private final String description;
        private final String icon;
        
        public SpecialOffer(String title, String description, String icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
        }
        
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }
}