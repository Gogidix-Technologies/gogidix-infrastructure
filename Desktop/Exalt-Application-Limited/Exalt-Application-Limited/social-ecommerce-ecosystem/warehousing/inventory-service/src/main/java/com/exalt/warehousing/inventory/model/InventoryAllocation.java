package com.exalt.warehousing.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking inventory allocations across multiple warehouses
 */
@Entity
@Table(name = "inventory_allocations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"inventory_item_id", "warehouse_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "inventory_item_id", nullable = false)
    private UUID inventoryItemId;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Min(0)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Min(0)
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "bin_location")
    private String binLocation;

    @Column(name = "aisle")
    private String aisle;
    
    @Column(name = "rack")
    private String rack;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculates the available quantity (total - reserved)
     * @return available quantity for new orders in this warehouse
     */
    @Transient
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    /**
     * Reserves the specified quantity
     * @param amount quantity to reserve
     * @return true if reservation successful
     */
    public boolean reserve(int amount) {
        if (amount <= 0 || amount > getAvailableQuantity()) {
            return false;
        }
        reservedQuantity += amount;
        return true;
    }

    /**
     * Releases a previously reserved quantity
     * @param amount quantity to release
     * @return true if release successful
     */
    public boolean release(int amount) {
        if (amount <= 0 || amount > reservedQuantity) {
            return false;
        }
        reservedQuantity -= amount;
        return true;
    }

    /**
     * Commits a reservation to an actual deduction
     * @param amount quantity to commit
     * @return true if commit successful
     */
    public boolean commitReservation(int amount) {
        if (amount <= 0 || amount > reservedQuantity) {
            return false;
        }
        quantity -= amount;
        reservedQuantity -= amount;
        return true;
    }

    /**
     * Adds inventory to this warehouse location
     * @param amount quantity to add
     */
    public void addInventory(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }
}

