function validateForm() {
    const sizeSelect = document.getElementById('size');
    const sizeError = document.getElementById('size-error');
    
    // Reset previous error state
    sizeSelect.classList.remove('error');
    sizeError.style.display = 'none';
    
    // Check if a valid size (not placeholder) is selected
    if (sizeSelect.value === 'placeholder') {
        sizeSelect.classList.add('error');
        sizeError.style.display = 'block';
        return false;
    }
    
    return true;
}

// Add event listener to hide error message when user selects a size
document.addEventListener('DOMContentLoaded', function() {
    const sizeSelect = document.getElementById('size');
    if (sizeSelect) {
        sizeSelect.addEventListener('change', function() {
            if (this.value !== 'placeholder') {
                this.classList.remove('error');
                document.getElementById('size-error').style.display = 'none';
            } else {
                this.classList.add('error');
                document.getElementById('size-error').style.display = 'block';
            }
        });
    }
}); 