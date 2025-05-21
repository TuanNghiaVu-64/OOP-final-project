// Admin Components

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
    ]
};

// Admin Dashboard
export function dashboard() {
    return `
        <div class="dashboard">
            <div class="dashboard-header">
                <h2 class="dashboard-title">Admin Dashboard</h2>
                <div>
                    <a href="#" data-route="/admin/flights/new" class="btn btn-primary">
                        <i class="fas fa-plus mr-1"></i> Add New Flight
                    </a>
                </div>
            </div>
            
            <div class="dashboard-cards">
                <div class="card dashboard-card">
                    <i class="fas fa-plane"></i>
                    <h3>${mockData.flights.length} Flights</h3>
                    <a href="#" data-route="/admin/flights" class="btn btn-outline mt-2">Manage Flights</a>
                </div>
                
                <div class="card dashboard-card">
                    <i class="fas fa-plane-departure"></i>
                    <h3>${mockData.aircraft.length} Aircraft</h3>
                    <a href="#" data-route="/admin/aircraft" class="btn btn-outline mt-2">Manage Aircraft</a>
                </div>
                
                <div class="card dashboard-card">
                    <i class="fas fa-chair"></i>
                    <h3>${mockData.seatTypes.length} Seat Types</h3>
                    <a href="#" data-route="/admin/seat-types" class="btn btn-outline mt-2">Manage Seat Types</a>
                </div>
            </div>
            
            <div class="card">
                <h3>Recent Flights</h3>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Flight #</th>
                                <th>Origin</th>
                                <th>Destination</th>
                                <th>Departure</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${mockData.flights.slice(0, 3).map(flight => `
                                <tr>
                                    <td>${flight.flightNumber}</td>
                                    <td>${flight.origin}</td>
                                    <td>${flight.destination}</td>
                                    <td>${flight.departureDate} ${flight.departureTime}</td>
                                    <td><span class="badge badge-primary">${flight.status}</span></td>
                                    <td>
                                        <div class="table-actions">
                                            <a href="#" data-route="/admin/flights/edit/${flight.id}" class="btn btn-outline p-1">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <a href="#" data-route="/admin/seat-configuration/${flight.id}" class="btn btn-outline p-1">
                                                <i class="fas fa-chair"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;
}

