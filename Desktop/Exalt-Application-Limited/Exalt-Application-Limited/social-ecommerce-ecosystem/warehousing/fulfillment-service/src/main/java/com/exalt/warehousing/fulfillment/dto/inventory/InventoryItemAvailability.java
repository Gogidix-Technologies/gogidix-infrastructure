package com.exalt.warehousing.fulfillment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for inventory item availability information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemAvailability {
    
    /**
     * Product ID
     */
    private UUID productId;
    
    /**
     * SKU (Stock Keeping Unit)
     */
    private String sku;
    
    /**
     * Requested quantity
     */
    private Integer requestedQuantity;
    
    /**
     * Available quantity
     */
    private Integer availableQuantity;
    
    /**
     * Whether the requested quantity is available
     */
    private boolean available;
    
    /**
     * List of warehouse availability details
     */
    private List<WarehouseAvailability> warehouseAvailability;
    
    /**
     * Error message if any
     */
    private String message;
    
    /**
     * Warehouse availability details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseAvailability {
        
        /**
         * Warehouse ID
         */
        private UUID warehouseId;
        
        /**
         * Warehouse name
         */
        private String warehouseName;
        
        /**
         * Available quantity in this warehouse
         */
        private Integer availableQuantity;
        
        /**
         * Bin locations where this product is stored
         */
        private List<String> binLocations;
    }
} 
