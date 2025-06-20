package com.exalt.warehousing.inventory.model;

/**
 * Enum representing possible statuses of inventory items
 */
public enum InventoryStatus {
    ACTIVE("Active and available for purchase"),
    LOW_STOCK("Available but running low"),
    OUT_OF_STOCK("Currently unavailable due to no stock"),
    DISCONTINUED("No longer available for purchase"),
    BACKORDERED("Out of stock but can be ordered"),
    RESERVED("Temporarily not available for purchase"),
    QUALITY_CHECK("Under quality inspection"),
    QUARANTINED("Quarantined due to quality issues");

    private final String description;

    InventoryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
