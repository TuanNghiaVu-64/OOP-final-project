package com.hustairline.airline_system.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.hustairline.airline_system.model.Location;

@Repository
public class LocationRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/HUSTAirline";
    private static final String USER = "postgres";
    private static final String PASS = "NgH1A@2005";

    public boolean existsByCityAndCountry(String city, String country) {
        String sql = "SELECT COUNT(*) FROM locations WHERE LOWER(city) = LOWER(?) AND LOWER(country) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city);
            stmt.setString(2, country != null ? country : "");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking for duplicate location", e);
        }
        return false;
    }

    public Location addLocation(Location location) {
        if (existsByCityAndCountry(location.getCity(), location.getCountry())) {
            throw new RuntimeException("A location with city '" + location.getCity() + 
                                     "' and country '" + location.getCountry() + "' already exists");
        }

        String sql = "INSERT INTO locations (city, country) VALUES (?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, location.getCity());
            stmt.setString(2, location.getCountry());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        location.setId(rs.getInt(1));
                        return location;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding location: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Location> getAllLocations() {
        String sql = "SELECT * FROM locations ORDER BY country, city";
        List<Location> locations = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Location location = new Location();
                    location.setId(rs.getInt("id"));
                    location.setCity(rs.getString("city"));
                    location.setCountry(rs.getString("country"));
                    locations.add(location);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving locations: " + e.getMessage(), e);
        }
        
        return locations;
    }

    public Location findById(int id) {
        String sql = "SELECT * FROM locations WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Location location = new Location();
                    location.setId(rs.getInt("id"));
                    location.setCity(rs.getString("city"));
                    location.setCountry(rs.getString("country"));
                    return location;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving location: " + e.getMessage(), e);
        }
        
        return null;
    }

    public void deleteLocation(int locationId) {
        String sql = "DELETE FROM locations WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting location: " + e.getMessage(), e);
        }
    }

    public void updateLocation(Location location) {
        String sql = "UPDATE locations SET city = ?, country = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, location.getCity());
            stmt.setString(2, location.getCountry());
            stmt.setInt(3, location.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating location: " + e.getMessage(), e);
        }
    }
    
    public List<Location> findAll() {
        return getAllLocations();
    }
} 