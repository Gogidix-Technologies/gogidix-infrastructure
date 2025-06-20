package com.exalt.warehousing.fulfillment.dto.inventory;

import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory availability check responses
 */
public class InventoryCheckResponse {

    /**
     * ID of the order for which inventory was checked
     */
    private UUID orderId;

    /**
     * Flag indicating if all items are available in requested quantities
     */
    private boolean allItemsAvailable;

    /**
     * List of item availability details
     */
    private List<InventoryItemAvailabilityDTO> items;

    /**
     * Recommended warehouse ID for fulfillment
     */
    private UUID recommendedWarehouseId;

    // Constructors
    public InventoryCheckResponse() {
        // Default constructor
    }

    public InventoryCheckResponse(UUID orderId, boolean allItemsAvailable, List<InventoryItemAvailabilityDTO> items, UUID recommendedWarehouseId) {
        this.orderId = orderId;
        this.allItemsAvailable = allItemsAvailable;
        this.items = items;
        this.recommendedWarehouseId = recommendedWarehouseId;
    }

    // Getters
    public UUID getOrderId() {
        return orderId;
    }

    public boolean isAllItemsAvailable() {
        return allItemsAvailable;
    }

    public List<InventoryItemAvailabilityDTO> getItems() {
        return items;
    }

    public UUID getRecommendedWarehouseId() {
        return recommendedWarehouseId;
    }

    // Setters
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setAllItemsAvailable(boolean allItemsAvailable) {
        this.allItemsAvailable = allItemsAvailable;
    }

    public void setItems(List<InventoryItemAvailabilityDTO> items) {
        this.items = items;
    }

    public void setRecommendedWarehouseId(UUID recommendedWarehouseId) {
        this.recommendedWarehouseId = recommendedWarehouseId;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID orderId;
        private boolean allItemsAvailable;
        private List<InventoryItemAvailabilityDTO> items;
        private UUID recommendedWarehouseId;

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder allItemsAvailable(boolean allItemsAvailable) {
            this.allItemsAvailable = allItemsAvailable;
            return this;
        }

        public Builder items(List<InventoryItemAvailabilityDTO> items) {
            this.items = items;
            return this;
        }

        public Builder recommendedWarehouseId(UUID recommendedWarehouseId) {
            this.recommendedWarehouseId = recommendedWarehouseId;
            return this;
        }

        public InventoryCheckResponse build() {
            return new InventoryCheckResponse(orderId, allItemsAvailable, items, recommendedWarehouseId);
        }
    }

    /**
     * DTO for inventory item availability details
     */
    public static class InventoryItemAvailabilityDTO {
        private UUID productId;
        private String sku;
        private int requestedQuantity;
        private int availableQuantity;
        private boolean available;
        private String unavailableReason;
        private UUID warehouseId;
        private String binLocation;

        // Constructors
        public InventoryItemAvailabilityDTO() {
            // Default constructor
        }

        public InventoryItemAvailabilityDTO(UUID productId, String sku, int requestedQuantity, int availableQuantity, boolean available, String unavailableReason, UUID warehouseId, String binLocation) {
            this.productId = productId;
            this.sku = sku;
            this.requestedQuantity = requestedQuantity;
            this.availableQuantity = availableQuantity;
            this.available = available;
            this.unavailableReason = unavailableReason;
            this.warehouseId = warehouseId;
            this.binLocation = binLocation;
        }

        // Getters
        public UUID getProductId() {
            return productId;
        }

        public String getSku() {
            return sku;
        }

        public int getRequestedQuantity() {
            return requestedQuantity;
        }

        public int getAvailableQuantity() {
            return availableQuantity;
        }

        public boolean isAvailable() {
            return available;
        }

        public String getUnavailableReason() {
            return unavailableReason;
        }

        public UUID getWarehouseId() {
            return warehouseId;
        }

        public String getBinLocation() {
            return binLocation;
        }

        // Setters
        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public void setRequestedQuantity(int requestedQuantity) {
            this.requestedQuantity = requestedQuantity;
        }

        public void setAvailableQuantity(int availableQuantity) {
            this.availableQuantity = availableQuantity;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public void setUnavailableReason(String unavailableReason) {
            this.unavailableReason = unavailableReason;
        }

        public void setWarehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
        }

        public void setBinLocation(String binLocation) {
            this.binLocation = binLocation;
        }

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private UUID productId;
            private String sku;
            private int requestedQuantity;
            private int availableQuantity;
            private boolean available;
            private String unavailableReason;
            private UUID warehouseId;
            private String binLocation;

            public Builder productId(UUID productId) {
                this.productId = productId;
                return this;
            }

            public Builder sku(String sku) {
                this.sku = sku;
                return this;
            }

            public Builder requestedQuantity(int requestedQuantity) {
                this.requestedQuantity = requestedQuantity;
                return this;
            }

            public Builder availableQuantity(int availableQuantity) {
                this.availableQuantity = availableQuantity;
                return this;
            }

            public Builder available(boolean available) {
                this.available = available;
                return this;
            }

            public Builder unavailableReason(String unavailableReason) {
                this.unavailableReason = unavailableReason;
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

            public InventoryItemAvailabilityDTO build() {
                return new InventoryItemAvailabilityDTO(productId, sku, requestedQuantity, availableQuantity, available, unavailableReason, warehouseId, binLocation);
            }
        }
    }
}