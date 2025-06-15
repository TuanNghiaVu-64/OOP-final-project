import { api } from '../utils/api.js';

/**
 * Flight Management JavaScript
 * Handles all flight-related functionality including validation, approval, and form handling
 */

// Flight form validation
export function validateFlightForm() {
    const planeSelect = document.getElementById('planeId');
    const originSelect = document.getElementById('originId');
    const destinationSelect = document.getElementById('destinationId');
    const datetimeInput = document.getElementById('departureDateTime');
    const arrivalInput = document.getElementById('arrivalDateTime');
    
    const planeError = document.getElementById('plane-error');
    const originError = document.getElementById('origin-error');
    const destinationError = document.getElementById('destination-error');
    const datetimeError = document.getElementById('datetime-error');
    const arrivalError = document.getElementById('arrival-error');
    
    let isValid = true;
    
    // Reset previous error states
    planeSelect.classList.remove('error');
    originSelect.classList.remove('error');
    destinationSelect.classList.remove('error');
    datetimeInput.classList.remove('error');
    arrivalInput.classList.remove('error');
    planeError.style.display = 'none';
    originError.style.display = 'none';
    destinationError.style.display = 'none';
    datetimeError.style.display = 'none';
    arrivalError.style.display = 'none';
    
    // Validate aircraft selection
    if (!planeSelect.value) {
        planeSelect.classList.add('error');
        planeError.style.display = 'block';
        isValid = false;
    }
    
    // Validate origin selection
    if (!originSelect.value) {
        originSelect.classList.add('error');
        originError.style.display = 'block';
        isValid = false;
    }
    
    // Validate destination selection
    if (!destinationSelect.value) {
        destinationSelect.classList.add('error');
        destinationError.style.display = 'block';
        isValid = false;
    }
    
    // Validate departure datetime
    if (!datetimeInput.value) {
        datetimeInput.classList.add('error');
        datetimeError.style.display = 'block';
        isValid = false;
    } else {
        // Check if datetime is in the future
        const selectedDateTime = new Date(datetimeInput.value);
        const now = new Date();
        if (selectedDateTime <= now) {
            datetimeInput.classList.add('error');
            datetimeError.textContent = 'Departure time must be in the future';
            datetimeError.style.display = 'block';
            isValid = false;
        }
    }
    
    // Validate arrival datetime
    if (!arrivalInput.value) {
        arrivalInput.classList.add('error');
        arrivalError.style.display = 'block';
        isValid = false;
    } else {
        // Check if arrival time is after departure time
        const departureDateTime = new Date(datetimeInput.value);
        const arrivalDateTime = new Date(arrivalInput.value);
        if (arrivalDateTime <= departureDateTime) {
            arrivalInput.classList.add('error');
            arrivalError.textContent = 'Arrival time must be after departure time';
            arrivalError.style.display = 'block';
            isValid = false;
        }
    }
    
    // Check if origin and destination are the same
    if (originSelect.value && destinationSelect.value && originSelect.value === destinationSelect.value) {
        originSelect.classList.add('error');
        destinationSelect.classList.add('error');
        originError.textContent = 'Origin and destination cannot be the same';
        destinationError.textContent = 'Origin and destination cannot be the same';
        originError.style.display = 'block';
        destinationError.style.display = 'block';
        isValid = false;
    }
    
    return isValid;
}

// Approve flight
export async function approveFlight(flightId, rowNum) {
    if (confirm('Are you sure you want to approve flight #' + rowNum + '?')) {
        try {
            const response = await api.post(`/flights/approve/${flightId}`);
            
            if (response === 'success') {
                location.reload();
            } else {
                alert('Error: ' + response);
            }
        } catch (error) {
            alert('Error: ' + error);
        }
    }
}

// Reject flight
export async function rejectFlight(flightId, rowNum) {
    if (confirm('Are you sure you want to reject flight #' + rowNum + '?')) {
        try {
            const response = await api.post(`/flights/reject/${flightId}`);
            
            if (response === 'success') {
                location.reload();
            } else {
                alert('Error: ' + response);
            }
        } catch (error) {
            alert('Error: ' + error);
        }
    }
}

// Delete flight
export async function deleteFlight(flightId) {
    if (!confirm('Are you sure you want to delete this flight? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await api.post(`/flights/delete/${flightId}`);
        
        if (response === 'success') {
            location.reload();
        } else {
            alert('Error deleting flight: ' + response.replace('error: ', ''));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error deleting flight');
    }
}

// Initialize flight form validation
export function initializeFlightValidation() {
    const planeSelect = document.getElementById('planeId');
    const originSelect = document.getElementById('originId');
    const destinationSelect = document.getElementById('destinationId');
    const datetimeInput = document.getElementById('departureDateTime');
    const arrivalInput = document.getElementById('arrivalDateTime');
    
    if (planeSelect) {
        planeSelect.addEventListener('change', function() {
            if (this.value) {
                this.classList.remove('error');
                document.getElementById('plane-error').style.display = 'none';
            }
        });
    }
    
    if (originSelect) {
        originSelect.addEventListener('change', function() {
            if (this.value) {
                this.classList.remove('error');
                document.getElementById('origin-error').style.display = 'none';
            }
            // Re-validate if both origin and destination are selected
            if (this.value && destinationSelect.value && this.value === destinationSelect.value) {
                validateFlightForm();
            }
        });
    }
    
    if (destinationSelect) {
        destinationSelect.addEventListener('change', function() {
            if (this.value) {
                this.classList.remove('error');
                document.getElementById('destination-error').style.display = 'none';
            }
            // Re-validate if both origin and destination are selected
            if (this.value && originSelect.value && this.value === originSelect.value) {
                validateFlightForm();
            }
        });
    }
    
    if (datetimeInput) {
        datetimeInput.addEventListener('change', function() {
            if (this.value) {
                const selectedDateTime = new Date(this.value);
                const now = new Date();
                if (selectedDateTime > now) {
                    this.classList.remove('error');
                    document.getElementById('datetime-error').style.display = 'none';
                }
            }
        });
    }
    
    if (arrivalInput) {
        arrivalInput.addEventListener('change', function() {
            if (this.value) {
                const selectedDateTime = new Date(this.value);
                const now = new Date();
                if (selectedDateTime > now) {
                    this.classList.remove('error');
                    document.getElementById('arrival-error').style.display = 'none';
                }
            }
        });
    }
}

// Make functions globally available for onclick handlers
window.validateFlightForm = validateFlightForm;
window.approveFlight = approveFlight;
window.rejectFlight = rejectFlight;
window.deleteFlight = deleteFlight;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initializeFlightValidation); 