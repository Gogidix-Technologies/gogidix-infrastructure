package com.exalt.warehousing.inventory.dto;

import com.exalt.warehousing.inventory.model.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for warehouse location data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocationDTO {
    
    private UUID id;
    
    @NotBlank(message = "Warehouse name is required")
    private String name;
    
    @NotBlank(message = "Warehouse code is required")
    private String code;
    
    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;
    
    private String addressLine2;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State/Province is required")
    private String stateProvince;
    
    @NotBlank(message = "Postal code is required")
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    private Double latitude;
    
    private Double longitude;
    
    private String contactPhone;
    
    private String contactEmail;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    @NotNull(message = "Warehouse type is required")
    private WarehouseType type;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Calculated fields
    private Integer totalInventoryItems;
    private Integer totalQuantity;
}

