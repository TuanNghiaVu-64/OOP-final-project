const planeId = document.currentScript.dataset.planeId;
const seatTypesData = JSON.parse(document.currentScript.dataset.seatTypes);

let selectedSeatId = null;
let selectedSeatTypeId = null;

// Map to store colors for each seat type
const seatTypeColors = new Map();

// Function to generate a stable HSL color based on seat type ID
function generateColor(id) {
    const hue = (id * 137) % 360; // Use a prime number for better distribution
    return `hsl(${hue}, 70%, 70%)`; // Keep saturation and lightness constant
}

// Initialize seat type colors
seatTypesData.forEach(type => {
    if (!seatTypeColors.has(type.id)) {
        seatTypeColors.set(type.id, generateColor(type.id));
    }
});

function updateSeatCardBorder(seatTypeId, isSelected) {
    const seatCard = document.querySelector(`.seat-type-card[data-seat-type-id='${seatTypeId}']`);
    if (seatCard) {
        if (isSelected) {
            const color = seatTypeColors.get(seatTypeId);
            seatCard.style.borderColor = color;
            seatCard.style.borderWidth = '2px';
        } else {
            seatCard.style.borderColor = 'transparent';
            seatCard.style.borderWidth = '1px';
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const seatCards = document.querySelectorAll('.seat-type-card');
    const seats = document.querySelectorAll('.seat');
    const legendContainer = document.getElementById('legend-container');

    // Populate the legend dynamically
    seatTypesData.forEach(type => {
        const color = seatTypeColors.get(type.id);
        const legendItem = document.createElement('div');
        legendItem.className = 'legend-item';
        legendItem.innerHTML = `
            <span class="legend-color" style="background-color: ${color};"></span>
            ${type.name}
        `;
        legendContainer.appendChild(legendItem);
    });

    seatCards.forEach(card => {
        card.addEventListener('click', function() {
            const currentSeatTypeId = this.dataset.seatTypeId;

            if (selectedSeatTypeId === currentSeatTypeId) {
                // Deselect if clicking the already selected card
                updateSeatCardBorder(selectedSeatTypeId, false);
                selectedSeatTypeId = null;
            } else {
                // Deselect previous card if any
                if (selectedSeatTypeId !== null) {
                    updateSeatCardBorder(selectedSeatTypeId, false);
                }
                // Select new card
                selectedSeatTypeId = currentSeatTypeId;
                updateSeatCardBorder(selectedSeatTypeId, true);
            }
        });
    });

    seats.forEach(seat => {
        seat.addEventListener('click', function() {
            const seatElement = this;
            const currentSeatId = seatElement.dataset.seatId;
            const assignedSeatTypeId = seatElement.dataset.assignedSeatTypeId;

            if (assignedSeatTypeId) {
                // Always deselect if already assigned, regardless of current selectedSeatTypeId
                if (confirm('Are you sure you want to unassign this seat?')) {
                    fetch(`/seats/assign/${planeId}/${currentSeatId}/unassign`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
                    .then(response => {
                        if (response.ok) {
                            seatElement.classList.remove('assigned');
                            seatElement.style.backgroundColor = ''; // Remove background color
                            seatElement.dataset.assignedSeatTypeId = ''; // Clear assigned type ID
                            // Optionally update the UI to reflect unassignment more clearly
                        } else {
                            alert('Failed to unassign seat.');
                        }
                    })
                    .catch(error => console.error('Error unassigning seat:', error));
                }
            } else if (selectedSeatTypeId) {
                // Assign if a seat type is selected and seat is not assigned
                fetch(`/seats/assign/${planeId}/${currentSeatId}/${selectedSeatTypeId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (response.ok) {
                        seatElement.classList.add('assigned');
                        seatElement.style.backgroundColor = seatTypeColors.get(selectedSeatTypeId); // Set background color
                        seatElement.dataset.assignedSeatTypeId = selectedSeatTypeId; // Store assigned type ID
                    } else {
                        alert('Failed to assign seat.');
                    }
                })
                .catch(error => console.error('Error assigning seat:', error));
            } else {
                alert('Please select a seat type first.');
            }
        });
    });
}); 