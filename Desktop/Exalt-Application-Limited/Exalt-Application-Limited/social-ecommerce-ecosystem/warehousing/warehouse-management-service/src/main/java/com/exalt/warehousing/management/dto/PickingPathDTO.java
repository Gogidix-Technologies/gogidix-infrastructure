package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for picking paths
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingPathDTO {
    
    /**
     * Unique identifier for the picking path
     */
    private UUID id;
    
    /**
     * Warehouse where the picking path is located
     */
    private UUID warehouseId;
    
    /**
     * Zone where the picking path is located (optional)
     */
    private UUID zoneId;
    
    /**
     * The ordered sequence of locations to visit
     */
    private List<LocationDTO> locations;
    
    /**
     * The items to pick at each location
     */
    private List<PickItemDTO> pickItems;
    
    /**
     * Starting location
     */
    private LocationDTO startLocation;
    
    /**
     * Ending location
     */
    private LocationDTO endLocation;
    
    /**
     * Total distance of the path in meters
     */
    private double totalDistance;
    
    /**
     * Estimated time to complete the path in minutes
     */
    private double estimatedTimeMinutes;
    
    /**
     * Algorithm used for path optimization
     */
    private String algorithm;
    
    /**
     * Creation timestamp of this picking path
     */
    private LocalDateTime createdAt;
    
    // Explicit getters and setters for the main class
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getWarehouseId() {
        return warehouseId;
    }
    
    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    public UUID getZoneId() {
        return zoneId;
    }
    
    public void setZoneId(UUID zoneId) {
        this.zoneId = zoneId;
    }
    
    public List<LocationDTO> getLocations() {
        return locations;
    }
    
    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }
    
    public List<PickItemDTO> getPickItems() {
        return pickItems;
    }
    
    public void setPickItems(List<PickItemDTO> pickItems) {
        this.pickItems = pickItems;
    }
    
    public LocationDTO getStartLocation() {
        return startLocation;
    }
    
    public void setStartLocation(LocationDTO startLocation) {
        this.startLocation = startLocation;
    }
    
    public LocationDTO getEndLocation() {
        return endLocation;
    }
    
    public void setEndLocation(LocationDTO endLocation) {
        this.endLocation = endLocation;
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }
    
    public void setEstimatedTimeMinutes(double estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Data Transfer Object for a pick item
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PickItemDTO {
        /**
         * Item identifier
         */
        private UUID itemId;
        
        /**
         * Location where the item is stored
         */
        private UUID locationId;
        
        /**
         * Product identifier
         */
        private UUID productId;
        
        /**
         * Product SKU
         */
        private String sku;
        
        /**
         * Product name
         */
        private String productName;
        
        /**
         * Quantity to pick
         */
        private int quantity;
        
        /**
         * Order the item belongs to
         */
        private UUID orderId;
        
        /**
         * Position in the picking sequence
         */
        private int pickSequence;
        
        /**
         * Location code for the item
         */
        private String locationCode;
        
        // Alias methods for backward compatibility
        public int getSequence() {
            return pickSequence;
        }
        
        public String getLocationCode() {
            return locationCode;
        }
        
        // Explicit getters and setters for the inner class
        public UUID getItemId() {
            return itemId;
        }
        
        public void setItemId(UUID itemId) {
            this.itemId = itemId;
        }
        
        public UUID getLocationId() {
            return locationId;
        }
        
        public void setLocationId(UUID locationId) {
            this.locationId = locationId;
        }
        
        public UUID getProductId() {
            return productId;
        }
        
        public void setProductId(UUID productId) {
            this.productId = productId;
        }
        
        public String getSku() {
            return sku;
        }
        
        public void setSku(String sku) {
            this.sku = sku;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public UUID getOrderId() {
            return orderId;
        }
        
        public void setOrderId(UUID orderId) {
            this.orderId = orderId;
        }
        
        public int getPickSequence() {
            return pickSequence;
        }
        
        public void setPickSequence(int pickSequence) {
            this.pickSequence = pickSequence;
        }
        
        public void setLocationCode(String locationCode) {
            this.locationCode = locationCode;
        }
    }
} 
