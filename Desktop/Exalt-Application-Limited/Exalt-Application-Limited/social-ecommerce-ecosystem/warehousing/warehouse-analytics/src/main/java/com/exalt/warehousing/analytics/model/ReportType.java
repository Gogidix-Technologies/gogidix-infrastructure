package com.exalt.warehousing.analytics.model;

/**
 * Report Type Enumeration
 * 
 * Represents different types of analytics reports
 */
public enum ReportType {
    DASHBOARD("Interactive dashboard report"),
    SUMMARY("Executive summary report"),
    DETAILED("Detailed analytical report"),
    TREND_ANALYSIS("Trend analysis report"),
    COMPARATIVE("Comparative analysis report"),
    FORECAST("Forecasting and prediction report"),
    EXCEPTION("Exception and anomaly report"),
    PERFORMANCE("Performance assessment report"),
    COMPLIANCE("Compliance and audit report"),
    FINANCIAL("Financial analysis report"),
    OPERATIONAL("Operational metrics report"),
    CUSTOM("Custom user-defined report");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if report type is real-time
     */
    public boolean isRealTime() {
        return this == DASHBOARD || this == EXCEPTION || this == OPERATIONAL;
    }

    /**
     * Check if report type is suitable for automation
     */
    public boolean isSuitableForAutomation() {
        return this == SUMMARY || this == PERFORMANCE || this == COMPLIANCE || 
               this == FINANCIAL || this == OPERATIONAL;
    }

    /**
     * Get default generation frequency
     */
    public String getDefaultFrequency() {
        return switch (this) {
            case DASHBOARD -> "REAL_TIME";
            case OPERATIONAL, EXCEPTION -> "HOURLY";
            case PERFORMANCE -> "DAILY";
            case SUMMARY, DETAILED -> "WEEKLY";
            case FINANCIAL, COMPLIANCE -> "MONTHLY";
            case TREND_ANALYSIS, COMPARATIVE, FORECAST -> "MONTHLY";
            case CUSTOM -> "ON_DEMAND";
        };
    }

    /**
     * Get typical retention period in days
     */
    public int getRetentionDays() {
        return switch (this) {
            case DASHBOARD -> 7;
            case OPERATIONAL, EXCEPTION -> 30;
            case PERFORMANCE, SUMMARY -> 90;
            case DETAILED, TREND_ANALYSIS -> 365;
            case FINANCIAL, COMPLIANCE -> 2555; // 7 years
            case COMPARATIVE, FORECAST -> 730; // 2 years
            case CUSTOM -> 90;
        };
    }

    /**
     * Check if report type requires executive access
     */
    public boolean requiresExecutiveAccess() {
        return this == SUMMARY || this == FINANCIAL || this == COMPLIANCE;
    }
}