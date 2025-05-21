// Customer Components

// Mock data for the application
const mockData = {
    flights: [
        { id: 1, flightNumber: 'SK101', aircraft: 'Boeing 737', origin: 'New York (JFK)', destination: 'London (LHR)', departureDate: '2023-08-15', departureTime: '09:00', arrivalDate: '2023-08-15', arrivalTime: '21:30', status: 'Scheduled', price: 450 },
        { id: 2, flightNumber: 'SK202', aircraft: 'Airbus A320', origin: 'London (LHR)', destination: 'Paris (CDG)', departureDate: '2023-08-16', departureTime: '11:15', arrivalDate: '2023-08-16', arrivalTime: '13:30', status: 'Scheduled', price: 180 },
        { id: 3, flightNumber: 'SK303', aircraft: 'Boeing 787', origin: 'Tokyo (HND)', destination: 'Los Angeles (LAX)', departureDate: '2023-08-17', departureTime: '23:45', arrivalDate: '2023-08-18', arrivalTime: '19:30', status: 'Scheduled', price: 950 }
    ],
    
    bookings: [
        { id: 1, flightId: 1, flightNumber: 'SK101', origin: 'New York (JFK)', destination: 'London (LHR)', departureDate: '2023-08-15', departureTime: '09:00', arrivalDate: '2023-08-15', arrivalTime: '21:30', seatNumber: '15A', seatType: 'business', price: 650, bookingDate: '2023-07-29', status: 'Confirmed' },
        { id: 2, flightId: 3, flightNumber: 'SK303', origin: 'Tokyo (HND)', destination: 'Los Angeles (LAX)', departureDate: '2023-08-17', departureTime: '23:45', arrivalDate: '2023-08-18', arrivalTime: '19:30', seatNumber: '22C', seatType: 'economy', price: 1200, bookingDate: '2023-07-30', status: 'Confirmed' }
    ],
    
    seatTypes: [
        { id: 1, name: 'first-class', label: 'First Class', basePrice: 1000 },
        { id: 2, name: 'business', label: 'Business', basePrice: 500 },
        { id: 3, name: 'economy', label: 'Economy', basePrice: 200 }
    ],
    
    airports: [
        'New York (JFK)', 'London (LHR)', 'Paris (CDG)', 'Tokyo (HND)', 'Los Angeles (LAX)', 
        'San Francisco (SFO)', 'Beijing (PEK)', 'Sydney (SYD)', 'Dubai (DXB)', 'Amsterdam (AMS)'
    ]
};

