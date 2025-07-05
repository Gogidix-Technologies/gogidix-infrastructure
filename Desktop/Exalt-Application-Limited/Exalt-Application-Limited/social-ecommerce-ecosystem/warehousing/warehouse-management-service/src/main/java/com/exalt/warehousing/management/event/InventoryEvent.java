package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.dto.InventoryItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event model for inventory changes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private UUID inventoryItemId;
    private UUID warehouseId;
    private InventoryEventType type;
    private InventoryItemDTO inventoryItem;

    /**
     * Event types for inventory changes
     */
    public enum InventoryEventType {
        CREATED,
        UPDATED,
        DELETED,
        RELOCATED,
        QUANTITY_CHANGED,
        RESERVED,
        RELEASED
    }
} 
