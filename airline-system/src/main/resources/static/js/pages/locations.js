import { api } from '../utils/api.js';

/**
 * Location Management JavaScript
 * Handles all location-related functionality including validation, deletion, and form handling
 */

// Location form validation
export function validateLocationForm() {
    const cityInput = document.getElementById('city');
    const cityError = document.getElementById('city-error');
    const countryInput = document.getElementById('country');
    const countryError = document.getElementById('country-error');
    let isValid = true;
    
    // Reset previous error states
    cityInput.classList.remove('error');
    cityError.style.display = 'none';
    countryInput.classList.remove('error');
    countryError.style.display = 'none';
    
    // Check if city is not empty (trim whitespace)
    if (!cityInput.value.trim()) {
        cityInput.classList.add('error');
        cityError.style.display = 'block';
        isValid = false;
    }
    
    // Check if country is not empty (trim whitespace)
    if (!countryInput.value.trim()) {
        countryInput.classList.add('error');
        countryError.style.display = 'block';
        isValid = false;
    }
    
    return isValid;
}

// Delete location with confirmation
export async function deleteLocation(locationId) {
    if (!confirm('Are you sure you want to delete this location? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await api.post(`/locations/delete/${locationId}`);
        
        if (response === 'success') {
            location.reload();
        } else {
            alert('Error deleting location: ' + response.replace('error: ', ''));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error deleting location');
    }
}

// Initialize location form validation
export function initializeLocationValidation() {
    const cityInput = document.getElementById('city');
    const countryInput = document.getElementById('country');
    
    if (cityInput) {
        cityInput.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('error');
                document.getElementById('city-error').style.display = 'none';
            }
        });
    }
    
    if (countryInput) {
        countryInput.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('error');
                document.getElementById('country-error').style.display = 'none';
            }
        });
    }
}

// Make functions globally available for onclick handlers
window.validateLocationForm = validateLocationForm;
window.deleteLocation = deleteLocation;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initializeLocationValidation); 