package com.exalt.warehousing.fulfillment.model;

/**
 * Enum representing the possible conditions of a returned item
 */
public enum ReturnItemCondition {
    NEW("Item is in new condition, can be resold as new"),
    LIKE_NEW("Item is like new, can be resold with minimal processing"),
    GOOD("Item is in good condition, can be resold as used"),
    FAIR("Item is in fair condition, may need repair before resale"),
    POOR("Item is in poor condition, may not be resalable"),
    DAMAGED("Item is damaged"),
    DEFECTIVE("Item is defective"),
    OPENED("Item has been opened but otherwise in good condition"),
    USED("Item shows signs of use");

    private final String description;

    ReturnItemCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
