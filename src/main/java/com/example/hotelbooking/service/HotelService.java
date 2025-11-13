package com.example.hotelbooking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.repository.HotelRepository;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    // Method that accepts String ID and converts to Long
    public Hotel getHotelById(String id) {
        try {
            // Convert String ID to Long
            Long hotelId = Long.valueOf(id);
            return hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid hotel ID format: " + id);
        }
    }

    // Method that returns Optional<Hotel> with Long ID
    public Optional<Hotel> getHotelByIdOptional(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return hotelRepository.findById(id);
    }

    // Method that directly accepts Long and returns Hotel
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
    }

    // DELETE method that accepts String
    public void deleteHotel(String id) {
        try {
            Long hotelId = Long.valueOf(id);
            hotelRepository.deleteById(hotelId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid hotel ID format: " + id);
        }
    }

    // DELETE method that accepts Long
    public void deleteHotel(Long id) {
        if (id != null) {
            hotelRepository.deleteById(id);
        }
    }

    // Basic CRUD operations
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    // Additional methods for the discovery page
    public List<Hotel> getFeaturedHotels() {
        List<Hotel> featuredHotels = hotelRepository.findByFeaturedTrue();
        // If no featured hotels, return some regular hotels as fallback
        if (featuredHotels.isEmpty()) {
            return hotelRepository.findAll().stream()
                    .limit(3)
                    .toList();
        }
        return featuredHotels;
    }
    
    public List<Hotel> searchHotels(String location, Integer guests) {
        if (location == null || location.trim().isEmpty()) {
            return getFeaturedHotels();
        }
        return hotelRepository.findByLocationContainingIgnoreCase(location);
    }
    
    // For popular destinations
    public List<String> getPopularDestinations() {
        List<String> destinations = hotelRepository.findDistinctLocations();
        // Fallback destinations if database is empty
        if (destinations.isEmpty()) {
            return List.of("New York", "Miami", "Las Vegas", "Los Angeles", "Chicago");
        }
        return destinations;
    }
    
    // Get hotels by location
    public List<Hotel> getHotelsByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return hotelRepository.findByLocationContainingIgnoreCase(location);
    }
}