package com.exalt.warehousing.fulfillment.repository;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for FulfillmentOrder entities
 */
@Repository
public interface FulfillmentOrderRepository extends JpaRepository<FulfillmentOrder, String> {

    /**
     * Find fulfillment order by external order ID
     *
     * @param externalOrderId the external order ID
     * @return the fulfillment order if found
     */
    Optional<FulfillmentOrder> findByExternalOrderId(String externalOrderId);

    /**
     * Find fulfillment order by order reference
     *
     * @param orderReference the order reference
     * @return the fulfillment order if found
     */
    Optional<FulfillmentOrder> findByOrderReference(String orderReference);

    /**
     * Find all fulfillment orders by status
     *
     * @param status the status
     * @param pageable pagination information
     * @return page of fulfillment orders
     */
    Page<FulfillmentOrder> findAllByStatus(FulfillmentStatus status, Pageable pageable);

    /**
     * Find all fulfillment orders assigned to a warehouse
     *
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return page of fulfillment orders
     */
    Page<FulfillmentOrder> findAllByWarehouseId(Long warehouseId, Pageable pageable);

    /**
     * Find all fulfillment orders assigned to a warehouse with a specific status
     *
     * @param warehouseId the warehouse ID
     * @param status the status
     * @param pageable pagination information
     * @return page of fulfillment orders
     */
    Page<FulfillmentOrder> findAllByWarehouseIdAndStatus(Long warehouseId, FulfillmentStatus status, Pageable pageable);

    /**
     * Find all active fulfillment orders (not completed, delivered, or cancelled)
     *
     * @param pageable pagination information
     * @return page of active fulfillment orders
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.status NOT IN " +
           "(com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.COMPLETED, " +
           "com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.DELIVERED, " +
           "com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.CANCELLED)")
    Page<FulfillmentOrder> findAllActiveOrders(Pageable pageable);

    /**
     * Find all orders that need to be picked
     *
     * @return list of orders ready for picking
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.status = " +
           "com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.ALLOCATED " +
           "ORDER BY fo.priority DESC, fo.createdAt ASC")
    List<FulfillmentOrder> findAllOrdersReadyForPicking();

    /**
     * Find all orders that need to be packed
     *
     * @return list of orders ready for packing
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.status = " +
           "com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.PICKING_COMPLETE " +
           "ORDER BY fo.priority DESC, fo.pickCompletedAt ASC")
    List<FulfillmentOrder> findAllOrdersReadyForPacking();

    /**
     * Find all orders that need to be shipped
     *
     * @return list of orders ready to ship
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.status = " +
           "com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.READY_TO_SHIP " +
           "ORDER BY fo.priority DESC, fo.packCompletedAt ASC")
    List<FulfillmentOrder> findAllOrdersReadyToShip();

    /**
     * Find all orders recently shipped
     *
     * @param since time threshold
     * @param pageable pagination information
     * @return page of recently shipped orders
     */
    Page<FulfillmentOrder> findAllByStatusAndShippedAtAfter(
            FulfillmentStatus status, LocalDateTime since, Pageable pageable);

    /**
     * Find orders by customer information
     *
     * @param customerName customer name to search for
     * @param pageable pagination information
     * @return page of matching orders
     */
    Page<FulfillmentOrder> findAllByCustomerNameContainingIgnoreCase(
            String customerName, Pageable pageable);

    /**
     * Find orders by customer email
     *
     * @param customerEmail customer email to search for
     * @param pageable pagination information
     * @return page of matching orders
     */
    Page<FulfillmentOrder> findAllByCustomerEmailContainingIgnoreCase(
            String customerEmail, Pageable pageable);

    /**
     * Find orders with tracking number
     *
     * @param trackingNumber tracking number to search for
     * @return list of matching orders
     */
    List<FulfillmentOrder> findAllByTrackingNumberContainingIgnoreCase(String trackingNumber);

    /**
     * Count orders by status
     *
     * @param status the status
     * @return count of orders with the specified status
     */
    long countByStatus(FulfillmentStatus status);

    /**
     * Count active orders in warehouse
     *
     * @param warehouseId the warehouse ID
     * @return count of active orders in the warehouse
     */
    @Query("SELECT COUNT(fo) FROM FulfillmentOrder fo WHERE fo.warehouseId = :warehouseId " +
           "AND fo.status NOT IN ('COMPLETED', 'DELIVERED', 'CANCELLED')")
    long countActiveOrdersInWarehouse(@Param("warehouseId") Long warehouseId);

    /**
     * Find fulfillment orders by status
     *
     * @param status the fulfillment status
     * @return list of fulfillment orders
     */
    List<FulfillmentOrder> findByStatus(FulfillmentStatus status);
    
    /**
     * Find by order number
     */
    Optional<FulfillmentOrder> findByOrderNumber(String orderNumber);
    
