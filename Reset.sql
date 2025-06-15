--Remove public.
ALTER ROLE your_username SET search_path TO public;

-- Wipe data from table
DO $$ DECLARE
    r RECORD;
BEGIN
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP
        EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
END $$;

--Reset serial count
-- USERS
SELECT setval('users_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM users;

-- SEAT TYPES
SELECT setval('seat_types_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM seat_types;

-- PLANES
SELECT setval('planes_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM planes;

-- SEATS
SELECT setval('seats_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM seats;

-- LOCATIONS
SELECT setval('locations_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM locations;

-- FLIGHTS
SELECT setval('flights_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM flights;

-- FLIGHT_SEAT_TYPES
SELECT setval('flight_seat_types_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM flight_seat_types;

-- FLIGHT_SEAT_ASSIGNMENTS
SELECT setval('flight_seat_assignments_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM flight_seat_assignments;

-- BOOKINGS
SELECT setval('bookings_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM bookings;

--Users data
INSERT INTO users(username, password, role, approved) VALUES
('admin', 'admin123', 'ADMIN', true),
('manager', 'manager123', 'MANAGER', true),
('customer', 'customer123', 'CUSTOMER', true),
('hacker', 'hacker123', 'ADMIN', false);