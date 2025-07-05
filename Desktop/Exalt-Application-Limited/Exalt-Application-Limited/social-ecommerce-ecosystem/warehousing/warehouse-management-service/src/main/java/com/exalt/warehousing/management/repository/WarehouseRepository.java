package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.Warehouse;
import com.exalt.warehousing.management.model.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Warehouse entities
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    
    /**
     * Find a warehouse by its code
     * 
     * @param code the unique warehouse code
     * @return the warehouse if found
     */
    Optional<Warehouse> findByCode(String code);
    
    /**
     * Find warehouses by status
     * 
     * @param isActive the active status to search for
     * @return list of warehouses with the given status
     */
    List<Warehouse> findByIsActive(Boolean isActive);
    
    /**
     * Find warehouses by city
     * 
     * @param city the city to search for
     * @return list of warehouses in the given city
     */
    List<Warehouse> findByCity(String city);
    
    /**
     * Find warehouses by state/province
     * 
     * @param state the state to search for
     * @return list of warehouses in the given state
     */
    List<Warehouse> findByState(String state);
    
    /**
     * Find warehouses by country
     * 
     * @param country the country to search for
     * @return list of warehouses in the given country
     */
    List<Warehouse> findByCountry(String country);

    List<Warehouse> findByStatus(WarehouseStatus status);
    
    List<Warehouse> findByRegion(String region);
    
    List<Warehouse> findByNameContainingIgnoreCase(String name);
    
    boolean existsByCode(String code);
} 
