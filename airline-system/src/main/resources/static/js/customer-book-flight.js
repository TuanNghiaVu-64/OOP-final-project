// Variables will be injected by Thymeleaf template
let seatAssignments = [];
let seatTypes = [];
let seatTypePrices = {};
let flightId = 0;
let selectedSeats = []; // Changed to array for multiple seats

// Initialize page when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Set up event listeners
    document.getElementById('confirmBookingBtn').addEventListener('click', confirmBooking);
    document.getElementById('cancelSelectionBtn').addEventListener('click', cancelSelection);
    document.getElementById('payNowBtn').addEventListener('click', processPayment);
    document.getElementById('closeModalBtn').addEventListener('click', closeModal);
    
    // Close modal when clicking outside
    document.getElementById('paymentModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });
});

function initializeSeatMap() {
    const seatMap = document.getElementById('seatMap');
    
    if (seatAssignments.length === 0) {
        seatMap.innerHTML = '<div class="no-seats">No seats available for this flight</div>';
        return;
    }
    
    // Group seats by row
    const seatsByRow = {};
    let maxCol = 0;
    let maxSeatsInRow = 0;
    
    seatAssignments.forEach(assignment => {
        const seatNum = assignment.seatNumber;
        const row = parseInt(seatNum.match(/\d+/)[0]);
        const col = seatNum.match(/[A-Z]/)[0];
        
        if (!seatsByRow[row]) {
            seatsByRow[row] = {};
        }
        seatsByRow[row][col] = assignment;
        
        const colIndex = col.charCodeAt(0) - 'A'.charCodeAt(0);
        maxCol = Math.max(maxCol, colIndex);
    });
    
    // Determine max seats in any row
    Object.keys(seatsByRow).forEach(row => {
        const seatsInThisRow = Object.keys(seatsByRow[row]).length;
        maxSeatsInRow = Math.max(maxSeatsInRow, seatsInThisRow);
    });
    
    const rows = Object.keys(seatsByRow).sort((a, b) => parseInt(a) - parseInt(b));
    const cols = Array.from({length: maxCol + 1}, (_, i) => String.fromCharCode('A'.charCodeAt(0) + i));
    
    // Detect if it's a small plane (54 seats total, max 4 seats per row)
    const isSmallPlane = seatAssignments.length <= 54 && maxSeatsInRow <= 4;
    
    if (isSmallPlane) {
        // Set up small plane layout - no grid, custom positioning
        seatMap.style.display = 'flex';
        seatMap.style.flexDirection = 'column';
        seatMap.style.alignItems = 'center';
        seatMap.style.gap = '12px';
        seatMap.style.maxWidth = '400px';
        
        // Create custom row containers
        rows.forEach(rowNum => {
            const rowContainer = document.createElement('div');
            rowContainer.className = 'seat-row';
            rowContainer.style.display = 'flex';
            rowContainer.style.alignItems = 'center';
            rowContainer.style.gap = '8px';
            rowContainer.style.width = '100%';
            
            const seatsInRow = Object.keys(seatsByRow[rowNum]).length;
            // Row 4 with 1 seat should align left, others center
            if (parseInt(rowNum) === 4 && seatsInRow === 1) {
                rowContainer.style.justifyContent = 'flex-start';
                rowContainer.style.paddingLeft = '1.6px'; // Add some left margin for the 4th row seat
            } else {
                rowContainer.style.justifyContent = 'center';
            }
            const isPremiumRow = parseInt(rowNum) <= 4;
            
            cols.forEach((col, index) => {
                const assignment = seatsByRow[rowNum] && seatsByRow[rowNum][col];
                
                if (assignment) {
                    const seatElement = document.createElement('div');
                    seatElement.className = `seat ${assignment.available ? 'available' : 'occupied'}`;
                    seatElement.textContent = assignment.seatNumber;
                    const features = assignment.seatTypeFeatures ? ` - Features: ${assignment.seatTypeFeatures}` : '';
                    seatElement.title = `${assignment.seatNumber} - ${assignment.seatTypeName}${features}`;
                    seatElement.dataset.assignmentId = assignment.id;
                    
                    // Size seats based on row type
                    if (isPremiumRow) {
                        // Premium rows (1-3): bigger seats, fewer per row
                        if (seatsInRow === 1) {
                            seatElement.style.width = '60px'; // Same size as individual seats in rows 1-3
                            seatElement.style.height = '56px';
                        } else if (seatsInRow === 3) {
                            seatElement.style.width = '60px'; // 3 seats with spacing
                            seatElement.style.height = '56px';
                        } else {
                            seatElement.style.width = '48px';
                            seatElement.style.height = '56px';
                        }
                    } else {
                        // Economy rows (5+): normal sized seats
                        seatElement.style.width = '44px';
                        seatElement.style.height = '44px';
                    }
                    
                    // Add aisle spacing for small plane layout
                    if (seatsInRow === 4 && index === 1) {
                        seatElement.style.marginRight = '20px'; // Aisle after B
                    } else if (seatsInRow === 3 && index === 0) {
                        seatElement.style.marginRight = '20px'; // Aisle after A in 3-seat rows
                    }
                    
                    if (assignment.available) {
                        seatElement.onclick = () => toggleSeatSelection(assignment, seatElement);
                        seatElement.onmouseenter = (e) => showSeatTooltip(e, assignment);
                        seatElement.onmouseleave = () => hideSeatTooltip();
                    }
                    
                    rowContainer.appendChild(seatElement);
                }
            });
            
            seatMap.appendChild(rowContainer);
        });
    } else {
        // Big plane layout with premium first 3 rows
        seatMap.style.display = 'flex';
        seatMap.style.flexDirection = 'column';
        seatMap.style.alignItems = 'center';
        seatMap.style.gap = '12px';
        seatMap.style.maxWidth = '600px';
        
        rows.forEach(rowNum => {
            const rowContainer = document.createElement('div');
            rowContainer.className = 'seat-row';
            rowContainer.style.display = 'flex';
            rowContainer.style.alignItems = 'center';
            rowContainer.style.gap = '0px'; // Remove automatic gap, we'll add manual spacing
            rowContainer.style.width = '100%';
            
            // Special alignment for row 18 - align to the left like upper rows
            if (parseInt(rowNum) === 18) {
                rowContainer.style.justifyContent = 'flex-start';
                rowContainer.style.paddingLeft = '0px'; // left padding to align with upper rows
            } else {
                rowContainer.style.justifyContent = 'center';
            }
            
            // Check if this is a premium row (first 3 rows)
            const isPremiumRow = rowNum <= 3;
            
            // No special styling for premium rows - just bigger seats
            
            // Add row label
            const rowLabel = document.createElement('div');
            rowLabel.className = 'row-label';
            rowLabel.textContent = rowNum;
            rowLabel.style.minWidth = '25px';
            rowLabel.style.textAlign = 'center';
            rowLabel.style.fontSize = '12px';
            rowLabel.style.color = '#666';
            rowLabel.style.fontWeight = 'bold';
            rowContainer.appendChild(rowLabel);
            
            // Get actual seats for this row to determine how many seats exist
            const rowSeats = seatsByRow[rowNum] || {};
            const actualCols = Object.keys(rowSeats).sort();
            
            actualCols.forEach((col, index) => {
                const assignment = seatsByRow[rowNum] && seatsByRow[rowNum][col];
                
                // Since we're only iterating through actual seats, assignment should always exist
                const seatElement = document.createElement('div');
                seatElement.className = `seat ${assignment.available ? 'available' : 'occupied'}`;
                seatElement.textContent = assignment.seatNumber;
                const features = assignment.seatTypeFeatures ? ` - Features: ${assignment.seatTypeFeatures}` : '';
                seatElement.title = `${assignment.seatNumber} - ${assignment.seatTypeName}${features}`;
                seatElement.dataset.assignmentId = assignment.id;
                
                // Size seats based on row type
                if (isPremiumRow) {
                    // Premium rows (1-3): smaller seats to better match 6-seat row width
                    // Reduced from 57px to 50px for better proportion
                    seatElement.style.width = '54px';
                    seatElement.style.height = '54px';
                    seatElement.style.fontSize = '12px';
                } else {
                    // Economy rows (4+): normal sized seats
                    seatElement.style.width = '36px';
                    seatElement.style.height = '36px';
                    seatElement.style.fontSize = '11px';
                }
                
                if (assignment.available) {
                    seatElement.onclick = () => toggleSeatSelection(assignment, seatElement);
                    seatElement.onmouseenter = (e) => showSeatTooltip(e, assignment);
                    seatElement.onmouseleave = () => hideSeatTooltip();
                }
                
                // Add manual spacing after each seat (except the last one)
                if (index < actualCols.length - 1) {
                    // Define custom gaps for different seat positions
                    let gapSize = '8px'; // Default gap
                    
                    if (isPremiumRow) {
                        // Premium rows (1-3): Custom gaps for 4-seat layout (A B C D)
                        if (index === 0) gapSize = '10px'; // Gap after A
                        else if (index === 1) gapSize = '22px'; // Gap after B (aisle)
                        else if (index === 2) gapSize = '10px'; // Gap after C
                    } else if (parseInt(rowNum) === 18) {
                        // Special spacing for row 18
                        if (index === 0) gapSize = '5px';  // Gap after A
                        else if (index === 1) gapSize = '104px';  // Gap after B (wider aisle)
                        else if (index === 2) gapSize = '6px'; // Gap after C
                        else if (index === 3) gapSize = '10px';  // Gap after D
                    } else {
                        // Economy rows (4+): Custom gaps for 6-seat layout (A B C D E F)
                        if (index === 0) gapSize = '6px';  // Gap after A
                        else if (index === 1) gapSize = '6px';  // Gap after B
                        else if (index === 2) gapSize = '20px'; // Gap after C (aisle)
                        else if (index === 3) gapSize = '6px';  // Gap after D
                        else if (index === 4) gapSize = '6px';  // Gap after E
                    }
                    
                    seatElement.style.marginRight = gapSize;
                }
                
                rowContainer.appendChild(seatElement);
            });
            
            seatMap.appendChild(rowContainer);
            
            // Add separator after row 3 (between premium and economy)
            if (rowNum === 3) {
                const separator = document.createElement('div');
                separator.style.width = '100%';
                separator.style.height = '2px';
                separator.style.background = 'linear-gradient(to right, transparent, #ddd, transparent)';
                separator.style.margin = '10px 0';
                seatMap.appendChild(separator);
            }
        });
    }
}