    /**
     * Find by tracking number
     */
    Optional<FulfillmentOrder> findByTrackingNumber(String trackingNumber);
    
    /**
     * Find by assigned picker ID
     */
    List<FulfillmentOrder> findByAssignedPickerId(Long pickerId);
    
    /**
     * Find by assigned packer ID
     */
    List<FulfillmentOrder> findByAssignedPackerId(Long packerId);
    
    /**
     * Find unassigned picking orders
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.status IN ('READY_FOR_PICKING', 'ALLOCATED') AND fo.assignedPickerId IS NULL")
    List<FulfillmentOrder> findUnassignedPickingOrders();
    
    /**
     * Find orders requiring quality check
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.qualityCheckRequired = true AND fo.qualityCheckCompleted = false AND fo.status IN ('PICKING_COMPLETE', 'PACKING')")
    List<FulfillmentOrder> findOrdersRequiringQualityCheck();
    
    /**
     * Find quality failed orders
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.qualityStatus = 'FAILED'")
    List<FulfillmentOrder> findQualityFailedOrders();
    
    /**
     * Find by order date between
     */
    List<FulfillmentOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find fulfillment orders by warehouse ID
     *
     * @param warehouseId the warehouse ID
     * @return list of fulfillment orders
     */
    List<FulfillmentOrder> findByWarehouseId(Long warehouseId);

    /**
     * Find fulfillment orders by warehouse ID and status
     *
     * @param warehouseId the warehouse ID
     * @param status the fulfillment status
     * @return list of fulfillment orders
     */
    List<FulfillmentOrder> findByWarehouseIdAndStatus(Long warehouseId, FulfillmentStatus status);

    /**
     * Find pending fulfillment orders
     *
     * @return list of pending fulfillment orders
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.status = 'PENDING' ORDER BY fo.priority DESC, fo.createdAt ASC")
    List<FulfillmentOrder> findPendingOrders();

    /**
     * Find fulfillment orders created within a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of fulfillment orders
     */
    List<FulfillmentOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find fulfillment orders by external reference ID
     *
     * @param externalReferenceId the external reference ID
     * @return list of fulfillment orders
     */
    List<FulfillmentOrder> findByExternalReferenceId(String externalReferenceId);

    /**
     * Count fulfillment orders by warehouse ID and status
     *
     * @param warehouseId the warehouse ID
     * @param status the fulfillment status
     * @return count of fulfillment orders
     */
    long countByWarehouseIdAndStatus(Long warehouseId, FulfillmentStatus status);
    
    /**
     * Find active orders containing a specific SKU
     *
     * @param warehouseId the warehouse ID
     * @param sku the product SKU
     * @return list of active orders with the specified SKU
     */
    @Query("SELECT DISTINCT fo FROM FulfillmentOrder fo JOIN fo.orderItems item WHERE fo.warehouseId = :warehouseId AND item.sku = :sku AND fo.status NOT IN ('COMPLETED', 'DELIVERED', 'CANCELLED')")
    List<FulfillmentOrder> findActiveOrdersWithSku(@Param("warehouseId") Long warehouseId, @Param("sku") String sku);
    
    /**
     * Find pending orders containing a specific SKU
     *
     * @param sku the product SKU
     * @return list of pending orders with the specified SKU
     */
    @Query("SELECT DISTINCT fo FROM FulfillmentOrder fo JOIN fo.orderItems item WHERE item.sku = :sku AND fo.status = 'PENDING'")
    List<FulfillmentOrder> findPendingOrdersWithSku(@Param("sku") String sku);
    
    /**
     * Find fulfillment orders by warehouse ID and multiple statuses
     *
     * @param warehouseId the warehouse ID
     * @param statuses list of fulfillment statuses
     * @return list of fulfillment orders
     */
    List<FulfillmentOrder> findByWarehouseIdAndStatusIn(Long warehouseId, List<FulfillmentStatus> statuses);
    
    /**
     * Find fulfillment order by order ID (UUID) - renamed to avoid duplication
     */
    @Query("SELECT f FROM FulfillmentOrder f WHERE f.externalOrderId = :orderId")
    Optional<FulfillmentOrder> findByOrderIdString(@Param("orderId") String orderId);
    
    /**
     * Find fulfillment orders by assigned warehouse ID and multiple statuses
     *
     * @param assignedWarehouseId the assigned warehouse ID 
     * @param statuses list of fulfillment statuses
     * @return list of fulfillment orders
     */
    @Query("SELECT fo FROM FulfillmentOrder fo WHERE fo.warehouseId = :warehouseId AND fo.status IN :statuses")
    List<FulfillmentOrder> findByAssignedWarehouseIdAndStatusIn(@Param("warehouseId") Long warehouseId, @Param("statuses") List<FulfillmentStatus> statuses);
}
