package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for reporting warehouse fulfillment capacity metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentCapacityReport {
    private UUID warehouseId;
    private LocalDateTime reportTimestamp;
    private int maxDailyOrderCapacity;
    private int currentDailyOrderCount;
    private double capacityUtilizationPercentage;
    private int availablePickingCapacity;
    private int availablePackingCapacity;
    private int availableShippingCapacity;
    private Map<String, Integer> staffingByDepartment;
    private Map<String, Double> utilizationByDepartment;
    private List<Map<String, Object>> capacityForecast;
    private Map<String, Object> bottlenecks;
}
