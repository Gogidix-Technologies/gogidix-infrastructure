package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.model.Priority;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event payload for warehouse task events
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    /**
     * Unique event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private EventType eventType;
    
    /**
     * Task ID
     */
    private UUID taskId;
    
    /**
     * Task type
     */
    private TaskType taskType;
    
    /**
     * Task status
     */
    private TaskStatus taskStatus;
    
    /**
     * Assigned staff ID
     */
    private UUID assignedStaffId;
    
    /**
     * Task priority
     */
    private Priority priority;
    
    /**
     * Event timestamp
     */
    private LocalDateTime timestamp;
} 
