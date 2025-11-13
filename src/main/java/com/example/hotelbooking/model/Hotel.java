package com.example.hotelbooking.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotels")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    private String image;
    
    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;
    
    private Integer rating = 3;
    
    private String amenities;
    
    private String description;
    
    @Column(name = "is_featured")
    private Boolean featured = false;
    
    // Constructors
    public Hotel() {}
    
    public Hotel(String name, String location, BigDecimal pricePerNight) {
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    
    // For compatibility with existing code that uses getPrice()
    public BigDecimal getPrice() { return pricePerNight; }
    public void setPrice(BigDecimal price) { this.pricePerNight = price; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
}