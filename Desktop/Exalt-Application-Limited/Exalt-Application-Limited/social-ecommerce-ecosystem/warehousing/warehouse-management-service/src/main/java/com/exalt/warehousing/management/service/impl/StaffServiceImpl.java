package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.management.dto.StaffDTO;
import com.exalt.warehousing.management.exception.DuplicateResourceException;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.mapper.StaffMapper;
import com.exalt.warehousing.management.model.Staff;
import com.exalt.warehousing.management.model.StaffRole;
import com.exalt.warehousing.management.model.StaffStatus;
import com.exalt.warehousing.management.repository.StaffRepository;
import com.exalt.warehousing.management.repository.WarehouseRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the StaffService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffServiceImpl implements StaffService {
    
    private final StaffRepository staffRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final StaffMapper staffMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<StaffDTO> findAll() {
        log.debug("Finding all staff members");
        return staffMapper.toDTOList(staffRepository.findAll());
    }
    
    @Override
    @Transactional(readOnly = true)
    public StaffDTO findById(UUID id) {
        log.debug("Finding staff member by id: {}", id);
        return staffRepository.findById(id)
                .map(staffMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public StaffDTO findByEmployeeId(String employeeId) {
        log.debug("Finding staff member by employee id: {}", employeeId);
        return staffRepository.findByEmployeeId(employeeId)
                .map(staffMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "employeeId", employeeId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public StaffDTO findByUserId(UUID userId) {
        log.debug("Finding staff member by user id: {}", userId);
        return staffRepository.findByUserId(userId)
                .map(staffMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "userId", userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public StaffDTO findByEmail(String email) {
        log.debug("Finding staff member by email: {}", email);
        return staffRepository.findByEmail(email)
                .map(staffMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "email", email));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StaffDTO> findByWarehouseId(UUID warehouseId) {
        log.debug("Finding staff members by warehouse id: {}", warehouseId);
        List<Staff> staffList = staffRepository.findByWarehouseId(warehouseId);
        return staffMapper.toDTOList(staffList);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StaffDTO> findByRole(StaffRole role) {
        log.debug("Finding staff members by role: {}", role);
        return staffMapper.toDTOList(staffRepository.findByRole(role));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StaffDTO> findByStatus(StaffStatus status) {
        log.debug("Finding staff members by status: {}", status);
        return staffMapper.toDTOList(staffRepository.findByStatus(status));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StaffDTO> findByWarehouseIdAndRole(UUID warehouseId, StaffRole role) {
        log.debug("Finding staff members by warehouse id: {} and role: {}", warehouseId, role);
        return staffMapper.toDTOList(staffRepository.findByWarehouseIdAndRole(warehouseId, role));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StaffDTO> findByZoneId(UUID zoneId) {
        log.debug("Finding staff members by zone id: {}", zoneId);
        return staffMapper.toDTOList(staffRepository.findByZoneId(zoneId));
    }
    
    @Override
    @Transactional
    public StaffDTO create(StaffDTO staffDTO) {
        log.debug("Creating new staff member: {}", staffDTO);
        
        // Check if staff with employee ID already exists
        staffRepository.findByEmployeeId(staffDTO.getEmployeeId()).ifPresent(staff -> {
            throw new IllegalArgumentException("Staff member with employee ID " + staffDTO.getEmployeeId() + " already exists");
        });
        
        // Check if staff with email already exists
        staffRepository.findByEmail(staffDTO.getEmail()).ifPresent(staff -> {
            throw new IllegalArgumentException("Staff member with email " + staffDTO.getEmail() + " already exists");
        });
        
        // Convert DTO to entity and save
        Staff staff = staffMapper.toEntity(staffDTO);
        
        // Ensure default values are set
        if (staff.getStatus() == null) {
            staff.setStatus(StaffStatus.ACTIVE);
        }
        
        // Validate warehouse exists
        warehouseRepository.findById(staff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + staff.getWarehouseId()));
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        staff.setCreatedAt(now);
        staff.setUpdatedAt(now);
        
        Staff savedStaff = staffRepository.save(staff);
        log.info("Created new staff member with ID: {}", savedStaff.getId());
        
        return staffMapper.toDTO(savedStaff);
    }
    
    @Override
    @Transactional
    public StaffDTO update(UUID id, StaffDTO staffDTO) {
        log.debug("Updating staff member with id: {}", id);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        
        // Check if updated employee ID conflicts with another staff member
        if (staffDTO.getEmployeeId() != null && !staffDTO.getEmployeeId().equals(existingStaff.getEmployeeId())) {
            staffRepository.findByEmployeeId(staffDTO.getEmployeeId()).ifPresent(staff -> {
                throw new DuplicateResourceException("Staff member with employee ID " + staffDTO.getEmployeeId() + " already exists");
            });
        }
        
        // Check if updated email conflicts with another staff member
        if (staffDTO.getEmail() != null && !staffDTO.getEmail().equals(existingStaff.getEmail())) {
            staffRepository.findByEmail(staffDTO.getEmail()).ifPresent(staff -> {
                throw new DuplicateResourceException("Staff member with email " + staffDTO.getEmail() + " already exists");
            });
        }
        
        // Update fields from DTO
        Staff updatedStaff = staffMapper.updateEntityFromDTO(existingStaff, staffDTO);
        
        // Validate warehouse exists
        warehouseRepository.findById(updatedStaff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + updatedStaff.getWarehouseId()));
        
        // Update audit fields
        updatedStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(updatedStaff);
        log.info("Updated staff member with ID: {}", savedStaff.getId());
        
        return staffMapper.toDTO(savedStaff);
    }
    
    @Override
    @Transactional
    public StaffDTO changeStatus(UUID id, StaffStatus status) {
        log.debug("Changing status of staff member with id: {} to {}", id, status);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        
        // Update status
        existingStaff.setStatus(status);
        
        // Validate warehouse exists
        warehouseRepository.findById(existingStaff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + existingStaff.getWarehouseId()));
        
        // Update audit fields
        existingStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(existingStaff);
        log.info("Changed status of staff member with ID: {} to {}", savedStaff.getId(), status);
        
        return staffMapper.toDTO(savedStaff);
    }
    
    @Override
    @Transactional
    public StaffDTO assignToZone(UUID staffId, UUID zoneId) {
        log.debug("Assigning staff member with id: {} to zone with id: {}", staffId, zoneId);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));
        
        // Verify that the zone exists
        zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", zoneId));
        
        // Assign to zone
        existingStaff.setZoneId(zoneId.toString());
        
        // Validate warehouse exists
        warehouseRepository.findById(existingStaff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + existingStaff.getWarehouseId()));
        
        // Update audit fields
        existingStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(existingStaff);
        log.info("Assigned staff member with ID: {} to zone with ID: {}", savedStaff.getId(), zoneId);
        
        return staffMapper.toDTO(savedStaff);
    }
    
    @Transactional
    public StaffDTO removeFromZone(UUID staffId) {
        log.debug("Removing staff member with id: {} from zone assignment", staffId);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));
        
        // Remove zone assignment
        existingStaff.setZoneId(null);
        
        // Validate warehouse exists
        warehouseRepository.findById(existingStaff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + existingStaff.getWarehouseId()));
        
        // Update audit fields
        existingStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(existingStaff);
        log.info("Removed staff member with ID: {} from zone assignment", savedStaff.getId());
        
        return staffMapper.toDTO(savedStaff);
    }
    
    @Override
    @Transactional
    public void deleteStaff(UUID id) {
        log.debug("Deleting staff member with id: {}", id);
        
        // Check if staff member exists
        if (!staffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Staff", "id", id);
        }
        
        // Delete staff member
        staffRepository.deleteById(id);
        log.info("Deleted staff member with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getStaff(UUID id) {
        log.debug("Getting staff member by id: {}", id);
        return staffRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getStaffByEmployeeId(String employeeId) {
        log.debug("Getting staff member by employee id: {}", employeeId);
        return staffRepository.findByEmployeeId(employeeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getStaffByEmail(String email) {
        log.debug("Getting staff member by email: {}", email);
        return staffRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByWarehouseId(UUID warehouseId) {
        log.debug("Getting staff members by warehouse id: {}", warehouseId);
        return staffRepository.findByWarehouseId(warehouseId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByRole(StaffRole role) {
        log.debug("Getting staff members by role: {}", role);
        return staffRepository.findByRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByStatus(StaffStatus status) {
        log.debug("Getting staff members by status: {}", status);
        return staffRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByWarehouseIdAndRole(UUID warehouseId, StaffRole role) {
        log.debug("Getting staff members by warehouse id: {} and role: {}", warehouseId, role);
        return staffRepository.findByWarehouseIdAndRole(warehouseId, role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByWarehouseIdAndStatus(UUID warehouseId, StaffStatus status) {
        log.debug("Getting staff members by warehouse id: {} and status: {}", warehouseId, status);
        return staffRepository.findByWarehouseIdAndStatus(warehouseId, status);
    }
    
    @Override
    @Transactional
    public Staff createStaff(Staff staff) {
        log.debug("Creating new staff member: {}", staff);
        
        // Check if staff with employee ID already exists
        staffRepository.findByEmployeeId(staff.getEmployeeId()).ifPresent(existingStaff -> {
            throw new DuplicateResourceException("Staff member with employee ID " + staff.getEmployeeId() + " already exists");
        });
        
        // Check if staff with email already exists
        staffRepository.findByEmail(staff.getEmail()).ifPresent(existingStaff -> {
            throw new DuplicateResourceException("Staff member with email " + staff.getEmail() + " already exists");
        });
        
        // Ensure default values are set
        if (staff.getStatus() == null) {
            staff.setStatus(StaffStatus.ACTIVE);
        }
        
        // Validate warehouse exists
        warehouseRepository.findById(staff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + staff.getWarehouseId()));
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        staff.setCreatedAt(now);
        staff.setUpdatedAt(now);
        
        Staff savedStaff = staffRepository.save(staff);
        log.info("Created new staff member with ID: {}", savedStaff.getId());
        
        return savedStaff;
    }
    
    @Override
    @Transactional
    public Staff updateStaff(UUID id, Staff staff) {
        log.debug("Updating staff member with id: {}", id);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        
        // Check if updated employee ID conflicts with another staff member
        if (staff.getEmployeeId() != null && !staff.getEmployeeId().equals(existingStaff.getEmployeeId())) {
            staffRepository.findByEmployeeId(staff.getEmployeeId()).ifPresent(s -> {
                throw new DuplicateResourceException("Staff member with employee ID " + staff.getEmployeeId() + " already exists");
            });
        }
        
        // Check if updated email conflicts with another staff member
        if (staff.getEmail() != null && !staff.getEmail().equals(existingStaff.getEmail())) {
            staffRepository.findByEmail(staff.getEmail()).ifPresent(s -> {
                throw new DuplicateResourceException("Staff member with email " + staff.getEmail() + " already exists");
            });
        }
        
        // Update fields from entity
        if (staff.getFirstName() != null) existingStaff.setFirstName(staff.getFirstName());
        if (staff.getLastName() != null) existingStaff.setLastName(staff.getLastName());
        if (staff.getEmail() != null) existingStaff.setEmail(staff.getEmail());
        if (staff.getPhone() != null) existingStaff.setPhone(staff.getPhone());
        if (staff.getEmployeeId() != null) existingStaff.setEmployeeId(staff.getEmployeeId());
        if (staff.getRole() != null) existingStaff.setRole(staff.getRole());
        if (staff.getStatus() != null) existingStaff.setStatus(staff.getStatus());
        if (staff.getWarehouseId() != null) existingStaff.setWarehouseId(staff.getWarehouseId());
        
        // Validate warehouse exists
        warehouseRepository.findById(existingStaff.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + existingStaff.getWarehouseId()));
        
        // Update audit fields
        existingStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(existingStaff);
        log.info("Updated staff member with ID: {}", savedStaff.getId());
        
        return savedStaff;
    }
    
    @Override
    @Transactional
    public Staff updateStaffStatus(UUID id, StaffStatus status) {
        log.debug("Changing status of staff member with id: {} to {}", id, status);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        
        // Update status
        existingStaff.setStatus(status);
        
        // Update audit fields
        existingStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(existingStaff);
        log.info("Changed status of staff member with ID: {} to {}", savedStaff.getId(), status);
        
        return savedStaff;
    }
    
    @Override
    @Transactional
    public Staff assignStaffToZone(UUID staffId, UUID zoneId) {
        log.debug("Assigning staff member with id: {} to zone with id: {}", staffId, zoneId);
        
        // Find the existing staff member
        Staff existingStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));
        
        // Verify that the zone exists
        zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", zoneId));
        
        // Assign to zone
        existingStaff.setZoneId(zoneId.toString());
        
        // Update audit fields
        existingStaff.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Staff savedStaff = staffRepository.save(existingStaff);
        log.info("Assigned staff member with ID: {} to zone with ID: {}", savedStaff.getId(), zoneId);
        
        return savedStaff;
    }
    
} 
