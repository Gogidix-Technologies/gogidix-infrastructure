package com.exalt.warehousing.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for filtering warehouse tasks with various criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDTO {
    
    private UUID warehouseId;
    
    private List<String> taskTypes;
    
    private List<String> statuses;
    
    private UUID assignedTo;
    
    private Boolean unassignedOnly;
    
    private UUID referenceId;
    
    private String referenceType;
    
    private UUID productId;
    
    private String sku;
    
    private UUID sourceLocationId;
    
    private UUID destinationLocationId;
    
    private Integer minPriority;
    
    private Integer maxPriority;
    
    private Boolean overdueOnly;
    
    private LocalDateTime dueBefore;
    
    private LocalDateTime dueAfter;
    
    private LocalDateTime createdBefore;
    
    private LocalDateTime createdAfter;
    
    private LocalDateTime completedBefore;
    
    private LocalDateTime completedAfter;
    
    /**
     * Default builder for recently created tasks
     * 
     * @param warehouseId the warehouse ID
     * @return filter DTO
     */
    public static TaskFilterDTO recentlyCreated(UUID warehouseId) {
        return TaskFilterDTO.builder()
                .warehouseId(warehouseId)
                .createdAfter(LocalDateTime.now().minusHours(24))
                .build();
    }
    
    /**
     * Default builder for high priority tasks
     * 
     * @param warehouseId the warehouse ID
     * @return filter DTO
     */
    public static TaskFilterDTO highPriority(UUID warehouseId) {
        return TaskFilterDTO.builder()
                .warehouseId(warehouseId)
                .minPriority(7)
                .build();
    }
    
    /**
     * Default builder for overdue tasks
     * 
     * @param warehouseId the warehouse ID
     * @return filter DTO
     */
    public static TaskFilterDTO overdue(UUID warehouseId) {
        return TaskFilterDTO.builder()
                .warehouseId(warehouseId)
                .dueBefore(LocalDateTime.now())
                .overdueOnly(true)
                .build();
    }
}