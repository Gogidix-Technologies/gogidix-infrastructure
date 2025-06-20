package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.DemandForecastDTO;
import com.exalt.warehousing.management.dto.ForecastModelDTO;
import com.exalt.warehousing.management.model.ForecastModel;
import com.exalt.warehousing.management.repository.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for demand forecasting using machine learning models
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DemandForecastingService {

    private final InventoryMovementRepository inventoryMovementRepository;
    
    /**
     * Generate demand forecast for a product
     *
     * @param productId the product ID
     * @param forecastDays number of days to forecast
     * @param modelType the forecasting model type
     * @return the demand forecast
     */
    @Transactional(readOnly = true)
    public DemandForecastDTO generateProductDemandForecast(UUID productId, int forecastDays, String modelType) {
        log.info("Generating {} day demand forecast for product {} using {} model", 
                forecastDays, productId, modelType);
        
        // Get historical movement data for the product
        LocalDateTime startDate = LocalDateTime.now().minus(90, ChronoUnit.DAYS);
        List<Map<String, Object>> historicalData = inventoryMovementRepository
                .getProductMovementHistory(productId, startDate);
        
        // Prepare time series data
        List<Double> timeSeriesData = prepareTimeSeriesData(historicalData);
        
        // Select and train the appropriate forecasting model
        ForecastModel model = selectForecastModel(modelType, timeSeriesData);
        
        // Generate forecast
        List<Double> forecast = generateForecast(model, timeSeriesData, forecastDays);
        
        // Calculate confidence intervals
        Map<String, List<Double>> confidenceIntervals = calculateConfidenceIntervals(forecast, model);
        
        // Prepare forecast dates
        List<LocalDate> forecastDates = generateForecastDates(forecastDays);
        
        // Build and return the forecast DTO
        return DemandForecastDTO.builder()
                .productId(productId)
                .generatedAt(LocalDateTime.now())
                .forecastDays(forecastDays)
                .forecastValues(forecast)
                .forecastDates(forecastDates)
                .lowerBound(confidenceIntervals.get("lower"))
                .upperBound(confidenceIntervals.get("upper"))
                .confidenceLevel(95.0)
                .model(buildModelDTO(model, timeSeriesData.size()))
                .build();
    }
    
    /**
     * Generate demand forecast for a warehouse
     *
     * @param warehouseId the warehouse ID
     * @param forecastDays number of days to forecast
     * @param modelType the forecasting model type
     * @return the demand forecast
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generateWarehouseDemandForecast(UUID warehouseId, int forecastDays, String modelType) {
        log.info("Generating {} day demand forecast for warehouse {} using {} model", 
                forecastDays, warehouseId, modelType);
        
        // Get historical movement data for the warehouse
        LocalDateTime startDate = LocalDateTime.now().minus(90, ChronoUnit.DAYS);
        List<Map<String, Object>> historicalData = inventoryMovementRepository
                .getWarehouseMovementHistory(warehouseId, startDate);
        
        // Group by product category
        Map<String, List<Map<String, Object>>> dataByCategory = historicalData.stream()
                .collect(Collectors.groupingBy(data -> (String) data.get("categoryName")));
        
        // Generate forecast for each category
        Map<String, DemandForecastDTO> categoryForecasts = new HashMap<>();
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : dataByCategory.entrySet()) {
            String category = entry.getKey();
            List<Map<String, Object>> categoryData = entry.getValue();
            
            // Prepare time series data for this category
            List<Double> timeSeriesData = prepareTimeSeriesData(categoryData);
            
            // Select and train model
            ForecastModel model = selectForecastModel(modelType, timeSeriesData);
            
            // Generate forecast
            List<Double> forecast = generateForecast(model, timeSeriesData, forecastDays);
            
            // Calculate confidence intervals
            Map<String, List<Double>> confidenceIntervals = calculateConfidenceIntervals(forecast, model);
            
            // Prepare forecast dates
            List<LocalDate> forecastDates = generateForecastDates(forecastDays);
            
            // Build forecast DTO for this category
            DemandForecastDTO categoryForecast = DemandForecastDTO.builder()
                    .categoryName(category)
                    .generatedAt(LocalDateTime.now())
                    .forecastDays(forecastDays)
                    .forecastValues(forecast)
                    .forecastDates(forecastDates)
                    .lowerBound(confidenceIntervals.get("lower"))
                    .upperBound(confidenceIntervals.get("upper"))
                    .confidenceLevel(95.0)
                    .model(buildModelDTO(model, timeSeriesData.size()))
                    .build();
            
            categoryForecasts.put(category, categoryForecast);
        }
        
        // Calculate aggregate forecast for the warehouse
        List<Double> aggregateForecast = calculateAggregateForecast(categoryForecasts, forecastDays);
        
        // Build and return the warehouse forecast
        Map<String, Object> warehouseForecast = new HashMap<>();
        warehouseForecast.put("warehouseId", warehouseId);
        warehouseForecast.put("generatedAt", LocalDateTime.now());
        warehouseForecast.put("forecastDays", forecastDays);
        warehouseForecast.put("forecastDates", generateForecastDates(forecastDays));
        warehouseForecast.put("aggregateForecast", aggregateForecast);
        warehouseForecast.put("categoryForecasts", categoryForecasts);
        
        return warehouseForecast;
    }
    
    /**
     * Prepare time series data from historical movement data
     *
     * @param historicalData the historical movement data
     * @return list of daily demand values
     */
    private List<Double> prepareTimeSeriesData(List<Map<String, Object>> historicalData) {
        // In a real implementation, this would process and aggregate the historical data
        // For now, we'll generate sample data
        
        List<Double> timeSeriesData = new ArrayList<>();
        
        // Use historical data if available, otherwise generate sample data
        if (historicalData != null && !historicalData.isEmpty()) {
            for (Map<String, Object> dataPoint : historicalData) {
                Double quantity = Double.valueOf(dataPoint.get("quantity").toString());
                timeSeriesData.add(quantity);
            }
        } else {
            // Generate sample data with trend and seasonality
            Random random = new Random();
            double baseValue = 100.0;
            double trend = 0.5; // Increasing trend
            
            for (int i = 0; i < 90; i++) {
                // Add trend
                double value = baseValue + (trend * i);
                
                // Add weekly seasonality
                double seasonality = 20.0 * Math.sin(2 * Math.PI * i / 7.0);
                
                // Add noise
                double noise = random.nextGaussian() * 10.0;
                
                timeSeriesData.add(Math.max(0, value + seasonality + noise));
            }
        }
        
        return timeSeriesData;
    }
    
    /**
     * Select and train a forecasting model
     *
     * @param modelType the model type
     * @param timeSeriesData the time series data
     * @return the trained forecast model
     */
    private ForecastModel selectForecastModel(String modelType, List<Double> timeSeriesData) {
        // In a real implementation, this would select and train an appropriate model
        // For now, we'll create a placeholder model
        
        ForecastModel model = new ForecastModel();
        model.setType(modelType != null ? modelType : "ARIMA");
        model.setParameters(Map.of(
                "p", 1,
                "d", 1,
                "q", 1,
                "seasonalPeriod", 7
        ));
        model.setAccuracy(Map.of(
                "mae", 8.5,
                "rmse", 12.3,
                "mape", 5.2
        ));
        
        return model;
    }
    
    /**
     * Generate forecast using the trained model
     *
     * @param model the forecast model
     * @param timeSeriesData the time series data
     * @param forecastDays number of days to forecast
     * @return list of forecasted values
     */
    private List<Double> generateForecast(ForecastModel model, List<Double> timeSeriesData, int forecastDays) {
        // In a real implementation, this would use the trained model to generate forecasts
        // For now, we'll create a placeholder forecast
        
        List<Double> forecast = new ArrayList<>();
        Random random = new Random();
        
        // Use the last value as a starting point
        double lastValue = timeSeriesData.get(timeSeriesData.size() - 1);
        double trend = calculateTrend(timeSeriesData);
        
        for (int i = 0; i < forecastDays; i++) {
            // Add trend
            double value = lastValue + (trend * (i + 1));
            
            // Add weekly seasonality
            double seasonality = 20.0 * Math.sin(2 * Math.PI * i / 7.0);
            
            // Add reduced noise (forecasts should be smoother than historical data)
            double noise = random.nextGaussian() * 5.0;
            
            forecast.add(Math.max(0, value + seasonality + noise));
        }
        
        return forecast;
    }
    
    /**
     * Calculate trend from time series data
     *
     * @param timeSeriesData the time series data
     * @return the calculated trend
     */
    private double calculateTrend(List<Double> timeSeriesData) {
        // Simple linear trend calculation
        int n = timeSeriesData.size();
        if (n <= 1) {
            return 0.0;
        }
        
        // Use the last 14 days to calculate trend
        int startIdx = Math.max(0, n - 14);
        double startValue = timeSeriesData.get(startIdx);
        double endValue = timeSeriesData.get(n - 1);
        
        return (endValue - startValue) / (n - startIdx);
    }
    
    /**
     * Calculate confidence intervals for the forecast
     *
     * @param forecast the forecast values
     * @param model the forecast model
     * @return map containing lower and upper bounds
     */
    private Map<String, List<Double>> calculateConfidenceIntervals(List<Double> forecast, ForecastModel model) {
        // In a real implementation, this would calculate proper confidence intervals
        // For now, we'll use a simple approach based on model accuracy
        
        double rmse = (double) model.getAccuracy().getOrDefault("rmse", 10.0);
        double z = 1.96; // 95% confidence level
        double margin = rmse * z;
        
        List<Double> lowerBound = new ArrayList<>();
        List<Double> upperBound = new ArrayList<>();
        
        for (Double value : forecast) {
            lowerBound.add(Math.max(0, value - margin));
            upperBound.add(value + margin);
        }
        
        Map<String, List<Double>> intervals = new HashMap<>();
        intervals.put("lower", lowerBound);
        intervals.put("upper", upperBound);
        
        return intervals;
    }
    
    /**
     * Generate dates for the forecast period
     *
     * @param forecastDays number of days to forecast
     * @return list of forecast dates
     */
    private List<LocalDate> generateForecastDates(int forecastDays) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate startDate = LocalDate.now().plusDays(1);
        
        for (int i = 0; i < forecastDays; i++) {
            dates.add(startDate.plusDays(i));
        }
        
        return dates;
    }
    
    /**
     * Build model DTO from forecast model
     *
     * @param model the forecast model
     * @param trainingDataSize size of training data
     * @return the model DTO
     */
    private ForecastModelDTO buildModelDTO(ForecastModel model, int trainingDataSize) {
        return ForecastModelDTO.builder()
                .type(model.getType())
                .parameters(model.getParameters())
                .accuracy(model.getAccuracy())
                .trainingDataPoints(trainingDataSize)
                .build();
    }
    
    /**
     * Calculate aggregate forecast from category forecasts
     *
     * @param categoryForecasts map of category forecasts
     * @param forecastDays number of days to forecast
     * @return list of aggregated forecast values
     */
    private List<Double> calculateAggregateForecast(Map<String, DemandForecastDTO> categoryForecasts, int forecastDays) {
        List<Double> aggregateForecast = new ArrayList<>(Collections.nCopies(forecastDays, 0.0));
        
        // Sum up forecasts from all categories
        for (DemandForecastDTO forecast : categoryForecasts.values()) {
            List<Double> values = forecast.getForecastValues();
            
            for (int i = 0; i < Math.min(forecastDays, values.size()); i++) {
                aggregateForecast.set(i, aggregateForecast.get(i) + values.get(i));
            }
        }
        
        return aggregateForecast;
    }
} 
