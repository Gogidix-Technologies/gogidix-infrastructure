package com.exalt.warehousing.fulfillment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event representing a change in inventory
 * This is used to consume events from the Inventory Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    
    /**
     * Unique event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private String eventType;
    
    /**
     * ID of the inventory item
     */
    private UUID inventoryItemId;
    
    /**
     * SKU of the product
     */
    private String sku;
    
    /**
     * ID of the warehouse
     */
    private UUID warehouseId;
    
    /**
     * Available quantity
     */
    private Integer availableQuantity;
    
    /**
     * Reserved quantity
     */
    private Integer reservedQuantity;
    
    /**
     * Allocated quantity
     */
    private Integer allocatedQuantity;
    
    /**
     * Total quantity
     */
    private Integer totalQuantity;
    
    /**
     * Whether the item is low in stock
     */
    private Boolean lowStock;
    
    /**
     * Time the event was generated
     */
    private LocalDateTime timestamp;
} 
