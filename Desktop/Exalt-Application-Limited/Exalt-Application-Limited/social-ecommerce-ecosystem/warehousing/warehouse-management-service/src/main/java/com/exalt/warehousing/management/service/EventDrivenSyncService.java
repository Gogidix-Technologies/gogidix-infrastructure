package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.config.CacheConfig;
import com.exalt.warehousing.management.event.InventoryEvent;
import com.exalt.warehousing.management.event.OrderEvent;
import com.exalt.warehousing.management.model.InventoryItemReference;
import com.exalt.warehousing.management.model.OrderItemReference;
import com.exalt.warehousing.management.repository.InventoryItemRepository;
import com.exalt.warehousing.management.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Event-driven synchronization service for processing inventory and order events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventDrivenSyncService {

    private final InventoryItemRepository inventoryItemRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Listen for inventory events and update local reference data
     *
     * @param event the inventory event
     */
    @KafkaListener(topics = "${kafka.topics.inventory-events:inventory-events}", groupId = "${spring.kafka.consumer.group-id:warehouse-management-service}")
    @Transactional
    public void handleInventoryEvent(InventoryEvent event) {
        log.info("Received inventory event: type={}, itemId={}", event.getType(), event.getInventoryItemId());
        
        try {
            switch (event.getType()) {
                case CREATED:
                case UPDATED:
                case QUANTITY_CHANGED:
                case RELOCATED:
                    updateInventoryItem(event);
                    break;
                case DELETED:
                    deleteInventoryItem(event.getInventoryItemId());
                    break;
                default:
                    log.warn("Unhandled inventory event type: {}", event.getType());
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing inventory event: {}", e.getMessage(), e);
        }
    }

    /**
     * Listen for order events and update local reference data
     *
     * @param event the order event
     */
    @KafkaListener(topics = "${kafka.topics.order-events:order-events}", groupId = "${spring.kafka.consumer.group-id:warehouse-management-service}")
    @Transactional
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: type={}, orderId={}", event.getType(), event.getOrderId());
        
        try {
            switch (event.getType()) {
                case CREATED:
                case UPDATED:
                case ITEM_ADDED:
                case ITEM_UPDATED:
                    updateOrderItems(event);
                    break;
                case CANCELLED:
                case COMPLETED:
                    // These events might require special handling depending on business rules
                    updateOrderItems(event);
                    break;
                case ITEM_REMOVED:
                    if (event.getOrderItems() != null && !event.getOrderItems().isEmpty()) {
                        List<UUID> itemIds = event.getOrderItems().stream()
                                .map(item -> item.getId())
                                .collect(Collectors.toList());
                        deleteOrderItems(itemIds);
                    }
                    break;
                default:
                    log.warn("Unhandled order event type: {}", event.getType());
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    /**
     * Update inventory item reference data from event
     *
     * @param event the inventory event
     */
    @CacheEvict(value = {CacheConfig.INVENTORY_ITEMS_CACHE, CacheConfig.WAREHOUSE_INVENTORY_CACHE}, 
                key = "#event.inventoryItemId", allEntries = true)
    private void updateInventoryItem(InventoryEvent event) {
        if (event.getInventoryItem() == null) {
            log.warn("Inventory event contains no item data: {}", event.getEventId());
            return;
        }

        InventoryItemReference item = InventoryItemReference.builder()
                .id(event.getInventoryItem().getId() != null ? event.getInventoryItem().getId().toString() : null)
                .productId(event.getInventoryItem().getProductId())
                .sku(event.getInventoryItem().getProductSku())
                .productName(event.getInventoryItem().getProductName())
                .warehouseId(event.getInventoryItem().getWarehouseId())
                .locationId(event.getInventoryItem().getLocationId())
                .availableQuantity(event.getInventoryItem().getAvailableQuantity())
                .lastSyncedAt(LocalDateTime.now())
                .build();

        inventoryItemRepository.save(item);
        log.info("Updated inventory item reference: {}", item.getId());
    }

    /**
     * Delete inventory item reference data
     *
     * @param itemId the inventory item ID
     */
    @CacheEvict(value = {CacheConfig.INVENTORY_ITEMS_CACHE, CacheConfig.WAREHOUSE_INVENTORY_CACHE}, 
                key = "#itemId", allEntries = true)
    private void deleteInventoryItem(UUID itemId) {
        inventoryItemRepository.deleteById(itemId.toString());
        log.info("Deleted inventory item reference: {}", itemId);
    }

    /**
     * Update order item reference data from event
     *
     * @param event the order event
     */
    @CacheEvict(value = {CacheConfig.ORDER_ITEMS_CACHE, CacheConfig.ORDER_ITEMS_BY_ORDER_CACHE}, 
                key = "#event.orderId", allEntries = true)
    private void updateOrderItems(OrderEvent event) {
        if (event.getOrderItems() == null || event.getOrderItems().isEmpty()) {
            log.warn("Order event contains no item data: {}", event.getEventId());
            return;
        }

        List<OrderItemReference> items = new ArrayList<>();
        for (var orderItem : event.getOrderItems()) {
            OrderItemReference item = OrderItemReference.builder()
                    .id(orderItem.getId() != null ? orderItem.getId().toString() : null)
                    .orderId(event.getOrderId())
                    .inventoryItemId(orderItem.getInventoryItemId())
                    .productId(orderItem.getProductId())
                    .sku(orderItem.getSku())
                    .quantity(orderItem.getQuantity())
                    .status(orderItem.getStatus())
                    .lastSyncedAt(LocalDateTime.now())
                    .build();
            items.add(item);
        }

        orderItemRepository.saveAll(items);
        log.info("Updated {} order item references for order: {}", items.size(), event.getOrderId());
    }

    /**
     * Delete order item reference data
     *
     * @param itemIds the order item IDs to delete
     */
    private void deleteOrderItems(List<UUID> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }
        
        for (UUID id : itemIds) {
            Optional<OrderItemReference> orderItem = orderItemRepository.findById(id.toString());
            if (orderItem.isPresent()) {
                UUID orderId = orderItem.get().getOrderId();
                // Evict cache for this specific order
                evictOrderCache(orderId);
            }
            orderItemRepository.deleteById(id.toString());
        }
        
        log.info("Deleted {} order item references", itemIds.size());
    }
    
    /**
     * Evict order cache for a specific order
     *
     * @param orderId the order ID
     */
    @CacheEvict(value = CacheConfig.ORDER_ITEMS_BY_ORDER_CACHE, key = "#orderId")
    public void evictOrderCache(UUID orderId) {
        log.debug("Evicting cache for order: {}", orderId);
    }
} 
