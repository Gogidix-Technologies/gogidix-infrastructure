package com.exalt.warehousing.fulfillment.repository;

import java.util.Map;

import com.exalt.warehousing.fulfillment.entity.PackingTask;
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
 * Repository interface for PackingTask entities
 */
@Repository
public interface PackingTaskRepository extends JpaRepository<PackingTask, UUID> {

    /**
     * Find all tasks for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of tasks
     */
    List<PackingTask> findAllByFulfillmentOrderId(UUID fulfillmentOrderId);

    /**
     * Find all tasks by status
     *
     * @param status the status
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PackingTask> findAllByStatus(TaskStatus status, Pageable pageable);

    /**
     * Find all tasks assigned to a staff member
     *
     * @param staffId the staff member ID
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PackingTask> findAllByAssignedStaffId(UUID staffId, Pageable pageable);

    /**
     * Find all tasks assigned to a staff member with a specific status
     *
     * @param staffId the staff member ID
     * @param status the status
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PackingTask> findAllByAssignedStaffIdAndStatus(
            UUID staffId, TaskStatus status, Pageable pageable);

    /**
     * Find all overdue tasks
     *
     * @param currentTime the current time
     * @param pageable pagination information
     * @return page of overdue tasks
     */
    @Query("SELECT t FROM PackingTask t WHERE t.dueBy < :currentTime " +
           "AND t.status NOT IN (com.exalt.warehousing.fulfillment.enums.TaskStatus.COMPLETED, " +
           "com.exalt.warehousing.fulfillment.enums.TaskStatus.CANCELLED)")
    Page<PackingTask> findAllOverdueTasks(LocalDateTime currentTime, Pageable pageable);

    /**
     * Find all tasks at a specific packing station
     *
     * @param packingStation the packing station
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PackingTask> findAllByPackingStation(String packingStation, Pageable pageable);

    /**
     * Find all tasks requiring special handling
     *
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<PackingTask> findAllBySpecialHandlingRequiredTrue(Pageable pageable);

    /**
     * Find all pending tasks ordered by priority and due date
     *
     * @return list of pending tasks
     */
    @Query("SELECT t FROM PackingTask t WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.PENDING " +
           "ORDER BY t.priority DESC, t.dueBy ASC, t.createdAt ASC")
    List<PackingTask> findAllPendingTasksOrderByPriorityAndDueDate();

    /**
     * Find all pending tasks for a packing station ordered by priority and due date
     *
     * @param packingStation the packing station
     * @return list of pending tasks
     */
    @Query("SELECT t FROM PackingTask t WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.PENDING " +
           "AND t.packingStation = :packingStation ORDER BY t.priority DESC, t.dueBy ASC, t.createdAt ASC")
    List<PackingTask> findAllPendingTasksForPackingStationOrderByPriorityAndDueDate(String packingStation);

    /**
     * Find all in-progress tasks that haven't been updated recently
     *
     * @param cutoffTime the cutoff time
     * @return list of stalled tasks
     */
    @Query("SELECT t FROM PackingTask t WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.IN_PROGRESS " +
           "AND t.updatedAt < :cutoffTime")
    List<PackingTask> findAllStalledTasks(LocalDateTime cutoffTime);

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
    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', MINUTE, t.startedAt, t.completedAt)) FROM PackingTask t " +
           "WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.COMPLETED " +
           "AND t.startedAt IS NOT NULL AND t.completedAt IS NOT NULL")
    Double getAverageCompletionTimeMinutes();

    /**
     * Get average package dimensions and weight
     *
     * @return average dimensions and weight
     */
    @Query("SELECT NEW map(AVG(t.weightKg) as avgWeight, AVG(t.lengthCm) as avgLength, " +
           "AVG(t.widthCm) as avgWidth, AVG(t.heightCm) as avgHeight) FROM PackingTask t " +
           "WHERE t.status = com.exalt.warehousing.fulfillment.enums.TaskStatus.COMPLETED " +
           "AND t.weightKg IS NOT NULL AND t.lengthCm IS NOT NULL " +
           "AND t.widthCm IS NOT NULL AND t.heightCm IS NOT NULL")
    Object getAveragePackageDimensions();
}

