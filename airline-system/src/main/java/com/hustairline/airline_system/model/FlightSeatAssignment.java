package com.hustairline.airline_system.model;

public class FlightSeatAssignment {
    private int id;
    private int flightId;
    private int seatId;
    private int flightSeatTypeId;
    private boolean available;
    
    // Additional fields for display purposes
    private String seatNumber;
    private String seatTypeName;
    private String seatTypeFeatures;
    private String flightInfo;

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

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getFlightSeatTypeId() {
        return flightSeatTypeId;
    }

    public void setFlightSeatTypeId(int flightSeatTypeId) {
        this.flightSeatTypeId = flightSeatTypeId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
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
} 