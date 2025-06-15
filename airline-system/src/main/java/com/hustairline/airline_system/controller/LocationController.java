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

import com.hustairline.airline_system.model.Location;
import com.hustairline.airline_system.repository.LocationRepository;
import com.hustairline.airline_system.service.ReferentialIntegrityService;

@Controller
@RequestMapping("/locations")
public class LocationController {
    
    private final LocationRepository locationRepository;
    private final ReferentialIntegrityService referentialIntegrityService;
    
    public LocationController(LocationRepository locationRepository, 
                             ReferentialIntegrityService referentialIntegrityService) {
        this.locationRepository = locationRepository;
        this.referentialIntegrityService = referentialIntegrityService;
    }
    
    @GetMapping("/add")
    public String showAddLocationForm(Model model) {
        return "locations/add-location";
    }
    
    @PostMapping("/add")
    public String addLocation(@RequestParam String city, 
                             @RequestParam(required = false) String country, 
                             RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (city == null || city.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "City is required");
                return "redirect:/locations/add";
            }

            // Check if location already exists
            if (locationRepository.existsByCityAndCountry(city.trim(), country != null ? country.trim() : "")) {
                redirectAttributes.addFlashAttribute("error", "A location with city '" + city.trim() + 
                    "' and country '" + (country != null ? country.trim() : "") + "' already exists");
                return "redirect:/locations/add";
            }

            Location location = new Location();
            location.setCity(city.trim());
            location.setCountry(country != null ? country.trim() : "");
            
            locationRepository.addLocation(location);
            redirectAttributes.addFlashAttribute("success", "Location added successfully!");
            return "redirect:/admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add location: " + e.getMessage());
            return "redirect:/locations/add";
        }
    }
    
    @GetMapping("/list")
    public String listLocations(Model model) {
        model.addAttribute("locations", locationRepository.getAllLocations());
        return "locations/list-locations";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deleteLocation(@PathVariable int id) {
        try {
            Location location = locationRepository.findById(id);
            if (location == null) {
                return "error: Location not found";
            }
            
            // Check if location can be deleted
            ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                referentialIntegrityService.canDeleteLocation(id);
            
            if (!checkResult.canProceed()) {
                return "error: Cannot delete location because it is currently in use. " + 
                       String.join("; ", checkResult.getDependencies());
            }
            
            locationRepository.deleteLocation(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditLocationForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Location location = locationRepository.findById(id);
            if (location == null) {
                redirectAttributes.addFlashAttribute("error", "Location not found");
                return "redirect:/locations/list";
            }
            model.addAttribute("location", location);
            return "locations/edit-location";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error loading location: " + e.getMessage());
            return "redirect:/locations/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateLocation(@PathVariable int id,
                                @RequestParam String city,
                                @RequestParam(required = false) String country,
                                RedirectAttributes redirectAttributes) {
        try {
            Location existingLocation = locationRepository.findById(id);
            if (existingLocation == null) {
                redirectAttributes.addFlashAttribute("error", "Location not found");
                return "redirect:/locations/list";
            }

            // Validate input
            if (city == null || city.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "City is required");
                return "redirect:/locations/edit/" + id;
            }

            String trimmedCity = city.trim();
            String trimmedCountry = country != null ? country.trim() : "";
            
            // Check if location is being used and if changes would affect existing references
            boolean isLocationChanged = !trimmedCity.equals(existingLocation.getCity()) || 
                                       !trimmedCountry.equals(existingLocation.getCountry());
            
            if (isLocationChanged) {
                // Check if location can be modified (has dependencies)
                ReferentialIntegrityService.ReferenceCheckResult checkResult = 
                    referentialIntegrityService.canDeleteLocation(id);
                
                if (!checkResult.canProceed()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Cannot modify location because it is currently in use. " + 
                        String.join("; ", checkResult.getDependencies()) + 
                        ". Please ensure no flights are using this location before making changes.");
                    return "redirect:/locations/edit/" + id;
                }
                
                // Check if another location with the same city/country exists
                if (locationRepository.existsByCityAndCountry(trimmedCity, trimmedCountry)) {
                    redirectAttributes.addFlashAttribute("error", "A location with city '" + trimmedCity + 
                        "' and country '" + trimmedCountry + "' already exists");
                    return "redirect:/locations/edit/" + id;
                }
            }

            existingLocation.setCity(trimmedCity);
            existingLocation.setCountry(trimmedCountry);
            
            locationRepository.updateLocation(existingLocation);
            redirectAttributes.addFlashAttribute("success", "Location updated successfully!");
            return "redirect:/locations/list";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update location: " + e.getMessage());
            return "redirect:/locations/edit/" + id;
        }
    }
} 