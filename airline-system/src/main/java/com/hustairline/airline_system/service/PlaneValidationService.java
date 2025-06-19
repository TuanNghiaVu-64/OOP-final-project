package com.hustairline.airline_system.service;

import com.hustairline.airline_system.model.Plane;
import com.hustairline.airline_system.repository.PlaneRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of ValidationService for Plane entities
 * Demonstrates Strategy Pattern implementation
 */
@Service
public class PlaneValidationService implements ValidationService<Plane> {

    private final PlaneRepository planeRepository;

    public PlaneValidationService(PlaneRepository planeRepository) {
        this.planeRepository = planeRepository;
    }

    @Override
    public ValidationResult validateForSave(Plane plane) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Basic validation
        if (plane.getModel() == null || plane.getModel().trim().isEmpty()) {
            errors.add("Plane model is required");
        }

        if (plane.getSize() == null || plane.getSize().trim().isEmpty()) {
            errors.add("Plane size is required");
        } else if (!plane.getSize().equalsIgnoreCase("small") && !plane.getSize().equalsIgnoreCase("big")) {
            errors.add("Plane size must be either 'small' or 'big'");
        }

        // Business rule validation
        if (plane.getModel() != null && planeRepository.existsByModel(plane.getModel())) {
            errors.add("A plane with model '" + plane.getModel() + "' already exists");
        }

        // Warnings
        if (plane.getModel() != null && plane.getModel().length() > 50) {
            warnings.add("Plane model name is very long. Consider shortening it.");
        }

        if (plane.isRecentlyCreated() && plane.isApproved()) {
            warnings.add("Plane is being approved very quickly after creation. Please double-check.");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    @Override
    public ValidationResult validateForUpdate(Plane plane) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Basic validation (reuse save validation)
        ValidationResult saveValidation = validateForSave(plane);
        errors.addAll(saveValidation.getErrors());
        warnings.addAll(saveValidation.getWarnings());

        // Update-specific validation
        if (plane.getId() <= 0) {
            errors.add("Cannot update plane: Invalid ID");
        }

        // Check if plane exists
        Plane existingPlane = planeRepository.findById(plane.getId());
        if (existingPlane == null) {
            errors.add("Cannot update plane: Plane not found");
        } else {
            // Check if model is being changed to an existing one
            if (!existingPlane.getModel().equals(plane.getModel()) && 
                planeRepository.existsByModel(plane.getModel())) {
                errors.add("Cannot change model to '" + plane.getModel() + "': Model already exists");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    @Override
    public ValidationResult validateForDelete(Plane plane) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (plane.getId() <= 0) {
            errors.add("Cannot delete plane: Invalid ID");
        }

        if (plane.isApproved()) {
            warnings.add("Deleting an approved plane. This may affect existing flights.");
        }

        if (plane.getAssignedSeatsCount() > 0) {
            errors.add("Cannot delete plane: Plane has assigned seats");
        }

        // You could add more business rule checks here
        // For example, check if plane is used in any flights

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    @Override
    public boolean isValidEntity(Plane plane) {
        return plane != null && plane.isValid();
    }

    @Override
    public Class<Plane> getEntityType() {
        return Plane.class;
    }

    /**
     * Additional business-specific validation method
     * @param plane the plane to validate
     * @return true if plane can accept new flights
     */
    public boolean canAcceptNewFlights(Plane plane) {
        return plane != null && 
               plane.isApproved() && 
               plane.isActive() && 
               plane.getAssignedSeatsCount() > 0;
    }
} 