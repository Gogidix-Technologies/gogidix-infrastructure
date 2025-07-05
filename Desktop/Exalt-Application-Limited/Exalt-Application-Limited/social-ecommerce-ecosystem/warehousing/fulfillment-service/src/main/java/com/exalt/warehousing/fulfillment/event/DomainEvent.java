package com.exalt.warehousing.fulfillment.event;

import com.exalt.warehousing.shared.events.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

/**
 * Domain event base class for fulfillment specific events.
 * This provides a standardized structure for all domain-specific events
 * within the fulfillment module.
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class DomainEvent extends BaseEvent {

    private EventCategory category;
    private String originService;
    private UUID entityId;
    private String entityType;
    private String action;
    private LocalDateTime processingTimestamp;
    private boolean replayable = true;
    
    /**
     * Categorization of fulfillment domain events
     */
    public enum EventCategory {
        PICKING,
        PACKING,
        SHIPPING,
        INVENTORY,
        RETURN,
        QA,
        SYSTEM
    }
    
    /**
     * Constructor with basic fields
     */
    protected DomainEvent(String eventType, EventCategory category, 
                        UUID entityId, String entityType, String action) {
        super(eventType, entityId.toString(), entityType);
        this.category = category;
        this.entityId = entityId;
        this.entityType = entityType;
        this.action = action;
        this.originService = "warehousing-fulfillment-service";
        this.processingTimestamp = LocalDateTime.now();
    }
}