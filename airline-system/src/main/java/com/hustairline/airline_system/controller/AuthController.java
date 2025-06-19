package com.hustairline.airline_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hustairline.airline_system.model.User;
import com.hustairline.airline_system.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Username is required");
                return "redirect:/register";
            }

            if (password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Password is required");
                return "redirect:/register";
            }

            if (confirmPassword == null || !password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long");
                return "redirect:/register";
            }

            if (username.length() < 3) {
                redirectAttributes.addFlashAttribute("error", "Username must be at least 3 characters long");
                return "redirect:/register";
            }

            // Check if username already exists
            if (userRepository.usernameExists(username.trim())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists. Please choose a different username.");
                return "redirect:/register";
            }

            // Create new customer user
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(password); // In production, this should be hashed
            newUser.setRole("CUSTOMER");
            newUser.setApproved(true); // Customer accounts are automatically approved

            User createdUser = userRepository.createUser(newUser);

            if (createdUser != null) {
                redirectAttributes.addFlashAttribute("success", 
                    "Account created successfully! You can now log in with your credentials.");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to create account. Please try again.");
                return "redirect:/register";
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating account: " + e.getMessage());
            return "redirect:/register";
        }
    }
} 