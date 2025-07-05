package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a fulfillment business rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentRule {
    private UUID id;
    private String name;
    private String description;
    private String conditionExpression;
    private String actionExpression;
    private int priority;
    private boolean active;
    private UUID warehouseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
