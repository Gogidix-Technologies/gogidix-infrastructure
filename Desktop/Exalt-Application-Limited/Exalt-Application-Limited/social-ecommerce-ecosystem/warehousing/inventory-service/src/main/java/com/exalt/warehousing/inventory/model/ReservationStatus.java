package com.exalt.warehousing.inventory.model;

/**
 * Enum representing the status of inventory reservations
 */
public enum ReservationStatus {
    PENDING("Reservation created but not yet confirmed"),
    CONFIRMED("Reservation confirmed and inventory allocated"),
    COMPLETED("Reservation fulfilled and converted to sale"),
    CANCELLED("Reservation cancelled by user or system"),
    EXPIRED("Reservation expired due to time limit");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
