package com.hustairline.airline_system.model;

public class Location extends BaseEntity {
    private int id;
    private String city;
    private String country;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean isValid() {
        return city != null && !city.trim().isEmpty() && country != null && !country.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Location";
    }
} 