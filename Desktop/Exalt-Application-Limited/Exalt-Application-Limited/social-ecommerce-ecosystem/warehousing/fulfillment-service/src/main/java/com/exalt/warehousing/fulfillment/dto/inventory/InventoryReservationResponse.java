package com.exalt.warehousing.fulfillment.dto.inventory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory reservation responses
 */
public class InventoryReservationResponse {

    /**
     * ID of the reservation
     */
    private UUID reservationId;

    /**
     * ID of the order
     */
    private UUID orderId;

    /**
     * ID of the fulfillment order
     */
    private UUID fulfillmentOrderId;

    /**
     * Flag indicating if the reservation was successful
     */
    private boolean success;

    /**
     * Error message if reservation failed
     */
    private String errorMessage;

    /**
     * Expiration date/time of the reservation
     */
    private LocalDateTime expiresAt;

    /**
     * List of reserved items
     */
    private List<ReservedItemDTO> items;

    // Constructors
    public InventoryReservationResponse() {
        // Default constructor
    }

    public InventoryReservationResponse(UUID reservationId, UUID orderId, UUID fulfillmentOrderId, boolean success, String errorMessage, LocalDateTime expiresAt, List<ReservedItemDTO> items) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.fulfillmentOrderId = fulfillmentOrderId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.expiresAt = expiresAt;
        this.items = items;
    }

    // Getters
    public UUID getReservationId() {
        return reservationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public List<ReservedItemDTO> getItems() {
        return items;
    }

    // Setters
    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setFulfillmentOrderId(UUID fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setItems(List<ReservedItemDTO> items) {
        this.items = items;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID reservationId;
        private UUID orderId;
        private UUID fulfillmentOrderId;
        private boolean success;
        private String errorMessage;
        private LocalDateTime expiresAt;
        private List<ReservedItemDTO> items;

        public Builder reservationId(UUID reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder fulfillmentOrderId(UUID fulfillmentOrderId) {
            this.fulfillmentOrderId = fulfillmentOrderId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder items(List<ReservedItemDTO> items) {
            this.items = items;
            return this;
        }

        public InventoryReservationResponse build() {
            return new InventoryReservationResponse(reservationId, orderId, fulfillmentOrderId, success, errorMessage, expiresAt, items);
        }
    }

    /**
     * DTO for reserved item details
     */
    public static class ReservedItemDTO {
        private UUID productId;
        private String sku;
        private int quantity;
        private UUID reservationItemId;
        private UUID warehouseId;
        private String binLocation;
        private boolean success;
        private String errorMessage;

        // Constructors
        public ReservedItemDTO() {
            // Default constructor
        }

        public ReservedItemDTO(UUID productId, String sku, int quantity, UUID reservationItemId, UUID warehouseId, String binLocation, boolean success, String errorMessage) {
            this.productId = productId;
            this.sku = sku;
            this.quantity = quantity;
            this.reservationItemId = reservationItemId;
            this.warehouseId = warehouseId;
            this.binLocation = binLocation;
            this.success = success;
            this.errorMessage = errorMessage;
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

        public UUID getReservationItemId() {
            return reservationItemId;
        }

        public UUID getWarehouseId() {
            return warehouseId;
        }

        public String getBinLocation() {
            return binLocation;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
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

        public void setReservationItemId(UUID reservationItemId) {
            this.reservationItemId = reservationItemId;
        }

        public void setWarehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
        }

        public void setBinLocation(String binLocation) {
            this.binLocation = binLocation;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private UUID productId;
            private String sku;
            private int quantity;
            private UUID reservationItemId;
            private UUID warehouseId;
            private String binLocation;
            private boolean success;
            private String errorMessage;

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

            public Builder reservationItemId(UUID reservationItemId) {
                this.reservationItemId = reservationItemId;
                return this;
            }

            public Builder warehouseId(UUID warehouseId) {
                this.warehouseId = warehouseId;
                return this;
            }

            public Builder binLocation(String binLocation) {
                this.binLocation = binLocation;
                return this;
            }

            public Builder success(boolean success) {
                this.success = success;
                return this;
            }

            public Builder errorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
                return this;
            }

            public ReservedItemDTO build() {
                return new ReservedItemDTO(productId, sku, quantity, reservationItemId, warehouseId, binLocation, success, errorMessage);
            }
        }
    }
}