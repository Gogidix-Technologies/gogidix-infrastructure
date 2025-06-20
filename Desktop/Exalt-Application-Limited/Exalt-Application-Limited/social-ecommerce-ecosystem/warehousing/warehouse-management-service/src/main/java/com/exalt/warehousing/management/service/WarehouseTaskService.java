package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.WarehouseTaskDTO;
import com.exalt.warehousing.management.model.Priority;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.model.WarehouseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing warehouse task operations
 */
public interface WarehouseTaskService {
    
    /**
     * Find all warehouse tasks
     * 
     * @return list of all tasks
     */
    List<WarehouseTaskDTO> findAll();
    
    /**
     * Find all tasks with pagination
     * 
     * @param pageable pagination parameters
     * @return page of tasks
     */
    Page<WarehouseTaskDTO> findAll(Pageable pageable);
    
    /**
     * Find task by ID
     * 
     * @param id the task ID
     * @return the task if found
     */
    WarehouseTaskDTO findById(UUID id);
    
    /**
     * Find tasks by warehouse ID
     * 
     * @param warehouseId the warehouse ID
     * @return list of tasks in the warehouse
     */
    List<WarehouseTaskDTO> findByWarehouseId(UUID warehouseId);
    
    /**
     * Find tasks by warehouse ID with pagination
     * 
     * @param warehouseId the warehouse ID
     * @param pageable pagination parameters
     * @return page of tasks in the warehouse
     */
    Page<WarehouseTaskDTO> findByWarehouseId(UUID warehouseId, Pageable pageable);
    
    /**
     * Find tasks by status
     * 
     * @param status the task status
     * @return list of tasks with the given status
     */
    List<WarehouseTaskDTO> findByStatus(TaskStatus status);
    
    /**
     * Find tasks by type
     * 
     * @param type the task type
     * @return list of tasks of the given type
     */
    List<WarehouseTaskDTO> findByType(TaskType type);
    
    /**
     * Find tasks by priority
     * 
     * @param priority the task priority
     * @return list of tasks with the given priority
     */
    List<WarehouseTaskDTO> findByPriority(Priority priority);
    
    /**
     * Find tasks assigned to a staff member
     * 
     * @param staffId the staff ID
     * @return list of tasks assigned to the staff member
     */
    List<WarehouseTaskDTO> findByAssignedTo(UUID staffId);
    
    /**
     * Find active tasks (assigned or in progress) for a staff member
     * 
     * @param staffId the staff ID
     * @return list of active tasks for the staff member
     */
    List<WarehouseTaskDTO> findActiveTasksForStaff(UUID staffId);
    
    /**
     * Find tasks by zone ID
     * 
     * @param zoneId the zone ID
     * @return list of tasks in the zone
     */
    List<WarehouseTaskDTO> findByZoneId(UUID zoneId);
    
    /**
     * Find tasks by location ID
     * 
     * @param locationId the location ID
     * @return list of tasks at the location
     */
    List<WarehouseTaskDTO> findByLocationId(UUID locationId);
    
    /**
     * Find uncompleted tasks (not in COMPLETED or CANCELLED status)
     * 
     * @return list of uncompleted tasks
     */
    List<WarehouseTaskDTO> findUncompletedTasks();
    
    /**
     * Find unassigned tasks (status is PENDING)
     * 
     * @return list of unassigned tasks
     */
    List<WarehouseTaskDTO> findUnassignedTasks();
    
    /**
     * Find tasks due before a specific date
     * 
     * @param dueDate the due date
     * @return list of tasks due before the date
     */
    List<WarehouseTaskDTO> findByDueAtBefore(LocalDateTime dueDate);
    
    /**
     * Create a new task
     * 
     * @param taskDTO the task data
     * @return the created task
     */
    WarehouseTaskDTO create(WarehouseTaskDTO taskDTO);
    
    /**
     * Update an existing task
     * 
     * @param id the task ID
     * @param taskDTO the updated task data
     * @return the updated task
     */
    WarehouseTaskDTO update(UUID id, WarehouseTaskDTO taskDTO);
    
    /**
     * Change task status
     * 
     * @param id the task ID
     * @param status the new status
     * @return the updated task
     */
    WarehouseTaskDTO changeStatus(UUID id, TaskStatus status);
    
    /**
     * Assign task to staff member
     * 
     * @param taskId the task ID
     * @param staffId the staff ID
     * @return the updated task
     */
    WarehouseTaskDTO assignTask(UUID taskId, UUID staffId);
    
    /**
     * Start a task
     * 
     * @param id the task ID
     * @return the updated task
     */
    WarehouseTaskDTO startTask(UUID id);
    
