package com.hustairline.airline_system.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Default implementation of NotificationService
 * Demonstrates Observer Pattern implementation
 * Thread-safe implementation for concurrent access
 */
@Service
public class DefaultNotificationService implements NotificationService {

    // Thread-safe list for observers
    private final List<NotificationObserver> observers = new CopyOnWriteArrayList<>();
    
    // Map to cache which observers are interested in which events (performance optimization)
    private final Map<EventType, List<NotificationObserver>> eventObserverMap = new ConcurrentHashMap<>();

    @Override
    public void addObserver(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            
            // Update event-observer mapping for performance
            for (EventType eventType : observer.getInterestedEvents()) {
                eventObserverMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(observer);
            }
            
            System.out.println("Added observer: " + observer.getObserverName());
        }
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        if (observer != null && observers.remove(observer)) {
            // Remove from event-observer mapping
            for (List<NotificationObserver> observerList : eventObserverMap.values()) {
                observerList.remove(observer);
            }
            
            System.out.println("Removed observer: " + observer.getObserverName());
        }
    }

    @Override
    public void notifyObservers(SystemEvent event) {
        if (event == null) return;

        // Get interested observers for this event type (optimized lookup)
        List<NotificationObserver> interestedObservers = eventObserverMap.get(event.getEventType());
        
        if (interestedObservers != null) {
            for (NotificationObserver observer : interestedObservers) {
                try {
                    observer.onEvent(event);
                } catch (Exception e) {
                    System.err.println("Error notifying observer " + observer.getObserverName() + 
                                     ": " + e.getMessage());
                    // Continue with other observers even if one fails
                }
            }
        }

        // Log the event
        System.out.println("Event broadcasted: " + event.getEventType() + 
                         " for " + event.getEntityType() + 
                         " ID: " + event.getEntityId() + 
                         " by " + event.getUserRole());
    }

    @Override
    public void broadcastEvent(EventType eventType, String entityType, int entityId, 
                              String message, String userRole) {
        SystemEvent event = new SystemEvent(eventType, entityType, entityId, message, userRole);
        notifyObservers(event);
    }

    @Override
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Get observers interested in a specific event type
     * @param eventType the event type
     * @return list of interested observers
     */
    public List<NotificationObserver> getObserversForEvent(EventType eventType) {
        return eventObserverMap.getOrDefault(eventType, List.of());
    }

    /**
     * Check if there are any observers for a specific event type
     * @param eventType the event type
     * @return true if there are observers
     */
    public boolean hasObserversForEvent(EventType eventType) {
        List<NotificationObserver> observers = eventObserverMap.get(eventType);
        return observers != null && !observers.isEmpty();
    }
} 