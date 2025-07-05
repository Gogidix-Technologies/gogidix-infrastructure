package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.ShippingCarrier;
import com.exalt.warehousing.management.model.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for shipping rates
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRateDTO {
    
    /**
     * The shipping carrier
     */
    private ShippingCarrier carrier;
    
    /**
     * The shipping method
     */
    private ShippingMethod method;
    
    /**
     * The shipping rate
     */
    private double rate;
    
    /**
     * The currency of the rate
     */
    private String currency;
    
    /**
     * The estimated transit time in days
     */
    private int transitDays;
    
    /**
     * Whether the delivery date is guaranteed
     */
    private boolean guaranteedDelivery;
} 