function toggleSeatSelection(assignment, seatElement) {
    const isSelected = selectedSeats.find(seat => seat.id === assignment.id);
    
    if (isSelected) {
        // Deselect seat
        selectedSeats = selectedSeats.filter(seat => seat.id !== assignment.id);
        seatElement.classList.remove('selected');
    } else {
        // Select seat
        selectedSeats.push(assignment);
        seatElement.classList.add('selected');
    }
    
    updateBookingSummary();
}

function updateBookingSummary() {
    if (selectedSeats.length === 0) {
        document.getElementById('bookingSummary').style.display = 'none';
        return;
    }
    
    // Calculate total price
    let totalPrice = 0;
    let seatNumbers = [];
    let seatTypes = [];
    let seatFeatures = [];
    
    selectedSeats.forEach(seat => {
        const price = seatTypePrices[seat.flightSeatTypeId];
        if (price) {
            totalPrice += price;
        }
        seatNumbers.push(seat.seatNumber);
        if (!seatTypes.includes(seat.seatTypeName)) {
            seatTypes.push(seat.seatTypeName);
        }
        if (seat.seatTypeFeatures && !seatFeatures.includes(seat.seatTypeFeatures)) {
            seatFeatures.push(seat.seatTypeFeatures);
        }
    });
    
    // Update display
    document.getElementById('selectedSeatDisplay').textContent = seatNumbers.join(', ');
    document.getElementById('selectedSeatTypeDisplay').textContent = seatTypes.join(', ');
    document.getElementById('selectedSeatFeaturesDisplay').textContent = seatFeatures.length > 0 ? seatFeatures.join(', ') : 'No special features';
    document.getElementById('seatPriceDisplay').textContent = '$' + totalPrice.toFixed(2);
    document.getElementById('totalAmountDisplay').textContent = '$' + totalPrice.toFixed(2);
    
    // Show booking summary
    document.getElementById('bookingSummary').style.display = 'block';
}

