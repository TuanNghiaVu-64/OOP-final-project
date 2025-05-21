// Customer Booking Components

// Mock data for the application
const mockData = {
    flights: [
        { id: 1, flightNumber: 'SK101', aircraft: 'Boeing 737', origin: 'New York (JFK)', destination: 'London (LHR)', departureDate: '2023-08-15', departureTime: '09:00', arrivalDate: '2023-08-15', arrivalTime: '21:30', status: 'Scheduled', price: 450 },
        { id: 2, flightNumber: 'SK202', aircraft: 'Airbus A320', origin: 'London (LHR)', destination: 'Paris (CDG)', departureDate: '2023-08-16', departureTime: '11:15', arrivalDate: '2023-08-16', arrivalTime: '13:30', status: 'Scheduled', price: 180 },
        { id: 3, flightNumber: 'SK303', aircraft: 'Boeing 787', origin: 'Tokyo (HND)', destination: 'Los Angeles (LAX)', departureDate: '2023-08-17', departureTime: '23:45', arrivalDate: '2023-08-18', arrivalTime: '19:30', status: 'Scheduled', price: 950 }
    ],
   
    seatTypes: [
        { id: 1, name: 'first-class', label: 'First Class', basePrice: 1000 },
        { id: 2, name: 'business', label: 'Business', basePrice: 500 },
        { id: 3, name: 'economy', label: 'Economy', basePrice: 200 }
    ],
   
    // Mock seat configurations
    seatConfigurations: {
        1: {
            firstClass: 24,
            business: 36,
            economy: 90,
            occupiedSeats: ['1A', '1B', '2C', '5D', '10F', '15A']
        },
        2: {
            firstClass: 12,
            business: 48,
            economy: 120,
            occupiedSeats: ['2A', '3B', '5C', '7D', '12F']
        },
        3: {
            firstClass: 30,
            business: 40,
            economy: 180,
            occupiedSeats: ['1A', '2B', '8C', '9D', '15F', '22C']
        }
    }
};


