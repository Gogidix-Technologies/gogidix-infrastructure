package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.event.WarehouseEvent;
import com.exalt.warehousing.management.mapper.LocationMapper;
import com.exalt.warehousing.management.model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for publishing warehouse events to Kafka topics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final LocationMapper locationMapper;

    @Value("${kafka.topics.warehouse-events:warehouse-events}")
    private String warehouseEventsTopic;

    /**
     * Publish a location created event
     *
     * @param location the created location
     */
    public void publishLocationCreatedEvent(Location location) {
        LocationDTO locationDTO = locationMapper.toDTO(location);
        WarehouseEvent event = createWarehouseEvent(
                (UUID.fromString(location.getId())),
                (UUID.fromString(String.valueOf(location.getWarehouseId()))),
                (UUID.fromString(String.valueOf(location.getZoneId()))),
                WarehouseEvent.WarehouseEventType.LOCATION_CREATED,
                locationDTO
        );
        
        publishEvent(event, location.getId().toString());
    }

    /**
     * Publish a location updated event
     *
     * @param location the updated location
     */
    public void publishLocationUpdatedEvent(Location location) {
        LocationDTO locationDTO = locationMapper.toDTO(location);
        WarehouseEvent event = createWarehouseEvent(
                (UUID.fromString(location.getId())),
                (UUID.fromString(String.valueOf(location.getWarehouseId()))),
                (UUID.fromString(String.valueOf(location.getZoneId()))),
                WarehouseEvent.WarehouseEventType.LOCATION_UPDATED,
                locationDTO
        );
        
        publishEvent(event, location.getId().toString());
    }

    /**
     * Publish a location deleted event
     *
     * @param locationId the deleted location ID
     * @param warehouseId the warehouse ID
     * @param zoneId the zone ID
     */
    public void publishLocationDeletedEvent(UUID locationId, UUID warehouseId, UUID zoneId) {
        WarehouseEvent event = createWarehouseEvent(
                locationId,
                warehouseId,
                zoneId,
                WarehouseEvent.WarehouseEventType.LOCATION_DELETED,
                null
        );
        
        publishEvent(event, locationId.toString());
    }

    /**
     * Create a warehouse event
     *
     * @param locationId the location ID
     * @param warehouseId the warehouse ID
     * @param zoneId the zone ID
     * @param eventType the event type
     * @param locationDTO the location DTO (can be null for deletion events)
     * @return the warehouse event
     */
    private WarehouseEvent createWarehouseEvent(
            UUID locationId,
            UUID warehouseId,
            UUID zoneId,
            WarehouseEvent.WarehouseEventType eventType,
            LocationDTO locationDTO) {
        
        return WarehouseEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType.name())
                .timestamp(LocalDateTime.now())
                .locationId(locationId)
                .warehouseId(warehouseId)
                .zoneId(zoneId)
                .type(eventType)
                .location(locationDTO)
                .build();
    }

    /**
     * Publish an event to the warehouse events topic
     *
     * @param event the event to publish
     * @param key the message key
     */
    private void publishEvent(WarehouseEvent event, String key) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(warehouseEventsTopic, key, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published warehouse event: type={}, locationId={}, timestamp={}",
                            event.getType(), event.getLocationId(), event.getTimestamp());
                } else {
                    log.error("Failed to publish warehouse event: type={}, locationId={}, error={}",
                            event.getType(), event.getLocationId(), ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error while publishing warehouse event: {}", e.getMessage(), e);
        }
    }
} 
