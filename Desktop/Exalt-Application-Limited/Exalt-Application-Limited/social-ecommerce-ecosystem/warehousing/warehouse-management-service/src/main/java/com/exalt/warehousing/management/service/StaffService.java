package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.StaffDTO;
import com.exalt.warehousing.management.model.Staff;
import com.exalt.warehousing.management.model.StaffRole;
import com.exalt.warehousing.management.model.StaffStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing staff operations
 */
public interface StaffService {

    /**
     * Create a new staff member
     *
     * @param staff the staff member to create
     * @return the created staff member
     */
    Staff createStaff(Staff staff);

    /**
     * Update an existing staff member
     *
     * @param id the staff id
     * @param staff the staff data to update
     * @return the updated staff member
     */
    Staff updateStaff(UUID id, Staff staff);

    /**
     * Get a staff member by id
     *
     * @param id the staff id
     * @return the staff member if found
     */
    Optional<Staff> getStaff(UUID id);

    /**
     * Get a staff member by employee id
     *
     * @param employeeId the employee id
     * @return the staff member if found
     */
    Optional<Staff> getStaffByEmployeeId(String employeeId);

    /**
     * Get a staff member by email
     *
     * @param email the email address
     * @return the staff member if found
     */
    Optional<Staff> getStaffByEmail(String email);

    /**
     * Get all staff members for a warehouse
     *
     * @param warehouseId the warehouse id
     * @return list of staff members in the warehouse
     */
    List<Staff> getStaffByWarehouseId(UUID warehouseId);
    
    /**
     * Find all staff members for a warehouse and return as DTOs
     *
     * @param warehouseId the warehouse id
     * @return list of staff DTOs in the warehouse
     */
    List<StaffDTO> findByWarehouseId(UUID warehouseId);

    /**
     * Get all staff members with a specific role
     *
     * @param role the staff role
     * @return list of staff members with the given role
     */
    List<Staff> getStaffByRole(StaffRole role);

    /**
     * Get all staff members with a specific status
     *
     * @param status the staff status
     * @return list of staff members with the given status
     */
    List<Staff> getStaffByStatus(StaffStatus status);

    /**
     * Get all staff members in a warehouse with a specific role
     *
     * @param warehouseId the warehouse id
     * @param role the staff role
     * @return list of staff members matching the criteria
     */
    List<Staff> getStaffByWarehouseIdAndRole(UUID warehouseId, StaffRole role);

    /**
     * Get all staff members in a warehouse with a specific status
     *
     * @param warehouseId the warehouse id
     * @param status the staff status
     * @return list of staff members matching the criteria
     */
    List<Staff> getStaffByWarehouseIdAndStatus(UUID warehouseId, StaffStatus status);

    /**
     * Assign staff to a zone
     *
     * @param staffId the staff id
     * @param zoneId the zone id
     * @return the updated staff member
     */
    Staff assignStaffToZone(UUID staffId, UUID zoneId);

    /**
     * Update staff status
     *
     * @param id the staff id
     * @param status the new status
     * @return the updated staff member
     */
    Staff updateStaffStatus(UUID id, StaffStatus status);

    /**
     * Delete a staff member
     *
     * @param id the staff id
     */
    void deleteStaff(UUID id);

    /**
     * Retrieve all staff members as DTOs
     *
     * @return list of staff DTOs
     */
    List<StaffDTO> findAll();

    /**
     * Find a staff member by id and return as DTO
     */
    StaffDTO findById(UUID id);

    /**
     * Find a staff member by employee id and return as DTO
     */
    StaffDTO findByEmployeeId(String employeeId);

    /**
     * Find a staff member by associated user id and return as DTO
     */
    StaffDTO findByUserId(UUID userId);

    /**
     * Find a staff member by email and return as DTO
     */
    StaffDTO findByEmail(String email);

    /**
     * Get staff members by role (DTO list)
     */
    List<StaffDTO> findByRole(StaffRole role);

    /**
     * Get staff members by status (DTO list)
     */
    List<StaffDTO> findByStatus(StaffStatus status);

    /**
     * Get all staff members in a warehouse with a specific role and return as DTOs
     */
    List<StaffDTO> findByWarehouseIdAndRole(UUID warehouseId, StaffRole role);

    /**
     * Get all staff members in a zone and return as DTOs
     */
    List<StaffDTO> findByZoneId(UUID zoneId);

    /**
     * Create a new staff member via DTO
     */
    StaffDTO create(StaffDTO staffDTO);

    /**
     * Update an existing staff member via DTO
     */
    StaffDTO update(UUID id, StaffDTO staffDTO);

    /**
     * Change status of a staff member and return DTO
     */
    StaffDTO changeStatus(UUID id, StaffStatus status);

    /**
     * Assign staff to zone and return DTO
     */
    StaffDTO assignToZone(UUID staffId, UUID zoneId);
} 
