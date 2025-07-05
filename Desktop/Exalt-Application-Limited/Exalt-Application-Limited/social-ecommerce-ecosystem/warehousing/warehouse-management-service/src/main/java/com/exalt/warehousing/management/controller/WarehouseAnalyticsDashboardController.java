package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.TaskCompletionStatDTO;
import com.exalt.warehousing.management.dto.TaskPerformanceDTO;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.service.WarehouseTaskAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for warehouse analytics dashboard
 */
@RestController
@RequestMapping("/api/warehouses/{warehouseId}/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Analytics", description = "API for warehouse analytics and performance metrics")
public class WarehouseAnalyticsDashboardController {

    private final WarehouseTaskAnalyticsService analyticsService;

    @GetMapping("/task-completion")
    @Operation(summary = "Get task completion statistics for a warehouse")
    public ResponseEntity<TaskCompletionStatDTO> getTaskCompletionStats(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get task completion stats for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        TaskCompletionStatDTO stats = analyticsService.calculateTaskCompletionStats(warehouseId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/staff-performance")
    @Operation(summary = "Get staff performance metrics for a warehouse")
    public ResponseEntity<List<TaskPerformanceDTO>> getStaffPerformance(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get staff performance for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        List<TaskPerformanceDTO> performance = analyticsService.calculateStaffPerformance(warehouseId, startDate, endDate);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/average-completion-time")
    @Operation(summary = "Get average completion time by task type for a warehouse")
    public ResponseEntity<Map<TaskType, Double>> getAverageCompletionTimeByTaskType(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get average completion time by task type for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Map<TaskType, Double> avgTimes = analyticsService.calculateAverageCompletionTimeByTaskType(warehouseId, startDate, endDate);
        return ResponseEntity.ok(avgTimes);
    }

    @GetMapping("/zone-distribution")
    @Operation(summary = "Get task distribution by zone for a warehouse")
    public ResponseEntity<Map<UUID, Integer>> getTaskDistributionByZone(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get task distribution by zone for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Map<UUID, Integer> distribution = analyticsService.calculateTaskDistributionByZone(warehouseId, startDate, endDate);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/daily-completion")
    @Operation(summary = "Get daily task completion counts for a warehouse")
    public ResponseEntity<Map<LocalDate, Integer>> getDailyTaskCompletionCounts(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get daily task completion counts for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Map<LocalDate, Integer> dailyCounts = analyticsService.calculateDailyTaskCompletionCounts(warehouseId, startDate, endDate);
        return ResponseEntity.ok(dailyCounts);
    }

    @GetMapping("/delay-stats")
    @Operation(summary = "Get task delay statistics for a warehouse")
    public ResponseEntity<Map<String, Double>> getTaskDelayStats(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get task delay statistics for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Map<String, Double> delayStats = analyticsService.calculateTaskDelayStats(warehouseId, startDate, endDate);
        return ResponseEntity.ok(delayStats);
    }

    @GetMapping("/staff-efficiency")
    @Operation(summary = "Get staff efficiency report for a warehouse")
    public ResponseEntity<Map<UUID, Double>> getStaffEfficiencyReport(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get staff efficiency report for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Map<UUID, Double> efficiencyReport = analyticsService.generateStaffEfficiencyReport(warehouseId, startDate, endDate);
        return ResponseEntity.ok(efficiencyReport);
    }

    @GetMapping("/bottlenecks")
    @Operation(summary = "Identify workflow bottlenecks for a warehouse")
    public ResponseEntity<Map<TaskType, Map<String, Double>>> identifyWorkflowBottlenecks(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to identify workflow bottlenecks for warehouse: {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Map<TaskType, Map<String, Double>> bottlenecks = analyticsService.identifyWorkflowBottlenecks(warehouseId, startDate, endDate);
        return ResponseEntity.ok(bottlenecks);
    }
} 
