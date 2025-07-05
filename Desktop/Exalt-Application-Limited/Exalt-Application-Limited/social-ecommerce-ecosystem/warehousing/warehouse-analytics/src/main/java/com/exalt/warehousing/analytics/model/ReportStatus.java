package com.exalt.warehousing.analytics.model;

/**
 * Report Status Enumeration
 * 
 * Represents the current status of analytics report generation
 */
public enum ReportStatus {
    PENDING("Report scheduled and pending execution"),
    GENERATING("Report generation in progress"),
    PROCESSING("Report generation in progress"),
    COMPLETED("Report generated successfully"),
    FAILED("Report generation failed"),
    CANCELLED("Report generation cancelled"),
    EXPIRED("Report has expired and is no longer available");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if status is terminal
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == EXPIRED;
    }

    /**
     * Check if status indicates success
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Check if status indicates failure
     */
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }

    /**
     * Check if status allows cancellation
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == PROCESSING || this == GENERATING;
    }

    /**
     * Check if status allows retry
     */
    public boolean canBeRetried() {
        return this == FAILED || this == CANCELLED;
    }

    /**
     * Get next possible statuses
     */
    public ReportStatus[] getNextPossibleStatuses() {
        return switch (this) {
            case PENDING -> new ReportStatus[]{PROCESSING, GENERATING, CANCELLED};
            case PROCESSING, GENERATING -> new ReportStatus[]{COMPLETED, FAILED, CANCELLED};
            case COMPLETED -> new ReportStatus[]{EXPIRED};
            case FAILED, CANCELLED -> new ReportStatus[]{PENDING}; // For retry
            case EXPIRED -> new ReportStatus[]{}; // Terminal
        };
    }
}