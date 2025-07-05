package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.management.dto.WarehouseTaskDTO;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.mapper.WarehouseTaskMapper;
import com.exalt.warehousing.management.model.Priority;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.TaskType;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.repository.StaffRepository;
import com.exalt.warehousing.management.repository.WarehouseRepository;
import com.exalt.warehousing.management.repository.WarehouseTaskRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.service.WarehouseTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Implementation of the WarehouseTaskService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseTaskServiceImpl implements WarehouseTaskService {

    private final WarehouseTaskRepository warehouseTaskRepository;
    private final WarehouseRepository warehouseRepository;
    private final StaffRepository staffRepository;
    private final ZoneRepository zoneRepository;
    private final LocationRepository locationRepository;
    private final WarehouseTaskMapper warehouseTaskMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findAll() {
        log.debug("Finding all warehouse tasks");
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> findAll(Pageable pageable) {
        log.debug("Finding all warehouse tasks with pagination");
        return warehouseTaskRepository.findAll(pageable)
                .map(warehouseTaskMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseTaskDTO findById(UUID id) {
        log.debug("Finding warehouse task by id: {}", id);
        return warehouseTaskRepository.findById(id)
                .map(warehouseTaskMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByWarehouseId(UUID warehouseId) {
        log.debug("Finding warehouse tasks by warehouse id: {}", warehouseId);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByWarehouseId(warehouseId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> findByWarehouseId(UUID warehouseId, Pageable pageable) {
        log.debug("Finding warehouse tasks by warehouse id: {} with pagination", warehouseId);
        return warehouseTaskRepository.findByWarehouseIdOrderByPriorityAsc(warehouseId, pageable)
                .map(warehouseTaskMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByStatus(TaskStatus status) {
        log.debug("Finding warehouse tasks by status: {}", status);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByType(TaskType type) {
        log.debug("Finding warehouse tasks by type: {}", type);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByType(type));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByPriority(Priority priority) {
        log.debug("Finding warehouse tasks by priority: {}", priority);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByPriority(priority));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByAssignedTo(UUID staffId) {
        log.debug("Finding warehouse tasks assigned to staff id: {}", staffId);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByAssignedTo(staffId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findActiveTasksForStaff(UUID staffId) {
        log.debug("Finding active warehouse tasks for staff id: {}", staffId);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findActiveTasksForStaff(staffId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByZoneId(UUID zoneId) {
        log.debug("Finding warehouse tasks by zone id: {}", zoneId);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByZoneId(zoneId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByLocationId(UUID locationId) {
        log.debug("Finding warehouse tasks by location id: {}", locationId);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByLocationId(locationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findUncompletedTasks() {
        log.debug("Finding uncompleted warehouse tasks");
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findUncompletedTasks());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findUnassignedTasks() {
        log.debug("Finding unassigned warehouse tasks");
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findUnassignedTasks());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTaskDTO> findByDueAtBefore(LocalDateTime dueDate) {
        log.debug("Finding warehouse tasks due before: {}", dueDate);
        return warehouseTaskMapper.toDTOList(warehouseTaskRepository.findByDueAtBefore(dueDate));
    }

    @Override
    public WarehouseTaskDTO create(WarehouseTaskDTO taskDTO) {
        log.debug("Creating new warehouse task: {}", taskDTO);
        
        // Verify warehouse exists
        if (!warehouseRepository.existsById(taskDTO.getWarehouseId().toString())) {
            throw new ResourceNotFoundException("Warehouse", "id", taskDTO.getWarehouseId());
        }
        
        // Verify zone exists if provided
        if (taskDTO.getZoneId() != null && !zoneRepository.existsById(taskDTO.getZoneId())) {
            throw new ResourceNotFoundException("Zone", "id", taskDTO.getZoneId());
        }
        
        // Verify location exists if provided
        if (taskDTO.getLocationId() != null && !locationRepository.existsById(taskDTO.getLocationId().toString())) {
            throw new ResourceNotFoundException("Location", "id", taskDTO.getLocationId());
        }
        
        // Convert DTO to entity
        WarehouseTask task = warehouseTaskMapper.toEntity(taskDTO);
        
        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        
        if (task.getPriority() == null) {
            task.setPriority(Priority.MEDIUM);
        }
        
        // Save the task
        WarehouseTask savedTask = warehouseTaskRepository.save(task);
        log.info("Created new warehouse task with ID: {}", savedTask.getId());
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO update(UUID id, WarehouseTaskDTO taskDTO) {
        log.debug("Updating warehouse task with id: {}", id);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
        
        // Verify warehouse exists if changed
        if (taskDTO.getWarehouseId() != null && !taskDTO.getWarehouseId().equals(existingTask.getWarehouseId())
                && !warehouseRepository.existsById(taskDTO.getWarehouseId().toString())) {
            throw new ResourceNotFoundException("Warehouse", "id", taskDTO.getWarehouseId());
        }
        
        // Verify zone exists if provided
        if (taskDTO.getZoneId() != null && !taskDTO.getZoneId().equals(existingTask.getZoneId())
                && !zoneRepository.existsById(taskDTO.getZoneId())) {
            throw new ResourceNotFoundException("Zone", "id", taskDTO.getZoneId());
        }
        
        // Verify location exists if provided
        if (taskDTO.getLocationId() != null && !taskDTO.getLocationId().equals(existingTask.getLocationId())
                && !locationRepository.existsById(taskDTO.getLocationId().toString())) {
            throw new ResourceNotFoundException("Location", "id", taskDTO.getLocationId());
        }
        
        // Update fields from DTO
        WarehouseTask updatedTask = warehouseTaskMapper.updateEntityFromDTO(existingTask, taskDTO);
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(updatedTask);
        log.info("Updated warehouse task with ID: {}", savedTask.getId());
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO changeStatus(UUID id, TaskStatus status) {
        log.debug("Changing status of warehouse task with id: {} to {}", id, status);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
        
        // Update status
        existingTask.setStatus(status);
        
        // Update timestamps based on status
        if (status == TaskStatus.ASSIGNED && existingTask.getAssignedAt() == null) {
            existingTask.setAssignedAt(LocalDateTime.now());
        } else if (status == TaskStatus.IN_PROGRESS && existingTask.getStartedAt() == null) {
            existingTask.setStartedAt(LocalDateTime.now());
        } else if (status == TaskStatus.COMPLETED && existingTask.getCompletedAt() == null) {
            existingTask.setCompletedAt(LocalDateTime.now());
        }
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(existingTask);
        log.info("Changed status of warehouse task with ID: {} to {}", savedTask.getId(), status);
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO assignTask(UUID taskId, UUID staffId) {
        log.debug("Assigning warehouse task with id: {} to staff with id: {}", taskId, staffId);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", taskId));
        
        // Verify staff exists
        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("Staff", "id", staffId);
        }
        
        // Assign task
        existingTask.setAssignedTo(staffId);
        existingTask.setAssignedAt(LocalDateTime.now());
        existingTask.setStatus(TaskStatus.ASSIGNED);
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(existingTask);
        log.info("Assigned warehouse task with ID: {} to staff with ID: {}", savedTask.getId(), staffId);
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO startTask(UUID id) {
        log.debug("Starting warehouse task with id: {}", id);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
        
        // Verify task is in ASSIGNED status
        if (existingTask.getStatus() != TaskStatus.ASSIGNED) {
            throw new IllegalStateException("Task must be in ASSIGNED status to be started, current status: " + existingTask.getStatus());
        }
        
        // Start task
        existingTask.setStatus(TaskStatus.IN_PROGRESS);
        existingTask.setStartedAt(LocalDateTime.now());
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(existingTask);
        log.info("Started warehouse task with ID: {}", savedTask.getId());
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO completeTask(UUID id, Integer actualDurationMinutes) {
        log.debug("Completing warehouse task with id: {}", id);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
        
        // Verify task is in IN_PROGRESS status
        if (existingTask.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task must be in IN_PROGRESS status to be completed, current status: " + existingTask.getStatus());
        }
        
        // Complete task
        existingTask.setStatus(TaskStatus.COMPLETED);
        existingTask.setCompletedAt(LocalDateTime.now());
        existingTask.setActualDurationMinutes(actualDurationMinutes);
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(existingTask);
        log.info("Completed warehouse task with ID: {}", savedTask.getId());
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO cancelTask(UUID id, String reason) {
        log.debug("Cancelling warehouse task with id: {}", id);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
        
        // Verify task is not already completed or cancelled
        if (existingTask.getStatus() == TaskStatus.COMPLETED || existingTask.getStatus() == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Task is already in " + existingTask.getStatus() + " status and cannot be cancelled");
        }
        
        // Cancel task
        existingTask.setStatus(TaskStatus.CANCELLED);
        existingTask.setNotes(existingTask.getNotes() != null 
                ? existingTask.getNotes() + "\nCancellation reason: " + reason 
                : "Cancellation reason: " + reason);
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(existingTask);
        log.info("Cancelled warehouse task with ID: {}", savedTask.getId());
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public WarehouseTaskDTO changePriority(UUID id, Priority priority) {
        log.debug("Changing priority of warehouse task with id: {} to {}", id, priority);
        
        // Find the existing task
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WarehouseTask", "id", id));
        
        // Update priority
        existingTask.setPriority(priority);
        
        // Save changes
        WarehouseTask savedTask = warehouseTaskRepository.save(existingTask);
        log.info("Changed priority of warehouse task with ID: {} to {}", savedTask.getId(), priority);
        
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Deleting warehouse task with id: {}", id);
        
        // Check if task exists
        if (!warehouseTaskRepository.existsById(id)) {
            throw new ResourceNotFoundException("WarehouseTask", "id", id);
        }
        
        // Delete task
        warehouseTaskRepository.deleteById(id);
        log.info("Deleted warehouse task with ID: {}", id);
    }

    @Override
    @Transactional
    public WarehouseTask createTask(WarehouseTask task) {
        // Validate warehouse exists
        warehouseRepository.findById(task.getWarehouseId().toString())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + task.getWarehouseId()));

        // Validate staff exists if assigned
        if (task.getAssignedToId() != null) {
            staffRepository.findById(task.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id " + task.getAssignedToId()));
        }

        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }

        if (task.getPriority() == null) {
            task.setPriority(Priority.MEDIUM);
        }

        // Set audit and tracking fields
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        if (task.getAssignedToId() != null && task.getStatus() == TaskStatus.PENDING) {
            task.setStatus(TaskStatus.ASSIGNED);
            task.setAssignedAt(now);
        }

        return warehouseTaskRepository.save(task);
    }

    @Override
    @Transactional
    public WarehouseTask updateTask(UUID id, WarehouseTask task) {
        WarehouseTask existingTask = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));

        // Don't allow changing the warehouse
        if (!existingTask.getWarehouseId().equals(task.getWarehouseId())) {
            throw new IllegalArgumentException("Cannot change the warehouse of a task");
        }

        // Update fields
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setType(task.getType());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setZoneId(task.getZoneId());
        existingTask.setLocationId(task.getLocationId());
        existingTask.setReferenceId(task.getReferenceId());
        existingTask.setReferenceType(task.getReferenceType());
        existingTask.setNotes(task.getNotes());
        existingTask.setProperties(task.getProperties());
        
        // Don't update status, assignedToId through this method
        // Status should be changed through updateTaskStatus method
        // Staff assignment should be changed through assignTaskToStaff method

        // Update audit fields
        existingTask.setUpdatedAt(LocalDateTime.now());

        return warehouseTaskRepository.save(existingTask);
    }

    @Override
    public Optional<WarehouseTask> getTask(UUID id) {
        return warehouseTaskRepository.findById(id);
    }

    @Override
    public List<WarehouseTask> getTasksByWarehouseId(UUID warehouseId) {
        return warehouseTaskRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<WarehouseTask> getTasksByAssignedToId(UUID staffId) {
        return warehouseTaskRepository.findByAssignedToId(staffId);
    }

    @Override
    public List<WarehouseTask> getTasksByStatus(TaskStatus status) {
        return warehouseTaskRepository.findByStatus(status);
    }

    @Override
    public List<WarehouseTask> getTasksByType(TaskType type) {
        return warehouseTaskRepository.findByType(type);
    }

    @Override
    public List<WarehouseTask> getTasksByWarehouseIdAndStatus(UUID warehouseId, TaskStatus status) {
        return warehouseTaskRepository.findByWarehouseIdAndStatus(warehouseId, status);
    }

    @Override
    public List<WarehouseTask> getTasksByWarehouseIdAndType(UUID warehouseId, TaskType type) {
        return warehouseTaskRepository.findByWarehouseIdAndType(warehouseId, type);
    }

    @Override
    public List<WarehouseTask> getTasksByWarehouseIdAndPriority(UUID warehouseId, Priority priority) {
        return warehouseTaskRepository.findByWarehouseIdAndPriority(warehouseId, priority);
    }

    @Override
    public List<WarehouseTask> getTasksDueBefore(LocalDateTime date) {
        return warehouseTaskRepository.findByDueAtBefore(date);
    }

    @Override
    public List<WarehouseTask> getUnassignedTasks(UUID warehouseId, TaskStatus status) {
        return warehouseTaskRepository.findUnassignedTasksForWarehouse(warehouseId, status);
    }

    @Override
    @Transactional
    public WarehouseTask assignTaskToStaff(UUID taskId, UUID staffId) {
        WarehouseTask task = warehouseTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + taskId));

        // Validate staff exists
        staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id " + staffId));

        // Set assignment
        task.setAssignedToId(staffId);
        
        // Update status if it's in PENDING state
        if (task.getStatus() == TaskStatus.PENDING) {
            task.setStatus(TaskStatus.ASSIGNED);
        }
        
        // Set assignment time
        task.setAssignedAt(LocalDateTime.now());
        
        // Update audit fields
        task.setUpdatedAt(LocalDateTime.now());

        return warehouseTaskRepository.save(task);
    }

    @Override
    @Transactional
    public WarehouseTask updateTaskStatus(UUID id, TaskStatus status) {
        WarehouseTask task = warehouseTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));

        LocalDateTime now = LocalDateTime.now();
        
        // Validate transition
        if (!task.getStatus().canTransitionTo(status)) {
            throw new IllegalStateException("Invalid task status transition from " + task.getStatus() + " to " + status);
        }

        // Update status and tracking fields based on new status
        task.setStatus(status);
        task.setUpdatedAt(now);
        
        if (status == TaskStatus.IN_PROGRESS && task.getStartedAt() == null) {
            task.setStartedAt(now);
        } else if (status == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(now);
            if (task.getStartedAt() != null) {
                long minutes = java.time.Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes();
                task.setActualDurationMinutes((int) minutes);
            }
        } else if (status == TaskStatus.CANCELLED && task.getCancelledAt() == null) {
            task.setCancelledAt(now);
        }

        return warehouseTaskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(UUID id) {
        if (!warehouseTaskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id " + id);
        }
        warehouseTaskRepository.deleteById(id);
    }

    @Override
    public List<WarehouseTask> findTasksAssignedToStaff(UUID staffId) {
        log.info("Finding tasks assigned to staff: {}", staffId);
        return warehouseTaskRepository.findByAssignedToId(staffId);
    }

    @Override
    public WarehouseTaskDTO updateTaskStatus(UUID taskId, TaskStatus status, String note) {
        log.info("Updating task status - task: {}, status: {}", taskId, status);
        
        WarehouseTask task = warehouseTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        
        // Record previous status for logging/auditing
        TaskStatus previousStatus = task.getStatus();
        
        // Update task status
        task.setStatus(status);
        
        // Add note if provided
        if (note != null && !note.trim().isEmpty()) {
            String statusNote = "Status changed from " + previousStatus + " to " + status + ": " + note;
            if (task.getNotes() == null || task.getNotes().trim().isEmpty()) {
                task.setNotes(statusNote);
            } else {
                task.setNotes(task.getNotes() + "\n" + statusNote);
            }
        }
        
        // Set completion time if task is now completed
        if (status == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        // Save updated task
        WarehouseTask savedTask = warehouseTaskRepository.save(task);
        
        // Convert to DTO and return
        return warehouseTaskMapper.toDTO(savedTask);
    }

    @Override
    public long countPendingTasksForStaff(UUID staffId) {
        log.info("Counting pending tasks for staff: {}", staffId);
        
        List<WarehouseTask> tasks = warehouseTaskRepository.findByAssignedToId(staffId);
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING || task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
    }
} 
