package com.exalt.warehousing.inventory.service;

import com.exalt.warehousing.inventory.model.WarehouseLocation;
import com.exalt.warehousing.inventory.model.WarehouseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for warehouse management operations
 */
public interface WarehouseService {

    /**
     * Get a warehouse by ID
     * @param id the warehouse ID
     * @return the warehouse
     */
    WarehouseLocation getWarehouseById(UUID id);

    /**
     * Get a warehouse by code
     * @param code the warehouse code
     * @return the warehouse
     */
    WarehouseLocation getWarehouseByCode(String code);

    /**
     * Create a new warehouse
     * @param warehouse the warehouse to create
     * @return the created warehouse
     */
    WarehouseLocation createWarehouse(WarehouseLocation warehouse);

    /**
     * Update an existing warehouse
     * @param id the warehouse ID
     * @param warehouse the updated data
     * @return the updated warehouse
     */
    WarehouseLocation updateWarehouse(UUID id, WarehouseLocation warehouse);

    /**
     * Delete a warehouse (soft delete)
     * @param id the warehouse ID
     */
    void deleteWarehouse(UUID id);

    /**
     * Get all warehouses
     * @param pageable pagination information
     * @return paged result of warehouses
     */
    Page<WarehouseLocation> getAllWarehouses(Pageable pageable);

    /**
     * Get all active warehouses
     * @return list of active warehouses
     */
    List<WarehouseLocation> getActiveWarehouses();

    /**
     * Search warehouses by name, code, or city
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return paged result of matching warehouses
     */
    Page<WarehouseLocation> searchWarehouses(String searchTerm, Pageable pageable);

    /**
     * Get warehouses by type
     * @param type the warehouse type
     * @param pageable pagination information
     * @return paged result of warehouses
     */
    Page<WarehouseLocation> getWarehousesByType(WarehouseType type, Pageable pageable);

    /**
     * Get active warehouses by type
     * @param type the warehouse type
     * @return list of matching warehouses
     */
    List<WarehouseLocation> getActiveWarehousesByType(WarehouseType type);

    /**
     * Find warehouses near a location
     * @param latitude the target latitude
     * @param longitude the target longitude
     * @param radiusKm the search radius in kilometers
     * @return list of warehouses ordered by proximity
     */
    List<WarehouseLocation> findNearbyWarehouses(Double latitude, Double longitude, Double radiusKm);
}
