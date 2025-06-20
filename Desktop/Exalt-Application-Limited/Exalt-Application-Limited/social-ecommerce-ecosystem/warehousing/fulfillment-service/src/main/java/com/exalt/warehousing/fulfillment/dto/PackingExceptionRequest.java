package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for reporting and handling packing exceptions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackingExceptionRequest {
    private UUID packingTaskId;
    private UUID fulfillmentOrderId;
    private UUID fulfillmentOrderItemId;
    private UUID warehouseId;
    private String exceptionType;
    private String exceptionReason;
    private String description;
    private Integer quantityAffected;
    private String reportedBy;
    private UUID shipmentPackageId;
    private Map<String, Object> additionalDetails;
}
