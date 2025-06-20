package com.exalt.warehousing.inventory.service.impl;

import com.exalt.warehousing.inventory.exception.WarehouseNotFoundException;
import com.exalt.warehousing.inventory.model.WarehouseLocation;
import com.exalt.warehousing.inventory.model.WarehouseType;
import com.exalt.warehousing.inventory.repository.WarehouseLocationRepository;
import com.exalt.warehousing.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the warehouse service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseLocationRepository warehouseRepository;

    @Override
    public WarehouseLocation getWarehouseById(UUID id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with ID: " + id));
    }

    @Override
    public WarehouseLocation getWarehouseByCode(String code) {
        return warehouseRepository.findByCode(code)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with code: " + code));
    }

    @Override
    @Transactional
    public WarehouseLocation createWarehouse(WarehouseLocation warehouse) {
        log.info("Creating new warehouse with code: {}", warehouse.getCode());
        
        // Set default values if not provided
        if (warehouse.getIsActive() == null) {
            warehouse.setIsActive(true);
        }
        
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public WarehouseLocation updateWarehouse(UUID id, WarehouseLocation updatedWarehouse) {
        log.info("Updating warehouse with ID: {}", id);
        
        WarehouseLocation existingWarehouse = getWarehouseById(id);
        
        // Update fields
        existingWarehouse.setName(updatedWarehouse.getName());
        existingWarehouse.setAddressLine1(updatedWarehouse.getAddressLine1());
        existingWarehouse.setAddressLine2(updatedWarehouse.getAddressLine2());
        existingWarehouse.setCity(updatedWarehouse.getCity());
        existingWarehouse.setStateProvince(updatedWarehouse.getStateProvince());
        existingWarehouse.setPostalCode(updatedWarehouse.getPostalCode());
        existingWarehouse.setCountry(updatedWarehouse.getCountry());
        existingWarehouse.setLatitude(updatedWarehouse.getLatitude());
        existingWarehouse.setLongitude(updatedWarehouse.getLongitude());
        existingWarehouse.setContactPhone(updatedWarehouse.getContactPhone());
        existingWarehouse.setContactEmail(updatedWarehouse.getContactEmail());
        existingWarehouse.setIsActive(updatedWarehouse.getIsActive());
        existingWarehouse.setType(updatedWarehouse.getType());
        
        return warehouseRepository.save(existingWarehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(UUID id) {
        log.info("Deleting warehouse with ID: {}", id);
        
        WarehouseLocation warehouse = getWarehouseById(id);
        
        // Soft delete by deactivating
        warehouse.setIsActive(false);
        
        warehouseRepository.save(warehouse);
        
        log.info("Warehouse with ID: {} has been deactivated", id);
    }

    @Override
    public Page<WarehouseLocation> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }

    @Override
    public List<WarehouseLocation> getActiveWarehouses() {
        return warehouseRepository.findAllByIsActiveTrue();
    }

    @Override
    public Page<WarehouseLocation> searchWarehouses(String searchTerm, Pageable pageable) {
        return warehouseRepository.searchWarehouses(searchTerm, pageable);
    }

    @Override
    public Page<WarehouseLocation> getWarehousesByType(WarehouseType type, Pageable pageable) {
        return warehouseRepository.findAllByType(type, pageable);
    }

    @Override
    public List<WarehouseLocation> getActiveWarehousesByType(WarehouseType type) {
        return warehouseRepository.findAllByTypeAndIsActiveTrue(type);
    }

    @Override
    public List<WarehouseLocation> findNearbyWarehouses(Double latitude, Double longitude, Double radiusKm) {
        return warehouseRepository.findWarehousesNearLocation(latitude, longitude, radiusKm);
    }
}
