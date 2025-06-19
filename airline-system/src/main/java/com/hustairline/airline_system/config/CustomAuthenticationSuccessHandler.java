package com.hustairline.airline_system.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        System.out.println("Authentication success for user: " + authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println("User authorities: " + authorities);
        
        String role = authorities.iterator().next().getAuthority(); // This will be "ROLE_ADMIN"
        System.out.println("Authority from Spring Security: " + role);
        
        // Extract the role name without the "ROLE_" prefix for switch statement
        String targetUrl = switch (role) {
            case "ROLE_ADMIN" -> "/admin-dashboard";
            case "ROLE_MANAGER" -> "/manager-dashboard";
            case "ROLE_CUSTOMER" -> "/customer-dashboard";
            default -> {
                System.out.println("Unknown role: " + role);
                yield "/dashboard";
            }
        };
        
        System.out.println("Redirecting to: " + targetUrl);
        response.sendRedirect(targetUrl);
    }
} 