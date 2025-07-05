package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for creating fulfillment order items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {
    private String sku;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private Integer orderedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String currency;
    private BigDecimal weight;
    private String weightUnit;
    private String dimensions;
    private Map<String, Object> attributes;
    private Integer requestedQuantity;
    private String serialNumber;
    private String lotNumber;
    private String batchNumber;
    private UUID vendorId;
    private Boolean requiresSpecialHandling;
    private Boolean isFragile;
    private Boolean isHazmat;
    private Boolean isTemperatureControlled;
    private boolean isHazardous;
    private boolean isRefrigerated;
    private String notes;
    private String handlingInstructions;
    
    // Compatibility getters
    public Integer getOrderedQuantity() {
        return orderedQuantity != null ? orderedQuantity : quantity;
    }
    
    public Boolean getIsFragile() {
        return isFragile;
    }
    
    public Boolean getRequiresSpecialHandling() {
        return requiresSpecialHandling;
    }
    
    public Boolean getIsHazmat() {
        return isHazmat != null ? isHazmat : isHazardous;
    }
    
    public Boolean getIsTemperatureControlled() {
        return isTemperatureControlled != null ? isTemperatureControlled : isRefrigerated;
    }
    
    public String getHandlingInstructions() {
        return handlingInstructions != null ? handlingInstructions : notes;
    }
}
