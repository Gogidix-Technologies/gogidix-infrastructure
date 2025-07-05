package com.exalt.warehousing.analytics.dto;

import com.exalt.warehousing.analytics.model.MetricCategory;
import com.exalt.warehousing.analytics.model.PerformanceMetric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Dashboard Data
 * 
 * Contains all necessary data for warehouse analytics dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {

    private UUID warehouseId;
    private String warehouseName;
    private LocalDateTime lastUpdated;
    private String timeZone;

    // Overall Performance
    private BigDecimal overallPerformanceScore;
    private String performanceGrade; // A, B, C, D, F
    private String performanceTrend; // IMPROVING, DECLINING, STABLE
    
    // Additional fields
    private LocalDateTime generatedAt;
    private Integer totalMetrics;
    private BigDecimal efficiencyScore;
    private Integer activeAlerts;

    // Key Performance Indicators
    private Map<String, KPIData> kpis;

    // Performance by Category
    private Map<MetricCategory, CategorySummary> categoryPerformance;

    // Recent Metrics
    private List<PerformanceMetric> recentMetrics;

    // Alert Summary
    private AlertSummary alerts;

    // Trend Data
    private List<TrendPoint> performanceTrends;

    // Capacity and Utilization
    private CapacityData capacity;

    // Operational Metrics
    private OperationalSummary operational;

    // Financial Metrics
    private FinancialSummary financial;

    // Quality Metrics
    private QualitySummary quality;

    // Top Issues
    private List<IssueData> topIssues;

    // Recommendations
    private List<RecommendationData> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KPIData {
        private String name;
        private BigDecimal currentValue;
        private BigDecimal targetValue;
        private BigDecimal previousValue;
        private String unit;
        private String trend; // UP, DOWN, STABLE
        private BigDecimal changePercentage;
        private String status; // GOOD, WARNING, CRITICAL
        private LocalDateTime lastUpdate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private MetricCategory category;
        private BigDecimal averageScore;
        private Integer totalMetrics;
        private Integer alertCount;
        private String trendDirection;
        private List<PerformanceMetric> topMetrics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertSummary {
        private Integer totalAlerts;
        private Integer criticalAlerts;
        private Integer highAlerts;
        private Integer mediumAlerts;
        private Integer lowAlerts;
        private Integer newAlertsToday;
        private List<PerformanceMetric> recentCriticalAlerts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private LocalDateTime timestamp;
        private BigDecimal value;
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapacityData {
        private BigDecimal totalCapacity;
        private BigDecimal usedCapacity;
        private BigDecimal availableCapacity;
        private BigDecimal utilizationPercentage;
        private String utilizationStatus; // OPTIMAL, HIGH, CRITICAL
        private Map<String, BigDecimal> capacityByType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationalSummary {
        private BigDecimal throughputToday;
        private BigDecimal averageThroughput;
        private BigDecimal efficiency;
        private BigDecimal accuracy;
        private BigDecimal cycleTime;
        private Integer ordersProcessed;
        private Integer itemsHandled;
        private BigDecimal errorRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialSummary {
        private BigDecimal dailyCost;
        private BigDecimal monthlyCost;
        private BigDecimal costPerOrder;
        private BigDecimal costPerItem;
        private BigDecimal revenue;
        private BigDecimal profitMargin;
        private BigDecimal roi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualitySummary {
        private BigDecimal qualityScore;
        private BigDecimal accuracyRate;
        private BigDecimal damageRate;
        private BigDecimal returnRate;
        private Integer qualityIssues;
        private BigDecimal customerSatisfaction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssueData {
        private String issueType;
        private String description;
        private String severity;
        private LocalDateTime detectedAt;
        private Integer affectedMetrics;
        private BigDecimal impactScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationData {
        private String title;
        private String description;
        private String category;
        private String priority;
        private BigDecimal potentialImpact;
        private String actionRequired;
        private LocalDateTime createdAt;
    }

    /**
     * Calculate overall health score
     */
    public String getOverallHealthStatus() {
        if (overallPerformanceScore == null) {
            return "UNKNOWN";
        }
        
        if (overallPerformanceScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "EXCELLENT";
        } else if (overallPerformanceScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "GOOD";
        } else if (overallPerformanceScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "FAIR";
        } else if (overallPerformanceScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "POOR";
        } else {
            return "CRITICAL";
        }
    }

    /**
     * Get total alert count
     */
    public Integer getTotalAlertCount() {
        return alerts != null ? alerts.getTotalAlerts() : 0;
    }

    /**
     * Check if warehouse requires immediate attention
     */
    public Boolean requiresImmediateAttention() {
        return alerts != null && alerts.getCriticalAlerts() != null && alerts.getCriticalAlerts() > 0;
    }

    /**
     * Get capacity utilization status
     */
    public String getCapacityUtilizationStatus() {
        if (capacity == null || capacity.getUtilizationPercentage() == null) {
            return "UNKNOWN";
        }
        
        BigDecimal utilization = capacity.getUtilizationPercentage();
        if (utilization.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "CRITICAL";
        } else if (utilization.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "HIGH";
        } else if (utilization.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "OPTIMAL";
        } else {
            return "LOW";
        }
    }
}