function cancelSelection() {
    if (selectedSeats.length > 0) {
        // Remove selected class from all selected seats
        document.querySelectorAll('.seat.selected').forEach(seat => {
            seat.classList.remove('selected');
        });
        selectedSeats = [];
        document.getElementById('bookingSummary').style.display = 'none';
    }
}

function confirmBooking() {
    if (selectedSeats.length === 0) {
        alert('Please select at least one seat first');
        return;
    }
    
    // Update modal with selection details
    const seatNumbers = selectedSeats.map(seat => seat.seatNumber).join(', ');
    const seatTypes = [...new Set(selectedSeats.map(seat => seat.seatTypeName))].join(', ');
    document.getElementById('modalSeatInfo').textContent = 
        `${seatNumbers} (${seatTypes})`;
    document.getElementById('modalTotalAmount').textContent = 
        document.getElementById('totalAmountDisplay').textContent;
    
    // Show payment modal
    document.getElementById('paymentModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('paymentModal').style.display = 'none';
}

function processPayment() {
    if (selectedSeats.length === 0) return;
    
    const payBtn = document.getElementById('payNowBtn');
    const originalText = payBtn.innerHTML;
    payBtn.disabled = true;
    payBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
    
    // Create form data with multiple seat assignment IDs
    const seatIds = selectedSeats.map(seat => seat.id);
    const formData = new URLSearchParams();
    seatIds.forEach(id => formData.append('flightSeatAssignmentIds', id));
    
    fetch('/customer/confirm-booking', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString()
    })
    .then(response => response.text())
    .then(data => {
        let result;
        try {
            result = JSON.parse(data);
        } catch (e) {
            console.error('Failed to parse JSON from confirm-booking:', data);
            throw new Error('Server returned invalid response format');
        }
        
        if (result.success) {
            // Process payment for all bookings
            const bookingIds = result.bookingIds;
            const formData = new URLSearchParams();
            bookingIds.forEach(id => formData.append('bookingIds', id));
            
            return fetch('/customer/pay-bookings', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData.toString()
            });
        } else {
            throw new Error(result.message);
        }
    })
    .then(response => response.text())
    .then(data => {
        let result;
        try {
            result = JSON.parse(data);
        } catch (e) {
            console.error('Failed to parse JSON from pay-bookings:', data);
            throw new Error('Server returned invalid response format');
        }
        
        if (result.success) {
            // Success message and redirect
            const seatCount = selectedSeats.length;
            const seatText = seatCount === 1 ? 'seat has' : 'seats have';
            alert(`🎉 Booking successful! Payment confirmed.\n\nYour ${seatCount} ${seatText} been reserved and you will be redirected to view your bookings.`);
            window.location.href = '/customer/my-bookings';
        } else {
            throw new Error(result.message);
        }
    })
    .catch(error => {
        alert('Error: ' + error.message);
        payBtn.disabled = false;
        payBtn.innerHTML = originalText;
    });
}

