package com.exalt.warehousing.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for shipping carriers
 */
@Data
@Component
@ConfigurationProperties(prefix = "warehouse.shipping")
public class ShippingCarrierProperties {
    
    private CarrierConfig usps = new CarrierConfig();
    private CarrierConfig ups = new CarrierConfig();
    private CarrierConfig fedex = new CarrierConfig();
    private CarrierConfig dhl = new CarrierConfig();
    
    /**
     * Configuration for a shipping carrier
     */
    @Data
    public static class CarrierConfig {
        /**
         * API URL for the carrier
         */
        private String apiUrl = "https://api.example.com";
        
        /**
         * API key for the carrier
         */
        private String apiKey = "dummy-api-key";
        
        /**
         * Whether the carrier is enabled
         */
        private boolean enabled = true;
        
        /**
         * Timeout for API calls in milliseconds
         */
        private int timeoutMs = 5000;
    }
} 
