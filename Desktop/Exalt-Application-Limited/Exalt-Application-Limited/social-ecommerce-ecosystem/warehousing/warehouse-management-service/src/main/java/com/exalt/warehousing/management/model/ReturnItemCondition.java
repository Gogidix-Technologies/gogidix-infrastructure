package com.exalt.warehousing.management.model;

/**
 * Enumeration of possible return item conditions
 */
public enum ReturnItemCondition {
    /**
     * Item is in new condition, can be resold as new
     */
    NEW,
    
    /**
     * Item shows minimal signs of use, can be resold as open-box or almost new
     */
    LIKE_NEW,
    
    /**
     * Item shows some signs of use but is still in good condition
     */
    GOOD,
    
    /**
     * Item shows significant signs of use or wear
     */
    FAIR,
    
    /**
     * Item is in poor condition but still usable
     */
    POOR,
    
    /**
     * Item is damaged but repairable
     */
    DAMAGED_REPAIRABLE,
    
    /**
     * Item is damaged
     */
    DAMAGED,
    
    /**
     * Item is damaged beyond repair
     */
    DAMAGED_BEYOND_REPAIR,
    
    /**
     * Item is defective but can be repaired
     */
    DEFECTIVE_REPAIRABLE,
    
    /**
     * Item is defective and cannot be repaired
     */
    DEFECTIVE_BEYOND_REPAIR,
    
    /**
     * Item is missing parts
     */
    MISSING_PARTS,
    
    /**
     * Item has been opened or packaging is damaged
     */
    OPENED_PACKAGE,
    
    /**
     * Condition has not yet been assessed
     */
    NOT_ASSESSED
} 
