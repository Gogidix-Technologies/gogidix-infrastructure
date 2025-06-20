package com.exalt.warehousing.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Entity representing a single item within an inventory reservation.
 * Multiple reservation items can belong to a single inventory reservation.
 */
@Entity
@Table(name = "reservation_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @NotNull
    @Column(name = "inventory_item_id", nullable = false)
    private UUID inventoryItemId;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @NotNull
    @Column(name = "sku", nullable = false)
    private String sku;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "notes")
    private String notes;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "unit_price")
    private Double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", insertable = false, updatable = false)
    private InventoryReservation reservation;
}
