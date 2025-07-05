package com.exalt.warehousing.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for inventory allocation data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAllocationDTO {
    
    private UUID id;
    
    @NotNull(message = "Inventory item ID is required")
    private UUID inventoryItemId;
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
    
    @NotNull(message = "Reserved quantity is required")
    @Min(value = 0, message = "Reserved quantity must be non-negative")
    private Integer reservedQuantity;
    
    private String binLocation;
    
    private String aisle;
    
    private String rack;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Calculated fields
    private Integer availableQuantity;
    
    // Reference data
    private String warehouseName;
    private String warehouseCode;
    private String inventoryItemName;
    private String inventoryItemSku;
}
