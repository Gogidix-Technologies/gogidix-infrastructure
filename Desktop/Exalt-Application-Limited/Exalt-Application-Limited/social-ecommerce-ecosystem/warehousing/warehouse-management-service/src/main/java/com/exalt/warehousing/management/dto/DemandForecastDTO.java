package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for demand forecasts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandForecastDTO {
    
    /**
     * ID of the product being forecasted
     */
    private UUID productId;
    
    /**
     * Name of the product category
     */
    private String categoryName;
    
    /**
     * When the forecast was generated
     */
    private LocalDateTime generatedAt;
    
    /**
     * Number of days in the forecast
     */
    private int forecastDays;
    
    /**
     * List of forecasted values
     */
    private List<Double> forecastValues;
    
    /**
     * List of dates for the forecast period
     */
    private List<LocalDate> forecastDates;
    
    /**
     * Lower bound of the confidence interval
     */
    private List<Double> lowerBound;
    
    /**
     * Upper bound of the confidence interval
     */
    private List<Double> upperBound;
    
    /**
     * Confidence level (e.g., 95.0)
     */
    private double confidenceLevel;
    
    /**
     * Information about the forecast model
     */
    private ForecastModelDTO model;
} 
