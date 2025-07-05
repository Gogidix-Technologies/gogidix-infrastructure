package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.model.ZoneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Zone entities
 */
@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {
    
    /**
     * Find zones by warehouse id
     * 
     * @param warehouseId the warehouse id to search for
     * @return list of zones in the given warehouse
     */
    List<Zone> findByWarehouseId(UUID warehouseId);
    
    /**
     * Find zones by warehouse id and type
     * 
     * @param warehouseId the warehouse id
     * @param type the zone type
     * @return list of zones matching criteria
     */
    List<Zone> findByWarehouseIdAndType(UUID warehouseId, ZoneType type);
    
    /**
     * Find a zone by warehouse id and code
     * 
     * @param warehouseId the warehouse id
     * @param code the zone code
     * @return the zone if found
     */
    Optional<Zone> findByWarehouseIdAndCode(UUID warehouseId, String code);
    
    /**
     * Find zones by active status
     * 
     * @param isActive the active status
     * @return list of zones with given status
     */
    List<Zone> findByIsActive(Boolean isActive);
    
    /**
     * Find zones by type
     * 
     * @param type the zone type
     * @return list of zones of given type
     */
    List<Zone> findByType(ZoneType type);
    
    /**
     * Find zone by code
     * 
     * @param code the zone code
     * @return the zone if found
     */
    Optional<Zone> findByCode(String code);

    Optional<Zone> findByCodeAndWarehouseId(String code, UUID warehouseId);
    
    boolean existsByCodeAndWarehouseId(String code, UUID warehouseId);
} 
