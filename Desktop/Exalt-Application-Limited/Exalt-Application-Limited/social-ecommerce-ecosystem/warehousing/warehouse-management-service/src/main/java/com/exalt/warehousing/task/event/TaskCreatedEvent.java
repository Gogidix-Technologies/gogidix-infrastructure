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
 * Event triggered when a new warehouse task is created.
 * This can notify staff or systems about new work to be done.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TaskCreatedEvent extends BaseEvent {

    private UUID taskId;
    private String taskNumber;
    private WarehouseTask.TaskType taskType;
    private UUID warehouseId;
    private String warehouseName;
    private UUID referenceId;
    private String referenceType;
    private Integer priority;
    private UUID productId;
    private String sku;
    private Integer quantity;
    private UUID sourceLocationId;
    private UUID destinationLocationId;
    private LocalDateTime dueBy;
    private Integer estimatedCompletionTimeMinutes;
    
    /**
     * Create a new task created event with essential data
     * 
     * @param taskId the task ID
     * @param taskNumber the task number
     * @param taskType the task type
     * @param warehouseId the warehouse ID
     * @param priority the priority level
     * @return the event
     */
    public static TaskCreatedEvent create(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskType taskType,
            UUID warehouseId,
            Integer priority) {
        
        return TaskCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_CREATED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .taskType(taskType)
                .warehouseId(warehouseId)
                .priority(priority)
                .build();
    }
    
    /**
     * Create a new task created event with detailed data
     */
    public static TaskCreatedEvent createDetailed(
            UUID taskId,
            String taskNumber,
            WarehouseTask.TaskType taskType,
            UUID warehouseId,
            String warehouseName,
            UUID referenceId,
            String referenceType,
            Integer priority,
            UUID productId,
            String sku,
            Integer quantity,
            UUID sourceLocationId,
            UUID destinationLocationId,
            LocalDateTime dueBy,
            Integer estimatedCompletionTimeMinutes) {
        
        return TaskCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .aggregateId(taskId)
                .eventType("TASK_CREATED")
                .taskId(taskId)
                .taskNumber(taskNumber)
                .taskType(taskType)
                .warehouseId(warehouseId)
                .warehouseName(warehouseName)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .priority(priority)
                .productId(productId)
                .sku(sku)
                .quantity(quantity)
                .sourceLocationId(sourceLocationId)
                .destinationLocationId(destinationLocationId)
                .dueBy(dueBy)
                .estimatedCompletionTimeMinutes(estimatedCompletionTimeMinutes)
                .build();
    }
}