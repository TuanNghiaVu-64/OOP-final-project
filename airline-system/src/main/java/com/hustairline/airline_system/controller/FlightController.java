package com.hustairline.airline_system.controller;

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
import com.hustairline.airline_system.repository.FlightRepository;
import com.hustairline.airline_system.repository.PlaneRepository;
import com.hustairline.airline_system.repository.LocationRepository;
import com.hustairline.airline_system.service.ReferentialIntegrityService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/flights")
public class FlightController {
    
    private final FlightRepository flightRepository;
    private final PlaneRepository planeRepository;
    private final LocationRepository locationRepository;
    private final ReferentialIntegrityService referentialIntegrityService;
    
    public FlightController(FlightRepository flightRepository, 
                           PlaneRepository planeRepository,
                           LocationRepository locationRepository,
                           ReferentialIntegrityService referentialIntegrityService) {
        this.flightRepository = flightRepository;
        this.planeRepository = planeRepository;
        this.locationRepository = locationRepository;
        this.referentialIntegrityService = referentialIntegrityService;
    }
    
    @GetMapping("/add")
    public String showAddFlightForm(Model model) {
        model.addAttribute("planes", planeRepository.findApprovedPlanes());
        model.addAttribute("locations", locationRepository.getAllLocations());
        return "flights/add-flight";
    }
    
    @PostMapping("/add")
    public String addFlight(@RequestParam int planeId,
                           @RequestParam int originId,
                           @RequestParam int destinationId,
                           @RequestParam String departureDateTime,
                           @RequestParam String arrivalDateTime,
                           RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (originId == destinationId) {
                redirectAttributes.addFlashAttribute("error", "Origin and destination cannot be the same");
                return "redirect:/flights/add";
            }

            // Parse datetime
            LocalDateTime departureTime = LocalDateTime.parse(departureDateTime, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalDateTime, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            
            // Check if departure time is in the future
            if (departureTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "Departure time must be in the future");
                return "redirect:/flights/add";
            }
            
            // Check if arrival time is after departure time
            if (arrivalTime.isBefore(departureTime) || arrivalTime.equals(departureTime)) {
                redirectAttributes.addFlashAttribute("error", "Arrival time must be after departure time");
                return "redirect:/flights/add";
            }
            
            // Check for flight overlap with same plane
            ReferentialIntegrityService.ReferenceCheckResult overlapCheck = 
                referentialIntegrityService.checkFlightOverlap(planeId, departureTime, arrivalTime, null);
            
            if (!overlapCheck.canProceed()) {
                String errorMessage = "Cannot schedule flight - aircraft is already scheduled for overlapping times:\n";
                errorMessage += String.join("\n", overlapCheck.getDependencies());
                redirectAttributes.addFlashAttribute("error", errorMessage);
                return "redirect:/flights/add";
            }

            Flight flight = new Flight();
            flight.setPlaneId(planeId);
            flight.setOriginId(originId);
            flight.setDestinationId(destinationId);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);
            
            flightRepository.addFlight(flight);
            redirectAttributes.addFlashAttribute("success", "Flight added successfully! Waiting for manager approval.");
            return "redirect:/admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add flight: " + e.getMessage());
            return "redirect:/flights/add";
        }
    }
    
    @GetMapping("/list")
    public String listFlights(Model model) {
        model.addAttribute("flights", flightRepository.getAllFlights());
        return "flights/list-flights";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deleteFlight(@PathVariable int id) {
        try {
            Flight flight = flightRepository.findById(id);
            if (flight == null) {
                return "error: Flight not found";
            }
            
            // Check if flight can be deleted
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canDeleteFlight(id);
            
            if (!checkResult.canProceed()) {
                return "error: Cannot delete flight because it is currently in use. " + 
                       String.join("; ", checkResult.getDependencies());
            }
            
            flightRepository.deleteFlight(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/approve/{id}")
    @ResponseBody
    public String approveFlight(@PathVariable int id) {
        try {
            Flight flight = flightRepository.findById(id);
            if (flight == null) {
                return "error: Flight not found";
            }
            
            flightRepository.updateApprovalStatus(id, true);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/reject/{id}")
    @ResponseBody
    public String rejectFlight(@PathVariable int id) {
        try {
            flightRepository.deleteFlight(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @GetMapping("/review")
    public String reviewFlights(Model model) {
        model.addAttribute("pendingFlights", flightRepository.findPendingFlights());
        return "flights/review-flights";
    }

    @GetMapping("/manager-list")
    public String managerListFlights(Model model) {
        model.addAttribute("flights", flightRepository.getAllFlights());
        return "flights/manager-list-flights";
    }
} 