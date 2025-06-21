package com.hustairline.airline_system.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FlightSeatType extends BaseEntity {
    private int id;
    private int flightId;
    private int seatTypeId;
    private BigDecimal price;
    private boolean approved;
    
    // Additional fields for display purposes
    private String seatTypeName;
    private String seatTypeFeatures;
    private String flightInfo; // e.g., "Boeing 737 - NYC to LAX"
    private String departureTime;
    private String flightPlaneModel;
    private String flightOrigin;
    private String flightDestination;
    private Timestamp flightDepartureTime;
    private Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getSeatTypeId() {
        return seatTypeId;
    }

    public void setSeatTypeId(int seatTypeId) {
        this.seatTypeId = seatTypeId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getSeatTypeName() {
        return seatTypeName;
    }

    public void setSeatTypeName(String seatTypeName) {
        this.seatTypeName = seatTypeName;
    }

    public String getSeatTypeFeatures() {
        return seatTypeFeatures;
    }

    public void setSeatTypeFeatures(String seatTypeFeatures) {
        this.seatTypeFeatures = seatTypeFeatures;
    }

    public String getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(String flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getFlightPlaneModel() {
        return flightPlaneModel;
    }

    public void setFlightPlaneModel(String flightPlaneModel) {
        this.flightPlaneModel = flightPlaneModel;
    }

    public String getFlightOrigin() {
        return flightOrigin;
    }

    public void setFlightOrigin(String flightOrigin) {
        this.flightOrigin = flightOrigin;
    }

    public String getFlightDestination() {
        return flightDestination;
    }

    public void setFlightDestination(String flightDestination) {
        this.flightDestination = flightDestination;
    }

    public Timestamp getFlightDepartureTime() {
        return flightDepartureTime;
    }

    public void setFlightDepartureTime(Timestamp flightDepartureTime) {
        this.flightDepartureTime = flightDepartureTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean isValid() {
        return flightId > 0 && seatTypeId > 0 && price != null;
    }

    @Override
    public String getEntityType() {
        return "FlightSeatType";
    }
} 