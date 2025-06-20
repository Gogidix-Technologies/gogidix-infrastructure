package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.LocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Location entities
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
    
    /**
     * Find locations by location code and zone id
     * 
     * @param locationCode the location code
     * @param zoneId the zone id
     * @return the location if found
     */
    Optional<Location> findByLocationCodeAndZoneId(String locationCode, String zoneId);
    
    /**
     * Find locations by zone id
     * 
     * @param zoneId the zone id
     * @return list of locations in the zone
     */
    List<Location> findByZoneId(String zoneId);
    
    /**
     * Find locations by status
     * 
     * @param status the location status
     * @return list of locations with given status
     */
    List<Location> findByStatus(LocationStatus status);
    
    /**
     * Find locations by zone id and status
     * 
     * @param zoneId the zone id
     * @param status the location status
     * @return list of locations matching criteria
     */
    List<Location> findByZoneIdAndStatus(String zoneId, LocationStatus status);
    
    /**
     * Find locations by warehouse id
     * 
     * @param warehouseId the warehouse id
     * @return list of locations in the warehouse
     */
    @Query("SELECT l FROM Location l JOIN l.zone z WHERE z.warehouse.id = :warehouseId")
    List<Location> findByWarehouseId(String warehouseId);
    
    /**
     * Find location by barcode
     * 
     * @param barcode the location barcode
     * @return the location if found
     */
    Optional<Location> findByBarcode(String barcode);
    
    /**
     * Find location by warehouse id and position identifiers
     * 
     * @param warehouseId the warehouse id
     * @param aisle the aisle identifier
     * @param rack the rack identifier
     * @param level the level identifier
     * @param position the position identifier
     * @return the location if found
     */
    Optional<Location> findByWarehouseIdAndAisleAndRackAndLevelAndPosition(
            String warehouseId, String aisle, String rack, String level, String position);
    
    /**
     * Count locations by zone id
     * 
     * @param zoneId the zone id
     * @return count of locations in the zone
     */
    long countByZoneId(String zoneId);
    
    /**
     * Count locations by warehouse id and status
     * 
     * @param warehouseId the warehouse id
     * @param status the location status
     * @return count of locations matching criteria
     */
    @Query("SELECT COUNT(l) FROM Location l JOIN l.zone z WHERE z.warehouse.id = :warehouseId AND l.status = :status")
    long countByWarehouseIdAndStatus(String warehouseId, LocationStatus status);
    
    /**
     * Find locations by a list of zone IDs
     * 
     * @param zoneIds list of zone IDs
     * @return list of locations in the specified zones
     */
    List<Location> findByZoneIdIn(List<String> zoneIds);
    
    /**
     * Find the default starting location for a warehouse (e.g., packing station)
     * 
     * @param warehouseId the warehouse id
     * @return the default start location for picking paths
     */
    @Query("SELECT l FROM Location l WHERE l.warehouse.id = :warehouseId AND l.metadata['locationFunction'] = 'PACKING_STATION' AND l.status = 'AVAILABLE' ORDER BY l.metadata['priority'] ASC")
    Optional<Location> findDefaultStartLocationByWarehouseId(String warehouseId);
    
    /**
     * Find the default starting location for a zone
     * 
     * @param zoneId the zone id
     * @return the default start location for zone-specific picking paths
     */
    @Query("SELECT l FROM Location l WHERE l.zone.id = :zoneId AND l.metadata['locationFunction'] = 'ZONE_STATION' AND l.status = 'AVAILABLE' ORDER BY l.metadata['priority'] ASC")
    Optional<Location> findDefaultStartLocationByZoneId(String zoneId);

    /**
     * Find the first location in a zone
     *
     * @param zoneId the zone ID
     * @return the first location found
     */
    Optional<Location> findFirstByZoneId(String zoneId);

    /**
     * Find a location by its code
     *
     * @param locationCode the location code
     * @return the location if found
     */
    Optional<Location> findByLocationCode(String locationCode);

    /**
     * Find an entry point for a zone
     *
     * @param zoneId the zone ID
     * @return the entry point location if found
     */
    @Query("SELECT l FROM Location l WHERE l.zone.id = :zoneId AND l.properties['isEntryPoint'] = 'true'")
    Optional<Location> findZoneEntryPoint(@Param("zoneId") String zoneId);

    /**
     * Find a location by zone ID and entry point flag
     *
     * @param zoneId the zone ID
     * @return the entry point location if found
     */
    @Query("SELECT l FROM Location l WHERE l.zone.id = :zoneId AND l.properties['isEntryPoint'] = 'true'")
    Optional<Location> findByZoneIdAndIsEntryPointTrue(@Param("zoneId") String zoneId);

    /**
     * Find locations by aisle
     *
     * @param aisle the aisle name
     * @return list of locations
     */
    List<Location> findByAisle(String aisle);

    /**
     * Find locations by rack
     *
     * @param rack the rack name
     * @return list of locations
     */
    List<Location> findByRack(String rack);

    /**
     * Find locations by aisle and rack
     *
     * @param aisle the aisle name
     * @param rack the rack name
     * @return list of locations
     */
    List<Location> findByAisleAndRack(String aisle, String rack);

    /**
     * Find locations by aisle, rack, and level
     *
     * @param aisle the aisle name
     * @param rack the rack name
     * @param level the level
     * @return list of locations
     */
    List<Location> findByAisleAndRackAndLevel(String aisle, String rack, String level);


    /**
     * Find locations by warehouse ID and status
     *
     * @param warehouseId the warehouse ID
     * @param status the location status
     * @return list of locations
     */
    List<Location> findByWarehouseIdAndStatus(String warehouseId, LocationStatus status);


    /**
     * Find locations containing a specific property
     *
     * @param propertyKey the property key
     * @param propertyValue the property value
     * @return list of locations
     */
    @Query("SELECT l FROM Location l JOIN l.properties p WHERE KEY(p) = :propertyKey AND VALUE(p) = :propertyValue")
    List<Location> findByProperty(@Param("propertyKey") String propertyKey, @Param("propertyValue") String propertyValue);

    /**
     * Find locations containing a specific metadata
     *
     * @param metadataKey the metadata key
     * @param metadataValue the metadata value
     * @return list of locations
     */
    @Query("SELECT l FROM Location l JOIN l.metadata m WHERE KEY(m) = :metadataKey AND VALUE(m) = :metadataValue")
    List<Location> findByMetadata(@Param("metadataKey") String metadataKey, @Param("metadataValue") String metadataValue);

    /**
     * Count locations by warehouse id
     * 
     * @param warehouseId the warehouse id
     * @return count of locations in the warehouse
     */
    @Query("SELECT COUNT(l) FROM Location l JOIN l.zone z WHERE z.warehouse.id = :warehouseId")
    long countByWarehouseId(@Param("warehouseId") String warehouseId);

} 
