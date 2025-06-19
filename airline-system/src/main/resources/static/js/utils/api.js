/**
 * API Utility Functions
 * 
 * This module provides standardized functions for making API calls
 * throughout the airline system. Includes error handling, loading states,
 * and consistent response processing.
 * 
 * @author Airline System Team
 * @version 1.0
 */

/**
 * API Configuration
 * Centralized configuration for API calls
 */
const API_CONFIG = {
    baseURL: '',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-Requested-With': 'XMLHttpRequest'
    }
};

/**
 * HTTP Status Codes
 * Common HTTP status codes for reference
 */
const HTTP_STATUS = {
    OK: 200,
    CREATED: 201,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    INTERNAL_SERVER_ERROR: 500
};

/**
 * Makes a standardized API request with error handling
 * 
 * @param {string} url - The API endpoint URL
 * @param {Object} options - Request options
 * @param {string} options.method - HTTP method (GET, POST, PUT, DELETE)
 * @param {Object|FormData} options.data - Request data
 * @param {Object} options.headers - Additional headers
 * @param {boolean} options.showLoading - Whether to show loading indicator
 * @returns {Promise<Object>} Response data or error object
 * 
 * @example
 * // Simple GET request
 * const data = await apiRequest('/api/flights');
 * 
 * // POST request with data
 * const result = await apiRequest('/api/flights', {
 *     method: 'POST',
 *     data: { name: 'Flight 123' }
 * });
 */
