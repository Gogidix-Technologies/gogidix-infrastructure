package com.exalt.warehousing.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Stock Adjustment Request DTO
 * 
 * Handles various types of stock adjustments with proper validation
 * and audit trail support for inventory management.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Data
public class StockAdjustmentRequest {

    public enum AdjustmentType {
        INCREASE,
        DECREASE,
        SET_ABSOLUTE,
        CORRECTION,
        DAMAGE_WRITE_OFF,
        LOSS_WRITE_OFF,
        FOUND_STOCK,
        RETURN_TO_STOCK,
        CYCLE_COUNT_ADJUSTMENT
    }

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;

    @NotNull(message = "Adjustment quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Adjustment quantity must be positive")
    @Digits(integer = 9, fraction = 3, message = "Invalid adjustment quantity format")
    private BigDecimal adjustmentQuantity;

    @NotBlank(message = "Adjustment reason is required")
    @Size(max = 500, message = "Adjustment reason must not exceed 500 characters")
    private String reason;

    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    private String referenceNumber;

    @Size(max = 100, message = "Performed by must not exceed 100 characters")
    private String performedBy;

    private String notes;

    // For cycle count adjustments
    private BigDecimal countedQuantity;
    private BigDecimal systemQuantity;

    // For cost adjustments
    @DecimalMin(value = "0.0", message = "Unit cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid unit cost format")
    private BigDecimal newUnitCost;

    public boolean isValidAdjustment() {
        return switch (adjustmentType) {
            case CYCLE_COUNT_ADJUSTMENT -> countedQuantity != null && systemQuantity != null;
            case SET_ABSOLUTE -> adjustmentQuantity.compareTo(BigDecimal.ZERO) >= 0;
            default -> adjustmentQuantity.compareTo(BigDecimal.ZERO) > 0;
        };
    }
}
