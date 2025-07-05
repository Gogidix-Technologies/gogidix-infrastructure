package com.exalt.warehousing.task.repository;

import com.exalt.warehousing.task.model.WarehouseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing warehouse tasks
 */
@Repository
public interface WarehouseTaskRepository extends JpaRepository<WarehouseTask, UUID> {

    /**
     * Find task by its unique task number
     * @param taskNumber the task number
     * @return the warehouse task
     */
    Optional<WarehouseTask> findByTaskNumber(String taskNumber);
    
    /**
     * Find all tasks for a specific warehouse with pagination
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByWarehouseId(UUID warehouseId, Pageable pageable);
    
    /**
     * Find tasks by task type with pagination
     * @param taskType the task type
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByTaskType(WarehouseTask.TaskType taskType, Pageable pageable);
    
    /**
     * Find tasks by status with pagination
     * @param status the task status
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByStatus(WarehouseTask.TaskStatus status, Pageable pageable);
    
    /**
     * Find tasks assigned to a specific staff member with pagination
     * @param assignedTo the staff member ID
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByAssignedTo(UUID assignedTo, Pageable pageable);
    
    /**
     * Find tasks by reference ID (order ID, shipment ID, etc.)
     * @param referenceId the reference ID
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByReferenceId(UUID referenceId, Pageable pageable);
    
    /**
     * Find tasks by reference type
     * @param referenceType the reference type (ORDER, SHIPMENT, etc.)
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByReferenceType(String referenceType, Pageable pageable);
    
    /**
     * Find tasks by product ID with pagination
     * @param productId the product ID
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByProductId(UUID productId, Pageable pageable);
    
    /**
     * Find tasks by SKU with pagination
     * @param sku the product SKU
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findBySku(String sku, Pageable pageable);
    
    /**
     * Find tasks by source location ID with pagination
     * @param sourceLocationId the source location ID
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findBySourceLocationId(UUID sourceLocationId, Pageable pageable);
    
    /**
     * Find tasks by destination location ID with pagination
     * @param destinationLocationId the destination location ID
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByDestinationLocationId(UUID destinationLocationId, Pageable pageable);
    
    /**
     * Find tasks due by a specific date with pagination
     * @param dueBy the due date
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByDueByLessThanEqual(LocalDateTime dueBy, Pageable pageable);
    
    /**
     * Find pending tasks that have not been assigned
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByStatusAndAssignedToIsNull(WarehouseTask.TaskStatus status, Pageable pageable);
    
    /**
     * Find overdue tasks
     * @param currentTime the current time
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    @Query("SELECT t FROM WarehouseTask t WHERE t.dueBy < :currentTime AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    Page<WarehouseTask> findOverdueTasks(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);
    
    /**
     * Find tasks by priority with pagination
     * @param priority the priority level
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByPriority(Integer priority, Pageable pageable);
    
    /**
     * Find high priority tasks (priority >= 7)
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    @Query("SELECT t FROM WarehouseTask t WHERE t.priority >= 7 AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    Page<WarehouseTask> findHighPriorityTasks(Pageable pageable);
    
    /**
     * Find tasks by warehouse, type and status
     * @param warehouseId the warehouse ID
     * @param taskType the task type
     * @param status the task status
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByWarehouseIdAndTaskTypeAndStatus(
            UUID warehouseId, WarehouseTask.TaskType taskType, WarehouseTask.TaskStatus status, Pageable pageable);
    
    /**
     * Count tasks by status
     * @return list of counts by status
     */
    @Query("SELECT t.status as status, COUNT(t) as count FROM WarehouseTask t GROUP BY t.status")
    List<Object[]> countByStatus();
    
    /**
     * Count tasks by type
     * @return list of counts by type
     */
    @Query("SELECT t.taskType as type, COUNT(t) as count FROM WarehouseTask t GROUP BY t.taskType")
    List<Object[]> countByTaskType();
    
    /**
     * Find recently completed tasks
     * @param cutoffTime time cutoff for recently completed
     * @param pageable pagination information
     * @return paginated list of warehouse tasks
     */
    Page<WarehouseTask> findByStatusAndCompletedAtGreaterThan(
            WarehouseTask.TaskStatus status, LocalDateTime cutoffTime, Pageable pageable);
}