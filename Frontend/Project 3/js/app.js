// Import modules
import { router } from './router.js';
import { auth } from './auth.js';
import { renderPage } from './render.js';

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    // Check if user is logged in
    const currentUser = auth.getCurrentUser();
    
    if (currentUser) {
        // User is logged in, route to appropriate dashboard
        router.navigate(`/${currentUser.role}/dashboard`);
    } else {
        // User is not logged in, route to login page
        router.navigate('/login');
    }
    
    // Initialize navbar toggle for mobile
    const navbarToggle = document.querySelector('.navbar-toggle');
    const navbarMenu = document.querySelector('.navbar-menu');
    
    if (navbarToggle && navbarMenu) {
        navbarToggle.addEventListener('click', () => {
            navbarMenu.classList.toggle('active');
        });
    }
    
    // Initialize logout button
    document.addEventListener('click', (e) => {
        if (e.target.matches('.logout-btn')) {
            e.preventDefault();
            auth.logout();
            router.navigate('/login');
        }
    });
}); 