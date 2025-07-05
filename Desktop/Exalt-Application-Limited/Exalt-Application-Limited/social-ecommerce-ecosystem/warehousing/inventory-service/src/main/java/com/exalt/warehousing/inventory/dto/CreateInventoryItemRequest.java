package com.exalt.warehousing.inventory.dto;

import com.exalt.warehousing.inventory.enums.InventoryStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Create Inventory Item Request DTO
 * 
 * Supports both traditional warehousing and vendor self-storage models.
 * Comprehensive validation ensures data integrity across all storage types.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Data
public class CreateInventoryItemRequest {

    // Core Identification
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    // Location Information (conditional based on storage model)
    private Long warehouseId; // null for vendor self-storage

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Size(max = 50, message = "Zone must not exceed 50 characters")
    private String zone;

    // Quantity Information
    @NotNull(message = "Available quantity is required")
    @DecimalMin(value = "0.0", message = "Available quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid quantity format")
    private BigDecimal availableQuantity;

    @DecimalMin(value = "0.0", message = "Reserved quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid reserved quantity format")
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Committed quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid committed quantity format")
    private BigDecimal committedQuantity = BigDecimal.ZERO;

    // Stock Level Configuration
    @DecimalMin(value = "0.0", message = "Reorder point must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid reorder point format")
    private BigDecimal reorderPoint;

    @DecimalMin(value = "0.0", message = "Maximum stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid maximum stock level format")
    private BigDecimal maximumStockLevel;

    @DecimalMin(value = "0.0", message = "Minimum stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid minimum stock level format")
    private BigDecimal minimumStockLevel;

    // Status and Classification
    private InventoryStatus status = InventoryStatus.RECEIVING;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 100, message = "Subcategory must not exceed 100 characters")
    private String subcategory;

    // Supplier Information
    private Long supplierId;

    @Size(max = 100, message = "Supplier SKU must not exceed 100 characters")
    private String supplierSku;

    @Size(max = 50, message = "PO number must not exceed 50 characters")
    private String purchaseOrderNumber;

    // Cost Information
    @DecimalMin(value = "0.0", message = "Unit cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid unit cost format")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0", message = "Average cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid average cost format")
    private BigDecimal averageCost;

    // Date Information
    private LocalDateTime receivedDate;
    private LocalDateTime expiryDate;
    private LocalDateTime manufactureDate;

    // Batch and Serial Tracking
    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @Size(max = 100, message = "Lot number must not exceed 100 characters")
    private String lotNumber;

    // Physical Attributes
    @Size(max = 20, message = "Unit of measure must not exceed 20 characters")
    private String unitOfMeasure;

    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    @Digits(integer = 5, fraction = 3, message = "Invalid weight format")
    private BigDecimal weightPerUnit;

    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    private String dimensions;

    // Quality and Compliance
    @Size(max = 20, message = "Quality grade must not exceed 20 characters")
    private String qualityGrade;

    @Size(max = 30, message = "Compliance status must not exceed 30 characters")
    private String complianceStatus;

    @Size(max = 50, message = "Hazmat classification must not exceed 50 characters")
    private String hazmatClassification;

    // Configuration Flags
    private Boolean isSerialized = false;
    private Boolean isBatchTracked = false;
    private Boolean isPerishable = false;
    private Boolean requiresSpecialHandling = false;
    private Boolean isVendorManaged = false;
    private Boolean autoReorderEnabled = false;

    // Priority and Instructions
    @Min(value = 1, message = "Priority level must be at least 1")
    @Max(value = 10, message = "Priority level must not exceed 10")
    private Integer priorityLevel = 5;

    private String handlingInstructions;
    private String storageConditions;

    // Identification Codes
    @Size(max = 100, message = "Barcode must not exceed 100 characters")
    private String barcode;

    @Size(max = 200, message = "QR code must not exceed 200 characters")
    private String qrCode;

    @Size(max = 100, message = "RFID tag must not exceed 100 characters")
    private String rfidTag;

    // Custom Attributes
    private Map<String, String> customAttributes;

    // Validation Methods
    public boolean isValidForVendorSelfStorage() {
        return isVendorManaged && warehouseId == null && location != null;
    }

    public boolean isValidForWarehouseStorage() {
        return !isVendorManaged && warehouseId != null;
    }

    public BigDecimal getTotalQuantity() {
        BigDecimal total = availableQuantity != null ? availableQuantity : BigDecimal.ZERO;
        total = total.add(reservedQuantity != null ? reservedQuantity : BigDecimal.ZERO);
        total = total.add(committedQuantity != null ? committedQuantity : BigDecimal.ZERO);
        return total;
    }
}
