package com.exalt.warehousing.fulfillment.dto;

import com.exalt.warehousing.fulfillment.enums.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for shipment creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateRequestDTO {

    /**
     * ID of the fulfillment order to ship
     */
    @NotNull(message = "Fulfillment order ID is required")
    private UUID fulfillmentOrderId;
    
    /**
     * Shipping method to use
     */
    @NotNull(message = "Shipping method is required")
    private ShippingMethod shippingMethod;
    
    /**
     * Optional carrier preference (if null, the system will determine the carrier)
     */
    private String carrierPreference;
} 
