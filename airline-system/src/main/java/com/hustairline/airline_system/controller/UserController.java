package com.hustairline.airline_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hustairline.airline_system.model.User;
import com.hustairline.airline_system.repository.UserRepository;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/manage")
    public String manageUsers(Model model) {
        List<User> users = userRepository.getAllUsers();
        model.addAttribute("users", users);
        return "users/manage-users";
    }

    @GetMapping("/add")
    public String showAddUser() {
        return "users/add-user";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String role,
                         RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Username is required");
                return "redirect:/users/add";
            }

            if (password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Password is required");
                return "redirect:/users/add";
            }

            if (!role.equals("ADMIN") && !role.equals("MANAGER")) {
                redirectAttributes.addFlashAttribute("error", "Invalid role selected");
                return "redirect:/users/add";
            }

            // Check if username already exists
            if (userRepository.usernameExists(username.trim())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/users/add";
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(password); // In production, this should be hashed
            newUser.setRole(role);
            newUser.setApproved(false); // Admin-created accounts need manager approval

            User createdUser = userRepository.createUser(newUser);

            if (createdUser != null) {
                redirectAttributes.addFlashAttribute("success", 
                    "User '" + username + "' created successfully with " + role + " role. Pending manager approval.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to create user");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
        }

        return "redirect:/users/manage";
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteUser(@RequestParam int userId) {
        try {
            boolean deleted = userRepository.deleteUser(userId);
            
            if (deleted) {
                return "{\"success\": true, \"message\": \"User deleted successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"User not found or cannot be deleted\"}";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Error deleting user: " + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/view")
    public String viewUsers(Model model) {
        List<User> users = userRepository.getAllUsers();
        model.addAttribute("users", users);
        return "users/view-users";
    }

    @GetMapping("/pending")
    public String pendingUsers(Model model) {
        List<User> pendingUsers = userRepository.getPendingUsers();
        model.addAttribute("pendingUsers", pendingUsers);
        return "users/pending-users";
    }

    @PostMapping("/approve")
    @ResponseBody
    public String approveUser(@RequestParam int userId) {
        try {
            boolean approved = userRepository.approveUser(userId);
            
            if (approved) {
                return "{\"success\": true, \"message\": \"User approved successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"User not found or cannot be approved\"}";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Error approving user: " + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/reject")
    @ResponseBody
    public String rejectUser(@RequestParam int userId) {
        try {
            boolean rejected = userRepository.rejectUser(userId);
            
            if (rejected) {
                return "{\"success\": true, \"message\": \"User rejected and removed successfully\"}";
            } else {
                return "{\"success\": false, \"message\": \"User not found or cannot be rejected\"}";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Error rejecting user: " + e.getMessage() + "\"}";
        }
    }
} 