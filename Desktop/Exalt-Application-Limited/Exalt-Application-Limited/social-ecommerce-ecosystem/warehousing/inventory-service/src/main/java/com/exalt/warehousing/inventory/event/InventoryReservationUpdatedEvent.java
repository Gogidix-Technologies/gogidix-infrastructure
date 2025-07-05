package com.exalt.warehousing.inventory.event;

import com.exalt.warehousing.shared.events.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when an inventory reservation is updated.
 * This might happen when items become available or the reservation changes in some way.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class InventoryReservationUpdatedEvent extends BaseEvent {

    private UUID orderId;
    private UUID reservationId;
    private UUID warehouseId;
    private LocalDateTime expirationTime;
    private boolean isPartial;
    private int lineItemCount;
    private int fullyReservedCount;
    private String updateReason;
    private boolean isPriority;
    private String previousStatus;
    private String newStatus;
    
    /**
     * Create a new instance with essential data
     * 
     * @param aggregateId typically the order ID
     * @param orderId the order ID
     * @param reservationId the reservation ID
     * @param warehouseId the warehouse ID
     * @param updateReason reason for the update
     * @return the event
     */
    public static InventoryReservationUpdatedEvent create(
            UUID aggregateId,
            UUID orderId, 
            UUID reservationId, 
            UUID warehouseId,
            String updateReason) {
        
        return InventoryReservationUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(aggregateId)
                .eventType("INVENTORY_RESERVATION_UPDATED")
                .orderId(orderId)
                .reservationId(reservationId)
                .warehouseId(warehouseId)
                .updateReason(updateReason)
                .build();
    }
    
    /**
     * Create a new instance with all data fields
     */
    public static InventoryReservationUpdatedEvent createDetailed(
            UUID aggregateId,
            UUID orderId, 
            UUID reservationId, 
            UUID warehouseId,
            LocalDateTime expirationTime,
            boolean isPartial,
            int lineItemCount,
            int fullyReservedCount,
            String updateReason,
            boolean isPriority,
            String previousStatus,
            String newStatus) {
        
        return InventoryReservationUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(aggregateId)
                .eventType("INVENTORY_RESERVATION_UPDATED")
                .orderId(orderId)
                .reservationId(reservationId)
                .warehouseId(warehouseId)
                .expirationTime(expirationTime)
                .isPartial(isPartial)
                .lineItemCount(lineItemCount)
                .fullyReservedCount(fullyReservedCount)
                .updateReason(updateReason)
                .isPriority(isPriority)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .build();
    }
}