async function apiRequest(url, options = {}) {
    const {
        method = 'GET',
        data = null,
        headers = {},
        showLoading = true
    } = options;

    // Show loading indicator if requested
    if (showLoading) {
        showLoadingIndicator();
    }

    try {
        // Prepare request configuration
        const config = {
            method: method.toUpperCase(),
            headers: {
                ...API_CONFIG.headers,
                ...headers
            }
        };

        // Add request body for non-GET requests
        if (data && method.toUpperCase() !== 'GET') {
            if (data instanceof FormData) {
                config.body = data;
                // Remove Content-Type header for FormData (browser sets it automatically)
                delete config.headers['Content-Type'];
            } else if (typeof data === 'object') {
                // Convert object to URL-encoded string
                config.body = new URLSearchParams(data).toString();
            } else {
                config.body = data;
            }
        }

        // Make the request
        const response = await fetch(url, config);

        // Handle different response types
        let responseData;
        const contentType = response.headers.get('content-type');
        
        if (contentType && contentType.includes('application/json')) {
            responseData = await response.json();
        } else {
            responseData = await response.text();
        }

        // Check if request was successful
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${responseData.message || responseData}`);
        }

        return {
            success: true,
            data: responseData,
            status: response.status
        };

    } catch (error) {
        console.error('API Request Error:', error);
        
        return {
            success: false,
            error: error.message,
            status: error.status || 0
        };
    } finally {
        // Hide loading indicator
        if (showLoading) {
            hideLoadingIndicator();
        }
    }
}

/**
 * Convenience method for GET requests
 * 
 * @param {string} url - The API endpoint URL
 * @param {Object} options - Additional options
 * @returns {Promise<Object>} Response data
 */
async function apiGet(url, options = {}) {
    return apiRequest(url, { ...options, method: 'GET' });
}

/**
 * Convenience method for POST requests
 * 
 * @param {string} url - The API endpoint URL
 * @param {Object|FormData} data - Request data
 * @param {Object} options - Additional options
 * @returns {Promise<Object>} Response data
 */
async function apiPost(url, data, options = {}) {
    return apiRequest(url, { ...options, method: 'POST', data });
}

/**
 * Convenience method for PUT requests
 * 
 * @param {string} url - The API endpoint URL
 * @param {Object|FormData} data - Request data
 * @param {Object} options - Additional options
 * @returns {Promise<Object>} Response data
 */
async function apiPut(url, data, options = {}) {
    return apiRequest(url, { ...options, method: 'PUT', data });
}

/**
 * Convenience method for DELETE requests
 * 
 * @param {string} url - The API endpoint URL
 * @param {Object} options - Additional options
 * @returns {Promise<Object>} Response data
 */
async function apiDelete(url, options = {}) {
    return apiRequest(url, { ...options, method: 'DELETE' });
}

/**
 * Shows a loading indicator to the user
 * Can be customized based on UI framework
 */
function showLoadingIndicator() {
    // Add loading class to body or show spinner
    document.body.classList.add('loading');
    
    // Create or show loading overlay
    let loader = document.getElementById('api-loader');
    if (!loader) {
        loader = document.createElement('div');
        loader.id = 'api-loader';
        loader.className = 'api-loader';
        loader.innerHTML = '<div class="spinner"></div>';
        document.body.appendChild(loader);
    }
    loader.style.display = 'flex';
}

/**
 * Hides the loading indicator
 */
function hideLoadingIndicator() {
    document.body.classList.remove('loading');
    
    const loader = document.getElementById('api-loader');
    if (loader) {
        loader.style.display = 'none';
    }
}

/**
 * Handles API errors with user-friendly messages
 * 
 * @param {Object} error - Error object from API request
 * @param {string} defaultMessage - Default message if error is generic
 */
function handleApiError(error, defaultMessage = 'An error occurred') {
    let message = defaultMessage;
    
    if (error.error) {
        message = error.error;
    }
    
    // Show error message to user
    showErrorMessage(message);
    
    // Log error for debugging
    console.error('API Error:', error);
}

/**
 * Shows an error message to the user
 * 
 * @param {string} message - Error message to display
 */
function showErrorMessage(message) {
    // Create or update error message element
    let errorElement = document.getElementById('api-error');
    if (!errorElement) {
        errorElement = document.createElement('div');
        errorElement.id = 'api-error';
        errorElement.className = 'flash-message flash-error';
        errorElement.style.position = 'fixed';
        errorElement.style.top = '20px';
        errorElement.style.right = '20px';
        errorElement.style.zIndex = '9999';
        errorElement.style.maxWidth = '400px';
        document.body.appendChild(errorElement);
    }
    
    errorElement.textContent = message;
    errorElement.style.display = 'block';
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        if (errorElement && document.body.contains(errorElement)) {
            errorElement.style.display = 'none';
        }
    }, 5000);
}

/**
 * Shows a success message to the user
 * 
 * @param {string} message - Success message to display
 */
function showSuccessMessage(message) {
    // Create or update success message element
    let successElement = document.getElementById('api-success');
    if (!successElement) {
        successElement = document.createElement('div');
        successElement.id = 'api-success';
        successElement.className = 'flash-message flash-success';
        successElement.style.position = 'fixed';
        successElement.style.top = '20px';
        successElement.style.right = '20px';
        successElement.style.zIndex = '9999';
        successElement.style.maxWidth = '400px';
        document.body.appendChild(successElement);
    }
    
    successElement.textContent = message;
    successElement.style.display = 'block';
    
    // Auto-hide after 3 seconds
    setTimeout(() => {
        if (successElement && document.body.contains(successElement)) {
            successElement.style.display = 'none';
        }
    }, 3000);
}

/**
 * Simple API class for backward compatibility with existing code
 */
class SimpleAPI {
    async post(url, data = {}) {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: data instanceof FormData ? data : new URLSearchParams(data)
        });
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return response.text();
    }
    
    async postJson(url, data = {}) {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: new URLSearchParams(data)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    }
}

// Create instance for export
export const api = new SimpleAPI();

// Export functions for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        apiRequest,
        apiGet,
        apiPost,
        apiPut,
        apiDelete,
        handleApiError,
        showErrorMessage,
        showSuccessMessage,
        HTTP_STATUS,
        api
    };
} 