package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.client.InventoryServiceClient;
import com.exalt.warehousing.management.client.OrderServiceClient;
import com.exalt.warehousing.management.config.CacheConfig;
import com.exalt.warehousing.management.model.InventoryItemReference;
import com.exalt.warehousing.management.model.OrderItemReference;
import com.exalt.warehousing.management.repository.InventoryItemRepository;
import com.exalt.warehousing.management.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for synchronizing reference data from external services
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReferenceDataSyncService {

    private final InventoryServiceClient inventoryServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final InventoryItemRepository inventoryItemRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Synchronize inventory item reference data
     * Scheduled to run every 15 minutes
     */
    @Scheduled(fixedRateString = "${reference.sync.interval:900000}")
    @Transactional
    @CacheEvict(value = CacheConfig.INVENTORY_ITEMS_CACHE, allEntries = true)
    public void syncInventoryItemReferences() {
        log.info("Starting inventory item reference data synchronization");
        try {
            List<InventoryItemReference> references = inventoryServiceClient.getAllInventoryItems().getBody()
                    .stream()
                    .map(item -> InventoryItemReference.builder()
                            .id(item.getId().toString())
                            .productId(item.getProductId())
                            .sku(item.getProductSku())
                            .productName(item.getProductName())
                            .warehouseId(item.getWarehouseId())
                            .locationId(item.getLocationId())
                            .availableQuantity(item.getAvailableQuantity())
                            .lastSyncedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            inventoryItemRepository.saveAll(references);
            log.info("Successfully synchronized {} inventory item references", references.size());
        } catch (Exception e) {
            log.error("Failed to synchronize inventory item references", e);
        }
    }

    /**
     * Synchronize order item reference data
     * Scheduled to run every 15 minutes
     */
    @Scheduled(fixedRateString = "${reference.sync.interval:900000}")
    @Transactional
    @CacheEvict(value = CacheConfig.ORDER_ITEMS_CACHE, allEntries = true)
    public void syncOrderItemReferences() {
        log.info("Starting order item reference data synchronization");
        try {
            List<OrderItemReference> references = orderServiceClient.getAllOrderItems().getBody()
                    .stream()
                    .map(item -> OrderItemReference.builder()
                            .id(item.getId().toString())
                            .orderId(item.getOrderId())
                            .inventoryItemId(item.getInventoryItemId())
                            .productId(item.getProductId())
                            .sku(item.getSku())
                            .quantity(item.getQuantity())
                            .status(item.getStatus())
                            .lastSyncedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            orderItemRepository.saveAll(references);
            log.info("Successfully synchronized {} order item references", references.size());
        } catch (Exception e) {
            log.error("Failed to synchronize order item references", e);
        }
    }

    /**
     * Manually trigger synchronization for a specific warehouse
     *
     * @param warehouseId the warehouse ID
     */
    @Transactional
    @CacheEvict(value = CacheConfig.WAREHOUSE_INVENTORY_CACHE, key = "#warehouseId")
    public void syncInventoryForWarehouse(UUID warehouseId) {
        log.info("Manually synchronizing inventory data for warehouse: {}", warehouseId);
        try {
            List<InventoryItemReference> references = inventoryServiceClient.getInventoryByWarehouseId(warehouseId).getBody()
                    .stream()
                    .map(item -> InventoryItemReference.builder()
                            .id(item.getId().toString())
                            .productId(item.getProductId())
                            .sku(item.getProductSku())
                            .productName(item.getProductName())
                            .warehouseId(warehouseId)
                            .locationId(item.getLocationId())
                            .availableQuantity(item.getAvailableQuantity())
                            .lastSyncedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            inventoryItemRepository.saveAll(references);
            log.info("Successfully synchronized {} inventory items for warehouse {}", references.size(), warehouseId);
        } catch (Exception e) {
            log.error("Failed to synchronize inventory for warehouse: {}", warehouseId, e);
        }
    }

    /**
     * Manually trigger synchronization for a specific order
     *
     * @param orderId the order ID
     */
    @Transactional
    @CacheEvict(value = CacheConfig.ORDER_ITEMS_BY_ORDER_CACHE, key = "#orderId")
    public void syncOrderItems(UUID orderId) {
        log.info("Manually synchronizing order items for order: {}", orderId);
        try {
            List<OrderItemReference> references = orderServiceClient.getOrderItemsByOrderId(orderId).getBody()
                    .stream()
                    .map(item -> OrderItemReference.builder()
                            .id(item.getId().toString())
                            .orderId(orderId)
                            .inventoryItemId(item.getInventoryItemId())
                            .productId(item.getProductId())
                            .sku(item.getSku())
                            .quantity(item.getQuantity())
                            .status(item.getStatus())
                            .lastSyncedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            orderItemRepository.saveAll(references);
            log.info("Successfully synchronized {} order items for order {}", references.size(), orderId);
        } catch (Exception e) {
            log.error("Failed to synchronize order items for order: {}", orderId, e);
        }
    }
    
    /**
     * Get inventory item references by warehouse ID with caching
     *
     * @param warehouseId the warehouse ID
     * @return list of inventory item references for the warehouse
     */
    @Cacheable(value = CacheConfig.WAREHOUSE_INVENTORY_CACHE, key = "#warehouseId")
    @Transactional(readOnly = true)
    public List<InventoryItemReference> getInventoryItemsByWarehouseId(UUID warehouseId) {
        log.debug("Fetching inventory items for warehouse {} from database", warehouseId);
        return inventoryItemRepository.findAllByWarehouseId(warehouseId);
    }
    
    /**
     * Get order item references by order ID with caching
     *
     * @param orderId the order ID
     * @return list of order item references for the order
     */
    @Cacheable(value = CacheConfig.ORDER_ITEMS_BY_ORDER_CACHE, key = "#orderId")
    @Transactional(readOnly = true)
    public List<OrderItemReference> getOrderItemsByOrderId(UUID orderId) {
        log.debug("Fetching order items for order {} from database", orderId);
        return orderItemRepository.findAllByOrderId(orderId);
    }
    
    /**
     * Get inventory item reference by ID with caching
     *
     * @param itemId the item ID
     * @return the inventory item reference if found
     */
    @Cacheable(value = CacheConfig.INVENTORY_ITEMS_CACHE, key = "#itemId")
    @Transactional(readOnly = true)
    public Optional<InventoryItemReference> getInventoryItemById(UUID itemId) {
        log.debug("Fetching inventory item {} from database", itemId);
        return inventoryItemRepository.findById(itemId.toString());
    }
} 
