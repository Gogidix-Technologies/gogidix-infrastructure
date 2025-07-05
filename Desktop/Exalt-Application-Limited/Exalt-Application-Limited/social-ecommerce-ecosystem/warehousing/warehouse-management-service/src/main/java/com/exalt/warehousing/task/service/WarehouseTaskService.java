package com.exalt.warehousing.task.service;

import com.exalt.warehousing.task.dto.TaskBulkAssignmentDTO;
import com.exalt.warehousing.task.dto.TaskFilterDTO;
import com.exalt.warehousing.task.dto.WarehouseTaskDTO;
import com.exalt.warehousing.task.model.WarehouseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for warehouse task management
 */
public interface WarehouseTaskService {

    /**
     * Create a new warehouse task
     *
     * @param taskDTO the task data
     * @return the created task
     */
    WarehouseTaskDTO createTask(WarehouseTaskDTO taskDTO);

    /**
     * Create multiple tasks in a batch
     *
     * @param taskDTOs list of task DTOs
     * @return list of created tasks
     */
    List<WarehouseTaskDTO> createBulkTasks(List<WarehouseTaskDTO> taskDTOs);

    /**
     * Get a task by ID
     *
     * @param taskId the task ID
     * @return the task DTO
     */
    WarehouseTaskDTO getTaskById(UUID taskId);

    /**
     * Get a task by task number
     *
     * @param taskNumber the task number
     * @return the task DTO
     */
    WarehouseTaskDTO getTaskByNumber(String taskNumber);

    /**
     * Get all tasks with pagination
     *
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getAllTasks(Pageable pageable);

    /**
     * Get tasks by warehouse ID with pagination
     *
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getTasksByWarehouse(UUID warehouseId, Pageable pageable);

    /**
     * Get tasks by status with pagination
     *
     * @param status the task status
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getTasksByStatus(WarehouseTask.TaskStatus status, Pageable pageable);

    /**
     * Get tasks by type with pagination
     *
     * @param taskType the task type
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getTasksByType(WarehouseTask.TaskType taskType, Pageable pageable);

    /**
     * Get tasks assigned to a specific staff member
     *
     * @param staffId the staff member ID
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getTasksAssignedToStaff(UUID staffId, Pageable pageable);

    /**
     * Get overdue tasks
     *
     * @param currentTime the current time to compare against due dates
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getOverdueTasks(LocalDateTime currentTime, Pageable pageable);

    /**
     * Get high priority tasks
     *
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> getHighPriorityTasks(Pageable pageable);

    /**
     * Filter tasks by various criteria
     *
     * @param filterDTO the filter criteria
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    Page<WarehouseTaskDTO> filterTasks(TaskFilterDTO filterDTO, Pageable pageable);

    /**
     * Update a task
     *
     * @param taskDTO the updated task data
     * @return the updated task
     */
    WarehouseTaskDTO updateTask(WarehouseTaskDTO taskDTO);

    /**
     * Assign a task to a staff member
     *
     * @param taskId the task ID
     * @param staffId the staff member ID
     * @return the updated task
     */
    WarehouseTaskDTO assignTask(UUID taskId, UUID staffId);

    /**
     * Bulk assign tasks to a staff member
     *
     * @param bulkAssignmentDTO the bulk assignment data
     * @return the updated tasks
     */
    List<WarehouseTaskDTO> bulkAssignTasks(TaskBulkAssignmentDTO bulkAssignmentDTO);

    /**
     * Start a task
     *
     * @param taskId the task ID
     * @return the updated task
     */
    WarehouseTaskDTO startTask(UUID taskId);

    /**
     * Complete a task
     *
     * @param taskId the task ID
     * @param completionNotes optional completion notes
     * @return the updated task
     */
    WarehouseTaskDTO completeTask(UUID taskId, String completionNotes);

    /**
     * Cancel a task
     *
     * @param taskId the task ID
     * @param cancellationReason optional cancellation reason
     * @return the updated task
     */
    WarehouseTaskDTO cancelTask(UUID taskId, String cancellationReason);

    /**
     * Put a task on hold
     *
     * @param taskId the task ID
     * @return the updated task
     */
    WarehouseTaskDTO holdTask(UUID taskId);

    /**
     * Delete a task
     *
     * @param taskId the task ID
     */
    void deleteTask(UUID taskId);

    /**
     * Get task statistics
     *
     * @param warehouseId optional warehouse ID filter
     * @return task statistics
     */
    Map<String, Object> getTaskStatistics(UUID warehouseId);

    /**
     * Get task count by status
     *
     * @param warehouseId optional warehouse ID filter
     * @return task counts by status
     */
    Map<String, Long> getTaskCountByStatus(UUID warehouseId);

    /**
     * Get task count by type
     *
     * @param warehouseId optional warehouse ID filter
     * @return task counts by type
     */
    Map<String, Long> getTaskCountByType(UUID warehouseId);
}