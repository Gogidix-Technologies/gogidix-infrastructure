package com.exalt.warehousing.fulfillment.enums;

/**
 * Type of fulfillment method
 */
public enum FulfillmentType {
    WAREHOUSE("Warehouse", "Traditional warehouse fulfillment"),
    VENDOR_SELF_STORAGE("Vendor Self Storage", "Vendor manages their own storage"),
    DROP_SHIP("Drop Ship", "Direct from manufacturer/supplier"),
    CROSS_DOCK("Cross Dock", "Transfer without storage"),
    HYBRID("Hybrid", "Combination of methods");

    private final String displayName;
    private final String description;

    FulfillmentType(String displayName, String description) {
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
