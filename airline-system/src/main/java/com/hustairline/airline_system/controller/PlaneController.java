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

import com.hustairline.airline_system.model.Plane;
import com.hustairline.airline_system.repository.PlaneRepository;
import com.hustairline.airline_system.repository.SeatRepository;
import com.hustairline.airline_system.service.ReferentialIntegrityService;

@Controller
@RequestMapping("/planes")
public class PlaneController {
    
    private final PlaneRepository planeRepository;
    private final SeatRepository seatRepository;
    private final ReferentialIntegrityService referentialIntegrityService;
    
    public PlaneController(PlaneRepository planeRepository, SeatRepository seatRepository,
                          ReferentialIntegrityService referentialIntegrityService) {
        this.planeRepository = planeRepository;
        this.seatRepository = seatRepository;
        this.referentialIntegrityService = referentialIntegrityService;
    }
    
    @GetMapping("/add")
    public String showAddPlaneForm(Model model) {
        return "planes/add-plane";
    }
    
    @PostMapping("/add")
    public String addPlane(@RequestParam String model, @RequestParam String size, RedirectAttributes redirectAttributes) {
        try {
            // First check if a plane with this model already exists
            if (planeRepository.existsByModel(model)) {
                redirectAttributes.addFlashAttribute("error", "A plane with model '" + model + "' already exists");
                return "redirect:/planes/add";
            }

            Plane plane = new Plane();
            plane.setModel(model);
            plane.setSize(size.toLowerCase());
            
            planeRepository.addPlane(plane);
            redirectAttributes.addFlashAttribute("success", "Plane added successfully! Waiting for manager approval.");
            return "redirect:/admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add plane: " + e.getMessage());
            return "redirect:/planes/add";
        }
    }
    
    @GetMapping("/list")
    public String listPlanes(Model model) {
        model.addAttribute("planes", planeRepository.getAllPlanes());
        return "planes/list-planes";
    }

    @GetMapping("/review")
    public String reviewPlanes(Model model) {
        model.addAttribute("pendingPlanes", planeRepository.findPendingPlanes());
        return "planes/review-planes";
    }

    @GetMapping("/manager-list")
    public String managerListPlanes(Model model) {
        model.addAttribute("planes", planeRepository.getAllPlanes());
        return "planes/manager-list-planes";
    }

    @PostMapping("/approve/{id}")
    @ResponseBody
    public String approvePlane(@PathVariable int id) {
        try {
            Plane plane = planeRepository.findById(id);
            if (plane == null) {
                return "error: Plane not found";
            }
            
            // Update plane status to approved
            planeRepository.updateApprovalStatus(id, true);
            
            // Initialize seats for the plane
            seatRepository.initializeSeatsForPlane(id, plane.getSize());
            
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/reject/{id}")
    @ResponseBody
    public String rejectPlane(@PathVariable int id) {
        try {
            Plane plane = planeRepository.findById(id);
            if (plane == null) {
                return "error: Plane not found";
            }
            
            // Check if plane can be deleted
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canDeletePlane(id);
            
            if (!checkResult.canProceed()) {
                return "error: Cannot delete plane because it is currently in use. " + 
                       String.join("; ", checkResult.getDependencies());
            }
            
            planeRepository.deletePlane(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
} 