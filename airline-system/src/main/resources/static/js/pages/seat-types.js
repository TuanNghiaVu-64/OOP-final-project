import { api } from '../utils/api.js';

/**
 * Seat Type Management JavaScript
 * Handles all seat type functionality including approval and rejection
 */

// Approve seat type
export async function approveSeatType(seatTypeId, rowNum) {
    if (confirm('Are you sure you want to approve seat type #' + rowNum + '?')) {
        try {
            const response = await api.post(`/seat-types/approve/${seatTypeId}`);
            
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

// Reject seat type
export async function rejectSeatType(seatTypeId, rowNum) {
    if (confirm('Are you sure you want to reject seat type #' + rowNum + '?')) {
        try {
            const response = await api.post(`/seat-types/reject/${seatTypeId}`);
            
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
window.approveSeatType = approveSeatType;
window.rejectSeatType = rejectSeatType; 