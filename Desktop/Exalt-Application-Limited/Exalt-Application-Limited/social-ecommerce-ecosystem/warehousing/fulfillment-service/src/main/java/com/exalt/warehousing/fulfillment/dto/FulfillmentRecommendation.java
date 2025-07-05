package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for fulfillment recommendations and optimization results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentRecommendation {
    private UUID requestId;
    private List<UUID> orderIds;
    private Map<UUID, UUID> recommendedWarehouseAssignments;
    private Map<UUID, List<UUID>> recommendedBatches;
    private Map<String, Object> optimizationMetrics;
    private double costSaving;
    private double timeSaving;
    private List<String> appliedRules;
    private String recommendationType;
    private List<Map<String, Object>> alternativeRecommendations;
}