    /**
     * Complete a task
     * 
     * @param id the task ID
     * @param actualDurationMinutes the actual duration in minutes
     * @return the updated task
     */
    WarehouseTaskDTO completeTask(UUID id, Integer actualDurationMinutes);
    
    /**
     * Cancel a task
     * 
     * @param id the task ID
     * @param reason the cancellation reason
     * @return the updated task
     */
    WarehouseTaskDTO cancelTask(UUID id, String reason);
    
    /**
     * Change task priority
     * 
     * @param id the task ID
     * @param priority the new priority
     * @return the updated task
     */
    WarehouseTaskDTO changePriority(UUID id, Priority priority);
    
    /**
     * Delete a task
     * 
     * @param id the task ID
     */
    void delete(UUID id);

    /**
     * Create a new warehouse task
     *
     * @param task the task to create
     * @return the created task
     */
    WarehouseTask createTask(WarehouseTask task);

    /**
     * Update an existing warehouse task
     *
     * @param id the task id
     * @param task the task data to update
     * @return the updated task
     */
    WarehouseTask updateTask(UUID id, WarehouseTask task);

    /**
     * Get a task by its id
     *
     * @param id the task id
     * @return the task if found
     */
    Optional<WarehouseTask> getTask(UUID id);

    /**
     * Get all tasks for a warehouse
     *
     * @param warehouseId the warehouse id
     * @return list of tasks in the warehouse
     */
    List<WarehouseTask> getTasksByWarehouseId(UUID warehouseId);

    /**
     * Get all tasks assigned to a staff member
     *
     * @param staffId the staff id
     * @return list of tasks assigned to the staff member
     */
    List<WarehouseTask> getTasksByAssignedToId(UUID staffId);

    /**
     * Get tasks by status
     *
     * @param status the task status
     * @return list of tasks with the given status
     */
    List<WarehouseTask> getTasksByStatus(TaskStatus status);

    /**
     * Get tasks by type
     *
     * @param type the task type
     * @return list of tasks of the given type
     */
    List<WarehouseTask> getTasksByType(TaskType type);

    /**
     * Get tasks by warehouse id and status
     *
     * @param warehouseId the warehouse id
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<WarehouseTask> getTasksByWarehouseIdAndStatus(UUID warehouseId, TaskStatus status);

    /**
     * Get tasks by warehouse id and type
     *
     * @param warehouseId the warehouse id
     * @param type the task type
     * @return list of tasks matching the criteria
     */
    List<WarehouseTask> getTasksByWarehouseIdAndType(UUID warehouseId, TaskType type);

    /**
     * Get tasks by warehouse id and priority
     *
     * @param warehouseId the warehouse id
     * @param priority the task priority
     * @return list of tasks matching the criteria
     */
    List<WarehouseTask> getTasksByWarehouseIdAndPriority(UUID warehouseId, Priority priority);

    /**
     * Get tasks due before a specific date
     *
     * @param date the date to check against
     * @return list of tasks due before the date
     */
    List<WarehouseTask> getTasksDueBefore(LocalDateTime date);

    /**
     * Get unassigned tasks for a warehouse
     *
     * @param warehouseId the warehouse id
     * @param status the task status (usually PENDING)
     * @return list of unassigned tasks
     */
    List<WarehouseTask> getUnassignedTasks(UUID warehouseId, TaskStatus status);

    /**
     * Assign a task to a staff member
     *
     * @param taskId the task id
     * @param staffId the staff id
     * @return the updated task
     */
    WarehouseTask assignTaskToStaff(UUID taskId, UUID staffId);

    /**
     * Update the status of a task
     *
     * @param id the task id
     * @param status the new status
     * @return the updated task
     */
    WarehouseTask updateTaskStatus(UUID id, TaskStatus status);

    /**
     * Delete a task
     *
     * @param id the task id
     */
    void deleteTask(UUID id);

    /**
     * Find tasks assigned to a staff member
     *
     * @param staffId the staff ID
     * @return list of tasks assigned to the staff member
     */
    List<WarehouseTask> findTasksAssignedToStaff(UUID staffId);

    /**
     * Update task status
     *
     * @param taskId the task ID
     * @param status the new status
     * @param note optional note about the status change
     * @return the updated task DTO
     */
    WarehouseTaskDTO updateTaskStatus(UUID taskId, TaskStatus status, String note);

    /**
     * Count pending tasks for a staff member
     *
     * @param staffId the staff ID
     * @return count of pending tasks
     */
    long countPendingTasksForStaff(UUID staffId);
} 