function showSeatTooltip(event, assignment) {
    const tooltip = document.getElementById('seatTooltip');
    const price = seatTypePrices[assignment.flightSeatTypeId];
    
    // Update tooltip content
    document.getElementById('tooltipSeatNumber').textContent = assignment.seatNumber;
    document.getElementById('tooltipSeatType').textContent = assignment.seatTypeName;
    document.getElementById('tooltipFeatures').textContent = assignment.seatTypeFeatures || 'No special features';
    document.getElementById('tooltipPrice').textContent = price ? `$${price.toFixed(2)}` : 'Price not available';
    
    // Position tooltip
    const rect = event.target.getBoundingClientRect();
    const containerRect = document.querySelector('.seat-map-container').getBoundingClientRect();
    
    tooltip.style.left = (rect.left - containerRect.left + rect.width / 2 - 100) + 'px';
    tooltip.style.top = (rect.top - containerRect.top - tooltip.offsetHeight - 10) + 'px';
    
    // Show tooltip
    tooltip.style.display = 'block';
}

function hideSeatTooltip() {
    document.getElementById('seatTooltip').style.display = 'none';
}

// Function to initialize data from Thymeleaf template
function initializeData(assignments, types, prices, fId) {
    seatAssignments = assignments;
    seatTypes = types;
    seatTypePrices = prices;
    flightId = fId;
    
    // Debug: Check if we have data
    if (seatAssignments.length === 0) {
        console.warn('No seat assignments found for flight', fId);
    }
    
    // Initialize the seat map after data is loaded
    initializeSeatMap();
} 