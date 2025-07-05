package com.exalt.warehousing.fulfillment.dto;

import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for fulfillment order item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentOrderItemDTO {
    
    private UUID id;
    
    private UUID fulfillmentOrderId;
    
    @NotNull(message = "Order item ID is required")
    private UUID orderItemId;
    
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    private String sku;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    private String productImageUrl;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @Min(value = 0, message = "Quantity fulfilled cannot be negative")
    private Integer quantityFulfilled;
    
    @Min(value = 0, message = "Quantity picked cannot be negative")
    private Integer quantityPicked;
    
    @Min(value = 0, message = "Quantity packed cannot be negative")
    private Integer quantityPacked;
    
    private String binLocation;
    
    private String specialInstructions;
    
    private ItemFulfillmentStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Calculated fields
    private boolean fullyFulfilled;
    private boolean partiallyFulfilled;
    private boolean fullyPicked;
    private int remainingQuantity;
} 
