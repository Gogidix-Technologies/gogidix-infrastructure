package com.exalt.warehousing.inventory.model;

/**
 * Enum representing different types of warehouses in the system
 */
public enum WarehouseType {
    MAIN("Main central warehouse"),
    REGIONAL("Regional distribution center"),
    PARTNER("Third-party partner facility"),
    STORE("Retail store with inventory"),
    DROPSHIP("Dropship supplier location"),
    VIRTUAL("Virtual inventory location");

    private final String description;

    WarehouseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
