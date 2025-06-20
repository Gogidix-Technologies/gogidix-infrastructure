package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for fulfillment events and notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentEvent {
    private UUID eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private UUID fulfillmentOrderId;
    private UUID fulfillmentOrderItemId;
    private UUID warehouseId;
    private String userId;
    private String description;
    private Map<String, Object> eventData;
    private int severity;
    private boolean requiresAction;
    private boolean isAcknowledged;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
}

