package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Light-weight summary DTO for warehouse zones
 * Used for dashboard displays and dropdown selections
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneSummaryDTO {
    
    private UUID id;
    private String zoneName;
    private String zoneCode;
    private String zoneType;
    private UUID warehouseId;
    private String warehouseName;
    private Integer totalLocations;
    private Double utilizationPercentage;
    private String status;
    private Boolean restricted;
}