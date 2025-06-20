package com.exalt.warehousing.fulfillment.enums;

/**
 * Inventory status for fulfillment orders
 */
public enum InventoryStatus {
    PENDING("Pending", "Inventory check pending"),
    CHECKING("Checking", "Checking inventory availability"),
    AVAILABLE("Available", "Inventory is available"),
    ALLOCATED("Allocated", "Inventory has been allocated"),
    RESERVED("Reserved", "Inventory has been reserved"),
    CONFIRMED("Confirmed", "Inventory reservation confirmed"),
    PARTIAL("Partial", "Partially available"),
    UNAVAILABLE("Unavailable", "Inventory not available"),
    CANCELLED("Cancelled", "Inventory allocation cancelled"),
    EXPIRED("Expired", "Inventory reservation expired");
    
    private final String displayName;
    private final String description;
    
    InventoryStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}
