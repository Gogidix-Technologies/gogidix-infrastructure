package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.ZoneType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Zone entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {
    
    private UUID id;
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    @NotBlank(message = "Zone name is required")
    @Size(min = 2, max = 50, message = "Zone name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Zone code is required")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Zone code must be 2-10 uppercase alphanumeric characters")
    private String code;
    
    @NotNull(message = "Zone type is required")
    private ZoneType type;
    
    private String description;
    
    private Double squareFootage;
    
    private Integer maxCapacity;
    
    private Double temperatureRangeMin;
    
    private Double temperatureRangeMax;
    
    private Double humidityRangeMin;
    
    private Double humidityRangeMax;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 
