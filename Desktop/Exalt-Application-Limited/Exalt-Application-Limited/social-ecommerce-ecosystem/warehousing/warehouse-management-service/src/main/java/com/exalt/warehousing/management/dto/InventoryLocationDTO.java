package com.exalt.warehousing.management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Inventory Location information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryLocationDTO {

    private UUID locationId;
    
    private UUID warehouseId;
    
    private UUID zoneId;
    
    private String locationCode;
    
    private String barcode;
    
    private String aisle;
    
    private String rack;
    
    private String level;
    
    private String position;
    
    private String status;
    
    private Integer availableQuantity;
    
    private Integer reservedQuantity;
    
    private Integer totalQuantity;
    
    private LocalDateTime lastCountDate;
    
    private Map<String, Object> locationDetails;
} 
