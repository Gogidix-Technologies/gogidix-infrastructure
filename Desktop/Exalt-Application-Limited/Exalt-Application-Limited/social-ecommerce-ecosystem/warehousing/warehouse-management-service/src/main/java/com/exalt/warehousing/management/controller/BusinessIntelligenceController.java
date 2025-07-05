package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.etl.EtlBatchDTO;
import com.exalt.warehousing.management.dto.etl.EtlResultDTO;
import com.exalt.warehousing.management.service.BusinessIntelligenceService;
import com.exalt.warehousing.management.service.DataLakeEtlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller for Business Intelligence reporting endpoints
 */
@RestController
@RequestMapping("/api/v1/warehousing/bi")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Business Intelligence", description = "Business intelligence reporting endpoints")
public class BusinessIntelligenceController {

    private final BusinessIntelligenceService biService;
    private final DataLakeEtlService etlService;

    /**
     * Get warehouse efficiency metrics
     *
     * @param warehouseId the warehouse ID
     * @return warehouse efficiency metrics
     */
    @GetMapping("/warehouse/{warehouseId}/efficiency")
    @Operation(summary = "Get warehouse efficiency metrics", 
               description = "Retrieves various efficiency metrics for a specific warehouse")
    public ResponseEntity<Map<String, Object>> getWarehouseEfficiencyMetrics(
            @PathVariable UUID warehouseId) {
        log.info("Getting efficiency metrics for warehouse: {}", warehouseId);
        return ResponseEntity.ok(biService.getWarehouseEfficiencyMetrics(warehouseId));
    }

    /**
     * Get staff performance dashboard data
     *
     * @param warehouseId the warehouse ID
     * @return staff performance metrics
     */
    @GetMapping("/staff-performance/{warehouseId}")
    @Operation(summary = "Get staff performance dashboard data",
               description = "Retrieves performance metrics for staff in a specific warehouse")
    public ResponseEntity<Map<String, Object>> getStaffPerformanceData(
            @PathVariable UUID warehouseId) {
        log.info("Getting staff performance data for warehouse: {}", warehouseId);
        return ResponseEntity.ok(biService.getStaffPerformanceData(warehouseId));
    }

    /**
     * Get inventory turnover metrics
     *
     * @param warehouseId the warehouse ID
     * @param timeRangeInDays time range in days (optional, defaults to 30)
     * @return inventory turnover metrics
     */
    @GetMapping("/inventory-turnover/{warehouseId}")
    @Operation(summary = "Get inventory turnover metrics",
               description = "Retrieves inventory turnover metrics for a specific warehouse")
    public ResponseEntity<Map<String, Object>> getInventoryTurnoverMetrics(
            @PathVariable UUID warehouseId,
            @RequestParam(required = false, defaultValue = "30") Integer timeRangeInDays) {
        log.info("Getting inventory turnover metrics for warehouse: {} over {} days", 
                 warehouseId, timeRangeInDays);
        return ResponseEntity.ok(biService.getInventoryTurnoverMetrics(warehouseId, timeRangeInDays));
    }

    /**
     * Get order fulfillment analytics
     *
     * @param warehouseId the warehouse ID
     * @param timeRangeInDays time range in days (optional, defaults to 30)
     * @return order fulfillment analytics
     */
    @GetMapping("/order-fulfillment/{warehouseId}")
    @Operation(summary = "Get order fulfillment analytics",
               description = "Retrieves order fulfillment analytics for a specific warehouse")
    public ResponseEntity<Map<String, Object>> getOrderFulfillmentAnalytics(
            @PathVariable UUID warehouseId,
            @RequestParam(required = false, defaultValue = "30") Integer timeRangeInDays) {
        log.info("Getting order fulfillment analytics for warehouse: {} over {} days", 
                 warehouseId, timeRangeInDays);
        return ResponseEntity.ok(biService.getOrderFulfillmentAnalytics(warehouseId, timeRangeInDays));
    }

    /**
     * Manually trigger ETL for warehouse statistics
     *
     * @return ETL result
     */
    @PostMapping("/etl/warehouse-statistics")
    @Operation(summary = "Manually trigger ETL for warehouse statistics",
               description = "Manually extracts and sends warehouse statistics to the data lake")
    public ResponseEntity<EtlResultDTO> triggerWarehouseStatisticsEtl() {
        log.info("Manually triggering warehouse statistics ETL");
        return ResponseEntity.ok(etlService.extractAndSendWarehouseStatistics());
    }

    /**
     * Manually trigger ETL for task completion data
     *
     * @return ETL result
     */
    @PostMapping("/etl/task-completion")
    @Operation(summary = "Manually trigger ETL for task completion data",
               description = "Manually extracts and sends task completion data to the data lake")
    public ResponseEntity<EtlResultDTO> triggerTaskCompletionEtl() {
        log.info("Manually triggering task completion ETL");
        return ResponseEntity.ok(etlService.extractAndSendTaskCompletionData());
    }

    /**
     * Send custom data batch to the data lake
     *
     * @param batch the data batch to send
     * @return ETL result
     */
    @PostMapping("/etl/custom-batch")
    @Operation(summary = "Send custom data batch to the data lake",
               description = "Sends a custom batch of data to the data lake")
    public ResponseEntity<EtlResultDTO> sendCustomDataBatch(@RequestBody EtlBatchDTO batch) {
        log.info("Sending custom data batch to data lake: {}", batch.getDescription());
        return ResponseEntity.ok(etlService.sendBatch(batch));
    }
} 
