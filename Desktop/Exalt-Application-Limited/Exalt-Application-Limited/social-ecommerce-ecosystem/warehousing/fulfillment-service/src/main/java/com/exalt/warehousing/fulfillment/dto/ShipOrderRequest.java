package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for shipping orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipOrderRequest {
    private UUID orderId;
    private String courierCode;
    private String serviceType;
    private String trackingNumber;
    private BigDecimal shippingCost;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime estimatedDelivery;
    private String specialInstructions;
    private String packageType;
    private BigDecimal weight;
    private String dimensions;
}

