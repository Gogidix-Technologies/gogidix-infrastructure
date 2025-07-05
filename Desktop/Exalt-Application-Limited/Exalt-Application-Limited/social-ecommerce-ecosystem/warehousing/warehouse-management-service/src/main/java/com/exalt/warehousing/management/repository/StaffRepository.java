package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.Staff;
import com.exalt.warehousing.management.model.StaffRole;
import com.exalt.warehousing.management.model.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Staff entities
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    
    /**
     * Find staff members by warehouse id
     * 
     * @param warehouseId the warehouse id
     * @return list of staff in the warehouse
     */
    List<Staff> findByWarehouseId(UUID warehouseId);
    
    /**
     * Find staff by employee id
     * 
     * @param employeeId the employee id
     * @return the staff member if found
     */
    Optional<Staff> findByEmployeeId(String employeeId);
    
    /**
     * Find staff by user id
     * 
     * @param userId the user id
     * @return the staff member if found
     */
    Optional<Staff> findByUserId(UUID userId);
    
    /**
     * Find staff by warehouse id and role
     * 
     * @param warehouseId the warehouse id
     * @param role the staff role
     * @return list of staff matching criteria
     */
    List<Staff> findByWarehouseIdAndRole(UUID warehouseId, StaffRole role);
    
    /**
     * Find staff by warehouse id and status
     * 
     * @param warehouseId the warehouse id
     * @param status the staff status
     * @return list of staff matching criteria
     */
    List<Staff> findByWarehouseIdAndStatus(UUID warehouseId, StaffStatus status);
    
    /**
     * Find staff by zone id
     * 
     * @param zoneId the zone id
     * @return list of staff assigned to the zone
     */
    List<Staff> findByZoneId(UUID zoneId);
    
    /**
     * Find staff by role
     * 
     * @param role the staff role
     * @return list of staff with given role
     */
    List<Staff> findByRole(StaffRole role);
    
    /**
     * Find staff by status
     * 
     * @param status the staff status
     * @return list of staff with given status
     */
    List<Staff> findByStatus(StaffStatus status);
    
    /**
     * Find staff by email
     * 
     * @param email the staff email
     * @return the staff member if found
     */
    Optional<Staff> findByEmail(String email);
} 
