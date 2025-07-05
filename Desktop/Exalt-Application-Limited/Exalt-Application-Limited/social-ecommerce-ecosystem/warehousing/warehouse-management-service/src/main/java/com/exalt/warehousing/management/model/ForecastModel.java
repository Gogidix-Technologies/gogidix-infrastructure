package com.exalt.warehousing.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Model class for forecast models
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastModel {
    
    /**
     * Type of the forecast model (e.g., ARIMA, ETS, Prophet)
     */
    private String type;
    
    /**
     * Model parameters
     */
    private Map<String, Object> parameters;
    
    /**
     * Model accuracy metrics
     */
    private Map<String, Object> accuracy;
} 
