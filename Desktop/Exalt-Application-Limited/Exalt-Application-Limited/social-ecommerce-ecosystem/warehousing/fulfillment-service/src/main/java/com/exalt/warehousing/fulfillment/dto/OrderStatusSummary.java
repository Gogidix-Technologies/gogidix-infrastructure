package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for summarizing order status and processing metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusSummary {
    private UUID warehouseId;
    private LocalDateTime timestamp;
    private int totalOrders;
    private Map<String, Integer> orderCountByStatus;
    private Map<String, Double> averageProcessingTimeByStatus;
    private int ordersCreatedToday;
    private int ordersCompletedToday;
    private int ordersCancelledToday;
    private int ordersDelayedToday;
    private double fulfillmentRatePercentage;
    private double onTimeDeliveryRatePercentage;
    private double averageCycleTimeHours;
}
