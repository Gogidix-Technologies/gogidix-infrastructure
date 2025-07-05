package com.exalt.warehousing.analytics.model;

/**
 * Alert Level Enumeration
 * 
 * Represents severity levels for metric-based alerts
 */
public enum AlertLevel {
    NONE("No alert - metric within acceptable range"),
    INFO("Informational alert - no action required"),
    LOW("Low priority alert - minor deviation"),
    MEDIUM("Medium priority alert - moderate deviation"),
    HIGH("High priority alert - significant deviation"),
    CRITICAL("Critical alert - severe deviation requiring immediate attention");

    private final String description;

    AlertLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if alert level requires immediate action
     */
    public boolean requiresImmediateAction() {
        return this == CRITICAL;
    }

    /**
     * Check if alert level requires escalation
     */
    public boolean requiresEscalation() {
        return this == HIGH || this == CRITICAL;
    }

    /**
     * Get numerical priority (1-5, where 5 is highest)
     */
    public int getPriority() {
        return switch (this) {
            case NONE -> 0;
            case INFO -> 1;
            case LOW -> 2;
            case MEDIUM -> 3;
            case HIGH -> 4;
            case CRITICAL -> 5;
        };
    }

    /**
     * Get response time expectation in minutes
     */
    public int getResponseTimeMinutes() {
        return switch (this) {
            case NONE -> 0;
            case INFO -> 2880; // 48 hours
            case LOW -> 1440; // 24 hours
            case MEDIUM -> 480; // 8 hours
            case HIGH -> 120; // 2 hours
            case CRITICAL -> 15; // 15 minutes
        };
    }

    /**
     * Get notification channels required
     */
    public String[] getNotificationChannels() {
        return switch (this) {
            case NONE -> new String[]{};
            case INFO -> new String[]{"DASHBOARD"};
            case LOW -> new String[]{"EMAIL"};
            case MEDIUM -> new String[]{"EMAIL", "DASHBOARD"};
            case HIGH -> new String[]{"EMAIL", "DASHBOARD", "SMS"};
            case CRITICAL -> new String[]{"EMAIL", "DASHBOARD", "SMS", "PHONE", "SLACK"};
        };
    }

    /**
     * Get escalation level for management
     */
    public String getEscalationLevel() {
        return switch (this) {
            case NONE, INFO, LOW -> "NONE";
            case MEDIUM -> "SUPERVISOR";
            case HIGH -> "MANAGER";
            case CRITICAL -> "EXECUTIVE";
        };
    }

    /**
     * Check if level should trigger automated remediation
     */
    public boolean shouldTriggerAutomation() {
        return this == HIGH || this == CRITICAL;
    }

    /**
     * Get color code for UI display
     */
    public String getColorCode() {
        return switch (this) {
            case NONE -> "#28a745"; // Green
            case INFO -> "#17a2b8"; // Blue
            case LOW -> "#ffc107"; // Yellow
            case MEDIUM -> "#fd7e14"; // Orange
            case HIGH -> "#dc3545"; // Red
            case CRITICAL -> "#6f42c1"; // Purple
        };
    }
}