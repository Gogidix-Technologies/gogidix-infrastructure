package com.exalt.warehousing.task.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a warehouse task to be performed by warehouse staff.
 */
@Entity
@Table(name = "warehouse_task")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseTask extends BaseEntity {

    @Column(name = "task_number", nullable = false, unique = true)
    private String taskNumber;
    
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;
    
    @Column(name = "reference_id")
    private UUID referenceId;
    
    @Column(name = "reference_type")
    private String referenceType;
    
    @Column(name = "source_location_id")
    private UUID sourceLocationId;
    
    @Column(name = "destination_location_id")
    private UUID destinationLocationId;
    
    @Column(name = "product_id")
    private UUID productId;
    
    @Column(name = "sku")
    private String sku;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;
    
    @Column(name = "assigned_to")
    private UUID assignedTo;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "instructions", length = 1000)
    private String instructions;
    
    @Column(name = "completion_notes", length = 1000)
    private String completionNotes;
    
    @Column(name = "due_by")
    private LocalDateTime dueBy;
    
    @Column(name = "estimated_completion_time")
    private Integer estimatedCompletionTimeMinutes;
    
    /**
     * Types of warehouse tasks
     */
    public enum TaskType {
        PICK,           // Pick item from location for order
        PACK,           // Pack items for shipment
        PUTAWAY,        // Put received items away to location
        REPLENISH,      // Replenish picking location from bulk storage
        CYCLE_COUNT,    // Inventory counting task
        RETURN_PROCESS, // Process returned items
        TRANSFER,       // Transfer inventory between locations
        QA_CHECK,       // Quality assurance check
        DISPOSAL,       // Dispose of damaged/expired items
        CONSOLIDATE,    // Consolidate partial quantities from multiple locations
        OTHER           // Other miscellaneous tasks
    }
    
    /**
     * Status of warehouse tasks
     */
    public enum TaskStatus {
        PENDING,        // Task created but not yet assigned
        ASSIGNED,       // Task assigned but not started
        IN_PROGRESS,    // Task in progress
        COMPLETED,      // Task completed
        CANCELLED,      // Task cancelled
        ON_HOLD,        // Task temporarily on hold
        FAILED          // Task failed to complete
    }
    
    /**
     * Assign task to staff member
     */
    public void assignTask(UUID staffId) {
        this.assignedTo = staffId;
        this.assignedAt = LocalDateTime.now();
        this.status = TaskStatus.ASSIGNED;
    }
    
    /**
     * Mark task as started
     */
    public void startTask() {
        this.startedAt = LocalDateTime.now();
        this.status = TaskStatus.IN_PROGRESS;
    }
    
    /**
     * Complete the task
     */
    public void completeTask(String notes) {
        this.completedAt = LocalDateTime.now();
        this.completionNotes = notes;
        this.status = TaskStatus.COMPLETED;
    }
    
    /**
     * Cancel the task
     */
    public void cancelTask(String reason) {
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.status = TaskStatus.CANCELLED;
    }
    
    /**
     * Put the task on hold
     */
    public void holdTask() {
        this.status = TaskStatus.ON_HOLD;
    }
}