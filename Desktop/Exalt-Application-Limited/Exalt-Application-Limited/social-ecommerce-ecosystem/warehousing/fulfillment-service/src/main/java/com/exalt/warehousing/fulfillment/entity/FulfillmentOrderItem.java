package com.exalt.warehousing.fulfillment.entity;

import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Fulfillment Order Item Entity
 * 
 * Represents an individual item within a fulfillment order.
 * Tracks item-specific fulfillment details and status.
 */
@Entity
@Table(name = "fulfillment_order_items", indexes = {
    @Index(name = "idx_item_order", columnList = "fulfillment_order_id"),
    @Index(name = "idx_item_sku", columnList = "sku"),
    @Index(name = "idx_item_status", columnList = "status"),
    @Index(name = "idx_item_product", columnList = "product_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"fulfillmentOrder"})
public class FulfillmentOrderItem extends BaseEntity {

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_order_id", nullable = false)
    @NotNull(message = "Fulfillment order is required")
    private FulfillmentOrder fulfillmentOrder;

    // Item Identification
    @Column(name = "order_item_id", nullable = false, length = 100)
    @NotBlank(message = "Order item ID is required")
    @Size(max = 100, message = "Order item ID must not exceed 100 characters")
    private String orderItemId;

    @Column(name = "product_id", nullable = false, length = 100)
    @NotBlank(message = "Product ID is required")
    @Size(max = 100, message = "Product ID must not exceed 100 characters")
    private String productId;

    @Column(name = "sku", nullable = false, length = 100)
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @Column(name = "product_name", length = 500)
    @Size(max = 500, message = "Product name must not exceed 500 characters")
    private String productName;

    @Column(name = "product_image_url", length = 1000)
    @Size(max = 1000, message = "Product image URL must not exceed 1000 characters")
    private String productImageUrl;

    @Column(name = "variant_id", length = 100)
    @Size(max = 100, message = "Variant ID must not exceed 100 characters")
    private String variantId;

    @Column(name = "variant_name", length = 255)
    @Size(max = 255, message = "Variant name must not exceed 255 characters")
    private String variantName;

    // Quantities
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "quantity_picked")
    @Min(value = 0, message = "Quantity picked must be non-negative")
    private Integer quantityPicked = 0;

    @Column(name = "quantity_packed")
    @Min(value = 0, message = "Quantity packed must be non-negative")
    private Integer quantityPacked = 0;

    @Column(name = "quantity_fulfilled")
    @Min(value = 0, message = "Quantity fulfilled must be non-negative")
    private Integer quantityFulfilled = 0;

    @Column(name = "quantity_returned")
    @Min(value = 0, message = "Quantity returned must be non-negative")
    private Integer quantityReturned = 0;

    @Column(name = "quantity_damaged")
    @Min(value = 0, message = "Quantity damaged must be non-negative")
    private Integer quantityDamaged = 0;

    // Item Details
    @Column(name = "unit_price", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Total price must be non-negative")
    private BigDecimal totalPrice;

    @Column(name = "tax_amount", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount;

    @Column(name = "weight", precision = 10, scale = 3)
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weight;

    @Column(name = "weight_unit", length = 10)
    @Size(max = 10, message = "Weight unit must not exceed 10 characters")
    private String weightUnit = "KG";

    @Column(name = "dimensions", length = 100)
    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    private String dimensions;

    // Status
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private ItemFulfillmentStatus status = ItemFulfillmentStatus.PENDING;

    @Column(name = "status_reason", length = 500)
    @Size(max = 500, message = "Status reason must not exceed 500 characters")
    private String statusReason;

    // Location Information
    @Column(name = "warehouse_location", length = 100)
    @Size(max = 100, message = "Warehouse location must not exceed 100 characters")
    private String warehouseLocation;

    @Column(name = "bin_location", length = 50)
    @Size(max = 50, message = "Bin location must not exceed 50 characters")
    private String binLocation;

    @Column(name = "zone", length = 50)
    @Size(max = 50, message = "Zone must not exceed 50 characters")
    private String zone;

    @Column(name = "aisle", length = 20)
    @Size(max = 20, message = "Aisle must not exceed 20 characters")
    private String aisle;

    @Column(name = "shelf", length = 20)
    @Size(max = 20, message = "Shelf must not exceed 20 characters")
    private String shelf;

    // Tracking Information
    @Column(name = "lot_number", length = 100)
    @Size(max = 100, message = "Lot number must not exceed 100 characters")
    private String lotNumber;

    @Column(name = "serial_number", length = 100)
    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNumber;

    @Column(name = "batch_number", length = 100)
    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    // Processing Information
    @Column(name = "picked_at")
    private LocalDateTime pickedAt;

    @Column(name = "picked_by")
    private Long pickedBy;

    @Column(name = "packed_at")
    private LocalDateTime packedAt;

    @Column(name = "packed_by")
    private Long packedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private Long verifiedBy;

    // Special Handling
    @Column(name = "requires_special_handling", nullable = false)
    private Boolean requiresSpecialHandling = false;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "is_fragile", nullable = false)
    private Boolean isFragile = false;

    @Column(name = "is_hazmat", nullable = false)
    private Boolean isHazmat = false;

    @Column(name = "is_temperature_controlled", nullable = false)
    private Boolean isTemperatureControlled = false;

    @Column(name = "temperature_range", length = 50)
    @Size(max = 50, message = "Temperature range must not exceed 50 characters")
    private String temperatureRange;

    // Quality Control
    @Column(name = "quality_check_required", nullable = false)
    private Boolean qualityCheckRequired = false;

    @Column(name = "quality_check_passed", nullable = false)
    private Boolean qualityCheckPassed = false;

    @Column(name = "quality_notes", columnDefinition = "TEXT")
    private String qualityNotes;

    // Return Information
    @Column(name = "is_returnable", nullable = false)
    private Boolean isReturnable = true;

    @Column(name = "return_window_days")
    @Min(value = 0, message = "Return window days must be non-negative")
    private Integer returnWindowDays = 30;

    @Column(name = "return_reason", length = 500)
    @Size(max = 500, message = "Return reason must not exceed 500 characters")
    private String returnReason;

    // Notes
    @Column(name = "picking_notes", columnDefinition = "TEXT")
    private String pickingNotes;

    @Column(name = "packing_notes", columnDefinition = "TEXT")
    private String packingNotes;

    @Column(name = "shipping_notes", columnDefinition = "TEXT")
    private String shippingNotes;

    // Metadata
    @Column(name = "custom_attributes", columnDefinition = "JSON")
    @Convert(converter = JsonAttributeConverter.class)
    private Map<String, Object> customAttributes;

    // Helper methods
    public boolean isFullyPicked() {
        return quantityPicked != null && quantityPicked.equals(quantity);
    }

    public boolean isFullyPacked() {
        return quantityPacked != null && quantityPacked.equals(quantity);
    }

    public boolean isFullyFulfilled() {
        return quantityFulfilled != null && quantityFulfilled.equals(quantity);
    }
    
    public boolean isPartiallyFulfilled() {
        return quantityFulfilled != null && quantityFulfilled > 0 && quantityFulfilled < quantity;
    }
    
    public Integer getRemainingQuantity() {
        return quantity - (quantityFulfilled != null ? quantityFulfilled : 0);
    }

    public Integer getRemainingToPick() {
        return quantity - (quantityPicked != null ? quantityPicked : 0);
    }

    public Integer getRemainingToPack() {
        return quantity - (quantityPacked != null ? quantityPacked : 0);
    }

    public BigDecimal calculateItemWeight() {
        if (weight != null && quantity != null) {
            return weight.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    // Explicit getters/setters for fields that may have issues with Lombok
    public Boolean getRequiresSpecialHandling() {
        return requiresSpecialHandling != null ? requiresSpecialHandling : false;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Integer getQuantity() {
        return quantity;
    }
    
    // Additional setters for compatibility
    public void setFulfillmentOrder(FulfillmentOrder order) {
        this.fulfillmentOrder = order;
    }
    
    public void setStatus(ItemFulfillmentStatus status) {
        this.status = status;
    }
    
    public void setPickedAt(LocalDateTime pickedAt) {
        this.pickedAt = pickedAt;
    }
    
    public void setPickedBy(Long pickedBy) {
        this.pickedBy = pickedBy;
    }
    
    public void setQuantityPicked(Integer quantityPicked) {
        this.quantityPicked = quantityPicked;
    }
    
    public void setPackedAt(LocalDateTime packedAt) {
        this.packedAt = packedAt;
    }
    
    public void setPackedBy(Long packedBy) {
        this.packedBy = packedBy;
    }
    
    public void setQuantityPacked(Integer quantityPacked) {
        this.quantityPacked = quantityPacked;
    }
    
    public void setQuantityFulfilled(Integer quantityFulfilled) {
        this.quantityFulfilled = quantityFulfilled;
    }
    
    // Additional getters for service compatibility
    public ItemFulfillmentStatus getStatus() {
        return status;
    }
    
    public Integer getQuantityFulfilled() {
        return quantityFulfilled;
    }
    
    public Integer getQuantityPicked() {
        return quantityPicked;
    }
    
    public Integer getQuantityPacked() {
        return quantityPacked;
    }
}
