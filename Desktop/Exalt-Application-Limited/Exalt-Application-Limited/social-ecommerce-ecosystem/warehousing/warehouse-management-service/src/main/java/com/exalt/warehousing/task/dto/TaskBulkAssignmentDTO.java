package com.exalt.warehousing.task.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for bulk assignment of warehouse tasks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskBulkAssignmentDTO {
    
    @NotNull(message = "Staff ID is required")
    private UUID staffId;
    
    @NotEmpty(message = "At least one task ID is required")
    private List<UUID> taskIds;
    
    private String assignmentNote;
    
    private Boolean notifyStaff;
}