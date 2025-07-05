package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a shipping or billing address
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String addressType; // SHIPPING, BILLING, etc.
    private boolean isDefault;
    private boolean isValidated;
}

