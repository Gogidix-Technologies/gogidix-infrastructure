package com.exalt.warehousing.inventory.service;

import com.exalt.warehousing.inventory.entity.InventoryItem;
import com.exalt.warehousing.inventory.enums.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for inventory management operations
 */
public interface InventoryService {

    /**
     * Get an inventory item by ID
     * @param id the inventory item ID
     * @return the inventory item
     */
    InventoryItem getInventoryItemById(UUID id);

    /**
     * Get an inventory item by SKU
     * @param sku the SKU
     * @return the inventory item
     */
    InventoryItem getInventoryItemBySku(String sku);

    /**
     * Get an inventory item by product ID
     * @param productId the product ID
     * @return the inventory item
     */
    InventoryItem getInventoryItemByProductId(Long productId);

    /**
     * Create a new inventory item
     * @param inventoryItem the inventory item to create
     * @return the created inventory item
     */
    InventoryItem createInventoryItem(InventoryItem inventoryItem);

    /**
     * Update an existing inventory item
     * @param id the inventory item ID
     * @param inventoryItem the updated data
     * @return the updated inventory item
     */
    InventoryItem updateInventoryItem(UUID id, InventoryItem inventoryItem);

    /**
     * Delete an inventory item
     * @param id the inventory item ID
     */
    void deleteInventoryItem(UUID id);

    /**
     * Get all inventory items
     * @param pageable pagination information
     * @return paged result of inventory items
     */
    Page<InventoryItem> getAllInventoryItems(Pageable pageable);

    /**
     * Get all active inventory items
     * @param pageable pagination information
     * @return paged result of active inventory items
     */
    Page<InventoryItem> getActiveInventoryItems(Pageable pageable);

    /**
     * Search inventory items by name or SKU
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return paged result of matching inventory items
     */
    Page<InventoryItem> searchInventoryItems(String searchTerm, Pageable pageable);

    /**
     * Update inventory status
     * @param id the inventory item ID
     * @param status the new status
     * @return the updated inventory item
     */
    InventoryItem updateInventoryStatus(UUID id, InventoryStatus status);

    /**
     * Get all low stock items
     * @return list of items with quantity below threshold
     */
    List<InventoryItem> getLowStockItems();

    /**
     * Get all critically low stock items
     * @return list of items with quantity below safety stock
     */
    List<InventoryItem> getCriticalStockItems();

    /**
     * Check if an item has sufficient available quantity
     * @param itemId the inventory item ID
     * @param quantity the required quantity
     * @return true if sufficient quantity is available
     */
    boolean checkAvailability(UUID itemId, int quantity);

    /**
     * Get inventory items by status
     * @param status the status to filter by
     * @param pageable pagination information
     * @return paged result of matching inventory items
     */
    Page<InventoryItem> getInventoryItemsByStatus(InventoryStatus status, Pageable pageable);

    /**
     * Update inventory thresholds
     * @param id the inventory item ID
     * @param lowStockThreshold the new low stock threshold
     * @param safetyStockLevel the new safety stock level
     * @return the updated inventory item
     */
    InventoryItem updateInventoryThresholds(UUID id, int lowStockThreshold, int safetyStockLevel);

    /**
     * Adjust inventory quantity
     * @param id the inventory item ID
     * @param adjustment the quantity adjustment (positive or negative)
     * @param reason the reason for adjustment
     * @param userId the user making the adjustment
     * @return the updated inventory item
     */
    InventoryItem adjustInventory(UUID id, int adjustment, String reason, UUID userId);

    /**
     * Mark item for quality check
     * @param id the inventory item ID
     * @param reason the reason for quality check
     * @return the updated inventory item
     */
    InventoryItem markForQualityCheck(UUID id, String reason);

    /**
     * Quarantine an inventory item
     * @param id the inventory item ID
     * @param reason the reason for quarantine
     * @return the updated inventory item
     */
    InventoryItem quarantineItem(UUID id, String reason);

    /**
     * Get inventory items by warehouse location
     * @param location the warehouse location
     * @return list of inventory items in the location
     */
    List<InventoryItem> getItemsByLocation(String location);

    /**
     * Get inventory items by warehouse zone
     * @param zone the warehouse zone
     * @return list of inventory items in the zone
     */
    List<InventoryItem> getItemsByZone(String zone);
}
