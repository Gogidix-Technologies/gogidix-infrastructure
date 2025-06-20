package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.TaskCompletionStatDTO;
import com.exalt.warehousing.management.dto.TaskPerformanceDTO;
import com.exalt.warehousing.management.model.TaskType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for warehouse task analytics operations
 */
public interface WarehouseTaskAnalyticsService {
    
    /**
     * Calculate task completion statistics for a specific warehouse
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return task completion statistics
     */
    TaskCompletionStatDTO calculateTaskCompletionStats(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate staff performance metrics for task completion
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return list of staff performance metrics
     */
    List<TaskPerformanceDTO> calculateStaffPerformance(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate average task completion time grouped by task type
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return map of task type to average completion time in minutes
     */
    Map<TaskType, Double> calculateAverageCompletionTimeByTaskType(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate task distribution by zone
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return map of zone ID to task count
     */
    Map<UUID, Integer> calculateTaskDistributionByZone(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate daily task completion counts for a period
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return map of date to task completion count
     */
    Map<LocalDate, Integer> calculateDailyTaskCompletionCounts(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate task delay statistics - tasks that were completed after their due date
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return percentage of delayed tasks and average delay in minutes
     */
    Map<String, Double> calculateTaskDelayStats(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate efficiency report for staff members
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return map of staff ID to efficiency score (percentage)
     */
    Map<UUID, Double> generateStaffEfficiencyReport(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Identify bottlenecks in task workflow
     * 
     * @param warehouseId the warehouse ID
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return map of task type to average time spent in each status
     */
    Map<TaskType, Map<String, Double>> identifyWorkflowBottlenecks(UUID warehouseId, LocalDate startDate, LocalDate endDate);
} 
