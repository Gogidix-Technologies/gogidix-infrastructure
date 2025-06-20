package com.exalt.warehousing.task.service;

import com.exalt.warehousing.task.dto.TaskBulkAssignmentDTO;
import com.exalt.warehousing.task.dto.TaskFilterDTO;
import com.exalt.warehousing.task.dto.WarehouseTaskDTO;
import com.exalt.warehousing.task.event.TaskAssignedEvent;
import com.exalt.warehousing.task.event.TaskCreatedEvent;
import com.exalt.warehousing.task.event.TaskStatusUpdatedEvent;
import com.exalt.warehousing.task.model.WarehouseTask;
import com.exalt.warehousing.task.repository.WarehouseTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of warehouse task service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseTaskServiceImpl implements WarehouseTaskService {

    private final WarehouseTaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public WarehouseTaskDTO createTask(WarehouseTaskDTO taskDTO) {
        log.info("Creating new warehouse task");
        
        // Generate task number if not provided
        if (taskDTO.getTaskNumber() == null) {
            taskDTO.setTaskNumber(generateTaskNumber());
        }
        
        // Convert DTO to entity
        WarehouseTask task = taskDTO.toEntity();
        
        // Set initial state if not set
        if (task.getStatus() == null) {
            task.setStatus(WarehouseTask.TaskStatus.PENDING);
        }
        
        // Save to repository
        WarehouseTask savedTask = taskRepository.save(task);
        
        // Publish task created event
        publishTaskCreatedEvent(savedTask);
        
        return WarehouseTaskDTO.fromEntity(savedTask);
    }

    @Override
    @Transactional
    public List<WarehouseTaskDTO> createBulkTasks(List<WarehouseTaskDTO> taskDTOs) {
        log.info("Creating {} warehouse tasks in bulk", taskDTOs.size());
        
        List<WarehouseTask> tasks = taskDTOs.stream()
                .map(dto -> {
                    // Generate task number if not provided
                    if (dto.getTaskNumber() == null) {
                        dto.setTaskNumber(generateTaskNumber());
                    }
                    
                    // Convert DTO to entity
                    WarehouseTask task = dto.toEntity();
                    
                    // Set initial state if not set
                    if (task.getStatus() == null) {
                        task.setStatus(WarehouseTask.TaskStatus.PENDING);
                    }
                    
                    return task;
                })
                .collect(Collectors.toList());
        
        // Save all tasks
        List<WarehouseTask> savedTasks = taskRepository.saveAll(tasks);
        
        // Publish events for each task
        savedTasks.forEach(this::publishTaskCreatedEvent);
        
        // Convert back to DTOs
        return savedTasks.stream()
                .map(WarehouseTaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseTaskDTO getTaskById(UUID taskId) {
        log.info("Fetching warehouse task by ID: {}", taskId);
        
        WarehouseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
                
        return WarehouseTaskDTO.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseTaskDTO getTaskByNumber(String taskNumber) {
        log.info("Fetching warehouse task by number: {}", taskNumber);
        
        WarehouseTask task = taskRepository.findByTaskNumber(taskNumber)
                .orElseThrow(() -> new RuntimeException("Task not found with number: " + taskNumber));
                
        return WarehouseTaskDTO.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getAllTasks(Pageable pageable) {
        log.info("Fetching all warehouse tasks with pagination");
        
        return taskRepository.findAll(pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getTasksByWarehouse(UUID warehouseId, Pageable pageable) {
        log.info("Fetching warehouse tasks for warehouse ID: {}", warehouseId);
        
        return taskRepository.findByWarehouseId(warehouseId, pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getTasksByStatus(WarehouseTask.TaskStatus status, Pageable pageable) {
        log.info("Fetching warehouse tasks with status: {}", status);
        
        return taskRepository.findByStatus(status, pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getTasksByType(WarehouseTask.TaskType taskType, Pageable pageable) {
        log.info("Fetching warehouse tasks with type: {}", taskType);
        
        return taskRepository.findByTaskType(taskType, pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getTasksAssignedToStaff(UUID staffId, Pageable pageable) {
        log.info("Fetching warehouse tasks assigned to staff ID: {}", staffId);
        
        return taskRepository.findByAssignedTo(staffId, pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getOverdueTasks(LocalDateTime currentTime, Pageable pageable) {
        log.info("Fetching overdue warehouse tasks as of: {}", currentTime);
        
        return taskRepository.findOverdueTasks(currentTime, pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> getHighPriorityTasks(Pageable pageable) {
        log.info("Fetching high priority warehouse tasks");
        
        return taskRepository.findHighPriorityTasks(pageable)
                .map(WarehouseTaskDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseTaskDTO> filterTasks(TaskFilterDTO filterDTO, Pageable pageable) {
        log.info("Filtering warehouse tasks with criteria: {}", filterDTO);
        
        // For now, implementing a basic filter by warehouse ID
        // In a real implementation, you would use a Specification or QueryDSL
        // to build a dynamic query based on the filter criteria
        
        if (filterDTO.getWarehouseId() != null) {
            return getTasksByWarehouse(filterDTO.getWarehouseId(), pageable);
        }
        
        if (filterDTO.getOverdueOnly() != null && filterDTO.getOverdueOnly()) {
            return getOverdueTasks(LocalDateTime.now(), pageable);
        }
        
        // Default to all tasks if no specific filter is applied
        return getAllTasks(pageable);
    }
    
    // Helper method to generate a unique task number
    private String generateTaskNumber() {
        return "TASK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    // Helper method to publish task created event
    private void publishTaskCreatedEvent(WarehouseTask task) {
        TaskCreatedEvent event = TaskCreatedEvent.create(
                (UUID.fromString(task.getId())),
                task.getTaskNumber(),
                task.getTaskType(),
                task.getWarehouseId(),
                task.getPriority()
        );
        
        eventPublisher.publishEvent(event);
        log.debug("Published task created event for task ID: {}", task.getId());
    }

    @Override
    @Transactional
    public WarehouseTaskDTO updateTask(WarehouseTaskDTO taskDTO) {
        log.info("Updating warehouse task with ID: {}", taskDTO.getId());
        WarehouseTask existing = taskRepository.findById(taskDTO.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskDTO.getId()));

        taskDTO.setTaskNumber(existing.getTaskNumber());
        taskDTO.setCreatedAt(existing.getCreatedAt());
        WarehouseTask saved = taskRepository.save(taskDTO.toEntity());
        return WarehouseTaskDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public WarehouseTaskDTO assignTask(UUID taskId, UUID staffId) {
        log.info("Assigning warehouse task {} to staff {}", taskId, staffId);
        WarehouseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        WarehouseTask.TaskStatus prevStatus = task.getStatus();
        task.assignTask(staffId);
        WarehouseTask saved = taskRepository.save(task);
        publishTaskAssignedEvent(saved);
        publishTaskStatusUpdatedEvent(saved, prevStatus);
        return WarehouseTaskDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public List<WarehouseTaskDTO> bulkAssignTasks(TaskBulkAssignmentDTO dto) {
        log.info("Bulk assigning {} tasks to staff {}", dto.getTaskIds().size(), dto.getStaffId());
        List<WarehouseTask> tasks = taskRepository.findAllById(dto.getTaskIds());
        tasks.forEach(t -> {
            WarehouseTask.TaskStatus prev = t.getStatus();
            t.assignTask(dto.getStaffId());
            publishTaskAssignedEvent(t);
            publishTaskStatusUpdatedEvent(t, prev);
        });
        List<WarehouseTask> saved = taskRepository.saveAll(tasks);
        return saved.stream().map(WarehouseTaskDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarehouseTaskDTO startTask(UUID taskId) {
        log.info("Starting warehouse task {}", taskId);
        WarehouseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        WarehouseTask.TaskStatus prev = task.getStatus();
        if (prev != WarehouseTask.TaskStatus.ASSIGNED && prev != WarehouseTask.TaskStatus.ON_HOLD) {
            throw new RuntimeException("Task cannot be started from status: " + prev);
        }
        task.startTask();
        WarehouseTask saved = taskRepository.save(task);
        publishTaskStatusUpdatedEvent(saved, prev);
        return WarehouseTaskDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public WarehouseTaskDTO completeTask(UUID taskId, String completionNotes) {
        log.info("Completing warehouse task {}", taskId);
        WarehouseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        if (task.getStatus() != WarehouseTask.TaskStatus.IN_PROGRESS) {
            throw new RuntimeException("Task cannot be completed from status: " + task.getStatus());
        }
        task.completeTask(completionNotes);
        WarehouseTask saved = taskRepository.save(task);
        publishTaskStatusUpdatedEvent(saved, WarehouseTask.TaskStatus.IN_PROGRESS);
        return WarehouseTaskDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public WarehouseTaskDTO cancelTask(UUID taskId, String cancellationReason) {
        log.info("Cancelling warehouse task {}", taskId);
        WarehouseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        WarehouseTask.TaskStatus prev = task.getStatus();
        task.cancelTask(cancellationReason);
        WarehouseTask saved = taskRepository.save(task);
        publishTaskStatusUpdatedEvent(saved, prev);
        return WarehouseTaskDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public WarehouseTaskDTO holdTask(UUID taskId) {
        log.info("Putting warehouse task {} on hold", taskId);
        WarehouseTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        WarehouseTask.TaskStatus prev = task.getStatus();
        task.holdTask();
        WarehouseTask saved = taskRepository.save(task);
        publishTaskStatusUpdatedEvent(saved, prev);
        return WarehouseTaskDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId) {
        log.info("Deleting warehouse task {}", taskId);
        taskRepository.deleteById(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStatistics(UUID warehouseId) {
        Map<String, Long> byStatus = getTaskCountByStatus(warehouseId);
        Map<String, Long> byType = getTaskCountByType(warehouseId);
        return Map.of("countByStatus", byStatus, "countByType", byType);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTaskCountByStatus(UUID warehouseId) {
        List<Object[]> raw = taskRepository.countByStatus();
        return raw.stream().collect(Collectors.toMap(o -> o[0].toString(), o -> (Long) o[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTaskCountByType(UUID warehouseId) {
        List<Object[]> raw = taskRepository.countByTaskType();
        return raw.stream().collect(Collectors.toMap(o -> o[0].toString(), o -> (Long) o[1]));
    }

    private void publishTaskAssignedEvent(WarehouseTask task) {
        TaskAssignedEvent event = TaskAssignedEvent.create(
                (UUID.fromString(task.getId())),
                task.getTaskNumber(),
                task.getTaskType(),
                task.getWarehouseId(),
                task.getAssignedTo(),
                task.getPriority()
        );
        eventPublisher.publishEvent(event);
    }

    private void publishTaskStatusUpdatedEvent(WarehouseTask task, WarehouseTask.TaskStatus previousStatus) {
        TaskStatusUpdatedEvent event = TaskStatusUpdatedEvent.create(
                (UUID.fromString(task.getId())),
                task.getTaskNumber(),
                previousStatus,
                task.getStatus()
        );
        eventPublisher.publishEvent(event);
    }
}