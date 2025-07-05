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
 * Event triggered when a warehouse task is assigned to a staff member.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TaskAssignedEvent extends BaseEvent {

    private UUID taskId;
    private String taskNumber;
    private WarehouseTask.TaskType taskType;
    private UUID warehouseId;
    private UUID assignedTo;
    private String assignedToName;
    private Integer priority;
    private UUID referenceId;
    private String referenceType;
    private LocalDateTime assignedAt;
    private LocalDateTime dueBy;
    private Integer estimatedCompletionTimeMinutes;
    private String instructions;

    /**
     * Create a new task assigned event with essential data
     * 
     * @param taskId the task ID
     * @param taskNumber the task number
     * @param taskType the task type
     * @param warehouseId the warehouse ID
     * @param assignedTo the staff member ID
     * @param priority the priority level
     * @return the event
     */
    public static TaskAssignedEvent create(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskType taskType,
            UUID warehouseId,
            UUID assignedTo,
            Integer priority) {
        
        return TaskAssignedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_ASSIGNED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .taskType(taskType)
                .warehouseId(warehouseId)
                .assignedTo(assignedTo)
                .priority(priority)
                .assignedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a new task assigned event with detailed data
     */
    public static TaskAssignedEvent createDetailed(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskType taskType,
            UUID warehouseId,
            UUID assignedTo,
            String assignedToName,
            Integer priority,
            UUID referenceId,
            String referenceType,
            LocalDateTime dueBy,
            Integer estimatedCompletionTimeMinutes,
            String instructions) {
        
        return TaskAssignedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_ASSIGNED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .taskType(taskType)
                .warehouseId(warehouseId)
                .assignedTo(assignedTo)
                .assignedToName(assignedToName)
                .priority(priority)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .assignedAt(LocalDateTime.now())
                .dueBy(dueBy)
                .estimatedCompletionTimeMinutes(estimatedCompletionTimeMinutes)
                .instructions(instructions)
                .build();
    }
}