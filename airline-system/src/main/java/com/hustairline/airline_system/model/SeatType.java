package com.hustairline.airline_system.model;

public class SeatType extends BaseEntity {
    private int id;
    private String name;
    private String features;
    private boolean approved;
    private String color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "SeatType";
    }
} 