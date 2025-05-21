// Authentication Module

// Mock user data for demonstration
const mockUsers = [
    { id: 1, username: 'admin', password: 'admin123', role: 'admin', name: 'Admin User' },
    { id: 2, username: 'manager', password: 'manager123', role: 'manager', name: 'Manager User' },
    { id: 3, username: 'customer', password: 'customer123', role: 'customer', name: 'Customer User' }
];

// Auth module
export const auth = {
    /**
     * Attempt to log in with the provided credentials
     * @param {string} username - The username
     * @param {string} password - The password
     * @returns {object|null} - The user object if login successful, null otherwise
     */
    login(username, password) {
        // Find user with matching credentials
        const user = mockUsers.find(u => u.username === username && u.password === password);
        
        if (user) {
            // Store user in local storage (in a real app, store a JWT token)
            const userData = { 
                id: user.id, 
                username: user.username, 
                role: user.role, 
                name: user.name 
            };
            localStorage.setItem('currentUser', JSON.stringify(userData));
            return userData;
        }
        
        return null;
    },
    
    /**
     * Get the current logged in user
     * @returns {object|null} - The current user or null if not logged in
     */
    getCurrentUser() {
        const userJson = localStorage.getItem('currentUser');
        return userJson ? JSON.parse(userJson) : null;
    },
    
    /**
     * Log out the current user
     */
    logout() {
        localStorage.removeItem('currentUser');
    },
    
    /**
     * Check if the current user has a specific role
     * @param {string} role - The role to check
     * @returns {boolean} - True if user has the role, false otherwise
     */
    hasRole(role) {
        const user = this.getCurrentUser();
        return user ? user.role === role : false;
    },
    
    /**
     * Require a specific role to access a page
     * @param {string} role - The required role
     * @returns {boolean} - True if user has the role, false otherwise
     */
    requireRole(role) {
        if (!this.hasRole(role)) {
            // Redirect to login page if not authorized
            window.location.href = '/login';
            return false;
        }
        return true;
    }
}; 