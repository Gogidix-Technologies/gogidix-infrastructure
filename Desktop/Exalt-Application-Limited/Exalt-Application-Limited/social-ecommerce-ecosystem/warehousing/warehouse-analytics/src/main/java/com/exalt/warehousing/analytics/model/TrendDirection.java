package com.exalt.warehousing.analytics.model;

/**
 * Trend Direction Enumeration
 * 
 * Represents the direction of metric trends over time
 */
public enum TrendDirection {
    STRONG_UPWARD("Strong positive improvement trend"),
    UPWARD("Positive improvement trend"),
    STABLE("Metric is stable with minimal variation"),
    DOWNWARD("Negative declining trend"),
    STRONG_DOWNWARD("Strong negative declining trend"),
    IMPROVING("Metric is showing positive improvement"),
    DECLINING("Metric is showing negative decline"),
    VOLATILE("Metric shows high volatility"),
    UNKNOWN("Trend direction cannot be determined"),
    SEASONAL("Metric follows seasonal patterns"),
    CYCLICAL("Metric follows cyclical patterns");

    private final String description;

    TrendDirection(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if trend is positive
     */
    public boolean isPositive() {
        return this == IMPROVING || this == STABLE || this == STRONG_UPWARD || this == UPWARD;
    }

    /**
     * Check if trend is negative
     */
    public boolean isNegative() {
        return this == DECLINING || this == DOWNWARD || this == STRONG_DOWNWARD;
    }

    /**
     * Check if trend requires attention
     */
    public boolean requiresAttention() {
        return this == DECLINING || this == VOLATILE || this == DOWNWARD || this == STRONG_DOWNWARD;
    }

    /**
     * Check if trend is predictable
     */
    public boolean isPredictable() {
        return this != UNKNOWN && this != VOLATILE;
    }

    /**
     * Get trend severity score (1-5, where 5 is most severe)
     */
    public int getSeverityScore() {
        return switch (this) {
            case STRONG_UPWARD, IMPROVING -> 1;
            case UPWARD, STABLE -> 2;
            case SEASONAL, CYCLICAL -> 3;
            case UNKNOWN, VOLATILE -> 4;
            case DOWNWARD, DECLINING -> 5;
            case STRONG_DOWNWARD -> 5;
        };
    }

    /**
     * Get recommended action priority
     */
    public String getActionPriority() {
        return switch (this) {
            case STRONG_UPWARD, UPWARD, IMPROVING, STABLE -> "MONITOR";
            case SEASONAL, CYCLICAL -> "ANALYZE";
            case UNKNOWN -> "INVESTIGATE";
            case VOLATILE -> "REVIEW";
            case DOWNWARD, DECLINING -> "ACTION_REQUIRED";
            case STRONG_DOWNWARD -> "IMMEDIATE_ACTION";
        };
    }
}