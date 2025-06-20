package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing fulfillment settings configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentSettings {
    private UUID id;
    private UUID warehouseId;
    private boolean enableAutoAssignment;
    private boolean enableBatchProcessing;
    private boolean enableRoundRobinAllocation;
    private int defaultShippingMethodPriority;
    private int maxOrdersPerBatch;
    private List<String> priorityCustomerSegments;
    private List<String> priorityProductCategories;
    private int reservationTimeoutMinutes;
    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;
}
