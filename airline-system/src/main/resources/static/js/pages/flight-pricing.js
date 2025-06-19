import { api } from '../utils/api.js';

/**
 * Flight Pricing JavaScript
 * Handles all flight pricing functionality including validation, approval, and form handling
 */

// Pricing form validation
export function validatePricesForm() {
    let isValid = true;
    const priceInputs = document.querySelectorAll('input[name="price"]');
    
    // Clear previous errors
    document.querySelectorAll('.field-error').forEach(error => {
        error.style.display = 'none';
    });
    priceInputs.forEach(input => {
        input.classList.remove('error');
    });
    
    // Since all seat types are required for this aircraft, all prices must be filled
    priceInputs.forEach((input, index) => {
        const value = input.value.trim();
        const errorSpan = document.getElementById(`price-error-${index}`);
        
        if (!value) {
            input.classList.add('error');
            errorSpan.textContent = 'Price is required for this seat type';
            errorSpan.style.display = 'block';
            isValid = false;
        } else {
            const price = parseFloat(value);
            if (isNaN(price) || price <= 0) {
                input.classList.add('error');
                errorSpan.textContent = 'Price must be greater than 0';
                errorSpan.style.display = 'block';
                isValid = false;
            }
        }
    });
    
    if (!isValid) {
        alert('Please set valid prices for all seat types used on this aircraft.');
    }
    
    return isValid;
}

// Approve all pricing for a flight
export async function approveAllForFlight(flightId) {
    if (confirm('Are you sure you want to approve ALL seat type pricing for this flight?')) {
        try {
            const response = await api.postJson(`/flight-pricing/approve-flight/${flightId}`);
            
            if (response.success) {
                showSuccessMessage(response.message);
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
            } else {
                alert('Error: ' + response.message);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while processing your request.');
        }
    }
}

// Reject all pricing for a flight
export async function rejectAllForFlight(flightId) {
    if (confirm('Are you sure you want to reject ALL seat type pricing for this flight? This will delete all pricing for this flight.')) {
        try {
            const response = await api.postJson(`/flight-pricing/reject-flight/${flightId}`);
            
            if (response.success) {
                showSuccessMessage(response.message);
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
            } else {
                alert('Error: ' + response.message);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while processing your request.');
        }
    }
}

// Show success message
export function showSuccessMessage(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'flash-message flash-success';
    successDiv.textContent = message;
    successDiv.style.position = 'fixed';
    successDiv.style.top = '20px';
    successDiv.style.right = '20px';
    successDiv.style.zIndex = '9999';
    successDiv.style.maxWidth = '400px';
    document.body.appendChild(successDiv);
    
    setTimeout(() => {
        if (document.body.contains(successDiv)) {
            document.body.removeChild(successDiv);
        }
    }, 3000);
}

// Initialize pricing form validation
export function initializePricingValidation() {
    const priceInputs = document.querySelectorAll('input[name="price"]');
    
    priceInputs.forEach((input, index) => {
        input.addEventListener('input', function() {
            const errorSpan = document.getElementById(`price-error-${index}`);
            const value = this.value.trim();
            
            if (value) {
                const price = parseFloat(value);
                if (!isNaN(price) && price > 0) {
                    this.classList.remove('error');
                    errorSpan.style.display = 'none';
                }
            }
        });
    });
}

// Make functions globally available for onclick handlers
window.validatePricesForm = validatePricesForm;
window.approveAllForFlight = approveAllForFlight;
window.rejectAllForFlight = rejectAllForFlight;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initializePricingValidation); 