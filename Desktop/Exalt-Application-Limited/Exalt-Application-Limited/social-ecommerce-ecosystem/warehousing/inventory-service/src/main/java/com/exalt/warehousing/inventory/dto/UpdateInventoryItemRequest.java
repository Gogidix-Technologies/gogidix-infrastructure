package com.exalt.warehousing.inventory.dto;

import com.exalt.warehousing.inventory.enums.InventoryStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Update Inventory Item Request DTO
 * 
 * Supports partial updates to inventory items with comprehensive validation.
 * All fields are optional to support flexible update operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Data
public class UpdateInventoryItemRequest {

    // Location Updates
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Size(max = 50, message = "Zone must not exceed 50 characters")
    private String zone;

    private Long warehouseId;

    // Stock Level Updates
    @DecimalMin(value = "0.0", message = "Reorder point must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid reorder point format")
    private BigDecimal reorderPoint;

    @DecimalMin(value = "0.0", message = "Maximum stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid maximum stock level format")
    private BigDecimal maximumStockLevel;

    @DecimalMin(value = "0.0", message = "Minimum stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid minimum stock level format")
    private BigDecimal minimumStockLevel;

    // Status Updates
    private InventoryStatus status;

    @Size(max = 500, message = "Status reason must not exceed 500 characters")
    private String statusReason;

    // Classification Updates
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 100, message = "Subcategory must not exceed 100 characters")
    private String subcategory;

    // Cost Updates
    @DecimalMin(value = "0.0", message = "Unit cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid unit cost format")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0", message = "Average cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid average cost format")
    private BigDecimal averageCost;

    // Date Updates
    private LocalDateTime expiryDate;
    private LocalDateTime manufactureDate;

    // Quality and Compliance Updates
    @Size(max = 20, message = "Quality grade must not exceed 20 characters")
    private String qualityGrade;

    @Size(max = 30, message = "Compliance status must not exceed 30 characters")
    private String complianceStatus;

    @Size(max = 50, message = "Hazmat classification must not exceed 50 characters")
    private String hazmatClassification;

    // Configuration Updates
    private Boolean autoReorderEnabled;
    private Boolean requiresSpecialHandling;

    // Priority Updates
    @Min(value = 1, message = "Priority level must be at least 1")
    @Max(value = 10, message = "Priority level must not exceed 10")
    private Integer priorityLevel;

    private String handlingInstructions;
    private String storageConditions;

    // Custom Attributes Updates
    private Map<String, String> customAttributes;

    // Update reason for audit trail
    @Size(max = 500, message = "Update reason must not exceed 500 characters")
    private String updateReason;
}
