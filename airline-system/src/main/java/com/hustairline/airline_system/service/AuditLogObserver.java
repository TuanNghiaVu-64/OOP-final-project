package com.hustairline.airline_system.service;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Audit log observer implementation
 * Demonstrates Observer Pattern usage
 * Logs all system events for auditing purposes
 */
@Component
public class AuditLogObserver implements NotificationService.NotificationObserver {

    private final DefaultNotificationService notificationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuditLogObserver(DefaultNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        // Register this observer with the notification service
        notificationService.addObserver(this);
    }

    @Override
    public void onEvent(NotificationService.SystemEvent event) {
        // Create audit log entry
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format(
            "[AUDIT] %s | %s | %s (ID: %d) | User: %s | Message: %s",
            timestamp,
            event.getEventType(),
            event.getEntityType(),
            event.getEntityId(),
            event.getUserRole(),
            event.getMessage()
        );

        // In a real application, you would save this to a database or file
        System.out.println(logEntry);
        
        // You could also add logic here to:
        // - Save to audit_logs table in database
        // - Send to external logging service
        // - Trigger additional workflows based on event type
        
        handleSpecificEvents(event);
    }

    private void handleSpecificEvents(NotificationService.SystemEvent event) {
        switch (event.getEventType()) {
            case PLANE_APPROVED:
                System.out.println("[AUDIT] Plane approved - Seats can now be assigned");
                break;
            case FLIGHT_APPROVED:
                System.out.println("[AUDIT] Flight approved - Pricing can now be set");
                break;
            case PRICING_APPROVED:
                System.out.println("[AUDIT] Pricing approved - Flight is now available for booking");
                break;
            case USER_REGISTERED:
                System.out.println("[AUDIT] New user registration - Requires approval");
                break;
            default:
                // General audit logging
                break;
        }
    }

    @Override
    public List<NotificationService.EventType> getInterestedEvents() {
        // This observer is interested in ALL events for complete audit trail
        return Arrays.asList(NotificationService.EventType.values());
    }

    @Override
    public String getObserverName() {
        return "AuditLogObserver";
    }
} 