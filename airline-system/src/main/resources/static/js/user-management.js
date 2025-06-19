// User Management JavaScript Functions

async function deleteUser(userId, username) {
    if (!confirm('Are you sure you want to delete user "' + username + '"? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch('/users/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'userId=' + userId
        });

        const result = await response.text();
        let data;
        try {
            data = JSON.parse(result);
        } catch (e) {
            console.error('Failed to parse JSON:', result);
            alert('Error: Server returned invalid response');
            return;
        }

        if (data.success) {
            alert(data.message);
            window.location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error deleting user: ' + error.message);
    }
}

async function approveUser(userId) {
    try {
        const response = await fetch('/users/approve', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'userId=' + userId
        });

        const result = await response.text();
        let data;
        try {
            data = JSON.parse(result);
        } catch (e) {
            console.error('Failed to parse JSON:', result);
            alert('Error: Server returned invalid response');
            return;
        }

        if (data.success) {
            alert(data.message);
            window.location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error approving user: ' + error.message);
    }
}

async function rejectUser(userId) {
    if (!confirm('Are you sure you want to reject this user? This will permanently delete their account.')) {
        return;
    }

    try {
        const response = await fetch('/users/reject', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'userId=' + userId
        });

        const result = await response.text();
        let data;
        try {
            data = JSON.parse(result);
        } catch (e) {
            console.error('Failed to parse JSON:', result);
            alert('Error: Server returned invalid response');
            return;
        }

        if (data.success) {
            alert(data.message);
            window.location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error rejecting user: ' + error.message);
    }
} 