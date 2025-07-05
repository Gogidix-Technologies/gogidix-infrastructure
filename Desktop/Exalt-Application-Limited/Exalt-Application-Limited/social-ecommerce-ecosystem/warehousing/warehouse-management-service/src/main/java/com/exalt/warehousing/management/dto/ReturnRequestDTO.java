package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.ReturnStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Return Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnRequestDTO {
    
    private UUID id;
    private UUID orderId;
    private UUID customerId;
    private UUID warehouseId;
    private String returnCode;
    private String returnReason;
    private ReturnStatus status;
    private List<ReturnItemDTO> returnItems;
    private String qualityCheckNotes;
    private boolean refundProcessed;
    private boolean inventoryReintegrated;
    private UUID processedByStaffId;
    private LocalDateTime processedAt;
    private LocalDateTime receivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String returnTrackingNumber;
    private String notes;
    
    // Calculated fields for display purposes
    private int totalItemCount;
    private int acceptedItemCount;
    private int rejectedItemCount;
    private Double totalRefundAmount;
} 
