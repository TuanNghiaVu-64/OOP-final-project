package com.hustairline.airline_system.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Service to handle referential integrity checks
 * Prevents deletion or modification of entities that are currently in use
 */
@Service
public class ReferentialIntegrityService {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/HUSTAirline";
    private static final String USER = "postgres";
    private static final String PASS = "NgH1A@2005";

    /**
     * Check if a location can be deleted
     * @param locationId the location ID to check
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult canDeleteLocation(int locationId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Check if location is used as origin in flights
            String originSql = "SELECT COUNT(*) FROM flights WHERE origin_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(originSql)) {
                stmt.setInt(1, locationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " flight(s) use this location as origin");
                    }
                }
            }
            
            // Check if location is used as destination in flights
            String destSql = "SELECT COUNT(*) FROM flights WHERE destination_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(destSql)) {
                stmt.setInt(1, locationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " flight(s) use this location as destination");
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking location dependencies: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Check if a plane can be deleted
     * @param planeId the plane ID to check
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult canDeletePlane(int planeId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Check if plane is used in flights
            String flightSql = "SELECT COUNT(*) FROM flights WHERE plane_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(flightSql)) {
                stmt.setInt(1, planeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " flight(s) are assigned to this plane");
                    }
                }
            }
            
            // Check if plane has seat assignments
            String seatSql = "SELECT COUNT(*) FROM seats WHERE plane_id = ? AND seat_type_id IS NOT NULL";
            try (PreparedStatement stmt = conn.prepareStatement(seatSql)) {
                stmt.setInt(1, planeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " seat(s) have been configured for this plane");
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking plane dependencies: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Check if a seat type can be deleted
     * @param seatTypeId the seat type ID to check
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult canDeleteSeatType(int seatTypeId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Check if seat type is used in seats
            String seatSql = "SELECT COUNT(*) FROM seats WHERE seat_type_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(seatSql)) {
                stmt.setInt(1, seatTypeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " seat(s) are assigned this seat type");
                    }
                }
            }
            
            // Check if seat type is used in flight pricing
            String pricingSql = "SELECT COUNT(*) FROM flight_seat_types WHERE seat_type_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(pricingSql)) {
                stmt.setInt(1, seatTypeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " flight pricing configuration(s) use this seat type");
                    }
                }
            }
            
            // Check if seat type is used in flight seat assignments
            String assignmentSql = """
                SELECT COUNT(*) FROM flight_seat_assignments fsa 
                JOIN seats s ON fsa.seat_id = s.id 
                WHERE s.seat_type_id = ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(assignmentSql)) {
                stmt.setInt(1, seatTypeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " flight seat assignment(s) use this seat type");
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking seat type dependencies: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Check if a flight can be deleted
     * @param flightId the flight ID to check
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult canDeleteFlight(int flightId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Check if flight has approved pricing
            String pricingSql = "SELECT COUNT(*) FROM flight_seat_types WHERE flight_id = ? AND approved = true";
            try (PreparedStatement stmt = conn.prepareStatement(pricingSql)) {
                stmt.setInt(1, flightId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " approved pricing configuration(s) exist for this flight");
                    }
                }
            }
            
            // Check if flight has seat assignments
            String assignmentSql = "SELECT COUNT(*) FROM flight_seat_assignments WHERE flight_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(assignmentSql)) {
                stmt.setInt(1, flightId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " seat assignment(s) exist for this flight");
                    }
                }
            }
            
            // Check if flight has bookings (if bookings table exists)
            // This would be added when booking functionality is implemented
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking flight dependencies: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Check if plane seat assignments can be modified
     * @param planeId the plane ID to check
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult canModifyPlaneSeats(int planeId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Check if plane has flights with ANY pricing configurations (approved or not)
            // Once pricing is set, seat configuration must be locked to maintain consistency
            String flightsWithPricingSql = """
                SELECT COUNT(DISTINCT f.id) FROM flights f
                JOIN flight_seat_types fst ON f.id = fst.flight_id
                WHERE f.plane_id = ? AND f.approved = true
                """;
            try (PreparedStatement stmt = conn.prepareStatement(flightsWithPricingSql)) {
                stmt.setInt(1, planeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " approved flight(s) with pricing configurations use this plane");
                    }
                }
            }
            
            // Also check for pending flights with pricing - indicates serious configuration intent
            String pendingFlightsWithPricingSql = """
                SELECT COUNT(DISTINCT f.id) FROM flights f
                JOIN flight_seat_types fst ON f.id = fst.flight_id
                WHERE f.plane_id = ? AND f.approved = false
                """;
            try (PreparedStatement stmt = conn.prepareStatement(pendingFlightsWithPricingSql)) {
                stmt.setInt(1, planeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " pending flight(s) with pricing configurations use this plane");
                    }
                }
            }
            
            // Check if plane has active seat assignments
            String seatAssignmentsSql = """
                SELECT COUNT(*) FROM flight_seat_assignments fsa
                JOIN flights f ON fsa.flight_id = f.id
                WHERE f.plane_id = ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(seatAssignmentsSql)) {
                stmt.setInt(1, planeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " seat assignment(s) exist for flights using this plane");
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking plane seat modification dependencies: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Check if flight pricing can be deleted/modified
     * @param flightId the flight ID to check
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult canModifyFlightPricing(int flightId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Only check if flight has actual bookings (when booking system is implemented)
            // Allow managers to delete approved pricing if no bookings exist
            
            // Check if flight has bookings (when booking table exists)
            String bookingSql = "SELECT COUNT(*) FROM bookings WHERE flight_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(bookingSql)) {
                stmt.setInt(1, flightId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        dependencies.add(rs.getInt(1) + " booking(s) exist for this flight");
                    }
                }
            } catch (SQLException e) {
                // Bookings table may not exist yet - this is okay
                // System.out.println("Bookings table not found - this is expected if booking system is not implemented yet");
            }
            
            // Note: We removed the flight_seat_assignments check because managers should be able to
            // delete pricing configs if no actual bookings exist. Seat assignments can be recreated.
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking flight pricing modification dependencies: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Check if a flight has overlapping times with existing flights using the same plane
     * @param planeId the plane ID to check
     * @param departureTime the departure time of the new flight
     * @param arrivalTime the arrival time of the new flight
     * @param excludeFlightId optional flight ID to exclude from the check (for updates)
     * @return ReferenceCheckResult with details
     */
    public ReferenceCheckResult checkFlightOverlap(int planeId, LocalDateTime departureTime, 
                                                   LocalDateTime arrivalTime, Integer excludeFlightId) {
        List<String> dependencies = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String overlapSql = """
                SELECT f.id, 
                       f.departure_time, 
                       f.arrival_time,
                       o.city || ', ' || o.country as origin,
                       d.city || ', ' || d.country as destination
                FROM flights f
                JOIN locations o ON f.origin_id = o.id
                JOIN locations d ON f.destination_id = d.id
                WHERE f.plane_id = ? 
                AND f.approved = true
                AND (
                    (f.departure_time <= ? AND f.arrival_time > ?) OR
                    (f.departure_time < ? AND f.arrival_time >= ?) OR
                    (f.departure_time >= ? AND f.departure_time < ?)
                )
                """ + (excludeFlightId != null ? " AND f.id != ?" : "");
            
            try (PreparedStatement stmt = conn.prepareStatement(overlapSql)) {
                stmt.setInt(1, planeId);
                stmt.setTimestamp(2, Timestamp.valueOf(departureTime));
                stmt.setTimestamp(3, Timestamp.valueOf(departureTime));
                stmt.setTimestamp(4, Timestamp.valueOf(arrivalTime));
                stmt.setTimestamp(5, Timestamp.valueOf(arrivalTime));
                stmt.setTimestamp(6, Timestamp.valueOf(departureTime));
                stmt.setTimestamp(7, Timestamp.valueOf(arrivalTime));
                
                if (excludeFlightId != null) {
                    stmt.setInt(8, excludeFlightId);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String conflictInfo = String.format(
                            "Flight #%d (%s → %s) from %s to %s",
                            rs.getInt("id"),
                            rs.getString("origin"),
                            rs.getString("destination"),
                            rs.getTimestamp("departure_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM dd HH:mm")),
                            rs.getTimestamp("arrival_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM dd HH:mm"))
                        );
                        dependencies.add("Overlaps with existing " + conflictInfo);
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking flight overlap: " + e.getMessage(), e);
        }
        
        return new ReferenceCheckResult(dependencies.isEmpty(), dependencies);
    }

    /**
     * Result class for reference checks
     */
    public static class ReferenceCheckResult {
        private final boolean canProceed;
        private final List<String> dependencies;

        public ReferenceCheckResult(boolean canProceed, List<String> dependencies) {
            this.canProceed = canProceed;
            this.dependencies = dependencies;
        }

        public boolean canProceed() {
            return canProceed;
        }

        public List<String> getDependencies() {
            return dependencies;
        }

        public String getDependencyMessage() {
            if (dependencies.isEmpty()) {
                return "";
            }
            return "Cannot proceed because:\n• " + String.join("\n• ", dependencies);
        }
    }
} 