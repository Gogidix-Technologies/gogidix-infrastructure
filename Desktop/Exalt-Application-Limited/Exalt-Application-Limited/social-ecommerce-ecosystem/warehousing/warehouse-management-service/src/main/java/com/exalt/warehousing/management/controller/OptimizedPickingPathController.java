package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.PickingPathDTO;
import com.exalt.warehousing.management.service.OptimizedPickingPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for optimized picking path operations
 */
@RestController
@RequestMapping("/api/v1/picking-paths")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Picking Path", description = "Optimized picking path operations")
public class OptimizedPickingPathController {

    private final OptimizedPickingPathService optimizedPickingPathService;

    /**
     * Generate an optimized picking path using the specified algorithm
     *
     * @param warehouseId the warehouse ID
     * @param locationIds the list of item IDs to pick (renamed for interface compatibility)
     * @return the optimized picking path
     */
    @PostMapping("/optimize")
    @Operation(summary = "Generate an optimized picking path", 
              description = "Generates an optimized picking path for a set of items")
    public ResponseEntity<PickingPathDTO> generateOptimizedPickingPath(
            @RequestParam UUID warehouseId,
            @RequestBody List<UUID> locationIds) {
        
        log.info("Generating optimized picking path for {} items", locationIds.size());
        PickingPathDTO pickingPath = optimizedPickingPathService.generateOptimizedPickingPath(
                warehouseId, locationIds);
        
        return ResponseEntity.ok(pickingPath);
    }

    /**
     * Generate an optimized picking path for a warehouse zone
     *
     * @param zoneId the zone ID
     * @param locationIds the list of item IDs to pick
     * @return the optimized picking path
     */
    @PostMapping("/optimize/zone")
    @Operation(summary = "Generate a zone-optimized picking path",
              description = "Generates an optimized picking path within a specific warehouse zone")
    public ResponseEntity<PickingPathDTO> generateZoneOptimizedPath(
            @RequestParam UUID zoneId,
            @RequestBody List<UUID> locationIds) {
        
        log.info("Generating zone-optimized picking path for zone: {}", zoneId);
        PickingPathDTO pickingPath = optimizedPickingPathService.generateZoneOptimizedPickingPath(zoneId, locationIds);
        
        return ResponseEntity.ok(pickingPath);
    }

    /*
     * Generate activity heatmap data for a warehouse
     * 
     * @param warehouseId the warehouse ID
     * @param timeframeDays number of days to include in the heatmap
     * @return map of location IDs to activity counts
     *
    @GetMapping("/heatmap")
    @Operation(summary = "Generate activity heatmap data",
              description = "Generates a heatmap of warehouse activity based on location usage")
    public ResponseEntity<Map<UUID, Integer>> generateActivityHeatmap(
            @RequestParam UUID warehouseId,
            @RequestParam(defaultValue = "30") int timeframeDays) {
        
        log.info("Generating activity heatmap for warehouse: {} over {} days", warehouseId, timeframeDays);
        Map<UUID, Integer> heatmap = optimizedPickingPathService.generateActivityHeatmap(warehouseId, timeframeDays);
        
        return ResponseEntity.ok(heatmap);
    }
    */

    /**
     * Compare results of different optimization algorithms
     *
     * @param warehouseId the warehouse ID
     * @param locationIds the list of item IDs to pick
     * @return a map of algorithm name to picking path
     */
    @PostMapping("/compare-algorithms")
    @Operation(summary = "Compare optimization algorithms",
              description = "Generates picking paths using different algorithms for comparison")
    public ResponseEntity<Map<String, PickingPathDTO>> compareOptimizationAlgorithms(
            @RequestParam UUID warehouseId,
            @RequestBody List<UUID> locationIds) {
        
        log.info("Comparing optimization algorithms for {} items", locationIds.size());
        
        // Generate paths using the interface methods
        Map<String, PickingPathDTO> results = new HashMap<>();
        
        // Get a basic path
        PickingPathDTO path = optimizedPickingPathService.generateOptimizedPickingPath(
                warehouseId, locationIds);
        results.put("DEFAULT", path);
        
        // In a real implementation, we would use multiple algorithm options
        // or have a service method that returns results for multiple algorithms
        
        return ResponseEntity.ok(results);
    }
} 
