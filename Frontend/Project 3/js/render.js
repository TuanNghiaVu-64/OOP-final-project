// Render Module

import { auth } from './auth.js';
import { router } from './router.js';
import { components, login, notFound } from './components.js';

// App container
const appContainer = document.getElementById('app');

/**
 * Render a page
 * @param {object} route - The route object
 */
export function renderPage(route) {
    // Get component name from route
    const componentName = route.component;
    
    // Get current user
    const currentUser = auth.getCurrentUser();
    
    // Create page container
    const pageContainer = document.createElement('div');
    pageContainer.className = 'page';
    
    // Add header for authenticated users
    if (currentUser && route.path !== '/login') {
        const header = createHeader(currentUser);
        pageContainer.appendChild(header);
    }
    
    // Create content container
    const contentContainer = document.createElement('div');
    contentContainer.className = 'container';
    
    // Render component
    if (componentName === 'login') {
        contentContainer.innerHTML = login();
    } else if (componentName === '404') {
        contentContainer.innerHTML = notFound();
    } else if (components[componentName]) {
        const componentContent = components[componentName](route.params);
        contentContainer.innerHTML = componentContent;
    } else {
        contentContainer.innerHTML = '<div class="card"><h2>Page not found</h2><p>The requested page could not be found.</p></div>';
    }
    
    // Add content to page
    pageContainer.appendChild(contentContainer);
    
    // Add footer
    const footer = createFooter();
    pageContainer.appendChild(footer);
    
    // Replace app content
    appContainer.innerHTML = '';
    appContainer.appendChild(pageContainer);
    
    // Initialize any event listeners for the page
    initializePageEvents(componentName, route.params);
}

/**
 * Create header with navigation
 * @param {object} user - The current user
 * @returns {HTMLElement} - The header element
 */
function createHeader(user) {
    const header = document.createElement('header');
    
    // Create navigation based on user role
    let navItems = '';
    
    if (user.role === 'admin') {
        navItems = `
            <li class="navbar-item"><a href="#" data-route="/admin/dashboard" class="navbar-link">Dashboard</a></li>
            <li class="navbar-item"><a href="#" data-route="/admin/flights" class="navbar-link">Flights</a></li>
            <li class="navbar-item"><a href="#" data-route="/admin/aircraft" class="navbar-link">Aircraft</a></li>
            <li class="navbar-item"><a href="#" data-route="/admin/seat-types" class="navbar-link">Seat Types</a></li>
        `;
    } else if (user.role === 'manager') {
        navItems = `
            <li class="navbar-item"><a href="#" data-route="/manager/dashboard" class="navbar-link">Dashboard</a></li>
            <li class="navbar-item"><a href="#" data-route="/manager/approval-requests" class="navbar-link">Approvals</a></li>
        `;
    } else if (user.role === 'customer') {
        navItems = `
            <li class="navbar-item"><a href="#" data-route="/customer/dashboard" class="navbar-link">Dashboard</a></li>
            <li class="navbar-item"><a href="#" data-route="/customer/search" class="navbar-link">Find Flights</a></li>
            <li class="navbar-item"><a href="#" data-route="/customer/tickets" class="navbar-link">My Tickets</a></li>
        `;
    }
    
    // Add logout to all roles
    navItems += `<li class="navbar-item"><a href="#" class="navbar-link logout-btn">Logout</a></li>`;
    
    // Create navbar
    header.innerHTML = `
        <nav class="navbar">
            <div class="navbar-brand">SkyBooker</div>
            <div class="navbar-toggle">
                <i class="fas fa-bars"></i>
            </div>
            <ul class="navbar-menu">
                ${navItems}
            </ul>
        </nav>
    `;
    
    return header;
}

/**
 * Create footer
 * @returns {HTMLElement} - The footer element
 */
function createFooter() {
    const footer = document.createElement('footer');
    footer.className = 'footer';
    footer.innerHTML = `
        <div class="container">
            <p class="text-center text-gray">&copy; ${new Date().getFullYear()} SkyBooker Flight Booking System</p>
        </div>
    `;
    return footer;
}

/**
 * Initialize event listeners for a specific page
 * @param {string} componentName - The component name
 * @param {object} params - Route parameters
 */
