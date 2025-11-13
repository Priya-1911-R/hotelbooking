package com.example.hotelbooking.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "hotel_id")
    private String hotelId;
    
    // ADD THIS FIELD - matches the database column
    @Column(name = "hotel_name")
    private String hotelName;
    
    @Column(name = "check_in_date")
    private LocalDate checkInDate;
    
    @Column(name = "check_out_date")
    private LocalDate checkOutDate;
    
    private Integer guests;
    
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    
    private String username;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "booking_date")
    private LocalDate bookingDate;
    
    // Constructors
    public Booking() {}
    
    public Booking(String hotelId, String hotelName, LocalDate checkInDate, LocalDate checkOutDate, 
                  Integer guests, BigDecimal totalPrice, String username) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.username = username;
        this.status = BookingStatus.PENDING;
        this.bookingDate = LocalDate.now();
    }
    
    @PrePersist
    public void prePersist() {
        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }
        if (status == null) {
            status = BookingStatus.PENDING;
        }
        // Set default hotel name if not provided
        if (hotelName == null) {
            hotelName = "Hotel";
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public Integer getGuests() { return guests; }
    public void setGuests(Integer guests) { this.guests = guests; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    
    @Override
    public String toString() {
        return "Booking{id=" + id + ", hotelId='" + hotelId + "', hotelName='" + hotelName + 
               "', checkIn=" + checkInDate + ", checkOut=" + checkOutDate + ", guests=" + guests + 
               ", totalPrice=" + totalPrice + ", bookingDate=" + bookingDate + "}";
    }
}