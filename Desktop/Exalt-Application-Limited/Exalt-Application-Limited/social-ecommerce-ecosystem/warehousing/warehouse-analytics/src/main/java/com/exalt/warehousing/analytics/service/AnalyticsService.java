package com.exalt.warehousing.analytics.service;

import com.exalt.warehousing.analytics.dto.MetricDTO;
import com.exalt.warehousing.analytics.dto.AnalyticsReportRequest;
import com.exalt.warehousing.analytics.dto.DashboardData;
import com.exalt.warehousing.analytics.dto.TrendAnalysisResult;
import com.exalt.warehousing.analytics.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Analytics Service Interface
 * 
 * Provides comprehensive analytics capabilities for warehouse operations
 */
public interface AnalyticsService {

    // ========== Metric Management ==========

    /**
     * Record a new performance metric
     */
    PerformanceMetric recordMetric(MetricDTO metricDTO, String recordedBy);

    /**
     * Get metric by ID
     */
    PerformanceMetric getMetric(UUID metricId);

    /**
     * Get metrics by warehouse
     */
    List<PerformanceMetric> getMetricsByWarehouse(UUID warehouseId);

    /**
     * Get metrics by warehouse with pagination
     */
    Page<PerformanceMetric> getMetricsByWarehouse(UUID warehouseId, Pageable pageable);

    /**
     * Get metrics by type and time range
     */
    List<PerformanceMetric> getMetricsByTypeAndTimeRange(MetricType metricType, 
                                                        LocalDateTime startDate, 
                                                        LocalDateTime endDate);

    /**
     * Get metrics by category
     */
    List<PerformanceMetric> getMetricsByCategory(MetricCategory category);

    /**
     * Update metric with new value
     */
    PerformanceMetric updateMetric(UUID metricId, BigDecimal newValue, String updatedBy);

    /**
     * Delete metric
     */
    void deleteMetric(UUID metricId);

    /**
     * Bulk import metrics
     */
    List<PerformanceMetric> bulkImportMetrics(List<MetricDTO> metrics, String importedBy);

    // ========== Analytics and Calculations ==========

    /**
     * Calculate performance score for a metric
     */
    BigDecimal calculatePerformanceScore(UUID metricId);

    /**
     * Calculate trend direction for a metric over time
     */
    TrendDirection calculateTrendDirection(UUID warehouseId, MetricType metricType, int days);

    /**
     * Detect anomalies in metrics
     */
    List<PerformanceMetric> detectAnomalies(UUID warehouseId, MetricType metricType, int days);

    /**
     * Calculate correlation between two metric types
     */
    BigDecimal calculateCorrelation(UUID warehouseId, MetricType metric1, MetricType metric2, int days);

