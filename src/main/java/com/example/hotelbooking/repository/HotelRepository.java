package com.example.hotelbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    // CORRECT: Use Long for ID, not String
    Optional<Hotel> findById(Long id);
    
    // Find featured hotels
    List<Hotel> findByFeaturedTrue();
    
    // Search hotels by location
    List<Hotel> findByLocationContainingIgnoreCase(String location);
    
    // Find distinct locations
    @Query("SELECT DISTINCT h.location FROM Hotel h")
    List<String> findDistinctLocations();
    
    // Find hotels by rating greater than or equal to
    List<Hotel> findByRatingGreaterThanEqual(Integer rating);
    
    // If you need to find by String ID, use a custom query
    @Query("SELECT h FROM Hotel h WHERE CAST(h.id AS string) = :id")
    Optional<Hotel> findByIdString(String id);
}