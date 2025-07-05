package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.model.Staff;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.repository.StaffRepository;
import com.exalt.warehousing.management.repository.WarehouseTaskRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.service.StaffAssignmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link StaffAssignmentService}. This class keeps the logic
 * very thin and re-uses existing repositories to avoid duplicating business rules that
 * already live in {@link com.exalt.warehousing.management.service.impl.WarehouseTaskServiceImpl}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StaffAssignmentServiceImpl implements StaffAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(StaffAssignmentServiceImpl.class);

    private final StaffRepository staffRepository;
    private final ZoneRepository zoneRepository;
    private final WarehouseTaskRepository warehouseTaskRepository;

    @Override
    public Staff assignStaffToZone(UUID staffId, UUID zoneId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));
        zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", zoneId));

        staff.setZoneId(zoneId.toString());
        staff.setUpdatedAt(LocalDateTime.now());
        Staff saved = staffRepository.save(staff);
        log.info("Assigned staff {} to zone {}", staffId, zoneId);
        return saved;
    }

    @Override
    public Staff unassignStaffFromZone(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));
        staff.setZoneId(null);
        staff.setUpdatedAt(LocalDateTime.now());
        Staff saved = staffRepository.save(staff);
        log.info("Unassigned staff {} from any zone", staffId);
        return saved;
    }

    @Override
    public WarehouseTask assignTaskToStaff(UUID taskId, UUID staffId) {
        WarehouseTask task = warehouseTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", taskId));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));

        if (task.getStatus() != TaskStatus.PENDING && task.getStatus() != TaskStatus.ON_HOLD) {
            throw new IllegalStateException("Task " + taskId + " status must be PENDING or ON_HOLD to be assigned");
        }

        if (task.getAssignedTo() != null) {
            throw new IllegalStateException("Task " + taskId + " is already assigned");
        }

        task.setAssignedTo(staffId);
        task.setStatus(TaskStatus.ASSIGNED);
        task.setUpdatedAt(LocalDateTime.now());
        WarehouseTask saved = warehouseTaskRepository.save(task);
        log.info("Assigned task {} to staff {}", taskId, staffId);
        return saved;
    }

    @Override
    public WarehouseTask unassignTask(UUID taskId) {
        WarehouseTask task = warehouseTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", taskId));
        if (task.getAssignedTo() == null) {
            return task; // already unassigned
        }
        task.setAssignedTo(null);
        task.setStatus(TaskStatus.PENDING);
        task.setUpdatedAt(LocalDateTime.now());
        WarehouseTask saved = warehouseTaskRepository.save(task);
        log.info("Unassigned staff from task {}", taskId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTask> getTasksAssignedToStaff(UUID staffId) {
        return warehouseTaskRepository.findByAssignedTo(staffId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffAssignedToZone(UUID zoneId) {
        return staffRepository.findByZoneId(zoneId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTask> getUnassignedTasks(UUID warehouseId) {
        return warehouseTaskRepository.findUnassignedTasksForWarehouse(warehouseId, TaskStatus.PENDING);
    }

    @Override
    public int autoAssignTasks(UUID warehouseId, int maxAssignments) {
        List<WarehouseTask> unassignedTasks = getUnassignedTasks(warehouseId);
        if (unassignedTasks.isEmpty()) {
            return 0;
        }

        // Get active staff in the warehouse ordered by current task count (ascending)
        List<Staff> staffInWarehouse = staffRepository.findByWarehouseId(warehouseId)
                .stream()
                .filter(s -> s.getStatus() != null && s.getStatus() == com.exalt.warehousing.management.model.StaffStatus.ACTIVE)
                .collect(Collectors.toList());

        if (staffInWarehouse.isEmpty()) {
            return 0;
        }

        int assignmentsMade = 0;
        for (WarehouseTask task : unassignedTasks) {
            if (assignmentsMade >= maxAssignments) {
                break;
            }
            Staff selectedStaff = staffInWarehouse.stream()
                    .sorted(Comparator.comparingInt(s -> warehouseTaskRepository.findActiveTasksForStaff((UUID.fromString(s.getId()))).size()))
                    .findFirst()
                    .orElse(null);
            if (selectedStaff == null) {
                break;
            }
            assignTaskToStaff(task.getId(), (UUID.fromString(selectedStaff.getId())));
            assignmentsMade++;
        }
        return assignmentsMade;
    }
}