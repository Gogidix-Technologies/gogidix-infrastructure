package com.exalt.warehousing.fulfillment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event representing a change in warehouse information
 * This is used to consume events from the Warehouse Management Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseEvent {
    
    /**
     * Unique event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private String eventType;
    
    /**
     * ID of the warehouse
     */
    private UUID warehouseId;
    
    /**
     * ID of the zone (if applicable)
     */
    private UUID zoneId;
    
    /**
     * ID of the location (if applicable)
     */
    private UUID locationId;
    
    /**
     * Warehouse code
     */
    private String warehouseCode;
    
    /**
     * Zone code (if applicable)
     */
    private String zoneCode;
    
    /**
     * Status of the warehouse/zone/location
     */
    private String status;
    
    /**
     * Time the event was generated
     */
    private LocalDateTime timestamp;
} 
