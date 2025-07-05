package com.exalt.warehousing.analytics.model;

/**
 * Metric Type Enumeration
 * 
 * Represents different types of performance metrics in warehouse operations
 */
public enum MetricType {
    // Operational Metrics
    THROUGHPUT("Throughput metrics measuring processing rates"),
    EFFICIENCY("Efficiency metrics measuring resource utilization"),
    ACCURACY("Accuracy metrics measuring error rates and quality"),
    PRODUCTIVITY("Productivity metrics measuring output per unit input"),
    UTILIZATION("Utilization metrics measuring capacity usage"),
    
    // Financial Metrics
    COST("Cost-related metrics"),
    REVENUE("Revenue-related metrics"),
    PROFIT("Profit and margin metrics"),
    ROI("Return on investment metrics"),
    
    // Quality Metrics
    QUALITY("Quality control and assurance metrics"),
    COMPLIANCE("Compliance and regulatory metrics"),
    SAFETY("Safety and incident metrics"),
    
    // Time-based Metrics
    CYCLE_TIME("Time taken to complete processes"),
    LEAD_TIME("Time from order to delivery"),
    TURNAROUND_TIME("Time to process requests"),
    UPTIME("System and equipment availability"),
    DOWNTIME("System and equipment unavailability"),
    
    // Inventory Metrics
    INVENTORY_LEVEL("Current inventory quantities"),
    INVENTORY_TURNOVER("Inventory rotation rates"),
    STOCK_ACCURACY("Inventory accuracy measurements"),
    SHRINKAGE("Inventory loss and shrinkage"),
    
    // Customer Service Metrics
    ORDER_FULFILLMENT("Order processing and fulfillment"),
    ORDER_ACCURACY("Order accuracy and correctness metrics"),
    ORDER_PROCESSING_TIME("Time taken to process orders"),
    DELIVERY_PERFORMANCE("Delivery timeliness and accuracy"),
    CUSTOMER_SATISFACTION("Customer satisfaction scores"),
    RETURN_RATE("Return and refund rates"),
    
    // Resource Metrics
    CAPACITY("Capacity measurements"),
    SPACE_UTILIZATION("Space usage efficiency"),
    STORAGE_UTILIZATION("Storage capacity utilization"),
    EQUIPMENT_PERFORMANCE("Equipment efficiency and reliability"),
    LABOR_PRODUCTIVITY("Labor efficiency and output"),
    STAFF_UTILIZATION("Staff workload and utilization"),
    ENERGY_CONSUMPTION("Energy usage and efficiency"),
    
    // Predictive Metrics
    FORECAST_ACCURACY("Prediction accuracy metrics"),
    DEMAND_FORECAST("Demand prediction metrics"),
    TREND_ANALYSIS("Trend identification metrics"),
    ANOMALY_DETECTION("Anomaly and outlier detection"),
    
    // Integration Metrics
    SYSTEM_PERFORMANCE("System performance and response times"),
    DATA_QUALITY("Data accuracy and completeness"),
    API_PERFORMANCE("API response times and availability"),
    SYNCHRONIZATION("Data synchronization metrics");

    private final String description;

    MetricType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if metric type is operational
     */
    public boolean isOperational() {
        return this == THROUGHPUT || this == EFFICIENCY || this == ACCURACY || 
               this == PRODUCTIVITY || this == UTILIZATION;
    }

    /**
     * Check if metric type is financial
     */
    public boolean isFinancial() {
        return this == COST || this == REVENUE || this == PROFIT || this == ROI;
    }

    /**
     * Check if metric type is time-based
     */
    public boolean isTimeBased() {
        return this == CYCLE_TIME || this == LEAD_TIME || this == TURNAROUND_TIME || 
               this == UPTIME || this == DOWNTIME;
    }

    /**
     * Check if metric type is predictive
     */
    public boolean isPredictive() {
        return this == FORECAST_ACCURACY || this == DEMAND_FORECAST || 
               this == TREND_ANALYSIS || this == ANOMALY_DETECTION;
    }

    /**
     * Check if metric type requires real-time monitoring
     */
    public boolean requiresRealTimeMonitoring() {
        return this == THROUGHPUT || this == SYSTEM_PERFORMANCE || this == API_PERFORMANCE || 
               this == CAPACITY || this == SAFETY || this == ANOMALY_DETECTION;
    }

    /**
     * Get recommended measurement frequency
     */
    public String getRecommendedFrequency() {
        return switch (this) {
            case THROUGHPUT, SYSTEM_PERFORMANCE, API_PERFORMANCE, SAFETY -> "REAL_TIME";
            case EFFICIENCY, PRODUCTIVITY, UTILIZATION, CAPACITY -> "HOURLY";
            case ACCURACY, QUALITY, ORDER_FULFILLMENT, ORDER_ACCURACY, DELIVERY_PERFORMANCE -> "DAILY";
            case ORDER_PROCESSING_TIME -> "HOURLY";
            case COST, REVENUE, INVENTORY_TURNOVER, CUSTOMER_SATISFACTION -> "WEEKLY";
            case PROFIT, ROI, FORECAST_ACCURACY, TREND_ANALYSIS -> "MONTHLY";
            default -> "DAILY";
        };
    }
}