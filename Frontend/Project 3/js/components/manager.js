// Manager Components

// Mock data for the application
const mockData = {
    approvalRequests: [
        { id: 1, flightId: 1, flightNumber: 'SK101', submittedBy: 'Admin User', submittedDate: '2023-07-30', status: 'Pending' },
        { id: 2, flightId: 2, flightNumber: 'SK202', submittedBy: 'Admin User', submittedDate: '2023-07-31', status: 'Pending' }
    ],
    
    flights: [
        { id: 1, flightNumber: 'SK101', aircraft: 'Boeing 737', origin: 'New York (JFK)', destination: 'London (LHR)', departureDate: '2023-08-15', departureTime: '09:00', arrivalDate: '2023-08-15', arrivalTime: '21:30', status: 'Scheduled' },
        { id: 2, flightNumber: 'SK202', aircraft: 'Airbus A320', origin: 'London (LHR)', destination: 'Paris (CDG)', departureDate: '2023-08-16', departureTime: '11:15', arrivalDate: '2023-08-16', arrivalTime: '13:30', status: 'Scheduled' }
    ],
    
    aircraft: [
        { id: 1, name: 'Boeing 737', seats: 150 },
        { id: 2, name: 'Airbus A320', seats: 180 }
    ],
    
    seatTypes: [
        { id: 1, name: 'first-class', label: 'First Class', basePrice: 1000 },
        { id: 2, name: 'business', label: 'Business', basePrice: 500 },
        { id: 3, name: 'economy', label: 'Economy', basePrice: 200 }
    ],
    
    // Mock seat configurations for review
    seatConfigurations: {
        1: {
            firstClass: 24,
            business: 36,
            economy: 90,
            totalRevenue: 78000,
            previousRevenue: 60000,
            revenueIncrease: '30%'
        },
        2: {
            firstClass: 12,
            business: 48,
            economy: 120,
            totalRevenue: 66000,
            previousRevenue: 54000,
            revenueIncrease: '22%'
        }
    }
};

