package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for tracking information from shipping carriers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfoDTO {
    
    private String trackingNumber;
    
    private String status;
    
    private String statusDescription;
    
    private String carrierName;
    
    private String estimatedDeliveryDate;
    
    private String lastUpdateLocation;
    
    private LocalDateTime lastUpdateTime;
    
    private String deliveryAttempts;
    
    private String signedBy;
    
    private LocalDateTime deliveryTime;
    
    private String additionalNotes;
}
