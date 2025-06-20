package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.TaskCompletionStatDTO;
import com.exalt.warehousing.management.dto.TaskPerformanceDTO;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.service.WarehouseTaskAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * REST controller for warehouse task analytics operations
 */
@RestController
@RequestMapping("/warehouse-tasks/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Task Analytics", description = "API for warehouse task analytics and reporting")
public class WarehouseTaskAnalyticsController {
    
    private final WarehouseTaskAnalyticsService analyticsService;
    
    @GetMapping("/completion-stats/{warehouseId}")
    @Operation(summary = "Get task completion statistics", 
            description = "Returns task completion statistics for a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TaskCompletionStatDTO.class)))
    public ResponseEntity<TaskCompletionStatDTO> getTaskCompletionStats(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get task completion stats for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.calculateTaskCompletionStats(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/staff-performance/{warehouseId}")
    @Operation(summary = "Get staff performance metrics", 
            description = "Returns performance metrics for staff members in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Performance metrics retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TaskPerformanceDTO.class)))
    public ResponseEntity<List<TaskPerformanceDTO>> getStaffPerformance(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get staff performance for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.calculateStaffPerformance(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/completion-time-by-type/{warehouseId}")
    @Operation(summary = "Get average completion time by task type", 
            description = "Returns average completion time in minutes for each task type in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Average completion times retrieved successfully")
    public ResponseEntity<Map<TaskType, Double>> getAverageCompletionTimeByTaskType(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get average completion time by task type for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.calculateAverageCompletionTimeByTaskType(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/task-distribution-by-zone/{warehouseId}")
    @Operation(summary = "Get task distribution by zone", 
            description = "Returns task count for each zone in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Task distribution retrieved successfully")
    public ResponseEntity<Map<UUID, Integer>> getTaskDistributionByZone(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get task distribution by zone for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.calculateTaskDistributionByZone(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/daily-task-completions/{warehouseId}")
    @Operation(summary = "Get daily task completion counts", 
            description = "Returns task completion count for each day in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Daily completion counts retrieved successfully")
    public ResponseEntity<Map<LocalDate, Integer>> getDailyTaskCompletionCounts(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get daily task completion counts for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.calculateDailyTaskCompletionCounts(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/task-delay-stats/{warehouseId}")
    @Operation(summary = "Get task delay statistics", 
            description = "Returns statistics about delayed tasks in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Task delay statistics retrieved successfully")
    public ResponseEntity<Map<String, Double>> getTaskDelayStats(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get task delay statistics for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.calculateTaskDelayStats(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/staff-efficiency/{warehouseId}")
    @Operation(summary = "Get staff efficiency report", 
            description = "Returns efficiency scores for staff members in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Staff efficiency report retrieved successfully")
    public ResponseEntity<Map<UUID, Double>> getStaffEfficiencyReport(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get staff efficiency report for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.generateStaffEfficiencyReport(warehouseId, startDate, endDate));
    }
    
    @GetMapping("/workflow-bottlenecks/{warehouseId}")
    @Operation(summary = "Identify workflow bottlenecks", 
            description = "Returns analysis of workflow bottlenecks in a specific warehouse in a given period")
    @ApiResponse(responseCode = "200", description = "Workflow bottleneck analysis retrieved successfully")
    public ResponseEntity<Map<TaskType, Map<String, Double>>> getWorkflowBottlenecks(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to identify workflow bottlenecks for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        return ResponseEntity.ok(analyticsService.identifyWorkflowBottlenecks(warehouseId, startDate, endDate));
    }
} 
