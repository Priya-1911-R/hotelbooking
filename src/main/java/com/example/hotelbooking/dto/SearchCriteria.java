package com.example.hotelbooking.dto;

public class SearchCriteria {
    private String query;
    private String location;
    private Double minPrice;
    private Double maxPrice;
    private String amenities;
    private Integer minRating;
    private String checkIn;
    private String checkOut;
    private Integer guests;

    // Constructors
    public SearchCriteria() {}

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public Integer getMinRating() { return minRating; }
    public void setMinRating(Integer minRating) { this.minRating = minRating; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public Integer getGuests() { return guests; }
    public void setGuests(Integer guests) { this.guests = guests; }
}