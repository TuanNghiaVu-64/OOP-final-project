package com.hustairline.airline_system.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hustairline.airline_system.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hustairline.airline_system.model.FlightSeatType;

@Repository
public class FlightSeatTypeRepository extends AbstractJdbcRepository {

    @Autowired
    public FlightSeatTypeRepository(DatabaseConfig dbConfig) {
        super(dbConfig);
    }

    public FlightSeatType addFlightSeatType(FlightSeatType flightSeatType) {
        String sql = "INSERT INTO flight_seat_types (flight_id, seat_type_id, price, approved) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, flightSeatType.getFlightId());
            stmt.setInt(2, flightSeatType.getSeatTypeId());
            stmt.setBigDecimal(3, flightSeatType.getPrice());
            stmt.setBoolean(4, false); // Always start as not approved
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        flightSeatType.setId(rs.getInt(1));
                        flightSeatType.setApproved(false);
                        return flightSeatType;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding flight seat type: " + e.getMessage(), e);
        }
        return null;
    }

    public List<FlightSeatType> getFlightSeatTypesByFlightId(int flightId) {
        String sql = """
            SELECT fst.*, st.name as seat_type_name
            FROM flight_seat_types fst
            JOIN seat_types st ON fst.seat_type_id = st.id
            WHERE fst.flight_id = ?
            ORDER BY st.name
            """;
        List<FlightSeatType> flightSeatTypes = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FlightSeatType flightSeatType = new FlightSeatType();
                    flightSeatType.setId(rs.getInt("id"));
                    flightSeatType.setFlightId(rs.getInt("flight_id"));
                    flightSeatType.setSeatTypeId(rs.getInt("seat_type_id"));
                    flightSeatType.setPrice(rs.getBigDecimal("price"));
                    flightSeatType.setApproved(rs.getBoolean("approved"));
                    flightSeatType.setSeatTypeName(rs.getString("seat_type_name"));
                    
                    flightSeatTypes.add(flightSeatType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving flight seat types: " + e.getMessage(), e);
        }
        
        return flightSeatTypes;
    }

    public List<FlightSeatType> getPendingFlightSeatTypes() {
        String sql = """
            SELECT fst.*, st.name as seat_type_name,
                   p.model as plane_model,
                   o.city as origin_city, o.country as origin_country,
                   d.city as destination_city, d.country as destination_country,
                   f.departure_time
            FROM flight_seat_types fst
            JOIN seat_types st ON fst.seat_type_id = st.id
            JOIN flights f ON fst.flight_id = f.id
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            WHERE fst.approved = false
            ORDER BY f.departure_time, st.name
            """;
        List<FlightSeatType> flightSeatTypes = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FlightSeatType flightSeatType = new FlightSeatType();
                    flightSeatType.setId(rs.getInt("id"));
                    flightSeatType.setFlightId(rs.getInt("flight_id"));
                    flightSeatType.setSeatTypeId(rs.getInt("seat_type_id"));
                    flightSeatType.setPrice(rs.getBigDecimal("price"));
                    flightSeatType.setApproved(rs.getBoolean("approved"));
                    flightSeatType.setSeatTypeName(rs.getString("seat_type_name"));
                    
                    // Create flight info string
                    String flightInfo = rs.getString("plane_model") + " - " +
                                      rs.getString("origin_city") + ", " + rs.getString("origin_country") +
                                      " to " +
                                      rs.getString("destination_city") + ", " + rs.getString("destination_country");
                    flightSeatType.setFlightInfo(flightInfo);
                    flightSeatType.setDepartureTime(rs.getTimestamp("departure_time").toString());
                    
                    flightSeatTypes.add(flightSeatType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving pending flight seat types: " + e.getMessage(), e);
        }
        
        return flightSeatTypes;
    }

    public void updateApprovalStatus(int flightSeatTypeId, boolean approved) {
        String sql = "UPDATE flight_seat_types SET approved = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, approved);
            stmt.setInt(2, flightSeatTypeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating flight seat type approval status: " + e.getMessage(), e);
        }
    }

    public void deleteFlightSeatType(int flightSeatTypeId) {
        String sql = "DELETE FROM flight_seat_types WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightSeatTypeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting flight seat type: " + e.getMessage(), e);
        }
    }

    public List<FlightSeatType> getAllFlightSeatTypes() {
        String sql = """
            SELECT fst.*, st.name as seat_type_name, st.features as seat_type_features,
                   p.model as flight_plane_model,
                   CONCAT(o.city, ', ', o.country) as flight_origin,
                   CONCAT(d.city, ', ', d.country) as flight_destination,
                   f.departure_time as flight_departure_time
            FROM flight_seat_types fst
            JOIN seat_types st ON fst.seat_type_id = st.id
            JOIN flights f ON fst.flight_id = f.id
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            ORDER BY f.departure_time, st.name
            """;
        List<FlightSeatType> flightSeatTypes = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FlightSeatType flightSeatType = new FlightSeatType();
                    flightSeatType.setId(rs.getInt("id"));
                    flightSeatType.setFlightId(rs.getInt("flight_id"));
                    flightSeatType.setSeatTypeId(rs.getInt("seat_type_id"));
                    flightSeatType.setPrice(rs.getBigDecimal("price"));
                    flightSeatType.setApproved(rs.getBoolean("approved"));
                    flightSeatType.setSeatTypeName(rs.getString("seat_type_name"));
                    
                    // Set additional display fields
                    flightSeatType.setFlightPlaneModel(rs.getString("flight_plane_model"));
                    flightSeatType.setFlightOrigin(rs.getString("flight_origin"));
                    flightSeatType.setFlightDestination(rs.getString("flight_destination"));
                    flightSeatType.setFlightDepartureTime(rs.getTimestamp("flight_departure_time"));
                    flightSeatType.setSeatTypeFeatures(rs.getString("seat_type_features"));
                    
                    flightSeatTypes.add(flightSeatType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all flight seat types: " + e.getMessage(), e);
        }
        
        return flightSeatTypes;
    }

    public boolean existsByFlightAndSeatType(int flightId, int seatTypeId) {
        String sql = "SELECT COUNT(*) FROM flight_seat_types WHERE flight_id = ? AND seat_type_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            stmt.setInt(2, seatTypeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking flight seat type existence: " + e.getMessage(), e);
        }
        
        return false;
    }

    public void updatePrice(int flightSeatTypeId, BigDecimal price) {
        String sql = "UPDATE flight_seat_types SET price = ?, approved = false WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, price);
            stmt.setInt(2, flightSeatTypeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating flight seat type price: " + e.getMessage(), e);
        }
    }
} 