// Manager Dashboard
export function dashboard() {
    const pendingRequests = mockData.approvalRequests.filter(req => req.status === 'Pending').length;
    
    return `
        <div class="dashboard">
            <div class="dashboard-header">
                <h2 class="dashboard-title">Manager Dashboard</h2>
                <div>
                    <a href="#" data-route="/manager/approval-requests" class="btn btn-primary">
                        Review Requests
                    </a>
                </div>
            </div>
            
            <div class="dashboard-cards">
                <div class="card dashboard-card">
                    <i class="fas fa-clipboard-check"></i>
                    <h3>${pendingRequests} Pending Requests</h3>
                    <a href="#" data-route="/manager/approval-requests" class="btn btn-outline mt-2">Review Requests</a>
                </div>
                
                <div class="card dashboard-card">
                    <i class="fas fa-check-circle"></i>
                    <h3>Approved Configurations</h3>
                    <p class="text-success">15 this month</p>
                </div>
                
                <div class="card dashboard-card">
                    <i class="fas fa-times-circle"></i>
                    <h3>Rejected Configurations</h3>
                    <p class="text-danger">3 this month</p>
                </div>
            </div>
            
            <div class="card">
                <h3>Recent Approval Requests</h3>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Flight #</th>
                                <th>Submitted By</th>
                                <th>Submitted Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${mockData.approvalRequests.map(request => `
                                <tr>
                                    <td>${request.flightNumber}</td>
                                    <td>${request.submittedBy}</td>
                                    <td>${request.submittedDate}</td>
                                    <td><span class="badge badge-${request.status === 'Pending' ? 'primary' : (request.status === 'Approved' ? 'success' : 'danger')}">${request.status}</span></td>
                                    <td>
                                        <div class="table-actions">
                                            <a href="#" data-route="/manager/seat-review/${request.flightId}" class="btn btn-outline p-1" title="Review Configuration">
                                                <i class="fas fa-eye"></i>
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

// Approval Requests List
export function approvalRequests() {
    return `
        <div class="card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Pending Approval Requests</h2>
            </div>
            
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Request ID</th>
                            <th>Flight #</th>
                            <th>Submitted By</th>
                            <th>Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mockData.approvalRequests.map(request => `
                            <tr>
                                <td>${request.id}</td>
                                <td>${request.flightNumber}</td>
                                <td>${request.submittedBy}</td>
                                <td>${request.submittedDate}</td>
                                <td><span class="badge badge-${request.status === 'Pending' ? 'primary' : (request.status === 'Approved' ? 'success' : 'danger')}">${request.status}</span></td>
                                <td>
                                    <div class="table-actions">
                                        <a href="#" data-route="/manager/seat-review/${request.flightId}" class="btn btn-outline p-1" title="Review Configuration">
                                            <i class="fas fa-eye"></i>
                                        </a>
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

// Seat Configuration Review
export function seatReview(params) {
    const flightId = params?.id;
    const request = mockData.approvalRequests.find(r => r.flightId.toString() === flightId);
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
    const seatConfig = flightId ? mockData.seatConfigurations[flightId] : null;
    
    if (!flight || !request || !seatConfig) {
        return `
            <div class="card">
                <h2>Request Not Found</h2>
                <p>The requested configuration could not be found.</p>
                <a href="#" data-route="/manager/approval-requests" class="btn btn-primary mt-3">Back to Approval Requests</a>
            </div>
        `;
    }
    
    // Get aircraft for this flight
    const aircraft = mockData.aircraft.find(a => a.name === flight.aircraft);
    const seatCount = aircraft?.seats || 100;
    const rows = Math.ceil(seatCount / 6);
    
    // Generate seat map with mock configuration
    let seatMap = '';
    for (let row = 1; row <= rows; row++) {
        seatMap += '<div class="row">';
        for (let col = 0; col < 6; col++) {
            const seatNum = (row - 1) * 6 + col + 1;
            if (seatNum <= seatCount) {
                const colLabel = String.fromCharCode(65 + col); // A, B, C, D, E, F
                
                // Distribute seat types (mock configuration)
                let seatType = 'economy';
                if (seatNum <= seatConfig.firstClass) {
                    seatType = 'first-class';
                } else if (seatNum <= (seatConfig.firstClass + seatConfig.business)) {
                    seatType = 'business';
                }
                
                seatMap += `
                    <div class="seat seat-${seatType}" data-seat="${row}${colLabel}">
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
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="mb-0">Seat Configuration Review</h2>
                <span class="badge badge-primary">Pending Approval</span>
            </div>
            
            <div class="card bg-gray-100 p-3 mb-4">
                <h4 class="mb-2">Flight Details</h4>
                <div class="row">
                    <div class="col">
                        <p class="mb-1"><strong>Flight Number:</strong> ${flight.flightNumber}</p>
                        <p class="mb-1"><strong>Aircraft:</strong> ${flight.aircraft}</p>
                        <p class="mb-1"><strong>Route:</strong> ${flight.origin} to ${flight.destination}</p>
                    </div>
                    <div class="col">
                        <p class="mb-1"><strong>Departure:</strong> ${flight.departureDate} ${flight.departureTime}</p>
                        <p class="mb-1"><strong>Arrival:</strong> ${flight.arrivalDate} ${flight.arrivalTime}</p>
                        <p class="mb-1"><strong>Submitted By:</strong> ${request.submittedBy}</p>
                    </div>
                </div>
            </div>
            
            <div class="row mb-4">
                <div class="col">
                    <div class="card bg-gray-100 p-3">
                        <h4 class="mb-2">Seat Distribution</h4>
                        <div class="d-flex mb-2">
                            <div class="seat seat-first-class mr-2" style="width: 30px; height: 30px;"></div>
                            <div class="d-flex justify-content-between w-100">
                                <span>First Class</span>
                                <span><strong>${seatConfig.firstClass} seats</strong></span>
                            </div>
                        </div>
                        <div class="d-flex mb-2">
                            <div class="seat seat-business mr-2" style="width: 30px; height: 30px;"></div>
                            <div class="d-flex justify-content-between w-100">
                                <span>Business</span>
                                <span><strong>${seatConfig.business} seats</strong></span>
                            </div>
                        </div>
                        <div class="d-flex mb-2">
                            <div class="seat seat-economy mr-2" style="width: 30px; height: 30px;"></div>
                            <div class="d-flex justify-content-between w-100">
                                <span>Economy</span>
                                <span><strong>${seatConfig.economy} seats</strong></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card bg-gray-100 p-3">
                        <h4 class="mb-2">Revenue Analysis</h4>
                        <p class="mb-1"><strong>Previous Revenue:</strong> $${seatConfig.previousRevenue.toLocaleString()}</p>
                        <p class="mb-1"><strong>New Projected Revenue:</strong> $${seatConfig.totalRevenue.toLocaleString()}</p>
                        <p class="mb-2"><strong>Revenue Increase:</strong> <span class="text-success">${seatConfig.revenueIncrease}</span></p>
                        <div class="progress">
                            <div class="progress-bar bg-success" role="progressbar" style="width: ${seatConfig.revenueIncrease}"></div>
                        </div>
                    </div>
                </div>
            </div>
            
            <h4>Seat Map Preview</h4>
            <div class="card p-3 mb-4">
                <div class="seat-map">
                    ${seatMap}
                </div>
            </div>
            
            <div class="d-flex justify-content-between">
                <button id="reject-config" class="btn btn-danger">
                    <i class="fas fa-times mr-1"></i> Reject Configuration
                </button>
                <button id="approve-config" class="btn btn-success">
                    <i class="fas fa-check mr-1"></i> Approve Configuration
                </button>
            </div>
        </div>
    `;
} 