function initializePageEvents(componentName, params) {
    // Login page events
    if (componentName === 'login') {
        const loginForm = document.getElementById('login-form');
        const roleOptions = document.querySelectorAll('.role-option');
        
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => {
                e.preventDefault();
                
                const username = document.getElementById('username').value;
                const password = document.getElementById('password').value;
                const selectedRole = document.querySelector('.role-option.active')?.getAttribute('data-role');
                
                if (!username || !password || !selectedRole) {
                    alert('Please fill in all fields and select a role.');
                    return;
                }
                
                // Attempt login
                const user = auth.login(username, password);
                
                if (user && user.role === selectedRole) {
                    router.navigate(`/${user.role}/dashboard`);
                } else {
                    alert('Invalid credentials or role mismatch.');
                }
            });
        }
        
        // Role selection
        if (roleOptions) {
            roleOptions.forEach(option => {
                option.addEventListener('click', () => {
                    // Remove active class from all options
                    roleOptions.forEach(opt => opt.classList.remove('active'));
                    // Add active class to clicked option
                    option.classList.add('active');
                });
            });
        }
    }
    
    // Admin flight form events
    if (componentName === 'admin/flight-form') {
        const flightForm = document.getElementById('flight-form');
        
        if (flightForm) {
            flightForm.addEventListener('submit', (e) => {
                e.preventDefault();
                
                // Handle form submission (would save data in a real app)
                alert('Flight saved successfully!');
                router.navigate('/admin/flights');
            });
        }
    }
    
    // Admin seat configuration events
    if (componentName === 'admin/seat-configuration') {
        const seatMap = document.querySelector('.seat-map');
        const seatTypeSelect = document.getElementById('seat-type');
        const saveConfigBtn = document.getElementById('save-config');
        
        if (seatMap && seatTypeSelect) {
            // Handle seat click
            seatMap.addEventListener('click', (e) => {
                if (e.target.classList.contains('seat') && !e.target.classList.contains('aisle')) {
                    // Remove previous seat type classes
                    e.target.classList.remove('seat-first-class', 'seat-business', 'seat-economy');
                    
                    // Add selected seat type class
                    const selectedType = seatTypeSelect.value;
                    e.target.classList.add(`seat-${selectedType}`);
                    e.target.setAttribute('data-type', selectedType);
                }
            });
        }
        
        if (saveConfigBtn) {
            saveConfigBtn.addEventListener('click', () => {
                // Would save configuration in a real app
                alert('Configuration saved as pending. Awaiting manager approval.');
                router.navigate('/admin/flights');
            });
        }
    }
    
    // Manager seat review events
    if (componentName === 'manager/seat-review') {
        const approveBtn = document.getElementById('approve-config');
        const rejectBtn = document.getElementById('reject-config');
        
        if (approveBtn) {
            approveBtn.addEventListener('click', () => {
                alert('Configuration approved.');
                router.navigate('/manager/approval-requests');
            });
        }
        
        if (rejectBtn) {
            rejectBtn.addEventListener('click', () => {
                alert('Configuration rejected.');
                router.navigate('/manager/approval-requests');
            });
        }
    }
    
    // Customer seat selection events
    if (componentName === 'customer/select-seat') {
        const seatMap = document.querySelector('.seat-map');
        const continueBtn = document.getElementById('continue-btn');
        
        if (seatMap) {
            seatMap.addEventListener('click', (e) => {
                if (e.target.classList.contains('seat') && 
                    !e.target.classList.contains('aisle') && 
                    !e.target.classList.contains('seat-occupied')) {
                    
                    // Remove selected class from all seats
                    const seats = document.querySelectorAll('.seat');
                    seats.forEach(seat => seat.classList.remove('seat-selected'));
                    
                    // Add selected class to clicked seat
                    e.target.classList.add('seat-selected');
                    
                    // Enable continue button
                    if (continueBtn) {
                        continueBtn.disabled = false;
                    }
                }
            });
        }
        
        if (continueBtn) {
            continueBtn.addEventListener('click', () => {
                const flightId = params.id;
                router.navigate(`/customer/trip-overview/${flightId}`);
            });
        }
    }
    
    // Customer payment page events
    if (componentName === 'customer/payment') {
        const paymentForm = document.getElementById('payment-form');
        
        if (paymentForm) {
            paymentForm.addEventListener('submit', (e) => {
                e.preventDefault();
                
                const flightId = params.id;
                router.navigate(`/customer/confirmation/${flightId}`);
            });
        }
    }
} 