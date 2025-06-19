package com.hustairline.airline_system.service;

import java.util.List;

/**
 * Interface for notification services
 * Demonstrates Observer Pattern usage
 * Allows decoupled notification handling for system events
 */
public interface NotificationService {

    /**
     * Enum for different types of system events
     */
    enum EventType {
        USER_REGISTERED, USER_APPROVED, USER_REJECTED,
        PLANE_ADDED, PLANE_APPROVED, PLANE_REJECTED,
        FLIGHT_ADDED, FLIGHT_APPROVED, FLIGHT_REJECTED,
        SEAT_TYPE_ADDED, SEAT_TYPE_APPROVED, SEAT_TYPE_REJECTED,
        PRICING_SET, PRICING_APPROVED, PRICING_REJECTED,
        BOOKING_CREATED, BOOKING_CANCELLED
    }

    /**
     * Event data container
     */
    class SystemEvent {
        private final EventType eventType;
        private final String entityType;
        private final int entityId;
        private final String message;
        private final String userRole;
        private final long timestamp;

        public SystemEvent(EventType eventType, String entityType, int entityId, 
                          String message, String userRole) {
            this.eventType = eventType;
            this.entityType = entityType;
            this.entityId = entityId;
            this.message = message;
            this.userRole = userRole;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public EventType getEventType() { return eventType; }
        public String getEntityType() { return entityType; }
        public int getEntityId() { return entityId; }
        public String getMessage() { return message; }
        public String getUserRole() { return userRole; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * Observer interface for notification listeners
     */
    interface NotificationObserver {
        /**
         * Handle a system event
         * @param event the event that occurred
         */
        void onEvent(SystemEvent event);

        /**
         * Get the types of events this observer is interested in
         * @return list of event types
         */
        List<EventType> getInterestedEvents();

        /**
         * Get the name of this observer (for debugging)
         * @return observer name
         */
        String getObserverName();
    }

    /**
     * Register an observer for notifications
     * @param observer the observer to register
     */
    void addObserver(NotificationObserver observer);

    /**
     * Remove an observer
     * @param observer the observer to remove
     */
    void removeObserver(NotificationObserver observer);

    /**
     * Notify all interested observers of an event
     * @param event the event to broadcast
     */
    void notifyObservers(SystemEvent event);

    /**
     * Create and broadcast a system event
     * @param eventType the type of event
     * @param entityType the entity type affected
     * @param entityId the entity ID
     * @param message the event message
     * @param userRole the role of the user who triggered the event
     */
    void broadcastEvent(EventType eventType, String entityType, int entityId, 
                       String message, String userRole);

    /**
     * Get count of registered observers
     * @return number of observers
     */
    int getObserverCount();
} 