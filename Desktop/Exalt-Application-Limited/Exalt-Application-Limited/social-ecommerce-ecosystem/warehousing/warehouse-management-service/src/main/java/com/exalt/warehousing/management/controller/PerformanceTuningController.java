package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.service.PerformanceTuningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for performance tuning and load testing operations
 */
@RestController
@RequestMapping("/api/v1/admin/performance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Performance Tuning", description = "Operations for performance tuning and load testing")
public class PerformanceTuningController {

    private final PerformanceTuningService performanceTuningService;

    /**
     * Get method performance metrics
     *
     * @return method performance metrics
     */
    @GetMapping("/metrics/methods")
    @Operation(summary = "Get method performance metrics",
               description = "Retrieves performance metrics for all monitored methods")
    public ResponseEntity<Map<String, Object>> getMethodPerformanceMetrics() {
        log.info("Getting method performance metrics");
        return ResponseEntity.ok(performanceTuningService.getMethodPerformanceMetrics());
    }

    /**
     * Get database connection pool statistics
     *
     * @return connection pool statistics
     */
    @GetMapping("/metrics/connection-pool")
    @Operation(summary = "Get connection pool statistics",
               description = "Retrieves database connection pool statistics")
    public ResponseEntity<Map<String, Object>> getConnectionPoolStats() {
        log.info("Getting connection pool statistics");
        return ResponseEntity.ok(performanceTuningService.getConnectionPoolStats());
    }

    /**
     * Get slow query statistics
     *
     * @return slow query statistics
     */
    @GetMapping("/metrics/slow-queries")
    @Operation(summary = "Get slow query statistics",
               description = "Retrieves statistics for slow database queries")
    public ResponseEntity<List<Map<String, Object>>> getSlowQueryStats() {
        log.info("Getting slow query statistics");
        return ResponseEntity.ok(performanceTuningService.getSlowQueryStats());
    }

    /**
     * Analyze database indexes
     *
     * @return index analysis results
     */
    @GetMapping("/analyze/indexes")
    @Operation(summary = "Analyze database indexes",
               description = "Analyzes database indexes and suggests improvements")
    public ResponseEntity<Map<String, Object>> analyzeIndexes() {
        log.info("Analyzing database indexes");
        return ResponseEntity.ok(performanceTuningService.analyzeIndexes());
    }

    /**
     * Run simulated load test
     *
     * @param concurrentUsers number of concurrent users to simulate
     * @param durationSeconds duration of the test in seconds
     * @return load test results
     */
    @PostMapping("/load-test/simulate")
    @Operation(summary = "Run simulated load test",
               description = "Runs a simulated load test to analyze system performance")
    public ResponseEntity<Map<String, Object>> runSimulatedLoadTest(
            @RequestParam(defaultValue = "10") int concurrentUsers,
            @RequestParam(defaultValue = "60") int durationSeconds) {
        log.info("Running simulated load test with {} concurrent users for {} seconds",
                concurrentUsers, durationSeconds);
        return ResponseEntity.ok(performanceTuningService.runSimulatedLoadTest(concurrentUsers, durationSeconds));
    }

    /**
     * Reset performance metrics
     *
     * @return confirmation message
     */
    @PostMapping("/metrics/reset")
    @Operation(summary = "Reset performance metrics",
               description = "Resets all collected performance metrics")
    public ResponseEntity<Map<String, String>> resetPerformanceMetrics() {
        log.info("Resetting performance metrics");
        performanceTuningService.resetPerformanceMetrics();
        return ResponseEntity.ok(Map.of("message", "Performance metrics have been reset"));
    }
} 
