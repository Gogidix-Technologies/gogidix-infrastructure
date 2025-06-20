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
 * DTO for fulfillment dashboard data and metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentDashboard {
    private UUID warehouseId;
    private LocalDateTime reportTimestamp;
    private Map<String, Integer> ordersByStatus;
    private Map<String, Integer> shipmentsByStatus;
    private Map<String, Integer> tasksByStatus;
    private Map<String, Double> fulfillmentMetrics;
    private int totalOrdersPending;
    private int totalOrdersInProgress;
    private int totalOrdersCompleted;
    private int totalOrdersDelayed;
    private double averageProcessingTimeMinutes;
    private double averageCycleTimeMinutes;
    private List<Map<String, Object>> recentOrders;
    private List<Map<String, Object>> criticalAlerts;
}
