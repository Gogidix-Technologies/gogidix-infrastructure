package com.exalt.warehousing.fulfillment.dto;

import com.exalt.warehousing.fulfillment.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for PackingTask
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackingTaskDTO {
    
    private UUID id;
    
    @NotNull(message = "Fulfillment order ID is required")
    private UUID fulfillmentOrderId;
    
    private UUID assignedStaffId;
    
    @NotNull(message = "Status is required")
    private TaskStatus status;
    
    private Integer priority;
    
    private String packingStation;
    
    private String instruction;
    
    private String packagingType;
    
    private Double weightKg;
    
    private Double lengthCm;
    
    private Double widthCm;
    
    private Double heightCm;
    
    private Boolean specialHandlingRequired;
    
    private String specialHandlingInstructions;
    
    private LocalDateTime dueBy;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private String completionNotes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 