// Flights Management
export function flights() {
    return `
        <div class="card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Flights Management</h2>
                <a href="#" data-route="/admin/flights/new" class="btn btn-primary">
                    <i class="fas fa-plus mr-1"></i> Add New Flight
                </a>
            </div>
            
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Flight #</th>
                            <th>Aircraft</th>
                            <th>Origin</th>
                            <th>Destination</th>
                            <th>Departure</th>
                            <th>Arrival</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mockData.flights.map(flight => `
                            <tr>
                                <td>${flight.flightNumber}</td>
                                <td>${flight.aircraft}</td>
                                <td>${flight.origin}</td>
                                <td>${flight.destination}</td>
                                <td>${flight.departureDate}<br>${flight.departureTime}</td>
                                <td>${flight.arrivalDate}<br>${flight.arrivalTime}</td>
                                <td><span class="badge badge-primary">${flight.status}</span></td>
                                <td>
                                    <div class="table-actions">
                                        <a href="#" data-route="/admin/flights/edit/${flight.id}" class="btn btn-outline p-1" title="Edit Flight">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <a href="#" data-route="/admin/seat-configuration/${flight.id}" class="btn btn-outline p-1" title="Configure Seats">
                                            <i class="fas fa-chair"></i>
                                        </a>
                                        <button class="btn btn-danger p-1" title="Delete Flight">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// Flight Form (Add/Edit)
export function flightForm(params) {
    // Get flight data if editing
    const flightId = params?.id;
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
    const title = flight ? 'Edit Flight' : 'Add New Flight';
    
    return `
        <div class="card">
            <h2>${title}</h2>
            
            <form id="flight-form" class="mt-4">
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="flightNumber" class="form-label">Flight Number</label>
                            <input type="text" id="flightNumber" class="form-control" value="${flight?.flightNumber || ''}" required>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="aircraft" class="form-label">Aircraft</label>
                            <select id="aircraft" class="form-control" required>
                                <option value="">Select Aircraft</option>
                                ${mockData.aircraft.map(aircraft => `
                                    <option value="${aircraft.id}" ${flight?.aircraft === aircraft.name ? 'selected' : ''}>
                                        ${aircraft.name} (${aircraft.seats} seats)
                                    </option>
                                `).join('')}
                            </select>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="origin" class="form-label">Origin</label>
                            <input type="text" id="origin" class="form-control" value="${flight?.origin || ''}" required>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="destination" class="form-label">Destination</label>
                            <input type="text" id="destination" class="form-control" value="${flight?.destination || ''}" required>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="departureDate" class="form-label">Departure Date</label>
                            <input type="date" id="departureDate" class="form-control" value="${flight?.departureDate || ''}" required>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="departureTime" class="form-label">Departure Time</label>
                            <input type="time" id="departureTime" class="form-control" value="${flight?.departureTime || ''}" required>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col">
                        <div class="form-group">
                            <label for="arrivalDate" class="form-label">Arrival Date</label>
                            <input type="date" id="arrivalDate" class="form-control" value="${flight?.arrivalDate || ''}" required>
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-group">
                            <label for="arrivalTime" class="form-label">Arrival Time</label>
                            <input type="time" id="arrivalTime" class="form-control" value="${flight?.arrivalTime || ''}" required>
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="status" class="form-label">Status</label>
                    <select id="status" class="form-control" required>
                        <option value="Scheduled" ${flight?.status === 'Scheduled' ? 'selected' : ''}>Scheduled</option>
                        <option value="Delayed" ${flight?.status === 'Delayed' ? 'selected' : ''}>Delayed</option>
                        <option value="Cancelled" ${flight?.status === 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </div>
                
                <div class="d-flex justify-content-between mt-4">
                    <a href="#" data-route="/admin/flights" class="btn btn-outline">Cancel</a>
                    <button type="submit" class="btn btn-primary">Save Flight</button>
                </div>
            </form>
        </div>
    `;
}

// Seat Configuration
export function seatConfiguration(params) {
    const flightId = params?.id;
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
    
    if (!flight) {
        return `
            <div class="card">
                <h2>Flight Not Found</h2>
                <p>The requested flight could not be found.</p>
                <a href="#" data-route="/admin/flights" class="btn btn-primary mt-3">Back to Flights</a>
            </div>
        `;
    }
    
    // Get aircraft for this flight
    const aircraft = mockData.aircraft.find(a => a.name === flight.aircraft);
    const seatCount = aircraft?.seats || 100;
    const rows = Math.ceil(seatCount / 6);
    
    // Generate seat map
    let seatMap = '';
    for (let row = 1; row <= rows; row++) {
        seatMap += '<div class="row">';
        for (let col = 0; col < 6; col++) {
            const seatNum = (row - 1) * 6 + col + 1;
            if (seatNum <= seatCount) {
                const colLabel = String.fromCharCode(65 + col); // A, B, C, D, E, F
                seatMap += `
                    <div class="seat" data-seat="${row}${colLabel}" data-type="economy">
                        ${row}${colLabel}
                    </div>
                `;
                
                // Add aisle after 3rd seat (between C and D)
                if (col === 2) {
                    seatMap += '<div class="seat aisle"></div>';
                }
            }
        }
        seatMap += '</div>';
    }
    
    return `
        <div class="card">
            <h2>Seat Configuration</h2>
            <h4>Flight: ${flight.flightNumber} - ${flight.origin} to ${flight.destination}</h4>
            <h5>Aircraft: ${flight.aircraft} (${seatCount} seats)</h5>
            
            <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
                <div class="form-group mb-0">
                    <label for="seat-type" class="form-label mb-0 mr-2">Selected Seat Type:</label>
                    <select id="seat-type" class="form-control">
                        ${mockData.seatTypes.map(type => `
                            <option value="${type.name}">${type.label} ($${type.basePrice})</option>
                        `).join('')}
                    </select>
                </div>
                
                <div>
                    <button id="save-config" class="btn btn-primary">
                        Save Configuration
                    </button>
                </div>
            </div>
            
            <div class="card bg-gray-100 p-3 mb-4">
                <div class="d-flex justify-content-between mb-2">
                    <div class="d-flex align-items-center">
                        <div class="seat seat-first-class mr-2" style="width: 30px; height: 30px;"></div>
                        <span>First Class</span>
                    </div>
                    <div class="d-flex align-items-center">
                        <div class="seat seat-business mr-2" style="width: 30px; height: 30px;"></div>
                        <span>Business</span>
                    </div>
                    <div class="d-flex align-items-center">
                        <div class="seat seat-economy mr-2" style="width: 30px; height: 30px;"></div>
                        <span>Economy</span>
                    </div>
                </div>
                <p class="mb-0 text-gray"><i class="fas fa-info-circle mr-1"></i> Click on a seat to change its type</p>
            </div>
            
            <div class="seat-map">
                ${seatMap}
            </div>
        </div>
    `;
}

// Aircraft Management
export function aircraft() {
    return `
        <div class="card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Aircraft Management</h2>
                <button class="btn btn-primary">
                    <i class="fas fa-plus mr-1"></i> Add New Aircraft
                </button>
            </div>
            
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Aircraft Name</th>
                            <th>Total Seats</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mockData.aircraft.map(aircraft => `
                            <tr>
                                <td>${aircraft.id}</td>
                                <td>${aircraft.name}</td>
                                <td>${aircraft.seats}</td>
                                <td>
                                    <div class="table-actions">
                                        <button class="btn btn-outline p-1" title="Edit Aircraft">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-danger p-1" title="Delete Aircraft">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// Seat Types Management
export function seatTypes() {
    return `
        <div class="card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Seat Types Management</h2>
                <button class="btn btn-primary">
                    <i class="fas fa-plus mr-1"></i> Add New Seat Type
                </button>
            </div>
            
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Type</th>
                            <th>Display Name</th>
                            <th>Base Price</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mockData.seatTypes.map(type => `
                            <tr>
                                <td>${type.id}</td>
                                <td>${type.name}</td>
                                <td>${type.label}</td>
                                <td>$${type.basePrice}</td>
                                <td>
                                    <div class="table-actions">
                                        <button class="btn btn-outline p-1" title="Edit Seat Type">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-danger p-1" title="Delete Seat Type">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}