package com.example.hotelbooking.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setHotelId("1");
        booking.setHotelName("Test Hotel");
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setGuests(2);
        booking.setTotalPrice(new BigDecimal("200.00"));
        booking.setUsername("testuser");
        booking.setStatus(BookingStatus.PENDING);

        // Use the actual service method name: save() instead of createBooking()
        when(bookingService.save(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.hotelName").value("Test Hotel"));
    }

    @Test
    void testGetBookingById() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setHotelName("Test Hotel");

        // Use the actual service method name: findById() instead of getBookingById()
        when(bookingService.findById(1L)).thenReturn(booking);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.hotelName").value("Test Hotel"));
    }

    @Test
    void testGetAllBookings() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setHotelName("Test Hotel 1");

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setHotelName("Test Hotel 2");

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        // Use the actual service method name: findAll() instead of getAllBookings()
        when(bookingService.findAll()).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].hotelName").value("Test Hotel 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].hotelName").value("Test Hotel 2"));
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setHotelName("Test Hotel");
        booking.setStatus(BookingStatus.CONFIRMED);

        // Use the actual service method: updateBookingStatus()
        when(bookingService.updateBookingStatus(eq(1L), any(BookingStatus.class))).thenReturn(booking);

        mockMvc.perform(put("/api/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CONFIRMED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testDeleteBooking() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetBookingsByUsername() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setHotelName("Test Hotel 1");
        booking1.setUsername("testuser");

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setHotelName("Test Hotel 2");
        booking2.setUsername("testuser");

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.findByUsername("testuser")).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/user/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("testuser"));
    }

    @Test
    void testCancelBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setHotelName("Test Hotel");
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUsername("testuser");

        when(bookingService.cancelBooking(1L, "testuser")).thenReturn(booking);

        mockMvc.perform(put("/api/bookings/1/cancel")
                .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}