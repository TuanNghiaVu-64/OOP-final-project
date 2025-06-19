// Registration Form Validation

function validateForm() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // Clear previous errors
    document.getElementById('username-error').textContent = '';
    document.getElementById('password-error').textContent = '';
    document.getElementById('confirm-password-error').textContent = '';

    let isValid = true;

    if (username.length < 3) {
        document.getElementById('username-error').textContent = 'Username must be at least 3 characters long';
        isValid = false;
    }

    if (password.length < 6) {
        document.getElementById('password-error').textContent = 'Password must be at least 6 characters long';
        isValid = false;
    }

    if (password !== confirmPassword) {
        document.getElementById('confirm-password-error').textContent = 'Passwords do not match';
        isValid = false;
    }

    return isValid;
}

// Initialize registration form validation when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Add form validation on submit
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            if (!validateForm()) {
                e.preventDefault();
            }
        });
    }

    // Real-time password confirmation validation
    const confirmPasswordField = document.getElementById('confirmPassword');
    if (confirmPasswordField) {
        confirmPasswordField.addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            const errorSpan = document.getElementById('confirm-password-error');
            
            if (confirmPassword && password !== confirmPassword) {
                errorSpan.textContent = 'Passwords do not match';
                this.style.borderColor = '#dc3545';
            } else {
                errorSpan.textContent = '';
                this.style.borderColor = '';
            }
        });
    }
}); 