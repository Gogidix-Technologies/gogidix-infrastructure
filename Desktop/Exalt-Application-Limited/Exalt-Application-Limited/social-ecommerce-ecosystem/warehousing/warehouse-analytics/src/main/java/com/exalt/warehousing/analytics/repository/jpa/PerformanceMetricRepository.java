package com.exalt.warehousing.analytics.repository.jpa;

import com.exalt.warehousing.analytics.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for Performance Metrics
 */
@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, UUID> {

    /**
     * Find metrics by warehouse ID
     */
    List<PerformanceMetric> findByWarehouseId(UUID warehouseId);

    /**
     * Find metrics by warehouse ID with pagination
     */
    Page<PerformanceMetric> findByWarehouseId(UUID warehouseId, Pageable pageable);

    /**
     * Find metrics by type
     */
    List<PerformanceMetric> findByMetricType(MetricType metricType);

    /**
     * Find metrics by category
     */
    List<PerformanceMetric> findByMetricCategory(MetricCategory metricCategory);

    /**
     * Find metrics by warehouse and type
     */
    List<PerformanceMetric> findByWarehouseIdAndMetricType(UUID warehouseId, MetricType metricType);

    /**
     * Find metrics by warehouse and category
     */
    List<PerformanceMetric> findByWarehouseIdAndMetricCategory(UUID warehouseId, MetricCategory metricCategory);

    /**
     * Find metrics by time period
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.periodStart >= :startDate AND m.periodEnd <= :endDate")
    List<PerformanceMetric> findByTimePeriod(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find metrics by warehouse and time period
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.warehouseId = :warehouseId AND " +
           "m.periodStart >= :startDate AND m.periodEnd <= :endDate")
    List<PerformanceMetric> findByWarehouseAndTimePeriod(@Param("warehouseId") UUID warehouseId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Find metrics that triggered alerts
     */
    List<PerformanceMetric> findByIsAlertTriggeredTrue();

    /**
     * Find metrics by alert level
     */
    List<PerformanceMetric> findByAlertLevel(AlertLevel alertLevel);

    /**
     * Find metrics with critical alerts
     */
    List<PerformanceMetric> findByAlertLevelAndIsAlertTrue(AlertLevel alertLevel);

    /**
     * Find metrics by recorded date range
     */
    List<PerformanceMetric> findByRecordedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find metrics by warehouse and recorded date range
     */
    List<PerformanceMetric> findByWarehouseIdAndRecordedAtBetween(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find metrics by type and recorded date range
     */
    List<PerformanceMetric> findByMetricTypeAndRecordedAtBetween(MetricType metricType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find metrics by warehouse, type and recorded date range
     */
    List<PerformanceMetric> findByWarehouseIdAndMetricTypeAndRecordedAtBetween(UUID warehouseId, MetricType metricType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent metrics after a specific date
     */
    List<PerformanceMetric> findByRecordedAtAfter(LocalDateTime date);

    /**
     * Find recent metrics for warehouse after a specific date
     */
    List<PerformanceMetric> findByWarehouseIdAndRecordedAtAfter(UUID warehouseId, LocalDateTime date);

    /**
     * Find metrics by type after a specific date
     */
    List<PerformanceMetric> findByMetricTypeAndRecordedAtAfter(MetricType metricType, LocalDateTime date);

    /**
     * Find metrics by warehouse, type after a specific date
     */
    List<PerformanceMetric> findByWarehouseIdAndMetricTypeAndRecordedAtAfter(UUID warehouseId, MetricType metricType, LocalDateTime date);

    /**
     * Find metrics by warehouse, category and recorded date range
     */
    List<PerformanceMetric> findByWarehouseIdAndMetricCategoryAndRecordedAtAfter(UUID warehouseId, MetricCategory category, LocalDateTime date);

    /**
     * Find active alerts
     */
    List<PerformanceMetric> findByIsAlertTrueAndIsActiveTrue();

    /**
     * Find active alerts for warehouse
     */
    List<PerformanceMetric> findByWarehouseIdAndIsAlertTrueAndIsActiveTrue(UUID warehouseId);

    /**
     * Find metrics by value range
     */
    List<PerformanceMetric> findByWarehouseIdAndMetricTypeAndValueBetween(UUID warehouseId, MetricType metricType, BigDecimal minValue, BigDecimal maxValue);

    /**
     * Find old metrics before a date
     */
    List<PerformanceMetric> findByRecordedAtBefore(LocalDateTime date);

    /**
     * Find archived metrics before a date
     */
    List<PerformanceMetric> findByIsActiveFalseAndArchiveDateBefore(LocalDateTime date);

    /**
     * Find active metrics
     */
    List<PerformanceMetric> findByIsActiveTrue();

    /**
     * Find metrics with description containing text
     */
    List<PerformanceMetric> findByDescriptionContainingIgnoreCase(String searchText);

    /**
     * Complex search with filters
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE " +
           "(:warehouseId IS NULL OR m.warehouseId = :warehouseId) AND " +
           "(:metricType IS NULL OR m.metricType = :metricType) AND " +
           "(:category IS NULL OR m.metricCategory = :category) AND " +
           "(:startDate IS NULL OR m.recordedAt >= :startDate) AND " +
           "(:endDate IS NULL OR m.recordedAt <= :endDate)")
    Page<PerformanceMetric> findWithFilters(@Param("warehouseId") UUID warehouseId,
                                          @Param("metricType") MetricType metricType,
                                          @Param("category") MetricCategory category,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    /**
     * Count methods for statistics
     */
    long countByRecordedAtAfter(LocalDateTime date);
    long countByIsAlertTrue();
    long countByIsActiveTrue();
    long countByIsActiveFalse();
    long countByValueIsNull();
    long countByWarehouseIdIsNull();
    @Query("SELECT m FROM PerformanceMetric m WHERE m.alertLevel = 'CRITICAL' AND m.isAlertTriggered = true")
    List<PerformanceMetric> findCriticalAlerts();

    /**
     * Find metrics by trend direction
     */
    List<PerformanceMetric> findByTrendDirection(TrendDirection trendDirection);

    /**
     * Find declining metrics
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.trendDirection = 'DECLINING'")
    List<PerformanceMetric> findDecliningMetrics();

    /**
     * Find improving metrics
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.trendDirection = 'IMPROVING'")
    List<PerformanceMetric> findImprovingMetrics();

    /**
     * Find metrics by measurement period
     */
    List<PerformanceMetric> findByMeasurementPeriod(MeasurementPeriod measurementPeriod);

    /**
     * Find metrics with performance score below threshold
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.performanceScore < :threshold")
    List<PerformanceMetric> findByPerformanceScoreBelow(@Param("threshold") BigDecimal threshold);

    /**
     * Find metrics with performance score above threshold
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.performanceScore > :threshold")
    List<PerformanceMetric> findByPerformanceScoreAbove(@Param("threshold") BigDecimal threshold);

    /**
     * Find latest metrics by warehouse and type
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE m.warehouseId = :warehouseId AND " +
           "m.metricType = :metricType ORDER BY m.periodEnd DESC")
    List<PerformanceMetric> findLatestByWarehouseAndType(@Param("warehouseId") UUID warehouseId,
                                                        @Param("metricType") MetricType metricType,
                                                        Pageable pageable);

    /**
     * Find metrics by data source
     */
    List<PerformanceMetric> findByDataSource(String dataSource);

    /**
     * Count metrics by warehouse
     */
    long countByWarehouseId(UUID warehouseId);

    /**
     * Count metrics by type
     */
    long countByMetricType(MetricType metricType);

    /**
     * Count metrics by category
     */
    long countByMetricCategory(MetricCategory metricCategory);

    /**
     * Get average performance score by warehouse
     */
    @Query("SELECT AVG(m.performanceScore) FROM PerformanceMetric m WHERE m.warehouseId = :warehouseId")
    Optional<BigDecimal> getAveragePerformanceScoreByWarehouse(@Param("warehouseId") UUID warehouseId);

    /**
     * Get metrics summary by category
     */
    @Query("SELECT m.metricCategory, COUNT(m), AVG(m.performanceScore), MIN(m.performanceScore), MAX(m.performanceScore) " +
           "FROM PerformanceMetric m WHERE m.warehouseId = :warehouseId GROUP BY m.metricCategory")
    List<Object[]> getMetricsSummaryByCategory(@Param("warehouseId") UUID warehouseId);

    /**
     * Find metrics with tags
     */
    @Query("SELECT DISTINCT m FROM PerformanceMetric m JOIN m.tags t WHERE KEY(t) = :tagKey AND VALUE(t) = :tagValue")
    List<PerformanceMetric> findByTag(@Param("tagKey") String tagKey, @Param("tagValue") String tagValue);

    /**
     * Find metrics created today
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE DATE(m.createdAt) = CURRENT_DATE")
    List<PerformanceMetric> findMetricsCreatedToday();

    /**
     * Find top performing warehouses
     */
    @Query("SELECT m.warehouseId, AVG(m.performanceScore) as avgScore FROM PerformanceMetric m " +
           "WHERE m.periodStart >= :startDate GROUP BY m.warehouseId ORDER BY avgScore DESC")
    List<Object[]> findTopPerformingWarehouses(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    /**
     * Find warehouses with most alerts
     */
    @Query("SELECT m.warehouseId, COUNT(m) as alertCount FROM PerformanceMetric m " +
           "WHERE m.isAlertTriggered = true AND m.periodStart >= :startDate " +
           "GROUP BY m.warehouseId ORDER BY alertCount DESC")
    List<Object[]> findWarehousesWithMostAlerts(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    /**
     * Get metric trends over time
     */
    @Query("SELECT m.periodStart, AVG(m.metricValue) FROM PerformanceMetric m " +
           "WHERE m.warehouseId = :warehouseId AND m.metricType = :metricType " +
           "AND m.periodStart >= :startDate GROUP BY m.periodStart ORDER BY m.periodStart")
    List<Object[]> getMetricTrends(@Param("warehouseId") UUID warehouseId,
                                  @Param("metricType") MetricType metricType,
                                  @Param("startDate") LocalDateTime startDate);

    /**
     * Delete old metrics
     */
    @Query("DELETE FROM PerformanceMetric m WHERE m.createdAt < :cutoffDate")
    void deleteOldMetrics(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find metrics requiring attention (alerts or declining)
     */
    @Query("SELECT m FROM PerformanceMetric m WHERE " +
           "(m.isAlertTriggered = true AND m.alertLevel IN ('HIGH', 'CRITICAL')) OR " +
           "m.trendDirection = 'DECLINING'")
    List<PerformanceMetric> findMetricsRequiringAttention();
}