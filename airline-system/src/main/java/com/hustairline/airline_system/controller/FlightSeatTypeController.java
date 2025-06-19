package com.hustairline.airline_system.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

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
import com.hustairline.airline_system.model.FlightSeatType;
import com.hustairline.airline_system.model.SeatType;
import com.hustairline.airline_system.repository.FlightRepository;
import com.hustairline.airline_system.repository.FlightSeatTypeRepository;
import com.hustairline.airline_system.repository.SeatTypeRepository;
import com.hustairline.airline_system.repository.SeatRepository;
import com.hustairline.airline_system.repository.FlightSeatAssignmentRepository;
import com.hustairline.airline_system.service.ReferentialIntegrityService;

@Controller
@RequestMapping("/flight-pricing")
public class FlightSeatTypeController {
    
    private final FlightSeatTypeRepository flightSeatTypeRepository;
    private final FlightRepository flightRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final SeatRepository seatRepository;
    private final FlightSeatAssignmentRepository flightSeatAssignmentRepository;
    private final ReferentialIntegrityService referentialIntegrityService;
    
    public FlightSeatTypeController(FlightSeatTypeRepository flightSeatTypeRepository,
                                   FlightRepository flightRepository,
                                   SeatTypeRepository seatTypeRepository,
                                   SeatRepository seatRepository,
                                   FlightSeatAssignmentRepository flightSeatAssignmentRepository,
                                   ReferentialIntegrityService referentialIntegrityService) {
        this.flightSeatTypeRepository = flightSeatTypeRepository;
        this.flightRepository = flightRepository;
        this.seatTypeRepository = seatTypeRepository;
        this.seatRepository = seatRepository;
        this.flightSeatAssignmentRepository = flightSeatAssignmentRepository;
        this.referentialIntegrityService = referentialIntegrityService;
    }
    
    // Admin endpoints
    @GetMapping("/list-flights")
    public String listApprovedFlights(Model model) {
        List<Flight> allApprovedFlights = flightRepository.getAllFlights()
            .stream()
            .filter(Flight::isApproved)
            .toList();
        
        // Filter flights to only include those where all seats are assigned
        List<Flight> pricingReadyFlights = allApprovedFlights.stream()
            .filter(flight -> seatRepository.areAllSeatsAssigned(flight.getPlaneId()))
            .toList();
        
        model.addAttribute("flights", pricingReadyFlights);
        model.addAttribute("totalApprovedFlights", allApprovedFlights.size());
        model.addAttribute("pricingReadyFlights", pricingReadyFlights.size());
        return "flight-pricing/list-flights";
    }
    
    @GetMapping("/set-prices/{flightId}")
    public String showSetPricesForm(@PathVariable int flightId, Model model, RedirectAttributes redirectAttributes) {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null || !flight.isApproved()) {
            redirectAttributes.addFlashAttribute("error", "Flight not found or not approved");
            return "redirect:/flight-pricing/list-flights";
        }
        
        // Check if all seats are assigned seat types
        if (!seatRepository.areAllSeatsAssigned(flight.getPlaneId())) {
            redirectAttributes.addFlashAttribute("error", "Cannot set pricing. All seats on the aircraft must be assigned seat types first. Please complete seat assignment for this plane.");
            return "redirect:/flight-pricing/list-flights";
        }
        
        // Get seat types that are actually used on this plane
        List<Integer> usedSeatTypeIds = seatRepository.getUsedSeatTypeIds(flight.getPlaneId());
        List<SeatType> usedSeatTypes = seatTypeRepository.getApprovedSeatTypes()
            .stream()
            .filter(st -> usedSeatTypeIds.contains(st.getId()))
            .toList();
        
        List<FlightSeatType> existingPrices = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
        
