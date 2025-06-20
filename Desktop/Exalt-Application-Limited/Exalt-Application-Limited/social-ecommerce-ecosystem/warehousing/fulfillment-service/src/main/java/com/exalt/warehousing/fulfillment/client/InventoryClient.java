package com.exalt.warehousing.fulfillment.client;

import com.exalt.warehousing.fulfillment.dto.inventory.InventoryAllocationRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryAllocationResponse;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryCheckRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryCheckResponse;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReleaseRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReservationRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReservationResponse;

import java.util.UUID;

/**
 * Client interface for interacting with the Inventory Service
 */
public interface InventoryClient {

    /**
     * Check if inventory is available for the requested items
     *
     * @param request The inventory check request
     * @return The inventory check response with availability information
     */
    InventoryCheckResponse checkInventoryAvailability(InventoryCheckRequest request);

    /**
     * Check if inventory is available in a specific warehouse
     *
     * @param warehouseId The warehouse ID to check
     * @param request The inventory check request
     * @return The inventory check response with availability information
     */
    InventoryCheckResponse checkWarehouseInventory(UUID warehouseId, InventoryCheckRequest request);

    /**
     * Get the recommended warehouse for fulfilling an order
     *
     * @param request The inventory check request
     * @return The ID of the recommended warehouse
     */
    UUID getRecommendedWarehouse(InventoryCheckRequest request);

    /**
     * Reserve inventory for the requested items
     *
     * @param request The inventory reservation request
     * @return The inventory reservation response
     */
    InventoryReservationResponse reserveInventory(InventoryReservationRequest request);

    /**
     * Release previously reserved inventory
     *
     * @param request The inventory release request
     * @return True if successful, false otherwise
     */
    boolean releaseInventory(InventoryReleaseRequest request);

    /**
     * Allocate inventory for picking
     *
     * @param request The inventory allocation request
     * @return The inventory allocation response
     */
    InventoryAllocationResponse allocateInventory(InventoryAllocationRequest request);
} 
