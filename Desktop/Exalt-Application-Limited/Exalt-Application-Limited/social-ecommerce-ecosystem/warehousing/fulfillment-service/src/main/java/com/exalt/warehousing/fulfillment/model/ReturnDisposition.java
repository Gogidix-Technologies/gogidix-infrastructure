package com.exalt.warehousing.fulfillment.model;

/**
 * Enum representing the possible dispositions for a returned item
 */
public enum ReturnDisposition {
    RETURN_TO_INVENTORY("Return item to inventory for resale"),
    REFURBISH("Send item for refurbishment before resale"),
    REPAIR("Send item for repair"),
    REPACKAGE("Repackage item for resale"),
    DISCARD("Discard the item"),
    RETURN_TO_VENDOR("Return the item to the vendor"),
    DONATE("Donate the item"),
    RECYCLE("Recycle the item"),
    SAMPLE("Use as a display or sample"),
    DESTROY("Destroy the item"),
    PENDING_DECISION("Decision on disposition is pending");

    private final String description;

    ReturnDisposition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
