package com.example.hotelbooking.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("🔍 Searching for user: " + usernameOrEmail);
        
        // Use the custom query method
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: " + usernameOrEmail);
                    return new UsernameNotFoundException("User not found: " + usernameOrEmail);
                });
        
        System.out.println("✅ User found: " + user.getUsername() + " with role: " + user.getRole());
        return user;
    }
}