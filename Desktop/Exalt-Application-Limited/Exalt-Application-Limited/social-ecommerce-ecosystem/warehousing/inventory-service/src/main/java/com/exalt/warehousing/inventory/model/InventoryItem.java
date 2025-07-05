package com.exalt.warehousing.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking inventory items with their quantities and thresholds
 */
@Entity
@Table(name = "inventory_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotBlank
    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Min(0)
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Min(0)
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Min(0)
    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;

    @Min(0)
    @Column(name = "safety_stock_level", nullable = false)
    private Integer safetyStockLevel;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InventoryStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculates the available quantity (total - reserved)
     * @return available quantity for new orders
     */
    @Transient
    public Integer getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    /**
     * Checks if the item is below its low stock threshold
     * @return true if available quantity is below threshold
     */
    @Transient
    public Boolean isLowStock() {
        return getAvailableQuantity() <= lowStockThreshold;
    }

    /**
     * Checks if the item is critically low (below safety stock level)
     * @return true if available quantity is below safety stock
     */
    @Transient
    public Boolean isCriticallyLowStock() {
        return getAvailableQuantity() <= safetyStockLevel;
    }

    /**
     * Checks if the item is out of stock
     * @return true if available quantity is zero or negative
     */
    @Transient
    public Boolean isOutOfStock() {
        return getAvailableQuantity() <= 0;
    }
}
