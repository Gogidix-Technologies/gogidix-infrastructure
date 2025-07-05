package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.shared.exception.ResourceNotFoundException;
import com.exalt.warehousing.shared.common.BaseEntity;
import com.exalt.warehousing.shared.common.BaseEntityService;
import org.springframework.data.jpa.repository.JpaRepository;
import com.exalt.warehousing.management.dto.WarehouseDTO;
import com.exalt.warehousing.management.exception.DuplicateResourceException;
import com.exalt.warehousing.management.mapper.WarehouseMapper;
import com.exalt.warehousing.management.model.Warehouse;
import com.exalt.warehousing.management.model.WarehouseStatus;
import com.exalt.warehousing.management.repository.WarehouseRepository;
import com.exalt.warehousing.management.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Implementation of the WarehouseService interface
 */
@Service
@Slf4j
@Transactional
public class WarehouseServiceImpl extends BaseEntityService<Warehouse, String> implements WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }
    
    @Override
    protected JpaRepository<Warehouse, String> getRepository() {
        return warehouseRepository;
    }
    
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouseDTOs() {
        log.debug("Finding all warehouses");
        return warehouseMapper.toDTOList(warehouseRepository.findAll());
    }
    
    @Override
    public Optional<Warehouse> findById(String id) {
        return warehouseRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseDTOById(String id) {
        log.debug("Finding warehouse by id: {}", id);
        return warehouseRepository.findById(id)
                .map(warehouseMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }
    
    @Transactional(readOnly = true)
    public WarehouseDTO findByCode(String code) {
        log.debug("Finding warehouse by code: {}", code);
        return warehouseRepository.findByCode(code)
                .map(warehouseMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", code));
    }
    
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findByActiveStatus(boolean isActive) {
        log.debug("Finding warehouses by active status: {}", isActive);
        return warehouseMapper.toDTOList(warehouseRepository.findByIsActive(isActive));
    }
    
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findByStatus(WarehouseStatus status) {
        log.debug("Finding warehouses by status: {}", status);
        return warehouseMapper.toDTOList(warehouseRepository.findByStatus(status));
    }
    
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findByLocation(String city, String state, String country) {
        log.debug("Finding warehouses by location - city: {}, state: {}, country: {}", city, state, country);
        List<Warehouse> results;
        
        if (city != null && !city.isEmpty()) {
            results = warehouseRepository.findByCity(city);
        } else if (state != null && !state.isEmpty()) {
            results = warehouseRepository.findByState(state);
        } else if (country != null && !country.isEmpty()) {
            results = warehouseRepository.findByCountry(country);
        } else {
            results = List.of();
        }
        
        return warehouseMapper.toDTOList(results);
    }
    
    @Transactional
    public WarehouseDTO create(WarehouseDTO warehouseDTO) {
        log.debug("Creating new warehouse: {}", warehouseDTO);
        
        // Check if warehouse with code already exists
        warehouseRepository.findByCode(warehouseDTO.getCode()).ifPresent(warehouse -> {
            throw new DuplicateResourceException("Warehouse with code " + warehouseDTO.getCode() + " already exists");
        });
        
        // Convert DTO to entity and save
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        
        // Ensure default values are set
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(WarehouseStatus.UNDER_CONSTRUCTION);
        }
        if (warehouse.getIsActive() == null) {
            warehouse.setIsActive(true);
        }
        
        // Set creation timestamp
        if (warehouse.getCreatedAt() == null) {
            warehouse.setCreatedAt(LocalDateTime.now());
        }
        warehouse.setUpdatedAt(LocalDateTime.now());
        
        Warehouse savedWarehouse = save(warehouse);
        log.info("Created new warehouse with ID: {}", savedWarehouse.getId());
        
        return warehouseMapper.toDTO(savedWarehouse);
    }
    
    @Override
    @Transactional
    public Warehouse createWarehouse(Warehouse warehouse) {
        log.debug("Creating new warehouse: {}", warehouse);
        
        // Check if warehouse with code already exists
        warehouseRepository.findByCode(warehouse.getCode()).ifPresent(existing -> {
            throw new DuplicateResourceException("Warehouse with code " + warehouse.getCode() + " already exists");
        });
        
        // Ensure default values are set
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(WarehouseStatus.UNDER_CONSTRUCTION);
        }
        if (warehouse.getIsActive() == null) {
            warehouse.setIsActive(true);
        }
        
        // Set creation timestamp
        if (warehouse.getCreatedAt() == null) {
            warehouse.setCreatedAt(LocalDateTime.now());
        }
        warehouse.setUpdatedAt(LocalDateTime.now());
        
        return save(warehouse);
    }
    
    @Transactional
    public WarehouseDTO update(String id, WarehouseDTO warehouseDTO) {
        log.debug("Updating warehouse with id: {}", id);
        
        // Find the existing warehouse
        Warehouse existingWarehouse = getWarehouseById(id);
        
        // Check if updated code conflicts with another warehouse
        if (warehouseDTO.getCode() != null && !warehouseDTO.getCode().equals(existingWarehouse.getCode())) {
            warehouseRepository.findByCode(warehouseDTO.getCode()).ifPresent(warehouse -> {
                throw new DuplicateResourceException("Warehouse with code " + warehouseDTO.getCode() + " already exists");
            });
        }
        
        // Update fields from DTO
        Warehouse updatedWarehouse = warehouseMapper.updateEntityFromDTO(existingWarehouse, warehouseDTO);
        
        // Save changes
        updatedWarehouse.setUpdatedAt(LocalDateTime.now());
        Warehouse savedWarehouse = save(updatedWarehouse);
        log.info("Updated warehouse with ID: {}", savedWarehouse.getId());
        
        return warehouseMapper.toDTO(savedWarehouse);
    }
    
    @Override
    @Transactional
    public Warehouse updateWarehouse(String id, Warehouse warehouseDetails) {
        log.debug("Updating warehouse with id: {}", id);
        
        // Find the existing warehouse
        Warehouse existingWarehouse = getWarehouseById(id);
        
        // Check if updated code conflicts with another warehouse
        if (warehouseDetails.getCode() != null && !warehouseDetails.getCode().equals(existingWarehouse.getCode())) {
            warehouseRepository.findByCode(warehouseDetails.getCode()).ifPresent(warehouse -> {
                if (!warehouse.getId().equals(id)) {
                    throw new DuplicateResourceException("Warehouse with code " + warehouseDetails.getCode() + " already exists");
                }
            });
        }
        
        // Update fields
        existingWarehouse.setName(warehouseDetails.getName());
        existingWarehouse.setCode(warehouseDetails.getCode());
        existingWarehouse.setAddressLine1(warehouseDetails.getAddressLine1());
        existingWarehouse.setAddressLine2(warehouseDetails.getAddressLine2());
        existingWarehouse.setCity(warehouseDetails.getCity());
        existingWarehouse.setState(warehouseDetails.getState());
        existingWarehouse.setPostalCode(warehouseDetails.getPostalCode());
        existingWarehouse.setCountry(warehouseDetails.getCountry());
        existingWarehouse.setPhoneNumber(warehouseDetails.getPhoneNumber());
        existingWarehouse.setEmail(warehouseDetails.getEmail());
        existingWarehouse.setSquareFootage(warehouseDetails.getSquareFootage());
        existingWarehouse.setStatus(warehouseDetails.getStatus());
        existingWarehouse.setIsActive(warehouseDetails.getIsActive());
        
        // Set updated timestamp
        existingWarehouse.setUpdatedAt(LocalDateTime.now());
        
        // Save and return the updated warehouse
        return save(existingWarehouse);
    }
    
    @Transactional
    public WarehouseDTO changeStatus(String id, WarehouseStatus status) {
        log.debug("Changing status of warehouse with id: {} to {}", id, status);
        
        // Find the existing warehouse
        Warehouse existingWarehouse = getWarehouseById(id);
        
        // Update status
        existingWarehouse.setStatus(status);
        existingWarehouse.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Warehouse savedWarehouse = save(existingWarehouse);
        log.info("Changed status of warehouse with ID: {} to {}", savedWarehouse.getId(), status);
        
        return warehouseMapper.toDTO(savedWarehouse);
    }
    
    @Override
    @Transactional
    public Warehouse changeWarehouseStatus(String id, WarehouseStatus status) {
        log.debug("Changing status of warehouse with id: {} to {}", id, status);
        
        // Find the existing warehouse
        Warehouse existingWarehouse = getWarehouseById(id);
        
        // Update status
        existingWarehouse.setStatus(status);
        existingWarehouse.setUpdatedAt(LocalDateTime.now());
        
        // Save and return the updated warehouse
        return save(existingWarehouse);
    }
    
    @Transactional
    public WarehouseDTO setActive(String id, boolean isActive) {
        log.debug("Setting active status of warehouse with id: {} to {}", id, isActive);
        
        // Find the existing warehouse
        Warehouse existingWarehouse = getWarehouseById(id);
        
        // Update active status
        existingWarehouse.setIsActive(isActive);
        existingWarehouse.setUpdatedAt(LocalDateTime.now());
        
        // Save changes
        Warehouse savedWarehouse = save(existingWarehouse);
        log.info("Set active status of warehouse with ID: {} to {}", savedWarehouse.getId(), isActive);
        
        return warehouseMapper.toDTO(savedWarehouse);
    }
    
    /**
     * Helper method to get a warehouse by ID or throw exception if not found
     * 
     * @param id the warehouse ID
     * @return the warehouse
     * @throws ResourceNotFoundException if warehouse not found
     */
    private Warehouse getWarehouseById(String id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }
    
    @Override
    public Optional<Warehouse> getWarehouse(String id) {
        return warehouseRepository.findById(id);
    }

    @Override
    public Optional<Warehouse> getWarehouseByCode(String code) {
        return warehouseRepository.findByCode(code);
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public List<Warehouse> getWarehousesByStatus(WarehouseStatus status) {
        return warehouseRepository.findByStatus(status);
    }

    @Override
    public List<Warehouse> getWarehousesByRegion(String region) {
        return warehouseRepository.findByRegion(region);
    }

    @Override
    public List<Warehouse> searchWarehousesByName(String name) {
        return warehouseRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional
    public void deleteWarehouse(String id) {
        log.debug("Deleting warehouse with id: {}", id);
        warehouseRepository.deleteById(id);
    }

    @Override
    public boolean isWarehouseCodeExists(String code) {
        return warehouseRepository.existsByCode(code);
    }
} 

