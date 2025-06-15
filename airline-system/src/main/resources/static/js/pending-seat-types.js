function approveSeatType(seatTypeId, rowNum) {
    if (confirm('Are you sure you want to approve seat type #' + rowNum + '?')) {
        fetch('/seat-types/approve/' + seatTypeId, {
            method: 'POST'
        })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                location.reload();
            } else {
                alert('Error: ' + result);
            }
        })
        .catch(error => {
            alert('Error: ' + error);
        });
    }
}

function rejectSeatType(seatTypeId, rowNum) {
    if (confirm('Are you sure you want to reject seat type #' + rowNum + '?')) {
        fetch('/seat-types/reject/' + seatTypeId, {
            method: 'POST'
        })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                location.reload();
            } else {
                alert('Error: ' + result);
            }
        })
        .catch(error => {
            alert('Error: ' + error);
        });
    }
} 