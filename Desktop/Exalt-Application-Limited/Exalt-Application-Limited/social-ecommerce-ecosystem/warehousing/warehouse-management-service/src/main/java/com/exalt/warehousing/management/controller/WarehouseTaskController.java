package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.WarehouseTaskDTO;
import com.exalt.warehousing.management.model.Priority;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.service.WarehouseTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for warehouse task operations
 */
@RestController
@RequestMapping("/warehouse-tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Task Management", description = "API for warehouse task operations")
public class WarehouseTaskController {
    
    private final WarehouseTaskService warehouseTaskService;
    
    @GetMapping
    @Operation(summary = "Get all warehouse tasks", 
            description = "Returns a list of all warehouse tasks in the system")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getAllTasks() {
        log.debug("REST request to get all warehouse tasks");
        return ResponseEntity.ok(warehouseTaskService.findAll());
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get all warehouse tasks with pagination", 
            description = "Returns a paginated list of all warehouse tasks in the system")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<Page<WarehouseTaskDTO>> getAllTasksPaginated(Pageable pageable) {
        log.debug("REST request to get a page of warehouse tasks");
        return ResponseEntity.ok(warehouseTaskService.findAll(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse task by ID", 
            description = "Returns a warehouse task based on ID")
    @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<WarehouseTaskDTO> getTaskById(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id) {
        log.debug("REST request to get warehouse task by id: {}", id);
        return ResponseEntity.ok(warehouseTaskService.findById(id));
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get warehouse tasks by warehouse", 
            description = "Returns tasks in a specific warehouse")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByWarehouse(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId) {
        log.debug("REST request to get warehouse tasks by warehouse id: {}", warehouseId);
        return ResponseEntity.ok(warehouseTaskService.findByWarehouseId(warehouseId));
    }
    
    @GetMapping("/warehouse/{warehouseId}/paginated")
    @Operation(summary = "Get warehouse tasks by warehouse with pagination", 
            description = "Returns a paginated list of tasks in a specific warehouse")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<Page<WarehouseTaskDTO>> getTasksByWarehousePaginated(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            Pageable pageable) {
        log.debug("REST request to get a page of warehouse tasks by warehouse id: {}", warehouseId);
        return ResponseEntity.ok(warehouseTaskService.findByWarehouseId(warehouseId, pageable));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get warehouse tasks by status", 
            description = "Returns tasks with a specific status")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByStatus(
            @Parameter(description = "Task status", required = true)
            @PathVariable("status") TaskStatus status) {
        log.debug("REST request to get warehouse tasks by status: {}", status);
        return ResponseEntity.ok(warehouseTaskService.findByStatus(status));
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Get warehouse tasks by type", 
            description = "Returns tasks of a specific type")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByType(
            @Parameter(description = "Task type", required = true)
            @PathVariable("type") TaskType type) {
        log.debug("REST request to get warehouse tasks by type: {}", type);
        return ResponseEntity.ok(warehouseTaskService.findByType(type));
    }
    
    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get warehouse tasks by priority", 
            description = "Returns tasks with a specific priority")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByPriority(
            @Parameter(description = "Task priority", required = true)
            @PathVariable("priority") Priority priority) {
        log.debug("REST request to get warehouse tasks by priority: {}", priority);
        return ResponseEntity.ok(warehouseTaskService.findByPriority(priority));
    }
    
    @GetMapping("/assigned-to/{staffId}")
    @Operation(summary = "Get warehouse tasks assigned to staff", 
            description = "Returns tasks assigned to a specific staff member")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByAssignedTo(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("staffId") UUID staffId) {
        log.debug("REST request to get warehouse tasks assigned to staff id: {}", staffId);
        return ResponseEntity.ok(warehouseTaskService.findByAssignedTo(staffId));
    }
    
    @GetMapping("/active-tasks/{staffId}")
    @Operation(summary = "Get active warehouse tasks for staff", 
            description = "Returns active tasks (assigned or in progress) for a specific staff member")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getActiveTasksForStaff(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("staffId") UUID staffId) {
        log.debug("REST request to get active warehouse tasks for staff id: {}", staffId);
        return ResponseEntity.ok(warehouseTaskService.findActiveTasksForStaff(staffId));
    }
    
    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "Get warehouse tasks by zone", 
            description = "Returns tasks in a specific zone")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByZone(
            @Parameter(description = "Zone ID", required = true)
            @PathVariable("zoneId") UUID zoneId) {
        log.debug("REST request to get warehouse tasks by zone id: {}", zoneId);
        return ResponseEntity.ok(warehouseTaskService.findByZoneId(zoneId));
    }
    
    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get warehouse tasks by location", 
            description = "Returns tasks at a specific location")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksByLocation(
            @Parameter(description = "Location ID", required = true)
            @PathVariable("locationId") UUID locationId) {
        log.debug("REST request to get warehouse tasks by location id: {}", locationId);
        return ResponseEntity.ok(warehouseTaskService.findByLocationId(locationId));
    }
    
    @GetMapping("/uncompleted")
    @Operation(summary = "Get uncompleted warehouse tasks", 
            description = "Returns tasks that are not in COMPLETED or CANCELLED status")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getUncompletedTasks() {
        log.debug("REST request to get uncompleted warehouse tasks");
        return ResponseEntity.ok(warehouseTaskService.findUncompletedTasks());
    }
    
    @GetMapping("/unassigned")
    @Operation(summary = "Get unassigned warehouse tasks", 
            description = "Returns tasks that are in PENDING status")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getUnassignedTasks() {
        log.debug("REST request to get unassigned warehouse tasks");
        return ResponseEntity.ok(warehouseTaskService.findUnassignedTasks());
    }
    
    @GetMapping("/due-before")
    @Operation(summary = "Get warehouse tasks due before a specific date", 
            description = "Returns tasks due before the specified date")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    public ResponseEntity<List<WarehouseTaskDTO>> getTasksDueBefore(
            @Parameter(description = "Due date", required = true)
            @RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        log.debug("REST request to get warehouse tasks due before: {}", dueDate);
        return ResponseEntity.ok(warehouseTaskService.findByDueAtBefore(dueDate));
    }
    
    @PostMapping
    @Operation(summary = "Create a new warehouse task", 
            description = "Creates a new task in the warehouse")
    @ApiResponse(responseCode = "201", description = "Task created successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<WarehouseTaskDTO> createTask(
            @Parameter(description = "Task data", required = true)
            @Valid @RequestBody WarehouseTaskDTO taskDTO) {
        log.debug("REST request to create warehouse task: {}", taskDTO);
        WarehouseTaskDTO result = warehouseTaskService.create(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a warehouse task", 
            description = "Updates an existing task's details")
    @ApiResponse(responseCode = "200", description = "Task updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<WarehouseTaskDTO> updateTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Updated task data", required = true)
            @Valid @RequestBody WarehouseTaskDTO taskDTO) {
        log.debug("REST request to update warehouse task with id: {}", id);
        WarehouseTaskDTO result = warehouseTaskService.update(id, taskDTO);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Change warehouse task status", 
            description = "Updates the status of a warehouse task")
    @ApiResponse(responseCode = "200", description = "Task status updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<WarehouseTaskDTO> changeTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "New task status", required = true)
            @PathVariable("status") TaskStatus status) {
        log.debug("REST request to change status of warehouse task with id: {} to {}", id, status);
        WarehouseTaskDTO result = warehouseTaskService.changeStatus(id, status);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/assign/{staffId}")
    @Operation(summary = "Assign warehouse task to staff", 
            description = "Assigns a warehouse task to a specific staff member")
    @ApiResponse(responseCode = "200", description = "Task assigned successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task or staff not found")
    public ResponseEntity<WarehouseTaskDTO> assignTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("staffId") UUID staffId) {
        log.debug("REST request to assign warehouse task with id: {} to staff with id: {}", id, staffId);
        WarehouseTaskDTO result = warehouseTaskService.assignTask(id, staffId);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/start")
    @Operation(summary = "Start warehouse task", 
            description = "Marks a warehouse task as started")
    @ApiResponse(responseCode = "200", description = "Task started successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Task cannot be started, wrong status")
    public ResponseEntity<WarehouseTaskDTO> startTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id) {
        log.debug("REST request to start warehouse task with id: {}", id);
        WarehouseTaskDTO result = warehouseTaskService.startTask(id);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete warehouse task", 
            description = "Marks a warehouse task as completed")
    @ApiResponse(responseCode = "200", description = "Task completed successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Task cannot be completed, wrong status")
    public ResponseEntity<WarehouseTaskDTO> completeTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Actual duration in minutes", required = true)
            @RequestParam(value = "actualDurationMinutes", required = true) Integer actualDurationMinutes) {
        log.debug("REST request to complete warehouse task with id: {}", id);
        WarehouseTaskDTO result = warehouseTaskService.completeTask(id, actualDurationMinutes);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel warehouse task", 
            description = "Marks a warehouse task as cancelled")
    @ApiResponse(responseCode = "200", description = "Task cancelled successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Task cannot be cancelled, wrong status")
    public ResponseEntity<WarehouseTaskDTO> cancelTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Cancellation reason", required = true)
            @RequestParam(value = "reason", required = true) String reason) {
        log.debug("REST request to cancel warehouse task with id: {}", id);
        WarehouseTaskDTO result = warehouseTaskService.cancelTask(id, reason);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/priority/{priority}")
    @Operation(summary = "Change warehouse task priority", 
            description = "Updates the priority of a warehouse task")
    @ApiResponse(responseCode = "200", description = "Task priority updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseTaskDTO.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<WarehouseTaskDTO> changeTaskPriority(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "New task priority", required = true)
            @PathVariable("priority") Priority priority) {
        log.debug("REST request to change priority of warehouse task with id: {} to {}", id, priority);
        WarehouseTaskDTO result = warehouseTaskService.changePriority(id, priority);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a warehouse task", 
            description = "Removes a warehouse task from the system")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") UUID id) {
        log.debug("REST request to delete warehouse task with id: {}", id);
        warehouseTaskService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 
