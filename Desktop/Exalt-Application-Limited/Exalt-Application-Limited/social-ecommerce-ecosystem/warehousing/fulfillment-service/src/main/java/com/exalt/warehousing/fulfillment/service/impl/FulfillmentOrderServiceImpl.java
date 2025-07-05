package com.exalt.warehousing.fulfillment.service.impl;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.InventoryStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.exalt.warehousing.fulfillment.exception.ResourceNotFoundException;
import com.exalt.warehousing.fulfillment.repository.FulfillmentOrderRepository;
import com.exalt.warehousing.fulfillment.service.FulfillmentOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the FulfillmentOrderService
 * Simplified to work directly with entities to avoid duplication
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FulfillmentOrderServiceImpl implements FulfillmentOrderService {

    private final FulfillmentOrderRepository fulfillmentOrderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public FulfillmentOrder createFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        log.info("Creating new fulfillment order for external order ID: {}", fulfillmentOrder.getExternalOrderId());
        
        // Set initial status
        if (fulfillmentOrder.getStatus() == null) {
            fulfillmentOrder.setStatus(FulfillmentStatus.RECEIVED);
        }
        
        // Set order date if not set
        if (fulfillmentOrder.getOrderDate() == null) {
            fulfillmentOrder.setOrderDate(LocalDateTime.now());
        }
        
        // Initialize items status if not set
        if (fulfillmentOrder.getOrderItems() != null) {
            fulfillmentOrder.getOrderItems().forEach(item -> {
                if (item.getStatus() == null) {
                    item.setStatus(ItemFulfillmentStatus.PENDING);
                }
                if (item.getQuantityFulfilled() == null) {
                    item.setQuantityFulfilled(0);
                }
                if (item.getQuantityPicked() == null) {
                    item.setQuantityPicked(0);
                }
                if (item.getQuantityPacked() == null) {
                    item.setQuantityPacked(0);
                }
                item.setFulfillmentOrder(fulfillmentOrder);
            });
        }
        
        // Save the fulfillment order
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(fulfillmentOrder);
        
        // Publish event for new fulfillment order
        publishFulfillmentOrderEvent(savedOrder, "FULFILLMENT_ORDER_CREATED");
        
        return savedOrder;
    }

    @Override
    public FulfillmentOrder getFulfillmentOrder(String fulfillmentOrderId) {
        log.debug("Getting fulfillment order with ID: {}", fulfillmentOrderId);
        
        // Convert UUID to Long for entity ID
        
        
        return fulfillmentOrderRepository.findById(fulfillmentOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment order not found with ID: " + fulfillmentOrderId));
    }

    @Override
    public List<FulfillmentOrder> getFulfillmentOrdersByOrderId(String orderId) {
        log.debug("Getting fulfillment orders for order ID: {}", orderId);
        
        
        Optional<FulfillmentOrder> optionalOrder = fulfillmentOrderRepository.findByExternalOrderId(orderId);
        return optionalOrder.map(Arrays::asList).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public FulfillmentOrder updateFulfillmentOrderStatus(String fulfillmentOrderId, FulfillmentStatus status) {
        log.info("Updating fulfillment order {} status to: {}", fulfillmentOrderId, status);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        // Set appropriate timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        
        switch (status) {
            case PROCESSING:
                order.setStatus(status);
                break;
            case PICKING:
                order.setPickStartedAt(now);
                order.setStatus(status);
                break;
            case PICKING_COMPLETE:
                order.setPickCompletedAt(now);
                order.setStatus(status);
                break;
            case PACKING:
                order.setPackStartedAt(now);
                order.setStatus(status);
                break;
            case PACKING_COMPLETE:
                order.setPackCompletedAt(now);
                order.setStatus(status);
                break;
            case SHIPPED:
                order.setShippedAt(now);
                order.setStatus(status);
                break;
            case DELIVERED:
                order.setDeliveredAt(now);
                order.setStatus(status);
                break;
            case CANCELLED:
                order.setStatus(status);
                break;
            default:
                order.setStatus(status);
                break;
        }
        
        // Save the updated order
        FulfillmentOrder updatedOrder = fulfillmentOrderRepository.save(order);
        
        // Publish event
        publishFulfillmentOrderEvent(updatedOrder, "FULFILLMENT_ORDER_STATUS_UPDATED");
        
        return updatedOrder;
    }

    @Override
    @Transactional
    public FulfillmentOrder updateItemStatus(String fulfillmentOrderId, String itemId, ItemFulfillmentStatus status) {
        log.info("Updating item {} status to: {} in fulfillment order: {}", itemId, status, fulfillmentOrderId);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        // Convert UUID to Long for item ID
        
        
        // Find the item in the order
        FulfillmentOrderItem item = order.getOrderItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));
        
        // Update the item status
        item.setStatus(status);
        
        // Update timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        Long userId = 1L; // TODO: Get from security context
        
        switch (status) {
            case PICKED:
                item.setPickedAt(now);
                item.setPickedBy(userId);
                item.setQuantityPicked(item.getQuantity());
                break;
            case PACKED:
                item.setPackedAt(now);
                item.setPackedBy(userId);
                item.setQuantityPacked(item.getQuantity());
                break;
            case FULFILLED:
                item.setQuantityFulfilled(item.getQuantity());
                break;
            default:
                break;
        }
        
        // Check if all items have the same status and update order accordingly
        boolean allItemsSameStatus = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == status);
        
        if (allItemsSameStatus) {
            switch (status) {
                case PICKED:
                    updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.PICKING_COMPLETE);
                    break;
                case PACKED:
                    updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.PACKING_COMPLETE);
                    break;
                case FULFILLED:
                    updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.COMPLETED);
                    break;
                default:
                    break;
            }
        }
        
        return fulfillmentOrderRepository.save(order);
    }

    @Override
    @Transactional
    public int processPendingOrders() {
        log.info("Processing pending fulfillment orders");
        
        List<FulfillmentOrder> pendingOrders = fulfillmentOrderRepository.findPendingOrders();
        int processedCount = 0;
        
        for (FulfillmentOrder order : pendingOrders) {
            try {
                // For now, just move to PROCESSING status
                // In real implementation, would check inventory
                order.setStatus(FulfillmentStatus.PROCESSING);
                fulfillmentOrderRepository.save(order);
                processedCount++;
            } catch (Exception e) {
                log.error("Error processing fulfillment order {}: {}", order.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Processed {} out of {} pending fulfillment orders", processedCount, pendingOrders.size());
        return processedCount;
    }

    @Override
    @Transactional
    public FulfillmentOrder cancelFulfillmentOrder(String fulfillmentOrderId, String reason) {
        log.info("Cancelling fulfillment order: {} with reason: {}", fulfillmentOrderId, reason);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        order.setStatus(FulfillmentStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setInternalNotes(reason);
        
        // Update all items to cancelled status
        order.getOrderItems().forEach(item -> item.setStatus(ItemFulfillmentStatus.CANCELLED));
        
        // Save and publish event
        FulfillmentOrder cancelledOrder = fulfillmentOrderRepository.save(order);
        publishFulfillmentOrderEvent(cancelledOrder, "FULFILLMENT_ORDER_CANCELLED");
        
        return cancelledOrder;
    }

    @Override
    public List<FulfillmentOrder> getFulfillmentOrdersByStatus(FulfillmentStatus status) {
        log.debug("Getting fulfillment orders with status: {}", status);
        return fulfillmentOrderRepository.findByStatus(status);
    }

    @Override
    public List<FulfillmentOrder> getFulfillmentOrdersByWarehouse(Long warehouseId) {
        log.debug("Getting fulfillment orders for warehouse: {}", warehouseId);
        
        // Convert UUID to Long for warehouse ID
        
        
        return fulfillmentOrderRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public Map<String, Object> getFulfillmentStatistics(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting fulfillment statistics from {} to {}", startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<FulfillmentOrder> orders = fulfillmentOrderRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Count orders by status
        Map<FulfillmentStatus, Long> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(FulfillmentOrder::getStatus, Collectors.counting()));
        statistics.put("ordersByStatus", ordersByStatus);
        
        // Total orders
        statistics.put("totalOrders", orders.size());
        
        // Completed orders
        long completedOrders = orders.stream()
                .filter(FulfillmentOrder::isCompleted)
                .count();
        statistics.put("completedOrders", completedOrders);
        
        // Cancelled orders
        long cancelledOrders = orders.stream()
                .filter(o -> o.getStatus() == FulfillmentStatus.CANCELLED)
                .count();
        statistics.put("cancelledOrders", cancelledOrders);
        
        // Average processing time (in hours)
        double avgProcessingTime = orders.stream()
                .filter(o -> o.getShippedAt() != null && o.getCreatedAt() != null)
                .mapToLong(o -> java.time.Duration.between(o.getCreatedAt(), o.getShippedAt()).toHours())
                .average()
                .orElse(0.0);
        statistics.put("averageProcessingTimeHours", avgProcessingTime);
        
        return statistics;
    }

    @Override
    @Transactional
    public List<FulfillmentOrder> splitFulfillmentOrder(String fulfillmentOrderId) {
        log.info("Splitting fulfillment order: {}", fulfillmentOrderId);
        
        FulfillmentOrder originalOrder = getFulfillmentOrder(fulfillmentOrderId);
        
        // For now, just return the original order
        // In real implementation, would split based on warehouse availability
        return List.of(originalOrder);
    }

    @Override
    @Transactional
    public FulfillmentOrder assignToWarehouse(String fulfillmentOrderId, Long warehouseId) {
        log.info("Assigning fulfillment order {} to warehouse: {}", fulfillmentOrderId, warehouseId);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        // Convert UUID to Long for warehouse ID
        
        
        order.setWarehouseId(warehouseId);
        
        // Update status to processing if still in received state
        if (order.getStatus() == FulfillmentStatus.RECEIVED) {
            order.setStatus(FulfillmentStatus.PROCESSING);
        }
        
        // Save and publish event
        FulfillmentOrder updatedOrder = fulfillmentOrderRepository.save(order);
        publishFulfillmentOrderEvent(updatedOrder, "FULFILLMENT_ORDER_ASSIGNED");
        
        return updatedOrder;
    }
    
    @Override
    @Transactional
    public void proceedToNextFulfillmentStage(FulfillmentOrder fulfillmentOrder) {
        log.info("Proceeding to next fulfillment stage for order: {}", fulfillmentOrder.getId());
        
        // Move to next stage based on current status
        FulfillmentStatus currentStatus = fulfillmentOrder.getStatus();
        FulfillmentStatus nextStatus = getNextStatus(currentStatus);
        
        if (nextStatus != null && nextStatus != currentStatus) {
            fulfillmentOrder.setStatus(nextStatus);
            fulfillmentOrderRepository.save(fulfillmentOrder);
            
            // Publish event
            publishFulfillmentOrderEvent(fulfillmentOrder, "FULFILLMENT_STAGE_ADVANCED");
        }
    }
    
    @Override
    @Transactional
    public void handleInventoryCancellation(FulfillmentOrder fulfillmentOrder, String reason) {
        log.info("Handling inventory cancellation for order: {}", fulfillmentOrder.getId());
        
        fulfillmentOrder.setStatus(FulfillmentStatus.CANCELLED);
        fulfillmentOrder.setCancelledAt(LocalDateTime.now());
        fulfillmentOrder.setInternalNotes("Inventory cancelled: " + reason);
        
        fulfillmentOrderRepository.save(fulfillmentOrder);
        
        // Publish event
        publishFulfillmentOrderEvent(fulfillmentOrder, "FULFILLMENT_INVENTORY_CANCELLED");
    }
    
    @Override
    @Transactional
    public void handleInventoryExpiration(FulfillmentOrder fulfillmentOrder) {
        log.info("Handling inventory expiration for order: {}", fulfillmentOrder.getId());
        
        fulfillmentOrder.setStatus(FulfillmentStatus.PENDING_INVENTORY);
        fulfillmentOrder.setInternalNotes("Inventory reservation expired");
        
        fulfillmentOrderRepository.save(fulfillmentOrder);
        
        // Publish event
        publishFulfillmentOrderEvent(fulfillmentOrder, "FULFILLMENT_INVENTORY_EXPIRED");
    }
    
    /**
     * Get the next status in the fulfillment workflow
     */
    private FulfillmentStatus getNextStatus(FulfillmentStatus currentStatus) {
        switch (currentStatus) {
            case NEW:
            case PENDING:
                return FulfillmentStatus.RECEIVED;
            case RECEIVED:
                return FulfillmentStatus.PROCESSING;
            case PROCESSING:
                return FulfillmentStatus.ALLOCATED;
            case ALLOCATED:
                return FulfillmentStatus.READY_FOR_PICKING;
            case READY_FOR_PICKING:
                return FulfillmentStatus.PICKING;
            case PICKING:
                return FulfillmentStatus.PICKING_COMPLETE;
            case PICKING_COMPLETE:
                return FulfillmentStatus.PACKING;
            case PACKING:
                return FulfillmentStatus.PACKING_COMPLETE;
            case PACKING_COMPLETE:
                return FulfillmentStatus.COMPLETED;
            case COMPLETED:
                return FulfillmentStatus.SHIPPED;
            case SHIPPED:
                return FulfillmentStatus.DELIVERED;
            default:
                return null;
        }
    }
    
    /**
     * Publish a fulfillment order event to Kafka
     */
    private void publishFulfillmentOrderEvent(FulfillmentOrder order, String eventType) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("fulfillmentOrderId", order.getId());
        event.put("orderId", order.getExternalOrderId());
        event.put("status", order.getStatus());
        event.put("warehouseId", order.getWarehouseId());
        event.put("timestamp", LocalDateTime.now());
        
        kafkaTemplate.send("fulfillment-events", String.valueOf(order.getId()), event);
        log.debug("Published {} event for order {}", eventType, order.getId());
    }
}

