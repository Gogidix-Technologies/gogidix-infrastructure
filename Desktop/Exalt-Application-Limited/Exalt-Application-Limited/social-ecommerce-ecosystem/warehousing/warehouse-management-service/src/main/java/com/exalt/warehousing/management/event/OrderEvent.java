package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event model for order changes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private UUID orderId;
    private OrderEventType type;
    private List<OrderItemDTO> orderItems;

    /**
     * Event types for order changes
     */
    public enum OrderEventType {
        CREATED,
        UPDATED,
        CANCELLED,
        COMPLETED,
        ITEM_ADDED,
        ITEM_REMOVED,
        ITEM_UPDATED,
        FULFILLMENT_STATUS_CHANGED
    }
} 
