package com.exalt.warehousing.fulfillment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for delivery address information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDTO {
    
    /**
     * Address line 1
     */
    private String addressLine1;
    
    /**
     * Address line 2
     */
    private String addressLine2;
    
    /**
     * City
     */
    private String city;
    
    /**
     * State or province
     */
    private String stateProvince;
    
    /**
     * Postal code
     */
    private String postalCode;
    
    /**
     * Country
     */
    private String country;
    
    /**
     * Latitude (for distance calculations)
     */
    private Double latitude;
    
    /**
     * Longitude (for distance calculations)
     */
    private Double longitude;
} 
