package com.ecosystem.warehousing.analytics.model;

/**
 * Metric Category Enumeration
 * 
 * Represents high-level categories for organizing warehouse metrics
 */
public enum MetricCategory {
    OPERATIONS("Operational performance and efficiency metrics"),
    FINANCIAL("Financial performance and cost metrics"),
    QUALITY("Quality control and compliance metrics"),
    INVENTORY("Inventory management and accuracy metrics"),
    CUSTOMER_SERVICE("Customer satisfaction and service level metrics"),
    RESOURCE_MANAGEMENT("Resource utilization and capacity metrics"),
    SAFETY("Safety and incident metrics"),
    SUSTAINABILITY("Environmental and sustainability metrics"),
    TECHNOLOGY("Technology performance and integration metrics"),
    STRATEGIC("Strategic business metrics and KPIs");

    private final String description;

    MetricCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if category is business-critical
     */
    public boolean isBusinessCritical() {
        return this == OPERATIONS || this == FINANCIAL || this == CUSTOMER_SERVICE || this == STRATEGIC;
    }

    /**
     * Check if category requires compliance monitoring
     */
    public boolean requiresCompliance() {
        return this == QUALITY || this == SAFETY || this == SUSTAINABILITY;
    }

    /**
     * Get dashboard priority level
     */
    public int getDashboardPriority() {
        return switch (this) {
            case STRATEGIC, FINANCIAL -> 1; // Highest priority
            case OPERATIONS, CUSTOMER_SERVICE -> 2; // High priority
            case QUALITY, INVENTORY -> 3; // Medium priority
            case RESOURCE_MANAGEMENT, TECHNOLOGY -> 4; // Low priority
            case SAFETY, SUSTAINABILITY -> 5; // Monitoring priority
        };
    }

    /**
     * Get typical reporting frequency
     */
    public String getTypicalReportingFrequency() {
        return switch (this) {
            case OPERATIONS, TECHNOLOGY -> "REAL_TIME";
            case INVENTORY, QUALITY -> "DAILY";
            case CUSTOMER_SERVICE, RESOURCE_MANAGEMENT -> "WEEKLY";
            case FINANCIAL, STRATEGIC -> "MONTHLY";
            case SAFETY, SUSTAINABILITY -> "MONTHLY";
        };
    }
}