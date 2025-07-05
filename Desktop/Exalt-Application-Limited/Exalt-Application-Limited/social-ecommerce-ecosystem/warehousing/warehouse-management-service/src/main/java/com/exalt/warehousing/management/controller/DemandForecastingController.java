package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.DemandForecastDTO;
import com.exalt.warehousing.management.service.DemandForecastingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller for demand forecasting operations
 */
@RestController
@RequestMapping("/api/v1/warehousing/analytics/forecasting")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Demand Forecasting", description = "Operations for demand forecasting using machine learning models")
public class DemandForecastingController {

    private final DemandForecastingService forecastingService;

    /**
     * Generate demand forecast for a product
     *
     * @param productId the product ID
     * @param forecastDays number of days to forecast
     * @param modelType the forecasting model type
     * @return the demand forecast
     */
    @GetMapping("/products/{productId}")
    @Operation(summary = "Generate product demand forecast",
               description = "Generates a demand forecast for a specific product using machine learning models",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Forecast generated successfully",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = DemandForecastDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Product not found"),
                   @ApiResponse(responseCode = "500", description = "Error generating forecast")
               })
    public ResponseEntity<DemandForecastDTO> generateProductDemandForecast(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "30") int forecastDays,
            @RequestParam(required = false) String modelType) {
        log.info("Generating {} day demand forecast for product {} using model type: {}", 
                forecastDays, productId, modelType);
        
        DemandForecastDTO forecast = forecastingService.generateProductDemandForecast(
                productId, forecastDays, modelType);
        
        return ResponseEntity.ok(forecast);
    }

    /**
     * Generate demand forecast for a warehouse
     *
     * @param warehouseId the warehouse ID
     * @param forecastDays number of days to forecast
     * @param modelType the forecasting model type
     * @return the warehouse demand forecast
     */
    @GetMapping("/warehouses/{warehouseId}")
    @Operation(summary = "Generate warehouse demand forecast",
               description = "Generates a demand forecast for an entire warehouse using machine learning models",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Forecast generated successfully"),
                   @ApiResponse(responseCode = "404", description = "Warehouse not found"),
                   @ApiResponse(responseCode = "500", description = "Error generating forecast")
               })
    public ResponseEntity<Map<String, Object>> generateWarehouseDemandForecast(
            @PathVariable UUID warehouseId,
            @RequestParam(defaultValue = "30") int forecastDays,
            @RequestParam(required = false) String modelType) {
        log.info("Generating {} day demand forecast for warehouse {} using model type: {}", 
                forecastDays, warehouseId, modelType);
        
        Map<String, Object> forecast = forecastingService.generateWarehouseDemandForecast(
                warehouseId, forecastDays, modelType);
        
        return ResponseEntity.ok(forecast);
    }

    /**
     * Compare different forecasting models for a product
     *
     * @param productId the product ID
     * @param forecastDays number of days to forecast
     * @return comparison of different forecasting models
     */
    @GetMapping("/products/{productId}/model-comparison")
    @Operation(summary = "Compare forecasting models",
               description = "Compares different forecasting models for a specific product",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Model comparison generated successfully"),
                   @ApiResponse(responseCode = "404", description = "Product not found"),
                   @ApiResponse(responseCode = "500", description = "Error comparing models")
               })
    public ResponseEntity<Map<String, DemandForecastDTO>> compareModelsForecastProduct(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "30") int forecastDays) {
        log.info("Comparing forecasting models for product {} with {} day forecast", 
                productId, forecastDays);
        
        // Generate forecasts using different models
        DemandForecastDTO arimaForecast = forecastingService.generateProductDemandForecast(
                productId, forecastDays, "ARIMA");
        
        DemandForecastDTO etsForecast = forecastingService.generateProductDemandForecast(
                productId, forecastDays, "ETS");
        
        DemandForecastDTO prophetForecast = forecastingService.generateProductDemandForecast(
                productId, forecastDays, "PROPHET");
        
        // Return comparison
        Map<String, DemandForecastDTO> comparison = Map.of(
                "ARIMA", arimaForecast,
                "ETS", etsForecast,
                "PROPHET", prophetForecast
        );
        
        return ResponseEntity.ok(comparison);
    }
} 
