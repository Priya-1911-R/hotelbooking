package com.example.hotelbooking.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.service.HotelService;
import com.example.hotelbooking.service.UserService;

@Controller
public class HotelController {

    @Autowired
    private HotelService hotelService;
    
    @Autowired
    private UserService userService;

    // FIXED: Use the correct method signature
    @GetMapping("/book")
    public String showBookingPage(@RequestParam(required = false) Long hotelId, Model model, Principal principal) {
        System.out.println("🎯 /book endpoint called with hotelId: " + hotelId);
        
        try {
            if (hotelId != null) {
                // FIX: Use the method that returns Optional<Hotel>
                Optional<Hotel> hotelOptional = hotelService.getHotelByIdOptional(hotelId);
                
                if (hotelOptional.isPresent()) {
                    Hotel hotel = hotelOptional.get();
                    model.addAttribute("hotel", hotel);
                    System.out.println("✅ Hotel found: " + hotel.getName());
                } else {
                    // Fallback hotel data - use setters instead of constructor
                    Hotel fallbackHotel = createHotel("Grand Plaza Hotel", "New York, NY", 249.0);
                    fallbackHotel.setId(1L);
                    fallbackHotel.setRating(4);
                    model.addAttribute("hotel", fallbackHotel);
                    System.out.println("⚠️ Using fallback hotel data");
                }
            } else {
                // If no hotelId provided, use a default hotel
                Hotel defaultHotel = createHotel("Select a Hotel", "Choose from our collection", 0.0);
                model.addAttribute("hotel", defaultHotel);
            }
            
            // Add user info if logged in
            if (principal != null) {
                model.addAttribute("user", userService.findByUsername(principal.getName()));
            }
            
            model.addAttribute("title", "Book Your Stay");
            
        } catch (Exception e) {
            System.out.println("❌ Error in showBookingPage: " + e.getMessage());
            // Fallback data
            Hotel fallbackHotel = createHotel("Grand Plaza Hotel", "New York, NY", 249.0);
            fallbackHotel.setId(1L);
            model.addAttribute("hotel", fallbackHotel);
        }
        
        return "booking";
    }

    @GetMapping("/book/{hotelId}")
    public String showBookingPageWithPath(@PathVariable Long hotelId, Model model, Principal principal) {
        System.out.println("🎯 /book/" + hotelId + " endpoint called!");
        return showBookingPage(hotelId, model, principal);
    }

    // BROWSE ALL HOTELS
    @GetMapping("/hotels")
    public String browseHotels(Model model, Principal principal) {
        System.out.println("🎯 /hotels endpoint called!");
        
        try {
            List<Hotel> allHotels = hotelService.getAllHotels();
            List<String> destinations = hotelService.getPopularDestinations();
            
            System.out.println("🏨 Found " + allHotels.size() + " hotels");
            
            model.addAttribute("title", "Browse All Hotels");
            model.addAttribute("hotels", allHotels);
            model.addAttribute("destinations", destinations);
            
            // Add user info if logged in
            if (principal != null) {
                model.addAttribute("user", userService.findByUsername(principal.getName()));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error in browseHotels: " + e.getMessage());
            // Fallback data
            model.addAttribute("hotels", getFallbackHotels());
            model.addAttribute("destinations", Arrays.asList("New York", "Miami", "Chicago"));
            model.addAttribute("error", "Unable to load hotels at this time");
        }
        
        return "hotels";
    }
    

    @GetMapping("/discover")
    public String getHotelDiscovery(Model model, Principal principal) {
        try {
            // Get featured hotels
            List<Hotel> featuredHotels = hotelService.getFeaturedHotels();
            
            // Get popular destinations
            List<String> popularDestinations = hotelService.getPopularDestinations();
            
            // Add attributes to model
            model.addAttribute("title", "Discover Amazing Hotels");
            model.addAttribute("featuredHotels", featuredHotels);
            model.addAttribute("popularDestinations", popularDestinations);
            
            // Add user info if logged in
            if (principal != null) {
                model.addAttribute("user", userService.findByUsername(principal.getName()));
            }
            
        } catch (Exception e) {
            // Fallback data in case of error
            model.addAttribute("featuredHotels", getFallbackHotels());
            model.addAttribute("popularDestinations", Arrays.asList("New York", "Miami", "Las Vegas", "Los Angeles", "Chicago"));
        }
        
        return "hotel-discovery";
    }

    @GetMapping("/search")
    public String searchHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String checkin,
            @RequestParam(required = false) String checkout,
            @RequestParam(required = false, defaultValue = "2") Integer guests,
            Model model,
            Principal principal) {
        
        try {
            List<Hotel> searchResults;
            
            if (location != null && !location.trim().isEmpty()) {
                searchResults = hotelService.searchHotels(location, guests);
            } else {
                searchResults = hotelService.getFeaturedHotels();
            }
            
            model.addAttribute("title", "Hotels in " + (location != null ? location : "All Locations"));
            model.addAttribute("featuredHotels", searchResults);
            model.addAttribute("popularDestinations", hotelService.getPopularDestinations());
            model.addAttribute("searchQuery", new SearchQuery(location, checkin, checkout, guests));
            
            if (principal != null) {
                model.addAttribute("user", userService.findByUsername(principal.getName()));
            }
            
        } catch (Exception e) {
            model.addAttribute("featuredHotels", getFallbackHotels());
            model.addAttribute("popularDestinations", Arrays.asList("New York", "Miami", "Las Vegas", "Los Angeles", "Chicago"));
        }
        
        return "hotel-discovery";
    }
    
