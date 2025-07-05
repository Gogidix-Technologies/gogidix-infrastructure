package com.exalt.warehousing.inventory.event;

import com.exalt.warehousing.shared.events.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event triggered when inventory levels are manually adjusted.
 * Used for corrections, reconciliations after stocktakes, etc.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class InventoryAdjustmentEvent extends BaseEvent {

    private UUID inventoryId;
    private UUID productId;
    private String sku;
    private UUID warehouseId;
    private UUID locationId;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String adjustmentType;
    private String reason;
    private UUID adjustedBy;
    private String referenceNumber;
    private boolean requiresApproval;
    private UUID approvedBy;
    private boolean isSystemAdjustment;
    
    /**
     * Create a standard inventory adjustment event
     * 
     * @param inventoryId the inventory record ID
     * @param productId the product ID
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     * @param previousQuantity the quantity before adjustment
     * @param newQuantity the quantity after adjustment
     * @param reason the reason for adjustment
     * @param adjustedBy the staff member making the adjustment
     * @return the event
     */
    public static InventoryAdjustmentEvent create(
            UUID inventoryId,
            UUID productId,
            String sku,
            UUID warehouseId,
            Integer previousQuantity,
            Integer newQuantity,
            String reason,
            UUID adjustedBy) {
        
        return InventoryAdjustmentEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(inventoryId)
                .eventType("INVENTORY_ADJUSTED")
                .inventoryId(inventoryId)
                .productId(productId)
                .sku(sku)
                .warehouseId(warehouseId)
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .adjustmentType(newQuantity > previousQuantity ? "INCREASE" : "DECREASE")
                .reason(reason)
                .adjustedBy(adjustedBy)
                .requiresApproval(Math.abs(newQuantity - previousQuantity) > 10) // Require approval for large adjustments
                .isSystemAdjustment(false)
                .build();
    }
    
    /**
     * Create a system-initiated inventory adjustment event
     * 
     * @param inventoryId the inventory record ID
     * @param productId the product ID
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     * @param previousQuantity the quantity before adjustment
     * @param newQuantity the quantity after adjustment
     * @param reason the reason for adjustment
     * @param referenceNumber reference to associated process
     * @return the event
     */
    public static InventoryAdjustmentEvent createSystemAdjustment(
            UUID inventoryId,
            UUID productId,
            String sku,
            UUID warehouseId,
            Integer previousQuantity,
            Integer newQuantity,
            String reason,
            String referenceNumber) {
        
        return InventoryAdjustmentEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(inventoryId)
                .eventType("INVENTORY_ADJUSTED")
                .inventoryId(inventoryId)
                .productId(productId)
                .sku(sku)
                .warehouseId(warehouseId)
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .adjustmentType(newQuantity > previousQuantity ? "INCREASE" : "DECREASE")
                .reason(reason)
                .referenceNumber(referenceNumber)
                .requiresApproval(false)
                .isSystemAdjustment(true)
                .build();
    }
}