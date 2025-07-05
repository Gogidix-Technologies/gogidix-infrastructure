package com.exalt.warehousing.management.client;

import com.exalt.warehousing.management.dto.InventoryItemDTO;
import com.exalt.warehousing.management.dto.InventoryLocationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for the Inventory Service
 */
@FeignClient(name = "inventory-service", fallbackFactory = InventoryServiceClientFallbackFactory.class)
public interface InventoryServiceClient {

    /**
     * Get all inventory items
     *
     * @return list of all inventory items
     */
    @GetMapping("/api/v1/inventory/items")
    ResponseEntity<List<InventoryItemDTO>> getAllInventoryItems();

    /**
     * Get inventory items by location id
     *
     * @param locationId the location id
     * @return list of inventory items at the location
     */
    @GetMapping("/api/v1/inventory/location/{locationId}")
    ResponseEntity<List<InventoryItemDTO>> getInventoryByLocationId(@PathVariable UUID locationId);

    /**
     * Get inventory items by warehouse id
     *
     * @param warehouseId the warehouse id
     * @return list of inventory items in the warehouse
     */
    @GetMapping("/api/v1/inventory/warehouse/{warehouseId}")
    ResponseEntity<List<InventoryItemDTO>> getInventoryByWarehouseId(@PathVariable UUID warehouseId);

    /**
     * Reserve inventory items at a location
     *
     * @param locationId the location id
     * @param inventoryItem the inventory item data to reserve
     * @return the updated inventory item
     */
    @PostMapping("/api/v1/inventory/location/{locationId}/reserve")
    ResponseEntity<InventoryItemDTO> reserveInventory(@PathVariable UUID locationId, 
                                                     @RequestBody InventoryItemDTO inventoryItem);

    /**
     * Release reserved inventory at a location
     *
     * @param locationId the location id
     * @param inventoryItem the inventory item data to release
     * @return the updated inventory item
     */
    @PostMapping("/api/v1/inventory/location/{locationId}/release")
    ResponseEntity<InventoryItemDTO> releaseInventory(@PathVariable UUID locationId, 
                                                     @RequestBody InventoryItemDTO inventoryItem);

    /**
     * Get inventory locations for a specific product
     *
     * @param productId the product id
     * @param warehouseId the warehouse id (optional)
     * @return list of inventory locations for the product
     */
    @GetMapping("/api/v1/inventory/product/{productId}/locations")
    ResponseEntity<List<InventoryLocationDTO>> getProductLocations(@PathVariable UUID productId,
                                                                  @RequestParam(required = false) UUID warehouseId);

    /**
     * Update inventory count at a location
     *
     * @param locationId the location id
     * @param inventoryItem the inventory item data to update
     * @return the updated inventory item
     */
    @PutMapping("/api/v1/inventory/location/{locationId}")
    ResponseEntity<InventoryItemDTO> updateInventory(@PathVariable UUID locationId, 
                                                    @RequestBody InventoryItemDTO inventoryItem);
} 
