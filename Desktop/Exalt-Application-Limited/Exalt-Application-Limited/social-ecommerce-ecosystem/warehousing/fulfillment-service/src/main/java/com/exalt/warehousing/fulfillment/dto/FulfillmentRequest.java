package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request object for fulfillment operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentRequest {
    
    private UUID orderId;
    private UUID customerId;
    private List<FulfillmentOrderItemDTO> items;
    private String priority;
    private String shippingAddress;
    private String specialInstructions;
}