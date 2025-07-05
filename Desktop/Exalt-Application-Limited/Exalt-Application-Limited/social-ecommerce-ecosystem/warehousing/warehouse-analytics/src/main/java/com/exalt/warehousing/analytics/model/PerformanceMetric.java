package com.exalt.warehousing.analytics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing performance metrics for warehouse operations
 * Stored in both PostgreSQL for ACID compliance and Elasticsearch for analytics
 */
@Entity
@Table(name = "performance_metrics")
@Document(indexName = "warehouse_performance_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @org.springframework.data.annotation.Id
    private UUID id;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    @Field(type = FieldType.Keyword)
    private UUID warehouseId;

    @NotNull
    @Column(name = "metric_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword)
    private MetricType metricType;

    @NotNull
    @Column(name = "metric_category", nullable = false)
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword)
    private MetricCategory metricCategory;

    @NotNull
    @Column(name = "metric_name", nullable = false)
    @Field(type = FieldType.Text, analyzer = "standard")
    private String metricName;

    @NotNull
    @PositiveOrZero
    @Column(name = "metric_value", precision = 15, scale = 4, nullable = false)
    @Field(type = FieldType.Double)
    private BigDecimal metricValue;

    @Column(name = "target_value", precision = 15, scale = 4)
    @Field(type = FieldType.Double)
    private BigDecimal targetValue;

    @Column(name = "threshold_min", precision = 15, scale = 4)
    @Field(type = FieldType.Double)
    private BigDecimal thresholdMin;

    @Column(name = "threshold_max", precision = 15, scale = 4)
    @Field(type = FieldType.Double)
    private BigDecimal thresholdMax;

    @Column(name = "unit_of_measure")
    @Field(type = FieldType.Keyword)
    private String unitOfMeasure;

    @NotNull
    @Column(name = "measurement_period", nullable = false)
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword)
    private MeasurementPeriod measurementPeriod;

    @NotNull
    @Column(name = "period_start", nullable = false)
    @Field(type = FieldType.Date)
    private LocalDateTime periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    @Field(type = FieldType.Date)
    private LocalDateTime periodEnd;

    @Column(name = "trend_direction")
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword)
    private TrendDirection trendDirection;

    @Column(name = "variance_percentage", precision = 5, scale = 2)
    @Field(type = FieldType.Double)
    private BigDecimal variancePercentage;

    @Column(name = "performance_score", precision = 5, scale = 2)
    @Field(type = FieldType.Double)
    private BigDecimal performanceScore;

    @Column(name = "is_alert_triggered")
    @Field(type = FieldType.Boolean)
    private Boolean isAlertTriggered;

    @Column(name = "alert_level")
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword)
    private AlertLevel alertLevel;

    @Column(name = "data_source")
    @Field(type = FieldType.Keyword)
    private String dataSource;

    @Column(name = "calculation_method")
    @Field(type = FieldType.Text)
    private String calculationMethod;

    // JSON storage for additional context and metadata
    @ElementCollection
    @CollectionTable(name = "metric_tags", joinColumns = @JoinColumn(name = "metric_id"))
    @MapKeyColumn(name = "tag_key")
    @Column(name = "tag_value")
    @Builder.Default
    @Field(type = FieldType.Object)
    private Map<String, String> tags = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "metric_attributes", joinColumns = @JoinColumn(name = "metric_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    @Builder.Default
    @Field(type = FieldType.Object)
    private Map<String, String> attributes = new HashMap<>();

    @Column(name = "notes", columnDefinition = "TEXT")
    @Field(type = FieldType.Text, analyzer = "standard")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
    
    @Column(name = "recorded_at")
    @Field(type = FieldType.Date)
    private LocalDateTime recordedAt;
    
    @Column(name = "last_updated_by")
    @Field(type = FieldType.Keyword)
    private String lastUpdatedBy;
    
    @Column(name = "last_updated_at")
    @Field(type = FieldType.Date)
    private LocalDateTime lastUpdatedAt;
    
    @Column(name = "is_active")
    @Field(type = FieldType.Boolean)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "archive_date")
    @Field(type = FieldType.Date)
    private LocalDateTime archiveDate;
    
    @Column(name = "description")
    @Field(type = FieldType.Text)
    private String description;

    /**
     * Calculate performance score based on target vs actual
     */
    @Transient
    public BigDecimal calculatePerformanceScore() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100); // Default score when no target
        }
        
        BigDecimal achievement = metricValue.divide(targetValue, 4, java.math.RoundingMode.HALF_UP);
        BigDecimal score = achievement.multiply(BigDecimal.valueOf(100));
        
        // Cap at 150% to prevent extremely high scores
        if (score.compareTo(BigDecimal.valueOf(150)) > 0) {
            score = BigDecimal.valueOf(150);
        }
        
        return score;
    }

    /**
     * Check if metric is within acceptable range
     */
    @Transient
    public boolean isWithinThreshold() {
        if (thresholdMin == null && thresholdMax == null) {
            return true;
        }
        
        boolean aboveMin = thresholdMin == null || metricValue.compareTo(thresholdMin) >= 0;
        boolean belowMax = thresholdMax == null || metricValue.compareTo(thresholdMax) <= 0;
        
        return aboveMin && belowMax;
    }

    /**
     * Calculate variance from target
     */
    @Transient
    public BigDecimal calculateVarianceFromTarget() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal difference = metricValue.subtract(targetValue);
        return difference.divide(targetValue, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Determine alert level based on performance
     */
    @Transient
    public AlertLevel determineAlertLevel() {
        if (isWithinThreshold()) {
            return AlertLevel.NONE;
        }
        
        BigDecimal variance = calculateVarianceFromTarget().abs();
        
        if (variance.compareTo(BigDecimal.valueOf(50)) > 0) {
            return AlertLevel.CRITICAL;
        } else if (variance.compareTo(BigDecimal.valueOf(25)) > 0) {
            return AlertLevel.HIGH;
        } else if (variance.compareTo(BigDecimal.valueOf(10)) > 0) {
            return AlertLevel.MEDIUM;
        } else {
            return AlertLevel.LOW;
        }
    }

    /**
     * Add a tag to the metric
     */
    public void addTag(String key, String value) {
        if (tags == null) {
            tags = new HashMap<>();
        }
        tags.put(key, value);
    }

    /**
     * Add an attribute to the metric
     */
    public void addAttribute(String key, String value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    /**
     * Check if metric indicates improving trend
     */
    @Transient
    public boolean isImproving() {
        return trendDirection == TrendDirection.IMPROVING;
    }

    /**
     * Check if metric indicates declining trend
     */
    @Transient
    public boolean isDeclining() {
        return trendDirection == TrendDirection.DECLINING;
    }

    /**
     * Check if alert should be triggered
     */
    @Transient
    public boolean shouldTriggerAlert() {
        return !isWithinThreshold() || isDeclining();
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
    
    public Boolean getIsAlert() {
        return isAlertTriggered;
    }
    
    public void setIsAlert(Boolean isAlert) {
        this.isAlertTriggered = isAlert;
    }
}