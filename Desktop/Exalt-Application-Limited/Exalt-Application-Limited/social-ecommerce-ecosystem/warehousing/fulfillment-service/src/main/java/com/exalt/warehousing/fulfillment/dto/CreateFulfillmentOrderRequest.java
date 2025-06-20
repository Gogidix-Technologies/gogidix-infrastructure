package com.exalt.warehousing.fulfillment.dto;

import com.exalt.warehousing.fulfillment.enums.FulfillmentType;
import com.exalt.warehousing.fulfillment.enums.Priority;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Create Fulfillment Order Request DTO
 * 
 * Supports both traditional warehousing and vendor self-storage fulfillment models.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Data
public class CreateFulfillmentOrderRequest {

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderNumber;

    @Size(max = 100, message = "External order ID must not exceed 100 characters")
    private String externalOrderId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @NotNull(message = "Fulfillment type is required")
    private FulfillmentType fulfillmentType;

    private Long warehouseId; // null for vendor self-storage

    private Long vendorLocationId; // for vendor self-storage

    @Size(max = 50, message = "Fulfillment zone must not exceed 50 characters")
    private String fulfillmentZone;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    private LocalDateTime dueDate;

    @DecimalMin(value = "0.0", message = "Total amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid total amount format")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.0", message = "Shipping cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid shipping cost format")
    private BigDecimal shippingCost;

    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid tax amount format")
    private BigDecimal taxAmount;

    // Shipping Address
    @NotBlank(message = "Shipping name is required")
    @Size(max = 100, message = "Shipping name must not exceed 100 characters")
    private String shippingName;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 200, message = "Shipping address must not exceed 200 characters")
    private String shippingAddressLine1;

    @Size(max = 200, message = "Shipping address line 2 must not exceed 200 characters")
    private String shippingAddressLine2;

    @NotBlank(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;

    @NotBlank(message = "Shipping state is required")
    @Size(max = 50, message = "Shipping state must not exceed 50 characters")
    private String shippingState;

    @NotBlank(message = "Shipping postal code is required")
    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    @Size(max = 50, message = "Shipping country must not exceed 50 characters")
    private String shippingCountry;

    @Size(max = 20, message = "Shipping phone must not exceed 20 characters")
    private String shippingPhone;

    // Special Instructions
    private String pickingInstructions;
    private String packingInstructions;
    private String shippingInstructions;
    private String customerNotes;

    // Configuration Flags
    private Boolean requiresQualityCheck = false;
    private Boolean isGift = false;
    private Boolean isExpedited = false;
    private Boolean isFragile = false;
    private Boolean requiresSignature = false;
    private Boolean requiresAgeVerification = false;

    // Order Items
    @NotEmpty(message = "Order must have at least one item")
    private List<CreateOrderItemRequest> orderItems;

    // Validation Methods
    public boolean isValidForWarehouseFulfillment() {
        return fulfillmentType == FulfillmentType.WAREHOUSE && warehouseId != null;
    }

    public boolean isValidForVendorSelfStorage() {
        return fulfillmentType == FulfillmentType.VENDOR_SELF_STORAGE && vendorLocationId != null;
    }
}


