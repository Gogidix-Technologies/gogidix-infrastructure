package com.exalt.warehousing.fulfillment.dto.inventory;

import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory allocation requests
 */
public class InventoryAllocationRequest {

    /**
     * ID of the order for which inventory is being allocated
     */
    private UUID orderId;

    /**
     * ID of the fulfillment order
     */
    private UUID fulfillmentOrderId;

    /**
     * ID of the reservation to allocate
     */
    private UUID reservationId;

    /**
     * List of specific reservation item IDs to allocate
     */
    private List<UUID> reservationItemIds;

    /**
     * Warehouse ID where to allocate from
     */
    private UUID warehouseId;

    // Constructors
    public InventoryAllocationRequest() {
        // Default constructor
    }

    public InventoryAllocationRequest(UUID orderId, UUID fulfillmentOrderId, UUID reservationId, List<UUID> reservationItemIds, UUID warehouseId) {
        this.orderId = orderId;
        this.fulfillmentOrderId = fulfillmentOrderId;
        this.reservationId = reservationId;
        this.reservationItemIds = reservationItemIds;
        this.warehouseId = warehouseId;
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

    public UUID getWarehouseId() {
        return warehouseId;
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

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
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
        private UUID warehouseId;

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

        public Builder warehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public InventoryAllocationRequest build() {
            return new InventoryAllocationRequest(orderId, fulfillmentOrderId, reservationId, reservationItemIds, warehouseId);
        }
    }
}