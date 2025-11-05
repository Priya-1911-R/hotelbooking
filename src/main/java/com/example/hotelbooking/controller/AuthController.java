package com.example.hotelbooking.controller;

import com.example.hotelbooking.dto.*;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.security.JwtUtil;
import com.example.hotelbooking.service.UserService;
import com.example.hotelbooking.security.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService uds;

    public AuthController(AuthenticationManager am, UserService us, JwtUtil ju, CustomUserDetailsService uds) {
        this.authenticationManager = am;
        this.userService = us;
        this.jwtUtil = ju;
        this.uds = uds;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User u = userService.register(req);
        return ResponseEntity.ok(u);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        UserDetails ud = uds.loadUserByUsername(req.getUsername());
        // retrieve role from user service (or user repo)
        var userOpt = userService.findByUsername(req.getUsername());
        String role = userOpt.map(u -> u.getRole().name()).orElse("USER");
        String token = jwtUtil.generateToken(req.getUsername(), role);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