// Seat Selection
export function selectSeat(params) {
    const flightId = params?.id;
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
    const seatConfig = flightId ? mockData.seatConfigurations[flightId] : null;
    
    if (!flight || !seatConfig) {
        return `
            <div class="card">
                <h2>Flight Not Found</h2>
                <p>The requested flight could not be found.</p>
                <a href="#" data-route="/customer/search" class="btn btn-primary mt-3">Back to Search</a>
            </div>
        `;
    }
    
    // Generate seat legend
    const seatLegend = `
        <div class="seat-legend">
            <div class="legend-item">
                <div class="legend-color seat-first-class"></div>
                <span>First Class (+$${getSeatPrice('first-class')})</span>
            </div>
            <div class="legend-item">
                <div class="legend-color seat-business"></div>
                <span>Business Class (+$${getSeatPrice('business')})</span>
            </div>
            <div class="legend-item">
                <div class="legend-color seat-economy"></div>
                <span>Economy Class (+$${getSeatPrice('economy')})</span>
            </div>
            <div class="legend-item">
                <div class="legend-color seat-occupied"></div>
                <span>Occupied</span>
            </div>
            <div class="legend-item">
                <div class="legend-color seat-selected"></div>
                <span>Selected</span>
            </div>
        </div>
    `;
    
    // Generate seat map by class
    let seatMap = '';
    
    // First Class Section
    if (seatConfig.firstClass > 0) {
        seatMap += `<div class="class-separator">First Class</div>`;
        seatMap += generateSeatsForClass('first-class', seatConfig.firstClass, seatConfig.occupiedSeats);
    }
    
    // Business Class Section
    if (seatConfig.business > 0) {
        seatMap += `<div class="class-separator">Business Class</div>`;
        seatMap += generateSeatsForClass('business', seatConfig.business, seatConfig.occupiedSeats, 
            seatConfig.firstClass > 0 ? Math.ceil(seatConfig.firstClass / 6) * 6 + 1 : 1);
    }
    
    // Economy Class Section
    if (seatConfig.economy > 0) {
        seatMap += `<div class="class-separator">Economy Class</div>`;
        const startSeatNum = 
            (seatConfig.firstClass > 0 ? Math.ceil(seatConfig.firstClass / 6) * 6 : 0) + 
            (seatConfig.business > 0 ? Math.ceil(seatConfig.business / 6) * 6 : 0) + 1;
        seatMap += generateSeatsForClass('economy', seatConfig.economy, seatConfig.occupiedSeats, startSeatNum);
    }
    
    return `
        <div class="card">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="mb-0">Select Your Seat</h2>
                <a href="#" data-route="/customer/search-results" class="btn btn-outline">
                    <i class="fas fa-arrow-left mr-1"></i> Back to Results
                </a>
            </div>
            
            <div class="card bg-gray-100 p-3 mb-4">
                <div class="row">
                    <div class="col">
                        <p class="mb-1"><strong>Flight:</strong> ${flight.flightNumber}</p>
                        <p class="mb-1"><strong>Route:</strong> ${flight.origin} to ${flight.destination}</p>
                    </div>
                    <div class="col">
                        <p class="mb-1"><strong>Date:</strong> ${flight.departureDate}</p>
                        <p class="mb-1"><strong>Time:</strong> ${flight.departureTime} - ${flight.arrivalTime}</p>
                    </div>
                    <div class="col">
                        <p class="mb-1"><strong>Base Price:</strong> $${flight.price}</p>
                        <p class="mb-1"><strong>Aircraft:</strong> ${flight.aircraft}</p>
                    </div>
                </div>
            </div>
            
            ${seatLegend}
            
            <div class="text-center p-2 mb-3">
                <div class="mb-2" style="width: 60%; height: 10px; background-color: var(--gray-300); margin: 0 auto; border-radius: 5px;"></div>
                <p class="text-gray">Front of Aircraft</p>
            </div>
            
            <div class="seat-map-container">
                <div class="seat-map">
                    ${seatMap}
                </div>
            </div>
            
            <div class="card p-3 mb-4" id="selection-summary" style="display: none;">
                <h4 class="mb-2">Selection Summary</h4>
                <div class="row">
                    <div class="col">
                        <p class="mb-1"><strong>Selected Seat:</strong> <span id="selected-seat-label">-</span></p>
                        <p class="mb-1"><strong>Seat Type:</strong> <span id="selected-seat-type">-</span></p>
                    </div>
                    <div class="col">
                        <p class="mb-1"><strong>Base Price:</strong> $${flight.price}</p>
                        <p class="mb-1"><strong>Seat Price:</strong> $<span id="selected-seat-price">0</span></p>
                    </div>
                    <div class="col text-right">
                        <p class="mb-1"><strong>Total Price:</strong> $<span id="total-price">${flight.price}</span></p>
                    </div>
                </div>
            </div>
            
            <div class="d-flex justify-content-between">
                <a href="#" data-route="/customer/search-results" class="btn btn-outline">Cancel</a>
                <button id="continue-btn" class="btn btn-primary" disabled>Continue to Booking</button>
            </div>
        </div>
    `;
}

// Function to generate seats for a specific class
function generateSeatsForClass(seatType, totalSeats, occupiedSeats, startSeatNum = 1) {
    const seatsPerRow = 6; // 6 seats per row (A-F)
    const columns = ['A', 'B', 'C', 'D', 'E', 'F'];
    const rows = Math.ceil(totalSeats / seatsPerRow);
    
    let seatMap = '';
    
    // Add column labels (A-F)
    seatMap += '<div class="column-labels">';
    columns.forEach((col, index) => {
        seatMap += `<div class="column-label ${index === 3 ? 'aisle' : ''}">${col}</div>`;
        // Add aisle spacer after column C
        if (index === 2) {
            seatMap += '<div class="column-label aisle"></div>';
        }
    });
    seatMap += '</div>';
    
    // Add rows with seats
    seatMap += '<div class="seat-rows">';
    
    for (let rowIndex = 0; rowIndex < rows; rowIndex++) {
        const rowNumber = Math.ceil(startSeatNum / seatsPerRow) + rowIndex;
        
        // Start row
        seatMap += `<div class="seat-row">`;
        
        // Add row label
        seatMap += `<div class="row-label">${rowNumber}</div>`;
        
        // Add seats for this row
        seatMap += `<div class="seat-grid">`;
        for (let colIndex = 0; colIndex < columns.length; colIndex++) {
            const seatNumber = `${rowNumber}${columns[colIndex]}`;
            const isOccupied = occupiedSeats.includes(seatNumber);
            
            seatMap += `
                <div class="seat seat-${seatType} ${isOccupied ? 'seat-occupied' : ''}" 
                     data-seat="${seatNumber}" 
                     data-type="${seatType}" 
                     data-price="${getSeatPrice(seatType)}">
                    ${seatNumber}
                </div>
            `;
            
            // Add aisle after column C
            if (colIndex === 2) {
                seatMap += '<div class="seat aisle"></div>';
            }
        }
        seatMap += `</div>`;  // Close seat-grid
        
        // Close row
        seatMap += `</div>`;
    }
    
    seatMap += '</div>'; // Close seat-rows
    
    return seatMap;
}


