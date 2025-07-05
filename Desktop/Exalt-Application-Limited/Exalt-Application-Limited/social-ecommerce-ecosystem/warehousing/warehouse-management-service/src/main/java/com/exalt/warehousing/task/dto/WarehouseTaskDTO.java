package com.exalt.warehousing.task.dto;

import com.exalt.warehousing.task.model.WarehouseTask;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for warehouse tasks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseTaskDTO {
    
    private UUID id;
    
    private String taskNumber;
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    @NotNull(message = "Task type is required")
    private String taskType;
    
    private UUID referenceId;
    
    private String referenceType;
    
    private UUID sourceLocationId;
    
    private UUID destinationLocationId;
    
    private UUID productId;
    
    private String sku;
    
    private Integer quantity;
    
    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    private Integer priority;
    
    @NotNull(message = "Status is required")
    private String status;
    
    private UUID assignedTo;
    
    private String assignedToName;
    
    private LocalDateTime assignedAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime cancelledAt;
    
    private String cancellationReason;
    
    @Size(max = 1000, message = "Instructions must not exceed 1000 characters")
    private String instructions;
    
    @Size(max = 1000, message = "Completion notes must not exceed 1000 characters")
    private String completionNotes;
    
    private LocalDateTime dueBy;
    
    private Integer estimatedCompletionTimeMinutes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Create DTO from entity
     *
     * @param task the warehouse task entity
     * @return the DTO
     */
    public static WarehouseTaskDTO fromEntity(WarehouseTask task) {
        return WarehouseTaskDTO.builder()
                .id((UUID.fromString(task.getId())))
                .taskNumber(task.getTaskNumber())
                .warehouseId(task.getWarehouseId())
                .taskType(task.getTaskType().name())
                .referenceId(task.getReferenceId())
                .referenceType(task.getReferenceType())
                .sourceLocationId(task.getSourceLocationId())
                .destinationLocationId(task.getDestinationLocationId())
                .productId(task.getProductId())
                .sku(task.getSku())
                .quantity(task.getQuantity())
                .priority(task.getPriority())
                .status(task.getStatus().name())
                .assignedTo(task.getAssignedTo())
                .assignedAt(task.getAssignedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .cancelledAt(task.getCancelledAt())
                .cancellationReason(task.getCancellationReason())
                .instructions(task.getInstructions())
                .completionNotes(task.getCompletionNotes())
                .dueBy(task.getDueBy())
                .estimatedCompletionTimeMinutes(task.getEstimatedCompletionTimeMinutes())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert this DTO to entity
     *
     * @return the entity
     */
    public WarehouseTask toEntity() {
        return WarehouseTask.builder()
                .id(this.id != null ? this.id.toString() : null)
                .taskNumber(this.taskNumber != null ? this.taskNumber : generateTaskNumber())
                .warehouseId(this.warehouseId)
                .taskType(this.taskType != null ? WarehouseTask.TaskType.valueOf(this.taskType) : null)
                .referenceId(this.referenceId)
                .referenceType(this.referenceType)
                .sourceLocationId(this.sourceLocationId)
                .destinationLocationId(this.destinationLocationId)
                .productId(this.productId)
                .sku(this.sku)
                .quantity(this.quantity)
                .priority(this.priority)
                .status(this.status != null ? WarehouseTask.TaskStatus.valueOf(this.status) : WarehouseTask.TaskStatus.PENDING)
                .assignedTo(this.assignedTo)
                .assignedAt(this.assignedAt)
                .startedAt(this.startedAt)
                .completedAt(this.completedAt)
                .cancelledAt(this.cancelledAt)
                .cancellationReason(this.cancellationReason)
                .instructions(this.instructions)
                .completionNotes(this.completionNotes)
                .dueBy(this.dueBy)
                .estimatedCompletionTimeMinutes(this.estimatedCompletionTimeMinutes)
                .build();
    }
    
    /**
     * Generate a unique task number
     */
    private String generateTaskNumber() {
        return "TASK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}