package com.exalt.warehousing.fulfillment.dto.inventory;

import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory release requests
 */
public class InventoryReleaseRequest {

    /**
     * ID of the order for which inventory is being released
     */
    private UUID orderId;

    /**
     * ID of the fulfillment order
     */
    private UUID fulfillmentOrderId;

    /**
     * ID of the reservation to release (if releasing entire reservation)
     */
    private UUID reservationId;

    /**
     * List of specific reservation item IDs to release (if not releasing entire reservation)
     */
    private List<UUID> reservationItemIds;

    /**
     * Reason for releasing the inventory
     */
    private String reason;

    // Constructors
    public InventoryReleaseRequest() {
        // Default constructor
    }

    public InventoryReleaseRequest(UUID orderId, UUID fulfillmentOrderId, UUID reservationId, List<UUID> reservationItemIds, String reason) {
        this.orderId = orderId;
        this.fulfillmentOrderId = fulfillmentOrderId;
        this.reservationId = reservationId;
        this.reservationItemIds = reservationItemIds;
        this.reason = reason;
    }

    // Getters
    public UUID getOrderId() {
        return orderId;
    }

    public UUID getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public List<UUID> getReservationItemIds() {
        return reservationItemIds;
    }

    public String getReason() {
        return reason;
    }

    // Setters
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setFulfillmentOrderId(UUID fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public void setReservationItemIds(List<UUID> reservationItemIds) {
        this.reservationItemIds = reservationItemIds;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID orderId;
        private UUID fulfillmentOrderId;
        private UUID reservationId;
        private List<UUID> reservationItemIds;
        private String reason;

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder fulfillmentOrderId(UUID fulfillmentOrderId) {
            this.fulfillmentOrderId = fulfillmentOrderId;
            return this;
        }

        public Builder reservationId(UUID reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Builder reservationItemIds(List<UUID> reservationItemIds) {
            this.reservationItemIds = reservationItemIds;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public InventoryReleaseRequest build() {
            return new InventoryReleaseRequest(orderId, fulfillmentOrderId, reservationId, reservationItemIds, reason);
        }
    }
}