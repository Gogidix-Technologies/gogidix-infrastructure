package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for order metrics and statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMetrics {
    private Integer pendingOrders;
    private Integer processingOrders;
    private Integer completedOrders;
    private Integer cancelledOrders;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private BigDecimal totalOrderValue;
    private Integer averageItemsPerOrder;
    private BigDecimal averageProcessingTimeHours;
    private BigDecimal orderAccuracyPercentage;
    private BigDecimal onTimeDeliveryPercentage;
    private String peakHour;
    private String performanceStatus;
}

