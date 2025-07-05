package com.exalt.warehousing.fulfillment.dto.inventory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory allocation responses
 */
public class InventoryAllocationResponse {

    /**
     * ID of the allocation
     */
    private UUID allocationId;

    /**
     * ID of the order
     */
    private UUID orderId;

    /**
     * ID of the fulfillment order
     */
    private UUID fulfillmentOrderId;

    /**
     * ID of the reservation that was allocated
     */
    private UUID reservationId;

    /**
     * Flag indicating if the allocation was successful
     */
    private boolean success;

    /**
     * Error message if allocation failed
     */
    private String errorMessage;

    /**
     * List of allocated items
     */
    private List<AllocatedItemDTO> items;

    // Constructors
    public InventoryAllocationResponse() {
        // Default constructor
    }

    public InventoryAllocationResponse(UUID allocationId, UUID orderId, UUID fulfillmentOrderId, UUID reservationId, boolean success, String errorMessage, List<AllocatedItemDTO> items) {
        this.allocationId = allocationId;
        this.orderId = orderId;
        this.fulfillmentOrderId = fulfillmentOrderId;
        this.reservationId = reservationId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.items = items;
    }

    // Getters
    public UUID getAllocationId() {
        return allocationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<AllocatedItemDTO> getItems() {
        return items;
    }

    // Setters
    public void setAllocationId(UUID allocationId) {
        this.allocationId = allocationId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setFulfillmentOrderId(UUID fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setItems(List<AllocatedItemDTO> items) {
        this.items = items;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID allocationId;
        private UUID orderId;
        private UUID fulfillmentOrderId;
        private UUID reservationId;
        private boolean success;
        private String errorMessage;
        private List<AllocatedItemDTO> items;

        public Builder allocationId(UUID allocationId) {
            this.allocationId = allocationId;
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

        public Builder reservationId(UUID reservationId) {
            this.reservationId = reservationId;
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

        public Builder items(List<AllocatedItemDTO> items) {
            this.items = items;
            return this;
        }

        public InventoryAllocationResponse build() {
            return new InventoryAllocationResponse(allocationId, orderId, fulfillmentOrderId, reservationId, success, errorMessage, items);
        }
    }

    /**
     * DTO for allocated item details
     */
    public static class AllocatedItemDTO {
        private UUID productId;
        private String sku;
        private int quantity;
        private UUID reservationItemId;
        private UUID allocationItemId;
        private UUID warehouseId;
        private String binLocation;
        private boolean success;
        private String errorMessage;

        // Constructors
        public AllocatedItemDTO() {
            // Default constructor
        }

        public AllocatedItemDTO(UUID productId, String sku, int quantity, UUID reservationItemId, UUID allocationItemId, UUID warehouseId, String binLocation, boolean success, String errorMessage) {
            this.productId = productId;
            this.sku = sku;
            this.quantity = quantity;
            this.reservationItemId = reservationItemId;
            this.allocationItemId = allocationItemId;
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

        public UUID getAllocationItemId() {
            return allocationItemId;
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

        public void setAllocationItemId(UUID allocationItemId) {
            this.allocationItemId = allocationItemId;
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
            private UUID allocationItemId;
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

            public Builder allocationItemId(UUID allocationItemId) {
                this.allocationItemId = allocationItemId;
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

            public AllocatedItemDTO build() {
                return new AllocatedItemDTO(productId, sku, quantity, reservationItemId, allocationItemId, warehouseId, binLocation, success, errorMessage);
            }
        }
    }
}