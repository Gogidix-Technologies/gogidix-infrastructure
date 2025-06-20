package com.exalt.warehousing.analytics.dto;

import com.exalt.warehousing.analytics.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Analytics Report Requests
 * 
 * Used for creating new analytics reports with comprehensive configuration options
 */
public class AnalyticsReportRequest {

    @NotBlank(message = "Report title is required")
    @Size(max = 255, message = "Report title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Report description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotNull(message = "Report format is required")
    private ReportFormat reportFormat;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private UUID warehouseId; // Optional: null for cross-warehouse reports

    private List<UUID> warehouseIds; // Optional: for multi-warehouse reports

    private List<MetricType> metricTypes; // Optional: specific metrics to include

    private List<MetricCategory> metricCategories; // Optional: specific categories to include

    private MeasurementPeriod aggregationPeriod; // Optional: how to aggregate data

    private boolean includeComparisons = false; // Include year-over-year, period-over-period

    private boolean includeForecasting = false; // Include predictive analytics

    private boolean includeAnomalies = false; // Include anomaly detection

    private boolean includeTrends = false; // Include trend analysis

    private AccessLevel accessLevel = AccessLevel.INTERNAL; // Report visibility level

    private List<String> recipientEmails; // Optional: for automated delivery

    // Constructors
    public AnalyticsReportRequest() {}

    public AnalyticsReportRequest(String title, ReportType reportType, ReportFormat reportFormat, 
                                LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.reportType = reportType;
        this.reportFormat = reportFormat;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(ReportFormat reportFormat) {
        this.reportFormat = reportFormat;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<UUID> getWarehouseIds() {
        return warehouseIds;
    }

    public void setWarehouseIds(List<UUID> warehouseIds) {
        this.warehouseIds = warehouseIds;
    }

    public List<MetricType> getMetricTypes() {
        return metricTypes;
    }

    public void setMetricTypes(List<MetricType> metricTypes) {
        this.metricTypes = metricTypes;
    }

    public List<MetricCategory> getMetricCategories() {
        return metricCategories;
    }

    public void setMetricCategories(List<MetricCategory> metricCategories) {
        this.metricCategories = metricCategories;
    }

    public MeasurementPeriod getAggregationPeriod() {
        return aggregationPeriod;
    }

    public void setAggregationPeriod(MeasurementPeriod aggregationPeriod) {
        this.aggregationPeriod = aggregationPeriod;
    }

    public boolean isIncludeComparisons() {
        return includeComparisons;
    }

    public void setIncludeComparisons(boolean includeComparisons) {
        this.includeComparisons = includeComparisons;
    }

    public boolean isIncludeForecasting() {
        return includeForecasting;
    }

    public void setIncludeForecasting(boolean includeForecasting) {
        this.includeForecasting = includeForecasting;
    }

    public boolean isIncludeAnomalies() {
        return includeAnomalies;
    }

    public void setIncludeAnomalies(boolean includeAnomalies) {
        this.includeAnomalies = includeAnomalies;
    }

    public boolean isIncludeTrends() {
        return includeTrends;
    }

    public void setIncludeTrends(boolean includeTrends) {
        this.includeTrends = includeTrends;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    @Override
    public String toString() {
        return "AnalyticsReportRequest{" +
                "title='" + title + '\'' +
                ", reportType=" + reportType +
                ", reportFormat=" + reportFormat +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", warehouseId=" + warehouseId +
                ", accessLevel=" + accessLevel +
                '}';
    }
}