// Trip Overview
export function tripOverview(params) {
    const flightId = params?.id;
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
    
    if (!flight) {
        return `
            <div class="card">
                <h2>Flight Not Found</h2>
                <p>The requested flight could not be found.</p>
                <a href="#" data-route="/customer/search" class="btn btn-primary mt-3">Back to Search</a>
            </div>
        `;
    }
    
    // Get selected seat information from session storage or use defaults
    let selectedSeat = '15A';
    let seatType = 'business';
    let seatPrice = getSeatPrice(seatType);
    let totalPrice = flight.price + seatPrice;
    
    // Try to get stored seat selection
    const storedSeatInfo = sessionStorage.getItem('selectedSeatInfo');
    if (storedSeatInfo) {
        try {
            const seatInfo = JSON.parse(storedSeatInfo);
            selectedSeat = seatInfo.number;
            seatType = seatInfo.type;
            seatPrice = seatInfo.price;
            totalPrice = seatInfo.totalPrice;
        } catch (e) {
            console.error('Error parsing stored seat information:', e);
        }
    }
    
    return `
        <div class="card">
            <h2>Trip Overview</h2>
            
            <div class="card bg-gray-100 p-3 mb-4 mt-4">
                <div class="row">
                    <div class="col">
                        <h4 class="mb-2">Flight Details</h4>
                        <p class="mb-1"><strong>Flight Number:</strong> ${flight.flightNumber}</p>
                        <p class="mb-1"><strong>Aircraft:</strong> ${flight.aircraft}</p>
                        <p class="mb-1"><strong>Route:</strong> ${flight.origin} to ${flight.destination}</p>
                        <p class="mb-1"><strong>Date:</strong> ${flight.departureDate}</p>
                        <p class="mb-1"><strong>Departure:</strong> ${flight.departureTime}</p>
                        <p class="mb-1"><strong>Arrival:</strong> ${flight.arrivalTime}</p>
                    </div>
                    
                    <div class="col">
                        <h4 class="mb-2">Seat Selection</h4>
                        <p class="mb-1"><strong>Selected Seat:</strong> ${selectedSeat}</p>
                        <p class="mb-1"><strong>Seat Type:</strong> ${getSeatTypeLabel(seatType)}</p>
                        <p class="mb-1"><strong>Features:</strong></p>
                        <ul>
                            ${getSeatFeatures(seatType)}
                        </ul>
                    </div>
                </div>
            </div>
            
            <div class="card p-3 mb-4">
                <h4 class="mb-3">Price Breakdown</h4>
                <div class="d-flex justify-content-between mb-2">
                    <span>Base Ticket Price:</span>
                    <span>$${flight.price}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>${getSeatTypeLabel(seatType)} Seat:</span>
                    <span>$${seatPrice}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Taxes & Fees:</span>
                    <span>Included</span>
                </div>
                <hr>
                <div class="d-flex justify-content-between">
                    <h4 class="mb-0">Total:</h4>
                    <h4 class="mb-0">$${totalPrice}</h4>
                </div>
            </div>
            
            <div class="d-flex justify-content-between">
                <a href="#" data-route="/customer/select-seat/${flight.id}" class="btn btn-outline">
                    <i class="fas fa-arrow-left mr-1"></i> Back to Seat Selection
                </a>
                <a href="#" data-route="/customer/payment/${flight.id}" class="btn btn-primary">
                    <i class="fas fa-credit-card mr-1"></i> Proceed to Payment
                </a>
            </div>
        </div>
    `;
}

