package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event model for warehouse changes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private UUID locationId;
    private UUID warehouseId;
    private UUID zoneId;
    private WarehouseEventType type;
    private LocationDTO location;

    /**
     * Event types for warehouse changes
     */
    public enum WarehouseEventType {
        LOCATION_CREATED,
        LOCATION_UPDATED,
        LOCATION_DELETED,
        ZONE_CREATED,
        ZONE_UPDATED,
        ZONE_DELETED,
        STAFF_ASSIGNED,
        STAFF_UNASSIGNED,
        TASK_CREATED,
        TASK_ASSIGNED,
        TASK_COMPLETED
    }
} 
