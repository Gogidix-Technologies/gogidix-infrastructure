package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.dto.PickingPathDTO;
import com.exalt.warehousing.management.model.Zone;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for generating optimized picking paths for warehouse operations
 */
public interface OptimizedPickingPathService {

    /**
     * Generate an optimized picking path for a set of items
     *
     * @param warehouseId the warehouse ID
     * @param pickItems the list of item IDs to pick
     * @return the optimized picking path
     */
    PickingPathDTO generateOptimizedPickingPath(UUID warehouseId, List<UUID> pickItems);

    /**
     * Generate an optimized picking path for a set of items within a zone
     *
     * @param zoneId the zone ID
     * @param pickItems the list of item IDs to pick
     * @return the optimized picking path
     */
    PickingPathDTO generateZoneOptimizedPickingPath(UUID zoneId, List<UUID> pickItems);

    /**
     * Calculate the shortest path between a starting location and multiple destination locations
     *
     * @param startLocationId the starting location ID
     * @param destinationLocationIds the destination location IDs
     * @return an ordered list of locations representing the shortest path
     */
    List<LocationDTO> calculateShortestPath(UUID startLocationId, List<UUID> destinationLocationIds);

    /**
     * Calculate the shortest path between locations in a zone using a specific algorithm
     *
     * @param zone the zone
     * @param startLocationId the starting location ID
     * @param destinationLocationIds the destination location IDs
     * @param algorithm the algorithm to use
     * @return an ordered list of locations representing the shortest path
     */
    List<LocationDTO> calculateZoneShortestPath(Zone zone, UUID startLocationId, List<UUID> destinationLocationIds, String algorithm);

    /**
     * Optimize batch picking for multiple orders
     *
     * @param warehouseId the warehouse ID
     * @param orderIds the order IDs to include in batch picking
     * @return a list of optimized picking paths for batch picking
     */
    List<PickingPathDTO> optimizeBatchPicking(UUID warehouseId, List<UUID> orderIds);

    /**
     * Get picking time estimate for a picking path
     *
     * @param pickingPathId the picking path ID
     * @return the estimated time in minutes
     */
    double getPickingTimeEstimate(UUID pickingPathId);

    /**
     * Find a picking path by its ID
     *
     * @param pickingPathId the picking path ID
     * @return the picking path DTO
     */
    PickingPathDTO findPickingPathById(UUID pickingPathId);
}
