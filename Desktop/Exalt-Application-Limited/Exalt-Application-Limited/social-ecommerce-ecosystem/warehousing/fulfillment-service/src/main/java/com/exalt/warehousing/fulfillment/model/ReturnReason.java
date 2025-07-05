package com.exalt.warehousing.fulfillment.model;

/**
 * Enum representing the possible reasons for a return
 */
public enum ReturnReason {
    DAMAGED("Item arrived damaged"),
    DEFECTIVE("Item is defective"),
    INCORRECT_ITEM("Received incorrect item"),
    WRONG_SIZE("Item is the wrong size"),
    CHANGED_MIND("Customer changed their mind"),
    NOT_AS_DESCRIBED("Item not as described"),
    ARRIVED_LATE("Item arrived too late"),
    QUALITY_ISSUE("Quality not as expected"),
    DUPLICATE_ORDER("Duplicate order received"),
    OTHER("Other reason");

    private final String description;

    ReturnReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