// Helper function to get seat features based on seat type
function getSeatFeatures(seatType) {
    let features = '';
    
    switch(seatType) {
        case 'first-class':
            features = `
                <li>Extra spacious seating</li>
                <li>Priority boarding and check-in</li>
                <li>Full-course gourmet meals</li>
                <li>Premium entertainment system</li>
                <li>Complimentary amenity kit</li>
                <li>Fully reclining seats with bedding</li>
            `;
            break;
        case 'business':
            features = `
                <li>Extra legroom</li>
                <li>Priority boarding</li>
                <li>Complimentary meal</li>
                <li>In-flight entertainment</li>
                <li>Reclining seats</li>
            `;
            break;
        case 'economy':
            features = `
                <li>Standard seating</li>
                <li>Complimentary drinks</li>
                <li>In-flight entertainment</li>
            `;
            break;
        default:
            features = '<li>Standard features</li>';
    }
    
    return features;
}


// Payment
export function payment(params) {
    const flightId = params?.id;
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
   
    if (!flight) {
        return `
            <div class="card">
                <h2>Flight Not Found</h2>
                <p>The requested flight could not be found.</p>
                <a href="#" data-route="/customer/search" class="btn btn-primary mt-3">Back to Search</a>
            </div>
        `;
    }
   
    // For demo, assume 15A business class seat is selected
    const seatType = 'business';
    const seatPrice = getSeatPrice(seatType);
    const totalPrice = flight.price + seatPrice;
   
    return `
        <div class="card">
            <h2>Payment</h2>
           
            <div class="row mt-4">
                <div class="col-md-7">
                    <div class="card p-3 mb-4">
                        <h4 class="mb-3">Payment Information</h4>
                       
                        <form id="payment-form">
                            <div class="form-group">
                                <label for="card-name" class="form-label">Name on Card</label>
                                <input type="text" id="card-name" class="form-control" placeholder="John Doe" required>
                            </div>
                           
                            <div class="form-group">
                                <label for="card-number" class="form-label">Card Number</label>
                                <input type="text" id="card-number" class="form-control" placeholder="1234 5678 9012 3456" required>
                            </div>
                           
                            <div class="row">
                                <div class="col">
                                    <div class="form-group">
                                        <label for="expiry-date" class="form-label">Expiry Date</label>
                                        <input type="text" id="expiry-date" class="form-control" placeholder="MM/YY" required>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="form-group">
                                        <label for="cvv" class="form-label">CVV</label>
                                        <input type="text" id="cvv" class="form-control" placeholder="123" required>
                                    </div>
                                </div>
                            </div>
                           
                            <h4 class="mb-3 mt-4">Billing Address</h4>
                           
                            <div class="form-group">
                                <label for="address" class="form-label">Address</label>
                                <input type="text" id="address" class="form-control" required>
                            </div>
                           
                            <div class="row">
                                <div class="col">
                                    <div class="form-group">
                                        <label for="city" class="form-label">City</label>
                                        <input type="text" id="city" class="form-control" required>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="form-group">
                                        <label for="postal-code" class="form-label">Postal Code</label>
                                        <input type="text" id="postal-code" class="form-control" required>
                                    </div>
                                </div>
                            </div>
                           
                            <div class="form-group">
                                <label for="country" class="form-label">Country</label>
                                <select id="country" class="form-control" required>
                                    <option value="">Select Country</option>
                                    <option value="us">United States</option>
                                    <option value="ca">Canada</option>
                                    <option value="uk">United Kingdom</option>
                                    <option value="au">Australia</option>
                                </select>
                            </div>
                           
                            <div class="form-group">
                                <div class="form-check">
                                    <input type="checkbox" id="save-info" class="form-check-input">
                                    <label for="save-info" class="form-check-label">Save this information for next time</label>
                                </div>
                            </div>
                           
                            <div class="d-flex justify-content-between mt-4">
                                <a href="#" data-route="/customer/trip-overview/${flight.id}" class="btn btn-outline">Go Back</a>
                                <button type="submit" class="btn btn-primary">Confirm Payment</button>
                            </div>
                        </form>
                    </div>
                </div>
               
                <div class="col-md-5">
                    <div class="card p-3 mb-4">
                        <h4 class="mb-3">Order Summary</h4>
                       
                        <div class="d-flex justify-content-between mb-2">
                            <span>Flight ${flight.flightNumber}:</span>
                            <span>$${flight.price}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>${getSeatTypeLabel(seatType)} Seat:</span>
                            <span>$${seatPrice}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Taxes & Fees:</span>
                            <span>Included</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <h4 class="mb-0">Total:</h4>
                            <h4 class="mb-0">$${totalPrice}</h4>
                        </div>
                    </div>
                   
                    <div class="card bg-gray-100 p-3">
                        <h5 class="mb-2">Flight Details</h5>
                        <p class="mb-1"><strong>${flight.flightNumber}:</strong> ${flight.origin} to ${flight.destination}</p>
                        <p class="mb-1"><strong>Date:</strong> ${flight.departureDate}</p>
                        <p class="mb-1"><strong>Time:</strong> ${flight.departureTime} - ${flight.arrivalTime}</p>
                        <p class="mb-0"><strong>Seat:</strong> 15A (${getSeatTypeLabel(seatType)})</p>
                    </div>
                </div>
            </div>
        </div>
    `;
}


