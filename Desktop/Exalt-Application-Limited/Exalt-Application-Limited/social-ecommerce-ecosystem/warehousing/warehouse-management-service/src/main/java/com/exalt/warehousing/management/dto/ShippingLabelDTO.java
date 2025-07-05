package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.ShippingCarrier;
import com.exalt.warehousing.management.model.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for shipping labels
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingLabelDTO {
    
    /**
     * Unique identifier for the shipping label
     */
    private UUID id;
    
    /**
     * The shipping carrier
     */
    private ShippingCarrier carrier;
    
    /**
     * The shipping method
     */
    private ShippingMethod method;
    
    /**
     * The tracking number
     */
    private String trackingNumber;
    
    /**
     * The origin address
     */
    private Map<String, String> originAddress;
    
    /**
     * The destination address
     */
    private Map<String, String> destinationAddress;
    
    /**
     * The package weight in pounds
     */
    private double weightLbs;
    
    /**
     * The package dimensions in inches (length, width, height)
     */
    private double[] dimensionsInches;
    
    /**
     * Description of package contents
     */
    private String packageContents;
    
    /**
     * URL to download the label
     */
    private String labelUrl;
    
    /**
     * The label data as a byte array
     */
    private byte[] labelData;
    
    /**
     * When the label was created
     */
    private LocalDateTime createdAt;
    
    /**
     * The estimated delivery date
     */
    private LocalDateTime estimatedDelivery;
} 
