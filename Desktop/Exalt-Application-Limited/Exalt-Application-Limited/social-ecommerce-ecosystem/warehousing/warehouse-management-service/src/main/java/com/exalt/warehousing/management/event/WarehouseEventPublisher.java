package com.exalt.warehousing.management.event;

import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.model.Zone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Component responsible for publishing warehouse events to Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WarehouseEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topics.warehouse-events}")
    private String warehouseEventsTopic;
    
    @Value("${kafka.topics.task-events}")
    private String taskEventsTopic;
    
    @Value("${kafka.topics.location-events}")
    private String locationEventsTopic;
    
    /**
     * Publish a warehouse task event
     *
     * @param task the task
     * @param eventType the event type
     */
    public void publishTaskEvent(WarehouseTask task, EventType eventType) {
        TaskEvent event = new TaskEvent(
                UUID.randomUUID(),
                eventType,
                task.getId(),
                task.getType(),
                task.getStatus(),
                task.getAssignedTo(),
                task.getPriority(),
                LocalDateTime.now()
        );
        
        sendMessage(taskEventsTopic, task.getId().toString(), event);
        
        log.info("Published task event: {} for task: {}", eventType, task.getId());
    }
    
    /**
     * Publish a location event
     *
     * @param location the location
     * @param eventType the event type
     */
    public void publishLocationEvent(Location location, EventType eventType) {
        LocationEvent event = new LocationEvent(
                UUID.randomUUID(),
                eventType,
                (UUID.fromString(location.getId())),
                location.getZoneIdAsUUID(),
                location.getWarehouseIdAsUUID(),
                location.getStatus(),
                LocalDateTime.now()
        );
        
        sendMessage(locationEventsTopic, location.getId().toString(), event);
        
        log.info("Published location event: {} for location: {}", eventType, location.getId());
    }
    
    /**
     * Publish a zone event
     *
     * @param zone the zone
     * @param eventType the event type
     */
    public void publishZoneEvent(Zone zone, EventType eventType) {
        ZoneEvent event = new ZoneEvent(
                UUID.randomUUID(),
                eventType,
                (UUID.fromString(zone.getId())),
                zone.getWarehouseIdAsUUID(),
                zone.getType(),
                zone.getCode(),
                LocalDateTime.now()
        );
        
        sendMessage(warehouseEventsTopic, zone.getId().toString(), event);
        
        log.info("Published zone event: {} for zone: {}", eventType, zone.getId());
    }
    
    /**
     * Send a message to a Kafka topic
     *
     * @param topic the topic
     * @param key the message key
     * @param value the message value
     */
    private void sendMessage(String topic, String key, Object value) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, value);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Message sent to topic {}, partition: {}, offset: {}",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message to topic {}: {}", topic, ex.getMessage());
            }
        });
    }
} 