// Booking Confirmation
export function confirmation(params) {
    const flightId = params?.id;
    const flight = flightId ? mockData.flights.find(f => f.id.toString() === flightId) : null;
   
    if (!flight) {
        return `
            <div class="card">
                <h2>Flight Not Found</h2>
                <p>The requested flight could not be found.</p>
                <a href="#" data-route="/customer/search" class="btn btn-primary mt-3">Back to Search</a>
            </div>
        `;
    }
   
    // For demo, assume 15A business class seat is selected
    const selectedSeat = '15A';
    const seatType = 'business';
    const bookingId = Math.floor(Math.random() * 100000);
   
    return `
        <div class="card text-center p-5">
            <div class="mb-4">
                <i class="fas fa-check-circle text-success" style="font-size: 4rem;"></i>
            </div>
           
            <h2 class="mb-3">Booking Confirmed</h2>
            <p class="mb-4">Your flight has been successfully booked. Your booking reference is <strong>#${bookingId}</strong>.</p>
           
            <div class="card bg-gray-100 p-3 mb-4 text-left" style="max-width: 600px; margin: 0 auto;">
                <div class="row">
                    <div class="col-md-6">
                        <h4 class="mb-2">Flight Details</h4>
                        <p class="mb-1"><strong>Flight Number:</strong> ${flight.flightNumber}</p>
                        <p class="mb-1"><strong>Route:</strong> ${flight.origin} to ${flight.destination}</p>
                        <p class="mb-1"><strong>Date:</strong> ${flight.departureDate}</p>
                        <p class="mb-1"><strong>Departure:</strong> ${flight.departureTime}</p>
                        <p class="mb-1"><strong>Arrival:</strong> ${flight.arrivalTime}</p>
                    </div>
                    <div class="col-md-6">
                        <h4 class="mb-2">Seat Details</h4>
                        <p class="mb-1"><strong>Seat Number:</strong> ${selectedSeat}</p>
                        <p class="mb-1"><strong>Seat Type:</strong> ${getSeatTypeLabel(seatType)}</p>
                        <h4 class="mb-2 mt-3">Passenger</h4>
                        <p class="mb-1">John Doe</p>
                    </div>
                </div>
            </div>
           
            <p class="mb-4">A confirmation email has been sent to your registered email address with all the details.</p>
           
            <div class="d-flex justify-content-center">
                <a href="#" data-route="/customer/tickets" class="btn btn-outline mr-2">
                    <i class="fas fa-ticket-alt mr-1"></i> View My Tickets
                </a>
                <a href="#" data-route="/customer/dashboard" class="btn btn-primary">
                    <i class="fas fa-home mr-1"></i> Go to Dashboard
                </a>
            </div>
        </div>
    `;
}


