package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.model.LocationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event payload for warehouse location events
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationEvent {
    /**
     * Unique event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private EventType eventType;
    
    /**
     * Location ID
     */
    private UUID locationId;
    
    /**
     * Zone ID
     */
    private UUID zoneId;
    
    /**
     * Warehouse ID
     */
    private UUID warehouseId;
    
    /**
     * Location status
     */
    private LocationStatus locationStatus;
    
    /**
     * Event timestamp
     */
    private LocalDateTime timestamp;
} 
