package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for picking path optimization requests and results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingPathOptimization {
    private UUID batchId;
    private UUID warehouseId;
    private List<UUID> taskIds;
    private List<Map<String, Object>> locations;
    private List<Map<String, Object>> optimizedPath;
    private double distanceSaving;
    private double timeSaving;
    private String algorithm;
    private Map<String, Object> optimizationMetrics;
}
