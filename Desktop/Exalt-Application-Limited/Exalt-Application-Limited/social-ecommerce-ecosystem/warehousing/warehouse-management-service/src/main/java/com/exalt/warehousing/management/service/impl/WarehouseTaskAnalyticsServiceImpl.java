package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.management.dto.StaffDTO;
import com.exalt.warehousing.management.dto.TaskCompletionStatDTO;
import com.exalt.warehousing.management.dto.TaskPerformanceDTO;
import com.exalt.warehousing.management.dto.WarehouseTaskDTO;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.repository.StaffRepository;
import com.exalt.warehousing.management.repository.WarehouseTaskRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.service.StaffService;
import com.exalt.warehousing.management.service.WarehouseTaskAnalyticsService;
import com.exalt.warehousing.management.service.WarehouseTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the WarehouseTaskAnalyticsService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WarehouseTaskAnalyticsServiceImpl implements WarehouseTaskAnalyticsService {

    private final WarehouseTaskRepository taskRepository;
    private final StaffRepository staffRepository;
    private final ZoneRepository zoneRepository;
    private final WarehouseTaskService taskService;
    private final StaffService staffService;

    @Override
    public TaskCompletionStatDTO calculateTaskCompletionStats(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating task completion stats for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all tasks for the warehouse in the given period
        List<WarehouseTaskDTO> tasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> task.getCreatedAt() != null && 
                        !task.getCreatedAt().isBefore(startDateTime) && 
                        !task.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());
        
        if (tasks.isEmpty()) {
            return TaskCompletionStatDTO.builder()
                    .totalTasksCreated(0)
                    .totalTasksCompleted(0)
                    .totalTasksCancelled(0)
                    .completionRate(0.0)
                    .avgCompletionTimeMinutes(0.0)
                    .taskCountsByType(new HashMap<>())
                    .taskCountsByStatus(new HashMap<>())
                    .tasksCompletedOnTime(0)
                    .tasksCompletedLate(0)
                    .build();
        }
        
        // Count tasks by type
        Map<String, Integer> taskCountsByType = new HashMap<>();
        for (WarehouseTaskDTO task : tasks) {
            if (task.getType() != null) {
                String type = task.getType().name();
                taskCountsByType.put(type, taskCountsByType.getOrDefault(type, 0) + 1);
            }
        }
        
        // Count tasks by status
        Map<String, Integer> taskCountsByStatus = new HashMap<>();
        for (WarehouseTaskDTO task : tasks) {
            if (task.getStatus() != null) {
                String status = task.getStatus().name();
                taskCountsByStatus.put(status, taskCountsByStatus.getOrDefault(status, 0) + 1);
            }
        }
        
        // Calculate completion metrics
        List<WarehouseTaskDTO> completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());
        
        int totalCompleted = completedTasks.size();
        int totalCancelled = (int) tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.CANCELLED)
                .count();
        
        double completionRate = tasks.isEmpty() ? 0.0 : (double) totalCompleted / tasks.size() * 100.0;
        
        // Calculate average completion time
        double avgCompletionTime = completedTasks.stream()
                .filter(task -> task.getStartedAt() != null && task.getCompletedAt() != null)
                .mapToLong(task -> Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes())
                .average()
                .orElse(0.0);
        
        // Calculate on-time vs late completion
        int tasksCompletedOnTime = 0;
        int tasksCompletedLate = 0;
        
        for (WarehouseTaskDTO task : completedTasks) {
            if (task.getDueAt() != null && task.getCompletedAt() != null) {
                if (task.getCompletedAt().isBefore(task.getDueAt()) || task.getCompletedAt().isEqual(task.getDueAt())) {
                    tasksCompletedOnTime++;
                } else {
                    tasksCompletedLate++;
                }
            }
        }
        
        // Find busiest zone
        Map<UUID, Integer> tasksByZone = new HashMap<>();
        for (WarehouseTaskDTO task : tasks) {
            if (task.getZoneId() != null) {
                tasksByZone.put(task.getZoneId(), tasksByZone.getOrDefault(task.getZoneId(), 0) + 1);
            }
        }
        
        UUID busiestZoneId = null;
        int busiestZoneTaskCount = 0;
        
        if (!tasksByZone.isEmpty()) {
            Map.Entry<UUID, Integer> busiestZone = tasksByZone.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            
            if (busiestZone != null) {
                busiestZoneId = busiestZone.getKey();
                busiestZoneTaskCount = busiestZone.getValue();
            }
        }
        
        // Find most efficient staff
        Map<UUID, Integer> tasksByStaff = new HashMap<>();
        for (WarehouseTaskDTO task : completedTasks) {
            if (task.getAssignedTo() != null) {
                tasksByStaff.put(task.getAssignedTo(), tasksByStaff.getOrDefault(task.getAssignedTo(), 0) + 1);
            }
        }
        
        UUID mostEfficientStaffId = null;
        int mostEfficientStaffTaskCount = 0;
        
        if (!tasksByStaff.isEmpty()) {
            Map.Entry<UUID, Integer> mostEfficientStaff = tasksByStaff.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            
            if (mostEfficientStaff != null) {
                mostEfficientStaffId = mostEfficientStaff.getKey();
                mostEfficientStaffTaskCount = mostEfficientStaff.getValue();
            }
        }
        
        return TaskCompletionStatDTO.builder()
                .totalTasksCreated(tasks.size())
                .totalTasksCompleted(totalCompleted)
                .totalTasksCancelled(totalCancelled)
                .completionRate(completionRate)
                .avgCompletionTimeMinutes(avgCompletionTime)
                .taskCountsByType(taskCountsByType)
                .taskCountsByStatus(taskCountsByStatus)
                .tasksCompletedOnTime(tasksCompletedOnTime)
                .tasksCompletedLate(tasksCompletedLate)
                .mostEfficientStaffId(mostEfficientStaffId)
                .mostEfficientStaffTaskCount(mostEfficientStaffTaskCount)
                .busiestZoneId(busiestZoneId)
                .busiestZoneTaskCount(busiestZoneTaskCount)
                .build();
    }

    @Override
    public List<TaskPerformanceDTO> calculateStaffPerformance(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating staff performance for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all tasks for the warehouse in the given period
        List<WarehouseTaskDTO> allTasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> task.getCreatedAt() != null && 
                        !task.getCreatedAt().isBefore(startDateTime) && 
                        !task.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());
        
        // Get all staff in the warehouse
        List<StaffDTO> staffList = staffService.findByWarehouseId(warehouseId);
        
        List<TaskPerformanceDTO> performanceList = new ArrayList<>();
        
        for (StaffDTO staff : staffList) {
            // Tasks assigned to this staff member
            List<WarehouseTaskDTO> staffTasks = allTasks.stream()
                    .filter(task -> staff.getId().equals(task.getAssignedTo()))
                    .collect(Collectors.toList());
            
            if (staffTasks.isEmpty()) {
                continue; // Skip staff with no assigned tasks
            }
            
            // Completed tasks
            List<WarehouseTaskDTO> completedTasks = staffTasks.stream()
                    .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                    .collect(Collectors.toList());
            
            // Calculate metrics
            int totalAssigned = staffTasks.size();
            int totalCompleted = completedTasks.size();
            double completionRate = (double) totalCompleted / totalAssigned * 100.0;
            
            // Calculate average completion time
            double avgCompletionTime = completedTasks.stream()
                    .filter(task -> task.getStartedAt() != null && task.getCompletedAt() != null)
                    .mapToLong(task -> Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes())
                    .average()
                    .orElse(0.0);
            
            // Calculate on-time completion
            int onTimeCompleted = 0;
            for (WarehouseTaskDTO task : completedTasks) {
                if (task.getDueAt() != null && task.getCompletedAt() != null && 
                        (task.getCompletedAt().isBefore(task.getDueAt()) || task.getCompletedAt().isEqual(task.getDueAt()))) {
                    onTimeCompleted++;
                }
            }
            
            double onTimeRate = totalCompleted == 0 ? 0.0 : (double) onTimeCompleted / totalCompleted * 100.0;
            
            // Calculate performance score (weighted average of completion rate, on-time rate, and inverse of completion time)
            double timeScore = avgCompletionTime == 0 ? 100.0 : Math.min(100.0, 100.0 * (1.0 / (avgCompletionTime / 60.0)));
            double performanceScore = (completionRate * 0.4) + (onTimeRate * 0.4) + (timeScore * 0.2);
            
            // Count tasks by type
            Map<String, Integer> tasksByType = new HashMap<>();
            for (WarehouseTaskDTO task : staffTasks) {
                if (task.getType() != null) {
                    String type = task.getType().name();
                    tasksByType.put(type, tasksByType.getOrDefault(type, 0) + 1);
                }
            }
            
            // Calculate average completion time by task type
            Map<String, Double> avgTimeByType = new HashMap<>();
            
            // Group completed tasks by type
            Map<TaskType, List<WarehouseTaskDTO>> tasksByTypeMap = completedTasks.stream()
                    .filter(task -> task.getType() != null && task.getStartedAt() != null && task.getCompletedAt() != null)
                    .collect(Collectors.groupingBy(WarehouseTaskDTO::getType));
            
            // Calculate average completion time for each type
            for (Map.Entry<TaskType, List<WarehouseTaskDTO>> entry : tasksByTypeMap.entrySet()) {
                double avgTime = entry.getValue().stream()
                        .mapToLong(task -> Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes())
                        .average()
                        .orElse(0.0);
                
                avgTimeByType.put(entry.getKey().name(), avgTime);
            }
            
            // Create performance DTO
            TaskPerformanceDTO performance = TaskPerformanceDTO.builder()
                    .staffId(staff.getId())
                    .employeeId(staff.getEmployeeId())
                    .staffName(staff.getFirstName() + " " + staff.getLastName())
                    .role(staff.getRole())
                    .zoneId(staff.getZoneId())
                    .totalTasksAssigned(totalAssigned)
                    .totalTasksCompleted(totalCompleted)
                    .tasksCompletedOnTime(onTimeCompleted)
                    .avgCompletionTimeMinutes(avgCompletionTime)
                    .completionRate(completionRate)
                    .onTimeCompletionRate(onTimeRate)
                    .performanceScore(performanceScore)
                    .tasksByType(tasksByType)
                    .avgCompletionTimeByType(avgTimeByType)
                    .build();
            
            performanceList.add(performance);
        }
        
        // Sort by performance score (descending)
        performanceList.sort(Comparator.comparing(TaskPerformanceDTO::getPerformanceScore).reversed());
        
        return performanceList;
    }

    @Override
    public Map<TaskType, Double> calculateAverageCompletionTimeByTaskType(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating average completion time by task type for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all completed tasks for the warehouse in the given period
        List<WarehouseTaskDTO> completedTasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED &&
                        task.getCompletedAt() != null && 
                        !task.getCompletedAt().isBefore(startDateTime) && 
                        !task.getCompletedAt().isAfter(endDateTime))
                .collect(Collectors.toList());
        
        Map<TaskType, Double> result = new HashMap<>();
        
        if (completedTasks.isEmpty()) {
            return result;
        }
        
        // Group tasks by type
        Map<TaskType, List<WarehouseTaskDTO>> tasksByType = completedTasks.stream()
                .filter(task -> task.getType() != null && task.getStartedAt() != null && task.getCompletedAt() != null)
                .collect(Collectors.groupingBy(WarehouseTaskDTO::getType));
        
        // Calculate average completion time for each type
        for (Map.Entry<TaskType, List<WarehouseTaskDTO>> entry : tasksByType.entrySet()) {
            double avgTime = entry.getValue().stream()
                    .mapToLong(task -> Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes())
                    .average()
                    .orElse(0.0);
            
            result.put(entry.getKey(), avgTime);
        }
        
        return result;
    }

    @Override
    public Map<UUID, Integer> calculateTaskDistributionByZone(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating task distribution by zone for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all tasks for the warehouse in the given period
        List<WarehouseTaskDTO> tasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> task.getCreatedAt() != null && 
                        !task.getCreatedAt().isBefore(startDateTime) && 
                        !task.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());
        
        Map<UUID, Integer> result = new HashMap<>();
        
        if (tasks.isEmpty()) {
            return result;
        }
        
        // Count tasks by zone
        for (WarehouseTaskDTO task : tasks) {
            if (task.getZoneId() != null) {
                result.put(task.getZoneId(), result.getOrDefault(task.getZoneId(), 0) + 1);
            }
        }
        
        return result;
    }

    @Override
    public Map<LocalDate, Integer> calculateDailyTaskCompletionCounts(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating daily task completion counts for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        // Get all completed tasks for the warehouse in the given period
        List<WarehouseTaskDTO> completedTasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                        task.getCompletedAt() != null &&
                        !task.getCompletedAt().toLocalDate().isBefore(startDate) && 
                        !task.getCompletedAt().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        Map<LocalDate, Integer> result = new HashMap<>();
        
        // Initialize all dates in the range with zero count
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            result.put(date, 0);
            date = date.plusDays(1);
        }
        
        // Count completions by date
        for (WarehouseTaskDTO task : completedTasks) {
            LocalDate completionDate = task.getCompletedAt().toLocalDate();
            result.put(completionDate, result.getOrDefault(completionDate, 0) + 1);
        }
        
        return result;
    }

    @Override
    public Map<String, Double> calculateTaskDelayStats(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating task delay statistics for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all completed tasks for the warehouse in the given period
        List<WarehouseTaskDTO> completedTasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                        task.getCompletedAt() != null && 
                        !task.getCompletedAt().isBefore(startDateTime) && 
                        !task.getCompletedAt().isAfter(endDateTime))
                .collect(Collectors.toList());
        
        Map<String, Double> result = new HashMap<>();
        result.put("percentageDelayed", 0.0);
        result.put("averageDelayMinutes", 0.0);
        
        if (completedTasks.isEmpty()) {
            return result;
        }
        
        int totalWithDueDate = 0;
        int totalDelayed = 0;
        double totalDelayMinutes = 0.0;
        
        for (WarehouseTaskDTO task : completedTasks) {
            if (task.getDueAt() != null) {
                totalWithDueDate++;
                
                if (task.getCompletedAt().isAfter(task.getDueAt())) {
                    totalDelayed++;
                    
                    // Calculate delay in minutes
                    long delayMinutes = Duration.between(task.getDueAt(), task.getCompletedAt()).toMinutes();
                    totalDelayMinutes += delayMinutes;
                }
            }
        }
        
        double percentageDelayed = totalWithDueDate == 0 ? 0.0 : (double) totalDelayed / totalWithDueDate * 100.0;
        double averageDelayMinutes = totalDelayed == 0 ? 0.0 : totalDelayMinutes / totalDelayed;
        
        result.put("percentageDelayed", percentageDelayed);
        result.put("averageDelayMinutes", averageDelayMinutes);
        
        return result;
    }

    @Override
    public Map<UUID, Double> generateStaffEfficiencyReport(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Generating staff efficiency report for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        // Get staff performance metrics
        List<TaskPerformanceDTO> performanceList = calculateStaffPerformance(warehouseId, startDate, endDate);
        
        Map<UUID, Double> result = new HashMap<>();
        
        // Extract efficiency scores
        for (TaskPerformanceDTO performance : performanceList) {
            result.put(performance.getStaffId(), performance.getPerformanceScore());
        }
        
        return result;
    }

    @Override
    public Map<TaskType, Map<String, Double>> identifyWorkflowBottlenecks(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Identifying workflow bottlenecks for warehouse {} from {} to {}", warehouseId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all completed or cancelled tasks for the warehouse in the given period
        List<WarehouseTaskDTO> tasks = taskService.findByWarehouseId(warehouseId).stream()
                .filter(task -> (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.CANCELLED) &&
                        task.getCreatedAt() != null && 
                        !task.getCreatedAt().isBefore(startDateTime) && 
                        !task.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());
        
        Map<TaskType, Map<String, Double>> result = new HashMap<>();
        
        if (tasks.isEmpty()) {
            return result;
        }
        
        // Group tasks by type
        Map<TaskType, List<WarehouseTaskDTO>> tasksByType = tasks.stream()
                .filter(task -> task.getType() != null)
                .collect(Collectors.groupingBy(WarehouseTaskDTO::getType));
        
        // For each task type, calculate average time spent in each status
        for (Map.Entry<TaskType, List<WarehouseTaskDTO>> entry : tasksByType.entrySet()) {
            Map<String, Double> statusTimes = new HashMap<>();
            
            // Calculate average time from creation to assignment
            double avgPendingTime = entry.getValue().stream()
                    .filter(task -> task.getCreatedAt() != null && task.getAssignedAt() != null)
                    .mapToLong(task -> Duration.between(task.getCreatedAt(), task.getAssignedAt()).toMinutes())
                    .average()
                    .orElse(0.0);
            
            // Calculate average time from assignment to start
            double avgAssignedTime = entry.getValue().stream()
                    .filter(task -> task.getAssignedAt() != null && task.getStartedAt() != null)
                    .mapToLong(task -> Duration.between(task.getAssignedAt(), task.getStartedAt()).toMinutes())
                    .average()
                    .orElse(0.0);
            
            // Calculate average time from start to completion
            double avgInProgressTime = entry.getValue().stream()
                    .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                            task.getStartedAt() != null && task.getCompletedAt() != null)
                    .mapToLong(task -> Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes())
                    .average()
                    .orElse(0.0);
            
            // Calculate average total time
            double avgTotalTime = entry.getValue().stream()
                    .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                            task.getCreatedAt() != null && task.getCompletedAt() != null)
                    .mapToLong(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()).toMinutes())
                    .average()
                    .orElse(0.0);
            
            statusTimes.put("PENDING", avgPendingTime);
            statusTimes.put("ASSIGNED", avgAssignedTime);
            statusTimes.put("IN_PROGRESS", avgInProgressTime);
            statusTimes.put("TOTAL", avgTotalTime);
            
            result.put(entry.getKey(), statusTimes);
        }
        
        return result;
    }
} 
