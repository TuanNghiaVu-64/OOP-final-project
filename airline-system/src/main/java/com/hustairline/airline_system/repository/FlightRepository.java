package com.hustairline.airline_system.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hustairline.airline_system.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hustairline.airline_system.model.Flight;

@Repository
public class FlightRepository extends AbstractJdbcRepository {

    @Autowired
    public FlightRepository(DatabaseConfig dbConfig) {
        super(dbConfig);
    }

    public Flight addFlight(Flight flight) {
        String sql = "INSERT INTO flights (plane_id, origin_id, destination_id, departure_time, arrival_time, approved) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, flight.getPlaneId());
            stmt.setInt(2, flight.getOriginId());
            stmt.setInt(3, flight.getDestinationId());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setBoolean(6, false); // Always start as not approved
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        flight.setId(rs.getInt(1));
                        flight.setApproved(false);
                        return flight;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding flight: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Flight> getAllFlights() {
        String sql = """
            SELECT f.*, p.model as plane_model, 
                   o.city as origin_city, o.country as origin_country,
                   d.city as destination_city, d.country as destination_country
            FROM flights f
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            ORDER BY f.departure_time
            """;
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Flight flight = new Flight();
                    flight.setId(rs.getInt("id"));
                    flight.setPlaneId(rs.getInt("plane_id"));
                    flight.setOriginId(rs.getInt("origin_id"));
                    flight.setDestinationId(rs.getInt("destination_id"));
                    flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                    flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                    flight.setApproved(rs.getBoolean("approved"));
                    
                    // Set display fields
                    flight.setPlaneModel(rs.getString("plane_model"));
                    flight.setOriginCity(rs.getString("origin_city"));
                    flight.setOriginCountry(rs.getString("origin_country"));
                    flight.setDestinationCity(rs.getString("destination_city"));
                    flight.setDestinationCountry(rs.getString("destination_country"));
                    
                    flights.add(flight);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving flights: " + e.getMessage(), e);
        }
        
        return flights;
    }

    public Flight findById(int id) {
        String sql = """
            SELECT f.*, p.model as plane_model, 
                   o.city as origin_city, o.country as origin_country,
                   d.city as destination_city, d.country as destination_country
            FROM flights f
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            WHERE f.id = ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Flight flight = new Flight();
                    flight.setId(rs.getInt("id"));
                    flight.setPlaneId(rs.getInt("plane_id"));
                    flight.setOriginId(rs.getInt("origin_id"));
                    flight.setDestinationId(rs.getInt("destination_id"));
                    flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                    flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                    flight.setApproved(rs.getBoolean("approved"));
                    
                    // Set display fields
                    flight.setPlaneModel(rs.getString("plane_model"));
                    flight.setOriginCity(rs.getString("origin_city"));
                    flight.setOriginCountry(rs.getString("origin_country"));
                    flight.setDestinationCity(rs.getString("destination_city"));
                    flight.setDestinationCountry(rs.getString("destination_country"));
                    
                    return flight;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving flight: " + e.getMessage(), e);
        }
        
        return null;
    }

    public void deleteFlight(int flightId) {
        String sql = "DELETE FROM flights WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting flight: " + e.getMessage(), e);
        }
    }

    public void updateApprovalStatus(int flightId, boolean approved) {
        String sql = "UPDATE flights SET approved = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, approved);
            stmt.setInt(2, flightId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating flight approval status: " + e.getMessage(), e);
        }
    }

    public List<Flight> findPendingFlights() {
        String sql = """
            SELECT f.*, p.model as plane_model, 
                   o.city as origin_city, o.country as origin_country,
                   d.city as destination_city, d.country as destination_country
            FROM flights f
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            WHERE f.approved = false
            ORDER BY f.departure_time
            """;
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Flight flight = new Flight();
                    flight.setId(rs.getInt("id"));
                    flight.setPlaneId(rs.getInt("plane_id"));
                    flight.setOriginId(rs.getInt("origin_id"));
                    flight.setDestinationId(rs.getInt("destination_id"));
                    flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                    flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                    flight.setApproved(rs.getBoolean("approved"));
                    
                    // Set display fields
                    flight.setPlaneModel(rs.getString("plane_model"));
                    flight.setOriginCity(rs.getString("origin_city"));
                    flight.setOriginCountry(rs.getString("origin_country"));
                    flight.setDestinationCity(rs.getString("destination_city"));
                    flight.setDestinationCountry(rs.getString("destination_country"));
                    
                    flights.add(flight);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving pending flights: " + e.getMessage(), e);
        }
        
        return flights;
    }
    
    public List<Flight> searchFlights(Integer originId, Integer destinationId, String departureDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT f.*, p.model as plane_model, 
                   o.city as origin_city, o.country as origin_country,
                   d.city as destination_city, d.country as destination_country
            FROM flights f
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            WHERE f.approved = true
            """);
        
        List<Object> params = new ArrayList<>();
        
        if (originId != null) {
            sql.append(" AND f.origin_id = ?");
            params.add(originId);
        }
        
        if (destinationId != null) {
            sql.append(" AND f.destination_id = ?");
            params.add(destinationId);
        }
        
        if (departureDate != null && !departureDate.trim().isEmpty()) {
            sql.append(" AND DATE(f.departure_time) = ?");
            params.add(java.sql.Date.valueOf(departureDate));
        }
        
        sql.append(" ORDER BY f.departure_time");
        
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof java.sql.Date) {
                    stmt.setDate(i + 1, (java.sql.Date) param);
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Flight flight = new Flight();
                    flight.setId(rs.getInt("id"));
                    flight.setPlaneId(rs.getInt("plane_id"));
                    flight.setOriginId(rs.getInt("origin_id"));
                    flight.setDestinationId(rs.getInt("destination_id"));
                    flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                    flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                    flight.setApproved(rs.getBoolean("approved"));
                    
                    // Set display fields
                    flight.setPlaneModel(rs.getString("plane_model"));
                    flight.setOriginCity(rs.getString("origin_city"));
                    flight.setOriginCountry(rs.getString("origin_country"));
                    flight.setDestinationCity(rs.getString("destination_city"));
                    flight.setDestinationCountry(rs.getString("destination_country"));
                    
                    flights.add(flight);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error searching flights: " + e.getMessage(), e);
        }
        
        return flights;
    }
} 