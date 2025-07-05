package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.ReturnRequest;
import com.exalt.warehousing.management.model.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for return request operations
 */
@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {

    /**
     * Find return request by return code
     * 
     * @param returnCode the return code
     * @return the return request if found
     */
    Optional<ReturnRequest> findByReturnCode(String returnCode);
    
    /**
     * Find all return requests for a specific order
     * 
     * @param orderId the order ID
     * @return list of return requests
     */
    List<ReturnRequest> findByOrderId(UUID orderId);
    
    /**
     * Find all return requests for a specific customer
     * 
     * @param customerId the customer ID
     * @param pageable pagination information
     * @return page of return requests
     */
    Page<ReturnRequest> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * Find all return requests for a specific warehouse
     * 
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return page of return requests
     */
    Page<ReturnRequest> findByWarehouseId(UUID warehouseId, Pageable pageable);
    
    /**
     * Find all return requests with a specific status
     * 
     * @param status the return status
     * @param pageable pagination information
     * @return page of return requests
     */
    Page<ReturnRequest> findByStatus(ReturnStatus status, Pageable pageable);
    
    /**
     * Find all return requests with a specific status for a warehouse
     * 
     * @param warehouseId the warehouse ID
     * @param status the return status
     * @param pageable pagination information
     * @return page of return requests
     */
    Page<ReturnRequest> findByWarehouseIdAndStatus(UUID warehouseId, ReturnStatus status, Pageable pageable);
    
    /**
     * Find all return requests that were created within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return page of return requests
     */
    Page<ReturnRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find all return requests that were received within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return page of return requests
     */
    Page<ReturnRequest> findByReceivedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Count return requests by status
     * 
     * @param status the return status
     * @return count of return requests
     */
    long countByStatus(ReturnStatus status);
    
    /**
     * Count return requests by status for a specific warehouse
     * 
     * @param warehouseId the warehouse ID
     * @param status the return status
     * @return count of return requests
     */
    long countByWarehouseIdAndStatus(UUID warehouseId, ReturnStatus status);
    
    /**
     * Get counts of return requests grouped by status for reporting
     * 
     * @param warehouseId the warehouse ID
     * @return list of status counts
     */
    @Query("SELECT r.status as status, COUNT(r) as count FROM ReturnRequest r " +
           "WHERE r.warehouseId = :warehouseId GROUP BY r.status")
    List<Object[]> countReturnsByStatusForWarehouse(UUID warehouseId);
    
    /**
     * Find all return requests that need to be processed (received but not yet inspected)
     * 
     * @param warehouseId the warehouse ID
     * @return list of return requests
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.warehouseId = :warehouseId " +
           "AND r.status = 'RECEIVED' ORDER BY r.receivedAt ASC")
    List<ReturnRequest> findReturnsNeedingProcessing(UUID warehouseId);
    
    /**
     * Find all return requests that need inventory reintegration
     * 
     * @param warehouseId the warehouse ID
     * @return list of return requests
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.warehouseId = :warehouseId " +
           "AND r.status = 'ACCEPTED' AND r.inventoryReintegrated = false")
    List<ReturnRequest> findReturnsNeedingInventoryReintegration(UUID warehouseId);
} 
