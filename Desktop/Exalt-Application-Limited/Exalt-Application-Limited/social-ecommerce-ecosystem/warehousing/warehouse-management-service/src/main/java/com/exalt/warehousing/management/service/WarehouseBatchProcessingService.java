package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.BatchItemDTO;
import com.exalt.warehousing.management.dto.BatchProcessingResultDTO;
import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.dto.PickingPathDTO;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.Priority;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.repository.WarehouseTaskRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling batch processing operations in the warehouse
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseBatchProcessingService {

    private final LocationRepository locationRepository;
    private final ZoneRepository zoneRepository;
    private final WarehouseTaskRepository taskRepository;
    private final OptimizedPickingPathService pickingPathService;
    private final ReferenceDataSyncService referenceDataSyncService;

    /**
     * Process a batch of items to optimize warehouse operations
     *
     * @param warehouseId the warehouse ID
     * @param batchItems list of items to process in batch
     * @return the batch processing results
     */
    @Transactional
    public BatchProcessingResultDTO processBatch(UUID warehouseId, List<BatchItemDTO> batchItems) {
        log.info("Processing batch of {} items for warehouse {}", batchItems.size(), warehouseId);
        
        // Group items by zone for zone-based processing
        Map<UUID, List<BatchItemDTO>> itemsByZone = groupItemsByZone(warehouseId, batchItems);
        
        // Process each zone batch
        List<PickingPathDTO> zonePaths = new ArrayList<>();
        for (Map.Entry<UUID, List<BatchItemDTO>> entry : itemsByZone.entrySet()) {
            UUID zoneId = entry.getKey();
            List<BatchItemDTO> zoneItems = entry.getValue();
            
            // Skip empty zones
            if (zoneItems.isEmpty()) {
                continue;
            }
            
            // Get item location IDs
            List<UUID> locationIds = zoneItems.stream()
                    .map(BatchItemDTO::getLocationId)
                    .collect(Collectors.toList());
            
            // Generate optimized picking path for this zone
            PickingPathDTO zonePath = pickingPathService.generateZoneOptimizedPickingPath(zoneId, locationIds);
            zonePaths.add(zonePath);
        }
        
        // Calculate overall metrics
        double totalDistance = zonePaths.stream()
                .mapToDouble(PickingPathDTO::getTotalDistance)
                .sum();
        
        double totalEstimatedTime = zonePaths.stream()
                .mapToDouble(PickingPathDTO::getEstimatedTimeMinutes)
                .sum();
        
        // Create tasks for each zone path
        List<WarehouseTask> tasks = createTasksForZonePaths(warehouseId, zonePaths);
        
        return BatchProcessingResultDTO.builder()
                .batchId(UUID.randomUUID())
                .warehouseId(warehouseId)
                .itemCount(batchItems.size())
                .zoneCount(itemsByZone.size())
                .totalDistance(totalDistance)
                .totalEstimatedTimeMinutes(totalEstimatedTime)
                .pickingPaths(zonePaths)
                .createdTaskIds(tasks.stream().map(WarehouseTask::getId).collect(Collectors.toList()))
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Optimize multi-order fulfillment to minimize picking time
     *
     * @param warehouseId the warehouse ID
     * @param orderIds list of order IDs to fulfill
     * @return the batch processing results
     */
    @Transactional
    public BatchProcessingResultDTO optimizeMultiOrderFulfillment(UUID warehouseId, List<UUID> orderIds) {
        log.info("Optimizing multi-order fulfillment for {} orders in warehouse {}", orderIds.size(), warehouseId);
        
        // Get picking paths optimized for batch picking
        List<PickingPathDTO> pickingPaths = pickingPathService.optimizeBatchPicking(warehouseId, orderIds);
        
        // Create tasks for each picking path
        List<WarehouseTask> tasks = createTasksForPickingPaths(warehouseId, pickingPaths);
        
        // Calculate overall metrics
        double totalDistance = pickingPaths.stream()
                .mapToDouble(PickingPathDTO::getTotalDistance)
                .sum();
        
        double totalEstimatedTime = pickingPaths.stream()
                .mapToDouble(PickingPathDTO::getEstimatedTimeMinutes)
                .sum();
        
        return BatchProcessingResultDTO.builder()
                .batchId(UUID.randomUUID())
                .warehouseId(warehouseId)
                .itemCount(orderIds.size())
                .zoneCount(pickingPaths.size())
                .totalDistance(totalDistance)
                .totalEstimatedTimeMinutes(totalEstimatedTime)
                .pickingPaths(pickingPaths)
                .createdTaskIds(tasks.stream().map(WarehouseTask::getId).collect(Collectors.toList()))
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create zone-based task batches to optimize worker movements
     *
     * @param warehouseId the warehouse ID
     * @param zoneId the zone ID
     * @param maxTasksPerBatch maximum number of tasks per batch
     * @return the batch processing results
     */
    @Transactional
    public BatchProcessingResultDTO createZoneTaskBatches(UUID warehouseId, UUID zoneId, int maxTasksPerBatch) {
        log.info("Creating zone task batches for zone {} in warehouse {}", zoneId, warehouseId);
        
        // Verify the zone exists
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + zoneId));
        
        // Find pending tasks in the zone
        List<WarehouseTask> pendingTasks = taskRepository.findByZoneIdAndStatus(zoneId, TaskStatus.PENDING);
        
        if (pendingTasks.isEmpty()) {
            log.info("No pending tasks found in zone {}", zoneId);
            return BatchProcessingResultDTO.builder()
                    .batchId(UUID.randomUUID())
                    .warehouseId(warehouseId)
                    .zoneId(zoneId)
                    .itemCount(0)
                    .zoneCount(1)
                    .totalDistance(0.0)
                    .totalEstimatedTimeMinutes(0.0)
                    .pickingPaths(Collections.emptyList())
                    .createdTaskIds(Collections.emptyList())
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        
        // Group tasks into batches
        List<List<WarehouseTask>> taskBatches = new ArrayList<>();
        List<WarehouseTask> currentBatch = new ArrayList<>();
        
        for (WarehouseTask task : pendingTasks) {
            currentBatch.add(task);
            
            if (currentBatch.size() >= maxTasksPerBatch) {
                taskBatches.add(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }
        }
        
        // Add any remaining tasks
        if (!currentBatch.isEmpty()) {
            taskBatches.add(currentBatch);
        }
        
        // For each batch, optimize the order of tasks
        List<PickingPathDTO> optimizedPaths = new ArrayList<>();
        List<UUID> allTaskIds = new ArrayList<>();
        
        for (List<WarehouseTask> batch : taskBatches) {
            // Extract location IDs for the tasks
            List<UUID> locationIds = batch.stream()
                    .map(WarehouseTask::getLocationId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            // Skip if no locations
            if (locationIds.isEmpty()) {
                continue;
            }
            
            // Generate optimized path
            PickingPathDTO path = pickingPathService.generateZoneOptimizedPickingPath(zoneId, locationIds);
            optimizedPaths.add(path);
            
            // Add task IDs to the list
            allTaskIds.addAll(batch.stream().map(WarehouseTask::getId).collect(Collectors.toList()));
        }
        
        // Calculate overall metrics
        double totalDistance = optimizedPaths.stream()
                .mapToDouble(PickingPathDTO::getTotalDistance)
                .sum();
        
        double totalEstimatedTime = optimizedPaths.stream()
                .mapToDouble(PickingPathDTO::getEstimatedTimeMinutes)
                .sum();
        
        return BatchProcessingResultDTO.builder()
                .batchId(UUID.randomUUID())
                .warehouseId(warehouseId)
                .zoneId(zoneId)
                .itemCount(pendingTasks.size())
                .zoneCount(1)
                .batchCount(taskBatches.size())
                .totalDistance(totalDistance)
                .totalEstimatedTimeMinutes(totalEstimatedTime)
                .pickingPaths(optimizedPaths)
                .createdTaskIds(allTaskIds)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Group items by zone for optimized batch processing
     *
     * @param warehouseId the warehouse ID
     * @param items the batch items
     * @return map of zone ID to batch items
     */
    private Map<UUID, List<BatchItemDTO>> groupItemsByZone(UUID warehouseId, List<BatchItemDTO> items) {
        Map<UUID, List<BatchItemDTO>> itemsByZone = new HashMap<>();
        
        for (BatchItemDTO item : items) {
            UUID locationId = item.getLocationId();
            
            if (locationId == null) {
                log.warn("Skipping item with null location ID: {}", item);
                continue;
            }
            
            // Get the location to find its zone
            Optional<Location> locationOpt = locationRepository.findById(locationId.toString());
            
            if (locationOpt.isPresent()) {
                Location location = locationOpt.get();
                UUID zoneId = (UUID)location.getZone();
                
                // Initialize the list if needed
                itemsByZone.computeIfAbsent(zoneId, k -> new ArrayList<>());
                
                // Add the item to the zone's list
                itemsByZone.get(zoneId).add(item);
            } else {
                log.warn("Location not found for item: {}", item);
            }
        }
        
        return itemsByZone;
    }
    
    /**
     * Create warehouse tasks for zone picking paths
     *
     * @param warehouseId the warehouse ID
     * @param paths the picking paths
     * @return list of created tasks
     */
    private List<WarehouseTask> createTasksForZonePaths(UUID warehouseId, List<PickingPathDTO> paths) {
        List<WarehouseTask> tasks = new ArrayList<>();
        
        for (PickingPathDTO path : paths) {
            WarehouseTask task = WarehouseTask.builder()
                    .warehouseId(warehouseId)
                    .zoneId(path.getZoneId())
                    .status(TaskStatus.PENDING)
                    .description("Batch picking task for zone " + path.getZoneId())
                    .priority(Priority.MEDIUM)
                    .estimatedDurationMinutes((int) Math.ceil(path.getEstimatedTimeMinutes()))
                    .referenceId(path.getId())
                    .referenceType("PICKING_PATH")
                    .properties("{" +
                            "\"pickingPathId\":\"" + path.getId().toString() + "\"," +
                            "\"totalDistance\":\"" + String.valueOf(path.getTotalDistance()) + "\"," +
                            "\"itemCount\":\"" + String.valueOf(path.getPickItems() != null ? path.getPickItems().size() : 0) + "\"" +
                            "}")
                    .build();
            
            tasks.add(taskRepository.save(task));
        }
        
        return tasks;
    }
    
    /**
     * Create warehouse tasks for picking paths
     *
     * @param warehouseId the warehouse ID
     * @param paths the picking paths
     * @return list of created tasks
     */
    private List<WarehouseTask> createTasksForPickingPaths(UUID warehouseId, List<PickingPathDTO> paths) {
        return createTasksForZonePaths(warehouseId, paths); // Reuse the same logic
    }
} 
