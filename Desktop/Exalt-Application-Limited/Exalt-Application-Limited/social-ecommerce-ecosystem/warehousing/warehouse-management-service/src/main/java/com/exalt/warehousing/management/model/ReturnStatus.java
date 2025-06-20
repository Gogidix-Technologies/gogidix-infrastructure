package com.exalt.warehousing.management.model;

/**
 * Enumeration of possible return request statuses
 */
public enum ReturnStatus {
    /**
     * Return has been requested by customer but not yet received
     */
    REQUESTED,
    
    /**
     * Return has been authorized but not yet received
     */
    AUTHORIZED,
    
    /**
     * Return label has been generated for the customer
     */
    LABEL_GENERATED,
    
    /**
     * Return is in transit to the warehouse
     */
    IN_TRANSIT,
    
    /**
     * Return has been received at the warehouse but not yet processed
     */
    RECEIVED,
    
    /**
     * Return is currently being inspected
     */
    INSPECTION,
    
    /**
     * Return has been inspected and accepted
     */
    ACCEPTED,
    
    /**
     * Return has been inspected and partially accepted (some items rejected)
     */
    PARTIALLY_ACCEPTED,
    
    /**
     * Return has been inspected and rejected
     */
    REJECTED,
    
    /**
     * Items have been reintegrated into inventory
     */
    INVENTORY_REINTEGRATED,
    
    /**
     * Refund has been processed
     */
    REFUND_PROCESSED,
    
    /**
     * Return has been completed
     */
    COMPLETED,
    
    /**
     * Return has been cancelled
     */
    CANCELLED
} 
