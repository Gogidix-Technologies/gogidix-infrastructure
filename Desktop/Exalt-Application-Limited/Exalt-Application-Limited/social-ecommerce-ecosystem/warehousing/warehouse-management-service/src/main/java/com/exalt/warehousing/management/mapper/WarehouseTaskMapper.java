package com.exalt.warehousing.management.mapper;

import com.exalt.warehousing.management.dto.WarehouseTaskDTO;
import com.exalt.warehousing.management.model.WarehouseTask;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between WarehouseTask entity and DTO
 */
@Component
public class WarehouseTaskMapper {
    
    /**
     * Convert WarehouseTask entity to WarehouseTaskDTO
     * 
     * @param task the entity to convert
     * @return the converted DTO
     */
    public WarehouseTaskDTO toDTO(WarehouseTask task) {
        if (task == null) {
            return null;
        }
        
        return WarehouseTaskDTO.builder()
                .id(task.getId())
                .warehouseId(task.getWarehouseId())
                .referenceId(task.getReferenceId())
                .referenceType(task.getReferenceType())
                .type(task.getType())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .zoneId(task.getZoneId())
                .locationId(task.getLocationId())
                .assignedTo(task.getAssignedTo())
                .assignedAt(task.getAssignedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .dueAt(task.getDueAt())
                .estimatedDurationMinutes(task.getEstimatedDurationMinutes())
                .actualDurationMinutes(task.getActualDurationMinutes())
                .notes(task.getNotes())
                .createdBy(task.getCreatedBy())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert WarehouseTaskDTO to WarehouseTask entity
     * 
     * @param taskDTO the DTO to convert
     * @return the converted entity
     */
    public WarehouseTask toEntity(WarehouseTaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }
        
        return WarehouseTask.builder()
                .id(taskDTO.getId())
                .warehouseId(taskDTO.getWarehouseId())
                .referenceId(taskDTO.getReferenceId())
                .referenceType(taskDTO.getReferenceType())
                .type(taskDTO.getType())
                .description(taskDTO.getDescription())
                .status(taskDTO.getStatus())
                .priority(taskDTO.getPriority())
                .zoneId(taskDTO.getZoneId())
                .locationId(taskDTO.getLocationId())
                .assignedTo(taskDTO.getAssignedTo())
                .assignedAt(taskDTO.getAssignedAt())
                .startedAt(taskDTO.getStartedAt())
                .completedAt(taskDTO.getCompletedAt())
                .dueAt(taskDTO.getDueAt())
                .estimatedDurationMinutes(taskDTO.getEstimatedDurationMinutes())
                .actualDurationMinutes(taskDTO.getActualDurationMinutes())
                .notes(taskDTO.getNotes())
                .createdBy(taskDTO.getCreatedBy())
                .createdAt(taskDTO.getCreatedAt())
                .updatedAt(taskDTO.getUpdatedAt())
                .build();
    }
    
    /**
     * Update WarehouseTask entity from WarehouseTaskDTO
     * 
     * @param task the entity to update
     * @param taskDTO the DTO with new values
     * @return the updated entity
     */
    public WarehouseTask updateEntityFromDTO(WarehouseTask task, WarehouseTaskDTO taskDTO) {
        if (taskDTO == null) {
            return task;
        }
        
        if (taskDTO.getWarehouseId() != null) {
            task.setWarehouseId(taskDTO.getWarehouseId());
        }
        task.setReferenceId(taskDTO.getReferenceId());
        task.setReferenceType(taskDTO.getReferenceType());
        if (taskDTO.getType() != null) {
            task.setType(taskDTO.getType());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        task.setZoneId(taskDTO.getZoneId());
        task.setLocationId(taskDTO.getLocationId());
        task.setAssignedTo(taskDTO.getAssignedTo());
        if (taskDTO.getAssignedAt() != null) {
            task.setAssignedAt(taskDTO.getAssignedAt());
        }
        if (taskDTO.getStartedAt() != null) {
            task.setStartedAt(taskDTO.getStartedAt());
        }
        if (taskDTO.getCompletedAt() != null) {
            task.setCompletedAt(taskDTO.getCompletedAt());
        }
        if (taskDTO.getDueAt() != null) {
            task.setDueAt(taskDTO.getDueAt());
        }
        task.setEstimatedDurationMinutes(taskDTO.getEstimatedDurationMinutes());
        task.setActualDurationMinutes(taskDTO.getActualDurationMinutes());
        task.setNotes(taskDTO.getNotes());
        
        return task;
    }
    
    /**
     * Convert list of WarehouseTask entities to list of WarehouseTaskDTOs
     * 
     * @param taskList the list of entities to convert
     * @return the list of converted DTOs
     */
    public List<WarehouseTaskDTO> toDTOList(List<WarehouseTask> taskList) {
        return taskList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 
