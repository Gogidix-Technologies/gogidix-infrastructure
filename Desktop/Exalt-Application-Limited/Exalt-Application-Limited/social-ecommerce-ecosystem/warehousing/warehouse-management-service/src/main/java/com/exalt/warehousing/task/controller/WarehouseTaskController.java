package com.exalt.warehousing.task.controller;

import com.exalt.warehousing.task.dto.TaskBulkAssignmentDTO;
import com.exalt.warehousing.task.dto.TaskFilterDTO;
import com.exalt.warehousing.task.dto.WarehouseTaskDTO;
import com.exalt.warehousing.task.model.WarehouseTask;
import com.exalt.warehousing.task.service.WarehouseTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for warehouse task management
 */
@RestController
@RequestMapping("/api/v1/warehousing/tasks")
@RequiredArgsConstructor
public class WarehouseTaskController {

    private final WarehouseTaskService taskService;

    /**
     * Create a new warehouse task
     *
     * @param taskDTO the task data
     * @return the created task
     */
    @PostMapping
    public ResponseEntity<WarehouseTaskDTO> createTask(@Valid @RequestBody WarehouseTaskDTO taskDTO) {
        WarehouseTaskDTO createdTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Create multiple tasks in a single request
     *
     * @param taskDTOs list of tasks to create
     * @return the created tasks
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<WarehouseTaskDTO>> createBulkTasks(@Valid @RequestBody List<WarehouseTaskDTO> taskDTOs) {
        List<WarehouseTaskDTO> createdTasks = taskService.createBulkTasks(taskDTOs);
        return new ResponseEntity<>(createdTasks, HttpStatus.CREATED);
    }

    /**
     * Get a task by ID
     *
     * @param taskId the task ID
     * @return the task
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<WarehouseTaskDTO> getTaskById(@PathVariable UUID taskId) {
        WarehouseTaskDTO task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    /**
     * Get a task by task number
     *
     * @param taskNumber the task number
     * @return the task
     */
    @GetMapping("/number/{taskNumber}")
    public ResponseEntity<WarehouseTaskDTO> getTaskByNumber(@PathVariable String taskNumber) {
        WarehouseTaskDTO task = taskService.getTaskByNumber(taskNumber);
        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks with pagination
     *
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping
    public ResponseEntity<Page<WarehouseTaskDTO>> getAllTasks(Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by warehouse ID with pagination
     *
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<Page<WarehouseTaskDTO>> getTasksByWarehouse(
            @PathVariable UUID warehouseId, Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getTasksByWarehouse(warehouseId, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status with pagination
     *
     * @param status the task status
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<WarehouseTaskDTO>> getTasksByStatus(
            @PathVariable String status, Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getTasksByStatus(
                WarehouseTask.TaskStatus.valueOf(status), pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by type with pagination
     *
     * @param type the task type
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<WarehouseTaskDTO>> getTasksByType(
            @PathVariable String type, Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getTasksByType(
                WarehouseTask.TaskType.valueOf(type), pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks assigned to a specific staff member
     *
     * @param staffId the staff member ID
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping("/assigned/{staffId}")
    public ResponseEntity<Page<WarehouseTaskDTO>> getTasksAssignedToStaff(
            @PathVariable UUID staffId, Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getTasksAssignedToStaff(staffId, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks that are overdue
     *
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping("/overdue")
    public ResponseEntity<Page<WarehouseTaskDTO>> getOverdueTasks(Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getOverdueTasks(LocalDateTime.now(), pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get high priority tasks
     *
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @GetMapping("/high-priority")
    public ResponseEntity<Page<WarehouseTaskDTO>> getHighPriorityTasks(Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.getHighPriorityTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Filter tasks by various criteria
     *
     * @param filterDTO the filter criteria
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    @PostMapping("/filter")
    public ResponseEntity<Page<WarehouseTaskDTO>> filterTasks(
            @RequestBody TaskFilterDTO filterDTO, Pageable pageable) {
        Page<WarehouseTaskDTO> tasks = taskService.filterTasks(filterDTO, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Update a task
     *
     * @param taskId the task ID
     * @param taskDTO the updated task data
     * @return the updated task
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<WarehouseTaskDTO> updateTask(
            @PathVariable UUID taskId, @Valid @RequestBody WarehouseTaskDTO taskDTO) {
        taskDTO.setId(taskId);
        WarehouseTaskDTO updatedTask = taskService.updateTask(taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Assign a task to a staff member
     *
     * @param taskId the task ID
     * @param staffId the staff member ID
     * @return the updated task
     */
    @PutMapping("/{taskId}/assign/{staffId}")
    public ResponseEntity<WarehouseTaskDTO> assignTask(
            @PathVariable UUID taskId, @PathVariable UUID staffId) {
        WarehouseTaskDTO updatedTask = taskService.assignTask(taskId, staffId);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Bulk assign tasks to a staff member
     *
     * @param bulkAssignmentDTO the bulk assignment data
     * @return the updated tasks
     */
    @PutMapping("/bulk-assign")
    public ResponseEntity<List<WarehouseTaskDTO>> bulkAssignTasks(
            @Valid @RequestBody TaskBulkAssignmentDTO bulkAssignmentDTO) {
        List<WarehouseTaskDTO> updatedTasks = taskService.bulkAssignTasks(bulkAssignmentDTO);
        return ResponseEntity.ok(updatedTasks);
    }

    /**
     * Start a task
     *
     * @param taskId the task ID
     * @return the updated task
     */
    @PutMapping("/{taskId}/start")
    public ResponseEntity<WarehouseTaskDTO> startTask(@PathVariable UUID taskId) {
        WarehouseTaskDTO updatedTask = taskService.startTask(taskId);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Complete a task
     *
     * @param taskId the task ID
     * @param completionNotes the completion notes
     * @return the updated task
     */
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<WarehouseTaskDTO> completeTask(
            @PathVariable UUID taskId, @RequestBody(required = false) String completionNotes) {
        WarehouseTaskDTO updatedTask = taskService.completeTask(taskId, completionNotes);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Cancel a task
     *
     * @param taskId the task ID
     * @param cancellationReason the cancellation reason
     * @return the updated task
     */
    @PutMapping("/{taskId}/cancel")
    public ResponseEntity<WarehouseTaskDTO> cancelTask(
            @PathVariable UUID taskId, @RequestBody(required = false) String cancellationReason) {
        WarehouseTaskDTO updatedTask = taskService.cancelTask(taskId, cancellationReason);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Put a task on hold
     *
     * @param taskId the task ID
     * @return the updated task
     */
    @PutMapping("/{taskId}/hold")
    public ResponseEntity<WarehouseTaskDTO> holdTask(@PathVariable UUID taskId) {
        WarehouseTaskDTO updatedTask = taskService.holdTask(taskId);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task
     *
     * @param taskId the task ID
     * @return no content
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get task statistics
     *
     * @param warehouseId optional warehouse ID filter
     * @return task statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(
            @RequestParam(required = false) UUID warehouseId) {
        Map<String, Object> statistics = taskService.getTaskStatistics(warehouseId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get task count by status
     *
     * @param warehouseId optional warehouse ID filter
     * @return task counts by status
     */
    @GetMapping("/count-by-status")
    public ResponseEntity<Map<String, Long>> getTaskCountByStatus(
            @RequestParam(required = false) UUID warehouseId) {
        Map<String, Long> counts = taskService.getTaskCountByStatus(warehouseId);
        return ResponseEntity.ok(counts);
    }

    /**
     * Get task count by type
     *
     * @param warehouseId optional warehouse ID filter
     * @return task counts by type
     */
    @GetMapping("/count-by-type")
    public ResponseEntity<Map<String, Long>> getTaskCountByType(
            @RequestParam(required = false) UUID warehouseId) {
        Map<String, Long> counts = taskService.getTaskCountByType(warehouseId);
        return ResponseEntity.ok(counts);
    }
}