package com.exalt.warehousing.fulfillment.enums;

import lombok.Getter;


/**
 * Item Status Enumeration for Order Items
 */
@Getter

public enum ItemStatus {
    
    PENDING("Pending", "Item is pending allocation"),
    ALLOCATED("Allocated", "Item has been allocated to fulfillment"),
    READY_FOR_PICKING("Ready for Picking", "Item is ready to be picked"),
    PICKING("Picking", "Item is currently being picked"),
    PICKED("Picked", "Item has been picked successfully"),
    SHORT_PICKED("Short Picked", "Item was picked but quantity is short"),
    PACKED("Packed", "Item has been packed for shipment"),
    SHIPPED("Shipped", "Item has been shipped"),
    CANCELLED("Cancelled", "Item has been cancelled"),
    SUBSTITUTED("Substituted", "Item has been substituted with alternative"),
    DAMAGED("Damaged", "Item was found to be damaged"),
    MISSING("Missing", "Item could not be found during picking");

    private final String displayName;
    private final String description;

    /**
     * Constructor for ItemStatus enum
     */
    ItemStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isCompleted() {
        return this == PACKED || this == SHIPPED;
    }

    public boolean isException() {
        return this == SHORT_PICKED || this == DAMAGED || this == MISSING;
    }

    public boolean canBePicked() {
        return this == READY_FOR_PICKING || this == ALLOCATED;
    }

    public boolean canBePacked() {
        return this == PICKED;
    }
}

