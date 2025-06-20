package com.exalt.warehousing.fulfillment.enums;

/**
 * Shipping methods available for fulfillment
 */
public enum ShippingMethod {
    STANDARD("Standard Shipping", "5-7 business days"),
    EXPRESS("Express Shipping", "2-3 business days"),
    OVERNIGHT("Overnight Shipping", "1 business day"),
    SAME_DAY("Same Day Delivery", "Same day"),
    PICKUP("Customer Pickup", "Customer will pickup"),
    ECONOMY("Economy Shipping", "7-10 business days");
    
    private final String displayName;
    private final String estimatedDelivery;
    
    ShippingMethod(String displayName, String estimatedDelivery) {
        this.displayName = displayName;
        this.estimatedDelivery = estimatedDelivery;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }
}