        model.addAttribute("flight", flight);
        model.addAttribute("seatTypes", usedSeatTypes);
        model.addAttribute("existingPrices", existingPrices);
        return "flight-pricing/set-prices";
    }
    
    @PostMapping("/set-prices/{flightId}")
    public String setPrices(@PathVariable int flightId,
                           @RequestParam("seatTypeId") List<Integer> seatTypeIds,
                           @RequestParam("price") List<BigDecimal> prices,
                           RedirectAttributes redirectAttributes) {
        try {
            Flight flight = flightRepository.findById(flightId);
            if (flight == null || !flight.isApproved()) {
                redirectAttributes.addFlashAttribute("error", "Flight not found or not approved");
                return "redirect:/flight-pricing/list-flights";
            }
            
            // Check if all seats are assigned seat types
            if (!seatRepository.areAllSeatsAssigned(flight.getPlaneId())) {
                redirectAttributes.addFlashAttribute("error", "Cannot set pricing. All seats on the aircraft must be assigned seat types first.");
                return "redirect:/flight-pricing/list-flights";
            }
            
            // Validate that prices are set for all seat types used on this plane
            List<Integer> usedSeatTypeIds = seatRepository.getUsedSeatTypeIds(flight.getPlaneId());
            List<Integer> pricesSetFor = new ArrayList<>();
            
            for (int i = 0; i < seatTypeIds.size(); i++) {
                int seatTypeId = seatTypeIds.get(i);
                BigDecimal price = prices.get(i);
                
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    pricesSetFor.add(seatTypeId);
                    
                    if (flightSeatTypeRepository.existsByFlightAndSeatType(flightId, seatTypeId)) {
                        // Update existing price
                        List<FlightSeatType> existing = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
                        for (FlightSeatType fst : existing) {
                            if (fst.getSeatTypeId() == seatTypeId) {
                                flightSeatTypeRepository.updatePrice(fst.getId(), price);
                                break;
                            }
                        }
                    } else {
                        // Create new flight seat type
                        FlightSeatType flightSeatType = new FlightSeatType();
                        flightSeatType.setFlightId(flightId);
                        flightSeatType.setSeatTypeId(seatTypeId);
                        flightSeatType.setPrice(price);
                        flightSeatTypeRepository.addFlightSeatType(flightSeatType);
                    }
                }
            }
            
            // Check if all used seat types have prices
            if (!pricesSetFor.containsAll(usedSeatTypeIds)) {
                redirectAttributes.addFlashAttribute("error", "Please set prices for all seat types used on this aircraft.");
                return "redirect:/flight-pricing/set-prices/" + flightId;
            }
            
            redirectAttributes.addFlashAttribute("success", "Prices set successfully! Waiting for manager approval.");
            return "redirect:/flight-pricing/list-flights";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to set prices: " + e.getMessage());
            return "redirect:/flight-pricing/set-prices/" + flightId;
        }
    }
    
    // Manager endpoints
    @GetMapping("/review")
    public String reviewFlightPricing(Model model) {
        List<FlightSeatType> allFlightPrices = flightSeatTypeRepository.getAllFlightSeatTypes();
        
        // Group flight prices by flight ID
        Map<Integer, List<FlightSeatType>> groupedFlightPrices = allFlightPrices.stream()
            .collect(Collectors.groupingBy(FlightSeatType::getFlightId));
        
        // Filter to only show flights that have pending (not approved) pricing
        Map<Integer, List<FlightSeatType>> pendingFlightPrices = groupedFlightPrices.entrySet().stream()
            .filter(entry -> entry.getValue().stream().anyMatch(fst -> !fst.isApproved()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        model.addAttribute("groupedFlightPrices", pendingFlightPrices);
        return "flight-pricing/review-prices";
    }
    
    @GetMapping("/approved")
    public String viewApprovedFlights(Model model) {
        List<FlightSeatType> allFlightPrices = flightSeatTypeRepository.getAllFlightSeatTypes();
        
        // Group flight prices by flight ID
        Map<Integer, List<FlightSeatType>> groupedFlightPrices = allFlightPrices.stream()
            .collect(Collectors.groupingBy(FlightSeatType::getFlightId));
        
        // Filter to only show flights where ALL pricing is approved
        Map<Integer, List<FlightSeatType>> approvedFlightPrices = groupedFlightPrices.entrySet().stream()
            .filter(entry -> entry.getValue().stream().allMatch(FlightSeatType::isApproved))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        model.addAttribute("groupedFlightPrices", approvedFlightPrices);
        return "flight-pricing/approved-flights";
    }
    
    // Debug endpoint to check data
    @GetMapping("/review-data")
    @ResponseBody
    public Map<String, Object> getReviewData() {
        List<FlightSeatType> allFlightPrices = flightSeatTypeRepository.getAllFlightSeatTypes();
        
        Map<Integer, List<FlightSeatType>> groupedFlightPrices = allFlightPrices.stream()
            .collect(Collectors.groupingBy(FlightSeatType::getFlightId));
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalItems", allFlightPrices.size());
        result.put("groupedFlights", groupedFlightPrices.size());
        result.put("data", groupedFlightPrices);
        
        return result;
    }
    
    @PostMapping("/review/{id}")
    @ResponseBody
    public String reviewFlightSeatType(@PathVariable int id, @RequestParam boolean approved) {
        try {
            if (approved) {
                // Get the flight seat type before approving to get the flight ID
                FlightSeatType currentFlightSeatType = null;
                List<FlightSeatType> allPrices = flightSeatTypeRepository.getAllFlightSeatTypes();
                for (FlightSeatType fst : allPrices) {
                    if (fst.getId() == id) {
                        currentFlightSeatType = fst;
                        break;
                    }
                }
                
                if (currentFlightSeatType == null) {
                    return "{\"success\": false, \"message\": \"Flight seat type not found\"}";
                }
                
                int flightId = currentFlightSeatType.getFlightId();
                
                // Update approval status
                flightSeatTypeRepository.updateApprovalStatus(id, true);
                
                // Check if all prices for this flight are now approved
                List<FlightSeatType> allPricesForFlight = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
                boolean allApproved = allPricesForFlight.stream().allMatch(FlightSeatType::isApproved);
                
                if (allApproved) {
                    // Create flight seat assignments automatically
                    flightSeatAssignmentRepository.createFlightSeatAssignments(flightId);
                }
                
                return "{\"success\": true, \"message\": \"Pricing approved successfully\"}";
            } else {
                // Reject - need to handle previously approved configs that have seat assignments
                
                // Get the flight seat type to find the flight ID
                FlightSeatType currentFlightSeatType = null;
                List<FlightSeatType> allPrices = flightSeatTypeRepository.getAllFlightSeatTypes();
                for (FlightSeatType fst : allPrices) {
                    if (fst.getId() == id) {
                        currentFlightSeatType = fst;
                        break;
                    }
                }
                
                if (currentFlightSeatType == null) {
                    return "{\"success\": false, \"message\": \"Flight seat type not found\"}";
                }
                
                int flightId = currentFlightSeatType.getFlightId();
                
                // Check if this flight has seat assignments that need to be cleared
                // This happens when pricing was previously approved and then modified
                boolean hasSeatsAssignments = flightSeatAssignmentRepository.flightSeatAssignmentsExist(flightId);
                
                if (hasSeatsAssignments) {
                    // Delete all seat assignments for this flight first to avoid foreign key conflicts
                    flightSeatAssignmentRepository.deleteFlightSeatAssignments(flightId);
                }
                
                // Now safely delete the pricing
                flightSeatTypeRepository.deleteFlightSeatType(id);
                
                return "{\"success\": true, \"message\": \"Pricing rejected and removed\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/approve-flight/{flightId}")
    @ResponseBody
    public String approveAllForFlight(@PathVariable int flightId) {
        try {
            // Get all flight seat types for this flight
            List<FlightSeatType> flightSeatTypes = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
            
            if (flightSeatTypes.isEmpty()) {
                return "{\"success\": false, \"message\": \"No pricing found for this flight\"}";
            }
            
            // Approve all seat types for this flight
            for (FlightSeatType fst : flightSeatTypes) {
                flightSeatTypeRepository.updateApprovalStatus(fst.getId(), true);
            }
            
            // Create flight seat assignments automatically
            flightSeatAssignmentRepository.createFlightSeatAssignments(flightId);
            
            return "{\"success\": true, \"message\": \"All seat type pricing approved successfully!\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/reject-flight/{flightId}")
    @ResponseBody
    public String rejectAllForFlight(@PathVariable int flightId) {
        try {
            // Get all flight seat types for this flight
            List<FlightSeatType> flightSeatTypes = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
            
            if (flightSeatTypes.isEmpty()) {
                return "{\"success\": false, \"message\": \"No pricing found for this flight\"}";
            }
            
            // Check if this flight has seat assignments that need to be cleared first
            // This happens when pricing was previously approved and then modified
            boolean hasSeatsAssignments = flightSeatAssignmentRepository.flightSeatAssignmentsExist(flightId);
            
            if (hasSeatsAssignments) {
                // Delete all seat assignments for this flight first to avoid foreign key conflicts
                flightSeatAssignmentRepository.deleteFlightSeatAssignments(flightId);
            }
            
            // Now safely delete all seat types pricing for this flight
            for (FlightSeatType fst : flightSeatTypes) {
                flightSeatTypeRepository.deleteFlightSeatType(fst.getId());
            }
            
            return "{\"success\": true, \"message\": \"All seat type pricing rejected and removed!\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/delete-flight/{flightId}")
    @ResponseBody
    public String deleteApprovedFlightPricing(@PathVariable int flightId) {
        try {
            // Check if flight pricing can be modified
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canModifyFlightPricing(flightId);
            
            if (!checkResult.canProceed()) {
                return "{\"success\": false, \"message\": \"Cannot delete flight pricing because it is currently in use. " + 
                       String.join("; ", checkResult.getDependencies()) + "\"}";
            }
            
            // Get all flight seat types for this flight
            List<FlightSeatType> flightSeatTypes = flightSeatTypeRepository.getFlightSeatTypesByFlightId(flightId);
            
            if (flightSeatTypes.isEmpty()) {
                return "{\"success\": false, \"message\": \"No pricing found for this flight\"}";
            }
            
            // Delete all seat assignments for this flight first (if any)
            flightSeatAssignmentRepository.deleteFlightSeatAssignments(flightId);
            
            // Delete all seat types pricing for this flight
            for (FlightSeatType fst : flightSeatTypes) {
                flightSeatTypeRepository.deleteFlightSeatType(fst.getId());
            }
            
            return "{\"success\": true, \"message\": \"Flight pricing and seat assignments deleted successfully!\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
} 