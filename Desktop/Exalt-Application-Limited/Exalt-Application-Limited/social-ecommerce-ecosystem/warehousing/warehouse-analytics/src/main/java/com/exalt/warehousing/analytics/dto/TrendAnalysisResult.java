package com.exalt.warehousing.analytics.dto;

import com.exalt.warehousing.analytics.model.MetricType;
import com.exalt.warehousing.analytics.model.TrendDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Trend Analysis Results
 * 
 * Contains comprehensive trend analysis data including statistics, forecasts, and insights
 */
public class TrendAnalysisResult {

    private UUID warehouseId;
    private MetricType metricType;
    private LocalDateTime analysisStartDate;
    private LocalDateTime analysisEndDate;
    private LocalDateTime generatedAt;

    // Basic trend information
    private TrendDirection overallTrend;
    private BigDecimal trendSlope;
    private BigDecimal trendStrength; // R-squared value (0-1)
    private BigDecimal changePercentage;
    private BigDecimal averageValue;

    // Statistical measures
    private BigDecimal standardDeviation;
    private BigDecimal variance;
    private BigDecimal minimum;
    private BigDecimal maximum;
    private BigDecimal median;
    private BigDecimal q1; // First quartile
    private BigDecimal q3; // Third quartile

    // Trend patterns
    private boolean isSeasonalPattern;
    private boolean hasCyclicalPattern;
    private Integer seasonalPeriod; // Days in seasonal cycle
    private BigDecimal seasonalityStrength;

    // Volatility analysis
    private BigDecimal volatilityIndex;
    private List<LocalDateTime> volatilitySpikes;
    private BigDecimal averageVolatility;

    // Data points and forecasting
    private List<DataPoint> historicalData;
    private List<DataPoint> forecastData;
    private BigDecimal forecastConfidence;

    // Anomalies and outliers
    private List<DataPoint> anomalies;
    private List<DataPoint> outliers;
    private Integer anomalyCount;

    // Comparative analysis
    private Map<String, BigDecimal> benchmarkComparison; // Against industry standards
    private Map<String, BigDecimal> periodComparison; // Month-over-month, year-over-year
    private Map<UUID, BigDecimal> peerWarehouseComparison;

    // Insights and recommendations
    private List<String> insights;
    private List<String> recommendations;
    private String riskAssessment;
    private String performanceGrade; // A, B, C, D, F

    // Confidence metrics
    private BigDecimal dataQualityScore;
    private BigDecimal analysisConfidence;
    private Integer dataPointCount;
    private BigDecimal completenessScore;

    // Nested class for data points
    public static class DataPoint {
        private LocalDateTime timestamp;
        private BigDecimal value;
        private BigDecimal predictedValue;
        private BigDecimal confidenceInterval;
        private boolean isAnomaly;
        private boolean isOutlier;
        private String notes;

        public DataPoint() {}

