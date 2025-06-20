package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for batch processing results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchProcessingResultDTO {
    
    /**
     * Unique identifier for the batch processing job
     */
    private UUID batchId;
    
    /**
     * Warehouse ID where this batch is processed
     */
    private UUID warehouseId;
    
    /**
     * Zone ID if this batch is zone-specific
     */
    private UUID zoneId;
    
    /**
     * Number of items in the batch
     */
    private Integer itemCount;
    
    /**
     * Number of zones involved in processing
     */
    private Integer zoneCount;
    
    /**
     * Number of batches created when grouping tasks
     */
    private Integer batchCount;
    
    /**
     * Total distance for all picking paths in meters
     */
    private Double totalDistance;
    
    /**
     * Total estimated time for completion in minutes
     */
    private Double totalEstimatedTimeMinutes;
    
    /**
     * The optimized picking paths generated for this batch
     */
    private List<PickingPathDTO> pickingPaths;
    
    /**
     * IDs of warehouse tasks created for this batch
     */
    private List<UUID> createdTaskIds;
    
    /**
     * Creation timestamp of this batch processing result
     */
    private LocalDateTime createdAt;
}
