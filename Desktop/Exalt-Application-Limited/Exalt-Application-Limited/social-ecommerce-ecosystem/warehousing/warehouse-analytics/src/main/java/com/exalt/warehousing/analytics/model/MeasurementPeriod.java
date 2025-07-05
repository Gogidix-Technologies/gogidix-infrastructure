package com.exalt.warehousing.analytics.model;

/**
 * Measurement Period Enumeration
 * 
 * Represents time periods for metric measurement and aggregation
 */
public enum MeasurementPeriod {
    REAL_TIME("Real-time measurement"),
    MINUTE("Per minute measurement"),
    FIVE_MINUTES("5-minute intervals"),
    FIFTEEN_MINUTES("15-minute intervals"),
    THIRTY_MINUTES("30-minute intervals"),
    HOURLY("Hourly measurement"),
    FOUR_HOURS("4-hour intervals"),
    DAILY("Daily measurement"),
    WEEKLY("Weekly measurement"),
    MONTHLY("Monthly measurement"),
    QUARTERLY("Quarterly measurement"),
    YEARLY("Annual measurement"),
    CUSTOM("Custom time period");

    private final String description;

    MeasurementPeriod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get duration in minutes
     */
    public long getDurationInMinutes() {
        return switch (this) {
            case REAL_TIME -> 0;
            case MINUTE -> 1;
            case FIVE_MINUTES -> 5;
            case FIFTEEN_MINUTES -> 15;
            case THIRTY_MINUTES -> 30;
            case HOURLY -> 60;
            case FOUR_HOURS -> 240;
            case DAILY -> 1440;
            case WEEKLY -> 10080;
            case MONTHLY -> 43200; // Approximate 30 days
            case QUARTERLY -> 129600; // Approximate 90 days
            case YEARLY -> 525600; // Approximate 365 days
            case CUSTOM -> -1; // Variable
        };
    }

    /**
     * Check if period is high frequency
     */
    public boolean isHighFrequency() {
        return this == REAL_TIME || this == MINUTE || this == FIVE_MINUTES || 
               this == FIFTEEN_MINUTES || this == THIRTY_MINUTES;
    }

    /**
     * Check if period is suitable for real-time monitoring
     */
    public boolean isSuitableForRealTimeMonitoring() {
        return isHighFrequency() || this == HOURLY;
    }

    /**
     * Check if period is suitable for trending
     */
    public boolean isSuitableForTrending() {
        return this == DAILY || this == WEEKLY || this == MONTHLY || 
               this == QUARTERLY || this == YEARLY;
    }

    /**
     * Get next aggregation level
     */
    public MeasurementPeriod getNextAggregationLevel() {
        return switch (this) {
            case REAL_TIME, MINUTE -> FIVE_MINUTES;
            case FIVE_MINUTES -> FIFTEEN_MINUTES;
            case FIFTEEN_MINUTES -> THIRTY_MINUTES;
            case THIRTY_MINUTES -> HOURLY;
            case HOURLY -> FOUR_HOURS;
            case FOUR_HOURS -> DAILY;
            case DAILY -> WEEKLY;
            case WEEKLY -> MONTHLY;
            case MONTHLY -> QUARTERLY;
            case QUARTERLY -> YEARLY;
            case YEARLY, CUSTOM -> YEARLY;
        };
    }

    /**
     * Get storage retention period in days
     */
    public int getRetentionDays() {
        return switch (this) {
            case REAL_TIME, MINUTE -> 1;
            case FIVE_MINUTES, FIFTEEN_MINUTES -> 7;
            case THIRTY_MINUTES, HOURLY -> 30;
            case FOUR_HOURS, DAILY -> 365;
            case WEEKLY -> 730; // 2 years
            case MONTHLY -> 2555; // 7 years
            case QUARTERLY, YEARLY -> 3650; // 10 years
            case CUSTOM -> 365; // Default
        };
    }
}