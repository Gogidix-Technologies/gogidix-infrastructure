package com.exalt.warehousing.analytics.controller;

import com.exalt.warehousing.analytics.dto.DashboardData;
import com.exalt.warehousing.analytics.dto.MetricDTO;
import com.exalt.warehousing.analytics.model.*;
import com.exalt.warehousing.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Warehouse Analytics
 * 
 * Provides comprehensive analytics endpoints for warehouse performance monitoring
 */
@RestController
@RequestMapping("/api/v1/warehousing/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Analytics", description = "Warehouse Analytics and Performance Monitoring API")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // ========== Dashboard and Overview ==========

    /**
     * Get warehouse dashboard data
     */
    @GetMapping("/dashboard/{warehouseId}")
    @Operation(summary = "Get warehouse dashboard", description = "Retrieve comprehensive dashboard data for a warehouse")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<DashboardData> getDashboard(@PathVariable UUID warehouseId) {
        log.info("Retrieving dashboard data for warehouse: {}", warehouseId);
        DashboardData dashboard = analyticsService.getDashboardData(warehouseId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get real-time metrics
     */
    @GetMapping("/realtime/{warehouseId}")
    @Operation(summary = "Get real-time metrics", description = "Retrieve real-time performance metrics for a warehouse")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> getRealTimeMetrics(@PathVariable UUID warehouseId) {
        log.info("Retrieving real-time metrics for warehouse: {}", warehouseId);
        List<PerformanceMetric> metrics = analyticsService.getRealTimeMetrics(warehouseId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get KPI summary
     */
    @GetMapping("/kpi/{warehouseId}")
    @Operation(summary = "Get KPI summary", description = "Retrieve KPI summary for a warehouse within date range")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getKPISummary(
            @PathVariable UUID warehouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Retrieving KPI summary for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        Map<String, Object> kpiSummary = analyticsService.getKPISummary(warehouseId, startDate, endDate);
        return ResponseEntity.ok(kpiSummary);
    }

    // ========== Metric Management ==========

    /**
     * Record new metric
     */
    @PostMapping("/metrics")
    @Operation(summary = "Record new metric", description = "Record a new performance metric")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<PerformanceMetric> recordMetric(
            @Valid @RequestBody MetricDTO metricDTO,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Recording new metric: {} for warehouse: {}", metricDTO.getMetricName(), metricDTO.getWarehouseId());
        PerformanceMetric metric = analyticsService.recordMetric(metricDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(metric);
    }

    /**
     * Get metric by ID
     */
    @GetMapping("/metrics/{metricId}")
    @Operation(summary = "Get metric by ID", description = "Retrieve a specific performance metric")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<PerformanceMetric> getMetric(@PathVariable UUID metricId) {
        log.info("Retrieving metric: {}", metricId);
        PerformanceMetric metric = analyticsService.getMetric(metricId);
        return ResponseEntity.ok(metric);
    }

    /**
     * Get metrics by warehouse
     */
    @GetMapping("/metrics/warehouse/{warehouseId}")
    @Operation(summary = "Get warehouse metrics", description = "Retrieve metrics for a specific warehouse with pagination")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Page<PerformanceMetric>> getWarehouseMetrics(
            @PathVariable UUID warehouseId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        log.info("Retrieving metrics for warehouse: {} with page: {}, size: {}", warehouseId, page, size);
        Page<PerformanceMetric> metrics = analyticsService.getMetricsByWarehouse(warehouseId, pageable);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by type and time range
     */
    @GetMapping("/metrics/type/{metricType}")
    @Operation(summary = "Get metrics by type", description = "Retrieve metrics by type within date range")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> getMetricsByType(
            @PathVariable MetricType metricType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Retrieving metrics of type: {} from {} to {}", metricType, startDate, endDate);
        List<PerformanceMetric> metrics = analyticsService.getMetricsByTypeAndTimeRange(metricType, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by category
     */
    @GetMapping("/metrics/category/{category}")
    @Operation(summary = "Get metrics by category", description = "Retrieve metrics by category")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> getMetricsByCategory(@PathVariable MetricCategory category) {
        log.info("Retrieving metrics for category: {}", category);
        List<PerformanceMetric> metrics = analyticsService.getMetricsByCategory(category);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Update metric value
     */
    @PutMapping("/metrics/{metricId}")
    @Operation(summary = "Update metric", description = "Update a performance metric value")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<PerformanceMetric> updateMetric(
            @PathVariable UUID metricId,
            @RequestParam BigDecimal newValue,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Updating metric: {} with new value: {} by user: {}", metricId, newValue, userId);
        PerformanceMetric metric = analyticsService.updateMetric(metricId, newValue, userId);
        return ResponseEntity.ok(metric);
    }

    /**
     * Delete metric
     */
    @DeleteMapping("/metrics/{metricId}")
    @Operation(summary = "Delete metric", description = "Delete a performance metric")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN')")
    public ResponseEntity<Void> deleteMetric(@PathVariable UUID metricId) {
        log.info("Deleting metric: {}", metricId);
        analyticsService.deleteMetric(metricId);
        return ResponseEntity.noContent().build();
    }

    // ========== Analytics and Calculations ==========

    /**
     * Calculate performance score
     */
    @PostMapping("/metrics/{metricId}/calculate-score")
    @Operation(summary = "Calculate performance score", description = "Calculate performance score for a metric")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<BigDecimal> calculatePerformanceScore(@PathVariable UUID metricId) {
        log.info("Calculating performance score for metric: {}", metricId);
        BigDecimal score = analyticsService.calculatePerformanceScore(metricId);
        return ResponseEntity.ok(score);
    }

    /**
     * Detect anomalies
     */
    @GetMapping("/anomalies/{warehouseId}")
    @Operation(summary = "Detect anomalies", description = "Detect anomalies in warehouse metrics")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> detectAnomalies(
            @PathVariable UUID warehouseId,
            @RequestParam MetricType metricType,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Detecting anomalies for warehouse: {}, type: {}, days: {}", warehouseId, metricType, days);
        List<PerformanceMetric> anomalies = analyticsService.detectAnomalies(warehouseId, metricType, days);
        return ResponseEntity.ok(anomalies);
    }

    /**
     * Calculate correlation
     */
    @GetMapping("/correlation/{warehouseId}")
    @Operation(summary = "Calculate correlation", description = "Calculate correlation between two metric types")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<BigDecimal> calculateCorrelation(
            @PathVariable UUID warehouseId,
            @RequestParam MetricType metric1,
            @RequestParam MetricType metric2,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Calculating correlation for warehouse: {} between {} and {} over {} days", 
                warehouseId, metric1, metric2, days);
        BigDecimal correlation = analyticsService.calculateCorrelation(warehouseId, metric1, metric2, days);
        return ResponseEntity.ok(correlation);
    }

    /**
     * Get performance trends
     */
    @GetMapping("/trends/{warehouseId}")
    @Operation(summary = "Get performance trends", description = "Get performance trends over time")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Map<LocalDateTime, BigDecimal>> getPerformanceTrends(
            @PathVariable UUID warehouseId,
            @RequestParam MetricType metricType,
            @RequestParam MeasurementPeriod period,
            @RequestParam(defaultValue = "30") int periodCount) {
        
        log.info("Getting performance trends for warehouse: {}, type: {}, period: {}, count: {}", 
                warehouseId, metricType, period, periodCount);
        Map<LocalDateTime, BigDecimal> trends = analyticsService.getPerformanceTrends(
                warehouseId, metricType, period, periodCount);
        return ResponseEntity.ok(trends);
    }

    // ========== Alert Management ==========

    /**
     * Get active alerts
     */
    @GetMapping("/alerts")
    @Operation(summary = "Get active alerts", description = "Retrieve all active alerts")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> getActiveAlerts() {
        log.info("Retrieving active alerts");
        List<PerformanceMetric> alerts = analyticsService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get alerts by severity
     */
    @GetMapping("/alerts/severity/{alertLevel}")
    @Operation(summary = "Get alerts by severity", description = "Retrieve alerts by severity level")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> getAlertsBySeverity(@PathVariable AlertLevel alertLevel) {
        log.info("Retrieving alerts with severity: {}", alertLevel);
        List<PerformanceMetric> alerts = analyticsService.getAlertsBySeverity(alertLevel);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Acknowledge alert
     */
    @PostMapping("/alerts/{metricId}/acknowledge")
    @Operation(summary = "Acknowledge alert", description = "Acknowledge an alert")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> acknowledgeAlert(
            @PathVariable UUID metricId,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Acknowledging alert for metric: {} by user: {}", metricId, userId);
        analyticsService.acknowledgeAlert(metricId, userId);
        return ResponseEntity.ok().build();
    }

    // ========== Search and Filtering ==========

    /**
     * Search metrics
     */
    @GetMapping("/search")
    @Operation(summary = "Search metrics", description = "Search metrics with full-text search")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> searchMetrics(@RequestParam String searchText) {
        log.info("Searching metrics with text: {}", searchText);
        List<PerformanceMetric> metrics = analyticsService.searchMetrics(searchText);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Find similar metrics
     */
    @GetMapping("/metrics/{metricId}/similar")
    @Operation(summary = "Find similar metrics", description = "Find metrics similar to the specified metric")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> findSimilarMetrics(@PathVariable UUID metricId) {
        log.info("Finding similar metrics to: {}", metricId);
        List<PerformanceMetric> similarMetrics = analyticsService.findSimilarMetrics(metricId);
        return ResponseEntity.ok(similarMetrics);
    }

    /**
     * Find outliers
     */
    @GetMapping("/outliers/{warehouseId}")
    @Operation(summary = "Find outlier metrics", description = "Find outlier metrics for a warehouse")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PerformanceMetric>> findOutliers(
            @PathVariable UUID warehouseId,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Finding outliers for warehouse: {} over {} days", warehouseId, days);
        List<PerformanceMetric> outliers = analyticsService.findOutlierMetrics(warehouseId, days);
        return ResponseEntity.ok(outliers);
    }

    // ========== Comparison and Benchmarking ==========

    /**
     * Compare warehouse performance
     */
    @PostMapping("/compare")
    @Operation(summary = "Compare warehouses", description = "Compare performance across multiple warehouses")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Map<UUID, BigDecimal>> compareWarehouses(
            @RequestBody List<UUID> warehouseIds,
            @RequestParam MetricType metricType,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Comparing {} warehouses for metric type: {} over {} days", 
                warehouseIds.size(), metricType, days);
        Map<UUID, BigDecimal> comparison = analyticsService.compareWarehousePerformance(warehouseIds, metricType, days);
        return ResponseEntity.ok(comparison);
    }

    /**
     * Get top performing warehouses
     */
    @GetMapping("/top-performers")
    @Operation(summary = "Get top performers", description = "Get top performing warehouses")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<UUID>> getTopPerformers(
            @RequestParam MetricType metricType,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting top {} performers for metric type: {} over {} days", limit, metricType, days);
        List<UUID> topPerformers = analyticsService.getTopPerformingWarehouses(metricType, days, limit);
        return ResponseEntity.ok(topPerformers);
    }

    // ========== Data Export ==========

    /**
     * Export metrics to CSV
     */
    @GetMapping("/export/csv/{warehouseId}")
    @Operation(summary = "Export to CSV", description = "Export warehouse metrics to CSV")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<byte[]> exportToCSV(
            @PathVariable UUID warehouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Exporting metrics to CSV for warehouse: {} from {} to {}", warehouseId, startDate, endDate);
        byte[] csvData = analyticsService.exportMetricsToCSV(warehouseId, startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=warehouse_metrics_" + warehouseId + ".csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    /**
     * Export dashboard to Excel
     */
    @GetMapping("/export/excel/{warehouseId}")
    @Operation(summary = "Export to Excel", description = "Export warehouse dashboard to Excel")
    @PreAuthorize("hasRole('ANALYTICS_VIEWER') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<byte[]> exportToExcel(@PathVariable UUID warehouseId) {
        log.info("Exporting dashboard to Excel for warehouse: {}", warehouseId);
        byte[] excelData = analyticsService.exportDashboardToExcel(warehouseId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=warehouse_dashboard_" + warehouseId + ".xlsx");
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    // ========== System Management ==========

    /**
     * Get API metrics
     */
    @GetMapping("/system/metrics")
    @Operation(summary = "Get API metrics", description = "Get API performance metrics")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAPIMetrics() {
        log.info("Retrieving API metrics");
        Map<String, Object> apiMetrics = analyticsService.getAPIMetrics();
        return ResponseEntity.ok(apiMetrics);
    }

    /**
     * Get storage statistics
     */
    @GetMapping("/system/storage")
    @Operation(summary = "Get storage statistics", description = "Get storage usage statistics")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN')")
    public ResponseEntity<Map<String, Object>> getStorageStatistics() {
        log.info("Retrieving storage statistics");
        Map<String, Object> storageStats = analyticsService.getStorageStatistics();
        return ResponseEntity.ok(storageStats);
    }

    /**
     * Validate data integrity
     */
    @PostMapping("/system/validate")
    @Operation(summary = "Validate data integrity", description = "Validate analytics data integrity")
    @PreAuthorize("hasRole('ANALYTICS_ADMIN')")
    public ResponseEntity<Map<String, Object>> validateDataIntegrity() {
        log.info("Validating data integrity");
        Map<String, Object> validationResults = analyticsService.validateDataIntegrity();
        return ResponseEntity.ok(validationResults);
    }
}