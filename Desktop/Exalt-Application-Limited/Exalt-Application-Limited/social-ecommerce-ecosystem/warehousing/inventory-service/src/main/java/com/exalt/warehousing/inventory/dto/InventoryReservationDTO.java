package com.exalt.warehousing.inventory.dto;

import com.exalt.warehousing.inventory.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for inventory reservation data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationDTO {
    
    private UUID id;
    
    @NotNull(message = "Inventory item ID is required")
    private UUID inventoryItemId;
    
    private UUID warehouseId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Order ID is required")
    private UUID orderId;
    
    @NotNull(message = "Expiration time is required")
    private LocalDateTime expirationTime;
    
    @NotNull(message = "Status is required")
    private ReservationStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Calculated fields
    private Boolean isExpired;
    private Boolean isFinalized;
    
    // Reference data
    private String inventoryItemName;
    private String inventoryItemSku;
    private String warehouseName;
    private String warehouseCode;
    private String orderReference;
}
