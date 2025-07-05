package com.exalt.warehousing.management.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing inventory movements for tracking and analytics
 * Used primarily for demand forecasting and inventory trend analysis
 */
@Entity
@Table(name = "inventory_movements", indexes = {
    @Index(name = "idx_inventory_movement_product", columnList = "product_id"),
    @Index(name = "idx_inventory_movement_warehouse", columnList = "warehouse_id"),
    @Index(name = "idx_inventory_movement_date", columnList = "movement_date"),
    @Index(name = "idx_inventory_movement_type", columnList = "movement_type"),
    @Index(name = "idx_inventory_movement_product_warehouse", columnList = "product_id, warehouse_id"),
    @Index(name = "idx_inventory_movement_product_date", columnList = "product_id, movement_date")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryMovement extends BaseEntity {

    /**
     * Product ID involved in the movement
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * SKU of the product
     */
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    /**
     * Warehouse where the movement occurred
     */
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    /**
     * Location ID where the movement occurred (if applicable)
     */
    @Column(name = "location_id")
    private UUID locationId;

    /**
     * Type of movement (INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT, RETURN)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 50)
    private MovementType movementType;

    /**
     * Sub-type for more granular classification
     */
    @Column(name = "movement_subtype", length = 100)
    private String movementSubtype;

    /**
     * Quantity moved (positive for inbound, negative for outbound)
     */
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    /**
     * Unit of measurement
     */
    @Column(name = "unit", length = 20)
    private String unit;

    /**
     * Date and time when the movement occurred
     */
    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    /**
     * Reference ID to the source document (order, transfer, adjustment, etc.)
     */
    @Column(name = "reference_id")
    private UUID referenceId;

    /**
     * Reference type (ORDER, TRANSFER, ADJUSTMENT, RETURN, etc.)
     */
    @Column(name = "reference_type", length = 50)
    private String referenceType;

    /**
     * Reference number (human-readable identifier)
     */
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    /**
     * Batch or lot number (if applicable)
     */
    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    /**
     * Serial number (if applicable)
     */
    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    /**
     * Cost per unit at the time of movement
     */
    @Column(name = "unit_cost", precision = 10, scale = 2)
    private Double unitCost;

    /**
     * Total cost of the movement
     */
    @Column(name = "total_cost", precision = 15, scale = 2)
    private Double totalCost;

    /**
     * Currency code
     */
    @Column(name = "currency", length = 3)
    private String currency;

    /**
     * Reason for the movement
     */
    @Column(name = "reason", length = 200)
    private String reason;

    /**
     * Staff member who initiated the movement
     */
    @Column(name = "initiated_by")
    private UUID initiatedBy;

    /**
     * Staff member who processed the movement
     */
    @Column(name = "processed_by")
    private UUID processedBy;

    /**
     * Indicates if this movement affects available inventory
     */
    @Column(name = "affects_available", nullable = false)
    private Boolean affectsAvailable = true;

    /**
     * Indicates if this movement is part of a larger transaction
     */
    @Column(name = "is_bundled", nullable = false)
    private Boolean isBundled = false;

    /**
     * Bundle ID if this movement is part of a larger transaction
     */
    @Column(name = "bundle_id")
    private UUID bundleId;

    /**
     * Additional notes or comments
     */
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * Quality status at the time of movement
     */
    @Column(name = "quality_status", length = 50)
    private String qualityStatus;

    /**
     * Expiration date (if applicable)
     */
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    /**
     * Temperature at the time of movement (if applicable)
     */
    @Column(name = "temperature")
    private Double temperature;

    /**
     * Humidity at the time of movement (if applicable)
     */
    @Column(name = "humidity")
    private Double humidity;

    /**
     * Enum for movement types
     */
    public enum MovementType {
        /**
         * Stock coming into the warehouse (receiving, returns, adjustments up)
         */
        INBOUND,
        
        /**
         * Stock going out of the warehouse (shipping, transfers out, adjustments down)
         */
        OUTBOUND,
        
        /**
         * Internal transfers between locations or warehouses
         */
        TRANSFER,
        
        /**
         * Inventory adjustments (cycle counts, corrections)
         */
        ADJUSTMENT,
        
        /**
         * Customer returns being processed
         */
        RETURN,
        
        /**
         * Quality control movements (quarantine, inspection)
         */
        QUALITY_CONTROL,
        
        /**
         * Manufacturing or production movements
         */
        PRODUCTION,
        
        /**
         * Damage or disposal movements
         */
        DISPOSAL
    }

    /**
     * Calculate absolute quantity (for reporting purposes)
     *
     * @return absolute value of quantity
     */
    public Long getAbsoluteQuantity() {
        return Math.abs(quantity);
    }

    /**
     * Determine if this is an inbound movement
     *
     * @return true if movement increases inventory
     */
    public boolean isInboundMovement() {
        return MovementType.INBOUND.equals(movementType) || 
               (MovementType.ADJUSTMENT.equals(movementType) && quantity > 0) ||
               MovementType.RETURN.equals(movementType);
    }

    /**
     * Determine if this is an outbound movement
     *
     * @return true if movement decreases inventory
     */
    public boolean isOutboundMovement() {
        return MovementType.OUTBOUND.equals(movementType) || 
               (MovementType.ADJUSTMENT.equals(movementType) && quantity < 0) ||
               MovementType.DISPOSAL.equals(movementType);
    }

    /**
     * Get movement direction as string
     *
     * @return "IN", "OUT", or "INTERNAL"
     */
    public String getMovementDirection() {
        if (isInboundMovement()) {
            return "IN";
        } else if (isOutboundMovement()) {
            return "OUT";
        } else {
            return "INTERNAL";
        }
    }

    /**
     * Calculate total value of the movement
     *
     * @return total value (quantity * unit cost)
     */
    public Double getTotalValue() {
        if (unitCost == null || quantity == null) {
            return totalCost;
        }
        return Math.abs(quantity) * unitCost;
    }
}