    // Helper method to create hotels with double price
    private Hotel createHotel(String name, String location, double price) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setPricePerNight(BigDecimal.valueOf(price));
        return hotel;
    }
    
    private List<Hotel> getFallbackHotels() {
        // Create fallback hotels using the helper method
        Hotel hotel1 = createHotel("Grand Plaza Hotel", "New York, NY", 249.0);
        hotel1.setId(1L);
        hotel1.setRating(4);
        hotel1.setDescription("Luxury hotel in downtown Manhattan");
        hotel1.setAmenities("Free WiFi, Pool, Spa");
        hotel1.setFeatured(true);
        
        Hotel hotel2 = createHotel("Luxury Suites Central", "New York, NY", 349.0);
        hotel2.setId(2L);
        hotel2.setRating(5);
        hotel2.setDescription("5-star luxury suites with city views");
        hotel2.setAmenities("Breakfast Included, Gym, Bar");
        hotel2.setFeatured(true);
        
        Hotel hotel3 = createHotel("Riverside Inn", "New York, NY", 199.0);
        hotel3.setId(3L);
        hotel3.setRating(4);
        hotel3.setDescription("Comfortable stay by the river");
        hotel3.setAmenities("Free Parking, Restaurant, Pet Friendly");
        hotel3.setFeatured(true);

        Hotel hotel4 = createHotel("Metropolitan Tower", "New York, NY", 299.0);
        hotel4.setId(4L);
        hotel4.setRating(5);
        hotel4.setDescription("Modern hotel with stunning city views");
        hotel4.setAmenities("City View, Concierge, Business Center");
        hotel4.setFeatured(true);
        
        return Arrays.asList(hotel1, hotel2, hotel3, hotel4);
    }
    
    // DTO for search query
    public static class SearchQuery {
        private String location;
        private String checkin;
        private String checkout;
        private Integer guests;
        
        public SearchQuery(String location, String checkin, String checkout, Integer guests) {
            this.location = location;
            this.checkin = checkin;
            this.checkout = checkout;
            this.guests = guests;
        }
        
        // Getters and setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getCheckin() { return checkin; }
        public void setCheckin(String checkin) { this.checkin = checkin; }
        public String getCheckout() { return checkout; }
        public void setCheckout(String checkout) { this.checkout = checkout; }
        public Integer getGuests() { return guests; }
        public void setGuests(Integer guests) { this.guests = guests; }
    }
}