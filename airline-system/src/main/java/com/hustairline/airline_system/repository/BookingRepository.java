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
import com.hustairline.airline_system.model.Booking;

@Repository
public class BookingRepository extends AbstractJdbcRepository {

    @Autowired
    public BookingRepository(DatabaseConfig dbConfig) {
        super(dbConfig);
    }

    public Booking createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, flight_seat_assignment_id, status, booking_time) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getFlightSeatAssignmentId());
            stmt.setString(3, booking.getStatus());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setId(rs.getInt(1));
                        booking.setBookingTime(new Timestamp(System.currentTimeMillis()));
                        return booking;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating booking: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Booking> getBookingsByUserId(int userId) {
        String sql = """
            SELECT b.*, 
                   u.username,
                   s.seat_code as seat_number,
                   st.name as seat_type_name,
                   CONCAT(p.model, ' - ', o.city, ', ', o.country, ' to ', d.city, ', ', d.country) as flight_info,
                   f.departure_time,
                   f.arrival_time,
                   CONCAT(o.city, ', ', o.country) as origin,
                   CONCAT(d.city, ', ', d.country) as destination,
                   p.model as plane_model,
                   fst.price as total_amount
            FROM bookings b
            JOIN users u ON b.user_id = u.id
            JOIN flight_seat_assignments fsa ON b.flight_seat_assignment_id = fsa.id
            JOIN seats s ON fsa.seat_id = s.id
            JOIN seat_types st ON s.seat_type_id = st.id
            JOIN flights f ON fsa.flight_id = f.id
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            JOIN flight_seat_types fst ON fst.flight_id = f.id AND fst.seat_type_id = st.id
            WHERE b.user_id = ?
            ORDER BY f.departure_time DESC
            """;
        
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setFlightSeatAssignmentId(rs.getInt("flight_seat_assignment_id"));
                    booking.setTotalAmount(rs.getBigDecimal("total_amount"));
                    booking.setStatus(rs.getString("status"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setPaymentTime(rs.getTimestamp("payment_time"));
                    
                    // Set display fields
                    booking.setUsername(rs.getString("username"));
                    booking.setSeatNumber(rs.getString("seat_number"));
                    booking.setSeatTypeName(rs.getString("seat_type_name"));
                    booking.setFlightInfo(rs.getString("flight_info"));
                    booking.setDepartureTime(rs.getTimestamp("departure_time").toString());
                    booking.setArrivalTime(rs.getTimestamp("arrival_time").toString());
                    booking.setOrigin(rs.getString("origin"));
                    booking.setDestination(rs.getString("destination"));
                    booking.setPlaneModel(rs.getString("plane_model"));
                    
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving user bookings: " + e.getMessage(), e);
        }
        
        return bookings;
    }

    public void updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ?, payment_time = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            if ("PAID".equals(status)) {
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setTimestamp(2, null);
            }
            stmt.setInt(3, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating booking status: " + e.getMessage(), e);
        }
    }

    public Booking getBookingById(int bookingId) {
        String sql = """
            SELECT b.*, 
                   u.username,
                   s.seat_code as seat_number,
                   st.name as seat_type_name,
                   CONCAT(p.model, ' - ', o.city, ', ', o.country, ' to ', d.city, ', ', d.country) as flight_info,
                   f.departure_time,
                   f.arrival_time,
                   CONCAT(o.city, ', ', o.country) as origin,
                   CONCAT(d.city, ', ', d.country) as destination,
                   p.model as plane_model,
                   fst.price as total_amount
            FROM bookings b
            JOIN users u ON b.user_id = u.id
            JOIN flight_seat_assignments fsa ON b.flight_seat_assignment_id = fsa.id
            JOIN seats s ON fsa.seat_id = s.id
            JOIN seat_types st ON s.seat_type_id = st.id
            JOIN flights f ON fsa.flight_id = f.id
            JOIN planes p ON f.plane_id = p.id
            JOIN locations o ON f.origin_id = o.id
            JOIN locations d ON f.destination_id = d.id
            JOIN flight_seat_types fst ON fst.flight_id = f.id AND fst.seat_type_id = st.id
            WHERE b.id = ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setFlightSeatAssignmentId(rs.getInt("flight_seat_assignment_id"));
                    booking.setTotalAmount(rs.getBigDecimal("total_amount"));
                    booking.setStatus(rs.getString("status"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setPaymentTime(rs.getTimestamp("payment_time"));
                    
                    // Set display fields
                    booking.setUsername(rs.getString("username"));
                    booking.setSeatNumber(rs.getString("seat_number"));
                    booking.setSeatTypeName(rs.getString("seat_type_name"));
                    booking.setFlightInfo(rs.getString("flight_info"));
                    booking.setDepartureTime(rs.getTimestamp("departure_time").toString());
                    booking.setArrivalTime(rs.getTimestamp("arrival_time").toString());
                    booking.setOrigin(rs.getString("origin"));
                    booking.setDestination(rs.getString("destination"));
                    booking.setPlaneModel(rs.getString("plane_model"));
                    
                    return booking;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving booking: " + e.getMessage(), e);
        }
        
        return null;
    }

    public boolean hasBookingForSeat(int flightSeatAssignmentId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE flight_seat_assignment_id = ? AND status IN ('PENDING', 'PAID')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, flightSeatAssignmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking seat booking status: " + e.getMessage(), e);
        }
        
        return false;
    }

    public boolean cancelBooking(int bookingId, int userId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ? AND user_id = ? AND status IN ('PENDING', 'PAID')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error cancelling booking: " + e.getMessage(), e);
        }
    }

    public int getFlightSeatAssignmentIdByBookingId(int bookingId) {
        String sql = "SELECT flight_seat_assignment_id FROM bookings WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("flight_seat_assignment_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting flight seat assignment ID: " + e.getMessage(), e);
        }
        
        return -1;
    }
} 