package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.InventoryItemReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for accessing inventory items from the warehouse management service
 * This is a simplified interface for integration with the Inventory Service
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItemReference, String> {

    /**
     * Find all inventory items by warehouse ID
     *
     * @param warehouseId the warehouse ID
     * @return list of inventory items in the warehouse
     */
    List<InventoryItemReference> findAllByWarehouseId(UUID warehouseId);

    /**
     * Find the location ID for an inventory item
     *
     * @param itemId the inventory item ID
     * @return the location ID where the item is stored
     */
    @Query("SELECT i.locationId FROM InventoryItemReference i WHERE i.id = :itemId")
    UUID findLocationIdByItemId(UUID itemId);

    /**
     * Find the product ID for an inventory item
     *
     * @param itemId the inventory item ID
     * @return the product ID associated with this item
     */
    @Query("SELECT i.productId FROM InventoryItemReference i WHERE i.id = :itemId")
    UUID findProductIdByItemId(UUID itemId);

    /**
     * Find the SKU for an inventory item
     *
     * @param itemId the inventory item ID
     * @return the SKU associated with this item
     */
    @Query("SELECT i.sku FROM InventoryItemReference i WHERE i.id = :itemId")
    String findSkuByItemId(UUID itemId);

    /**
     * Find the product name for an inventory item
     *
     * @param itemId the inventory item ID
     * @return the product name associated with this item
     */
    @Query("SELECT i.productName FROM InventoryItemReference i WHERE i.id = :itemId")
    String findProductNameByItemId(UUID itemId);

    /**
     * Find the quantity for an inventory item
     *
     * @param itemId the inventory item ID
     * @return the quantity of the item
     */
    @Query("SELECT i.availableQuantity FROM InventoryItemReference i WHERE i.id = :itemId")
    Integer findQuantityByItemId(UUID itemId);
} 