    /**
     * Perform trend analysis
     */
    TrendAnalysisResult performTrendAnalysis(UUID warehouseId, MetricType metricType, 
                                           LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculate metric statistics
     */
    Map<String, BigDecimal> calculateStatistics(UUID warehouseId, MetricType metricType, int days);

    /**
     * Forecast future values
     */
    List<BigDecimal> forecastMetricValues(UUID warehouseId, MetricType metricType, int forecastDays);

    // ========== Dashboard and Reporting ==========

    /**
     * Get dashboard data for warehouse
     */
    DashboardData getDashboardData(UUID warehouseId);

    /**
     * Get real-time metrics
     */
    List<PerformanceMetric> getRealTimeMetrics(UUID warehouseId);

    /**
     * Get KPI summary
     */
    Map<String, Object> getKPISummary(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generate analytics report
     */
    AnalyticsReport generateReport(AnalyticsReportRequest request, String generatedBy);

    /**
     * Get report by ID
     */
    AnalyticsReport getReport(UUID reportId);

    /**
     * Get reports by type
     */
    List<AnalyticsReport> getReportsByType(ReportType reportType);

    /**
     * Download report
     */
    byte[] downloadReport(UUID reportId, String accessedBy);

    /**
     * Schedule recurring report
     */
    void scheduleReport(AnalyticsReportRequest request, String cronExpression, String scheduledBy);

    // ========== Alert Management ==========

    /**
     * Process metric alerts
     */
    void processMetricAlerts();

    /**
     * Get active alerts
     */
    List<PerformanceMetric> getActiveAlerts();

    /**
     * Get alerts by severity
     */
    List<PerformanceMetric> getAlertsBySeverity(AlertLevel alertLevel);

    /**
     * Acknowledge alert
     */
    void acknowledgeAlert(UUID metricId, String acknowledgedBy);

    /**
     * Configure alert thresholds
     */
    void configureAlertThresholds(UUID warehouseId, MetricType metricType, 
                                BigDecimal minThreshold, BigDecimal maxThreshold);

    // ========== Search and Filtering ==========

    /**
     * Search metrics by text
     */
    List<PerformanceMetric> searchMetrics(String searchText);

    /**
     * Search metrics with filters
     */
    Page<PerformanceMetric> searchMetricsWithFilters(String searchText, 
                                                    UUID warehouseId, 
                                                    MetricType metricType, 
                                                    MetricCategory category,
                                                    LocalDateTime startDate,
                                                    LocalDateTime endDate,
                                                    Pageable pageable);

    /**
     * Find similar metrics
     */
    List<PerformanceMetric> findSimilarMetrics(UUID metricId);

    /**
     * Find outlier metrics
     */
    List<PerformanceMetric> findOutlierMetrics(UUID warehouseId, int days);

    // ========== Aggregation and Analysis ==========

    /**
     * Get metrics by warehouse comparison
     */
    Map<UUID, BigDecimal> compareWarehousePerformance(List<UUID> warehouseIds, 
                                                     MetricType metricType, 
                                                     int days);

    /**
     * Get top performing warehouses
     */
    List<UUID> getTopPerformingWarehouses(MetricType metricType, int days, int limit);

    /**
     * Get metric distribution by category
     */
    Map<MetricCategory, Long> getMetricDistributionByCategory(UUID warehouseId);

    /**
     * Get performance trends over time
     */
    Map<LocalDateTime, BigDecimal> getPerformanceTrends(UUID warehouseId, 
                                                       MetricType metricType, 
                                                       MeasurementPeriod period, 
                                                       int periodCount);

    /**
     * Calculate warehouse efficiency score
     */
    BigDecimal calculateWarehouseEfficiencyScore(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get capacity utilization metrics
     */
    Map<String, BigDecimal> getCapacityUtilization(UUID warehouseId);

    // ========== Data Export and Integration ==========

    /**
     * Export metrics to CSV
     */
    byte[] exportMetricsToCSV(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Export dashboard data to Excel
     */
    byte[] exportDashboardToExcel(UUID warehouseId);

    /**
     * Sync metrics to external BI system
     */
    void syncToExternalBI(UUID warehouseId, String systemName);

    /**
     * Get API metrics for integration monitoring
     */
    Map<String, Object> getAPIMetrics();

    // ========== Data Management ==========

    /**
     * Archive old metrics
     */
    void archiveOldMetrics(int daysOld);

    /**
     * Cleanup expired data
     */
    void cleanupExpiredData();

    /**
     * Reindex search data
     */
    void reindexSearchData();

    /**
     * Validate data integrity
     */
    Map<String, Object> validateDataIntegrity();

    /**
     * Get storage statistics
     */
    Map<String, Object> getStorageStatistics();

    // ========== Configuration Management ==========

    /**
     * Update metric configuration
     */
    void updateMetricConfiguration(UUID warehouseId, MetricType metricType, Map<String, String> config);

    /**
     * Get system configuration
     */
    Map<String, String> getSystemConfiguration();

    /**
     * Update system configuration
     */
    void updateSystemConfiguration(Map<String, String> config, String updatedBy);

    /**
     * Reset warehouse metrics
     */
    void resetWarehouseMetrics(UUID warehouseId, String resetBy);
}