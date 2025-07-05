package com.exalt.warehousing.fulfillment.dto.inventory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory reservation requests
 */
public class InventoryReservationRequest {

    /**
     * ID of the order for which inventory is being reserved
     */
    private UUID orderId;

    /**
     * ID of the fulfillment order
     */
    private UUID fulfillmentOrderId;

    /**
     * List of items to reserve
     */
    private List<ReservationItemDTO> items;

    /**
     * Warehouse ID to reserve from (optional, if null will use best warehouse)
     */
    private UUID warehouseId;
    
    /**
     * Reservation expiration time (when reservation should expire if not allocated)
     */
    private LocalDateTime expiresAt;
    
    /**
     * Priority of the reservation (higher priority means it will be allocated first)
     */
    private Integer priority;
    
    /**
     * Notes about the reservation
     */
    private String notes;

    // Constructors
    public InventoryReservationRequest() {
        // Default constructor
    }

    public InventoryReservationRequest(UUID orderId, UUID fulfillmentOrderId, List<ReservationItemDTO> items, UUID warehouseId, LocalDateTime expiresAt, Integer priority, String notes) {
        this.orderId = orderId;
        this.fulfillmentOrderId = fulfillmentOrderId;
        this.items = items;
        this.warehouseId = warehouseId;
        this.expiresAt = expiresAt;
        this.priority = priority;
        this.notes = notes;
    }

    // Getters
    public UUID getOrderId() {
        return orderId;
    }

    public UUID getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }

    public List<ReservationItemDTO> getItems() {
        return items;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setFulfillmentOrderId(UUID fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }

    public void setItems(List<ReservationItemDTO> items) {
        this.items = items;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID orderId;
        private UUID fulfillmentOrderId;
        private List<ReservationItemDTO> items;
        private UUID warehouseId;
        private LocalDateTime expiresAt;
        private Integer priority;
        private String notes;

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder fulfillmentOrderId(UUID fulfillmentOrderId) {
            this.fulfillmentOrderId = fulfillmentOrderId;
            return this;
        }

        public Builder items(List<ReservationItemDTO> items) {
            this.items = items;
            return this;
        }

        public Builder warehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder priority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public InventoryReservationRequest build() {
            return new InventoryReservationRequest(orderId, fulfillmentOrderId, items, warehouseId, expiresAt, priority, notes);
        }
    }

    /**
     * DTO for reservation item details
     */
    public static class ReservationItemDTO {
        private UUID productId;
        private String sku;
        private int quantity;
        private UUID fulfillmentOrderItemId;

        // Constructors
        public ReservationItemDTO() {
            // Default constructor
        }

        public ReservationItemDTO(UUID productId, String sku, int quantity, UUID fulfillmentOrderItemId) {
            this.productId = productId;
            this.sku = sku;
            this.quantity = quantity;
            this.fulfillmentOrderItemId = fulfillmentOrderItemId;
        }

        // Getters
        public UUID getProductId() {
            return productId;
        }

        public String getSku() {
            return sku;
        }

        public int getQuantity() {
            return quantity;
        }

        public UUID getFulfillmentOrderItemId() {
            return fulfillmentOrderItemId;
        }

        // Setters
        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setFulfillmentOrderItemId(UUID fulfillmentOrderItemId) {
            this.fulfillmentOrderItemId = fulfillmentOrderItemId;
        }

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private UUID productId;
            private String sku;
            private int quantity;
            private UUID fulfillmentOrderItemId;

            public Builder productId(UUID productId) {
                this.productId = productId;
                return this;
            }

            public Builder sku(String sku) {
                this.sku = sku;
                return this;
            }

            public Builder quantity(int quantity) {
                this.quantity = quantity;
                return this;
            }

            public Builder fulfillmentOrderItemId(UUID fulfillmentOrderItemId) {
                this.fulfillmentOrderItemId = fulfillmentOrderItemId;
                return this;
            }

            public ReservationItemDTO build() {
                return new ReservationItemDTO(productId, sku, quantity, fulfillmentOrderItemId);
            }
        }
    }
}