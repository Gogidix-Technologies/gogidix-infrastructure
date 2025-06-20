package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Report DTO for fulfillment performance metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentPerformanceReport {
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer cancelledOrders;
    private BigDecimal completionRate;
    private BigDecimal averageProcessingTime;
    private BigDecimal averagePickingTime;
    private BigDecimal averagePackingTime;
    private BigDecimal averageShippingTime;
    private Integer totalItemsPicked;
    private Integer totalItemsPacked;
    private Integer totalShipments;
    private BigDecimal orderAccuracyRate;
    private BigDecimal onTimeDeliveryRate;
    private String performanceGrade;
    private String recommendations;
}

