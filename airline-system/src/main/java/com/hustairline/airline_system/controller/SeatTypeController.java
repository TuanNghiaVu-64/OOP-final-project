package com.hustairline.airline_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import com.hustairline.airline_system.model.SeatType;
import com.hustairline.airline_system.repository.SeatTypeRepository;
import com.hustairline.airline_system.service.ReferentialIntegrityService;

@Controller
@RequestMapping("/seat-types")
public class SeatTypeController {
    
    private final SeatTypeRepository seatTypeRepository;
    private final ReferentialIntegrityService referentialIntegrityService;
    
    public SeatTypeController(SeatTypeRepository seatTypeRepository,
                             ReferentialIntegrityService referentialIntegrityService) {
        this.seatTypeRepository = seatTypeRepository;
        this.referentialIntegrityService = referentialIntegrityService;
    }
    
    @GetMapping("/add")
    public String showAddSeatTypeForm(Model model) {
        return "seat-types/add-seat-type";
    }
    
    @PostMapping("/add")
    public String addSeatType(@RequestParam String name, 
                            @RequestParam String features, 
                            RedirectAttributes redirectAttributes) {
        try {
            if (seatTypeRepository.existsByName(name)) {
                redirectAttributes.addFlashAttribute("error", "A seat type with name '" + name + "' already exists");
                return "redirect:/seat-types/add";
            }

            SeatType seatType = new SeatType();
            seatType.setName(name);
            seatType.setFeatures(features);
            
            seatTypeRepository.addSeatType(seatType);
            redirectAttributes.addFlashAttribute("success", "Seat type added successfully! Waiting for manager approval.");
            return "redirect:/admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add seat type: " + e.getMessage());
            return "redirect:/seat-types/add";
        }
    }
    
    @GetMapping("/list")
    public String listSeatTypes(Model model) {
        model.addAttribute("seatTypes", seatTypeRepository.getAllSeatTypes());
        return "seat-types/list-seat-types";
    }

    @GetMapping("/pending")
    public String listPendingSeatTypes(Model model) {
        model.addAttribute("seatTypes", seatTypeRepository.getPendingSeatTypes());
        return "seat-types/pending-seat-types";
    }

    @GetMapping("/manager-list")
    public String managerListSeatTypes(Model model) {
        model.addAttribute("seatTypes", seatTypeRepository.getAllSeatTypes());
        return "seat-types/manager-list-seat-types";
    }

    @PostMapping("/approve/{id}")
    @ResponseBody
    public String approveSeatType(@PathVariable int id) {
        try {
            seatTypeRepository.approveSeatType(id);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/reject/{id}")
    @ResponseBody
    public String rejectSeatType(@PathVariable int id) {
        try {
            // Check if seat type can be deleted
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canDeleteSeatType(id);
            
            if (!checkResult.canProceed()) {
                return "error: Cannot delete seat type because it is currently in use. " + 
                       String.join("; ", checkResult.getDependencies());
            }
            
            seatTypeRepository.deleteSeatType(id);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 