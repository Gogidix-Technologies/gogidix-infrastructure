package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for updating fulfillment orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFulfillmentOrderRequest {
    private UUID orderId;
    private String status;
    private String priority;
    private String notes;
    private UUID assignedPickerId;
    private String shippingAddress;
    private String trackingNumber;
    private String courierCode;
}

