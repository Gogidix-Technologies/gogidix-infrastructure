package com.exalt.warehousing.inventory.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity tracking inventory movement for analytics and demand forecasting.
 * Records all movement of inventory within and between warehouses.
 */
@Entity
@Table(name = "inventory_movement")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryMovement extends BaseEntity {

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "source_warehouse_id")
    private UUID sourceWarehouseId;

    @Column(name = "source_location_id")
    private UUID sourceLocationId;

    @Column(name = "target_warehouse_id")
    private UUID targetWarehouseId;

    @Column(name = "target_location_id")
    private UUID targetLocationId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "initiated_by")
    private UUID initiatedBy;

    @Column(name = "reason")
    private String reason;

    @Column(name = "notes")
    private String notes;

    /**
     * Types of inventory movement
     */
    public enum MovementType {
        STOCK_RECEIPT,       // New inventory received
        PICK,                // Inventory picked for fulfillment
        PUTAWAY,             // Inventory put away in storage
        TRANSFER_OUT,        // Inventory transferred to another warehouse
        TRANSFER_IN,         // Inventory received from another warehouse
        RETURN,              // Returned inventory
        ADJUSTMENT_POSITIVE, // Positive inventory adjustment (e.g., found inventory)
        ADJUSTMENT_NEGATIVE, // Negative inventory adjustment (e.g., damaged inventory)
        CYCLE_COUNT,         // Adjustment from cycle count
        REBALANCE,           // Rebalancing inventory between locations
        RETURN_TO_VENDOR,    // Returning inventory to vendor
        DISPOSE,             // Disposing of inventory
        OTHER                // Other movement types
    }

    /**
     * Create a movement record for a stock receipt
     */
    public static InventoryMovement createStockReceipt(
            String sku, UUID productId, UUID warehouseId, UUID locationId, 
            Integer quantity, UUID referenceId, UUID initiatedBy) {
        
        return InventoryMovement.builder()
                .sku(sku)
                .productId(productId)
                .targetWarehouseId(warehouseId)
                .targetLocationId(locationId)
                .quantity(quantity)
                .movementType(MovementType.STOCK_RECEIPT)
                .referenceId(referenceId)
                .referenceType("STOCK_RECEIPT")
                .movementDate(LocalDateTime.now())
                .initiatedBy(initiatedBy)
                .createdBy(initiatedBy.toString())
                .build();
    }

    /**
     * Create a movement record for an inventory transfer between warehouses
     */
    public static InventoryMovement createTransfer(
            String sku, UUID productId, UUID sourceWarehouseId, UUID sourceLocationId,
            UUID targetWarehouseId, UUID targetLocationId, Integer quantity, 
            UUID transferId, UUID initiatedBy, String reason) {
        
        return InventoryMovement.builder()
                .sku(sku)
                .productId(productId)
                .sourceWarehouseId(sourceWarehouseId)
                .sourceLocationId(sourceLocationId)
                .targetWarehouseId(targetWarehouseId)
                .targetLocationId(targetLocationId)
                .quantity(quantity)
                .movementType(MovementType.TRANSFER_OUT)
                .referenceId(transferId)
                .referenceType("TRANSFER")
                .movementDate(LocalDateTime.now())
                .initiatedBy(initiatedBy)
                .createdBy(initiatedBy.toString())
                .reason(reason)
                .build();
    }

    /**
     * Create a movement record for a pick operation
     */
    public static InventoryMovement createPick(
            String sku, UUID productId, UUID warehouseId, UUID locationId,
            Integer quantity, UUID pickingTaskId, UUID initiatedBy) {
        
        return InventoryMovement.builder()
                .sku(sku)
                .productId(productId)
                .sourceWarehouseId(warehouseId)
                .sourceLocationId(locationId)
                .quantity(quantity)
                .movementType(MovementType.PICK)
                .referenceId(pickingTaskId)
                .referenceType("PICKING_TASK")
                .movementDate(LocalDateTime.now())
                .initiatedBy(initiatedBy)
                .createdBy(initiatedBy.toString())
                .build();
    }
}

