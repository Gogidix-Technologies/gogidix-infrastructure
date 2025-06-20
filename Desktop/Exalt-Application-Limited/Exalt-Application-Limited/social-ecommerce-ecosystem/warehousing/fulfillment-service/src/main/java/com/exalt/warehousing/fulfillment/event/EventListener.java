package com.exalt.warehousing.fulfillment.event;

import com.exalt.warehousing.fulfillment.service.FulfillmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Listener for consuming Kafka events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {
    
    private final FulfillmentService fulfillmentService;
    
    /**
     * Listen for inventory events
     *
     * @param event the inventory event
     * @param ack the acknowledgment
     */
    @KafkaListener(topics = "inventory-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeInventoryEvent(InventoryEvent event, Acknowledgment ack) {
        try {
            log.info("Received inventory event: {}", event);
            
            // Process inventory event based on the event type
            switch (event.getEventType()) {
                case "INVENTORY_UPDATED":
                    // Check if any active orders need to be updated based on new inventory
                    fulfillmentService.checkActiveOrdersForItem(event.getSku(), event.getWarehouseId());
                    break;
                case "INVENTORY_RESERVED":
                    // Update any affected fulfillment orders
                    log.debug("Inventory reserved: {} units of {} in warehouse {}", 
                            event.getReservedQuantity(), event.getSku(), event.getWarehouseId());
                    break;
                case "INVENTORY_ALLOCATED":
                    // Update any affected fulfillment orders
                    log.debug("Inventory allocated: {} units of {} in warehouse {}", 
                            event.getAllocatedQuantity(), event.getSku(), event.getWarehouseId());
                    break;
                case "LOW_STOCK_ALERT":
                    // Handle low stock alerts
                    log.warn("Low stock alert: {} units of {} in warehouse {}", 
                            event.getAvailableQuantity(), event.getSku(), event.getWarehouseId());
                    fulfillmentService.handleLowStockAlert(event.getSku(), event.getWarehouseId());
                    break;
                default:
                    log.debug("Unhandled inventory event type: {}", event.getEventType());
            }
            
            // Acknowledge the message
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing inventory event: {}", e.getMessage(), e);
            // We don't acknowledge, which will cause the message to be redelivered
        }
    }
    
    /**
     * Listen for warehouse events
     *
     * @param event the warehouse event
     * @param ack the acknowledgment
     */
    @KafkaListener(topics = "warehouse-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeWarehouseEvent(WarehouseEvent event, Acknowledgment ack) {
        try {
            log.info("Received warehouse event: {}", event);
            
            // Process warehouse event based on the event type
            switch (event.getEventType()) {
                case "WAREHOUSE_DISABLED":
                    // Reassign orders from disabled warehouse
                    fulfillmentService.reassignOrdersFromWarehouse(event.getWarehouseId());
                    break;
                case "ZONE_UNAVAILABLE":
                    // Handle zone becoming unavailable
                    log.info("Zone {} in warehouse {} is now unavailable", 
                            event.getZoneId(), event.getWarehouseId());
                    break;
                default:
                    log.debug("Unhandled warehouse event type: {}", event.getEventType());
            }
            
            // Acknowledge the message
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing warehouse event: {}", e.getMessage(), e);
            // We don't acknowledge, which will cause the message to be redelivered
        }
    }
} 
