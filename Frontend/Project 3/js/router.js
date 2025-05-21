// Router Module

import { renderPage } from './render.js';
import { auth } from './auth.js';

// Routes configuration
const routes = [
    // Shared routes
    { path: '/login', component: 'login', access: 'public' },
    
    // Admin routes
    { path: '/admin/dashboard', component: 'admin/dashboard', access: 'admin' },
    { path: '/admin/flights', component: 'admin/flights', access: 'admin' },
    { path: '/admin/flights/new', component: 'admin/flight-form', access: 'admin' },
    { path: '/admin/flights/edit/:id', component: 'admin/flight-form', access: 'admin' },
    { path: '/admin/seat-configuration/:id', component: 'admin/seat-configuration', access: 'admin' },
    { path: '/admin/aircraft', component: 'admin/aircraft', access: 'admin' },
    { path: '/admin/seat-types', component: 'admin/seat-types', access: 'admin' },
    
    // Manager routes
    { path: '/manager/dashboard', component: 'manager/dashboard', access: 'manager' },
    { path: '/manager/approval-requests', component: 'manager/approval-requests', access: 'manager' },
    { path: '/manager/seat-review/:id', component: 'manager/seat-review', access: 'manager' },
    
    // Customer routes
    { path: '/customer/dashboard', component: 'customer/dashboard', access: 'customer' },
    { path: '/customer/search', component: 'customer/search', access: 'customer' },
    { path: '/customer/search-results', component: 'customer/search-results', access: 'customer' },
    { path: '/customer/select-seat/:id', component: 'customer/select-seat', access: 'customer' },
    { path: '/customer/trip-overview/:id', component: 'customer/trip-overview', access: 'customer' },
    { path: '/customer/payment/:id', component: 'customer/payment', access: 'customer' },
    { path: '/customer/confirmation/:id', component: 'customer/confirmation', access: 'customer' },
    { path: '/customer/tickets', component: 'customer/tickets', access: 'customer' },
    
    // Default route (404)
    { path: '404', component: '404', access: 'public' }
];

// Router module
export const router = {
    // Current route
    currentRoute: null,
    
    /**
     * Initialize router
     */
    init() {
        // Handle initial route
        this.handleRouteChange();
        
        // Listen for popstate events (browser back/forward)
        window.addEventListener('popstate', () => {
            this.handleRouteChange();
        });
        
        // Handle links with data-route attribute
        document.addEventListener('click', (e) => {
            if (e.target.matches('[data-route]')) {
                e.preventDefault();
                const path = e.target.getAttribute('data-route');
                this.navigate(path);
            }
        });
    },
    
    /**
     * Navigate to a specific route
     * @param {string} path - The path to navigate to
     * @param {object} params - Route parameters (optional)
     */
    navigate(path, params = {}) {
        // Update browser history
        window.history.pushState(params, '', path);
        
        // Handle route change
        this.handleRouteChange();
    },
    
    /**
     * Handle route change
     */
    handleRouteChange() {
        // Get current path from URL
        const path = window.location.pathname || '/';
        
        // Find matching route
        let matchedRoute = null;
        let routeParams = {};
        
        for (const route of routes) {
            // Handle route with parameters
            if (route.path.includes(':')) {
                const pathParts = path.split('/');
                const routeParts = route.path.split('/');
                
                if (pathParts.length === routeParts.length) {
                    let match = true;
                    
                    for (let i = 0; i < routeParts.length; i++) {
                        if (routeParts[i].startsWith(':')) {
                            // Extract parameter
                            const paramName = routeParts[i].substring(1);
                            routeParams[paramName] = pathParts[i];
                        } else if (routeParts[i] !== pathParts[i]) {
                            match = false;
                            break;
                        }
                    }
                    
                    if (match) {
                        matchedRoute = route;
                        break;
                    }
                }
            } 
            // Handle exact route match
            else if (route.path === path) {
                matchedRoute = route;
                break;
            }
        }
        
        // If no route found, use 404
        if (!matchedRoute) {
            matchedRoute = routes.find(r => r.path === '404');
        }
        
        // Check route access
        if (matchedRoute.access !== 'public') {
            const user = auth.getCurrentUser();
            
            if (!user || user.role !== matchedRoute.access) {
                // Redirect to login page
                this.navigate('/login');
                return;
            }
        }
        
        // Update current route
        this.currentRoute = {
            ...matchedRoute,
            params: routeParams
        };
        
        // Render the page
        renderPage(this.currentRoute);
    }
};

// Initialize router when module is loaded
router.init(); 