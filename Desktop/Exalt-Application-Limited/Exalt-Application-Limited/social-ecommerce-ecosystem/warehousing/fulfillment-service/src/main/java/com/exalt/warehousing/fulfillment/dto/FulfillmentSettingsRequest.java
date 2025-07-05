package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for updating fulfillment settings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentSettingsRequest {
    private UUID warehouseId;
    private boolean enableAutoAssignment;
    private boolean enableBatchProcessing;
    private boolean enableRoundRobinAllocation;
    private int defaultShippingMethodPriority;
    private int maxOrdersPerBatch;
    private List<String> priorityCustomerSegments;
    private List<String> priorityProductCategories;
    private int reservationTimeoutMinutes;
}
