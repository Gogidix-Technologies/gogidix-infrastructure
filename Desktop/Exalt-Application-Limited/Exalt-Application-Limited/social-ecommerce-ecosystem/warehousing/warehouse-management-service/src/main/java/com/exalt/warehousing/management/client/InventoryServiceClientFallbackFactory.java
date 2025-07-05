package com.exalt.warehousing.management.client;

import com.exalt.warehousing.management.dto.InventoryItemDTO;
import com.exalt.warehousing.management.dto.InventoryLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fallback factory for the Inventory Service client
 */
@Component
public class InventoryServiceClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClientFallbackFactory.class);

    @Override
    public InventoryServiceClient create(Throwable cause) {
        log.error("Fallback triggered for InventoryServiceClient due to: {}", cause.getMessage(), cause);
        
        return new InventoryServiceClient() {
            @Override
            public ResponseEntity<List<InventoryItemDTO>> getAllInventoryItems() {
                log.warn("Fallback: getAllInventoryItems");
                return ResponseEntity.ok(Collections.emptyList());
            }

            @Override
            public ResponseEntity<List<InventoryItemDTO>> getInventoryByLocationId(UUID locationId) {
                log.warn("Fallback: getInventoryByLocationId for locationId: {}", locationId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            @Override
            public ResponseEntity<List<InventoryItemDTO>> getInventoryByWarehouseId(UUID warehouseId) {
                log.warn("Fallback: getInventoryByWarehouseId for warehouseId: {}", warehouseId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            @Override
            public ResponseEntity<InventoryItemDTO> reserveInventory(UUID locationId, InventoryItemDTO inventoryItem) {
                log.warn("Fallback: reserveInventory at locationId: {}", locationId);
                return ResponseEntity.ok(inventoryItem); // Return unchanged, let application handle fallback logic
            }

            @Override
            public ResponseEntity<InventoryItemDTO> releaseInventory(UUID locationId, InventoryItemDTO inventoryItem) {
                log.warn("Fallback: releaseInventory at locationId: {}", locationId);
                return ResponseEntity.ok(inventoryItem); // Return unchanged, let application handle fallback logic
            }

            @Override
            public ResponseEntity<List<InventoryLocationDTO>> getProductLocations(UUID productId, UUID warehouseId) {
                log.warn("Fallback: getProductLocations for productId: {} in warehouseId: {}", productId, warehouseId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            @Override
            public ResponseEntity<InventoryItemDTO> updateInventory(UUID locationId, InventoryItemDTO inventoryItem) {
                log.warn("Fallback: updateInventory at locationId: {}", locationId);
                return ResponseEntity.ok(inventoryItem); // Return unchanged, let application handle fallback logic
            }
        };
    }
} 
