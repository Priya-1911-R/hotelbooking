package com.example.hotelbooking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && 
                                authentication.isAuthenticated() && 
                                !authentication.getName().equals("anonymousUser");
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        if (isAuthenticated) {
            model.addAttribute("username", authentication.getName());
        }
        
        model.addAttribute("title", "Welcome to Hotel Booking System");
        return "index";
    }
    
    @GetMapping("/home")
    public String homePage(Model model) {
        return home(model); // Reuse the same logic
    }
}