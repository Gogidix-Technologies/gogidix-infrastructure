package com.exalt.warehousing.inventory.entity;

import com.exalt.warehousing.inventory.enums.TransactionType;
import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an inventory transaction
 * Tracks all inventory movements and changes
 */
@Entity
@Table(name = "inventory_transactions")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction extends BaseEntity {

    /**
     * Reference to the inventory item
     */
    @Column(name = "inventory_item_id", nullable = false)
    @NotNull(message = "Inventory item ID is required")
    private UUID inventoryItemId;

    /**
     * Reference to the warehouse
     */
    @Column(name = "warehouse_id", nullable = false)
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    /**
     * Type of transaction
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    /**
     * Quantity involved in the transaction
     * Positive for incoming, negative for outgoing
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    /**
     * Quantity before the transaction
     */
    @Column(name = "quantity_before")
    private Integer quantityBefore;

    /**
     * Quantity after the transaction
     */
    @Column(name = "quantity_after")
    private Integer quantityAfter;

    /**
     * Unit cost of the items in this transaction
     */
    @Column(name = "unit_cost", precision = 19, scale = 2)
    private BigDecimal unitCost;

    /**
     * Total cost of the transaction
     */
    @Column(name = "total_cost", precision = 19, scale = 2)
    private BigDecimal totalCost;

    /**
     * Timestamp when the transaction occurred
     */
    @Column(name = "transaction_timestamp", nullable = false)
    @NotNull(message = "Transaction timestamp is required")
    private LocalDateTime timestamp;

    /**
     * Reference ID for the source of this transaction
     * Could be order ID, purchase order ID, adjustment ID, etc.
     */
    @Column(name = "reference_id")
    private UUID referenceId;

    /**
     * Type of reference (ORDER, PURCHASE_ORDER, ADJUSTMENT, etc.)
     */
    @Column(name = "reference_type", length = 50)
    private String referenceType;

    /**
     * User who initiated the transaction
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Location where the transaction occurred
     */
    @Column(name = "location_id")
    private UUID locationId;

    /**
     * Batch number if applicable
     */
    @Column(name = "batch_number")
    private String batchNumber;

    /**
     * Serial number if applicable
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * Expiration date for the items if applicable
     */
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    /**
     * Notes or comments about the transaction
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Whether this transaction was automatically generated
     */
    @Column(name = "is_automatic", nullable = false)
    private Boolean isAutomatic = false;

    /**
     * Status of the transaction
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "COMPLETED";

    /**
     * External transaction ID for integration purposes
     */
    @Column(name = "external_transaction_id")
    private String externalTransactionId;

    /**
     * Source system that generated this transaction
     */
    @Column(name = "source_system", length = 50)
    private String sourceSystem;

    /**
     * Calculate total cost based on quantity and unit cost
     */
    @PrePersist
    @PreUpdate
    public void calculateTotalCost() {
        if (quantity != null && unitCost != null) {
            this.totalCost = unitCost.multiply(BigDecimal.valueOf(Math.abs(quantity)));
        }
    }

    /**
     * Set the timestamp to current time if not provided
     */
    @PrePersist
    public void setDefaultTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    /**
     * Check if this is an incoming transaction (positive quantity)
     */
    public boolean isIncoming() {
        return quantity != null && quantity > 0;
    }

    /**
     * Check if this is an outgoing transaction (negative quantity)
     */
    public boolean isOutgoing() {
        return quantity != null && quantity < 0;
    }

    /**
     * Get absolute quantity value
     */
    public Integer getAbsoluteQuantity() {
        return quantity != null ? Math.abs(quantity) : 0;
    }

    /**
     * Create a sale transaction
     */
    public static InventoryTransaction createSaleTransaction(UUID inventoryItemId, UUID warehouseId, 
                                                           Integer quantity, UUID orderId, UUID userId) {
        return InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(-Math.abs(quantity))  // Negative for sale
                .type(TransactionType.SALE)
                .referenceId(orderId)
                .referenceType("ORDER")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .notes("Sale transaction for order: " + orderId)
                .build();
    }
}