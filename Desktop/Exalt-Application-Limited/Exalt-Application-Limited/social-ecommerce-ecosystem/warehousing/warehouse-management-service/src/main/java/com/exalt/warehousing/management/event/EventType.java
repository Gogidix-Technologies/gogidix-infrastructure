package com.exalt.warehousing.management.event;

/**
 * Enum defining the types of events in the warehouse management system
 */
public enum EventType {
    // Task events
    TASK_CREATED,
    TASK_UPDATED,
    TASK_ASSIGNED,
    TASK_STARTED,
    TASK_COMPLETED,
    TASK_CANCELLED,
    
    // Location events
    LOCATION_CREATED,
    LOCATION_UPDATED,
    LOCATION_STATUS_CHANGED,
    LOCATION_DELETED,
    
    // Zone events
    ZONE_CREATED,
    ZONE_UPDATED,
    ZONE_DELETED,
    
    // Warehouse events
    WAREHOUSE_CREATED,
    WAREHOUSE_UPDATED,
    WAREHOUSE_STATUS_CHANGED,
    
    // Staff events
    STAFF_CREATED,
    STAFF_UPDATED,
    STAFF_STATUS_CHANGED
} 
