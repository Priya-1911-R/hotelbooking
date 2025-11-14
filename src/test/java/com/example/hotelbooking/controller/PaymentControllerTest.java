package com.example.hotelbooking.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.BookingStatus;
import com.example.hotelbooking.service.BookingService;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setHotelId("1");
        testBooking.setHotelName("Test Hotel");
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setGuests(2);
        testBooking.setTotalPrice(new BigDecimal("199.99"));
        testBooking.setUsername("testuser");
        testBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    @WithMockUser
    void testPaymentPage() throws Exception {
        when(bookingService.findById(1L)).thenReturn(testBooking);

        mockMvc.perform(get("/payment")
                .param("bookingId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attributeExists("amount"))
                .andExpect(model().attributeExists("bookingId"))
                .andExpect(model().attributeExists("hotelId"))
                .andExpect(model().attributeExists("checkInDate"))
                .andExpect(model().attributeExists("checkOutDate"))
                .andExpect(model().attributeExists("guests"));

        verify(bookingService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void testPaymentPageBookingNotFound() throws Exception {
        when(bookingService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/payment")
                .param("bookingId", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking/list"));

        verify(bookingService, times(1)).findById(999L);
    }

    @Test
    @WithMockUser
    void testConfirmPaymentSuccess() throws Exception {
        when(bookingService.findById(1L)).thenReturn(testBooking);
        when(bookingService.confirmPayment(1L, "CREDIT_CARD")).thenReturn(testBooking);

        Map<String, String> paymentDetails = new HashMap<>();
        paymentDetails.put("bookingId", "1");
        paymentDetails.put("paymentMethod", "CREDIT_CARD");
        paymentDetails.put("cardNumber", "4111111111111111");
        paymentDetails.put("expiryDate", "12/25");
        paymentDetails.put("cvv", "123");
        paymentDetails.put("cardHolder", "John Doe");

        mockMvc.perform(post("/payment/confirm")
                .param("bookingId", "1")
                .param("paymentMethod", "CREDIT_CARD")
                .param("cardNumber", "4111111111111111")
                .param("expiryDate", "12/25")
                .param("cvv", "123")
                .param("cardHolder", "John Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking/confirmation?bookingId=1"));

        verify(bookingService, times(1)).findById(1L);
        verify(bookingService, times(1)).confirmPayment(1L, "CREDIT_CARD");
    }

    @Test
    @WithMockUser
    void testConfirmPaymentBookingNotFound() throws Exception {
        when(bookingService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/payment/confirm")
                .param("bookingId", "999")
                .param("paymentMethod", "CREDIT_CARD")
                .param("cardNumber", "4111111111111111")
                .param("expiryDate", "12/25")
                .param("cvv", "123")
                .param("cardHolder", "John Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?bookingId=999"))
                .andExpect(flash().attributeExists("error"));

        verify(bookingService, times(1)).findById(999L);
        verify(bookingService, never()).confirmPayment(anyLong(), anyString());
    }

    @Test
    @WithMockUser
    void testTestEndpoint() throws Exception {
        mockMvc.perform(get("/payment/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ PaymentController is working!"));
    }

    @Test
    @WithMockUser
    void testDebugInfo() throws Exception {
        mockMvc.perform(get("/payment/debug"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PaymentController Debug")));
    }

    @Test
    @WithMockUser
    void testTestBooking() throws Exception {
        when(bookingService.findById(1L)).thenReturn(testBooking);

        mockMvc.perform(get("/payment/test-booking")
                .param("bookingId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Booking FOUND")));

        verify(bookingService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void testTestPaymentFlow() throws Exception {
        when(bookingService.findById(1L)).thenReturn(testBooking);

        mockMvc.perform(get("/payment/test-flow")
                .param("bookingId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Booking ready for payment")));

        verify(bookingService, times(1)).findById(1L);
    }
}