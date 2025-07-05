package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for task completion statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionStatDTO {
    
    /**
     * Total number of tasks created in the period
     */
    private int totalTasksCreated;
    
    /**
     * Total number of tasks completed in the period
     */
    private int totalTasksCompleted;
    
    /**
     * Total number of tasks cancelled in the period
     */
    private int totalTasksCancelled;
    
    /**
     * Task completion rate (percentage)
     */
    private double completionRate;
    
    /**
     * Average task completion time in minutes
     */
    private double avgCompletionTimeMinutes;
    
    /**
     * Counts of tasks by type
     */
    private Map<String, Integer> taskCountsByType;
    
    /**
     * Counts of tasks by status
     */
    private Map<String, Integer> taskCountsByStatus;
    
    /**
     * Number of tasks completed on time (before due date)
     */
    private int tasksCompletedOnTime;
    
    /**
     * Number of tasks completed late (after due date)
     */
    private int tasksCompletedLate;
    
    /**
     * Most efficient staff member ID and their completion count
     */
    private UUID mostEfficientStaffId;
    
    /**
     * Number of tasks completed by the most efficient staff member
     */
    private int mostEfficientStaffTaskCount;
    
    /**
     * Busiest zone ID and its task count
     */
    private UUID busiestZoneId;
    
    /**
     * Number of tasks in the busiest zone
     */
    private int busiestZoneTaskCount;
} 
