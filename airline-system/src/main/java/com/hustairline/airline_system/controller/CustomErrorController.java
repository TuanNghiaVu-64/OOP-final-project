package com.hustairline.airline_system.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            switch (statusCode) {
                case 403:
                    model.addAttribute("error", "Access Denied");
                    model.addAttribute("message", "You don't have permission to access this resource. Please log in with appropriate credentials.");
                    break;
                case 404:
                    model.addAttribute("error", "Page Not Found");
                    model.addAttribute("message", "The page you are looking for does not exist.");
                    break;
                case 500:
                    model.addAttribute("error", "Internal Server Error");
                    model.addAttribute("message", "Something went wrong on our end. Please try again later.");
                    break;
                default:
                    model.addAttribute("error", "Error " + statusCode);
                    model.addAttribute("message", "An unexpected error occurred.");
                    break;
            }
            
            model.addAttribute("statusCode", statusCode);
        } else {
            model.addAttribute("error", "Unknown Error");
            model.addAttribute("message", "An unexpected error occurred.");
            model.addAttribute("statusCode", "Unknown");
        }
        
        return "error";
    }
} 