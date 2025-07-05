package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.Zone;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing warehouse locations
 */
public interface LocationService {

    /**
     * Create a new location
     *
     * @param location the location data
     * @return the created location
     */
    Location createLocation(Location location);

    /**
     * Create multiple locations at once
     *
     * @param locations the list of locations to create
     * @return the created locations
     */
    List<Location> createLocations(List<Location> locations);

    /**
     * Update a location
     *
     * @param id the location ID
     * @param location the updated location data
     * @return the updated location
     */
    Location updateLocation(UUID id, Location location);

    /**
     * Get a location by ID
     *
     * @param id the location ID
     * @return the location if found
     */
    Optional<Location> getLocation(UUID id);

    /**
     * Get a location by its code and zone id
     *
     * @param locationCode the location code
     * @param zoneId the zone id
     * @return the location if found
     */
    Optional<Location> getLocationByCodeAndZoneId(String locationCode, UUID zoneId);

    /**
     * Get all locations in a zone
     *
     * @param zoneId the zone ID
     * @return list of locations in the zone
     */
    List<Location> getLocationsByZoneId(UUID zoneId);

    /**
     * Get locations by status
     *
     * @param status the location status
     * @return list of locations with the given status
     */
    List<Location> getLocationsByStatus(LocationStatus status);

    /**
     * Get locations by zone id and status
     *
     * @param zoneId the zone id
     * @param status the location status
     * @return list of locations matching the criteria
     */
    List<Location> getLocationsByZoneIdAndStatus(UUID zoneId, LocationStatus status);

    /**
     * Get all locations in a warehouse
     *
     * @param warehouseId the warehouse ID
     * @return list of locations in the warehouse
     */
    List<Location> getLocationsByWarehouseId(UUID warehouseId);

    /**
     * Get location by barcode
     *
     * @param barcode the barcode to search for
     * @return the location if found
     */
    Optional<Location> getLocationByBarcode(String barcode);

    /**
     * Count locations by zone id
     *
     * @param zoneId the zone id
     * @return count of locations in the zone
     */
    long countLocationsByZoneId(UUID zoneId);

    /**
     * Count locations by warehouse id and status
     *
     * @param warehouseId the warehouse id
     * @param status the location status
     * @return count of locations matching the criteria
     */
    long countLocationsByWarehouseIdAndStatus(UUID warehouseId, LocationStatus status);

    /**
     * Update location status
     *
     * @param id the location ID
     * @param status the new status
     * @return the updated location
     */
    Location updateLocationStatus(UUID id, LocationStatus status);

    /**
     * Delete a location
     *
     * @param id the location ID
     */
    void deleteLocation(UUID id);

    /**
     * Generate location barcodes for a zone
     *
     * @param zone the zone
     * @param count the number of locations to generate
     * @return list of generated location barcodes
     */
    List<String> generateLocationBarcodes(Zone zone, int count);

    /**
     * Get total number of locations by status in a warehouse
     *
     * @param warehouseId the warehouse ID
     * @return map of status to count
     */
    Map<LocationStatus, Long> getLocationCountByStatus(UUID warehouseId);

    /**
     * Find location by barcode
     *
     * @param barcode the location barcode
     * @return the location DTO
     */
    LocationDTO findLocationByBarcode(String barcode);

    /**
     * Find locations by warehouse ID
     *
     * @param warehouseId the warehouse ID
     * @return list of locations in the warehouse
     */
    List<LocationDTO> findLocationsByWarehouseId(UUID warehouseId);
}
