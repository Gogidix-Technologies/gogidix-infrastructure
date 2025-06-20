package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data Transfer Object for forecast models
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastModelDTO {
    
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
    
    /**
     * Number of data points used for training
     */
    private int trainingDataPoints;
} 
