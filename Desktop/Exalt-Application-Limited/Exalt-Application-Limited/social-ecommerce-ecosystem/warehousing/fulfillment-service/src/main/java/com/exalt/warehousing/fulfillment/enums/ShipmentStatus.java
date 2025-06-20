package com.exalt.warehousing.fulfillment.enums;

/**
 * Enum representing the possible statuses of a shipment
 */
public enum ShipmentStatus {
    CREATED("Shipment created, waiting for processing"),
    READY_FOR_PICKUP("Shipment is ready for carrier pickup"),
    PICKED_UP("Shipment has been picked up by carrier"),
    SHIPPED("Shipment has been shipped"),
    IN_TRANSIT("Shipment is in transit"),
    OUT_FOR_DELIVERY("Shipment is out for delivery"),
    DELIVERED("Shipment has been delivered"),
    EXCEPTION("Shipment has encountered an exception"),
    RETURNED("Shipment has been returned"),
    CANCELLED("Shipment has been cancelled");

    private final String description;

    ShipmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}