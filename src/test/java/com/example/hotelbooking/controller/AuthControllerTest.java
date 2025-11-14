package com.example.hotelbooking.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hotelbooking.dto.RegisterRequest;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.service.UserService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testRegisterUserSuccess() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/register")
                .param("name", "John Doe")
                .param("email", "john@example.com")
                .param("username", "johndoe")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    void testRegisterUserFailure() throws Exception {
        when(userService.registerUser(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/register")
                .param("name", "John Doe")
                .param("email", "john@example.com")
                .param("username", "johndoe")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attributeExists("name"))
                .andExpect(flash().attributeExists("email"))
                .andExpect(flash().attributeExists("username"));

        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testLoginPageWithError() throws Exception {
        mockMvc.perform(get("/login")
                .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testLoginPageWithLogout() throws Exception {
        mockMvc.perform(get("/login")
                .param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}