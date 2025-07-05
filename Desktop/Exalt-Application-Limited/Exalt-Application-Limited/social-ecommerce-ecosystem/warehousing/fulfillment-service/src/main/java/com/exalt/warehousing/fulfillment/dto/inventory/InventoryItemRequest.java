package com.exalt.warehousing.fulfillment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for a single inventory item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemRequest {
    
    /**
     * Product ID
     */
    private UUID productId;
    
    /**
     * SKU (Stock Keeping Unit)
     */
    private String sku;
    
    /**
     * Quantity needed
     */
    private Integer quantity;
    
    /**
     * Order item ID (for linking to original order item)
     */
    private UUID orderItemId;
} 
