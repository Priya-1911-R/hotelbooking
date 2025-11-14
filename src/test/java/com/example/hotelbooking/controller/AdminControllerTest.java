package com.example.hotelbooking.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.service.BookingService;
import com.example.hotelbooking.service.HotelService;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private BookingService bookingService;

    private Hotel testHotel;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");
        testHotel.setLocation("Test Location");
        testHotel.setPricePerNight(new BigDecimal("199.99"));
        testHotel.setRating(4);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setHotelId("1");
        testBooking.setHotelName("Test Hotel");
        testBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminDashboard() throws Exception {
        List<Hotel> hotels = Arrays.asList(testHotel);
        List<Booking> bookings = Arrays.asList(testBooking);
        
        when(hotelService.getAllHotels()).thenReturn(hotels);
        when(bookingService.findAll()).thenReturn(bookings);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("totalHotels"))
                .andExpect(model().attributeExists("totalBookings"))
                .andExpect(model().attributeExists("recentHotels"));

        verify(hotelService, times(1)).getAllHotels();
        verify(bookingService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManageUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManageHotels() throws Exception {
        List<Hotel> hotels = Arrays.asList(testHotel);
        when(hotelService.getAllHotels()).thenReturn(hotels);

        mockMvc.perform(get("/admin/hotels"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-hotels"))
                .andExpect(model().attributeExists("hotels"));

        verify(hotelService, times(1)).getAllHotels();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddHotel() throws Exception {
        when(hotelService.saveHotel(any(Hotel.class))).thenReturn(testHotel);

        mockMvc.perform(post("/admin/addHotel")
                .param("name", "New Hotel")
                .param("location", "New Location")
                .param("pricePerNight", "199.99")
                .param("rating", "4")
                .param("description", "Test Description")
                .param("amenities", "WiFi, Pool"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?success=Hotel added successfully"));

        verify(hotelService, times(1)).saveHotel(any(Hotel.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteHotel() throws Exception {
        mockMvc.perform(post("/admin/deleteHotel")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?success=Hotel deleted successfully"));

        verify(hotelService, times(1)).deleteHotel(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEditHotelForm() throws Exception {
        when(hotelService.getHotelById(1L)).thenReturn(testHotel);

        mockMvc.perform(get("/admin/editHotel")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-edit-hotel"))
                .andExpect(model().attributeExists("hotel"));

        verify(hotelService, times(1)).getHotelById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateHotel() throws Exception {
        when(hotelService.getHotelById(1L)).thenReturn(testHotel);
        when(hotelService.saveHotel(any(Hotel.class))).thenReturn(testHotel);

        mockMvc.perform(post("/admin/updateHotel")
                .param("id", "1")
                .param("name", "Updated Hotel")
                .param("location", "Updated Location")
                .param("pricePerNight", "299.99")
                .param("rating", "5")
                .param("description", "Updated Description")
                .param("amenities", "WiFi, Pool, Gym")
                .param("featured", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?success=Hotel updated successfully"));

        verify(hotelService, times(1)).getHotelById(1L);
        verify(hotelService, times(1)).saveHotel(any(Hotel.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManageBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.findAll()).thenReturn(bookings);
        when(bookingService.getBookingStats()).thenReturn(
            new BookingService.BookingStats(10L, 2L, 7L, 1L)
        );

        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-bookings"))
                .andExpect(model().attributeExists("bookings"))
                .andExpect(model().attributeExists("stats"));

        verify(bookingService, times(1)).findAll();
        verify(bookingService, times(1)).getBookingStats();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookingStatus() throws Exception {
        when(bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED))
            .thenReturn(testBooking);

        mockMvc.perform(post("/admin/updateBookingStatus")
                .param("bookingId", "1")
                .param("status", "CONFIRMED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bookings?success=Booking status updated successfully"));

        verify(bookingService, times(1)).updateBookingStatus(1L, BookingStatus.CONFIRMED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddTestData() throws Exception {
        when(hotelService.getAllHotels()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/admin/test-data"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?success=Test data added"));

        verify(hotelService, times(1)).getAllHotels();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testTestAuth() throws Exception {
        mockMvc.perform(get("/admin/test-auth"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Authentication Test")));
    }
}