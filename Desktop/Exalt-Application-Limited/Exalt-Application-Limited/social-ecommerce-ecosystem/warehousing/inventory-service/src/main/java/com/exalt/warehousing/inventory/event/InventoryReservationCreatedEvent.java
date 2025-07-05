package com.exalt.warehousing.inventory.event;

import com.exalt.warehousing.shared.events.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when inventory is reserved for an order.
 * This triggers the fulfillment process to potentially begin order picking.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class InventoryReservationCreatedEvent extends BaseEvent {

    private UUID orderId;
    private UUID reservationId;
    private UUID warehouseId;
    private LocalDateTime expirationTime;
    private boolean isPartial;
    private int lineItemCount;
    private int fullyReservedCount;
    private UUID customerId;
    private String orderReference;
    private boolean isPriority;
    
    /**
     * Create a new instance with essential data
     * 
     * @param aggregateId typically the order ID
     * @param orderId the order ID
     * @param reservationId the reservation ID
     * @param warehouseId the warehouse ID
     * @return the event
     */
    public static InventoryReservationCreatedEvent create(
            UUID aggregateId,
            UUID orderId, 
            UUID reservationId, 
            UUID warehouseId) {
        
        return InventoryReservationCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(aggregateId)
                .eventType("INVENTORY_RESERVATION_CREATED")
                .orderId(orderId)
                .reservationId(reservationId)
                .warehouseId(warehouseId)
                .expirationTime(LocalDateTime.now().plusHours(24)) // Default 24-hour reservation
                .isPartial(false)
                .build();
    }
    
    /**
     * Create a new instance with all data fields
     */
    public static InventoryReservationCreatedEvent createDetailed(
            UUID aggregateId,
            UUID orderId, 
            UUID reservationId, 
            UUID warehouseId,
            LocalDateTime expirationTime,
            boolean isPartial,
            int lineItemCount,
            int fullyReservedCount,
            UUID customerId,
            String orderReference,
            boolean isPriority) {
        
        return InventoryReservationCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(aggregateId)
                .eventType("INVENTORY_RESERVATION_CREATED")
                .orderId(orderId)
                .reservationId(reservationId)
                .warehouseId(warehouseId)
                .expirationTime(expirationTime)
                .isPartial(isPartial)
                .lineItemCount(lineItemCount)
                .fullyReservedCount(fullyReservedCount)
                .customerId(customerId)
                .orderReference(orderReference)
                .isPriority(isPriority)
                .build();
    }
}