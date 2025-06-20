package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for order fulfillment optimization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationRequest {
    private List<UUID> orderIds;
    private UUID warehouseId;
    private List<String> optimizationObjectives;
    private Map<String, Integer> objectivePriorities;
    private boolean considerInventoryLevels;
    private boolean considerShippingCosts;
    private boolean considerDistance;
    private boolean considerHandlingTime;
    private Map<String, Object> additionalParameters;
}
