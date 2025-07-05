package com.exalt.warehousing.analytics.dto;

import com.exalt.warehousing.analytics.model.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Performance Metric creation and updates
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO {

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Metric type is required")
    private MetricType metricType;

    @NotNull(message = "Metric category is required")
    private MetricCategory metricCategory;

    @NotBlank(message = "Metric name is required")
    @Size(max = 200, message = "Metric name must not exceed 200 characters")
    private String metricName;

    @NotNull(message = "Metric value is required")
    @DecimalMin(value = "0.0", message = "Metric value must be non-negative")
    private BigDecimal metricValue;

    @DecimalMin(value = "0.0", message = "Target value must be non-negative")
    private BigDecimal targetValue;

    @DecimalMin(value = "0.0", message = "Minimum threshold must be non-negative")
    private BigDecimal thresholdMin;

    @DecimalMin(value = "0.0", message = "Maximum threshold must be non-negative")
    private BigDecimal thresholdMax;

    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    @NotNull(message = "Measurement period is required")
    private MeasurementPeriod measurementPeriod;

    @NotNull(message = "Period start is required")
    private LocalDateTime periodStart;

    @NotNull(message = "Period end is required")
    private LocalDateTime periodEnd;

    private TrendDirection trendDirection;

    @DecimalMin(value = "-100.0", message = "Variance percentage must be at least -100%")
    @DecimalMax(value = "1000.0", message = "Variance percentage must not exceed 1000%")
    private BigDecimal variancePercentage;

    @DecimalMin(value = "0.0", message = "Performance score must be non-negative")
    @DecimalMax(value = "200.0", message = "Performance score must not exceed 200%")
    private BigDecimal performanceScore;

    private Boolean isAlertTriggered;

    private AlertLevel alertLevel;

    @Size(max = 100, message = "Data source must not exceed 100 characters")
    private String dataSource;

    @Size(max = 500, message = "Calculation method must not exceed 500 characters")
    private String calculationMethod;

    // Additional metadata
    private Map<String, String> tags;

    private Map<String, String> attributes;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Validate that period end is after period start
     */
    @AssertTrue(message = "Period end must be after period start")
    public boolean isPeriodValid() {
        if (periodStart == null || periodEnd == null) {
            return true; // Let @NotNull handle null validation
        }
        return periodEnd.isAfter(periodStart);
    }

    /**
     * Validate that thresholds are logical
     */
    @AssertTrue(message = "Minimum threshold must be less than maximum threshold")
    public boolean areThresholdsValid() {
        if (thresholdMin == null || thresholdMax == null) {
            return true; // Allow null values
        }
        return thresholdMin.compareTo(thresholdMax) <= 0;
    }

    /**
     * Validate that target value is within thresholds if specified
     */
    @AssertTrue(message = "Target value must be within specified thresholds")
    public boolean isTargetValueValid() {
        if (targetValue == null) {
            return true; // Allow null target
        }
        
        boolean aboveMin = thresholdMin == null || targetValue.compareTo(thresholdMin) >= 0;
        boolean belowMax = thresholdMax == null || targetValue.compareTo(thresholdMax) <= 0;
        
        return aboveMin && belowMax;
    }

    /**
     * Create a minimal MetricDTO for testing
     */
    public static MetricDTO createMinimal(UUID warehouseId, MetricType metricType, 
                                        MetricCategory category, String name, 
                                        BigDecimal value) {
        LocalDateTime now = LocalDateTime.now();
        return MetricDTO.builder()
                .warehouseId(warehouseId)
                .metricType(metricType)
                .metricCategory(category)
                .metricName(name)
                .metricValue(value)
                .measurementPeriod(MeasurementPeriod.DAILY)
                .periodStart(now.minusDays(1))
                .periodEnd(now)
                .build();
    }

    /**
     * Create a MetricDTO with thresholds
     */
    public static MetricDTO createWithThresholds(UUID warehouseId, MetricType metricType,
                                               MetricCategory category, String name,
                                               BigDecimal value, BigDecimal target,
                                               BigDecimal minThreshold, BigDecimal maxThreshold) {
        LocalDateTime now = LocalDateTime.now();
        return MetricDTO.builder()
                .warehouseId(warehouseId)
                .metricType(metricType)
                .metricCategory(category)
                .metricName(name)
                .metricValue(value)
                .targetValue(target)
                .thresholdMin(minThreshold)
                .thresholdMax(maxThreshold)
                .measurementPeriod(MeasurementPeriod.DAILY)
                .periodStart(now.minusDays(1))
                .periodEnd(now)
                .build();
    }
    
    // Convenience methods for compatibility
    public BigDecimal getValue() {
        return metricValue;
    }
    
    public void setValue(BigDecimal value) {
        this.metricValue = value;
    }
    
    public String getUnit() {
        return unitOfMeasure;
    }
    
    public void setUnit(String unit) {
        this.unitOfMeasure = unit;
    }
}