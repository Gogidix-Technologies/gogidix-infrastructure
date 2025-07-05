package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for reporting and handling picking exceptions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingExceptionRequest {
    private UUID pickingTaskId;
    private UUID fulfillmentOrderId;
    private UUID fulfillmentOrderItemId;
    private UUID warehouseId;
    private UUID locationId;
    private String exceptionType;
    private String exceptionReason;
    private String description;
    private Integer quantityAffected;
    private String reportedBy;
    private Map<String, Object> additionalDetails;
}
