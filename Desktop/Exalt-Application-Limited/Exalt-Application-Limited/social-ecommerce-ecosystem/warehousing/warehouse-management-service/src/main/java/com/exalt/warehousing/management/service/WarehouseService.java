package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.model.Warehouse;
import com.exalt.warehousing.management.model.WarehouseStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing warehouse operations
 */
public interface WarehouseService {

    /**
     * Create a new warehouse
     *
     * @param warehouse the warehouse to create
     * @return the created warehouse
     */
    Warehouse createWarehouse(Warehouse warehouse);

    /**
     * Update an existing warehouse
     *
     * @param id the warehouse id
     * @param warehouse the warehouse data to update
     * @return the updated warehouse
     */
    Warehouse updateWarehouse(String id, Warehouse warehouse);

    /**
     * Get a warehouse by its id
     *
     * @param id the warehouse id
     * @return the warehouse if found
     */
    Optional<Warehouse> getWarehouse(String id);

    /**
     * Get a warehouse by its code
     *
     * @param code the warehouse code
     * @return the warehouse if found
     */
    Optional<Warehouse> getWarehouseByCode(String code);

    /**
     * Get all warehouses
     *
     * @return list of all warehouses
     */
    List<Warehouse> getAllWarehouses();

    /**
     * Get warehouses by status
     *
     * @param status the status to filter by
     * @return list of warehouses with the given status
     */
    List<Warehouse> getWarehousesByStatus(WarehouseStatus status);

    /**
     * Get warehouses by region
     *
     * @param region the region to filter by
     * @return list of warehouses in the given region
     */
    List<Warehouse> getWarehousesByRegion(String region);

    /**
     * Search warehouses by name
     *
     * @param name the name to search for
     * @return list of warehouses matching the search criteria
     */
    List<Warehouse> searchWarehousesByName(String name);

    /**
     * Delete a warehouse
     *
     * @param id the warehouse id
     */
    void deleteWarehouse(String id);

    /**
     * Check if a warehouse code already exists
     *
     * @param code the code to check
     * @return true if the code exists, false otherwise
     */
    boolean isWarehouseCodeExists(String code);

    /**
     * Change the status of a warehouse
     *
     * @param id the warehouse id
     * @param status the new status
     * @return the updated warehouse
     */
    Warehouse changeWarehouseStatus(String id, WarehouseStatus status);
} 
