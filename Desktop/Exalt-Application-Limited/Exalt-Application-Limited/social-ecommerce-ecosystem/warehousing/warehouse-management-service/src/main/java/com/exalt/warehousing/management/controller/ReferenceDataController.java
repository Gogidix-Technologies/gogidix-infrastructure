package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.model.InventoryItemReference;
import com.exalt.warehousing.management.model.OrderItemReference;
import com.exalt.warehousing.management.service.ReferenceDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for accessing reference data
 */
@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
@Slf4j
public class ReferenceDataController {

    private final ReferenceDataSyncService referenceDataSyncService;

    /**
     * Get all inventory items by warehouse ID
     *
     * @param warehouseId the warehouse ID
     * @return list of inventory items in the warehouse
     */
    @GetMapping("/inventory/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryItemReference>> getInventoryItemsByWarehouseId(@PathVariable UUID warehouseId) {
        log.info("Fetching inventory items for warehouse: {}", warehouseId);
        return ResponseEntity.ok(referenceDataSyncService.getInventoryItemsByWarehouseId(warehouseId));
    }

    /**
     * Get inventory item by ID
     *
     * @param itemId the item ID
     * @return the inventory item if found
     */
    @GetMapping("/inventory/item/{itemId}")
    public ResponseEntity<InventoryItemReference> getInventoryItemById(@PathVariable UUID itemId) {
        log.info("Fetching inventory item: {}", itemId);
        return referenceDataSyncService.getInventoryItemById(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all order items by order ID
     *
     * @param orderId the order ID
     * @return list of order items for the order
     */
    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<OrderItemReference>> getOrderItemsByOrderId(@PathVariable UUID orderId) {
        log.info("Fetching order items for order: {}", orderId);
        return ResponseEntity.ok(referenceDataSyncService.getOrderItemsByOrderId(orderId));
    }

    /**
     * Manually trigger synchronization for a specific warehouse
     *
     * @param warehouseId the warehouse ID
     * @return success response
     */
    @PostMapping("/sync/warehouse/{warehouseId}")
    public ResponseEntity<String> syncWarehouseInventory(@PathVariable UUID warehouseId) {
        log.info("Triggering manual synchronization for warehouse: {}", warehouseId);
        referenceDataSyncService.syncInventoryForWarehouse(warehouseId);
        return ResponseEntity.ok("Synchronization triggered for warehouse: " + warehouseId);
    }

    /**
     * Manually trigger synchronization for a specific order
     *
     * @param orderId the order ID
     * @return success response
     */
    @PostMapping("/sync/order/{orderId}")
    public ResponseEntity<String> syncOrderItems(@PathVariable UUID orderId) {
        log.info("Triggering manual synchronization for order: {}", orderId);
        referenceDataSyncService.syncOrderItems(orderId);
        return ResponseEntity.ok("Synchronization triggered for order: " + orderId);
    }

    /**
     * Manually trigger synchronization of all inventory items
     *
     * @return success response
     */
    @PostMapping("/sync/inventory")
    public ResponseEntity<String> syncAllInventoryItems() {
        log.info("Triggering manual synchronization for all inventory items");
        referenceDataSyncService.syncInventoryItemReferences();
        return ResponseEntity.ok("Synchronization triggered for all inventory items");
    }

    /**
     * Manually trigger synchronization of all order items
     *
     * @return success response
     */
    @PostMapping("/sync/orders")
    public ResponseEntity<String> syncAllOrderItems() {
        log.info("Triggering manual synchronization for all order items");
        referenceDataSyncService.syncOrderItemReferences();
        return ResponseEntity.ok("Synchronization triggered for all order items");
    }
} 