// Customer Dashboard
export function dashboard() {
    const upcomingBookings = mockData.bookings.slice(0, 2);
    
    return `
        <div class="dashboard">
            <div class="dashboard-header">
                <h2 class="dashboard-title">Customer Dashboard</h2>
                <div>
                    <a href="#" data-route="/customer/search" class="btn btn-primary">
                        <i class="fas fa-search mr-1"></i> Find Flights
                    </a>
                </div>
            </div>
            
            <div class="dashboard-cards">
                <div class="card dashboard-card">
                    <i class="fas fa-ticket-alt"></i>
                    <h3>${mockData.bookings.length} Bookings</h3>
                    <a href="#" data-route="/customer/tickets" class="btn btn-outline mt-2">View My Tickets</a>
                </div>
                
                <div class="card dashboard-card">
                    <i class="fas fa-plane-departure"></i>
                    <h3>Upcoming Flights</h3>
                    <p>${upcomingBookings.length} flights scheduled</p>
                </div>
                
                <div class="card dashboard-card">
                    <i class="fas fa-gift"></i>
                    <h3>Loyalty Points</h3>
                    <p>1,250 points available</p>
                </div>
            </div>
            
            <div class="card">
                <h3>Upcoming Flights</h3>
                
                ${upcomingBookings.length > 0 ? `
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Flight #</th>
                                    <th>Route</th>
                                    <th>Date</th>
                                    <th>Seat</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${upcomingBookings.map(booking => `
                                    <tr>
                                        <td>${booking.flightNumber}</td>
                                        <td>${booking.origin} to ${booking.destination}</td>
                                        <td>${booking.departureDate}<br>${booking.departureTime}</td>
                                        <td>${booking.seatNumber} (${booking.seatType})</td>
                                        <td><span class="badge badge-primary">${booking.status}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                ` : `
                    <div class="text-center p-4">
                        <p>You don't have any upcoming flights.</p>
                        <a href="#" data-route="/customer/search" class="btn btn-primary">Find Flights</a>
                    </div>
                `}
            </div>
        </div>
    `;
}

// Flight Search
export function search() {
    // Generate options for airports
    const airportOptions = mockData.airports.map(airport => `<option value="${airport}">${airport}</option>`).join('');
    
    // Tạo lưới ghế mẫu cho việc chọn vị trí ngồi
    const seatMapPreview = `
        <div class="form-group mt-4">
            <label class="form-label">Chọn vị trí ghế</label>
            <div class="seat-preview-container">
                <div class="seat-map-container">
                    <div class="column-labels">
                        <div class="column-label">A</div>
                        <div class="column-label">B</div>
                        <div class="column-label">C</div>
                        <div class="column-label aisle"></div>
                        <div class="column-label">D</div>
                        <div class="column-label">E</div>
                        <div class="column-label">F</div>
                    </div>
                    
                    <div class="seat-rows">
                        ${[1, 2, 3].map(row => `
                            <div class="seat-row">
                                <div class="row-label">${row}</div>
                                <div class="seat-grid">
                                    ${['A', 'B', 'C'].map(col => `
                                        <div class="seat seat-${row === 1 ? 'first-class' : row === 2 ? 'business' : 'economy'} ${(row === 1 && col === 'A') || (row === 2 && col === 'C') ? 'seat-occupied' : ''}" 
                                             data-seat="${row}${col}" 
                                             data-type="${row === 1 ? 'first-class' : row === 2 ? 'business' : 'economy'}">
                                            ${row}${col}
                                        </div>
                                    `).join('')}
                                    
                                    <div class="seat aisle"></div>
                                    
                                    ${['D', 'E', 'F'].map(col => `
                                        <div class="seat seat-${row === 1 ? 'first-class' : row === 2 ? 'business' : 'economy'} ${(row === 3 && col === 'E') ? 'seat-occupied' : ''}" 
                                             data-seat="${row}${col}" 
                                             data-type="${row === 1 ? 'first-class' : row === 2 ? 'business' : 'economy'}">
                                            ${row}${col}
                                        </div>
                                    `).join('')}
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
                
                <div class="seat-legend">
                    <div class="legend-item">
                        <div class="legend-color seat-first-class"></div>
                        <span>Hạng nhất</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color seat-business"></div>
                        <span>Thương gia</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color seat-economy"></div>
                        <span>Phổ thông</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color seat-occupied"></div>
                        <span>Đã đặt</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color seat-selected"></div>
                        <span>Đang chọn</span>
                    </div>
                </div>
                
                <div class="selected-seats-info mt-3" style="display: none;">
                    <p><strong>Ghế đã chọn:</strong> <span id="selected-seats-list">Chưa chọn ghế</span></p>
                </div>
            </div>
        </div>
    `;
    
    return `
        <div class="card">
            <h2>Search Flights</h2>
            
            <form id="search-form" class="mt-4">
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="origin" class="form-label">Origin</label>
                            <select id="origin" class="form-control" required>
                                <option value="">Select Origin</option>
                                ${airportOptions}
                            </select>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="destination" class="form-label">Destination</label>
                            <select id="destination" class="form-control" required>
                                <option value="">Select Destination</option>
                                ${airportOptions}
                            </select>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="departure-date" class="form-label">Departure Date</label>
                            <input type="date" id="departure-date" class="form-control" required>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="return-date" class="form-label">Return Date (Optional)</label>
                            <input type="date" id="return-date" class="form-control">
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="passengers" class="form-label">Passengers</label>
                            <select id="passengers" class="form-control" required>
                                <option value="1">1 Passenger</option>
                                <option value="2">2 Passengers</option>
                                <option value="3">3 Passengers</option>
                                <option value="4">4 Passengers</option>
                                <option value="5">5 Passengers</option>
                            </select>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="class" class="form-label">Class Preference</label>
                            <select id="class" class="form-control">
                                <option value="any">Any Class</option>
                                <option value="economy">Economy</option>
                                <option value="business">Business</option>
                                <option value="first-class">First Class</option>
                            </select>
                        </div>
                    </div>
                </div>

                ${seatMapPreview}
                
                <div class="d-flex justify-content-end mt-4">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search mr-1"></i> Search Flights
                    </button>
                </div>
            </form>
        </div>
    `;
}

// Search Results
export function searchResults() {
    // For demo purposes, show all flights
    const flights = mockData.flights;
    
    return `
        <div class="card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Flight Search Results</h2>
                <a href="#" data-route="/customer/search" class="btn btn-outline">
                    <i class="fas fa-search mr-1"></i> New Search
                </a>
            </div>
            
            <div class="card bg-gray-100 p-3 mb-4">
                <div class="row">
                    <div class="col">
                        <p class="mb-0"><strong>Origin:</strong> New York (JFK)</p>
                    </div>
                    <div class="col">
                        <p class="mb-0"><strong>Destination:</strong> London (LHR)</p>
                    </div>
                    <div class="col">
                        <p class="mb-0"><strong>Date:</strong> Aug 15, 2023</p>
                    </div>
                    <div class="col">
                        <p class="mb-0"><strong>Passengers:</strong> 1</p>
                    </div>
                </div>
            </div>
            
            <div class="mb-3">
                <p class="text-gray">Showing ${flights.length} flights</p>
            </div>
            
            ${flights.map(flight => `
                <div class="card mb-3">
                    <div class="row">
                        <div class="col-md-8">
                            <div class="d-flex align-items-center mb-2">
                                <h4 class="mb-0">${flight.flightNumber}</h4>
                                <span class="badge badge-primary ml-2">${flight.status}</span>
                            </div>
                            
                            <div class="d-flex justify-content-between mb-3">
                                <div>
                                    <p class="mb-0"><strong>${flight.departureTime}</strong></p>
                                    <p class="text-gray">${flight.origin}</p>
                                </div>
                                
                                <div class="d-flex flex-column align-items-center">
                                    <div class="text-gray">Direct Flight</div>
                                    <div class="d-flex align-items-center">
                                        <div style="height: 2px; width: 50px; background-color: var(--gray-300);"></div>
                                        <i class="fas fa-plane ml-1 mr-1 text-primary"></i>
                                        <div style="height: 2px; width: 50px; background-color: var(--gray-300);"></div>
                                    </div>
                                    <div class="text-gray">2h 15m</div>
                                </div>
                                
                                <div class="text-right">
                                    <p class="mb-0"><strong>${flight.arrivalTime}</strong></p>
                                    <p class="text-gray">${flight.destination}</p>
                                </div>
                            </div>
                            
                            <p class="mb-0"><strong>Aircraft:</strong> ${flight.aircraft}</p>
                        </div>
                        
                        <div class="col-md-4 d-flex flex-column justify-content-center align-items-end">
                            <div class="mb-2">
                                <h3 class="mb-0">$${flight.price}</h3>
                                <p class="text-gray">per passenger</p>
                            </div>
                            
                            <a href="#" data-route="/customer/select-seat/${flight.id}" class="btn btn-primary">Select Seats</a>
                        </div>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
}

// My Tickets
export function tickets() {
    const bookings = mockData.bookings;
    
    return `
        <div class="card">
            <h2>My Tickets</h2>
            
            ${bookings.length > 0 ? `
                <div class="table-container mt-4">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Booking ID</th>
                                <th>Flight</th>
                                <th>Route</th>
                                <th>Date</th>
                                <th>Seat</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${bookings.map(booking => `
                                <tr>
                                    <td>#${booking.id}</td>
                                    <td>${booking.flightNumber}</td>
                                    <td>${booking.origin} to ${booking.destination}</td>
                                    <td>${booking.departureDate}<br>${booking.departureTime}</td>
                                    <td>${booking.seatNumber} (${booking.seatType})</td>
                                    <td><span class="badge badge-primary">${booking.status}</span></td>
                                    <td>
                                        <div class="table-actions">
                                            <button class="btn btn-outline p-1" title="View Ticket">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                            <button class="btn btn-outline p-1" title="Print Ticket">
                                                <i class="fas fa-print"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            ` : `
                <div class="text-center p-4">
                    <p>You don't have any bookings yet.</p>
                    <a href="#" data-route="/customer/search" class="btn btn-primary">Find Flights</a>
                </div>
            `}
        </div>
    `;
}

// Add seat selection functionality for the search form
document.addEventListener('DOMContentLoaded', function() {
    initSearchFormSeatSelection();
});

// Initialize seat selection in the search form
function initSearchFormSeatSelection() {
    // Wait for the DOM to fully load
    setTimeout(() => {
        const seats = document.querySelectorAll('.seat-preview-container .seat:not(.aisle):not(.seat-occupied)');
        if (!seats.length) return;

        const selectedSeatsInfo = document.querySelector('.selected-seats-info');
        const selectedSeatsList = document.getElementById('selected-seats-list');
        
        const selectedSeats = new Set();
        
        seats.forEach(seat => {
            seat.addEventListener('click', function() {
                const seatNumber = this.dataset.seat;
                
                // Toggle seat selection
                if (this.classList.contains('seat-selected')) {
                    this.classList.remove('seat-selected');
                    selectedSeats.delete(seatNumber);
                } else {
                    this.classList.add('seat-selected');
                    selectedSeats.add(seatNumber);
                }
                
                // Update selected seats display
                if (selectedSeats.size > 0) {
                    selectedSeatsList.textContent = Array.from(selectedSeats).sort().join(', ');
                    selectedSeatsInfo.style.display = 'block';
                } else {
                    selectedSeatsList.textContent = 'Chưa chọn ghế';
                    selectedSeatsInfo.style.display = 'none';
                }
                
                // Store selected seats in session storage
                sessionStorage.setItem('searchPreselectedSeats', JSON.stringify(Array.from(selectedSeats)));
            });
        });
        
        // Check for previously selected seats
        const storedSeats = sessionStorage.getItem('searchPreselectedSeats');
        if (storedSeats) {
            try {
                const seatArray = JSON.parse(storedSeats);
                if (seatArray.length > 0) {
                    seatArray.forEach(seatNum => {
                        const seat = document.querySelector(`.seat[data-seat="${seatNum}"]`);
                        if (seat && !seat.classList.contains('seat-occupied')) {
                            seat.click();
                        }
                    });
                }
            } catch (e) {
                console.error('Error restoring seat selection:', e);
            }
        }
    }, 100);
} 