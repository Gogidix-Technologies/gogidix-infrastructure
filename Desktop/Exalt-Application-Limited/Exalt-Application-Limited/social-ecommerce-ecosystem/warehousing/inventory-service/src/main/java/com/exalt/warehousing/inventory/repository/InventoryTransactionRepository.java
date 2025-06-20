package com.exalt.warehousing.inventory.repository;

import java.util.Map;

import com.exalt.warehousing.inventory.entity.InventoryTransaction;
import com.exalt.warehousing.inventory.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for inventory transaction operations
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {

    /**
     * Find all transactions for an inventory item
     * @param inventoryItemId the inventory item ID
     * @param pageable pagination information
     * @return paged result of transactions
     */
    Page<InventoryTransaction> findAllByInventoryItemId(UUID inventoryItemId, Pageable pageable);

    /**
     * Find all transactions for a warehouse
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return paged result of transactions
     */
    Page<InventoryTransaction> findAllByWarehouseId(UUID warehouseId, Pageable pageable);

    /**
     * Find all transactions by type
     * @param type the transaction type
     * @param pageable pagination information
     * @return paged result of transactions
     */
    Page<InventoryTransaction> findAllByType(TransactionType type, Pageable pageable);

    /**
     * Find all transactions by reference ID
     * @param referenceId the reference ID (order, purchase, etc.)
     * @return list of related transactions
     */
    List<InventoryTransaction> findAllByReferenceId(UUID referenceId);

    /**
     * Find transactions by date range
     * @param start start date and time
     * @param end end date and time
     * @param pageable pagination information
     * @return paged result of transactions
     */
    Page<InventoryTransaction> findAllByTimestampBetween(
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find transactions by user
     * @param userId the user ID
     * @param pageable pagination information
     * @return paged result of transactions
     */
    Page<InventoryTransaction> findAllByUserId(UUID userId, Pageable pageable);

    /**
     * Find transactions by inventory item, type, and date range
     * @param inventoryItemId the inventory item ID
     * @param type the transaction type
     * @param start start date and time
     * @param end end date and time
     * @return list of matching transactions
     */
    List<InventoryTransaction> findAllByInventoryItemIdAndTypeAndTimestampBetween(
            UUID inventoryItemId, TransactionType type, 
            LocalDateTime start, LocalDateTime end);

    /**
     * Get sum of quantities by inventory item and type
     * @param inventoryItemId the inventory item ID
     * @param type the transaction type
     * @return sum of transaction quantities
     */
    @Query("SELECT SUM(t.quantity) FROM InventoryTransaction t " +
           "WHERE t.inventoryItemId = :inventoryItemId AND t.type = :type")
    Integer getSumByInventoryItemAndType(UUID inventoryItemId, TransactionType type);

    /**
     * Get current inventory movement data
     * @param start start date
     * @param end end date
     * @return list of inventory movement data
     */
    @Query("SELECT new map(t.inventoryItemId as itemId, t.type as type, SUM(t.quantity) as total) " +
           "FROM InventoryTransaction t " +
           "WHERE t.timestamp BETWEEN :start AND :end " +
           "GROUP BY t.inventoryItemId, t.type")
    List<Object> getInventoryMovementData(LocalDateTime start, LocalDateTime end);
}

