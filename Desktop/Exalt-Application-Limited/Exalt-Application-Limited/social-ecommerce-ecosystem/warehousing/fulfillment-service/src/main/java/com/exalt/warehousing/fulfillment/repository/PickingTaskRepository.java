package com.exalt.warehousing.fulfillment.repository;

import com.exalt.warehousing.fulfillment.entity.PickingTask;
import com.exalt.warehousing.fulfillment.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for PickingTask entities
 */
@Repository
public interface PickingTaskRepository extends JpaRepository<PickingTask, UUID> {

    /**
     * Find all tasks for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of tasks
     */
    List<PickingTask> findAllByFulfillmentOrderId(UUID fulfillmentOrderId);

    /**
     * Find all tasks by status
     *
     * @param status the status
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PickingTask> findAllByStatus(TaskStatus status, Pageable pageable);

    /**
     * Find all tasks assigned to a staff member
     *
     * @param staffId the staff member ID
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PickingTask> findAllByAssignedStaffId(UUID staffId, Pageable pageable);

    /**
     * Find all tasks assigned to a staff member with a specific status
     *
     * @param staffId the staff member ID
     * @param status the status
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PickingTask> findAllByAssignedStaffIdAndStatus(
            UUID staffId, TaskStatus status, Pageable pageable);

    /**
     * Find all overdue tasks
     *
     * @param currentTime the current time
     * @param pageable pagination information
     * @return page of overdue tasks
     */
    @Query("SELECT t FROM PickingTask t WHERE t.dueBy < :currentTime " +
           "AND t.status NOT IN (com.exalt.warehousing.fulfillment.enums.TaskStatus.COMPLETED, " +
           "com.exalt.warehousing.fulfillment.enums.TaskStatus.CANCELLED)")
    Page<PickingTask> findAllOverdueTasks(LocalDateTime currentTime, Pageable pageable);

    /**
     * Find all tasks in a specific batch
     *
     * @param batchId the batch ID
     * @return list of tasks
     */
    List<PickingTask> findAllByBatchId(String batchId);

    /**
     * Find all tasks in a specific zone
     *
     * @param zone the zone
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PickingTask> findAllByZone(String zone, Pageable pageable);

    /**
     * Find all tasks in a specific aisle
     *
     * @param aisle the aisle
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PickingTask> findAllByAisle(String aisle, Pageable pageable);

    /**
     * Find all pending tasks ordered by priority and due date
     *
     * @return list of pending tasks
     */
    @Query("SELECT t FROM PickingTask t WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.PENDING " +
           "ORDER BY t.priority DESC, t.dueBy ASC, t.createdAt ASC")
    List<PickingTask> findAllPendingTasksOrderByPriorityAndDueDate();

    /**
     * Find all pending tasks for a zone ordered by priority and due date
     *
     * @param zone the zone
     * @return list of pending tasks
     */
    @Query("SELECT t FROM PickingTask t WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.PENDING " +
           "AND t.zone = :zone ORDER BY t.priority DESC, t.dueBy ASC, t.createdAt ASC")
    List<PickingTask> findAllPendingTasksForZoneOrderByPriorityAndDueDate(String zone);

    /**
     * Find all in-progress tasks that haven't been updated recently
     *
     * @param cutoffTime the cutoff time
     * @return list of stalled tasks
     */
    @Query("SELECT t FROM PickingTask t WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.IN_PROGRESS " +
           "AND t.updatedAt < :cutoffTime")
    List<PickingTask> findAllStalledTasks(LocalDateTime cutoffTime);

    /**
     * Count tasks by status
     *
     * @param status the status
     * @return count of tasks with the specified status
     */
    long countByStatus(TaskStatus status);

    /**
     * Get average completion time in minutes
     *
     * @return average completion time
     */
    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', MINUTE, t.startedAt, t.completedAt)) FROM PickingTask t " +
           "WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.COMPLETED " +
           "AND t.startedAt IS NOT NULL AND t.completedAt IS NOT NULL")
    Double getAverageCompletionTimeMinutes();
}
