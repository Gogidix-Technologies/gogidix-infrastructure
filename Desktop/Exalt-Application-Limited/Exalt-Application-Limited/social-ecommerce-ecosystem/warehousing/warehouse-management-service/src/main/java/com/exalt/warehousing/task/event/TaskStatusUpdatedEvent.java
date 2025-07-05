package com.exalt.warehousing.task.event;

import com.exalt.warehousing.shared.events.BaseEvent;
import com.exalt.warehousing.task.model.WarehouseTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event triggered when a warehouse task status is updated.
 * Used for tracking task status changes, completions, and cancellations.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TaskStatusUpdatedEvent extends BaseEvent {

    private UUID taskId;
    private String taskNumber;
    private WarehouseTask.TaskType taskType;
    private UUID warehouseId;
    private UUID assignedTo;
    private WarehouseTask.TaskStatus previousStatus;
    private WarehouseTask.TaskStatus newStatus;
    private String completionNotes;
    private String cancellationReason;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime statusChangeTime;
    private Long taskDurationMinutes; // Duration from assignment to completion
    private UUID referenceId;
    private String referenceType;
    
    /**
     * Create a new task status updated event with essential data
     * 
     * @param taskId the task ID
     * @param taskNumber the task number
     * @param previousStatus the previous status
     * @param newStatus the new status
     * @return the event
     */
    public static TaskStatusUpdatedEvent create(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskStatus previousStatus,
            WarehouseTask.TaskStatus newStatus) {
        
        return TaskStatusUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_STATUS_UPDATED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .statusChangeTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a task completed event
     * 
     * @param taskId the task ID
     * @param taskNumber the task number
     * @param taskType the task type
     * @param warehouseId the warehouse ID
     * @param assignedTo the staff member ID
     * @param completionNotes the completion notes
     * @param completedAt the completion time
     * @param taskDurationMinutes the duration in minutes
     * @return the event
     */
    public static TaskStatusUpdatedEvent createTaskCompletedEvent(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskType taskType,
            UUID warehouseId,
            UUID assignedTo,
            String completionNotes,
            LocalDateTime completedAt,
            Long taskDurationMinutes) {
        
        return TaskStatusUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_COMPLETED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .taskType(taskType)
                .warehouseId(warehouseId)
                .assignedTo(assignedTo)
                .previousStatus(WarehouseTask.TaskStatus.IN_PROGRESS)
                .newStatus(WarehouseTask.TaskStatus.COMPLETED)
                .completionNotes(completionNotes)
                .completedAt(completedAt)
                .statusChangeTime(LocalDateTime.now())
                .taskDurationMinutes(taskDurationMinutes)
                .build();
    }
    
    /**
     * Create a task cancelled event
     * 
     * @param taskId the task ID
     * @param taskNumber the task number
     * @param taskType the task type
     * @param warehouseId the warehouse ID
     * @param previousStatus the previous status
     * @param cancellationReason the cancellation reason
     * @return the event
     */
    public static TaskStatusUpdatedEvent createTaskCancelledEvent(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskType taskType,
            UUID warehouseId,
            WarehouseTask.TaskStatus previousStatus,
            String cancellationReason) {
        
        return TaskStatusUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_CANCELLED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .taskType(taskType)
                .warehouseId(warehouseId)
                .previousStatus(previousStatus)
                .newStatus(WarehouseTask.TaskStatus.CANCELLED)
                .cancellationReason(cancellationReason)
                .cancelledAt(LocalDateTime.now())
                .statusChangeTime(LocalDateTime.now())
                .build();
    }
}