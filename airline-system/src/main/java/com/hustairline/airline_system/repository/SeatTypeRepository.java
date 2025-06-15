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
import com.hustairline.airline_system.model.SeatType;

@Repository
public class SeatTypeRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/HUSTAirline";
    private static final String USER = "postgres";
    private static final String PASS = "NgH1A@2005";

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM seat_types WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking for duplicate seat type", e);
        }
        return false;
    }

    public SeatType addSeatType(SeatType seatType) {
        if (existsByName(seatType.getName())) {
            throw new RuntimeException("A seat type with name '" + seatType.getName() + "' already exists");
        }

        String sql = "INSERT INTO seat_types (name, features, approved) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, seatType.getName());
            stmt.setString(2, seatType.getFeatures());
            stmt.setBoolean(3, false);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        seatType.setId(rs.getInt(1));
                        return seatType;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding seat type: " + e.getMessage(), e);
        }
        return null;
    }

    public List<SeatType> getAllSeatTypes() {
        String sql = "SELECT * FROM seat_types ORDER BY id";
        List<SeatType> seatTypes = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SeatType type = new SeatType();
                    type.setId(rs.getInt("id"));
                    type.setName(rs.getString("name"));
                    type.setFeatures(rs.getString("features"));
                    type.setApproved(rs.getBoolean("approved"));
                    
                    // Generate color if not set
                    if (type.getColor() == null) {
                        type.setColor(generateColor(type.getId()));
                    }
                    
                    seatTypes.add(type);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving seat types: " + e.getMessage(), e);
        }
        
        return seatTypes;
    }

    public List<SeatType> getApprovedSeatTypes() {
        String sql = "SELECT * FROM seat_types WHERE approved = true ORDER BY id";
        List<SeatType> seatTypes = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SeatType type = new SeatType();
                    type.setId(rs.getInt("id"));
                    type.setName(rs.getString("name"));
                    type.setFeatures(rs.getString("features"));
                    type.setApproved(rs.getBoolean("approved"));
                    type.setColor(generateColor(type.getId()));
                    seatTypes.add(type);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving approved seat types: " + e.getMessage(), e);
        }
        
        return seatTypes;
    }

    private String generateColor(int id) {
        // Use golden angle approximation for color generation
        double hue = (id * 137.508) % 360;
        return String.format("hsl(%.1f, 70%%, 70%%)", hue);
    }

    public void approveSeatType(int seatTypeId) {
        String sql = "UPDATE seat_types SET approved = true WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, seatTypeId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error approving seat type: " + e.getMessage(), e);
        }
    }

    public void rejectSeatType(int seatTypeId) {
        String sql = "DELETE FROM seat_types WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, seatTypeId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error rejecting seat type: " + e.getMessage(), e);
        }
    }

    public List<SeatType> getPendingSeatTypes() {
        String sql = "SELECT * FROM seat_types WHERE approved = false ORDER BY id";
        List<SeatType> seatTypes = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SeatType seatType = new SeatType();
                    seatType.setId(rs.getInt("id"));
                    seatType.setName(rs.getString("name"));
                    seatType.setFeatures(rs.getString("features"));
                    seatType.setApproved(rs.getBoolean("approved"));
                    seatTypes.add(seatType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving pending seat types: " + e.getMessage(), e);
        }
        
        return seatTypes;
    }

    public void deleteSeatType(int seatTypeId) {
        String sql = "DELETE FROM seat_types WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, seatTypeId);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new RuntimeException("Seat type not found with id: " + seatTypeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting seat type: " + e.getMessage(), e);
        }
    }
} 