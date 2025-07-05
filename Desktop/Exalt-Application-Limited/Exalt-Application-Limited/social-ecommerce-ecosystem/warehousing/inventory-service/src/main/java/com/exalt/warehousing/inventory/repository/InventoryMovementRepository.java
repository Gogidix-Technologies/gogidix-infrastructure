package com.exalt.warehousing.inventory.repository;

import com.exalt.warehousing.inventory.model.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing inventory movement records.
 * Supports demand forecasting and inventory analytics functionality.
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    
    /**
     * Find movements by SKU with pagination
     * @param sku the product SKU
     * @param pageable pagination information
     * @return paginated list of movements
     */
    Page<InventoryMovement> findBySku(String sku, Pageable pageable);
    
    /**
     * Find movements by warehouse ID with pagination
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return paginated list of movements
     */
    @Query("SELECT m FROM InventoryMovement m WHERE m.sourceWarehouseId = :warehouseId OR m.targetWarehouseId = :warehouseId")
    Page<InventoryMovement> findByWarehouseId(@Param("warehouseId") UUID warehouseId, Pageable pageable);
    
    /**
     * Find movements by product ID with pagination
     * @param productId the product ID
     * @param pageable pagination information
     * @return paginated list of movements
     */
    Page<InventoryMovement> findByProductId(UUID productId, Pageable pageable);
    
    /**
     * Find movements by movement type with pagination
     * @param movementType the movement type
     * @param pageable pagination information
     * @return paginated list of movements
     */
    Page<InventoryMovement> findByMovementType(InventoryMovement.MovementType movementType, Pageable pageable);
    
    /**
     * Find movements by date range with pagination
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @param pageable pagination information
     * @return paginated list of movements
     */
    Page<InventoryMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find movements by reference ID with pagination
     * @param referenceId the reference ID
     * @param pageable pagination information
     * @return paginated list of movements
     */
    Page<InventoryMovement> findByReferenceId(UUID referenceId, Pageable pageable);
    
    /**
     * Get sum of quantity by SKU and warehouse for a given date range
     * Used for analytics and demand forecasting
     * 
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the sum of quantity
     */
    @Query("SELECT SUM(m.quantity) FROM InventoryMovement m " +
           "WHERE m.sku = :sku AND " +
           "(m.targetWarehouseId = :warehouseId OR m.sourceWarehouseId = :warehouseId) AND " +
           "m.movementDate BETWEEN :startDate AND :endDate")
    Integer getSumQuantityBySkuAndWarehouse(
            @Param("sku") String sku,
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get daily movement totals for a product in a warehouse
     * Used for demand forecasting and trend analysis
     * 
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of daily movement totals
     */
    @Query(value = 
           "SELECT CAST(m.movement_date AS DATE) as movement_day, SUM(m.quantity) as total_quantity " +
           "FROM inventory_movement m " +
           "WHERE m.sku = :sku AND " +
           "(m.target_warehouse_id = :warehouseId OR m.source_warehouse_id = :warehouseId) AND " +
           "m.movement_date BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(m.movement_date AS DATE) " +
           "ORDER BY movement_day", nativeQuery = true)
    List<Object[]> getDailyMovementTotals(
            @Param("sku") String sku,
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find all transfers between warehouses with pagination
     * 
     * @param sourceWarehouseId the source warehouse ID
     * @param targetWarehouseId the target warehouse ID
     * @param pageable pagination information
     * @return paginated list of movements
     */
    Page<InventoryMovement> findBySourceWarehouseIdAndTargetWarehouseId(
            UUID sourceWarehouseId, UUID targetWarehouseId, Pageable pageable);
}