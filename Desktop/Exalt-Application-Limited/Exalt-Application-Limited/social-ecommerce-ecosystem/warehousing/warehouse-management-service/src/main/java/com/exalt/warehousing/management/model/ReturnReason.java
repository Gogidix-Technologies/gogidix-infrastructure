package com.exalt.warehousing.management.model;

/**
 * Enumeration of possible return reasons
 */
public enum ReturnReason {
    /**
     * Product was damaged during shipping
     */
    DAMAGED_IN_SHIPPING,
    
    /**
     * Product was defective or didn't work as expected
     */
    DEFECTIVE,
    
    /**
     * Product arrived too late
     */
    LATE_ARRIVAL,
    
    /**
     * Customer ordered the wrong product
     */
    WRONG_ITEM_ORDERED,
    
    /**
     * Wrong product was shipped
     */
    WRONG_ITEM_SHIPPED,
    
    /**
     * Product appearance or quality didn't match description
     */
    NOT_AS_DESCRIBED,
    
    /**
     * Product didn't fit (clothes, shoes, etc.)
     */
    DOES_NOT_FIT,
    
    /**
     * Customer changed their mind
     */
    CHANGED_MIND,
    
    /**
     * Customer found a better price elsewhere
     */
    BETTER_PRICE_FOUND,
    
    /**
     * Product was ordered accidentally
     */
    ACCIDENTAL_ORDER,
    
    /**
     * Product was a gift that wasn't needed or wanted
     */
    UNWANTED_GIFT,
    
    /**
     * Return for different color or size
     */
    EXCHANGE_COLOR_SIZE,
    
    /**
     * Missing parts or accessories
     */
    MISSING_PARTS,
    
    /**
     * Other reason not specified
     */
    OTHER
} 
