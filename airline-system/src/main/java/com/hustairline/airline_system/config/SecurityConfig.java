package com.hustairline.airline_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final CustomAuthenticationSuccessHandler successHandler;
    
    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/users/pending", "/users/approve", "/users/reject", "/users/view").hasRole("MANAGER")
                .requestMatchers("/users/add", "/users/delete", "/users/manage").hasRole("ADMIN")
                .requestMatchers("/admin-dashboard", "/seat-types/add", "/locations/**", "/flights/add", "/flights/list", "/flights/delete/**", "/flight-pricing/list-flights", "/flight-pricing/set-prices/**").hasRole("ADMIN")
                .requestMatchers("/manager-dashboard", "/seat-types/pending", "/seat-types/approve/**", "/seat-types/reject/**", "/flights/review", "/flights/manager-list", "/flights/approve/**", "/flights/reject/**", "/flight-pricing/review", "/flight-pricing/approved", "/flight-pricing/approve-flight/**", "/flight-pricing/reject-flight/**", "/flight-pricing/delete-flight/**", "/flight-pricing/approve/**", "/flight-pricing/reject/**").hasRole("MANAGER")
                .requestMatchers("/customer-dashboard", "/customer/**").hasRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
                .logoutSuccessUrl("/login?logout")
            );
            
        return http.build();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
