package com.exalt.warehousing.management.mapper;

import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.Warehouse;
import com.exalt.warehousing.management.model.Zone;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Location entities and DTOs
 */
@Component
public class LocationMapper {

    /**
     * Convert a Location entity to a LocationDTO
     *
     * @param location the location entity
     * @return the location DTO
     */
    public LocationDTO toDTO(Location location) {
        if (location == null) {
            return null;
        }

        return LocationDTO.builder()
                .id(UUID.fromString(location.getId()))
                .warehouseId(location.getWarehouseIdAsUUID())
                .zoneId(location.getZoneIdAsUUID())
                .locationCode(location.getLocationCode())
                .description(location.getDescription())
                .locationName(location.getName())
                .aisle(location.getAisle())
                .rack(location.getRack())
                .level(location.getLevel())
                .position(location.getPosition())
                .barcode(location.getBarcode())
                .maxWeightKg(location.getMaxWeight())
                .maxVolumeCubicMeters(location.getMaxVolume())
                .heightCm(location.getMaxHeight())
                .widthCm(location.getMaxWidth())
                .depthCm(location.getMaxDepth())
                .status(location.getStatus() != null ? location.getStatus().name() : null)
                .notes(location.getNotes())
                .metadata(convertStringMapToObjectMap(location.getMetadata()))
                .properties(convertStringMapToObjectMap(location.getProperties()))
                .maxItems(location.getCapacity())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    /**
     * Convert a LocationDTO to a Location entity
     *
     * @param locationDTO the location DTO
     * @return the location entity
     */
    public Location toEntity(LocationDTO locationDTO) {
        if (locationDTO == null) {
            return null;
        }

        Location location = new Location();
        location.setId(locationDTO.getId() != null ? locationDTO.getId().toString() : null);
        
        // Set warehouse reference if warehouseId is provided
        if (locationDTO.getWarehouseId() != null) {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(locationDTO.getWarehouseId().toString());
            location.setWarehouse(warehouse);
        }
        
        // Set zone reference if zoneId is provided
        if (locationDTO.getZoneId() != null) {
            Zone zone = new Zone();
            zone.setId(locationDTO.getZoneId().toString());
            location.setZone(zone);
        }
        location.setLocationCode(locationDTO.getLocationCode());
        location.setDescription(locationDTO.getDescription());
        location.setName(locationDTO.getLocationName());
        location.setAisle(locationDTO.getAisle());
        location.setRack(locationDTO.getRack());
        location.setLevel(locationDTO.getLevel());
        location.setPosition(locationDTO.getPosition());
        location.setBarcode(locationDTO.getBarcode());
        location.setMaxWeight(locationDTO.getMaxWeightKg());
        location.setMaxVolume(locationDTO.getMaxVolumeCubicMeters());
        location.setMaxHeight(locationDTO.getHeightCm());
        location.setMaxWidth(locationDTO.getWidthCm());
        location.setMaxDepth(locationDTO.getDepthCm());
        location.setStatus(locationDTO.getStatus() != null ? 
                LocationStatus.valueOf(locationDTO.getStatus()) : null);
        location.setNotes(locationDTO.getNotes());
        
        // Convert Map<String, Object> to Map<String, String>
        if (locationDTO.getMetadata() != null) {
            Map<String, String> metadata = new java.util.HashMap<>();
            locationDTO.getMetadata().forEach((key, value) -> 
                metadata.put(key, value != null ? value.toString() : null));
            location.setMetadata(metadata);
        } else {
            location.setMetadata(new java.util.HashMap<>());
        }
        
        if (locationDTO.getProperties() != null) {
            Map<String, String> properties = new java.util.HashMap<>();
            locationDTO.getProperties().forEach((key, value) -> 
                properties.put(key, value != null ? value.toString() : null));
            location.setProperties(properties);
        } else {
            location.setProperties(new java.util.HashMap<>());
        }
        location.setCapacity(locationDTO.getMaxItems());
        location.setCreatedAt(locationDTO.getCreatedAt());
        location.setUpdatedAt(locationDTO.getUpdatedAt());
        
        return location;
    }

    /**
     * Convert a list of Location entities to a list of LocationDTOs
     *
     * @param locations the list of location entities
     * @return the list of location DTOs
     */
    public List<LocationDTO> toDTOList(List<Location> locations) {
        if (locations == null) {
            return null;
        }
        
        return locations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of LocationDTOs to a list of Location entities
     *
     * @param locationDTOs the list of location DTOs
     * @return the list of location entities
     */
    public List<Location> toEntityList(List<LocationDTO> locationDTOs) {
        if (locationDTOs == null) {
            return null;
        }
        
        return locationDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update a Location entity from a LocationDTO
     *
     * @param existingLocation the existing location entity
     * @param locationDTO the location DTO with updated values
     * @return the updated location entity
     */
    public Location updateEntityFromDTO(Location existingLocation, LocationDTO locationDTO) {
        if (existingLocation == null || locationDTO == null) {
            return existingLocation;
        }
        
        // Only update non-null fields from the DTO
        if (locationDTO.getLocationCode() != null) {
            existingLocation.setLocationCode(locationDTO.getLocationCode());
        }
        if (locationDTO.getDescription() != null) {
            existingLocation.setDescription(locationDTO.getDescription());
        }
        if (locationDTO.getLocationName() != null) {
            existingLocation.setName(locationDTO.getLocationName());
        }
        if (locationDTO.getAisle() != null) {
            existingLocation.setAisle(locationDTO.getAisle());
        }
        if (locationDTO.getRack() != null) {
            existingLocation.setRack(locationDTO.getRack());
        }
        if (locationDTO.getLevel() != null) {
            existingLocation.setLevel(locationDTO.getLevel());
        }
        if (locationDTO.getPosition() != null) {
            existingLocation.setPosition(locationDTO.getPosition());
        }
        if (locationDTO.getBarcode() != null) {
            existingLocation.setBarcode(locationDTO.getBarcode());
        }
        if (locationDTO.getMaxWeightKg() != null) {
            existingLocation.setMaxWeight(locationDTO.getMaxWeightKg());
        }
        if (locationDTO.getMaxVolumeCubicMeters() != null) {
            existingLocation.setMaxVolume(locationDTO.getMaxVolumeCubicMeters());
        }
        if (locationDTO.getHeightCm() != null) {
            existingLocation.setMaxHeight(locationDTO.getHeightCm());
        }
        if (locationDTO.getWidthCm() != null) {
            existingLocation.setMaxWidth(locationDTO.getWidthCm());
        }
        if (locationDTO.getDepthCm() != null) {
            existingLocation.setMaxDepth(locationDTO.getDepthCm());
        }
        if (locationDTO.getStatus() != null) {
            existingLocation.setStatus(LocationStatus.valueOf(locationDTO.getStatus()));
        }
        if (locationDTO.getNotes() != null) {
            existingLocation.setNotes(locationDTO.getNotes());
        }
        if (locationDTO.getMetadata() != null) {
            Map<String, String> metadata = new HashMap<>();
            locationDTO.getMetadata().forEach((key, value) -> 
                metadata.put(key, value != null ? value.toString() : null));
            existingLocation.setMetadata(metadata);
        }
        if (locationDTO.getProperties() != null) {
            Map<String, String> properties = new HashMap<>();
            locationDTO.getProperties().forEach((key, value) -> 
                properties.put(key, value != null ? value.toString() : null));
            existingLocation.setProperties(properties);
        }
        if (locationDTO.getMaxItems() != null) {
            existingLocation.setCapacity(locationDTO.getMaxItems());
        }
        
        return existingLocation;
    }
    
    /**
     * Convert Map<String, String> to Map<String, Object>
     */
    private Map<String, Object> convertStringMapToObjectMap(Map<String, String> stringMap) {
        if (stringMap == null) {
            return null;
        }
        Map<String, Object> objectMap = new HashMap<>();
        stringMap.forEach((key, value) -> objectMap.put(key, value));
        return objectMap;
    }
} 
