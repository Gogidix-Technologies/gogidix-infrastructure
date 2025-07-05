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
 * Data Transfer Object for PickingTask
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingTaskDTO {
    
    private UUID id;
    
    @NotNull(message = "Fulfillment order ID is required")
    private UUID fulfillmentOrderId;
    
    private UUID assignedStaffId;
    
    @NotNull(message = "Status is required")
    private TaskStatus status;
    
    private Integer priority;
    
    private String batchId;
    
    private String zone;
    
    private String aisle;
    
    private String rack;
    
    private String bin;
    
    private String instruction;
    
    private LocalDateTime dueBy;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private String completionNotes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 
