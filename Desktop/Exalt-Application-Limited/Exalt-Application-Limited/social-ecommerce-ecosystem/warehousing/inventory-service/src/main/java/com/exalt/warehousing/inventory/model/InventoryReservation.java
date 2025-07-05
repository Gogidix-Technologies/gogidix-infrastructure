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
import java.util.List;
import java.util.UUID;

/**
 * Entity for tracking temporary inventory reservations
 * These reservations are time-bound and prevent inventory from being
 * double-allocated during the checkout process
 */
@Entity
@Table(name = "inventory_reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "inventory_item_id", nullable = false)
    private UUID inventoryItemId;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id")
    private UUID userId;

    @NotNull
    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "expiration_minutes")
    private Integer expirationMinutes;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationItem> items;

    /**
     * Convenience method to get the expiration time
     * @return the expiration time
     */
    public LocalDateTime getExpiresAt() {
        return expirationTime;
    }

    /**
     * Checks if this reservation has expired
     * @return true if current time is after expiration time
     */
    @Transient
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    /**
     * Checks if this reservation is in a terminal state
     * @return true if completed or cancelled
     */
    @Transient
    public boolean isFinalized() {
        return status == ReservationStatus.COMPLETED || 
               status == ReservationStatus.CANCELLED || 
               status == ReservationStatus.EXPIRED;
    }

    /**
     * Extends the expiration time by the specified minutes
     * @param minutes additional minutes to extend
     */
    public void extendExpiration(int minutes) {
        if (!isFinalized()) {
            this.expirationTime = this.expirationTime.plusMinutes(minutes);
        }
    }
}
