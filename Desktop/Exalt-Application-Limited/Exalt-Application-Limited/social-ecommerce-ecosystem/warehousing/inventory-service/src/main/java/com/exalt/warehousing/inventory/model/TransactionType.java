package com.exalt.warehousing.inventory.model;

/**
 * Enum representing types of inventory transactions
 */
public enum TransactionType {
    PURCHASE("Inventory received from purchase order"),
    SALE("Inventory deducted from sales order"),
    RETURN("Inventory returned by customer"),
    TRANSFER("Inventory transferred between warehouses"),
    ADJUSTMENT("Manual inventory adjustment"),
    RESERVATION("Temporary inventory reservation"),
    RELEASE("Release of previously reserved inventory"),
    DAMAGE("Inventory lost due to damage"),
    EXPIRY("Inventory expired"),
    RECOUNT("Inventory level corrected after physical count"),
    QUALITY_CHECK("Inventory marked for quality inspection"),
    QUARANTINE("Inventory quarantined due to quality issues");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
