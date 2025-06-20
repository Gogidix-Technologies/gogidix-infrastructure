package com.exalt.warehousing.management.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight reference entity for inventory items from the inventory service
 * Used for JPA repository support in the warehouse management service
 */
@Entity
@Table(name = "inventory_item_reference")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryItemReference extends BaseEntity {

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "name")
    private String productName;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "available_quantity")
    private Integer availableQuantity;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}
