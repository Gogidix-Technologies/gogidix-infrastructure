package com.exalt.warehousing.inventory.event;

import com.exalt.warehousing.shared.events.BaseEvent;
import com.exalt.warehousing.shared.events.*;
import com.exalt.warehousing.inventory.messaging.MessageQueueProducer;
import com.exalt.warehousing.inventory.model.InventoryReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Publisher for inventory-related events.
 * This component is responsible for publishing events to the message queue
 * when inventory operations occur.
 */
@Component
public class InventoryEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);
    
    // Service name used as the source in events
    @Value("${spring.application.name:inventory-service}")
    private String serviceName;
    
    // Topic names for different event types
    @Value("${events.topic.reservation.created:inventory.reservations.created}")
    private String reservationCreatedTopic;
    
    @Value("${events.topic.reservation.status-changed:inventory.reservations.status-changed}")
    private String reservationStatusChangedTopic;
    
    @Value("${events.topic.reservation.completed:inventory.reservations.completed}")
    private String reservationCompletedTopic;
    
    @Value("${events.topic.reservation.cancelled:inventory.reservations.cancelled}")
    private String reservationCancelledTopic;
    
    @Value("${events.topic.reservation.expired:inventory.reservations.expired}")
    private String reservationExpiredTopic;
    
    // Message queue producer
    private final MessageQueueProducer producer;
    
    @Autowired
    public InventoryEventPublisher(MessageQueueProducer producer) {
        this.producer = producer;
    }
    
    /**
     * Publishes a reservation created event.
     *
     * @param reservation The reservation that was created
     */
    public void publishReservationCreated(InventoryReservation reservation) {
        InventoryReservationCreatedEvent event = EventMapper.toReservationCreatedEvent(reservation);
        publish(reservationCreatedTopic, event);
    }
    
    /**
     * Publishes a reservation status changed event.
     *
     * @param reservation    The reservation with updated status
     * @param previousStatus The previous status
     * @param reason         The reason for the status change
     */
    public void publishReservationStatusChanged(
            InventoryReservation reservation, 
            com.exalt.warehousing.inventory.model.ReservationStatus previousStatus,
            String reason) {
        InventoryReservationStatusChangedEvent event = 
                EventMapper.toStatusChangedEvent(reservation, previousStatus, reason);
        publish(reservationStatusChangedTopic, event);
    }
    
    /**
     * Publishes a reservation completed event.
     *
     * @param reservation The reservation that was completed
     */
    public void publishReservationCompleted(InventoryReservation reservation) {
        InventoryReservationCompletedEvent event = EventMapper.toCompletedEvent(reservation);
        publish(reservationCompletedTopic, event);
    }
    
    /**
     * Publishes a reservation cancelled event.
     *
     * @param reservation The reservation that was cancelled
     * @param reason      The reason for cancellation
     */
    public void publishReservationCancelled(InventoryReservation reservation, String reason) {
        InventoryReservationCancelledEvent event = 
                EventMapper.toCancelledEvent(reservation, reason);
        publish(reservationCancelledTopic, event);
    }
    
    /**
     * Publishes a reservation expired event.
     *
     * @param reservation The reservation that expired
     * @param reason      The reason for expiration
     */
    public void publishReservationExpired(InventoryReservation reservation, String reason) {
        InventoryReservationExpiredEvent event = 
                EventMapper.toExpiredEvent(reservation, reason);
        publish(reservationExpiredTopic, event);
    }
    
    /**
     * Generic method to publish any event to the specified topic.
     *
     * @param topic The topic to publish to
     * @param event The event to publish
     */
    private void publish(String topic, BaseEvent event) {
        try {
            producer.send(topic, event);
            log.debug("Published event {} to topic {}", 
                    event.getClass().getSimpleName(), topic);
        } catch (Exception e) {
            log.error("Failed to publish event {} to topic {}: {}", 
                    event.getClass().getSimpleName(), topic, e.getMessage(), e);
        }
    }
}
