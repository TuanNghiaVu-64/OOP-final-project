package com.hustairline.airline_system.model;

public class Plane {
    private int id;
    private String model;
    private String size;  // Just 'small' or 'big'
    private boolean approved;
    private int assignedSeatsCount;
    private boolean canModifySeats = true;
    private String modificationBlockedReason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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