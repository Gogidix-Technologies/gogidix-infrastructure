package com.exalt.warehousing.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking all inventory movements and changes
 * Provides a complete audit trail of inventory changes
 */
@Entity
@Table(name = "inventory_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "inventory_item_id", nullable = false)
    private UUID inventoryItemId;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "note")
    private String note;

    @NotNull
    @Column(name = "user_id")
    private UUID userId;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /**
     * Creates a transaction record for stock receipt
     * @param inventoryItemId the inventory item
     * @param warehouseId the warehouse
     * @param quantity amount received
     * @param referenceId reference to purchase order
     * @param userId user who received
     * @return transaction record
     */
    public static InventoryTransaction createReceiptTransaction(
            UUID inventoryItemId, UUID warehouseId, int quantity, 
            UUID referenceId, UUID userId, String note) {
        
        return InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(Math.abs(quantity))
                .type(TransactionType.PURCHASE)
                .referenceId(referenceId)
                .referenceType("PURCHASE_ORDER")
                .userId(userId)
                .note(note)
                .build();
    }

    /**
     * Creates a transaction record for sales deduction
     * @param inventoryItemId the inventory item
     * @param warehouseId the warehouse
     * @param quantity amount sold (positive)
     * @param orderId reference to sales order
     * @param userId user who processed
     * @return transaction record
     */
    public static InventoryTransaction createSaleTransaction(
            UUID inventoryItemId, UUID warehouseId, int quantity, 
            UUID orderId, UUID userId) {
        
        return InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(-Math.abs(quantity))  // Negative for deductions
                .type(TransactionType.SALE)
                .referenceId(orderId)
                .referenceType("SALES_ORDER")
                .userId(userId)
                .build();
    }

    /**
     * Creates a transaction record for returned items
     * @param inventoryItemId the inventory item
     * @param warehouseId the warehouse
     * @param quantity amount returned
     * @param returnId reference to return
     * @param userId user who processed
     * @return transaction record
     */
    public static InventoryTransaction createReturnTransaction(
            UUID inventoryItemId, UUID warehouseId, int quantity, 
            UUID returnId, UUID userId, String note) {
        
        return InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(Math.abs(quantity))
                .type(TransactionType.RETURN)
                .referenceId(returnId)
                .referenceType("RETURN")
                .userId(userId)
                .note(note)
                .build();
    }

    /**
     * Creates a transaction record for inventory transfers
     * @param inventoryItemId the inventory item
     * @param sourceWarehouseId source warehouse
     * @param targetWarehouseId target warehouse
     * @param quantity amount transferred
     * @param transferId reference to transfer
     * @param userId user who processed
     * @return pair of transactions (source and target)
     */
    public static InventoryTransaction[] createTransferTransactions(
            UUID inventoryItemId, UUID sourceWarehouseId, UUID targetWarehouseId,
            int quantity, UUID transferId, UUID userId) {
        
        InventoryTransaction sourceTransaction = InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(sourceWarehouseId)
                .quantity(-Math.abs(quantity))  // Negative for source
                .type(TransactionType.TRANSFER)
                .referenceId(transferId)
                .referenceType("TRANSFER")
                .userId(userId)
                .note("Transfer out to warehouse: " + targetWarehouseId)
                .build();
                
        InventoryTransaction targetTransaction = InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(targetWarehouseId)
                .quantity(Math.abs(quantity))   // Positive for target
                .type(TransactionType.TRANSFER)
                .referenceId(transferId)
                .referenceType("TRANSFER")
                .userId(userId)
                .note("Transfer in from warehouse: " + sourceWarehouseId)
                .build();
                
        return new InventoryTransaction[] { sourceTransaction, targetTransaction };
    }

    /**
     * Creates a transaction record for inventory adjustments
     * @param inventoryItemId the inventory item
     * @param warehouseId the warehouse
     * @param quantity adjustment amount (positive or negative)
     * @param userId user who adjusted
     * @param note reason for adjustment
     * @return transaction record
     */
    public static InventoryTransaction createAdjustmentTransaction(
            UUID inventoryItemId, UUID warehouseId, int quantity, 
            UUID userId, String note) {
        
        return InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(quantity)  // Can be positive or negative
                .type(TransactionType.ADJUSTMENT)
                .userId(userId)
                .note(note)
                .build();
    }
}
