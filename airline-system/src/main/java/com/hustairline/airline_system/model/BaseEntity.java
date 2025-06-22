package com.hustairline.airline_system.model;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    protected int id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected boolean isActive = true;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BaseEntity(int id) {
        this();
        this.id = id;
    }

    // Abstract methods that subclasses MUST implement
    /**
     * Abstract method to validate entity state
     * Forces all entities to implement their own validation logic
     * @return true if entity is valid
     */
    public abstract boolean isValid();

    /**
     * Abstract method to get entity type name
     * Used for logging and debugging purposes
     * @return the entity type name
     */
    public abstract String getEntityType();

    // Concrete methods available to all subclasses
    /**
     * Updates the last modified timestamp
     * Called whenever entity is modified
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete - marks entity as inactive instead of removing
     */
    public void softDelete() {
        this.isActive = false;
        touch();
    }

    /**
     * Restore a soft-deleted entity
     */
    public void restore() {
        this.isActive = true;
        touch();
    }

    /**
     * Check if this entity was created recently (within last hour)
     * @return true if created within last hour
     */
    public boolean isRecentlyCreated() {
        return createdAt.isAfter(LocalDateTime.now().minusHours(1));
    }

    /**
     * Get age of entity in hours
     * @return hours since creation
     */
    public long getAgeInHours() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
    }

    // Standard getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if (active) {
            touch();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity that = (BaseEntity) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return getEntityType() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                '}';
    }
} 