package com.exalt.warehousing.fulfillment.dto.inventory;

/**
 * Enum representing different types of inventory release operations
 */
public enum ReleaseType {
    /**
     * Complete release of the entire reserved quantity
     */
    COMPLETE,
    
    /**
     * Partial release of only a portion of the reserved quantity
     */
    PARTIAL,
    
    /**
     * Emergency release due to system issues or force majeure
     */
    EMERGENCY,
    
    /**
     * Automatic release triggered by system timeout or business rules
     */
    AUTOMATIC,
    
    /**
     * Manual release initiated by warehouse staff
     */
    MANUAL,
    
    /**
     * Release due to order cancellation
     */
    CANCELLATION,
    
    /**
     * Release due to inventory adjustment or correction
     */
    ADJUSTMENT,
    
    /**
     * Release due to return or refund processing
     */
    RETURN,
    
    /**
     * Release for transfer to another warehouse or location
     */
    TRANSFER
}
