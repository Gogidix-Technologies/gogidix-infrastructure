package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for items in a batch processing job
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchItemDTO {
    
    /**
     * Unique identifier for the batch item
     */
    private UUID id;
    
    /**
     * Inventory item reference ID
     */
    private UUID inventoryItemId;
    
    /**
     * Product ID that this batch item represents
     */
    private UUID productId;
    
    /**
     * SKU of the product
     */
    private String sku;
    
    /**
     * Name of the product
     */
    private String productName;
    
    /**
     * Location ID where this item is stored
     */
    private UUID locationId;
    
    /**
     * Order ID if this batch item is related to an order
     */
    private UUID orderId;
    
    /**
     * Order item ID if this batch item is related to an order item
     */
    private UUID orderItemId;
    
    /**
     * Quantity to process in this batch
     */
    private Integer quantity;
    
    /**
     * Priority of this item (1-5, with 1 being highest)
     */
    private Integer priority;
    
    /**
     * Batch group identifier for grouping related items
     */
    private String batchGroup;
    
    /**
     * Additional metadata for the batch item
     */
    private Map<String, String> metadata;
} 
