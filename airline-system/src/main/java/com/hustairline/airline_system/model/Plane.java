package com.hustairline.airline_system.model;

/**
 * Plane entity extending BaseEntity
 * Demonstrates Inheritance and Abstract class implementation
 */
public class Plane extends BaseEntity {
    private String model;
    private String size;  // Just 'small' or 'big'
    private boolean approved;
    private int assignedSeatsCount;
    private boolean canModifySeats = true;
    private String modificationBlockedReason;

    // Constructors
    public Plane() {
        super();
    }

    public Plane(String model, String size) {
        super();
        this.model = model;
        this.size = size;
        this.approved = false;
    }

    // Implementation of abstract methods from BaseEntity
    @Override
    public boolean isValid() {
        return model != null && !model.trim().isEmpty() &&
               size != null && (size.equalsIgnoreCase("small") || size.equalsIgnoreCase("big"));
    }

    @Override
    public String getEntityType() {
        return "Plane";
    }

    // Business logic methods
    public boolean canAcceptBookings() {
        return approved && isActive() && assignedSeatsCount > 0;
    }

    public void approve() {
        this.approved = true;
        touch(); // Update timestamp from parent class
    }

    public void reject() {
        softDelete(); // Use parent class method
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getAssignedSeatsCount() {
        return assignedSeatsCount;
    }

    public void setAssignedSeatsCount(int assignedSeatsCount) {
        this.assignedSeatsCount = assignedSeatsCount;
    }

    public boolean isCanModifySeats() {
        return canModifySeats;
    }

    public void setCanModifySeats(boolean canModifySeats) {
        this.canModifySeats = canModifySeats;
    }

    public String getModificationBlockedReason() {
        return modificationBlockedReason;
    }

    public void setModificationBlockedReason(String modificationBlockedReason) {
        this.modificationBlockedReason = modificationBlockedReason;
    }
} 