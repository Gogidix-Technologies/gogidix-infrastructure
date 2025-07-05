package com.exalt.warehousing.inventory.repository;

import com.exalt.warehousing.inventory.entity.InventoryItem;
import com.exalt.warehousing.inventory.enums.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for inventory item operations
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    /**
     * Find an inventory item by SKU
     * @param sku the SKU to search for
     * @return the inventory item if found
     */
    Optional<InventoryItem> findBySku(String sku);

    /**
     * Find an inventory item by product ID
     * @param productId the product ID to search for
     * @return the inventory item if found
     */
    Optional<InventoryItem> findByProductId(Long productId);

    /**
     * Find all inventory items with low stock
     * @return list of items with available quantity below threshold
     */
    @Query("SELECT i FROM InventoryItem i WHERE (i.totalQuantity - i.reservedQuantity) <= i.lowStockThreshold AND i.isActive = true")
    List<InventoryItem> findAllLowStockItems();

    /**
     * Find all inventory items with critically low stock
     * @return list of items with available quantity below safety stock
     */
    @Query("SELECT i FROM InventoryItem i WHERE (i.totalQuantity - i.reservedQuantity) <= i.safetyStockLevel AND i.isActive = true")
    List<InventoryItem> findAllCriticalStockItems();
    
    /**
     * Find all items by status
     * @param status the status to filter by
     * @param pageable pagination information
     * @return paged result of inventory items
     */
    Page<InventoryItem> findAllByStatus(InventoryStatus status, Pageable pageable);
    
    /**
     * Find all active items
     * @param pageable pagination information
     * @return paged result of active inventory items
     */
    Page<InventoryItem> findAllByIsActiveTrue(Pageable pageable);
    
    /**
     * Search for inventory items by name or SKU
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return paged result of matching inventory items
     */
    @Query("SELECT i FROM InventoryItem i WHERE " +
           "(LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND i.isActive = true")
    Page<InventoryItem> searchByNameOrSku(String searchTerm, Pageable pageable);

    /**
     * Update inventory status
     * @param id the inventory item ID
     * @param status the new status
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryItem i SET i.status = :status WHERE i.id = :id")
    int updateStatus(UUID id, InventoryStatus status);
    
    /**
     * Update low stock threshold
     * @param id the inventory item ID
     * @param threshold the new threshold
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryItem i SET i.lowStockThreshold = :threshold WHERE i.id = :id")
    int updateLowStockThreshold(UUID id, int threshold);

    /**
     * Find inventory items by warehouse location
     * @param location the warehouse location
     * @return list of inventory items in the location
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.location = :location AND i.isActive = true")
    List<InventoryItem> findByLocation(String location);

    /**
     * Find inventory items by warehouse zone
     * @param zone the warehouse zone
     * @return list of inventory items in the zone
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.zone = :zone AND i.isActive = true")
    List<InventoryItem> findByZone(String zone);
}
