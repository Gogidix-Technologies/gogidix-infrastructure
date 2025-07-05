package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.dto.PickingPathDTO;
import com.exalt.warehousing.management.dto.WarehouseTaskDTO;
import com.exalt.warehousing.management.mapper.LocationMapper;
import com.exalt.warehousing.management.mapper.WarehouseTaskMapper;
import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.service.LocationService;
import com.exalt.warehousing.management.service.OptimizedPickingPathService;
import com.exalt.warehousing.management.service.WarehouseTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for mobile-friendly APIs
 * Provides optimized endpoints for warehouse staff using mobile devices
 */
@RestController
@RequestMapping("/api/v1/mobile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile APIs", description = "Mobile-friendly APIs for warehouse staff")
public class MobileApiController {

    // Manually define logger since Lombok's @Slf4j might not be processed during compilation
    private static final Logger log = LoggerFactory.getLogger(MobileApiController.class);

    private final WarehouseTaskService taskService;
    private final LocationService locationService;
    private final OptimizedPickingPathService pickingPathService;
    private final WarehouseTaskMapper taskMapper;
    private final LocationMapper locationMapper;

    /**
     * Get tasks assigned to a staff member with minimal response size
     *
     * @param staffId the staff ID
     * @return list of assigned tasks optimized for mobile
     */
    @GetMapping("/staff/{staffId}/tasks")
    @Operation(summary = "Get staff tasks for mobile",
               description = "Retrieves tasks assigned to a staff member with minimal response size")
    public ResponseEntity<List<Map<String, Object>>> getStaffTasksForMobile(@PathVariable UUID staffId) {
        log.info("Getting mobile-optimized tasks for staff: {}", staffId);
        
        List<WarehouseTask> tasks = taskService.findTasksAssignedToStaff(staffId);
        
        // Create lightweight response for mobile
        List<Map<String, Object>> mobileTasks = tasks.stream()
                .map(task -> {
                    Map<String, Object> mobileTask = new HashMap<>();
                    mobileTask.put("id", task.getId());
                    mobileTask.put("type", task.getType().name());
                    mobileTask.put("status", task.getStatus().name());
                    mobileTask.put("priority", task.getPriority().getValue());
                    mobileTask.put("locationId", task.getLocationId());
                    mobileTask.put("description", task.getDescription());
                    // Include only essential metadata
                    if (task.getMetadata() != null) {
                        // Parse JSON metadata or handle as map
                        mobileTask.put("pickingPathId", extractFromMetadata(task.getMetadata(), "pickingPathId"));
                        mobileTask.put("itemCount", extractFromMetadata(task.getMetadata(), "itemCount"));
                    }
                    return mobileTask;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(mobileTasks);
    }

    /**
     * Get optimized picking path for mobile device
     *
     * @param pathId the picking path ID
     * @return optimized picking path for mobile
     */
    @GetMapping("/picking-path/{pathId}")
    @Operation(summary = "Get optimized picking path for mobile",
               description = "Retrieves an optimized picking path formatted for mobile devices")
    public ResponseEntity<Map<String, Object>> getPickingPathForMobile(@PathVariable UUID pathId) {
        log.info("Getting mobile-optimized picking path: {}", pathId);
        
        PickingPathDTO path = pickingPathService.findPickingPathById(pathId);
        
        // Create lightweight response optimized for mobile
        Map<String, Object> mobilePath = new HashMap<>();
        mobilePath.put("id", path.getId());
        mobilePath.put("totalDistance", path.getTotalDistance());
        mobilePath.put("estimatedTimeMinutes", path.getEstimatedTimeMinutes());
        
        // Format the path items for easy mobile consumption
        List<Map<String, Object>> pickItems = path.getPickItems().stream()
                .map(item -> {
                    Map<String, Object> pickItem = new HashMap<>();
                    pickItem.put("sequence", item.getSequence());
                    pickItem.put("locationId", item.getLocationId());
                    pickItem.put("locationCode", item.getLocationCode());
                    pickItem.put("sku", item.getSku());
                    pickItem.put("quantity", item.getQuantity());
                    return pickItem;
                })
                .collect(Collectors.toList());
        
        mobilePath.put("pickItems", pickItems);
        
        return ResponseEntity.ok(mobilePath);
    }

    /**
     * Update task status via mobile
     *
     * @param taskId the task ID
     * @param status the new status
     * @return the updated task
     */
    @PutMapping("/tasks/{taskId}/status")
    @Operation(summary = "Update task status via mobile",
               description = "Updates the status of a task from a mobile device")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable UUID taskId,
            @RequestParam TaskStatus status,
            @RequestParam(required = false) String note) {
        log.info("Updating task status via mobile API - task: {}, status: {}", taskId, status);
        
        WarehouseTaskDTO updatedTask = taskService.updateTaskStatus(taskId, status, note);
        
        // Create lightweight response for mobile
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", updatedTask.getId());
        result.put("status", updatedTask.getStatus().name());
        result.put("success", true);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }

    /**
     * Scan location barcode via mobile
     *
     * @param barcode the location barcode
     * @return location details
     */
    @GetMapping("/locations/scan")
    @Operation(summary = "Scan location barcode",
               description = "Retrieves location details by scanning a barcode")
    public ResponseEntity<Map<String, Object>> scanLocationBarcode(@RequestParam String barcode) {
        log.info("Scanning location barcode via mobile API: {}", barcode);
        
        LocationDTO location = locationService.findLocationByBarcode(barcode);
        
        // Create lightweight response for mobile
        Map<String, Object> result = new HashMap<>();
        result.put("locationId", location.getId());
        result.put("locationCode", location.getLocationCode());
        result.put("status", location.getStatus());
        result.put("zoneId", location.getZoneId());
        result.put("warehouseId", location.getWarehouseId());
        
        // Include relevant location properties
        if (location.getProperties() != null) {
            result.put("locationType", location.getProperties().get("locationType"));
            result.put("maxWeight", location.getProperties().get("maxWeight"));
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Get offline data bundle for mobile app
     *
     * @param warehouseId the warehouse ID
     * @param staffId the staff ID
     * @return data bundle for offline use
     */
    @GetMapping("/offline-data")
    @Operation(summary = "Get offline data bundle",
               description = "Retrieves a bundle of data for offline use in the mobile app")
    public ResponseEntity<Map<String, Object>> getOfflineDataBundle(
            @RequestParam UUID warehouseId,
            @RequestParam UUID staffId) {
        log.info("Getting offline data bundle for warehouse: {} and staff: {}", warehouseId, staffId);
        
        Map<String, Object> offlineBundle = new HashMap<>();
        
        // Add staff tasks
        List<WarehouseTask> tasks = taskService.findTasksAssignedToStaff(staffId);
        List<Map<String, Object>> mobileTasks = tasks.stream()
                .map(task -> {
                    Map<String, Object> mobileTask = new HashMap<>();
                    mobileTask.put("id", task.getId());
                    mobileTask.put("type", task.getType().name());
                    mobileTask.put("status", task.getStatus().name());
                    mobileTask.put("priority", task.getPriority().getValue());
                    mobileTask.put("locationId", task.getLocationId());
                    mobileTask.put("description", task.getDescription());
                    return mobileTask;
                })
                .collect(Collectors.toList());
        offlineBundle.put("tasks", mobileTasks);
        
        // Add warehouse layout for navigation
        List<LocationDTO> zoneLocations = locationService.findLocationsByWarehouseId(warehouseId);
        List<Map<String, Object>> mobileLocations = zoneLocations.stream()
                .map(location -> {
                    Map<String, Object> mobileLocation = new HashMap<>();
                    mobileLocation.put("id", location.getId());
                    mobileLocation.put("code", location.getLocationCode());
                    mobileLocation.put("barcode", location.getBarcode());
                    mobileLocation.put("aisle", location.getAisle());
                    mobileLocation.put("rack", location.getRack());
                    mobileLocation.put("level", location.getLevel());
                    mobileLocation.put("position", location.getPosition());
                    mobileLocation.put("zoneId", location.getZoneId());
                    return mobileLocation;
                })
                .collect(Collectors.toList());
        offlineBundle.put("locations", mobileLocations);
        
        // Add timestamps for cache validation
        offlineBundle.put("timestamp", System.currentTimeMillis());
        offlineBundle.put("expiresAt", System.currentTimeMillis() + (8 * 60 * 60 * 1000)); // 8 hours
        
        return ResponseEntity.ok(offlineBundle);
    }

    /**
     * Synchronize local data from mobile app
     *
     * @param staffId the staff ID
     * @param syncData the data to synchronize
     * @return synchronization result
     */
    @PostMapping("/sync")
    @Operation(summary = "Synchronize mobile app data",
               description = "Synchronizes local data from the mobile app with the server")
    public ResponseEntity<Map<String, Object>> synchronizeData(
            @RequestParam UUID staffId,
            @RequestBody Map<String, Object> syncData) {
        log.info("Synchronizing data from mobile app for staff: {}", staffId);
        
        // Process task updates from offline mode
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> taskUpdates = (List<Map<String, Object>>) syncData.get("taskUpdates");
        
        if (taskUpdates != null) {
            for (Map<String, Object> update : taskUpdates) {
                UUID taskId = UUID.fromString(update.get("taskId").toString());
                TaskStatus status = TaskStatus.valueOf(update.get("status").toString());
                String note = update.get("note") != null ? update.get("note").toString() : null;
                
                try {
                    taskService.updateTaskStatus(taskId, status, note);
                } catch (Exception e) {
                    log.error("Error updating task {} during sync: {}", taskId, e.getMessage());
                }
            }
        }
        
        // Return sync result
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("syncedAt", System.currentTimeMillis());
        result.put("pendingTasksCount", taskService.countPendingTasksForStaff(staffId));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Helper method to extract values from metadata string (JSON or properties format)
     */
    private String extractFromMetadata(String metadata, String key) {
        if (metadata == null || metadata.trim().isEmpty()) {
            return null;
        }
        
        // Simple key-value extraction - in a real implementation, you'd parse JSON properly
        try {
            if (metadata.contains("\"" + key + "\"")) {
                int startIndex = metadata.indexOf("\"" + key + "\"");
                int valueStart = metadata.indexOf(":", startIndex);
                if (valueStart != -1) {
                    valueStart = metadata.indexOf("\"", valueStart) + 1;
                    int valueEnd = metadata.indexOf("\"", valueStart);
                    if (valueEnd != -1) {
                        return metadata.substring(valueStart, valueEnd);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error extracting {} from metadata: {}", key, e.getMessage());
        }
        
        return null;
    }
}
