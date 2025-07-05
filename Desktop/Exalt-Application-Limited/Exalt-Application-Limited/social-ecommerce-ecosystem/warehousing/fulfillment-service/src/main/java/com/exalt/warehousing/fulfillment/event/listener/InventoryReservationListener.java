package com.exalt.warehousing.fulfillment.event.listener;

import com.exalt.warehousing.shared.events.*;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.util.TypeConverterUtil;
import com.exalt.warehousing.fulfillment.enums.InventoryStatus;
import com.exalt.warehousing.fulfillment.repository.FulfillmentOrderRepository;
import com.exalt.warehousing.fulfillment.service.FulfillmentOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event listener for inventory reservation events.
 * This component consumes events from the inventory service and updates
 * fulfillment orders accordingly.
 */
@Component
public class InventoryReservationListener {
    private static final Logger log = LoggerFactory.getLogger(InventoryReservationListener.class);
    
    private final FulfillmentOrderRepository orderRepository;
    private final FulfillmentOrderService orderService;
    
    @Autowired
    public InventoryReservationListener(
            FulfillmentOrderRepository orderRepository,
            FulfillmentOrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }
    
    /**
     * Handles inventory reservation created events.
     * 
     * @param event The event data
     */
    @KafkaListener(topics = "${events.topic.reservation.created:inventory.reservations.created}")
    @Transactional
    public void handleReservationCreated(InventoryReservationCreatedEvent event) {
        log.info("Received reservation created event: {}", event.getReservationId());
        
        UUID orderId = event.getOrderId();
        if (orderId == null) {
            log.warn("Received reservation event with null orderId: {}", event.getReservationId());
            return;
        }
        
        FulfillmentOrder order = orderRepository.findByOrderIdString(orderId.toString()).orElse(null);
        
        if (order == null) {
            log.warn("No fulfillment order found for orderId: {}", orderId);
            return;
        }
        
        // Update inventory status to RESERVED
        order.setInventoryStatus(InventoryStatus.RESERVED);
        order.setInventoryReservationId(event.getReservationId());
        order.setInventoryReservationExpiresAt(event.getExpiresAt());
        
        // Add reservation details to notes
        Map<String, Object> reservationDetails = new HashMap<>();
        reservationDetails.put("reservationId", event.getReservationId());
        reservationDetails.put("expiresAt", event.getExpiresAt());
        reservationDetails.put("totalItems", event.getItems().size());
        
        order.addNote("Inventory reservation created: " + reservationDetails);
        orderRepository.save(order);
        
        log.info("Updated fulfillment order {} with reservation {}", orderId, event.getReservationId());
    }
    
    /**
     * Handles inventory reservation status changed events.
     * 
     * @param event The event data
     */
    @KafkaListener(topics = "${events.topic.reservation.status-changed:inventory.reservations.status-changed}")
    @Transactional
    public void handleReservationStatusChanged(InventoryReservationStatusChangedEvent event) {
        log.info("Received reservation status changed event: {} -> {}", 
                event.getPreviousStatus(), event.getNewStatus());
        
        UUID orderId = event.getOrderId();
        if (orderId == null) {
            log.warn("Received status change event with null orderId: {}", event.getReservationId());
            return;
        }
        
        FulfillmentOrder order = orderRepository.findByOrderIdString(orderId.toString()).orElse(null);
        
        if (order == null) {
            log.warn("No fulfillment order found for orderId: {}", orderId);
            return;
        }
        
        // Update inventory status based on the new reservation status
        switch (event.getNewStatus()) {
            case CONFIRMED:
                order.setInventoryStatus(InventoryStatus.CONFIRMED);
                break;
            case FULFILLED:
                order.setInventoryStatus(InventoryStatus.ALLOCATED);
                break;
            case CANCELLED:
                order.setInventoryStatus(InventoryStatus.CANCELLED);
                break;
            case EXPIRED:
                order.setInventoryStatus(InventoryStatus.EXPIRED);
                break;
            default:
                log.warn("Unhandled reservation status: {}", event.getNewStatus());
        }
        
        order.addNote("Inventory reservation status changed: " + 
                event.getPreviousStatus() + " -> " + event.getNewStatus() + 
                " (" + event.getReason() + ")");
        
        orderRepository.save(order);
        log.info("Updated fulfillment order {} inventory status to {}", 
                orderId, order.getInventoryStatus());
    }
    
