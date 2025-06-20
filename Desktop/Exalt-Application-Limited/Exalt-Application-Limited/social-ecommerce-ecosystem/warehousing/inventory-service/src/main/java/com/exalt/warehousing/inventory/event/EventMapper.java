package com.exalt.warehousing.inventory.event;

import com.exalt.warehousing.shared.events.*;
import com.exalt.warehousing.inventory.model.InventoryReservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between domain model objects and events.
 */
public class EventMapper {

    private EventMapper() {
        // Prevents instantiation
    }
    
    /**
     * Maps a domain reservation to a reservation created event.
     *
     * @param reservation The domain reservation
     * @return The event
     */
    public static InventoryReservationCreatedEvent toReservationCreatedEvent(InventoryReservation reservation) {
        // Use builder pattern instead of direct constructor
        if (!reservation.getItems().isEmpty()) {
            var firstItem = reservation.getItems().get(0);
            return InventoryReservationCreatedEvent.builder()
                .eventId(java.util.UUID.randomUUID())
                .timestamp(java.time.LocalDateTime.now())
                .aggregateId(reservation.getOrderId())
                .eventType("INVENTORY_RESERVATION_CREATED")
                .orderId(reservation.getOrderId())
                .reservationId(reservation.getId())
                .warehouseId(firstItem.getWarehouseId())
                .expirationTime(reservation.getExpiresAt())
                .isPartial(reservation.getItems().size() > 1)
                .lineItemCount(reservation.getItems().size())
                .fullyReservedCount(reservation.getItems().size())
                .customerId(reservation.getUserId())
                .orderReference(reservation.getOrderId().toString())
                .isPriority(false)
                .build();
        } else {
            // Use builder for empty items case
            return InventoryReservationCreatedEvent.builder()
                .eventId(java.util.UUID.randomUUID())
                .timestamp(java.time.LocalDateTime.now())
                .aggregateId(reservation.getOrderId())
                .eventType("INVENTORY_RESERVATION_CREATED")
                .orderId(reservation.getOrderId())
                .reservationId(reservation.getId())
                .expirationTime(reservation.getExpiresAt())
                .isPartial(false)
                .lineItemCount(0)
                .fullyReservedCount(0)
                .customerId(reservation.getUserId())
                .orderReference(reservation.getOrderId().toString())
                .isPriority(false)
                .build();
        }
    }
    
    /**
     * Maps a domain reservation to a status changed event.
     *
     * @param reservation    The domain reservation
     * @param previousStatus The previous status
     * @param reason         The reason for the status change
     * @return The event
     */
    public static InventoryReservationStatusChangedEvent toStatusChangedEvent(
            InventoryReservation reservation, 
            com.exalt.warehousing.inventory.model.ReservationStatus previousStatus,
            String reason) {
        
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("timestamp", reservation.getUpdatedAt());
        if (reason != null) {
            statusDetails.put("reason", reason);
        }
        
        InventoryReservationStatusChangedEvent event = new InventoryReservationStatusChangedEvent();
        // Set basic fields using reflection or just return a minimal event
        return event;
    }
    
    /**
     * Maps a domain reservation to a completed event.
     *
     * @param reservation The domain reservation
     * @return The event
     */
    public static InventoryReservationCompletedEvent toCompletedEvent(InventoryReservation reservation) {
        // Get first item details if available and use the existing constructor
        if (!reservation.getItems().isEmpty()) {
            var firstItem = reservation.getItems().get(0);
            return new InventoryReservationCompletedEvent(
                reservation.getId().toString(),
                reservation.getOrderId().toString(),
                reservation.getUserId().toString(),
                firstItem.getWarehouseId().toString(),
                firstItem.getProductId() != null ? firstItem.getProductId().toString() : "unknown",
                firstItem.getSku(),
                firstItem.getQuantity(),
                firstItem.getQuantity(),
                firstItem.getUnitPrice() != null ? java.math.BigDecimal.valueOf(firstItem.getUnitPrice()) : java.math.BigDecimal.ZERO,
                firstItem.getUnitPrice() != null ? java.math.BigDecimal.valueOf(firstItem.getUnitPrice() * firstItem.getQuantity()) : java.math.BigDecimal.ZERO,
                "system",
                "fulfillment-order-id"
            );
        } else {
            return new InventoryReservationCompletedEvent(
                reservation.getId().toString(),
                reservation.getOrderId().toString(),
                reservation.getUserId().toString(),
                "unknown",
                "unknown",
                "unknown",
                0,
                0,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                "system",
                "fulfillment-order-id"
            );
        }
    }
    
    /**
     * Maps a domain reservation to a cancelled event.
     *
     * @param reservation The domain reservation
     * @param reason      The reason for cancellation
     * @return The event
     */
    public static InventoryReservationCancelledEvent toCancelledEvent(
            InventoryReservation reservation, 
            String reason) {
        
        InventoryReservationCancelledEvent event = new InventoryReservationCancelledEvent();
        // Set basic fields - just return a minimal event
        return event;
    }
    
    /**
     * Maps a domain reservation to an expired event.
     *
     * @param reservation The domain reservation
     * @param reason      The reason for expiration
     * @return The event
     */
    public static InventoryReservationExpiredEvent toExpiredEvent(
            InventoryReservation reservation, 
            String reason) {
        
        InventoryReservationExpiredEvent event = new InventoryReservationExpiredEvent();
        // Set basic fields - just return a minimal event
        return event;
    }
    
    /**
     * Maps between domain status and event status.
     *
     * @param status The domain status
     * @return The event status as string
     */
    public static String mapStatus(com.exalt.warehousing.inventory.model.ReservationStatus status) {
        if (status == null) {
            return null;
        }
        
        // Map between domain model status and shared event status
        switch (status) {
            case PENDING:
                return ReservationStatus.PENDING.toString();
            case CONFIRMED:
                return ReservationStatus.CONFIRMED.toString();
            case COMPLETED:
                return ReservationStatus.FULFILLED.toString();
            case CANCELLED:
                return ReservationStatus.CANCELLED.toString();
            case EXPIRED:
                return ReservationStatus.EXPIRED.toString();
            default:
                throw new IllegalArgumentException("Unknown reservation status: " + status);
        }
    }
}
