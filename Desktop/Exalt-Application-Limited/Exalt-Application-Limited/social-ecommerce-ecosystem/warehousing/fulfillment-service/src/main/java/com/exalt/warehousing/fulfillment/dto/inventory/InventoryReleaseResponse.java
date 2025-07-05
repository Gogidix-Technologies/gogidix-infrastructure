package com.exalt.warehousing.fulfillment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for inventory release
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReleaseResponse {
    
    /**
     * Overall success status
     */
    private boolean success;
    
    /**
     * Release ID
     */
    private UUID releaseId;
    
    /**
     * Type of release (reservation or allocation)
     */
    private ReleaseType releaseType;
    
    /**
     * Reservation ID that was released (if releasing a reservation)
     */
    private UUID reservationId;
    
    /**
     * Allocation ID that was released (if releasing an allocation)
     */
    private UUID allocationId;
    
    /**
     * Fulfillment order ID
     */
    private UUID fulfillmentOrderId;
    
    /**
     * Item releases
     */
    private List<ItemRelease> itemReleases;
    
    /**
     * Error message if unsuccessful
     */
    private String errorMessage;
    
    /**
     * When the release was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Individual item release details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRelease {
        
        /**
         * Item release ID
         */
        private UUID id;
        
        /**
         * Item ID that was released (reservation or allocation item ID)
         */
        private UUID itemId;
        
        /**
         * Product ID
         */
        private UUID productId;
        
        /**
         * SKU
         */
        private String sku;
        
        /**
         * Released quantity
         */
        private Integer quantity;
        
        /**
         * Whether this item was successfully released
         */
        private boolean success;
        
        /**
         * Error message if unsuccessful
         */
        private String errorMessage;
    }
} 