        public DataPoint(LocalDateTime timestamp, BigDecimal value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        // Getters and setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }

        public BigDecimal getPredictedValue() { return predictedValue; }
        public void setPredictedValue(BigDecimal predictedValue) { this.predictedValue = predictedValue; }

        public BigDecimal getConfidenceInterval() { return confidenceInterval; }
        public void setConfidenceInterval(BigDecimal confidenceInterval) { this.confidenceInterval = confidenceInterval; }

        public boolean isAnomaly() { return isAnomaly; }
        public void setAnomaly(boolean anomaly) { isAnomaly = anomaly; }

        public boolean isOutlier() { return isOutlier; }
        public void setOutlier(boolean outlier) { isOutlier = outlier; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    // Constructors
    public TrendAnalysisResult() {
        this.generatedAt = LocalDateTime.now();
    }

    public TrendAnalysisResult(UUID warehouseId, MetricType metricType, 
                             LocalDateTime analysisStartDate, LocalDateTime analysisEndDate) {
        this();
        this.warehouseId = warehouseId;
        this.metricType = metricType;
        this.analysisStartDate = analysisStartDate;
        this.analysisEndDate = analysisEndDate;
    }

    // Getters and Setters
    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public LocalDateTime getAnalysisStartDate() {
        return analysisStartDate;
    }

    public void setAnalysisStartDate(LocalDateTime analysisStartDate) {
        this.analysisStartDate = analysisStartDate;
    }

    public LocalDateTime getAnalysisEndDate() {
        return analysisEndDate;
    }

    public void setAnalysisEndDate(LocalDateTime analysisEndDate) {
        this.analysisEndDate = analysisEndDate;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public TrendDirection getOverallTrend() {
        return overallTrend;
    }

    public void setOverallTrend(TrendDirection overallTrend) {
        this.overallTrend = overallTrend;
    }

    public BigDecimal getTrendSlope() {
        return trendSlope;
    }

    public void setTrendSlope(BigDecimal trendSlope) {
        this.trendSlope = trendSlope;
    }

    public BigDecimal getTrendStrength() {
        return trendStrength;
    }

    public void setTrendStrength(BigDecimal trendStrength) {
        this.trendStrength = trendStrength;
    }

    public BigDecimal getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(BigDecimal changePercentage) {
        this.changePercentage = changePercentage;
    }

    public BigDecimal getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(BigDecimal averageValue) {
        this.averageValue = averageValue;
    }

    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(BigDecimal standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public BigDecimal getVariance() {
        return variance;
    }

    public void setVariance(BigDecimal variance) {
        this.variance = variance;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
    }

    public BigDecimal getMedian() {
        return median;
    }

    public void setMedian(BigDecimal median) {
        this.median = median;
    }

    public BigDecimal getQ1() {
        return q1;
    }

    public void setQ1(BigDecimal q1) {
        this.q1 = q1;
    }

    public BigDecimal getQ3() {
        return q3;
    }

    public void setQ3(BigDecimal q3) {
        this.q3 = q3;
    }

    public boolean isSeasonalPattern() {
        return isSeasonalPattern;
    }

    public void setSeasonalPattern(boolean seasonalPattern) {
        isSeasonalPattern = seasonalPattern;
    }

    public boolean isHasCyclicalPattern() {
        return hasCyclicalPattern;
    }

    public void setHasCyclicalPattern(boolean hasCyclicalPattern) {
        this.hasCyclicalPattern = hasCyclicalPattern;
    }

    public Integer getSeasonalPeriod() {
        return seasonalPeriod;
    }

    public void setSeasonalPeriod(Integer seasonalPeriod) {
        this.seasonalPeriod = seasonalPeriod;
    }

    public BigDecimal getSeasonalityStrength() {
        return seasonalityStrength;
    }

    public void setSeasonalityStrength(BigDecimal seasonalityStrength) {
        this.seasonalityStrength = seasonalityStrength;
    }

    public BigDecimal getVolatilityIndex() {
        return volatilityIndex;
    }

    public void setVolatilityIndex(BigDecimal volatilityIndex) {
        this.volatilityIndex = volatilityIndex;
    }

    public List<LocalDateTime> getVolatilitySpikes() {
        return volatilitySpikes;
    }

    public void setVolatilitySpikes(List<LocalDateTime> volatilitySpikes) {
        this.volatilitySpikes = volatilitySpikes;
    }

    public BigDecimal getAverageVolatility() {
        return averageVolatility;
    }

    public void setAverageVolatility(BigDecimal averageVolatility) {
        this.averageVolatility = averageVolatility;
    }

    public List<DataPoint> getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(List<DataPoint> historicalData) {
        this.historicalData = historicalData;
    }

    public List<DataPoint> getForecastData() {
        return forecastData;
    }

    public void setForecastData(List<DataPoint> forecastData) {
        this.forecastData = forecastData;
    }

    public BigDecimal getForecastConfidence() {
        return forecastConfidence;
    }

    public void setForecastConfidence(BigDecimal forecastConfidence) {
        this.forecastConfidence = forecastConfidence;
    }

    public List<DataPoint> getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(List<DataPoint> anomalies) {
        this.anomalies = anomalies;
    }

    public List<DataPoint> getOutliers() {
        return outliers;
    }

    public void setOutliers(List<DataPoint> outliers) {
        this.outliers = outliers;
    }

    public Integer getAnomalyCount() {
        return anomalyCount;
    }

    public void setAnomalyCount(Integer anomalyCount) {
        this.anomalyCount = anomalyCount;
    }

    public Map<String, BigDecimal> getBenchmarkComparison() {
        return benchmarkComparison;
    }

    public void setBenchmarkComparison(Map<String, BigDecimal> benchmarkComparison) {
        this.benchmarkComparison = benchmarkComparison;
    }

    public Map<String, BigDecimal> getPeriodComparison() {
        return periodComparison;
    }

    public void setPeriodComparison(Map<String, BigDecimal> periodComparison) {
        this.periodComparison = periodComparison;
    }

    public Map<UUID, BigDecimal> getPeerWarehouseComparison() {
        return peerWarehouseComparison;
    }

    public void setPeerWarehouseComparison(Map<UUID, BigDecimal> peerWarehouseComparison) {
        this.peerWarehouseComparison = peerWarehouseComparison;
    }

    public List<String> getInsights() {
        return insights;
    }

    public void setInsights(List<String> insights) {
        this.insights = insights;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public String getPerformanceGrade() {
        return performanceGrade;
    }

    public void setPerformanceGrade(String performanceGrade) {
        this.performanceGrade = performanceGrade;
    }

    public BigDecimal getDataQualityScore() {
        return dataQualityScore;
    }

    public void setDataQualityScore(BigDecimal dataQualityScore) {
        this.dataQualityScore = dataQualityScore;
    }

    public BigDecimal getAnalysisConfidence() {
        return analysisConfidence;
    }

    public void setAnalysisConfidence(BigDecimal analysisConfidence) {
        this.analysisConfidence = analysisConfidence;
    }

    public Integer getDataPointCount() {
        return dataPointCount;
    }

    public void setDataPointCount(Integer dataPointCount) {
        this.dataPointCount = dataPointCount;
    }

    public BigDecimal getCompletenessScore() {
        return completenessScore;
    }

    public void setCompletenessScore(BigDecimal completenessScore) {
        this.completenessScore = completenessScore;
    }

    @Override
    public String toString() {
        return "TrendAnalysisResult{" +
                "warehouseId=" + warehouseId +
                ", metricType=" + metricType +
                ", overallTrend=" + overallTrend +
                ", changePercentage=" + changePercentage +
                ", trendStrength=" + trendStrength +
                ", generatedAt=" + generatedAt +
                '}';
    }
}