function clearFilters() {
    document.getElementById('originId').value = '';
    document.getElementById('destinationId').value = '';
    document.getElementById('departureDate').value = '';
    document.querySelector('.search-form').submit();
}

// Back to top functionality
document.addEventListener('DOMContentLoaded', function() {
    const backToTopBtn = document.getElementById('backToTopBtn');
    
    // Show button immediately for testing, then use scroll-based logic
    setTimeout(function() {
        backToTopBtn.classList.add('show');
    }, 1000);
    
    // Show/hide button based on scroll position
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 200) {
            backToTopBtn.classList.add('show');
        } else {
            backToTopBtn.classList.remove('show');
        }
    });
    
    // Smooth scroll to top when clicked
    backToTopBtn.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
    
    // Also show button if page content is tall enough
    if (document.body.scrollHeight > window.innerHeight + 400) {
        backToTopBtn.classList.add('show');
    }
}); 