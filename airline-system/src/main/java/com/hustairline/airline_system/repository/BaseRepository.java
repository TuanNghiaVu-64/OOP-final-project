package com.hustairline.airline_system.repository;

import java.util.List;

/**
 * Generic repository interface defining common CRUD operations
 * Demonstrates Interface usage in OOP design patterns
 * 
 * @param <T> the entity type
 * @param <ID> the primary key type
 */
public interface BaseRepository<T, ID> {
    
    /**
     * Save an entity to the database
     * @param entity the entity to save
     * @return the saved entity with generated ID
     */
    T save(T entity);
    
    /**
     * Find an entity by its ID
     * @param id the entity ID
     * @return the entity or null if not found
     */
    T findById(ID id);
    
    /**
     * Get all entities
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Delete an entity by ID
     * @param id the entity ID to delete
     * @return true if deletion was successful
     */
    boolean deleteById(ID id);
    
    /**
     * Check if an entity exists by ID
     * @param id the entity ID
     * @return true if entity exists
     */
    boolean existsById(ID id);
    
    /**
     * Count total number of entities
     * @return the count
     */
    long count();
} 