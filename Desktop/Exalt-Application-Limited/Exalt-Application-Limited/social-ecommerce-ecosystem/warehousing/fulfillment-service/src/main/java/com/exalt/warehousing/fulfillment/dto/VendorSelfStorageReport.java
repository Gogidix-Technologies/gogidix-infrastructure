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
 * DTO for vendor self-storage reporting and metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorSelfStorageReport {
    private UUID vendorId;
    private LocalDateTime reportTimestamp;
    private int totalInventoryItems;
    private double totalInventoryValue;
    private int totalStorageLocations;
    private double storageUtilizationPercentage;
    private int activeListings;
    private int ordersProcessedLastMonth;
    private double averageFulfillmentTimeHours;
    private Map<String, Double> inventoryTurnoverByCategory;
    private List<Map<String, Object>> stockLevelAlerts;
    private List<Map<String, Object>> performanceMetrics;
}
