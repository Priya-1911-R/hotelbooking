package com.example.hotelbooking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.hotelbooking.dto.RegisterRequest;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
            "John Doe", 
            "john@example.com", 
            "johndoe", 
            "password123", 
            "USER"
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setUsername("johndoe");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");
        testUser.setEnabled(true);
    }

    @Test
    void testRegisterUserSuccess() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User registeredUser = userService.registerUser(registerRequest);

        assertNotNull(registeredUser);
        assertEquals("johndoe", registeredUser.getUsername());
        assertEquals("john@example.com", registeredUser.getEmail());
        assertTrue(registeredUser.isEnabled());
        
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserUsernameExists() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registerRequest);
        });

        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        User foundUser = userService.findByUsername("johndoe");

        assertNotNull(foundUser);
        assertEquals("johndoe", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.findByUsername("nonexistent");
        });

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }
}