package com.exalt.warehousing.analytics.service;

import com.exalt.warehousing.analytics.dto.*;
import com.exalt.warehousing.analytics.model.*;
import com.exalt.warehousing.analytics.repository.elasticsearch.PerformanceMetricSearchRepository;
import com.exalt.warehousing.analytics.repository.jpa.PerformanceMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Analytics Service
 * 
 * Provides comprehensive analytics capabilities for warehouse operations
 * including real-time monitoring, predictive analytics, and reporting
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PerformanceMetricRepository metricRepository;
    private final PerformanceMetricSearchRepository searchRepository;

    // Statistical constants
    private static final BigDecimal PERFORMANCE_WEIGHT_EFFICIENCY = new BigDecimal("0.3");
    private static final BigDecimal PERFORMANCE_WEIGHT_ACCURACY = new BigDecimal("0.25");
    private static final BigDecimal PERFORMANCE_WEIGHT_SPEED = new BigDecimal("0.25");
    private static final BigDecimal PERFORMANCE_WEIGHT_COST = new BigDecimal("0.2");
    private static final BigDecimal ANOMALY_THRESHOLD = new BigDecimal("2.0"); // Standard deviations
    private static final int MAX_FORECAST_DAYS = 90;

    // ========== Metric Management ==========

    @Override
    public PerformanceMetric recordMetric(MetricDTO metricDTO, String recordedBy) {
        log.debug("Recording new metric: {} for warehouse: {}", metricDTO.getMetricType(), metricDTO.getWarehouseId());
        
        PerformanceMetric metric = PerformanceMetric.builder()
                .warehouseId(metricDTO.getWarehouseId())
                .metricType(metricDTO.getMetricType())
                .metricCategory(metricDTO.getMetricCategory())
                .metricValue(metricDTO.getValue())
                .unitOfMeasure(metricDTO.getUnit())
                .measurementPeriod(metricDTO.getMeasurementPeriod())
                .lastUpdatedBy(recordedBy)
                .recordedAt(LocalDateTime.now())
                .isActive(true)
                .performanceScore(calculatePerformanceScoreForMetric(metricDTO))
                .alertLevel(determineAlertLevel(metricDTO))
                .build();

        // Save to PostgreSQL
        PerformanceMetric savedMetric = metricRepository.save(metric);
        
        // Index in Elasticsearch for search
        try {
            searchRepository.save(savedMetric);
            log.debug("Metric indexed in Elasticsearch: {}", savedMetric.getId());
        } catch (Exception e) {
            log.warn("Failed to index metric in Elasticsearch: {}", e.getMessage());
        }

        return savedMetric;
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceMetric getMetric(UUID metricId) {
        return metricRepository.findById(metricId)
                .orElseThrow(() -> new RuntimeException("Metric not found with ID: " + metricId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> getMetricsByWarehouse(UUID warehouseId) {
        return metricRepository.findByWarehouseId(warehouseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerformanceMetric> getMetricsByWarehouse(UUID warehouseId, Pageable pageable) {
        return metricRepository.findByWarehouseId(warehouseId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> getMetricsByTypeAndTimeRange(MetricType metricType, 
                                                              LocalDateTime startDate, 
                                                              LocalDateTime endDate) {
        return metricRepository.findByMetricTypeAndRecordedAtBetween(metricType, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> getMetricsByCategory(MetricCategory category) {
        return metricRepository.findByMetricCategory(category);
    }

    @Override
    public PerformanceMetric updateMetric(UUID metricId, BigDecimal newValue, String updatedBy) {
        PerformanceMetric metric = getMetric(metricId);
        metric.setValue(newValue);
        metric.setLastUpdatedBy(updatedBy);
        metric.setLastUpdatedAt(LocalDateTime.now());
        
        // Recalculate performance score
        MetricDTO metricDTO = convertToDTO(metric);
        metricDTO.setValue(newValue);
        metric.setPerformanceScore(calculatePerformanceScoreForMetric(metricDTO));
        metric.setAlertLevel(determineAlertLevel(metricDTO));

        PerformanceMetric updatedMetric = metricRepository.save(metric);
        
        // Update in Elasticsearch
        try {
            searchRepository.save(updatedMetric);
        } catch (Exception e) {
            log.warn("Failed to update metric in Elasticsearch: {}", e.getMessage());
        }

        return updatedMetric;
    }

    @Override
    public void deleteMetric(UUID metricId) {
        metricRepository.deleteById(metricId);
        try {
            searchRepository.deleteById(metricId);
        } catch (Exception e) {
            log.warn("Failed to delete metric from Elasticsearch: {}", e.getMessage());
        }
    }

    @Override
    public List<PerformanceMetric> bulkImportMetrics(List<MetricDTO> metrics, String importedBy) {
        List<PerformanceMetric> savedMetrics = new ArrayList<>();
        
        for (MetricDTO metricDTO : metrics) {
            try {
                PerformanceMetric metric = recordMetric(metricDTO, importedBy);
                savedMetrics.add(metric);
            } catch (Exception e) {
                log.error("Failed to import metric: {}", e.getMessage());
            }
        }
        
        log.info("Bulk imported {} out of {} metrics", savedMetrics.size(), metrics.size());
        return savedMetrics;
    }

    // ========== Analytics and Calculations ==========

    @Override
    public BigDecimal calculatePerformanceScore(UUID metricId) {
        PerformanceMetric metric = getMetric(metricId);
        MetricDTO metricDTO = convertToDTO(metric);
        return calculatePerformanceScoreForMetric(metricDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TrendDirection calculateTrendDirection(UUID warehouseId, MetricType metricType, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metricType, startDate, endDate);
        
        if (metrics.size() < 2) {
            return TrendDirection.STABLE;
        }
        
        // Calculate linear regression slope
        double slope = calculateLinearRegressionSlope(metrics);
        
        if (slope > 0.05) {
            return TrendDirection.STRONG_UPWARD;
        } else if (slope > 0.01) {
            return TrendDirection.UPWARD;
        } else if (slope < -0.05) {
            return TrendDirection.STRONG_DOWNWARD;
        } else if (slope < -0.01) {
            return TrendDirection.DOWNWARD;
        } else {
            return TrendDirection.STABLE;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> detectAnomalies(UUID warehouseId, MetricType metricType, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metricType, startDate, endDate);
        
        if (metrics.size() < 10) {
            return new ArrayList<>(); // Not enough data for anomaly detection
        }
        
        BigDecimal mean = calculateMean(metrics);
        BigDecimal stdDev = calculateStandardDeviation(metrics, mean);
        BigDecimal threshold = stdDev.multiply(ANOMALY_THRESHOLD);
        
        return metrics.stream()
                .filter(metric -> {
                    BigDecimal deviation = metric.getValue().subtract(mean).abs();
                    return deviation.compareTo(threshold) > 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateCorrelation(UUID warehouseId, MetricType metric1, MetricType metric2, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<PerformanceMetric> metrics1 = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metric1, startDate, endDate);
        List<PerformanceMetric> metrics2 = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metric2, startDate, endDate);
        
        if (metrics1.size() < 3 || metrics2.size() < 3) {
            return BigDecimal.ZERO;
        }
        
        return calculatePearsonCorrelation(metrics1, metrics2);
    }

    @Override
    @Transactional(readOnly = true)
    public TrendAnalysisResult performTrendAnalysis(UUID warehouseId, MetricType metricType, 
                                                  LocalDateTime startDate, LocalDateTime endDate) {
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metricType, startDate, endDate);
        
        TrendAnalysisResult result = new TrendAnalysisResult(warehouseId, metricType, startDate, endDate);
        
        if (metrics.isEmpty()) {
            return result;
        }
        
        // Basic statistics
        BigDecimal mean = calculateMean(metrics);
        BigDecimal stdDev = calculateStandardDeviation(metrics, mean);
        BigDecimal min = findMinValue(metrics);
        BigDecimal max = findMaxValue(metrics);
        BigDecimal median = calculateMedian(metrics);
        
        result.setAverageValue(mean);
        result.setStandardDeviation(stdDev);
        result.setVariance(stdDev.multiply(stdDev));
        result.setMinimum(min);
        result.setMaximum(max);
        result.setMedian(median);
        
        // Trend analysis
        double slope = calculateLinearRegressionSlope(metrics);
        result.setTrendSlope(BigDecimal.valueOf(slope));
        result.setOverallTrend(determineTrendDirection(slope));
        
        // Calculate change percentage
        if (metrics.size() >= 2) {
            BigDecimal firstValue = metrics.get(0).getValue();
            BigDecimal lastValue = metrics.get(metrics.size() - 1).getValue();
            if (firstValue.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal changePercentage = lastValue.subtract(firstValue)
                        .divide(firstValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                result.setChangePercentage(changePercentage);
            }
        }
        
        // Set data quality metrics
        result.setDataPointCount(metrics.size());
        result.setDataQualityScore(calculateDataQualityScore(metrics));
        result.setCompletenessScore(calculateCompletenessScore(metrics, startDate, endDate));
        
        // Generate insights
        result.setInsights(generateInsights(metrics, result));
        result.setRecommendations(generateRecommendations(result));
        result.setPerformanceGrade(calculatePerformanceGrade(result));
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateStatistics(UUID warehouseId, MetricType metricType, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metricType, startDate, endDate);
        
        Map<String, BigDecimal> statistics = new HashMap<>();
        
        if (!metrics.isEmpty()) {
            BigDecimal mean = calculateMean(metrics);
            statistics.put("mean", mean);
            statistics.put("standardDeviation", calculateStandardDeviation(metrics, mean));
            statistics.put("minimum", findMinValue(metrics));
            statistics.put("maximum", findMaxValue(metrics));
            statistics.put("median", calculateMedian(metrics));
            statistics.put("count", BigDecimal.valueOf(metrics.size()));
            statistics.put("sum", metrics.stream()
                    .map(PerformanceMetric::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BigDecimal> forecastMetricValues(UUID warehouseId, MetricType metricType, int forecastDays) {
        if (forecastDays > MAX_FORECAST_DAYS) {
            throw new IllegalArgumentException("Forecast period cannot exceed " + MAX_FORECAST_DAYS + " days");
        }
        
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(Math.min(forecastDays * 3, 180)); // Use 3x forecast period or 180 days max
        
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metricType, startDate, endDate);
        
        if (metrics.size() < 7) {
            return new ArrayList<>(); // Not enough data for forecasting
        }
        
        return generateSimpleForecast(metrics, forecastDays);
    }

    // ========== Dashboard and Reporting ==========

    @Override
    @Transactional(readOnly = true)
    public DashboardData getDashboardData(UUID warehouseId) {
        DashboardData dashboardData = new DashboardData();
        dashboardData.setWarehouseId(warehouseId);
        dashboardData.setGeneratedAt(LocalDateTime.now());
        
        // Get recent metrics
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        List<PerformanceMetric> recentMetrics = metricRepository
                .findByWarehouseIdAndRecordedAtAfter(warehouseId, last24Hours);
        
        dashboardData.setTotalMetrics(recentMetrics.size());
        
        // Calculate efficiency score
        BigDecimal efficiencyScore = calculateWarehouseEfficiencyScore(warehouseId, 
                LocalDateTime.now().minusDays(7), LocalDateTime.now());
        dashboardData.setEfficiencyScore(efficiencyScore);
        
        // Get active alerts
        List<PerformanceMetric> alerts = getActiveAlertsForWarehouse(warehouseId);
        dashboardData.setActiveAlerts(alerts.size());
        
        // Performance by category
        Map<MetricCategory, DashboardData.CategorySummary> categoryPerformance = new HashMap<>();
        for (MetricCategory category : MetricCategory.values()) {
            List<PerformanceMetric> categoryMetrics = metricRepository
                    .findByWarehouseIdAndMetricCategoryAndRecordedAtAfter(warehouseId, category, last24Hours);
            if (!categoryMetrics.isEmpty()) {
                BigDecimal avgScore = categoryMetrics.stream()
                        .map(PerformanceMetric::getPerformanceScore)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(categoryMetrics.size()), RoundingMode.HALF_UP);
                
                DashboardData.CategorySummary summary = DashboardData.CategorySummary.builder()
                        .category(category)
                        .averageScore(avgScore)
                        .totalMetrics(categoryMetrics.size())
                        .alertCount((int) categoryMetrics.stream()
                                .filter(m -> m.getIsAlertTriggered() != null && m.getIsAlertTriggered())
                                .count())
                        .trendDirection("STABLE") // TODO: Calculate actual trend
                        .topMetrics(categoryMetrics.stream()
                                .sorted((a, b) -> b.getPerformanceScore().compareTo(a.getPerformanceScore()))
                                .limit(5)
                                .collect(Collectors.toList()))
                        .build();
                
                categoryPerformance.put(category, summary);
            }
        }
        dashboardData.setCategoryPerformance(categoryPerformance);
        
        return dashboardData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> getRealTimeMetrics(UUID warehouseId) {
        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        return metricRepository.findByWarehouseIdAndRecordedAtAfter(warehouseId, lastHour);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getKPISummary(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> kpiSummary = new HashMap<>();
        
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndRecordedAtBetween(warehouseId, startDate, endDate);
        
        if (!metrics.isEmpty()) {
            // Calculate key KPIs
            BigDecimal avgPerformanceScore = metrics.stream()
                    .map(PerformanceMetric::getPerformanceScore)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(metrics.size()), RoundingMode.HALF_UP);
            
            kpiSummary.put("averagePerformanceScore", avgPerformanceScore);
            kpiSummary.put("totalMetrics", metrics.size());
            kpiSummary.put("efficiencyScore", calculateWarehouseEfficiencyScore(warehouseId, startDate, endDate));
            
            // Count by alert level
            Map<AlertLevel, Long> alertCounts = metrics.stream()
                    .filter(m -> m.getAlertLevel() != null)
                    .collect(Collectors.groupingBy(
                            PerformanceMetric::getAlertLevel, 
                            Collectors.counting()));
            kpiSummary.put("alertCounts", alertCounts);
            
            // Count by category
            Map<MetricCategory, Long> categoryCounts = metrics.stream()
                    .collect(Collectors.groupingBy(
                            PerformanceMetric::getMetricCategory, 
                            Collectors.counting()));
            kpiSummary.put("categoryCounts", categoryCounts);
        }
        
        return kpiSummary;
    }

    @Override
    public AnalyticsReport generateReport(AnalyticsReportRequest request, String generatedBy) {
        log.info("Generating analytics report: {} for user: {}", request.getTitle(), generatedBy);
        
        AnalyticsReport report = AnalyticsReport.builder()
                .reportName(request.getTitle())
                .description(request.getDescription())
                .reportType(request.getReportType())
                .format(request.getReportFormat())
                .status(ReportStatus.GENERATING)
                .accessLevel(request.getAccessLevel())
                .periodStart(request.getStartDate())
                .periodEnd(request.getEndDate())
                .generatedBy(generatedBy)
                .warehouseId(request.getWarehouseId())
                .build();
        
        try {
            // Generate report content
            byte[] reportContent = generateReportContent(request);
            report.setContentData(reportContent);
            report.setReportStatus(ReportStatus.COMPLETED);
            report.setCompletedAt(LocalDateTime.now());
            
            log.info("Successfully generated report: {}", report.getId());
        } catch (Exception e) {
            report.setReportStatus(ReportStatus.FAILED);
            report.setErrorMessage(e.getMessage());
            log.error("Failed to generate report: {}", e.getMessage());
        }
        
        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsReport getReport(UUID reportId) {
        // Note: This would typically query a reports table
        // For now, returning a placeholder implementation
        throw new UnsupportedOperationException("Report retrieval not implemented - requires reports table");
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsReport> getReportsByType(ReportType reportType) {
        // Note: This would typically query a reports table
        throw new UnsupportedOperationException("Report retrieval not implemented - requires reports table");
    }

    @Override
    public byte[] downloadReport(UUID reportId, String accessedBy) {
        // Note: This would typically retrieve from reports table
        throw new UnsupportedOperationException("Report download not implemented - requires reports table");
    }

    @Override
    public void scheduleReport(AnalyticsReportRequest request, String cronExpression, String scheduledBy) {
        // Note: This would typically integrate with a scheduling service
        log.info("Report scheduling requested: {} with cron: {} by: {}", 
                request.getTitle(), cronExpression, scheduledBy);
        throw new UnsupportedOperationException("Report scheduling not implemented - requires scheduler integration");
    }

    // ========== Alert Management ==========

    @Override
    public void processMetricAlerts() {
        List<PerformanceMetric> recentMetrics = metricRepository
                .findByRecordedAtAfter(LocalDateTime.now().minusHours(1));
        
        for (PerformanceMetric metric : recentMetrics) {
            AlertLevel alertLevel = determineAlertLevel(convertToDTO(metric));
            if (alertLevel != AlertLevel.INFO) {
                metric.setAlertLevel(alertLevel);
                metric.setIsAlert(true);
                metricRepository.save(metric);
                log.warn("Alert detected for metric {} in warehouse {}: {}", 
                        metric.getMetricType(), metric.getWarehouseId(), alertLevel);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> getActiveAlerts() {
        return metricRepository.findByIsAlertTrueAndIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> getAlertsBySeverity(AlertLevel alertLevel) {
        return metricRepository.findByAlertLevelAndIsAlertTrue(alertLevel);
    }

    @Override
    public void acknowledgeAlert(UUID metricId, String acknowledgedBy) {
        PerformanceMetric metric = getMetric(metricId);
        metric.setIsAlert(false);
        metric.setLastUpdatedBy(acknowledgedBy);
        metric.setLastUpdatedAt(LocalDateTime.now());
        metricRepository.save(metric);
        
        log.info("Alert acknowledged for metric {} by {}", metricId, acknowledgedBy);
    }

    @Override
    public void configureAlertThresholds(UUID warehouseId, MetricType metricType, 
                                       BigDecimal minThreshold, BigDecimal maxThreshold) {
        // Note: This would typically store thresholds in a configuration table
        log.info("Alert thresholds configured for warehouse {} metric {}: {} - {}", 
                warehouseId, metricType, minThreshold, maxThreshold);
        throw new UnsupportedOperationException("Alert threshold configuration not implemented - requires configuration table");
    }

    // ========== Search and Filtering ==========

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> searchMetrics(String searchText) {
        try {
            return searchRepository.findByDescriptionContaining(searchText);
        } catch (Exception e) {
            log.warn("Elasticsearch search failed, falling back to database search: {}", e.getMessage());
            return metricRepository.findByDescriptionContainingIgnoreCase(searchText);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerformanceMetric> searchMetricsWithFilters(String searchText, 
                                                          UUID warehouseId, 
                                                          MetricType metricType, 
                                                          MetricCategory category,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate,
                                                          Pageable pageable) {
        try {
            return searchRepository.findWithFilters(searchText, warehouseId, metricType, 
                    category, startDate, endDate, pageable);
        } catch (Exception e) {
            log.warn("Elasticsearch search failed, falling back to database search: {}", e.getMessage());
            return metricRepository.findWithFilters(warehouseId, metricType, category, 
                    startDate, endDate, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> findSimilarMetrics(UUID metricId) {
        PerformanceMetric metric = getMetric(metricId);
        return metricRepository.findByWarehouseIdAndMetricTypeAndValueBetween(
                metric.getWarehouseId(),
                metric.getMetricType(),
                metric.getValue().multiply(new BigDecimal("0.9")),
                metric.getValue().multiply(new BigDecimal("1.1"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceMetric> findOutlierMetrics(UUID warehouseId, int days) {
        return detectAnomalies(warehouseId, null, days); // Detect anomalies across all metric types
    }

    // ========== Aggregation and Analysis ==========

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, BigDecimal> compareWarehousePerformance(List<UUID> warehouseIds, 
                                                           MetricType metricType, 
                                                           int days) {
        Map<UUID, BigDecimal> performance = new HashMap<>();
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        for (UUID warehouseId : warehouseIds) {
            List<PerformanceMetric> metrics = metricRepository
                    .findByWarehouseIdAndMetricTypeAndRecordedAtAfter(warehouseId, metricType, startDate);
            
            if (!metrics.isEmpty()) {
                BigDecimal avgPerformance = calculateMean(metrics);
                performance.put(warehouseId, avgPerformance);
            }
        }
        
        return performance;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getTopPerformingWarehouses(MetricType metricType, int days, int limit) {
        // Note: This would require a more complex query to aggregate across warehouses
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<PerformanceMetric> metrics = metricRepository
                .findByMetricTypeAndRecordedAtAfter(metricType, startDate);
        
        return metrics.stream()
                .collect(Collectors.groupingBy(
                        PerformanceMetric::getWarehouseId,
                        Collectors.averagingDouble(m -> m.getPerformanceScore() != null ? 
                                m.getPerformanceScore().doubleValue() : 0.0)))
                .entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<MetricCategory, Long> getMetricDistributionByCategory(UUID warehouseId) {
        List<PerformanceMetric> metrics = metricRepository.findByWarehouseId(warehouseId);
        return metrics.stream()
                .collect(Collectors.groupingBy(
                        PerformanceMetric::getMetricCategory, 
                        Collectors.counting()));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDateTime, BigDecimal> getPerformanceTrends(UUID warehouseId, 
                                                             MetricType metricType, 
                                                             MeasurementPeriod period, 
                                                             int periodCount) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDateForPeriod(endDate, period, periodCount);
        
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndMetricTypeAndRecordedAtBetween(warehouseId, metricType, startDate, endDate);
        
        return metrics.stream()
                .collect(Collectors.groupingBy(
                        m -> truncateToMeasurementPeriod(m.getRecordedAt(), period),
                        Collectors.averagingDouble(m -> m.getValue().doubleValue())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> BigDecimal.valueOf(e.getValue())
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateWarehouseEfficiencyScore(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate) {
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndRecordedAtBetween(warehouseId, startDate, endDate);
        
        if (metrics.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Calculate weighted average based on metric categories
        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        
        Map<MetricCategory, List<PerformanceMetric>> groupedMetrics = metrics.stream()
                .collect(Collectors.groupingBy(PerformanceMetric::getMetricCategory));
        
        for (Map.Entry<MetricCategory, List<PerformanceMetric>> entry : groupedMetrics.entrySet()) {
            BigDecimal categoryAverage = calculateMean(entry.getValue());
            BigDecimal weight = getCategoryWeight(entry.getKey());
            
            totalWeightedScore = totalWeightedScore.add(categoryAverage.multiply(weight));
            totalWeight = totalWeight.add(weight);
        }
        
        return totalWeight.compareTo(BigDecimal.ZERO) > 0 ? 
                totalWeightedScore.divide(totalWeight, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getCapacityUtilization(UUID warehouseId) {
        Map<String, BigDecimal> utilization = new HashMap<>();
        
        // Get storage capacity metrics
        List<PerformanceMetric> storageMetrics = metricRepository
                .findByWarehouseIdAndMetricType(warehouseId, MetricType.STORAGE_UTILIZATION);
        
        if (!storageMetrics.isEmpty()) {
            BigDecimal avgUtilization = calculateMean(storageMetrics);
            utilization.put("storageUtilization", avgUtilization);
        }
        
        // Get staff utilization metrics
        List<PerformanceMetric> staffMetrics = metricRepository
                .findByWarehouseIdAndMetricType(warehouseId, MetricType.STAFF_UTILIZATION);
        
        if (!staffMetrics.isEmpty()) {
            BigDecimal avgStaffUtilization = calculateMean(staffMetrics);
            utilization.put("staffUtilization", avgStaffUtilization);
        }
        
        // Calculate overall capacity score
        if (!utilization.isEmpty()) {
            BigDecimal overallUtilization = utilization.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(utilization.size()), RoundingMode.HALF_UP);
            utilization.put("overallUtilization", overallUtilization);
        }
        
        return utilization;
    }

    // ========== Data Export and Integration ==========

    @Override
    @Transactional(readOnly = true)
    public byte[] exportMetricsToCSV(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate) {
        List<PerformanceMetric> metrics = metricRepository
                .findByWarehouseIdAndRecordedAtBetween(warehouseId, startDate, endDate);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,Warehouse ID,Metric Type,Category,Value,Unit,Performance Score,Alert Level,Recorded At");
            
            // CSV Data
            for (PerformanceMetric metric : metrics) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        metric.getId(),
                        metric.getWarehouseId(),
                        metric.getMetricType(),
                        metric.getMetricCategory(),
                        metric.getValue(),
                        metric.getUnit(),
                        metric.getPerformanceScore(),
                        metric.getAlertLevel(),
                        metric.getRecordedAt());
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to export metrics to CSV", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportDashboardToExcel(UUID warehouseId) {
        // Note: This would require Apache POI or similar library for Excel generation
        throw new UnsupportedOperationException("Excel export not implemented - requires Apache POI");
    }

    @Override
    public void syncToExternalBI(UUID warehouseId, String systemName) {
        log.info("Syncing metrics for warehouse {} to external BI system: {}", warehouseId, systemName);
        // Note: This would require integration with external BI systems
        throw new UnsupportedOperationException("External BI sync not implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAPIMetrics() {
        Map<String, Object> apiMetrics = new HashMap<>();
        
        // Get basic counts
        long totalMetrics = metricRepository.count();
        long recentMetrics = metricRepository.countByRecordedAtAfter(LocalDateTime.now().minusHours(24));
        long activeAlerts = metricRepository.countByIsAlertTrue();
        
        apiMetrics.put("totalMetrics", totalMetrics);
        apiMetrics.put("recentMetrics", recentMetrics);
        apiMetrics.put("activeAlerts", activeAlerts);
        apiMetrics.put("lastUpdated", LocalDateTime.now());
        
        return apiMetrics;
    }

    // ========== Data Management ==========

    @Override
    public void archiveOldMetrics(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<PerformanceMetric> oldMetrics = metricRepository.findByRecordedAtBefore(cutoffDate);
        
        log.info("Archiving {} metrics older than {} days", oldMetrics.size(), daysOld);
        
        // Mark as archived instead of deleting
        for (PerformanceMetric metric : oldMetrics) {
            metric.setIsActive(false);
            metric.setArchiveDate(LocalDateTime.now());
        }
        
        metricRepository.saveAll(oldMetrics);
    }

    @Override
    public void cleanupExpiredData() {
        // Remove very old archived data (e.g., older than 2 years)
        LocalDateTime expiryDate = LocalDateTime.now().minusYears(2);
        List<PerformanceMetric> expiredMetrics = metricRepository
                .findByIsActiveFalseAndArchiveDateBefore(expiryDate);
        
        log.info("Cleaning up {} expired metrics", expiredMetrics.size());
        metricRepository.deleteAll(expiredMetrics);
        
        // Also cleanup from Elasticsearch
        try {
            for (PerformanceMetric metric : expiredMetrics) {
                searchRepository.deleteById(metric.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup some Elasticsearch data: {}", e.getMessage());
        }
    }

    @Override
    public void reindexSearchData() {
        log.info("Starting reindex of search data");
        
        try {
            // Clear existing index
            searchRepository.deleteAll();
            
            // Reindex all active metrics
            List<PerformanceMetric> activeMetrics = metricRepository.findByIsActiveTrue();
            searchRepository.saveAll(activeMetrics);
            
            log.info("Successfully reindexed {} metrics", activeMetrics.size());
        } catch (Exception e) {
            log.error("Failed to reindex search data: {}", e.getMessage());
            throw new RuntimeException("Reindex operation failed", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateDataIntegrity() {
        Map<String, Object> validation = new HashMap<>();
        
        // Check for orphaned records
        long totalMetrics = metricRepository.count();
        long metricsWithNullValues = metricRepository.countByValueIsNull();
        long metricsWithNullWarehouse = metricRepository.countByWarehouseIdIsNull();
        
        validation.put("totalMetrics", totalMetrics);
        validation.put("metricsWithNullValues", metricsWithNullValues);
        validation.put("metricsWithNullWarehouse", metricsWithNullWarehouse);
        validation.put("integrityScore", calculateIntegrityScore(totalMetrics, metricsWithNullValues, metricsWithNullWarehouse));
        validation.put("validatedAt", LocalDateTime.now());
        
        return validation;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStorageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMetrics = metricRepository.count();
        long activeMetrics = metricRepository.countByIsActiveTrue();
        long archivedMetrics = metricRepository.countByIsActiveFalse();
        long alertMetrics = metricRepository.countByIsAlertTrue();
        
        stats.put("totalMetrics", totalMetrics);
        stats.put("activeMetrics", activeMetrics);
        stats.put("archivedMetrics", archivedMetrics);
        stats.put("alertMetrics", alertMetrics);
        
        // Estimate storage size (rough calculation)
        long estimatedSizeBytes = totalMetrics * 1024; // Rough estimate of 1KB per metric
        stats.put("estimatedStorageSizeBytes", estimatedSizeBytes);
        stats.put("estimatedStorageSizeMB", estimatedSizeBytes / (1024 * 1024));
        
        return stats;
    }

    // ========== Configuration Management ==========

    @Override
    public void updateMetricConfiguration(UUID warehouseId, MetricType metricType, Map<String, String> config) {
        log.info("Updating metric configuration for warehouse {} metric {}", warehouseId, metricType);
        // Note: This would typically store in a configuration table
        throw new UnsupportedOperationException("Metric configuration not implemented - requires configuration table");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getSystemConfiguration() {
        Map<String, String> config = new HashMap<>();
        config.put("anomalyThreshold", ANOMALY_THRESHOLD.toString());
        config.put("maxForecastDays", String.valueOf(MAX_FORECAST_DAYS));
        config.put("version", "1.0.0");
        config.put("lastConfigUpdate", LocalDateTime.now().toString());
        return config;
    }

    @Override
    public void updateSystemConfiguration(Map<String, String> config, String updatedBy) {
        log.info("System configuration update requested by {}", updatedBy);
        // Note: This would typically store in a configuration table
        throw new UnsupportedOperationException("System configuration not implemented - requires configuration table");
    }

    @Override
    public void resetWarehouseMetrics(UUID warehouseId, String resetBy) {
        log.warn("Resetting all metrics for warehouse {} by {}", warehouseId, resetBy);
        
        List<PerformanceMetric> warehouseMetrics = metricRepository.findByWarehouseId(warehouseId);
        for (PerformanceMetric metric : warehouseMetrics) {
            metric.setIsActive(false);
            metric.setLastUpdatedBy(resetBy);
            metric.setLastUpdatedAt(LocalDateTime.now());
        }
        
        metricRepository.saveAll(warehouseMetrics);
        
        // Also remove from search index
        try {
            for (PerformanceMetric metric : warehouseMetrics) {
                searchRepository.deleteById(metric.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to remove some metrics from search index: {}", e.getMessage());
        }
    }

    // ========== Private Helper Methods ==========

    private MetricDTO convertToDTO(PerformanceMetric metric) {
        MetricDTO dto = new MetricDTO();
        dto.setWarehouseId(metric.getWarehouseId());
        dto.setMetricType(metric.getMetricType());
        dto.setMetricCategory(metric.getMetricCategory());
        dto.setValue(metric.getValue());
        dto.setUnit(metric.getUnit());
        dto.setMeasurementPeriod(metric.getMeasurementPeriod());
        dto.setDescription(metric.getDescription());
        return dto;
    }

    private BigDecimal calculatePerformanceScoreForMetric(MetricDTO metricDTO) {
        // Simplified performance score calculation
        // In a real implementation, this would use more sophisticated algorithms
        
        BigDecimal baseScore = metricDTO.getValue();
        
        // Apply category-specific weights and normalization
        switch (metricDTO.getMetricCategory()) {
            case OPERATIONS:
                return baseScore.multiply(PERFORMANCE_WEIGHT_EFFICIENCY).min(BigDecimal.valueOf(100));
            case QUALITY:
                return baseScore.multiply(PERFORMANCE_WEIGHT_ACCURACY).min(BigDecimal.valueOf(100));
            case RESOURCE_MANAGEMENT:
                return baseScore.multiply(PERFORMANCE_WEIGHT_SPEED).min(BigDecimal.valueOf(100));
            case FINANCIAL:
                // For cost metrics, lower is better, so invert the score
                return BigDecimal.valueOf(100).subtract(baseScore.multiply(PERFORMANCE_WEIGHT_COST)).max(BigDecimal.ZERO);
            default:
                return baseScore.min(BigDecimal.valueOf(100));
        }
    }

    private AlertLevel determineAlertLevel(MetricDTO metricDTO) {
        BigDecimal value = metricDTO.getValue();
        
        // Simplified alert level determination
        // In a real implementation, this would use configurable thresholds
        
        if (value.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return AlertLevel.INFO;
        } else if (value.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return AlertLevel.LOW;
        } else if (value.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return AlertLevel.MEDIUM;
        } else if (value.compareTo(BigDecimal.valueOf(30)) >= 0) {
            return AlertLevel.HIGH;
        } else {
            return AlertLevel.CRITICAL;
        }
    }

    private List<PerformanceMetric> getActiveAlertsForWarehouse(UUID warehouseId) {
        return metricRepository.findByWarehouseIdAndIsAlertTrueAndIsActiveTrue(warehouseId);
    }

    private double calculateLinearRegressionSlope(List<PerformanceMetric> metrics) {
        if (metrics.size() < 2) {
            return 0.0;
        }
        
        // Simple linear regression calculation
        int n = metrics.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        
        for (int i = 0; i < n; i++) {
            double x = i; // Time index
            double y = metrics.get(i).getValue().doubleValue();
            
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        return slope;
    }

    private BigDecimal calculateMean(List<PerformanceMetric> metrics) {
        if (metrics.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = metrics.stream()
                .map(PerformanceMetric::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return sum.divide(BigDecimal.valueOf(metrics.size()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateStandardDeviation(List<PerformanceMetric> metrics, BigDecimal mean) {
        if (metrics.size() <= 1) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sumSquaredDifferences = metrics.stream()
                .map(PerformanceMetric::getValue)
                .map(value -> value.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal variance = sumSquaredDifferences.divide(BigDecimal.valueOf(metrics.size() - 1), RoundingMode.HALF_UP);
        
        // Simple square root approximation
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }

    private BigDecimal findMinValue(List<PerformanceMetric> metrics) {
        return metrics.stream()
                .map(PerformanceMetric::getValue)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal findMaxValue(List<PerformanceMetric> metrics) {
        return metrics.stream()
                .map(PerformanceMetric::getValue)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMedian(List<PerformanceMetric> metrics) {
        List<BigDecimal> values = metrics.stream()
                .map(PerformanceMetric::getValue)
                .sorted()
                .collect(Collectors.toList());
        
        int size = values.size();
        if (size == 0) {
            return BigDecimal.ZERO;
        } else if (size % 2 == 1) {
            return values.get(size / 2);
        } else {
            return values.get(size / 2 - 1).add(values.get(size / 2))
                    .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
    }

    private BigDecimal calculatePearsonCorrelation(List<PerformanceMetric> metrics1, List<PerformanceMetric> metrics2) {
        // Simplified correlation calculation
        // In a real implementation, this would handle time alignment and use more sophisticated algorithms
        
        if (metrics1.size() != metrics2.size() || metrics1.size() < 2) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal mean1 = calculateMean(metrics1);
        BigDecimal mean2 = calculateMean(metrics2);
        
        BigDecimal numerator = BigDecimal.ZERO;
        BigDecimal sum1Sq = BigDecimal.ZERO;
        BigDecimal sum2Sq = BigDecimal.ZERO;
        
        for (int i = 0; i < metrics1.size(); i++) {
            BigDecimal diff1 = metrics1.get(i).getValue().subtract(mean1);
            BigDecimal diff2 = metrics2.get(i).getValue().subtract(mean2);
            
            numerator = numerator.add(diff1.multiply(diff2));
            sum1Sq = sum1Sq.add(diff1.pow(2));
            sum2Sq = sum2Sq.add(diff2.pow(2));
        }
        
        BigDecimal denominator = BigDecimal.valueOf(Math.sqrt(sum1Sq.doubleValue() * sum2Sq.doubleValue()));
        
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return numerator.divide(denominator, 4, RoundingMode.HALF_UP);
    }

    private TrendDirection determineTrendDirection(double slope) {
        if (slope > 0.05) {
            return TrendDirection.STRONG_UPWARD;
        } else if (slope > 0.01) {
            return TrendDirection.UPWARD;
        } else if (slope < -0.05) {
            return TrendDirection.STRONG_DOWNWARD;
        } else if (slope < -0.01) {
            return TrendDirection.DOWNWARD;
        } else {
            return TrendDirection.STABLE;
        }
    }

    private BigDecimal calculateDataQualityScore(List<PerformanceMetric> metrics) {
        if (metrics.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        long validMetrics = metrics.stream()
                .filter(m -> m.getValue() != null)
                .filter(m -> m.getWarehouseId() != null)
                .filter(m -> m.getMetricType() != null)
                .count();
        
        return BigDecimal.valueOf(validMetrics)
                .divide(BigDecimal.valueOf(metrics.size()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateCompletenessScore(List<PerformanceMetric> metrics, 
                                                LocalDateTime startDate, LocalDateTime endDate) {
        // Calculate expected data points based on time range
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long expectedDataPoints = Math.max(1, daysBetween); // At least one data point per day
        
        long actualDataPoints = metrics.size();
        
        if (expectedDataPoints == 0) {
            return BigDecimal.valueOf(100);
        }
        
        BigDecimal completeness = BigDecimal.valueOf(actualDataPoints)
                .divide(BigDecimal.valueOf(expectedDataPoints), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return completeness.min(BigDecimal.valueOf(100)); // Cap at 100%
    }

    private List<String> generateInsights(List<PerformanceMetric> metrics, TrendAnalysisResult result) {
        List<String> insights = new ArrayList<>();
        
        if (result.getOverallTrend() == TrendDirection.STRONG_UPWARD) {
            insights.add("Strong positive trend detected - performance is improving significantly");
        } else if (result.getOverallTrend() == TrendDirection.STRONG_DOWNWARD) {
            insights.add("Strong negative trend detected - immediate attention required");
        }
        
        if (result.getStandardDeviation() != null && 
            result.getAverageValue() != null && 
            result.getAverageValue().compareTo(BigDecimal.ZERO) > 0) {
            
            BigDecimal coefficientOfVariation = result.getStandardDeviation()
                    .divide(result.getAverageValue(), 4, RoundingMode.HALF_UP);
            
            if (coefficientOfVariation.compareTo(new BigDecimal("0.3")) > 0) {
                insights.add("High variability detected - consider investigating causes of inconsistency");
            }
        }
        
        if (metrics.size() < 10) {
            insights.add("Limited data available - consider increasing measurement frequency for better insights");
        }
        
        return insights;
    }

    private List<String> generateRecommendations(TrendAnalysisResult result) {
        List<String> recommendations = new ArrayList<>();
        
        if (result.getOverallTrend() == TrendDirection.STRONG_DOWNWARD) {
            recommendations.add("Implement immediate corrective actions to address declining performance");
            recommendations.add("Review operational procedures and identify potential bottlenecks");
        }
        
        if (result.getDataQualityScore() != null && 
            result.getDataQualityScore().compareTo(new BigDecimal("80")) < 0) {
            recommendations.add("Improve data collection processes to enhance data quality");
        }
        
        if (result.getCompletenessScore() != null && 
            result.getCompletenessScore().compareTo(new BigDecimal("70")) < 0) {
            recommendations.add("Increase measurement frequency to improve data completeness");
        }
        
        return recommendations;
    }

    private String calculatePerformanceGrade(TrendAnalysisResult result) {
        if (result.getAverageValue() == null) {
            return "F";
        }
        
        BigDecimal avgValue = result.getAverageValue();
        
        if (avgValue.compareTo(new BigDecimal("90")) >= 0) {
            return "A";
        } else if (avgValue.compareTo(new BigDecimal("80")) >= 0) {
            return "B";
        } else if (avgValue.compareTo(new BigDecimal("70")) >= 0) {
            return "C";
        } else if (avgValue.compareTo(new BigDecimal("60")) >= 0) {
            return "D";
        } else {
            return "F";
        }
    }

    private List<BigDecimal> generateSimpleForecast(List<PerformanceMetric> metrics, int forecastDays) {
        if (metrics.size() < 3) {
            return new ArrayList<>();
        }
        
        // Simple linear extrapolation
        double slope = calculateLinearRegressionSlope(metrics);
        BigDecimal lastValue = metrics.get(metrics.size() - 1).getValue();
        
        List<BigDecimal> forecast = new ArrayList<>();
        for (int i = 1; i <= forecastDays; i++) {
            BigDecimal forecastValue = lastValue.add(BigDecimal.valueOf(slope * i));
            forecast.add(forecastValue.max(BigDecimal.ZERO)); // Ensure non-negative
        }
        
        return forecast;
    }

    private byte[] generateReportContent(AnalyticsReportRequest request) {
        // Simplified report generation
        StringBuilder content = new StringBuilder();
        content.append("Analytics Report: ").append(request.getTitle()).append("\n");
        content.append("Generated: ").append(LocalDateTime.now()).append("\n");
        content.append("Period: ").append(request.getStartDate()).append(" to ").append(request.getEndDate()).append("\n\n");
        
        if (request.getWarehouseId() != null) {
            List<PerformanceMetric> metrics = metricRepository
                    .findByWarehouseIdAndRecordedAtBetween(request.getWarehouseId(), 
                            request.getStartDate(), request.getEndDate());
            
            content.append("Total Metrics: ").append(metrics.size()).append("\n");
            
            if (!metrics.isEmpty()) {
                BigDecimal avgPerformance = calculateMean(metrics);
                content.append("Average Performance: ").append(avgPerformance).append("\n");
            }
        }
        
        return content.toString().getBytes();
    }

    private BigDecimal getCategoryWeight(MetricCategory category) {
        switch (category) {
            case OPERATIONS:
                return PERFORMANCE_WEIGHT_EFFICIENCY;
            case QUALITY:
                return PERFORMANCE_WEIGHT_ACCURACY;
            case RESOURCE_MANAGEMENT:
                return PERFORMANCE_WEIGHT_SPEED;
            case FINANCIAL:
                return PERFORMANCE_WEIGHT_COST;
            default:
                return new BigDecimal("0.1");
        }
    }

    private LocalDateTime calculateStartDateForPeriod(LocalDateTime endDate, MeasurementPeriod period, int periodCount) {
        switch (period) {
            case HOURLY:
                return endDate.minusHours(periodCount);
            case DAILY:
                return endDate.minusDays(periodCount);
            case WEEKLY:
                return endDate.minusWeeks(periodCount);
            case MONTHLY:
                return endDate.minusMonths(periodCount);
            case QUARTERLY:
                return endDate.minusMonths(periodCount * 3);
            case YEARLY:
                return endDate.minusYears(periodCount);
            default:
                return endDate.minusDays(periodCount);
        }
    }

    private LocalDateTime truncateToMeasurementPeriod(LocalDateTime dateTime, MeasurementPeriod period) {
        switch (period) {
            case HOURLY:
                return dateTime.truncatedTo(ChronoUnit.HOURS);
            case DAILY:
                return dateTime.truncatedTo(ChronoUnit.DAYS);
            case WEEKLY:
                // Truncate to start of week (Monday)
                return dateTime.truncatedTo(ChronoUnit.DAYS)
                        .minusDays(dateTime.getDayOfWeek().getValue() - 1);
            case MONTHLY:
                return dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
            case QUARTERLY:
                int quarter = ((dateTime.getMonthValue() - 1) / 3) * 3 + 1;
                return dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).withMonth(quarter);
            case YEARLY:
                return dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfYear(1);
            default:
                return dateTime.truncatedTo(ChronoUnit.DAYS);
        }
    }

    private BigDecimal calculateIntegrityScore(long totalMetrics, long nullValues, long nullWarehouses) {
        if (totalMetrics == 0) {
            return BigDecimal.valueOf(100);
        }
        
        long invalidMetrics = nullValues + nullWarehouses;
        long validMetrics = totalMetrics - invalidMetrics;
        
        return BigDecimal.valueOf(validMetrics)
                .divide(BigDecimal.valueOf(totalMetrics), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}