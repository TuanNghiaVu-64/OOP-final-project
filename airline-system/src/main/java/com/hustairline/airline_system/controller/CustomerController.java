package com.hustairline.airline_system.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hustairline.airline_system.model.Flight;
import com.hustairline.airline_system.model.FlightSeatAssignment;
import com.hustairline.airline_system.model.FlightSeatType;
import com.hustairline.airline_system.model.Booking;
import com.hustairline.airline_system.model.User;
import com.hustairline.airline_system.repository.FlightRepository;
import com.hustairline.airline_system.repository.FlightSeatAssignmentRepository;
import com.hustairline.airline_system.repository.FlightSeatTypeRepository;
import com.hustairline.airline_system.repository.BookingRepository;
import com.hustairline.airline_system.repository.UserRepository;
import com.hustairline.airline_system.repository.LocationRepository;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    
    private final FlightRepository flightRepository;
    private final FlightSeatAssignmentRepository flightSeatAssignmentRepository;
    private final FlightSeatTypeRepository flightSeatTypeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    
    public CustomerController(FlightRepository flightRepository,
                             FlightSeatAssignmentRepository flightSeatAssignmentRepository,
                             FlightSeatTypeRepository flightSeatTypeRepository,
                             BookingRepository bookingRepository,
                             UserRepository userRepository,
                             LocationRepository locationRepository) {
        this.flightRepository = flightRepository;
        this.flightSeatAssignmentRepository = flightSeatAssignmentRepository;
        this.flightSeatTypeRepository = flightSeatTypeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }
    
    @GetMapping("/search-flights")
    public String showSearchFlights(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        return "customer/search-flights";
    }
    
    @PostMapping("/search-flights")
    public String searchFlights(@RequestParam(required = false) Integer originId,
                               @RequestParam(required = false) Integer destinationId,
                               @RequestParam(required = false) String departureDate,
                               Model model) {
        List<Flight> flights = flightRepository.searchFlights(originId, destinationId, departureDate);
        
        // Filter only approved flights with approved pricing
        flights = flights.stream()
            .filter(flight -> flight.isApproved() && hasApprovedPricing(flight.getId()))
            .toList();
        
        model.addAttribute("flights", flights);
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("selectedOriginId", originId);
        model.addAttribute("selectedDestinationId", destinationId);
        model.addAttribute("selectedDepartureDate", departureDate);
        
        return "customer/search-flights";
    }
    
    private boolean hasApprovedPricing(int flightId) {
        List<FlightSeatType> seatTypes = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
        return !seatTypes.isEmpty() && seatTypes.stream().allMatch(FlightSeatType::isApproved);
    }
    
    @GetMapping("/book-flight/{flightId}")
    public String showBookFlight(@PathVariable int flightId, Model model, RedirectAttributes redirectAttributes) {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null || !flight.isApproved()) {
            redirectAttributes.addFlashAttribute("error", "Flight not found or not available for booking");
            return "redirect:/customer/search-flights";
        }
        
        if (!hasApprovedPricing(flightId)) {
            redirectAttributes.addFlashAttribute("error", "This flight is not available for booking");
            return "redirect:/customer/search-flights";
        }
        
        // Create flight seat assignments if they don't exist
        if (!flightSeatAssignmentRepository.flightSeatAssignmentsExist(flightId)) {
            flightSeatAssignmentRepository.createFlightSeatAssignments(flightId);
        }
        
        List<FlightSeatAssignment> seatAssignments = flightSeatAssignmentRepository.getFlightSeatAssignments(flightId);
        List<FlightSeatType> seatTypes = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
        
        // Create a map for easier price lookup
        Map<Integer, BigDecimal> seatTypePrices = new HashMap<>();
        for (FlightSeatType seatType : seatTypes) {
            seatTypePrices.put(seatType.getId(), seatType.getPrice());
        }
        
        model.addAttribute("flight", flight);
        model.addAttribute("seatAssignments", seatAssignments);
        model.addAttribute("seatTypes", seatTypes);
        model.addAttribute("seatTypePrices", seatTypePrices);
        
        return "customer/book-flight";
    }
    
    @PostMapping("/confirm-booking")
    @ResponseBody
    public String confirmBooking(@RequestParam List<Integer> flightSeatAssignmentIds) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName());
            
            if (user == null) {
                return "{\"success\": false, \"message\": \"User not found\"}";
            }
            
            if (flightSeatAssignmentIds == null || flightSeatAssignmentIds.isEmpty()) {
                return "{\"success\": false, \"message\": \"No seats selected\"}";
            }
            
            // Validate all seats are available and from the same flight
            List<FlightSeatAssignment> targetAssignments = new ArrayList<>();
            Integer flightId = null;
            BigDecimal totalPrice = BigDecimal.ZERO;
            
            for (Integer assignmentId : flightSeatAssignmentIds) {
                FlightSeatAssignment assignment = getFlightSeatAssignmentById(assignmentId);
            
                if (assignment == null || !assignment.isAvailable()) {
                    return "{\"success\": false, \"message\": \"One or more seats are no longer available\"}";
            }
            
            // Check if user already has a booking for this seat
                if (bookingRepository.hasBookingForSeat(assignmentId)) {
                    return "{\"success\": false, \"message\": \"One or more seats are already booked\"}";
            }
            
                // Ensure all seats are from the same flight
                if (flightId == null) {
                    flightId = assignment.getFlightId();
                } else if (!flightId.equals(assignment.getFlightId())) {
                    return "{\"success\": false, \"message\": \"All seats must be from the same flight\"}";
                }
                
                targetAssignments.add(assignment);
            }
            
            // Get seat type prices for this flight
            List<FlightSeatType> seatTypes = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
            Map<Integer, BigDecimal> seatTypePrices = new HashMap<>();
            for (FlightSeatType seatType : seatTypes) {
                seatTypePrices.put(seatType.getId(), seatType.getPrice());
            }
            
            // Calculate total price and create individual bookings
            List<Integer> bookingIds = new ArrayList<>();
            
            for (FlightSeatAssignment assignment : targetAssignments) {
                BigDecimal seatPrice = seatTypePrices.get(assignment.getFlightSeatTypeId());
                if (seatPrice == null) {
                    return "{\"success\": false, \"message\": \"Price not found for seat " + assignment.getSeatNumber() + "\"}";
                }
                
                totalPrice = totalPrice.add(seatPrice);
            
                // Create individual booking for each seat
            Booking booking = new Booking();
            booking.setUserId(user.getId());
                booking.setFlightSeatAssignmentId(assignment.getId());
            booking.setStatus("PENDING");
            
            booking = bookingRepository.createBooking(booking);
            
            if (booking != null) {
                    bookingIds.add(booking.getId());
                // Mark seat as unavailable
                    flightSeatAssignmentRepository.updateSeatAvailability(assignment.getId(), false);
            } else {
                    return "{\"success\": false, \"message\": \"Failed to create booking for seat " + assignment.getSeatNumber() + "\"}";
                }
            }
            
            // Return the first booking ID for payment processing (we'll process all bookings)
            StringBuilder bookingIdsJson = new StringBuilder("[");
            for (int i = 0; i < bookingIds.size(); i++) {
                if (i > 0) bookingIdsJson.append(",");
                bookingIdsJson.append(bookingIds.get(i));
            }
            bookingIdsJson.append("]");
            
            return "{\"success\": true, \"message\": \"Bookings created successfully!\", \"bookingId\": " + bookingIds.get(0) + ", \"bookingIds\": " + bookingIdsJson.toString() + ", \"totalAmount\": " + totalPrice.toString() + "}";
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                errorMessage = errorMessage.replace("\"", "\\\""); // Escape quotes in error message
            }
            return "{\"success\": false, \"message\": \"" + errorMessage + "\"}";
        }
    }
    
    private FlightSeatAssignment getFlightSeatAssignmentById(int assignmentId) {
        return flightSeatAssignmentRepository.getFlightSeatAssignmentById(assignmentId);
    }
    
    @PostMapping("/pay-bookings")
    @ResponseBody
    public String payBookings(@RequestParam List<Integer> bookingIds) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName());
            
            if (user == null) {
                return "{\"success\": false, \"message\": \"User not found\"}";
            }
            
            // Update all bookings to PAID status
            for (Integer bookingId : bookingIds) {
                bookingRepository.updateBookingStatus(bookingId, "PAID");
            }
            
            return "{\"success\": true, \"message\": \"Payment successful! Your booking" + (bookingIds.size() > 1 ? "s have" : " has") + " been confirmed.\"}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Payment failed: " + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/cancel-booking")
    @ResponseBody
    public String cancelBooking(@RequestParam int bookingId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName());
            
            if (user == null) {
                return "{\"success\": false, \"message\": \"User not found\"}";
            }
            
            // Get the flight seat assignment ID before cancelling
            int flightSeatAssignmentId = bookingRepository.getFlightSeatAssignmentIdByBookingId(bookingId);
            
            if (flightSeatAssignmentId == -1) {
                return "{\"success\": false, \"message\": \"Booking not found\"}";
            }
            
            // Cancel the booking
            boolean cancelled = bookingRepository.cancelBooking(bookingId, user.getId());
            
            if (cancelled) {
                // Make the seat available again
                flightSeatAssignmentRepository.updateSeatAvailability(flightSeatAssignmentId, true);
                return "{\"success\": true, \"message\": \"Booking cancelled successfully. Your seat is now available for other passengers.\"}";
            } else {
                return "{\"success\": false, \"message\": \"Unable to cancel booking. It may have already been cancelled or doesn't belong to you.\"}";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Error cancelling booking: " + e.getMessage() + "\"}";
        }
    }
    
    @GetMapping("/my-bookings")
    public String showMyBookings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName());
        
        if (user != null) {
            List<Booking> bookings = bookingRepository.getBookingsByUserId(user.getId());
            model.addAttribute("bookings", bookings);
        }
        
        return "customer/my-bookings";
    }
} 