package com.exalt.warehousing.fulfillment.dto;

import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for fulfillment order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentOrderDTO {

    private UUID id;

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    private String orderReference;

    private FulfillmentStatus status;

    private UUID assignedWarehouseId;

    private Integer priority;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    @NotBlank(message = "Shipping address line 1 is required")
    private String shippingAddressLine1;

    private String shippingAddressLine2;

    @NotBlank(message = "Shipping city is required")
    private String shippingCity;

    @NotBlank(message = "Shipping state/province is required")
    private String shippingStateProvince;

    @NotBlank(message = "Shipping postal code is required")
    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    private String shippingCountry;

    private String shippingInstructions;

    @NotNull(message = "Shipping method is required")
    private ShippingMethod shippingMethod;

    private String carrier;

    private String trackingNumber;

    private LocalDateTime processingStartedAt;

    private LocalDateTime pickingStartedAt;

    private LocalDateTime pickingCompletedAt;

    private LocalDateTime packingStartedAt;

    private LocalDateTime packingCompletedAt;

    private LocalDateTime shippedAt;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime deliveredAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    private String notes;

    @Valid
    private List<FulfillmentOrderItemDTO> items;

    private Integer itemCount;

    private boolean completed;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 
