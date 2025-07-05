package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.StaffRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for staff task performance metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPerformanceDTO {
    
    /**
     * Staff member ID
     */
    private UUID staffId;
    
    /**
     * Staff member employee ID
     */
    private String employeeId;
    
    /**
     * Staff full name (first name + last name)
     */
    private String staffName;
    
    /**
     * Staff role
     */
    private StaffRole role;
    
    /**
     * Zone ID the staff is assigned to
     */
    private UUID zoneId;
    
    /**
     * Total tasks assigned to the staff member
     */
    private int totalTasksAssigned;
    
    /**
     * Total tasks completed by the staff member
     */
    private int totalTasksCompleted;
    
    /**
     * Tasks completed on time (before due date)
     */
    private int tasksCompletedOnTime;
    
    /**
     * Average task completion time in minutes
     */
    private double avgCompletionTimeMinutes;
    
    /**
     * Task completion rate (percentage)
     */
    private double completionRate;
    
    /**
     * On-time completion rate (percentage of tasks completed before due date)
     */
    private double onTimeCompletionRate;
    
    /**
     * Performance score (calculated metric based on completion time and rate)
     */
    private double performanceScore;
    
    /**
     * Distribution of tasks by type
     */
    private Map<String, Integer> tasksByType;
    
    /**
     * Average completion time by task type in minutes
     */
    private Map<String, Double> avgCompletionTimeByType;
} 
