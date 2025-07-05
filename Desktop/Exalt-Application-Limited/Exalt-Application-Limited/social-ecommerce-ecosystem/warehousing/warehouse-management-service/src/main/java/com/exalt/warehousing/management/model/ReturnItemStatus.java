package com.exalt.warehousing.management.model;

/**
 * Enumeration of possible return item statuses
 */
public enum ReturnItemStatus {
    /**
     * Item has been requested for return but not yet received
     */
    REQUESTED,
    
    /**
     * Item has been received but not yet inspected
     */
    RECEIVED,
    
    /**
     * Item is currently being inspected
     */
    INSPECTION,
    
    /**
     * Item has been assessed for quality
     */
    ASSESSED,
    
    /**
     * Item has been accepted for return
     */
    ACCEPTED,
    
    /**
     * Item has been rejected for return
     */
    REJECTED,
    
    /**
     * Item has been reintegrated into inventory
     */
    REINTEGRATED,
    
    /**
     * Item has been disposed of (damaged beyond repair)
     */
    DISPOSED,
    
    /**
     * Item has been sent for repair or refurbishment
     */
    REFURBISHMENT,
    
    /**
     * Item processing has been completed
     */
    COMPLETED
} 
