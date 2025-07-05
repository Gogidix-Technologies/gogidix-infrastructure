package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repository interface for managing InventoryMovement entities
 * Used for tracking inventory movements for demand forecasting and analytics
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {

    /**
     * Find inventory movements by product ID
     *
     * @param productId the product ID
     * @return list of inventory movements for the product
     */
    List<InventoryMovement> findByProductId(UUID productId);

    /**
     * Find inventory movements by warehouse ID
     *
     * @param warehouseId the warehouse ID
     * @return list of inventory movements in the warehouse
     */
    List<InventoryMovement> findByWarehouseId(UUID warehouseId);

    /**
     * Find inventory movements by product ID and warehouse ID
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @return list of inventory movements
     */
    List<InventoryMovement> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);

    /**
     * Find inventory movements by movement type
     *
     * @param movementType the type of movement (INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT)
     * @return list of inventory movements of the specified type
     */
    List<InventoryMovement> findByMovementType(String movementType);

    /**
     * Find inventory movements by date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of inventory movements within the date range
     */
    List<InventoryMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inventory movements by product ID and date range
     *
     * @param productId the product ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of inventory movements for the product within the date range
     */
    List<InventoryMovement> findByProductIdAndMovementDateBetween(UUID productId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inventory movements by warehouse ID and date range
     *
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of inventory movements in the warehouse within the date range
     */
    List<InventoryMovement> findByWarehouseIdAndMovementDateBetween(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find outbound movements (sales) for demand forecasting
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of outbound movements for demand analysis
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.productId = :productId AND im.warehouseId = :warehouseId AND im.movementType = 'OUTBOUND' AND im.movementDate BETWEEN :startDate AND :endDate ORDER BY im.movementDate ASC")
    List<InventoryMovement> findOutboundMovementsForDemandForecasting(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calculate total quantity moved for a product in a warehouse within a date range
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @param movementType the movement type
     * @param startDate the start date
     * @param endDate the end date
     * @return total quantity moved
     */
    @Query("SELECT COALESCE(SUM(im.quantity), 0) FROM InventoryMovement im WHERE im.productId = :productId AND im.warehouseId = :warehouseId AND im.movementType = :movementType AND im.movementDate BETWEEN :startDate AND :endDate")
    Long calculateTotalQuantityMoved(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId,
            @Param("movementType") String movementType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find daily outbound quantities for a product (for demand pattern analysis)
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of daily quantities
     */
    @Query("SELECT DATE(im.movementDate) as movementDate, SUM(im.quantity) as dailyQuantity FROM InventoryMovement im WHERE im.productId = :productId AND im.warehouseId = :warehouseId AND im.movementType = 'OUTBOUND' AND im.movementDate BETWEEN :startDate AND :endDate GROUP BY DATE(im.movementDate) ORDER BY DATE(im.movementDate)")
    List<Object[]> findDailyOutboundQuantities(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find weekly outbound quantities for a product
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of weekly quantities
     */
    @Query("SELECT YEAR(im.movementDate) as year, WEEK(im.movementDate) as week, SUM(im.quantity) as weeklyQuantity FROM InventoryMovement im WHERE im.productId = :productId AND im.warehouseId = :warehouseId AND im.movementType = 'OUTBOUND' AND im.movementDate BETWEEN :startDate AND :endDate GROUP BY YEAR(im.movementDate), WEEK(im.movementDate) ORDER BY YEAR(im.movementDate), WEEK(im.movementDate)")
    List<Object[]> findWeeklyOutboundQuantities(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find monthly outbound quantities for a product
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of monthly quantities
     */
    @Query("SELECT YEAR(im.movementDate) as year, MONTH(im.movementDate) as month, SUM(im.quantity) as monthlyQuantity FROM InventoryMovement im WHERE im.productId = :productId AND im.warehouseId = :warehouseId AND im.movementType = 'OUTBOUND' AND im.movementDate BETWEEN :startDate AND :endDate GROUP BY YEAR(im.movementDate), MONTH(im.movementDate) ORDER BY YEAR(im.movementDate), MONTH(im.movementDate)")
    List<Object[]> findMonthlyOutboundQuantities(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Count total movements by warehouse and date range
     *
     * @param warehouseId the warehouse ID
     * @param startDate the start date
     * @param endDate the end date
     * @return count of movements
     */
    long countByWarehouseIdAndMovementDateBetween(UUID warehouseId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent movements for a product (for trend analysis)
     *
     * @param productId the product ID
     * @param warehouseId the warehouse ID
     * @param limit the maximum number of records to return
     * @return list of recent movements
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.productId = :productId AND im.warehouseId = :warehouseId ORDER BY im.movementDate DESC LIMIT :limit")
    List<InventoryMovement> findRecentMovements(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId,
            @Param("limit") int limit
    );

    /**
     * Get product movement history for demand forecasting
     *
     * @param productId the product ID
     * @param startDate the start date for historical data
     * @return list of movement history data
     */
    @Query("SELECT im.movementDate as date, im.quantity as quantity, im.movementType as type FROM InventoryMovement im WHERE im.productId = :productId AND im.movementDate >= :startDate ORDER BY im.movementDate ASC")
    List<Map<String, Object>> getProductMovementHistory(
            @Param("productId") UUID productId,
            @Param("startDate") LocalDateTime startDate
    );

    /**
     * Get warehouse movement history for demand forecasting
     *
     * @param warehouseId the warehouse ID
     * @param startDate the start date for historical data
     * @return list of movement history data
     */
    @Query("SELECT im.movementDate as date, im.quantity as quantity, im.movementType as type, im.productId as productId FROM InventoryMovement im WHERE im.warehouseId = :warehouseId AND im.movementDate >= :startDate ORDER BY im.movementDate ASC")
    List<Map<String, Object>> getWarehouseMovementHistory(
            @Param("warehouseId") UUID warehouseId,
            @Param("startDate") LocalDateTime startDate
    );
}
