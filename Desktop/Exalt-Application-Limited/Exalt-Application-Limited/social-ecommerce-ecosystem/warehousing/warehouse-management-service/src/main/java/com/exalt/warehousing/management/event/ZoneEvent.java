package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.model.ZoneType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event payload for warehouse zone events
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneEvent {
    /**
     * Unique event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private EventType eventType;
    
    /**
     * Zone ID
     */
    private UUID zoneId;
    
    /**
     * Warehouse ID
     */
    private UUID warehouseId;
    
    /**
     * Zone type
     */
    private ZoneType zoneType;
    
    /**
     * Zone code
     */
    private String zoneCode;
    
    /**
     * Event timestamp
     */
    private LocalDateTime timestamp;
} 
