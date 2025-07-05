package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.model.Staff;
import com.exalt.warehousing.management.model.WarehouseTask;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing staff assignments to zones and tasks
 */
public interface StaffAssignmentService {

    /**
     * Assign a staff member to a zone
     *
     * @param staffId the staff id
     * @param zoneId the zone id
     * @return the updated staff member
     */
    Staff assignStaffToZone(UUID staffId, UUID zoneId);

    /**
     * Unassign a staff member from a zone
     *
     * @param staffId the staff id
     * @return the updated staff member
     */
    Staff unassignStaffFromZone(UUID staffId);

    /**
     * Assign a task to a staff member
     *
     * @param taskId the task id
     * @param staffId the staff id to assign the task to
     * @return the updated task
     */
    WarehouseTask assignTaskToStaff(UUID taskId, UUID staffId);

    /**
     * Unassign a task from any staff member
     *
     * @param taskId the task id
     * @return the updated task
     */
    WarehouseTask unassignTask(UUID taskId);

    /**
     * Get all tasks assigned to a staff member
     *
     * @param staffId the staff id
     * @return list of tasks assigned to the staff member
     */
    List<WarehouseTask> getTasksAssignedToStaff(UUID staffId);

    /**
     * Get all staff assigned to a zone
     *
     * @param zoneId the zone id
     * @return list of staff assigned to the zone
     */
    List<Staff> getStaffAssignedToZone(UUID zoneId);

    /**
     * Get all unassigned tasks in a warehouse
     *
     * @param warehouseId the warehouse id
     * @return list of unassigned tasks
     */
    List<WarehouseTask> getUnassignedTasks(UUID warehouseId);

    /**
     * Auto-assign tasks to available staff based on priority and location
     *
     * @param warehouseId the warehouse id
     * @param maxAssignments maximum number of assignments to make
     * @return number of tasks assigned
     */
    int autoAssignTasks(UUID warehouseId, int maxAssignments);
} 
