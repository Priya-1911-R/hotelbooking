package com.example.hotelbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hotelbooking.dto.RegisterRequest;
import com.example.hotelbooking.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String username,
                              @RequestParam String password,
                              RedirectAttributes redirectAttributes) {
        
        try {
            RegisterRequest request = new RegisterRequest(name, email, username, password, "USER");
            userService.registerUser(request);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }
}