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
import com.hustairline.airline_system.model.Plane;

@Repository
public class PlaneRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/HUSTAirline";
    private static final String USER = "postgres";
    private static final String PASS = "NgH1A@2005";

    public boolean existsByModel(String model) {
        String sql = "SELECT COUNT(*) FROM planes WHERE LOWER(model) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, model.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking for duplicate plane model", e);
        }
        return false;
    }

    public Plane addPlane(Plane plane) {
        if (existsByModel(plane.getModel())) {
            throw new RuntimeException("A plane with model '" + plane.getModel() + "' already exists");
        }

        String sql = "INSERT INTO planes (model, size, approved) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, plane.getModel());
            stmt.setString(2, plane.getSize().toLowerCase());
            stmt.setBoolean(3, false);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        plane.setId(rs.getInt(1));
                        return plane;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding plane: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Plane> getAllPlanes() {
        String sql = "SELECT * FROM planes ORDER BY id";
        List<Plane> planes = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Plane plane = new Plane();
                    plane.setId(rs.getInt("id"));
                    plane.setModel(rs.getString("model"));
                    plane.setSize(rs.getString("size"));
                    plane.setApproved(rs.getBoolean("approved"));
                    planes.add(plane);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving planes: " + e.getMessage(), e);
        }
        
        return planes;
    }

    public List<Plane> findApprovedPlanes() {
        String sql = """
            SELECT p.*, COUNT(s.id) as assigned_seats_count 
            FROM planes p 
            LEFT JOIN seats s ON p.id = s.plane_id AND s.seat_type_id IS NOT NULL 
            WHERE p.approved = true 
            GROUP BY p.id, p.model, p.size, p.approved 
            ORDER BY p.model
            """;
        List<Plane> planes = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Plane plane = new Plane();
                    plane.setId(rs.getInt("id"));
                    plane.setModel(rs.getString("model"));
                    plane.setSize(rs.getString("size"));
                    plane.setApproved(rs.getBoolean("approved"));
                    plane.setAssignedSeatsCount(rs.getInt("assigned_seats_count"));
                    planes.add(plane);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving approved planes: " + e.getMessage(), e);
        }
        
        return planes;
    }

    public Plane findById(int id) {
        String sql = "SELECT * FROM planes WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Plane plane = new Plane();
                    plane.setId(rs.getInt("id"));
                    plane.setModel(rs.getString("model"));
                    plane.setSize(rs.getString("size"));
                    plane.setApproved(rs.getBoolean("approved"));
                    return plane;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving plane: " + e.getMessage(), e);
        }
        
        return null;
    }

    public List<Plane> findPendingPlanes() {
        String sql = "SELECT * FROM planes WHERE approved = false ORDER BY id";
        List<Plane> planes = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Plane plane = new Plane();
                    plane.setId(rs.getInt("id"));
                    plane.setModel(rs.getString("model"));
                    plane.setSize(rs.getString("size"));
                    plane.setApproved(rs.getBoolean("approved"));
                    planes.add(plane);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving pending planes: " + e.getMessage(), e);
        }
        
        return planes;
    }

    public void updateApprovalStatus(int planeId, boolean approved) {
        String sql = "UPDATE planes SET approved = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, approved);
            stmt.setInt(2, planeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating plane approval status: " + e.getMessage(), e);
        }
    }

    public void deletePlane(int planeId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // First delete all seats associated with the plane
                String deleteSeatsSql = "DELETE FROM seats WHERE plane_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteSeatsSql)) {
                    stmt.setInt(1, planeId);
                    stmt.executeUpdate();
                }
                
                // Then delete the plane
                String deletePlaneSql = "DELETE FROM planes WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deletePlaneSql)) {
                    stmt.setInt(1, planeId);
                    stmt.executeUpdate();
                }
                
                // Commit the transaction
                conn.commit();
            } catch (SQLException e) {
                // Rollback in case of error
                conn.rollback();
                throw e;
            } finally {
                // Reset auto-commit to true
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting plane: " + e.getMessage(), e);
        }
    }
} 