package com.exalt.warehousing.management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Inventory Item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryItemDTO {

    private UUID id;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Location ID is required")
    private UUID locationId;

    private UUID warehouseId;

    private String productSku;

    private String productName;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity must be at least 0")
    private Integer availableQuantity;

    @Min(value = 0, message = "Reserved quantity must be at least 0")
    private Integer reservedQuantity;

    @Min(value = 0, message = "Total quantity must be at least 0")
    private Integer totalQuantity;

    private String lotNumber;

    private String serialNumber;

    private String status;

    private LocalDateTime expiryDate;

    private LocalDateTime receivedDate;

    private BigDecimal unitCost;

    private String unitOfMeasure;

    private Map<String, Object> properties;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 
