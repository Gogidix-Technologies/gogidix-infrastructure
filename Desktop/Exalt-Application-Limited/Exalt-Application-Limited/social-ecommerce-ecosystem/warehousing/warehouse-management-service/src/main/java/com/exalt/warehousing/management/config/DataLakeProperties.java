package com.exalt.warehousing.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for data lake integration
 */
// @Data - Replaced with explicit getters and setters
@Component
@ConfigurationProperties(prefix = "warehouse.data-lake")
public class DataLakeProperties {

    /**
     * Base URL for the data lake API
     */
    private String baseUrl = "http://data-lake-service:8080";
    
    /**
     * API key for data lake authentication
     */
    private String apiKey;
    
    /**
     * Default dataset name for warehouse data
     */
    private String defaultDataset = "warehouse";
    
    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 5000;
    
    /**
     * Read timeout in milliseconds
     */
    private int readTimeout = 30000;
    
    /**
     * Maximum retry attempts for failed operations
     */
    private int maxRetries = 3;
    
    /**
     * Flag to enable/disable data lake integration
     */
    private boolean enabled = true;
    
    /**
     * Gets the base URL for the data lake API
     * @return the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Sets the base URL for the data lake API
     * @param baseUrl the base URL to set
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Gets the API key for data lake authentication
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }
    
    /**
     * Sets the API key for data lake authentication
     * @param apiKey the API key to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    /**
     * Gets the default dataset name for warehouse data
     * @return the default dataset name
     */
    public String getDefaultDataset() {
        return defaultDataset;
    }
    
    /**
     * Sets the default dataset name for warehouse data
     * @param defaultDataset the default dataset name to set
     */
    public void setDefaultDataset(String defaultDataset) {
        this.defaultDataset = defaultDataset;
    }
    
    /**
     * Gets the connection timeout in milliseconds
     * @return the connection timeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    /**
     * Sets the connection timeout in milliseconds
     * @param connectionTimeout the connection timeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    /**
     * Gets the read timeout in milliseconds
     * @return the read timeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }
    
    /**
     * Sets the read timeout in milliseconds
     * @param readTimeout the read timeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    /**
     * Gets the maximum retry attempts for failed operations
     * @return the maximum retry attempts
     */
    public int getMaxRetries() {
        return maxRetries;
    }
    
    /**
     * Sets the maximum retry attempts for failed operations
     * @param maxRetries the maximum retry attempts to set
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    /**
     * Checks if data lake integration is enabled
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets whether data lake integration is enabled
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
