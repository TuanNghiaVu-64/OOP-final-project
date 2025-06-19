package com.hustairline.airline_system.service;

import com.hustairline.airline_system.model.BaseEntity;
import java.util.List;

/**
 * Interface for validation services
 * Demonstrates Strategy Pattern and Interface usage
 * Allows different validation implementations for different entities
 */
public interface ValidationService<T extends BaseEntity> {
    
    /**
     * Validation result class to encapsulate validation outcome
     */
    class ValidationResult {
        private final boolean isValid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean isValid, List<String> errors, List<String> warnings) {
            this.isValid = isValid;
            this.errors = errors;
            this.warnings = warnings;
        }

        public boolean isValid() { return isValid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        
        public boolean hasWarnings() { return warnings != null && !warnings.isEmpty(); }
        public boolean hasErrors() { return errors != null && !errors.isEmpty(); }
    }

    /**
     * Validate an entity before saving
     * @param entity the entity to validate
     * @return validation result
     */
    ValidationResult validateForSave(T entity);

    /**
     * Validate an entity before updating
     * @param entity the entity to validate
     * @return validation result
     */
    ValidationResult validateForUpdate(T entity);

    /**
     * Validate an entity before deletion
     * @param entity the entity to validate
     * @return validation result
     */
    ValidationResult validateForDelete(T entity);

    /**
     * Quick validation check
     * @param entity the entity to validate
     * @return true if entity passes basic validation
     */
    boolean isValidEntity(T entity);

    /**
     * Get the entity type this validator handles
     * @return the entity class
     */
    Class<T> getEntityType();
} 