package com.exalt.warehousing.management.service;

import java.util.UUID;

/**
 * Interface for inventory operations
 * This is a stub interface to be implemented with actual integration
 */
public interface InventoryService {
    
    /**
     * Increment inventory quantity for an item
     *
     * @param inventoryItemId the inventory item ID
     * @param quantity the quantity to increment
     * @return true if successful, false otherwise
     */
    boolean incrementInventory(UUID inventoryItemId, int quantity);
    
    /**
     * Decrement inventory quantity for an item
     *
     * @param inventoryItemId the inventory item ID
     * @param quantity the quantity to decrement
     * @return true if successful, false otherwise
     */
    boolean decrementInventory(UUID inventoryItemId, int quantity);
    
    /**
     * Check if inventory item exists
     *
     * @param inventoryItemId the inventory item ID
     * @return true if exists, false otherwise
     */
    boolean inventoryItemExists(UUID inventoryItemId);
    
    /**
     * Get available quantity for an inventory item
     *
     * @param inventoryItemId the inventory item ID
     * @return the available quantity
     */
    int getAvailableQuantity(UUID inventoryItemId);
} 
