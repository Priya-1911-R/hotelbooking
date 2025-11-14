package com.example.hotelbooking.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.hotelbooking.model.Hotel;

@DataJpaTest
class HotelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel1;
    private Hotel hotel2;
    private Hotel hotel3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        // Create test hotels
        hotel1 = new Hotel();
        hotel1.setName("Grand Plaza Hotel");
        hotel1.setLocation("New York");
        hotel1.setPricePerNight(new BigDecimal("199.99"));
        hotel1.setRating(5);
        hotel1.setDescription("Luxury hotel in downtown Manhattan");
        hotel1.setAmenities("Pool, Spa, Gym, Restaurant");
        hotel1.setFeatured(true);
        hotel1.setImage("hotel1.jpg");

        hotel2 = new Hotel();
        hotel2.setName("Beach Resort");
        hotel2.setLocation("Miami");
        hotel2.setPricePerNight(new BigDecimal("149.99"));
        hotel2.setRating(4);
        hotel2.setDescription("Beautiful beachfront resort");
        hotel2.setAmenities("Beach Access, Pool, Bar");
        hotel2.setFeatured(true);
        hotel2.setImage("hotel2.jpg");

        hotel3 = new Hotel();
        hotel3.setName("City Inn");
        hotel3.setLocation("Chicago");
        hotel3.setPricePerNight(new BigDecimal("99.99"));
        hotel3.setRating(3);
        hotel3.setDescription("Comfortable city hotel");
        hotel3.setAmenities("Free WiFi, Breakfast");
        hotel3.setFeatured(false);
        hotel3.setImage("hotel3.jpg");

        // Persist hotels
        entityManager.persist(hotel1);
        entityManager.persist(hotel2);
        entityManager.persist(hotel3);
        entityManager.flush();
    }

    @Test
    void testFindById() {
        // When
        Optional<Hotel> foundHotel = hotelRepository.findById(hotel1.getId());

        // Then
        assertTrue(foundHotel.isPresent());
        assertEquals("Grand Plaza Hotel", foundHotel.get().getName());
        assertEquals("New York", foundHotel.get().getLocation());
        assertEquals(new BigDecimal("199.99"), foundHotel.get().getPricePerNight());
        assertEquals(5, foundHotel.get().getRating());
        assertTrue(foundHotel.get().getFeatured());
    }

    @Test
    void testFindById_NotFound() {
        // When
        Optional<Hotel> foundHotel = hotelRepository.findById(999L);

        // Then
        assertFalse(foundHotel.isPresent());
    }

    @Test
    void testFindByFeaturedTrue() {
        // When
        List<Hotel> featuredHotels = hotelRepository.findByFeaturedTrue();

        // Then
        assertEquals(2, featuredHotels.size());
        assertTrue(featuredHotels.stream().allMatch(h -> h.getFeatured()));
        assertTrue(featuredHotels.stream().anyMatch(h -> h.getName().equals("Grand Plaza Hotel")));
        assertTrue(featuredHotels.stream().anyMatch(h -> h.getName().equals("Beach Resort")));
    }

    @Test
    void testFindByLocationContainingIgnoreCase() {
        // When
        List<Hotel> newYorkHotels = hotelRepository.findByLocationContainingIgnoreCase("new york");
        List<Hotel> miHotels = hotelRepository.findByLocationContainingIgnoreCase("mi");

        // Then
        assertEquals(1, newYorkHotels.size());
        assertEquals("Grand Plaza Hotel", newYorkHotels.get(0).getName());
        
        assertEquals(1, miHotels.size());
        assertEquals("Beach Resort", miHotels.get(0).getName());
    }

    @Test
    void testFindDistinctLocations() {
        // When
        List<String> locations = hotelRepository.findDistinctLocations();

        // Then
        assertEquals(3, locations.size());
        assertTrue(locations.contains("New York"));
        assertTrue(locations.contains("Miami"));
        assertTrue(locations.contains("Chicago"));
    }

    @Test
    void testFindByRatingGreaterThanEqual() {
        // When
        List<Hotel> highRatedHotels = hotelRepository.findByRatingGreaterThanEqual(4);

        // Then
        assertEquals(2, highRatedHotels.size());
        assertTrue(highRatedHotels.stream().allMatch(h -> h.getRating() >= 4));
    }

    @Test
    void testFindByIdString() {
        // When
        Optional<Hotel> foundHotel = hotelRepository.findByIdString(hotel1.getId().toString());

        // Then
        assertTrue(foundHotel.isPresent());
        assertEquals("Grand Plaza Hotel", foundHotel.get().getName());
    }

    @Test
    void testSaveHotel() {
        // Given
        Hotel newHotel = new Hotel();
        newHotel.setName("Mountain View Hotel");
        newHotel.setLocation("Denver");
        newHotel.setPricePerNight(new BigDecimal("129.99"));
        newHotel.setRating(4);
        newHotel.setDescription("Hotel with mountain views");
        newHotel.setAmenities("Mountain View, Hiking");
        newHotel.setFeatured(true);
        newHotel.setImage("mountain.jpg");

        // When
        Hotel savedHotel = hotelRepository.save(newHotel);

        // Then
        assertNotNull(savedHotel.getId());
        assertEquals("Mountain View Hotel", savedHotel.getName());
    }
}