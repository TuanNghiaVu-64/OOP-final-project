package com.hustairline.airline_system.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hustairline.airline_system.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hustairline.airline_system.model.FlightSeatAssignment;

@Repository
public class FlightSeatAssignmentRepository extends AbstractJdbcRepository {

    @Autowired
    public FlightSeatAssignmentRepository(DatabaseConfig dbConfig) {
        super(dbConfig);
    }

    public void createFlightSeatAssignments(int flightId) {
        // First, check if assignments already exist for this flight
        if (flightSeatAssignmentsExist(flightId)) {
            return; // Already created
        }

        String sql = """
            INSERT INTO flight_seat_assignments (flight_id, seat_id, flight_seat_type_id, available)
            SELECT f.id, s.id, fst.id, true
            FROM flights f
            JOIN planes p ON f.plane_id = p.id
            JOIN seats s ON s.plane_id = p.id
            JOIN seat_types st ON s.seat_type_id = st.id
            JOIN flight_seat_types fst ON fst.flight_id = f.id AND fst.seat_type_id = st.id
            WHERE f.id = ? AND fst.approved = true AND s.seat_type_id IS NOT NULL
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating flight seat assignments: " + e.getMessage(), e);
        }
    }

    public boolean flightSeatAssignmentsExist(int flightId) {
        String sql = "SELECT COUNT(*) FROM flight_seat_assignments WHERE flight_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking flight seat assignments existence: " + e.getMessage(), e);
        }
        return false;
    }

    public List<FlightSeatAssignment> getFlightSeatAssignments(int flightId) {
        String sql = """
            SELECT fsa.*, 
                   s.seat_code as seat_number,
                   st.name as seat_type_name,
                   st.features as seat_type_features
            FROM flight_seat_assignments fsa
            JOIN seats s ON fsa.seat_id = s.id
            JOIN seat_types st ON s.seat_type_id = st.id
            WHERE fsa.flight_id = ?
            ORDER BY s.row, s.col
            """;
        List<FlightSeatAssignment> assignments = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FlightSeatAssignment assignment = new FlightSeatAssignment();
                    assignment.setId(rs.getInt("id"));
                    assignment.setFlightId(rs.getInt("flight_id"));
                    assignment.setSeatId(rs.getInt("seat_id"));
                    assignment.setFlightSeatTypeId(rs.getInt("flight_seat_type_id"));
                    assignment.setAvailable(rs.getBoolean("available"));
                    assignment.setSeatNumber(rs.getString("seat_number"));
                    assignment.setSeatTypeName(rs.getString("seat_type_name"));
                    assignment.setSeatTypeFeatures(rs.getString("seat_type_features"));
                    
                    assignments.add(assignment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving flight seat assignments: " + e.getMessage(), e);
        }
        
        return assignments;
    }

    public void updateSeatAvailability(int assignmentId, boolean available) {
        String sql = "UPDATE flight_seat_assignments SET available = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, available);
            stmt.setInt(2, assignmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating seat availability: " + e.getMessage(), e);
        }
    }

    public int getAvailableSeatsCount(int flightId, int seatTypeId) {
        String sql = """
            SELECT COUNT(*) 
            FROM flight_seat_assignments fsa
            JOIN seats s ON fsa.seat_id = s.id
            WHERE fsa.flight_id = ? AND s.seat_type_id = ? AND fsa.available = true
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            stmt.setInt(2, seatTypeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error counting available seats: " + e.getMessage(), e);
        }
        return 0;
    }

    public void deleteFlightSeatAssignments(int flightId) {
        String sql = "DELETE FROM flight_seat_assignments WHERE flight_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting flight seat assignments: " + e.getMessage(), e);
        }
    }
    
    public FlightSeatAssignment getFlightSeatAssignmentById(int assignmentId) {
        String sql = """
            SELECT fsa.*, 
                   s.seat_code as seat_number,
                   st.name as seat_type_name,
                   st.features as seat_type_features
            FROM flight_seat_assignments fsa
            JOIN seats s ON fsa.seat_id = s.id
            JOIN seat_types st ON s.seat_type_id = st.id
            WHERE fsa.id = ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, assignmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FlightSeatAssignment assignment = new FlightSeatAssignment();
                    assignment.setId(rs.getInt("id"));
                    assignment.setFlightId(rs.getInt("flight_id"));
                    assignment.setSeatId(rs.getInt("seat_id"));
                    assignment.setFlightSeatTypeId(rs.getInt("flight_seat_type_id"));
                    assignment.setAvailable(rs.getBoolean("available"));
                    assignment.setSeatNumber(rs.getString("seat_number"));
                    assignment.setSeatTypeName(rs.getString("seat_type_name"));
                    assignment.setSeatTypeFeatures(rs.getString("seat_type_features"));
                    
                    return assignment;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving flight seat assignment: " + e.getMessage(), e);
        }
        
        return null;
    }
} 