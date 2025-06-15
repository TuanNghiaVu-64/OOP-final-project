import { api } from '../utils/api.js';

/**
 * Plane Management JavaScript
 * Handles all plane-related functionality including approval and rejection
 */

// Approve plane
export async function approvePlane(planeId, rowNum) {
    if (confirm('Are you sure you want to approve plane #' + rowNum + '?')) {
        try {
            const response = await api.post(`/planes/approve/${planeId}`);
            
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

// Reject plane
export async function rejectPlane(planeId, rowNum) {
    if (confirm('Are you sure you want to reject plane #' + rowNum + '?')) {
        try {
            const response = await api.post(`/planes/reject/${planeId}`);
            
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

// Make functions globally available for onclick handlers
window.approvePlane = approvePlane;
window.rejectPlane = rejectPlane; 