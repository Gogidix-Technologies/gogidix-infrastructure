package com.exalt.warehousing.management.model;

import lombok.Getter;

/**
 * Enum for shipping methods
 */
public enum ShippingMethod {
    // USPS Methods
    USPS_FIRST_CLASS(false),
    USPS_PRIORITY(false),
    USPS_EXPRESS(true),
    
    // UPS Methods
    UPS_GROUND(false),
    UPS_3_DAY_SELECT(true),
    UPS_2ND_DAY_AIR(true),
    UPS_NEXT_DAY_AIR(true),
    
    // FedEx Methods
    FEDEX_GROUND(false),
    FEDEX_2_DAY(true),
    FEDEX_OVERNIGHT(true),
    
    // DHL Methods
    DHL_EXPRESS(true),
    DHL_INTERNATIONAL(false);
    
    @Getter
    private final boolean guaranteed;
    
    ShippingMethod(boolean guaranteed) {
        this.guaranteed = guaranteed;
    }
} 
