// Components Module
// This module combines all component imports and exports them

// Import components from separate files
import * as admin from './components/admin.js';
import * as manager from './components/manager.js';
import * as customer from './components/customer.js';
import * as customerBooking from './components/customer-booking.js';

// Shared Components
export function login() {
    return `
        <div class="card login-container">
            <h2 class="login-title">Login to SkyBooker</h2>
            
            <div class="role-selector">
                <div class="role-option" data-role="admin">
                    <i class="fas fa-user-shield"></i>
                    <span>Admin</span>
                </div>
                <div class="role-option" data-role="manager">
                    <i class="fas fa-user-tie"></i>
                    <span>Manager</span>
                </div>
                <div class="role-option" data-role="customer">
                    <i class="fas fa-user"></i>
                    <span>Customer</span>
                </div>
            </div>
            
            <form id="login-form" class="login-form">
                <div class="form-group">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" id="username" class="form-control" placeholder="Enter your username">
                </div>
                
                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" id="password" class="form-control" placeholder="Enter your password">
                </div>
                
                <button type="submit" class="btn btn-primary">Login</button>
            </form>
            
            <div class="mt-3 text-center">
                <p class="text-gray">Demo credentials:</p>
                <p class="text-gray mb-1">Admin: admin / admin123</p>
                <p class="text-gray mb-1">Manager: manager / manager123</p>
                <p class="text-gray">Customer: customer / customer123</p>
            </div>
        </div>
    `;
}

// 404 Page
export function notFound() {
    return `
        <div class="card text-center mt-5">
            <h1>404</h1>
            <h2>Page Not Found</h2>
            <p>The page you are looking for does not exist.</p>
            <a href="#" data-route="/login" class="btn btn-primary mt-3">Go to Login</a>
        </div>
    `;
}

// Map route components using computed property names
const components = {
    // 404 page
    ['404']: notFound,

    // Admin components
    ['admin/dashboard']: admin.dashboard,
    ['admin/flights']: admin.flights,
    ['admin/flight-form']: admin.flightForm,
    ['admin/seat-configuration']: admin.seatConfiguration,
    ['admin/aircraft']: admin.aircraft,
    ['admin/seat-types']: admin.seatTypes,

    // Manager components
    ['manager/dashboard']: manager.dashboard,
    ['manager/approval-requests']: manager.approvalRequests,
    ['manager/seat-review']: manager.seatReview,

    // Customer components
    ['customer/dashboard']: customer.dashboard,
    ['customer/search']: customer.search,
    ['customer/search-results']: customer.searchResults,
    ['customer/tickets']: customer.tickets,

    // Customer booking components
    ['customer/select-seat']: customerBooking.selectSeat,
    ['customer/trip-overview']: customerBooking.tripOverview,
    ['customer/payment']: customerBooking.payment,
    ['customer/confirmation']: customerBooking.confirmation
};

// Export all components
export { components };

// Mock data for the application
const mockData = {
    flights: [
        { id: 1, flightNumber: 'SK101', aircraft: 'Boeing 737', origin: 'New York (JFK)', destination: 'London (LHR)', departureDate: '2023-08-15', departureTime: '09:00', arrivalDate: '2023-08-15', arrivalTime: '21:30', status: 'Scheduled' },
        { id: 2, flightNumber: 'SK202', aircraft: 'Airbus A320', origin: 'London (LHR)', destination: 'Paris (CDG)', departureDate: '2023-08-16', departureTime: '11:15', arrivalDate: '2023-08-16', arrivalTime: '13:30', status: 'Scheduled' },
        { id: 3, flightNumber: 'SK303', aircraft: 'Boeing 787', origin: 'Tokyo (HND)', destination: 'Los Angeles (LAX)', departureDate: '2023-08-17', departureTime: '23:45', arrivalDate: '2023-08-18', arrivalTime: '19:30', status: 'Scheduled' }
    ],
    
    aircraft: [
        { id: 1, name: 'Boeing 737', seats: 150 },
        { id: 2, name: 'Airbus A320', seats: 180 },
        { id: 3, name: 'Boeing 787', seats: 250 }
    ],
    
    seatTypes: [
        { id: 1, name: 'first-class', label: 'First Class', basePrice: 1000 },
        { id: 2, name: 'business', label: 'Business', basePrice: 500 },
        { id: 3, name: 'economy', label: 'Economy', basePrice: 200 }
    ],
    
    approvalRequests: [
        { id: 1, flightId: 1, flightNumber: 'SK101', submittedBy: 'Admin User', submittedDate: '2023-07-30', status: 'Pending' },
        { id: 2, flightId: 2, flightNumber: 'SK202', submittedBy: 'Admin User', submittedDate: '2023-07-31', status: 'Pending' }
    ],
    
    bookings: [
        { id: 1, flightId: 1, seatNumber: '15A', seatType: 'business', price: 650, bookingDate: '2023-07-29' },
        { id: 2, flightId: 3, seatNumber: '22C', seatType: 'economy', price: 1200, bookingDate: '2023-07-30' }
    ]
};