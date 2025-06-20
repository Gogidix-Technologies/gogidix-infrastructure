package com.exalt.warehousing.fulfillment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Address Embeddable Component
 * 
 * Represents a physical address used for shipping and billing purposes.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(name = "name", length = 255)
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Column(name = "address_line1", length = 255)
    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @Column(name = "city", length = 100)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Column(name = "state", length = 100)
    @NotBlank(message = "State/Province is required")
    @Size(max = 100, message = "State/Province must not exceed 100 characters")
    private String state;

    @Column(name = "postal_code", length = 50)
    @NotBlank(message = "Postal code is required")
    @Size(max = 50, message = "Postal code must not exceed 50 characters")
    private String postalCode;

    @Column(name = "country", length = 100)
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Column(name = "phone_number", length = 50)
    @Size(max = 50, message = "Phone number must not exceed 50 characters")
    private String phoneNumber;

    @Column(name = "email", length = 255)
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    // Helper methods
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        
        if (addressLine1 != null) {
            address.append(addressLine1);
        }
        
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address.append(", ").append(addressLine2);
        }
        
        if (city != null) {
            address.append(", ").append(city);
        }
        
        if (state != null) {
            address.append(", ").append(state);
        }
        
        if (postalCode != null) {
            address.append(" ").append(postalCode);
        }
        
        if (country != null) {
            address.append(", ").append(country);
        }
        
        return address.toString();
    }

    public boolean isComplete() {
        return addressLine1 != null && !addressLine1.isEmpty() &&
               city != null && !city.isEmpty() &&
               state != null && !state.isEmpty() &&
               postalCode != null && !postalCode.isEmpty() &&
               country != null && !country.isEmpty();
    }
}
