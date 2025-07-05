package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for courier integration and label generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierIntegrationRequest {
    private UUID shipmentPackageId;
    private String courierProvider;
    private String shippingMethod;
    private Map<String, String> deliveryAddress;
    private String trackingReference;
    private double weight;
    private double length;
    private double width;
    private double height;
    private String weightUnit;
    private String dimensionUnit;
    private List<Map<String, Object>> packageContents;
    private Map<String, Object> additionalParameters;
}
