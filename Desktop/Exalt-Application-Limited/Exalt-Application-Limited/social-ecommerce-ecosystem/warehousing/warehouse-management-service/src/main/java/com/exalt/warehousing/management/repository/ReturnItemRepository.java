package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing ReturnItem entities
 */
@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, UUID> {

    /**
     * Find return items by return request ID
     *
     * @param returnRequestId the return request ID
     * @return list of return items
     */
    List<ReturnItem> findByReturnRequestId(UUID returnRequestId);

    /**
     * Find return items by return request ID and status
     *
     * @param returnRequestId the return request ID
     * @param status the return item status
     * @return list of return items
     */
    List<ReturnItem> findByReturnRequestIdAndStatus(UUID returnRequestId, String status);

    /**
     * Find return items by SKU
     *
     * @param sku the product SKU
     * @return list of return items
     */
    List<ReturnItem> findBySku(String sku);

    /**
     * Find return items by product ID
     *
     * @param productId the product ID
     * @return list of return items
     */
    List<ReturnItem> findByProductId(UUID productId);

    /**
     * Find return items by condition
     *
     * @param condition the item condition
     * @return list of return items
     */
    List<ReturnItem> findByCondition(String condition);

    /**
     * Find return items requiring quality assessment
     *
     * @return list of return items that need quality assessment
     */
    @Query("SELECT ri FROM ReturnItem ri WHERE ri.qualityAssessmentRequired = true AND ri.qualityAssessmentCompletedAt IS NULL")
    List<ReturnItem> findItemsRequiringQualityAssessment();

    /**
     * Find return items by warehouse ID
     *
     * @param warehouseId the warehouse ID
     * @return list of return items in the warehouse
     */
    @Query("SELECT ri FROM ReturnItem ri JOIN ri.returnRequest rr WHERE rr.warehouseId = :warehouseId")
    List<ReturnItem> findByWarehouseId(@Param("warehouseId") UUID warehouseId);

    /**
     * Find return items by warehouse ID and status
     *
     * @param warehouseId the warehouse ID
     * @param status the return item status
     * @return list of return items
     */
    @Query("SELECT ri FROM ReturnItem ri JOIN ri.returnRequest rr WHERE rr.warehouseId = :warehouseId AND ri.status = :status")
    List<ReturnItem> findByWarehouseIdAndStatus(@Param("warehouseId") UUID warehouseId, @Param("status") String status);

    /**
     * Find return items by date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of return items within the date range
     */
    @Query("SELECT ri FROM ReturnItem ri JOIN ri.returnRequest rr WHERE rr.createdAt BETWEEN :startDate AND :endDate")
    List<ReturnItem> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count return items by return request ID
     *
     * @param returnRequestId the return request ID
     * @return count of return items
     */
    long countByReturnRequestId(UUID returnRequestId);

    /**
     * Count return items by return request ID and status
     *
     * @param returnRequestId the return request ID
     * @param status the return item status
     * @return count of return items
     */
    long countByReturnRequestIdAndStatus(UUID returnRequestId, String status);

    /**
     * Count return items requiring quality assessment by warehouse
     *
     * @param warehouseId the warehouse ID
     * @return count of items requiring quality assessment
     */
    @Query("SELECT COUNT(ri) FROM ReturnItem ri JOIN ri.returnRequest rr WHERE rr.warehouseId = :warehouseId AND ri.qualityAssessmentRequired = true AND ri.qualityAssessmentCompletedAt IS NULL")
    long countItemsRequiringQualityAssessmentByWarehouse(@Param("warehouseId") UUID warehouseId);

    /**
     * Find return items that have been processed for restocking
     *
     * @param warehouseId the warehouse ID
     * @return list of processed return items
     */
    @Query("SELECT ri FROM ReturnItem ri JOIN ri.returnRequest rr WHERE rr.warehouseId = :warehouseId AND ri.restockingStatus = 'COMPLETED'")
    List<ReturnItem> findProcessedForRestocking(@Param("warehouseId") UUID warehouseId);

    /**
     * Find return items by disposition
     *
     * @param disposition the item disposition (RESTOCK, SCRAP, RETURN_TO_VENDOR, etc.)
     * @return list of return items with the specified disposition
     */
    List<ReturnItem> findByDisposition(String disposition);

    /**
     * Find return items with specific quality scores range
     *
     * @param minScore the minimum quality score
     * @param maxScore the maximum quality score
     * @return list of return items within the quality score range
     */
    @Query("SELECT ri FROM ReturnItem ri WHERE ri.qualityScore BETWEEN :minScore AND :maxScore")
    List<ReturnItem> findByQualityScoreBetween(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
}
