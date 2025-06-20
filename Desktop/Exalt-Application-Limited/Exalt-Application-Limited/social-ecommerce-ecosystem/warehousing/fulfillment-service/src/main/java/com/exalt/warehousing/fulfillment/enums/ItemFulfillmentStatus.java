package com.exalt.warehousing.fulfillment.enums;

/**
 * Status values for individual fulfillment order items
 */
public enum ItemFulfillmentStatus {
    PENDING("Pending", "Item pending processing"),
    ALLOCATED("Allocated", "Inventory allocated for item"),
    READY_FOR_PICKING("Ready for Picking", "Item ready to be picked"),
    PICKING("Picking", "Item being picked"),
    PICKED("Picked", "Item has been picked"),
    PACKING("Packing", "Item being packed"),
    PACKED("Packed", "Item has been packed"),
    SHIPPED("Shipped", "Item has been shipped"),
    FULFILLED("Fulfilled", "Item has been fulfilled"),
    CANCELLED("Cancelled", "Item cancelled"),
    OUT_OF_STOCK("Out of Stock", "Item is out of stock"),
    BACKORDERED("Backordered", "Item is backordered"),
    DAMAGED("Damaged", "Item was damaged"),
    RETURNED("Returned", "Item was returned");
    
    private final String displayName;
    private final String description;
    
    ItemFulfillmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTerminal() {
        return this == FULFILLED || this == CANCELLED || this == RETURNED;
    }
}
