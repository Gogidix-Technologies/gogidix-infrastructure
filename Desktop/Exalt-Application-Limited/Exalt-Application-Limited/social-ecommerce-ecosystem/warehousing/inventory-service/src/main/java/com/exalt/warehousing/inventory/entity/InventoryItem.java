package com.exalt.warehousing.inventory.entity;

import com.exalt.warehousing.inventory.enums.InventoryStatus;
import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Enhanced Inventory Item Entity
 * 
 * Comprehensive inventory management supporting both centralized warehousing
 * and vendor self-storage models. Tracks items across multiple locations
 * with detailed business logic for stock management.
 * 
 * Supports Revolutionary Vendor Choice Model:
 * - Traditional warehouse storage
 * - Vendor self-storage locations
 * - Hybrid fulfillment strategies
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "inventory_items", indexes = {
    @Index(name = "idx_inventory_sku", columnList = "sku", unique = true),
    @Index(name = "idx_inventory_product_id", columnList = "productId"),
    @Index(name = "idx_inventory_vendor_id", columnList = "vendorId"),
    @Index(name = "idx_inventory_warehouse_id", columnList = "warehouseId"),
    @Index(name = "idx_inventory_status", columnList = "status"),
    @Index(name = "idx_inventory_location", columnList = "location"),
    @Index(name = "idx_inventory_reorder_point", columnList = "reorderPoint"),
    @Index(name = "idx_inventory_last_updated", columnList = "lastStockUpdate"),
    @Index(name = "idx_inventory_expiry", columnList = "expiryDate"),
    @Index(name = "idx_inventory_reserved", columnList = "reservedQuantity"),
    @Index(name = "idx_inventory_category", columnList = "category"),
    @Index(name = "idx_inventory_supplier", columnList = "supplierId"),
    @Index(name = "idx_inventory_batch", columnList = "batchNumber"),
    @Index(name = "idx_inventory_priority", columnList = "priorityLevel"),
    @Index(name = "idx_inventory_compliance", columnList = "complianceStatus")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryItem extends BaseEntity {

    // Core Identification
    @Column(name = "sku", nullable = false, unique = true, length = 100)
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "product_id", nullable = false)
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Column(name = "vendor_id", nullable = false)
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    // Location Information
    @Column(name = "warehouse_id")
    private Long warehouseId; // null for vendor self-storage

    @Column(name = "location", length = 200)
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location; // Aisle-Shelf-Bin or vendor location identifier

    @Column(name = "zone", length = 50)
    @Size(max = 50, message = "Zone must not exceed 50 characters")
    private String zone; // Warehouse zone or vendor facility section

    // Quantity Management
    @Column(name = "available_quantity", nullable = false, precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Available quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid quantity format")
    private BigDecimal availableQuantity;

    @Column(name = "reserved_quantity", nullable = false, precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Reserved quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid reserved quantity format")
    private BigDecimal reservedQuantity;

    @Column(name = "total_quantity", nullable = false, precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Total quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid total quantity format")
    private BigDecimal totalQuantity;

    @Column(name = "committed_quantity", precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Committed quantity must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid committed quantity format")
    private BigDecimal committedQuantity;

    // Stock Management
    @Column(name = "reorder_point", precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Reorder point must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid reorder point format")
    private BigDecimal reorderPoint;

    @Column(name = "maximum_stock_level", precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Maximum stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid maximum stock level format")
    private BigDecimal maximumStockLevel;

    @Column(name = "minimum_stock_level", precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Minimum stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid minimum stock level format")
    private BigDecimal minimumStockLevel;

    @Column(name = "low_stock_threshold", precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Low stock threshold must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid low stock threshold format")
    private BigDecimal lowStockThreshold;

    @Column(name = "safety_stock_level", precision = 12, scale = 3)
    @DecimalMin(value = "0.0", message = "Safety stock level must be non-negative")
    @Digits(integer = 9, fraction = 3, message = "Invalid safety stock level format")
    private BigDecimal safetyStockLevel;

    // Status and Classification
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Inventory status is required")
    private InventoryStatus status;

    @Column(name = "category", length = 100)
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Column(name = "subcategory", length = 100)
    @Size(max = 100, message = "Subcategory must not exceed 100 characters")
    private String subcategory;

    // Supplier and Procurement
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_sku", length = 100)
    @Size(max = 100, message = "Supplier SKU must not exceed 100 characters")
    private String supplierSku;

    @Column(name = "purchase_order_number", length = 50)
    @Size(max = 50, message = "PO number must not exceed 50 characters")
    private String purchaseOrderNumber;

    // Cost and Valuation
    @Column(name = "unit_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Unit cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid unit cost format")
    private BigDecimal unitCost;

    @Column(name = "average_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Average cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid average cost format")
    private BigDecimal averageCost;

    @Column(name = "total_value", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Total value must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid total value format")
    private BigDecimal totalValue;

    // Date Tracking
    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    @Column(name = "last_stock_update")
    private LocalDateTime lastStockUpdate;

    @Column(name = "last_movement_date")
    private LocalDateTime lastMovementDate;

    // Batch and Serial Tracking
    @Column(name = "batch_number", length = 100)
    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @Column(name = "lot_number", length = 100)
    @Size(max = 100, message = "Lot number must not exceed 100 characters")
    private String lotNumber;

    @Column(name = "serial_numbers", columnDefinition = "TEXT")
    private String serialNumbers; // JSON array of serial numbers

    // Physical Attributes
    @Column(name = "unit_of_measure", length = 20)
    @Size(max = 20, message = "Unit of measure must not exceed 20 characters")
    private String unitOfMeasure;

    @Column(name = "weight_per_unit", precision = 8, scale = 3)
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    @Digits(integer = 5, fraction = 3, message = "Invalid weight format")
    private BigDecimal weightPerUnit;

    @Column(name = "dimensions", length = 100)
    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    private String dimensions; // L x W x H format

    // Quality and Compliance
    @Column(name = "quality_grade", length = 20)
    @Size(max = 20, message = "Quality grade must not exceed 20 characters")
    private String qualityGrade;

    @Column(name = "compliance_status", length = 30)
    @Size(max = 30, message = "Compliance status must not exceed 30 characters")
    private String complianceStatus;

    @Column(name = "hazmat_classification", length = 50)
    @Size(max = 50, message = "Hazmat classification must not exceed 50 characters")
    private String hazmatClassification;

    // Business Logic Fields
    @Column(name = "is_serialized", nullable = false)
    private Boolean isSerialized = false;

    @Column(name = "is_batch_tracked", nullable = false)
    private Boolean isBatchTracked = false;

    @Column(name = "is_perishable", nullable = false)
    private Boolean isPerishable = false;

    @Column(name = "requires_special_handling", nullable = false)
    private Boolean requiresSpecialHandling = false;

    @Column(name = "is_vendor_managed", nullable = false)
    private Boolean isVendorManaged = false; // True for vendor self-storage

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "auto_reorder_enabled", nullable = false)
    private Boolean autoReorderEnabled = false;

    // Priority and Handling
    @Column(name = "priority_level")
    @Min(value = 1, message = "Priority level must be at least 1")
    @Max(value = 10, message = "Priority level must not exceed 10")
    private Integer priorityLevel = 5; // 1 = Highest, 10 = Lowest

    @Column(name = "handling_instructions", columnDefinition = "TEXT")
    private String handlingInstructions;

    @Column(name = "storage_conditions", columnDefinition = "TEXT")
    private String storageConditions;

    // Integration and Tracking
    @Column(name = "barcode", length = 100)
    @Size(max = 100, message = "Barcode must not exceed 100 characters")
    private String barcode;

    @Column(name = "qr_code", length = 200)
    @Size(max = 200, message = "QR code must not exceed 200 characters")
    private String qrCode;

    @Column(name = "rfid_tag", length = 100)
    @Size(max = 100, message = "RFID tag must not exceed 100 characters")
    private String rfidTag;

    // Custom Attributes
    @ElementCollection
    @CollectionTable(
        name = "inventory_item_attributes",
        joinColumns = @JoinColumn(name = "inventory_item_id")
    )
    @MapKeyColumn(name = "attribute_name", length = 100)
    @Column(name = "attribute_value", length = 500)
    private Map<String, String> customAttributes;

    // Business Methods
    public BigDecimal getAvailableForSale() {
        return availableQuantity.subtract(reservedQuantity != null ? reservedQuantity : BigDecimal.ZERO);
    }

    public boolean isLowStock() {
        return reorderPoint != null && availableQuantity.compareTo(reorderPoint) <= 0;
    }

    public boolean isOverstock() {
        return maximumStockLevel != null && totalQuantity.compareTo(maximumStockLevel) > 0;
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDateTime.now().plusDays(daysThreshold));
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    public boolean canFulfillQuantity(BigDecimal requestedQuantity) {
        return getAvailableForSale().compareTo(requestedQuantity) >= 0;
    }

    public void updateStockLevels() {
        this.lastStockUpdate = LocalDateTime.now();
        this.totalValue = this.totalQuantity.multiply(this.averageCost != null ? this.averageCost : BigDecimal.ZERO);
    }

    public boolean requiresImmediateAttention() {
        return isLowStock() || isExpired() || 
               (status == InventoryStatus.DAMAGED || status == InventoryStatus.QUARANTINED);
    }

    public boolean isCriticallyLowStock() {
        return safetyStockLevel != null && availableQuantity.compareTo(safetyStockLevel) <= 0;
    }

    public boolean isOutOfStock() {
        return availableQuantity.compareTo(BigDecimal.ZERO) <= 0;
    }
}
