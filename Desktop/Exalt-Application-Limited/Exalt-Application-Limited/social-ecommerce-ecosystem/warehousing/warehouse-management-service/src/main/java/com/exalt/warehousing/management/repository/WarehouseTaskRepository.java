package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.Priority;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.model.WarehouseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing WarehouseTask entities
 */
@Repository
public interface WarehouseTaskRepository extends JpaRepository<WarehouseTask, UUID> {
    
    /**
     * Find tasks by warehouse id
     * 
     * @param warehouseId the warehouse id
     * @return list of tasks in the warehouse
     */
    List<WarehouseTask> findByWarehouseId(UUID warehouseId);
    
    /**
     * Find tasks by warehouse id with pagination
     * 
     * @param warehouseId the warehouse id
     * @param pageable pagination parameters
     * @return page of tasks in the warehouse
     */
    Page<WarehouseTask> findByWarehouseIdOrderByPriorityAsc(UUID warehouseId, Pageable pageable);
    
    /**
     * Find tasks by status
     * 
     * @param status the task status
     * @return list of tasks with the given status
     */
    List<WarehouseTask> findByStatus(TaskStatus status);
    
    /**
     * Find tasks by type
     * 
     * @param type the task type
     * @return list of tasks of the given type
     */
    List<WarehouseTask> findByType(TaskType type);
    
    /**
     * Find tasks by priority
     * 
     * @param priority the task priority
     * @return list of tasks with the given priority
     */
    List<WarehouseTask> findByPriority(Priority priority);
    
    /**
     * Find tasks assigned to a staff member
     * 
     * @param assignedTo the staff id
     * @return list of tasks assigned to the staff member
     */
    List<WarehouseTask> findByAssignedTo(UUID assignedTo);
    
    /**
     * Find active tasks for a staff member (assigned or in progress)
     * 
     * @param staffId the staff id
     * @return list of active tasks for the staff member
     */
    @Query("SELECT t FROM WarehouseTask t WHERE t.assignedTo = :staffId AND t.status IN ('ASSIGNED', 'IN_PROGRESS')")
    List<WarehouseTask> findActiveTasksForStaff(@Param("staffId") UUID staffId);
    
    /**
     * Find tasks by zone id
     * 
     * @param zoneId the zone id
     * @return list of tasks in the zone
     */
    List<WarehouseTask> findByZoneId(UUID zoneId);
    
    /**
     * Find tasks by location id
     * 
     * @param locationId the location id
     * @return list of tasks at the location
     */
    List<WarehouseTask> findByLocationId(UUID locationId);
    
    /**
     * Find uncompleted tasks (not in COMPLETED or CANCELLED status)
     * 
     * @return list of uncompleted tasks
     */
    @Query("SELECT t FROM WarehouseTask t WHERE t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<WarehouseTask> findUncompletedTasks();
    
    /**
     * Find unassigned tasks (status is PENDING)
     * 
     * @return list of unassigned tasks
     */
    @Query("SELECT t FROM WarehouseTask t WHERE t.status = 'PENDING' AND t.assignedTo IS NULL")
    List<WarehouseTask> findUnassignedTasks();
    
    /**
     * Find unassigned tasks for a specific warehouse
     * 
     * @param warehouseId the warehouse id
     * @param status the task status
     * @return list of unassigned tasks in the warehouse
     */
    @Query("SELECT t FROM WarehouseTask t WHERE t.warehouseId = :warehouseId AND t.status = :status AND t.assignedTo IS NULL")
    List<WarehouseTask> findUnassignedTasksForWarehouse(@Param("warehouseId") UUID warehouseId, @Param("status") TaskStatus status);
    
    /**
     * Find tasks due before a specific date
     * 
     * @param dueAt the due date
     * @return list of tasks due before the date
     */
    List<WarehouseTask> findByDueAtBefore(LocalDateTime dueAt);
    
    /**
     * Find tasks by warehouse id and status
     * 
     * @param warehouseId the warehouse id
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<WarehouseTask> findByWarehouseIdAndStatus(UUID warehouseId, TaskStatus status);
    
    /**
     * Find tasks by warehouse id and type
     * 
     * @param warehouseId the warehouse id
     * @param type the task type
     * @return list of tasks matching the criteria
     */
    List<WarehouseTask> findByWarehouseIdAndType(UUID warehouseId, TaskType type);
    
    /**
     * Find tasks by warehouse id and priority
     * 
     * @param warehouseId the warehouse id
     * @param priority the task priority
     * @return list of tasks matching the criteria
     */
    List<WarehouseTask> findByWarehouseIdAndPriority(UUID warehouseId, Priority priority);
    
    /**
     * Find tasks assigned to a specific staff member (alias method)
     * 
     * @param assignedTo the staff id
     * @return list of tasks assigned to the staff member
     */
    List<WarehouseTask> findByAssignedToId(UUID assignedTo);
    
    /**
     * Find tasks by zone ID and status
     * 
     * @param zoneId the zone ID
     * @param status the task status
     * @return list of matching tasks
     */
    List<WarehouseTask> findByZoneIdAndStatus(UUID zoneId, TaskStatus status);
    
    /**
     * Count tasks by status
     * 
     * @param status the task status
     * @return count of tasks with the given status
     */
    long countByStatus(String status);
    
    /**
     * Calculate average completion time for completed tasks
     * 
     * @return average completion time in minutes
     */
    @Query("SELECT AVG(FUNCTION('timestampdiff', MINUTE, t.createdAt, t.completedAt)) " + 
           "FROM WarehouseTask t WHERE t.status = 'COMPLETED' AND t.completedAt IS NOT NULL")
    double calculateAverageCompletionTime();
    
    /**
     * Find tasks for a specific staff member
     * 
     * @param staffId the staff ID
     * @return list of assigned tasks
     */
    List<WarehouseTask> findByAssignedToStaffId(UUID staffId);
    
    /**
     * Get average task completion time for a specific warehouse
     * 
     * @param warehouseId the warehouse ID
     * @return average completion time in minutes
     */
    @Query("SELECT AVG(FUNCTION('timestampdiff', MINUTE, t.createdAt, t.completedAt)) " +
           "FROM WarehouseTask t WHERE t.warehouseId = :warehouseId AND t.status = 'COMPLETED' AND t.completedAt IS NOT NULL")
    double calculateAverageCompletionTimeByWarehouse(@Param("warehouseId") UUID warehouseId);
} 
