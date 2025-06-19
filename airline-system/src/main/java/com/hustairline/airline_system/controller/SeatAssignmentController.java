package com.hustairline.airline_system.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hustairline.airline_system.model.Seat;
import com.hustairline.airline_system.model.Plane;
import com.hustairline.airline_system.repository.SeatRepository;
import com.hustairline.airline_system.repository.SeatTypeRepository;
import com.hustairline.airline_system.repository.PlaneRepository;
import com.hustairline.airline_system.service.ReferentialIntegrityService;

@Controller
@RequestMapping("/seats")
public class SeatAssignmentController {

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private SeatTypeRepository seatTypeRepository;
    
    @Autowired
    private PlaneRepository planeRepository;
    
    @Autowired
    private ReferentialIntegrityService referentialIntegrityService;

    @GetMapping("/manage")
    public String listPlanesForAssignment(Model model) {
        List<Plane> approvedPlanes = planeRepository.findApprovedPlanes();
        
        // Add modification status for each plane
        for (Plane plane : approvedPlanes) {
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canModifyPlaneSeats(plane.getId());
            plane.setCanModifySeats(checkResult.canProceed());
            if (!checkResult.canProceed()) {
                plane.setModificationBlockedReason(String.join("; ", checkResult.getDependencies()));
            }
        }
        
        model.addAttribute("planes", approvedPlanes);
        return "seat-management";
    }

    @GetMapping("/assign/{planeId}")
    public String showSeatAssignment(@PathVariable int planeId, Model model) {
        Plane plane = planeRepository.findById(planeId);
        if (plane == null || !plane.isApproved()) {
            return "redirect:/seats/manage?error=Plane not found or not approved";
        }

        // Redirect to appropriate endpoint based on plane size
        if (plane.getSize().equalsIgnoreCase("big")) {
            return "redirect:/seats/assign/big/" + planeId;
        } else {
            return "redirect:/seats/assign/small/" + planeId;
        }
    }

    @GetMapping("/assign/small/{planeId}")
    public String showSmallPlaneSeatAssignment(@PathVariable int planeId, Model model, CsrfToken token) {
        Plane plane = planeRepository.findById(planeId);
        if (plane == null || !plane.isApproved()) {
            return "redirect:/seats/manage?error=Plane not found or not approved";
        }

        // Check if plane seat assignments can be modified
        ReferentialIntegrityService.ReferenceCheckResult checkResult = 
            referentialIntegrityService.canModifyPlaneSeats(planeId);
        
        if (!checkResult.canProceed()) {
            // Redirect back with detailed error message
            String errorMessage = "Cannot modify seat assignments for this plane. " + 
                                 String.join("; ", checkResult.getDependencies()) + 
                                 ". Seat assignments are locked once flights have approved pricing configurations.";
            return "redirect:/seats/manage?error=" + java.net.URLEncoder.encode(errorMessage, java.nio.charset.StandardCharsets.UTF_8);
        }

        List<Seat> seats = seatRepository.getSeatsForPlane(planeId);
        
        // If no seats exist for this plane, initialize them
        if (seats.isEmpty()) {
            seatRepository.initializeSeatsForPlane(planeId, plane.getSize());
            seats = seatRepository.getSeatsForPlane(planeId);
        }
        
        // Create a map of row numbers to their corresponding seats
        Map<Integer, List<Seat>> seatMap = new HashMap<>();
        for (Seat seat : seats) {
            seatMap.computeIfAbsent(seat.getRow(), _ -> new ArrayList<>()).add(seat);
        }
        
        model.addAttribute("plane", plane);
        model.addAttribute("planeId", planeId);
        model.addAttribute("seatTypes", seatTypeRepository.getApprovedSeatTypes());
        model.addAttribute("seatMap", seatMap);
        model.addAttribute("_csrf", token);

        return "seat-assignment";
    }

    @GetMapping("/assign/big/{planeId}")
    public String showBigPlaneSeatAssignment(@PathVariable int planeId, Model model, CsrfToken token) {
        Plane plane = planeRepository.findById(planeId);
        if (plane == null || !plane.isApproved()) {
            return "redirect:/seats/manage?error=Plane not found or not approved";
        }

        // Check if plane seat assignments can be modified
        ReferentialIntegrityService.ReferenceCheckResult checkResult = 
            referentialIntegrityService.canModifyPlaneSeats(planeId);
        
        if (!checkResult.canProceed()) {
            // Redirect back with detailed error message
            String errorMessage = "Cannot modify seat assignments for this plane. " + 
                                 String.join("; ", checkResult.getDependencies()) + 
                                 ". Seat assignments are locked once flights have approved pricing configurations.";
            return "redirect:/seats/manage?error=" + java.net.URLEncoder.encode(errorMessage, java.nio.charset.StandardCharsets.UTF_8);
        }

        List<Seat> seats = seatRepository.getSeatsForPlane(planeId);
        
        // If no seats exist for this plane, initialize them
        if (seats.isEmpty()) {
            seatRepository.initializeSeatsForPlane(planeId, plane.getSize());
            seats = seatRepository.getSeatsForPlane(planeId);
        }
        
        // Create a map of row numbers to their corresponding seats
        Map<Integer, List<Seat>> seatMap = new HashMap<>();
        for (Seat seat : seats) {
            seatMap.computeIfAbsent(seat.getRow(), _ -> new ArrayList<>()).add(seat);
        }

        model.addAttribute("plane", plane);
        model.addAttribute("planeId", planeId);
        model.addAttribute("seatTypes", seatTypeRepository.getApprovedSeatTypes());
        model.addAttribute("seatMap", seatMap);
        model.addAttribute("_csrf", token);

        return "seat-assignment-big";
    }

    @PostMapping("/assign/{planeId}")
    @ResponseBody
    public String updateSeatTypes(@PathVariable int planeId, @RequestBody List<Seat> seats) {
        try {
            // Check if plane seat assignments can be modified
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canModifyPlaneSeats(planeId);
            
            if (!checkResult.canProceed()) {
                return "error: Cannot modify seat assignments because they are currently in use. " + 
                       String.join("; ", checkResult.getDependencies()) + 
                       ". Please ensure no approved flights with pricing are using this plane.";
            }
            
            seatRepository.updateSeatTypes(planeId, seats);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/initialize/{planeId}")
    @ResponseBody
    public String initializeSeats(@PathVariable int planeId, @RequestParam String size) {
        try {
            seatRepository.initializeSeatsForPlane(planeId, size);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 