package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.model.ZoneType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing zone operations
 */
public interface ZoneService {

    /**
     * Create a new zone
     *
     * @param zone the zone to create
     * @return the created zone
     */
    Zone createZone(Zone zone);

    /**
     * Update an existing zone
     *
     * @param id the zone id
     * @param zone the zone data to update
     * @return the updated zone
     */
    Zone updateZone(UUID id, Zone zone);

    /**
     * Get a zone by its id
     *
     * @param id the zone id
     * @return the zone if found
     */
    Optional<Zone> getZone(UUID id);

    /**
     * Get a zone by its code and warehouse id
     *
     * @param code the zone code
     * @param warehouseId the warehouse id
     * @return the zone if found
     */
    Optional<Zone> getZoneByCodeAndWarehouseId(String code, UUID warehouseId);

    /**
     * Get all zones for a warehouse
     *
     * @param warehouseId the warehouse id
     * @return list of zones in the warehouse
     */
    List<Zone> getZonesByWarehouseId(UUID warehouseId);

    /**
     * Get zones by type
     *
     * @param type the zone type
     * @return list of zones of the given type
     */
    List<Zone> getZonesByType(ZoneType type);

    /**
     * Get zones by warehouse id and type
     *
     * @param warehouseId the warehouse id
     * @param type the zone type
     * @return list of zones matching the criteria
     */
    List<Zone> getZonesByWarehouseIdAndType(UUID warehouseId, ZoneType type);

    /**
     * Delete a zone
     *
     * @param id the zone id
     */
    void deleteZone(UUID id);

    /**
     * Check if a zone with the given code exists in a warehouse
     *
     * @param code the zone code
     * @param warehouseId the warehouse id
     * @return true if the zone exists, false otherwise
     */
    boolean isZoneCodeExistsInWarehouse(String code, UUID warehouseId);

    /**
     * Activate or deactivate a zone
     *
     * @param id the zone id
     * @param isActive the active status
     * @return the updated zone
     */
    Zone setZoneActive(UUID id, boolean isActive);
} 
