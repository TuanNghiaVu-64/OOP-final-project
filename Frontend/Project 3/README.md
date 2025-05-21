# SkyBooker - Flight Booking System

A modern, responsive frontend design for a flight booking web application that supports three user roles: Admin, Manager, and Customer.

## Project Overview

SkyBooker is a flight booking system that demonstrates role-based access control and different user interfaces for:

1. **Admins**: Manage flights, aircraft, and seat configurations
2. **Managers**: Review and approve seat configurations
3. **Customers**: Search flights, select seats, and book tickets

## Features

### Shared Features
- Role-based login system
- User-friendly dashboard
- Responsive design

### Admin Features
- Flight management (Add/Edit/Delete)
- Aircraft management
- Seat type management
- Seat configuration with visual grid interface

### Manager Features
- Review pending seat configurations
- Analyze revenue impact
- Approve or reject configuration changes

### Customer Features
- Flight search functionality
- Visual seat selection
- Booking process with payment
- My Tickets section

## Project Structure

```
Project 3/
├── css/
│   └── styles.css       # Main CSS styles with utility classes
├── js/
│   ├── app.js           # Main application entry point
│   ├── auth.js          # Authentication module
│   ├── router.js        # Routing module for page navigation
│   ├── render.js        # UI rendering module
│   ├── components.js    # Component imports and exports
│   └── components/      # Page components organized by role
│       ├── admin.js     # Admin-specific components
│       ├── manager.js   # Manager-specific components
│       ├── customer.js  # Customer dashboard and search components
│       └── customer-booking.js # Customer booking flow components
├── index.html           # Main HTML entry point
└── README.md            # Project documentation
```

## Technologies Used

- HTML5
- CSS3 with custom variables for theming
- JavaScript (ES6+)
- Font Awesome for icons
- Responsive design with mobile-first approach

## How to Run

1. Clone the repository
2. Open the `index.html` file in a web browser

No build steps or external dependencies are required to run the application.

## Demo Credentials

The application comes with pre-configured demo accounts:

- **Admin**: Username: `admin`, Password: `admin123`
- **Manager**: Username: `manager`, Password: `manager123`
- **Customer**: Username: `customer`, Password: `customer123`

## Design Approach

The application was designed with these principles in mind:

1. **Responsive Design**: Works on all device sizes from mobile to desktop
2. **Role-Based Navigation**: Different interfaces based on user role
3. **Visual Components**: Intuitive interfaces for complex tasks like seat selection
4. **Clean UI**: Modern, minimalist approach focusing on usability

## Developer Notes

This is a frontend-only application with mock data for demonstration purposes. In a real-world implementation, it would connect to a backend API for data persistence and real-time updates.

The app uses a simple component-based architecture without any frameworks, demonstrating vanilla JavaScript capabilities for building interactive applications.

## Deployment

To deploy this application to a hosting service:

1. Upload all the files maintaining the directory structure
2. No build process is required as the application uses plain HTML, CSS, and JavaScript
3. The application can be hosted on any static web hosting service like:
   - GitHub Pages
   - Netlify
   - Vercel
   - Amazon S3
   - Any standard web hosting provider

## Future Enhancements

Potential improvements for future iterations:

1. Integration with a backend API
2. User registration functionality
3. Real-time seat availability updates
4. Email notifications for bookings
5. Payment gateway integration
6. Flight filtering and sorting options
7. Localization support for multiple languages
8. Dark mode theme option 