    /**
     * Handles inventory reservation completed events.
     * 
     * @param event The event data
     */
    @KafkaListener(topics = "${events.topic.reservation.completed:inventory.reservations.completed}")
    @Transactional
    public void handleReservationCompleted(InventoryReservationCompletedEvent event) {
        log.info("Received reservation completed event: {}", event.getReservationId());
        
        String orderId = event.getOrderId();
        if (orderId == null) {
            log.warn("Received completion event with null orderId: {}", event.getReservationId());
            return;
        }
        
        FulfillmentOrder order = orderRepository.findByOrderIdString(orderId).orElse(null);
        
        if (order == null) {
            log.warn("No fulfillment order found for orderId: {}", orderId);
            return;
        }
        
        // Update inventory status to ALLOCATED
        order.setInventoryStatus(InventoryStatus.ALLOCATED);
        order.addNote("Inventory reservation completed at " + event.getCompletedAt());
        
        orderRepository.save(order);
        
        // Proceed with next steps in the fulfillment process if needed
        orderService.proceedToNextFulfillmentStage(order);
        
        log.info("Completed inventory allocation for fulfillment order {}", orderId);
    }
    
    /**
     * Handles inventory reservation cancelled events.
     * 
     * @param event The event data
     */
    @KafkaListener(topics = "${events.topic.reservation.cancelled:inventory.reservations.cancelled}")
    @Transactional
    public void handleReservationCancelled(InventoryReservationCancelledEvent event) {
        log.info("Received reservation cancelled event: {}", event.getReservationId());
        
        String orderId = event.getOrderId();
        if (orderId == null) {
            log.warn("Received cancellation event with null orderId: {}", event.getReservationId());
            return;
        }
        
        FulfillmentOrder order = orderRepository.findByOrderIdString(orderId).orElse(null);
        
        if (order == null) {
            log.warn("No fulfillment order found for orderId: {}", orderId);
            return;
        }
        
        // Update inventory status to CANCELLED
        order.setInventoryStatus(InventoryStatus.CANCELLED);
        order.addNote("Inventory reservation cancelled");
        
        orderRepository.save(order);
        
        // Handle cancellation in the order service
        orderService.handleInventoryCancellation(order, "Inventory reservation cancelled");
        
        log.info("Handled inventory cancellation for fulfillment order {}", orderId);
    }
    
    /**
     * Handles inventory reservation expired events.
     * 
     * @param event The event data
     */
    @KafkaListener(topics = "${events.topic.reservation.expired:inventory.reservations.expired}")
    @Transactional
    public void handleReservationExpired(InventoryReservationExpiredEvent event) {
        log.info("Received reservation expired event: {}", event.getReservationId());
        
        String orderId = event.getOrderId();
        if (orderId == null) {
            log.warn("Received expiration event with null orderId: {}", event.getReservationId());
            return;
        }
        
        FulfillmentOrder order = orderRepository.findByOrderIdString(orderId).orElse(null);
        
        if (order == null) {
            log.warn("No fulfillment order found for orderId: {}", orderId);
            return;
        }
        
        // Update inventory status to EXPIRED
        order.setInventoryStatus(InventoryStatus.EXPIRED);
        order.addNote("Inventory reservation expired at " + event.getExpirationTime());
        
        orderRepository.save(order);
        
        // Handle expiration in the order service
        orderService.handleInventoryExpiration(order);
        
        log.info("Handled inventory expiration for fulfillment order {}", orderId);
    }
}
