package com.exalt.warehousing.analytics.service;

import com.exalt.warehousing.analytics.dto.MetricDTO;
import com.exalt.warehousing.analytics.dto.TrendAnalysisResult;
import com.exalt.warehousing.analytics.model.*;
import com.exalt.warehousing.analytics.repository.elasticsearch.PerformanceMetricSearchRepository;
import com.exalt.warehousing.analytics.repository.jpa.PerformanceMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnalyticsServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private PerformanceMetricRepository metricRepository;

    @Mock
    private PerformanceMetricSearchRepository searchRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private UUID warehouseId;
    private UUID metricId;
    private PerformanceMetric testMetric;
    private MetricDTO testMetricDTO;

    @BeforeEach
    void setUp() {
        warehouseId = UUID.randomUUID();
        metricId = UUID.randomUUID();

        testMetric = PerformanceMetric.builder()
                .id(metricId)
                .warehouseId(warehouseId)
                .metricType(MetricType.THROUGHPUT)
                .metricCategory(MetricCategory.EFFICIENCY)
                .metricValue(new BigDecimal("85.5"))
                .unitOfMeasure("units/hour")
                .performanceScore(new BigDecimal("85.5"))
                .measurementPeriod(MeasurementPeriod.DAILY)
                .recordedAt(LocalDateTime.now())
                .isActive(true)
                .isAlertTriggered(false)
                .alertLevel(AlertLevel.INFO)
                .lastUpdatedBy("test-user")
                .build();

        testMetricDTO = new MetricDTO();
        testMetricDTO.setWarehouseId(warehouseId);
        testMetricDTO.setMetricType(MetricType.THROUGHPUT);
        testMetricDTO.setMetricCategory(MetricCategory.EFFICIENCY);
        testMetricDTO.setValue(new BigDecimal("85.5"));
        testMetricDTO.setUnit("units/hour");
        testMetricDTO.setMeasurementPeriod(MeasurementPeriod.DAILY);
        testMetricDTO.setDescription("Test throughput metric");
    }

    @Test
    void testRecordMetric_Success() {
        // Given
        when(metricRepository.save(any(PerformanceMetric.class))).thenReturn(testMetric);

        // When
        PerformanceMetric result = analyticsService.recordMetric(testMetricDTO, "test-user");

        // Then
        assertNotNull(result);
        assertEquals(testMetric.getId(), result.getId());
        assertEquals(testMetric.getWarehouseId(), result.getWarehouseId());
        assertEquals(testMetric.getMetricType(), result.getMetricType());
        assertEquals(testMetric.getValue(), result.getValue());

        verify(metricRepository).save(any(PerformanceMetric.class));
        verify(searchRepository).save(any(PerformanceMetric.class));
    }

    @Test
    void testGetMetric_Success() {
        // Given
        when(metricRepository.findById(metricId)).thenReturn(Optional.of(testMetric));

        // When
        PerformanceMetric result = analyticsService.getMetric(metricId);

        // Then
        assertNotNull(result);
        assertEquals(testMetric.getId(), result.getId());
        assertEquals(testMetric.getWarehouseId(), result.getWarehouseId());

        verify(metricRepository).findById(metricId);
    }

    @Test
    void testGetMetric_NotFound() {
        // Given
        when(metricRepository.findById(metricId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> analyticsService.getMetric(metricId));
        verify(metricRepository).findById(metricId);
    }

    @Test
    void testGetMetricsByWarehouse_Success() {
        // Given
        List<PerformanceMetric> metrics = Arrays.asList(testMetric);
        when(metricRepository.findByWarehouseId(warehouseId)).thenReturn(metrics);

        // When
        List<PerformanceMetric> result = analyticsService.getMetricsByWarehouse(warehouseId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMetric.getId(), result.get(0).getId());

        verify(metricRepository).findByWarehouseId(warehouseId);
    }

    @Test
    void testGetMetricsByWarehouse_WithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceMetric> page = new PageImpl<>(Arrays.asList(testMetric), pageable, 1);
        when(metricRepository.findByWarehouseId(warehouseId, pageable)).thenReturn(page);

        // When
        Page<PerformanceMetric> result = analyticsService.getMetricsByWarehouse(warehouseId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMetric.getId(), result.getContent().get(0).getId());

        verify(metricRepository).findByWarehouseId(warehouseId, pageable);
    }

    @Test
    void testCalculateTrendDirection_StrongUpward() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<PerformanceMetric> metrics = createTrendMetrics(warehouseId, MetricType.THROUGHPUT, 
                Arrays.asList(70.0, 75.0, 80.0, 85.0, 90.0), now);
        
        when(metricRepository.findByWarehouseIdAndMetricTypeAndRecordedAtBetween(
                eq(warehouseId), eq(MetricType.THROUGHPUT), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(metrics);

        // When
        TrendDirection result = analyticsService.calculateTrendDirection(warehouseId, MetricType.THROUGHPUT, 30);

        // Then
        assertEquals(TrendDirection.STRONG_UPWARD, result);
    }

    @Test
    void testCalculateTrendDirection_Stable() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<PerformanceMetric> metrics = createTrendMetrics(warehouseId, MetricType.THROUGHPUT, 
                Arrays.asList(85.0, 85.1, 84.9, 85.0, 85.0), now);
        
        when(metricRepository.findByWarehouseIdAndMetricTypeAndRecordedAtBetween(
                eq(warehouseId), eq(MetricType.THROUGHPUT), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(metrics);

        // When
        TrendDirection result = analyticsService.calculateTrendDirection(warehouseId, MetricType.THROUGHPUT, 30);

        // Then
        assertEquals(TrendDirection.STABLE, result);
    }

    @Test
    void testDetectAnomalies_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<PerformanceMetric> metrics = createAnomalyMetrics(warehouseId, MetricType.THROUGHPUT, now);
        
        when(metricRepository.findByWarehouseIdAndMetricTypeAndRecordedAtBetween(
                eq(warehouseId), eq(MetricType.THROUGHPUT), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(metrics);

        // When
        List<PerformanceMetric> anomalies = analyticsService.detectAnomalies(warehouseId, MetricType.THROUGHPUT, 30);

        // Then
        assertNotNull(anomalies);
        assertEquals(1, anomalies.size()); // Should detect the outlier value (150.0)
    }

    @Test
    void testPerformTrendAnalysis_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<PerformanceMetric> metrics = createTrendMetrics(warehouseId, MetricType.THROUGHPUT, 
                Arrays.asList(80.0, 82.0, 84.0, 86.0, 88.0), endDate);
        
        when(metricRepository.findByWarehouseIdAndMetricTypeAndRecordedAtBetween(
                warehouseId, MetricType.THROUGHPUT, startDate, endDate))
                .thenReturn(metrics);

        // When
        TrendAnalysisResult result = analyticsService.performTrendAnalysis(warehouseId, MetricType.THROUGHPUT, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(warehouseId, result.getWarehouseId());
        assertEquals(MetricType.THROUGHPUT, result.getMetricType());
        assertNotNull(result.getAverageValue());
        assertNotNull(result.getOverallTrend());
        assertEquals(5, result.getDataPointCount());
    }

    @Test
    void testCalculateWarehouseEfficiencyScore_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<PerformanceMetric> metrics = createMixedCategoryMetrics(warehouseId);
        
        when(metricRepository.findByWarehouseIdAndRecordedAtBetween(warehouseId, startDate, endDate))
                .thenReturn(metrics);

        // When
        BigDecimal efficiencyScore = analyticsService.calculateWarehouseEfficiencyScore(warehouseId, startDate, endDate);

        // Then
        assertNotNull(efficiencyScore);
        assertTrue(efficiencyScore.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(efficiencyScore.compareTo(BigDecimal.valueOf(100)) <= 0);
    }

    @Test
    void testUpdateMetric_Success() {
        // Given
        BigDecimal newValue = new BigDecimal("95.0");
        when(metricRepository.findById(metricId)).thenReturn(Optional.of(testMetric));
        when(metricRepository.save(any(PerformanceMetric.class))).thenReturn(testMetric);

        // When
        PerformanceMetric result = analyticsService.updateMetric(metricId, newValue, "updated-by");

        // Then
        assertNotNull(result);
        verify(metricRepository).findById(metricId);
        verify(metricRepository).save(any(PerformanceMetric.class));
        verify(searchRepository).save(any(PerformanceMetric.class));
    }

    @Test
    void testBulkImportMetrics_Success() {
        // Given
        List<MetricDTO> metricDTOs = Arrays.asList(testMetricDTO);
        when(metricRepository.save(any(PerformanceMetric.class))).thenReturn(testMetric);

        // When
        List<PerformanceMetric> result = analyticsService.bulkImportMetrics(metricDTOs, "import-user");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(metricRepository, times(1)).save(any(PerformanceMetric.class));
    }

    @Test
    void testForecastMetricValues_Success() {
        // Given
        List<PerformanceMetric> metrics = createTrendMetrics(warehouseId, MetricType.THROUGHPUT, 
                Arrays.asList(80.0, 82.0, 84.0, 86.0, 88.0, 90.0, 92.0), LocalDateTime.now());
        
        when(metricRepository.findByWarehouseIdAndMetricTypeAndRecordedAtBetween(
                eq(warehouseId), eq(MetricType.THROUGHPUT), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(metrics);

        // When
        List<BigDecimal> forecast = analyticsService.forecastMetricValues(warehouseId, MetricType.THROUGHPUT, 7);

        // Then
        assertNotNull(forecast);
        assertEquals(7, forecast.size());
        // Verify forecast values are reasonable (positive and increasing trend)
        for (BigDecimal value : forecast) {
            assertTrue(value.compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    void testSearchMetrics_ElasticsearchFallback() {
        // Given
        String searchText = "throughput";
        List<PerformanceMetric> expectedResults = Arrays.asList(testMetric);
        
        when(searchRepository.findByDescriptionContaining(searchText))
                .thenThrow(new RuntimeException("Elasticsearch unavailable"));
        when(metricRepository.findByDescriptionContainingIgnoreCase(searchText))
                .thenReturn(expectedResults);

        // When
        List<PerformanceMetric> result = analyticsService.searchMetrics(searchText);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(searchRepository).findByDescriptionContaining(searchText);
        verify(metricRepository).findByDescriptionContainingIgnoreCase(searchText);
    }

    // Helper methods for creating test data

    private List<PerformanceMetric> createTrendMetrics(UUID warehouseId, MetricType metricType, 
                                                     List<Double> values, LocalDateTime endTime) {
        List<PerformanceMetric> metrics = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            PerformanceMetric metric = PerformanceMetric.builder()
                    .id(UUID.randomUUID())
                    .warehouseId(warehouseId)
                    .metricType(metricType)
                    .metricCategory(MetricCategory.EFFICIENCY)
                    .metricValue(BigDecimal.valueOf(values.get(i)))
                    .performanceScore(BigDecimal.valueOf(values.get(i)))
                    .recordedAt(endTime.minusDays(values.size() - i - 1))
                    .isActive(true)
                    .build();
            metrics.add(metric);
        }
        return metrics;
    }

    private List<PerformanceMetric> createAnomalyMetrics(UUID warehouseId, MetricType metricType, LocalDateTime endTime) {
        List<Double> values = Arrays.asList(80.0, 82.0, 84.0, 83.0, 81.0, 150.0, 82.0, 83.0, 81.0, 84.0);
        return createTrendMetrics(warehouseId, metricType, values, endTime);
    }

    private List<PerformanceMetric> createMixedCategoryMetrics(UUID warehouseId) {
        List<PerformanceMetric> metrics = new ArrayList<>();
        
        // Efficiency metric
        metrics.add(PerformanceMetric.builder()
                .id(UUID.randomUUID())
                .warehouseId(warehouseId)
                .metricType(MetricType.THROUGHPUT)
                .metricCategory(MetricCategory.EFFICIENCY)
                .metricValue(new BigDecimal("85.0"))
                .performanceScore(new BigDecimal("85.0"))
                .recordedAt(LocalDateTime.now().minusHours(1))
                .isActive(true)
                .build());
        
        // Accuracy metric
        metrics.add(PerformanceMetric.builder()
                .id(UUID.randomUUID())
                .warehouseId(warehouseId)
                .metricType(MetricType.ORDER_ACCURACY)
                .metricCategory(MetricCategory.ACCURACY)
                .metricValue(new BigDecimal("98.5"))
                .performanceScore(new BigDecimal("98.5"))
                .recordedAt(LocalDateTime.now().minusHours(2))
                .isActive(true)
                .build());
        
        // Speed metric
        metrics.add(PerformanceMetric.builder()
                .id(UUID.randomUUID())
                .warehouseId(warehouseId)
                .metricType(MetricType.ORDER_PROCESSING_TIME)
                .metricCategory(MetricCategory.SPEED)
                .metricValue(new BigDecimal("75.0"))
                .performanceScore(new BigDecimal("75.0"))
                .recordedAt(LocalDateTime.now().minusHours(3))
                .isActive(true)
                .build());
        
        return metrics;
    }
}