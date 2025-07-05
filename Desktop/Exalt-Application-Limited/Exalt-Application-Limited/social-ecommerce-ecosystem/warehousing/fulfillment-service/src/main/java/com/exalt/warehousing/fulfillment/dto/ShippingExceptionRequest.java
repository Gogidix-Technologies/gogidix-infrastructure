package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for reporting and handling shipping exceptions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingExceptionRequest {
    private UUID shipmentPackageId;
    private UUID fulfillmentOrderId;
    private UUID warehouseId;
    private String exceptionType;
    private String exceptionReason;
    private String description;
    private String reportedBy;
    private String carrierName;
    private String trackingNumber;
    private Map<String, Object> additionalDetails;
}
