package com.exalt.warehousing.management.model;

/**
 * Warehouse Type Enumeration
 * 
 * Defines different types of warehouses in the micro social ecommerce ecosystem.
 * Supports various fulfillment models including centralized, distributed, and vendor-managed storage.
 * 
 * @author Exalt Application Limited
 * @version 1.0
 */
public enum WarehouseType {
    
    /**
     * Main distribution center handling bulk inventory
     * - Primary storage facility
     * - Handles large volume orders
     * - Connected to major shipping carriers
     */
    DISTRIBUTION_CENTER("Distribution Center", "Primary facility for bulk inventory and major order fulfillment"),
    
    /**
     * Fulfillment center optimized for fast shipping
     * - Focus on quick order processing
     * - Located near major population centers
     * - Optimized for same-day or next-day delivery
     */
    FULFILLMENT_CENTER("Fulfillment Center", "Fast-shipping optimized facility near population centers"),
    
    /**
     * Regional hub for local distribution
     * - Serves specific geographic regions
     * - Handles local and regional orders
     * - May serve multiple local markets
     */
    REGIONAL_HUB("Regional Hub", "Regional distribution facility serving local markets"),
    
    /**
     * Cross-docking facility for rapid transit
     * - Minimal storage, focus on transit
     * - Rapid sorting and redistribution
     * - Coordinates between facilities
     */
    CROSS_DOCK("Cross Dock", "Transit facility for rapid sorting and redistribution"),
    
    /**
     * Cold storage for temperature-sensitive items
     * - Refrigerated storage capability
     * - Specialized handling procedures
     * - Required for perishable goods
     */
    COLD_STORAGE("Cold Storage", "Temperature-controlled facility for perishable goods"),
    
    /**
     * Vendor-managed warehouse
     * - Managed by third-party vendors
     * - Vendor controls inventory and fulfillment
     * - Platform provides coordination interface
     */
    VENDOR_MANAGED("Vendor Managed", "Third-party vendor controlled storage and fulfillment"),
    
    /**
     * Returns processing center
     * - Specialized for handling returns
     * - Quality assessment and refurbishment
     * - Redistribution of returned items
     */
    RETURNS_CENTER("Returns Center", "Specialized facility for processing returns and refurbishment"),
    
    /**
     * Temporary or pop-up warehouse
     * - Short-term storage solution
     * - Event-based or seasonal use
     * - Flexible capacity management
     */
    TEMPORARY("Temporary", "Short-term or seasonal storage facility"),
    
    /**
     * Automated micro-fulfillment center
     * - High automation level
     * - Compact urban facilities
     * - Robotic picking and packing
     */
    MICRO_FULFILLMENT("Micro Fulfillment", "Automated compact facility for urban rapid delivery");
    
    private final String displayName;
    private final String description;
    
    WarehouseType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Get human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get detailed description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this warehouse type supports automated operations
     */
    public boolean supportsAutomation() {
        return this == FULFILLMENT_CENTER || this == MICRO_FULFILLMENT || this == CROSS_DOCK;
    }
    
    /**
     * Check if this warehouse type requires special handling capabilities
     */
    public boolean requiresSpecialHandling() {
        return this == COLD_STORAGE || this == RETURNS_CENTER;
    }
    
    /**
     * Check if this warehouse type is vendor-controlled
     */
    public boolean isVendorControlled() {
        return this == VENDOR_MANAGED;
    }
    
    /**
     * Check if this warehouse type is suitable for rapid delivery
     */
    public boolean supportsRapidDelivery() {
        return this == FULFILLMENT_CENTER || this == MICRO_FULFILLMENT || this == REGIONAL_HUB;
    }
    
    /**
     * Get recommended capacity utilization threshold for this warehouse type
     */
    public double getRecommendedCapacityThreshold() {
        return switch (this) {
            case MICRO_FULFILLMENT, FULFILLMENT_CENTER -> 0.85; // High utilization for fast centers
            case CROSS_DOCK, TEMPORARY -> 0.60; // Lower utilization for transit facilities
            case COLD_STORAGE, RETURNS_CENTER -> 0.75; // Moderate utilization for special handling
            default -> 0.80; // Standard utilization
        };
    }
}