// Helper functions
function getSeatPrice(seatType) {
    const seatTypeObj = mockData.seatTypes.find(type => type.name === seatType);
    return seatTypeObj ? seatTypeObj.basePrice : 0;
}


function getSeatTypeLabel(seatType) {
    const seatTypeObj = mockData.seatTypes.find(type => type.name === seatType);
    return seatTypeObj ? seatTypeObj.label : 'Unknown';
}

// Add seat selection functionality
document.addEventListener('DOMContentLoaded', function() {
    initSeatSelection();
});

// Initialize seat selection functionality
function initSeatSelection() {
    const seatMap = document.querySelector('.seat-map');
    if (!seatMap) return;
    
    let selectedSeat = null;
    const continueBtn = document.getElementById('continue-btn');
    const selectionSummary = document.getElementById('selection-summary');
    const selectedSeatLabel = document.getElementById('selected-seat-label');
    const selectedSeatType = document.getElementById('selected-seat-type');
    const selectedSeatPrice = document.getElementById('selected-seat-price');
    const totalPrice = document.getElementById('total-price');
    
    // Get base price from the flight information
    const basePrice = parseInt(document.querySelector('.card.bg-gray-100 .col:nth-child(3) p:first-child')
        .textContent.replace(/[^\d]/g, ''));
    
    // Add click event listeners to all available seats
    const seats = document.querySelectorAll('.seat:not(.aisle):not(.seat-occupied)');
    seats.forEach(seat => {
        seat.addEventListener('click', function() {
            // Toggle selection - if clicking the same seat, deselect it
            if (selectedSeat === this) {
                // Deselect current seat
                this.classList.remove('seat-selected');
                selectedSeat = null;
                
                // Hide summary and disable continue button
                selectionSummary.style.display = 'none';
                continueBtn.disabled = true;
                
                // Clear session storage
                sessionStorage.removeItem('selectedSeatInfo');
                return;
            }
            
            // Clear previous selection
            if (selectedSeat) {
                selectedSeat.classList.remove('seat-selected');
            }
            
            // Update selection
            selectedSeat = this;
            this.classList.add('seat-selected');
            
            // Get seat information
            const seatNumber = this.dataset.seat;
            const seatType = this.dataset.type;
            const seatPrice = parseInt(this.dataset.price);
            
            // Update selection summary
            selectedSeatLabel.textContent = seatNumber;
            selectedSeatType.textContent = getSeatTypeLabel(seatType);
            selectedSeatPrice.textContent = seatPrice;
            totalPrice.textContent = basePrice + seatPrice;
            
            // Show summary and enable continue button
            selectionSummary.style.display = 'block';
            continueBtn.disabled = false;
            
            // Store selection in session storage for later use
            sessionStorage.setItem('selectedSeatInfo', JSON.stringify({
                number: seatNumber,
                type: seatType,
                price: seatPrice,
                totalPrice: basePrice + seatPrice
            }));
        });
    });
    
    // Handle continue button click
    if (continueBtn) {
        continueBtn.addEventListener('click', function() {
            const flightId = window.location.pathname.split('/').pop();
            window.location.href = `/customer/trip-overview/${flightId}`;
        });
    }
    
    // Check for previously selected seat from session storage and restore selection
    const storedSeatInfo = sessionStorage.getItem('selectedSeatInfo');
    if (storedSeatInfo) {
        try {
            const seatInfo = JSON.parse(storedSeatInfo);
            const savedSeat = document.querySelector(`.seat[data-seat="${seatInfo.number}"]`);
            
            if (savedSeat && !savedSeat.classList.contains('seat-occupied')) {
                // Trigger click event to select the saved seat
                savedSeat.click();
            }
        } catch (e) {
            console.error('Error restoring seat selection:', e);
        }
    }
}

