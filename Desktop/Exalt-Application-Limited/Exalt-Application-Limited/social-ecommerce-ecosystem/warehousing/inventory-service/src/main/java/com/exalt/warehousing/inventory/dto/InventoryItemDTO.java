package com.exalt.warehousing.inventory.dto;

import com.exalt.warehousing.inventory.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for inventory item data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDTO {
    
    private UUID id;
    
    @NotNull(message = "{inventory.productId.notNull}")
    private UUID productId;
    
    @NotBlank(message = "{inventory.sku.notBlank}")
    @Size(min = 2, max = 50, message = "{inventory.sku.size}")
    private String sku;
    
    @NotBlank(message = "{inventory.name.notBlank}")
    @Size(min = 2, max = 255, message = "{inventory.name.size}")
    private String name;
    
    @Size(max = 1000)
    private String description;
    
    @NotNull
    @PositiveOrZero(message = "{inventory.quantity.positiveOrZero}")
    private Integer totalQuantity;
    
    @PositiveOrZero
    private Integer reservedQuantity;
    
    @NotNull
    @Min(value = 0, message = "{inventory.minimumStockLevel.min}")
    private Integer lowStockThreshold;
    
    @NotNull
    @Min(value = 0, message = "{inventory.safetyStockLevel.min}")
    private Integer safetyStockLevel;
    
    private Boolean isActive;
    
    private InventoryStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Calculated fields
    private Integer availableQuantity;
    
    private Boolean isLowStock;
    
    private Boolean isCriticallyLowStock;
    
    private Boolean isOutOfStock;
}
