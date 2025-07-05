package com.exalt.warehousing.management.mapper;

import com.exalt.warehousing.management.dto.StaffDTO;
import com.exalt.warehousing.management.model.Staff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Staff entity and DTO
 */
@Component
public class StaffMapper {
    
    /**
     * Convert Staff entity to StaffDTO
     * 
     * @param staff the entity to convert
     * @return the converted DTO
     */
    public StaffDTO toDTO(Staff staff) {
        if (staff == null) {
            return null;
        }
        
        return StaffDTO.builder()
                .id(UUID.fromString(staff.getId()))
                .userId(staff.getUserId() != null ? new UUID(0L, staff.getUserId()) : null)
                .warehouseId((UUID.fromString(String.valueOf(staff.getWarehouseId()))))
                .employeeId(staff.getEmployeeId())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .role(staff.getRole())
                .status(staff.getStatus())
                .zoneId((UUID.fromString(String.valueOf(staff.getZoneId()))))
                .maxTasksPerDay(staff.getMaxTasksPerDay())
                .notes(staff.getNotes())
                .hireDate(staff.getHireDate() != null ? staff.getHireDate().atStartOfDay() : null)
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert StaffDTO to Staff entity
     * 
     * @param staffDTO the DTO to convert
     * @return the converted entity
     */
    public Staff toEntity(StaffDTO staffDTO) {
        if (staffDTO == null) {
            return null;
        }
        
        return Staff.builder()
                .id(staffDTO.getId() != null ? staffDTO.getId().toString() : null)
                .userId(staffDTO.getUserId() != null ? (long) Math.abs(staffDTO.getUserId().hashCode()) : null)
                .warehouseId(staffDTO.getWarehouseId() != null ? staffDTO.getWarehouseId().toString() : null)
                .employeeId(staffDTO.getEmployeeId())
                .firstName(staffDTO.getFirstName())
                .lastName(staffDTO.getLastName())
                .email(staffDTO.getEmail())
                .phoneNumber(staffDTO.getPhoneNumber())
                .role(staffDTO.getRole())
                .status(staffDTO.getStatus())
                .zoneId(staffDTO.getZoneId() != null ? staffDTO.getZoneId().toString() : null)
                .maxTasksPerDay(staffDTO.getMaxTasksPerDay())
                .notes(staffDTO.getNotes())
                .hireDate(staffDTO.getHireDate() != null ? staffDTO.getHireDate().toLocalDate() : null)
                .build();
    }
    
    /**
     * Update Staff entity from StaffDTO
     * 
     * @param staff the entity to update
     * @param staffDTO the DTO with new values
     * @return the updated entity
     */
    public Staff updateEntityFromDTO(Staff staff, StaffDTO staffDTO) {
        if (staffDTO == null) {
            return staff;
        }
        
        if (staffDTO.getUserId() != null) {
            staff.setUserId((long) Math.abs(staffDTO.getUserId().hashCode()));
        }
        if (staffDTO.getWarehouseId() != null) {
            staff.setWarehouseId(staffDTO.getWarehouseId().toString());
        }
        if (staffDTO.getEmployeeId() != null) {
            staff.setEmployeeId(staffDTO.getEmployeeId());
        }
        if (staffDTO.getFirstName() != null) {
            staff.setFirstName(staffDTO.getFirstName());
        }
        if (staffDTO.getLastName() != null) {
            staff.setLastName(staffDTO.getLastName());
        }
        if (staffDTO.getEmail() != null) {
            staff.setEmail(staffDTO.getEmail());
        }
        staff.setPhoneNumber(staffDTO.getPhoneNumber());
        if (staffDTO.getRole() != null) {
            staff.setRole(staffDTO.getRole());
        }
        if (staffDTO.getStatus() != null) {
            staff.setStatus(staffDTO.getStatus());
        }
        if (staffDTO.getZoneId() != null) {
            staff.setZoneId(staffDTO.getZoneId().toString());
        }
        staff.setMaxTasksPerDay(staffDTO.getMaxTasksPerDay());
        staff.setNotes(staffDTO.getNotes());
        if (staffDTO.getHireDate() != null) {
            staff.setHireDate(staffDTO.getHireDate().toLocalDate());
        }
        
        return staff;
    }
    
    /**
     * Convert list of Staff entities to list of StaffDTOs
     * 
     * @param staffList the list of entities to convert
     * @return the list of converted DTOs
     */
    public List<StaffDTO> toDTOList(List<Staff> staffList) {
        return staffList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 
