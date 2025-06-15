document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const usernameError = document.getElementById('username-error');
    const passwordError = document.getElementById('password-error');

    // Form submission validation
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        if (validateForm()) {
            this.submit();
        }
    });

    // Real-time validation for username
    username.addEventListener('input', function() {
        if (this.value.trim()) {
            usernameError.textContent = '';
            this.classList.remove('error');
        }
    });

    // Real-time validation for password
    password.addEventListener('input', function() {
        if (this.value.trim()) {
            passwordError.textContent = '';
            this.classList.remove('error');
        }
    });

    // Form validation function
    function validateForm() {
        let isValid = true;

        // Reset error messages
        usernameError.textContent = '';
        passwordError.textContent = '';
        
        // Username validation
        if (!username.value.trim()) {
            usernameError.textContent = 'Username is required';
            username.classList.add('error');
            isValid = false;
        } else {
            username.classList.remove('error');
        }

        // Password validation
        if (!password.value.trim()) {
            passwordError.textContent = 'Password is required';
            password.classList.add('error');
            isValid = false;
        } else {
            password.classList.remove('error');
        }

        return isValid;
    }
}); 