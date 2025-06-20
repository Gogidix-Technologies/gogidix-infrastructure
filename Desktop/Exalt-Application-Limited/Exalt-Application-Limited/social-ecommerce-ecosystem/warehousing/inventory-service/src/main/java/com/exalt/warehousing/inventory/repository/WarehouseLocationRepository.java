package com.exalt.warehousing.inventory.repository;

import com.exalt.warehousing.inventory.model.WarehouseLocation;
import com.exalt.warehousing.inventory.model.WarehouseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for warehouse location operations
 */
@Repository
public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, UUID> {

    /**
     * Find a warehouse by code
     * @param code the warehouse code
     * @return the warehouse if found
     */
    Optional<WarehouseLocation> findByCode(String code);

    /**
     * Find all active warehouses
     * @return list of active warehouses
     */
    List<WarehouseLocation> findAllByIsActiveTrue();

    /**
     * Find all warehouses by type
     * @param type the warehouse type
     * @param pageable pagination information
     * @return paged result of warehouses
     */
    Page<WarehouseLocation> findAllByType(WarehouseType type, Pageable pageable);

    /**
     * Find active warehouses by type
     * @param type the warehouse type
     * @return list of matching warehouses
     */
    List<WarehouseLocation> findAllByTypeAndIsActiveTrue(WarehouseType type);

    /**
     * Find warehouses near a specific location
     * Uses Haversine formula to calculate distance
     * @param latitude the target latitude
     * @param longitude the target longitude
     * @param radiusKm the search radius in kilometers
     * @return list of warehouses ordered by proximity
     */
    @Query(value = 
        "SELECT *, " +
        "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
        "cos(radians(longitude) - radians(:longitude)) + " +
        "sin(radians(:latitude)) * sin(radians(latitude)))) AS distance " +
        "FROM warehouse_locations " +
        "WHERE is_active = true " +
        "HAVING distance < :radiusKm " +
        "ORDER BY distance", 
        nativeQuery = true)
    List<WarehouseLocation> findWarehousesNearLocation(
        Double latitude, Double longitude, Double radiusKm);

    /**
     * Search warehouses by name, code, or city
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return paged result of matching warehouses
     */
    @Query("SELECT w FROM WarehouseLocation w WHERE " +
           "(LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND w.isActive = true")
    Page<WarehouseLocation> searchWarehouses(String searchTerm, Pageable pageable);
}
