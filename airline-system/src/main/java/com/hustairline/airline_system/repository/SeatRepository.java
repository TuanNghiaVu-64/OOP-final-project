package com.hustairline.airline_system.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.hustairline.airline_system.model.Seat;

@Repository
public class SeatRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/HUSTAirline";
    private static final String USER = "postgres";
    private static final String PASS = "NgH1A@2005";

    public List<Seat> getSeatsForPlane(int planeId) {
        String sql = "SELECT * FROM seats WHERE plane_id = ? ORDER BY row, col";
        List<Seat> seats = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setId(rs.getInt("id"));
                    seat.setPlaneId(rs.getInt("plane_id"));
                    seat.setSeatCode(rs.getString("seat_code"));
                    seat.setRow(rs.getInt("row"));
                    seat.setCol(rs.getInt("col"));
                    seat.setSeatTypeId(rs.getObject("seat_type_id") != null ? rs.getInt("seat_type_id") : null);
                    seats.add(seat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving seats: " + e.getMessage(), e);
        }
        
        return seats;
    }

    public void initializeSeatsForPlane(int planeId, String size) {
        String sql = "INSERT INTO seats (plane_id, seat_code, row, col) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if ("small".equalsIgnoreCase(size)) {
                // First three rows with 3 high-level seats each (rows 1-3)
                for (int row = 1; row <= 3; row++) {
                    for (int col = 1; col <= 3; col++) {
                        stmt.setInt(1, planeId);
                        stmt.setString(2, row + getColumnLetter(col));
                        stmt.setInt(3, row);
                        stmt.setInt(4, col);
                        stmt.addBatch();
                    }
                }

                // Fourth row with single high-level seat
                stmt.setInt(1, planeId);
                stmt.setString(2, "4A");
                stmt.setInt(3, 4);
                stmt.setInt(4, 1);
                stmt.addBatch();

                // Economy section: 40 seats (4 seats × 10 rows)
                for (int row = 5; row <= 14; row++) {
                    for (int col = 1; col <= 4; col++) {
                        stmt.setInt(1, planeId);
                        stmt.setString(2, row + getColumnLetter(col));
                        stmt.setInt(3, row);
                        stmt.setInt(4, col);
                        stmt.addBatch();
                    }
                }
            } else {
                // First Class section (4 seats per row, 3 rows = 12 seats)
                for (int row = 1; row <= 3; row++) {
                    for (int col = 1; col <= 4; col++) {
                        stmt.setInt(1, planeId);
                        stmt.setString(2, row + getColumnLetter(col));
                        stmt.setInt(3, row);
                        stmt.setInt(4, col);
                        stmt.addBatch();
                    }
                }
                
                // Comfort+ section (6 seats per row, 4 rows = 24 seats)
                for (int row = 4; row <= 7; row++) {
                    for (int col = 1; col <= 6; col++) {
                        stmt.setInt(1, planeId);
                        stmt.setString(2, row + getColumnLetter(col));
                        stmt.setInt(3, row);
                        stmt.setInt(4, col);
                        stmt.addBatch();
                    }
                }
                
                // Economy section (6 seats per row for 10 rows, plus 4 seats in last row = 64 seats)
                // Full rows first (rows 8-17)
                for (int row = 8; row <= 17; row++) {
                    for (int col = 1; col <= 6; col++) {
                        stmt.setInt(1, planeId);
                        stmt.setString(2, row + getColumnLetter(col));
                        stmt.setInt(3, row);
                        stmt.setInt(4, col);
                        stmt.addBatch();
                    }
                }
                
                // Last row with 4 seats (row 18)
                for (int col = 1; col <= 4; col++) {
                    stmt.setInt(1, planeId);
                    stmt.setString(2, "18" + getColumnLetter(col));
                    stmt.setInt(3, 18);
                    stmt.setInt(4, col);
                    stmt.addBatch();
                }
            }
            
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing seats: " + e.getMessage(), e);
        }
    }

    public void updateSeatTypes(int planeId, List<Seat> seats) {
        String sql = "UPDATE seats SET seat_type_id = ? WHERE plane_id = ? AND row = ? AND col = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Seat seat : seats) {
                stmt.setObject(1, seat.getSeatTypeId());
                stmt.setInt(2, planeId);
                stmt.setInt(3, seat.getRow());
                stmt.setInt(4, seat.getCol());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating seat types: " + e.getMessage(), e);
        }
    }

    private String getColumnLetter(int col) {
        return String.valueOf((char)('A' + col - 1));
    }

    public boolean areAllSeatsAssigned(int planeId) {
        String sql = "SELECT COUNT(*) as total_seats, COUNT(seat_type_id) as assigned_seats FROM seats WHERE plane_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalSeats = rs.getInt("total_seats");
                    int assignedSeats = rs.getInt("assigned_seats");
                    return totalSeats > 0 && totalSeats == assignedSeats;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking seat assignments: " + e.getMessage(), e);
        }
        return false;
    }

    public List<Integer> getUsedSeatTypeIds(int planeId) {
        String sql = "SELECT DISTINCT seat_type_id FROM seats WHERE plane_id = ? AND seat_type_id IS NOT NULL";
        List<Integer> seatTypeIds = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seatTypeIds.add(rs.getInt("seat_type_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving used seat types: " + e.getMessage(), e);
        }
        
        return seatTypeIds;
    }
} 