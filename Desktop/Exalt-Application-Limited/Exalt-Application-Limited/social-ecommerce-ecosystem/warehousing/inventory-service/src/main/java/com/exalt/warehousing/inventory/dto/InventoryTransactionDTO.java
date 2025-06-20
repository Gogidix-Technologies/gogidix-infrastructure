package com.exalt.warehousing.inventory.dto;

import com.exalt.warehousing.inventory.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for inventory transaction data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionDTO {
    
    private UUID id;
    
    @NotNull(message = "Inventory item ID is required")
    private UUID inventoryItemId;
    
    private UUID warehouseId;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    private UUID referenceId;
    
    private String referenceType;
    
    private String note;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    private LocalDateTime timestamp;
    
    // Reference data
    private String inventoryItemName;
    private String inventoryItemSku;
    private String warehouseName;
    private String warehouseCode;
    private String userName;
}
