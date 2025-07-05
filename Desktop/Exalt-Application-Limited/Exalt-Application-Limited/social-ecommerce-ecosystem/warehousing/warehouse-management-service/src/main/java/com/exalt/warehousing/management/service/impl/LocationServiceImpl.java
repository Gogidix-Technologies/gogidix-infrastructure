package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.shared.common.BaseEntityService;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.exception.DuplicateResourceException;
import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the LocationService interface
 */
@Service
@Slf4j
public class LocationServiceImpl extends BaseEntityService<Location, String> implements LocationService {

    private final LocationRepository locationRepository;
    private final ZoneRepository zoneRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, ZoneRepository zoneRepository) {
        this.locationRepository = locationRepository;
        this.zoneRepository = zoneRepository;
    }

    @Override
    protected JpaRepository<Location, String> getRepository() {
        return locationRepository;
    }

    /**
     * Get location by ID, converting UUID to String for repository call
     * 
     * @param id the location ID
     * @return the location
     * @throws ResourceNotFoundException if location not found
     */
    private Location getById(UUID id) {
        String entityId = id.toString();
        return locationRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));
    }

    @Override
    @Transactional
    public Location createLocation(Location location) {
        // Validate zone exists
        if (location.getZoneId() == null) {
            throw new IllegalArgumentException("Zone ID must be provided for location");
        }
        Zone zone = zoneRepository.findById((UUID.fromString(String.valueOf(location.getZoneId()))))
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", location.getZoneId()));

        // Validate location code uniqueness within zone
        if (locationRepository.findByLocationCodeAndZoneId(location.getLocationCode(), location.getZoneId()).isPresent()) {
            throw new DuplicateResourceException("Location with code " + location.getLocationCode() + 
                    " already exists in zone with id " + location.getZoneId());
        }

        // Set default values if not provided
        if (location.getStatus() == null) {
            location.setStatus(LocationStatus.AVAILABLE);
        }

        return save(location);
    }

    @Override
    @Transactional
    public List<Location> createLocations(List<Location> locations) {
        // Validate all locations
        for (Location location : locations) {
            // Validate zone exists
            if (location.getZoneId() == null) {
                throw new IllegalArgumentException("Zone ID must be provided for location");
            }
            Zone zone = zoneRepository.findById((UUID.fromString(String.valueOf(location.getZoneId()))))
                    .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", location.getZoneId()));

            // Set default values if not provided
            if (location.getStatus() == null) {
                location.setStatus(LocationStatus.AVAILABLE);
            }
        }

        // Check for duplicate location codes within each zone
        // Group locations by zone id using String to ensure correct grouping with String keys
        var locationsByZoneId = locations.stream()
                .collect(Collectors.groupingBy(Location::getZoneId));
        
        // Check each zone group for duplicates
        for (var entry : locationsByZoneId.entrySet()) {
            String zoneId = entry.getKey();
            List<Location> zoneLocations = entry.getValue();
            
            // Get all existing location codes in this zone
            List<String> existingCodes = locationRepository.findByZoneId(zoneId).stream()
                    .map(Location::getLocationCode)
                    .collect(Collectors.toList());
            
            // Check each new location code against existing ones
            for (Location location : zoneLocations) {
                if (existingCodes.contains(location.getLocationCode())) {
                    throw new DuplicateResourceException("Location with code " + location.getLocationCode() + 
                            " already exists in zone with id " + zoneId);
                }
                existingCodes.add(location.getLocationCode()); // Add to prevent duplicates in the batch
            }
        }

        return locationRepository.saveAll(locations);
    }

    @Override
    @Transactional
    public Location updateLocation(UUID id, Location location) {
        Location existingLocation = getById(id);

        // Don't allow changing the zone
        if (!existingLocation.getZoneId().equals(location.getZoneId())) {
            throw new IllegalArgumentException("Cannot change the zone of a location");
        }

        // Check if code is being changed and if the new code already exists in the zone
        if (!existingLocation.getLocationCode().equals(location.getLocationCode()) &&
                locationRepository.findByLocationCodeAndZoneId(location.getLocationCode(), location.getZoneId()).isPresent()) {
            throw new DuplicateResourceException("Location with code " + location.getLocationCode() + 
                    " already exists in zone with id " + location.getZoneId());
        }

        // Update entity fields using the abstract method from BaseEntityService
        updateEntityFields(existingLocation, location);

        // Don't update status through this method
        // Status should be changed through updateLocationStatus method
        existingLocation.setStatus(existingLocation.getStatus());

        return save(existingLocation);
    }

    protected void updateEntityFields(Location existingLocation, Location updatedLocation) {
        existingLocation.setLocationCode(updatedLocation.getLocationCode());
        existingLocation.setBarcode(updatedLocation.getBarcode());
        existingLocation.setDescription(updatedLocation.getDescription());
        existingLocation.setAisle(updatedLocation.getAisle());
        existingLocation.setRack(updatedLocation.getRack());
        existingLocation.setLevel(updatedLocation.getLevel());
        existingLocation.setPosition(updatedLocation.getPosition());
        existingLocation.setDimensions(updatedLocation.getDimensions());
        existingLocation.setCapacity(updatedLocation.getCapacity());
        existingLocation.setProperties(updatedLocation.getProperties());
    }

    @Override
    public Optional<Location> getLocation(UUID id) {
        String entityId = id.toString();
        return locationRepository.findById(entityId);
    }

    @Override
    public Optional<Location> getLocationByCodeAndZoneId(String locationCode, UUID zoneId) {
        String zoneEntityId = zoneId.toString();
        return locationRepository.findByLocationCodeAndZoneId(locationCode, zoneEntityId);
    }

    @Override
    public List<Location> getLocationsByZoneId(UUID zoneId) {
        String zoneEntityId = zoneId.toString();
        return locationRepository.findByZoneId(zoneEntityId);
    }

    @Override
    public List<Location> getLocationsByStatus(LocationStatus status) {
        return locationRepository.findByStatus(status);
    }

    @Override
    public List<Location> getLocationsByZoneIdAndStatus(UUID zoneId, LocationStatus status) {
        String zoneEntityId = zoneId.toString();
        return locationRepository.findByZoneIdAndStatus(zoneEntityId, status);
    }

    @Override
    public List<Location> getLocationsByWarehouseId(UUID warehouseId) {
        String warehouseEntityId = warehouseId.toString();
        return locationRepository.findByWarehouseId(warehouseEntityId);
    }

    @Override
    public Optional<Location> getLocationByBarcode(String barcode) {
        return locationRepository.findByBarcode(barcode);
    }

    @Override
    public long countLocationsByZoneId(UUID zoneId) {
        String zoneEntityId = zoneId.toString();
        return locationRepository.countByZoneId(zoneEntityId);
    }

    @Override
    public long countLocationsByWarehouseIdAndStatus(UUID warehouseId, LocationStatus status) {
        String warehouseEntityId = warehouseId.toString();
        return locationRepository.countByWarehouseIdAndStatus(warehouseEntityId, status);
    }

    @Override
    @Transactional
    public Location updateLocationStatus(UUID id, LocationStatus status) {
        Location location = getById(id);
        location.setStatus(status);
        return save(location);
    }

    @Override
    @Transactional
    public void deleteLocation(UUID id) {
        deleteById(id.toString());
    }

    /**
     * Create a grid of locations in a zone
     *
     * @param zoneId the zone ID
     * @param rows the number of rows
     * @param columns the number of columns
     * @return the created locations
     */
    public List<Location> createLocationsInZoneWithGrid(UUID zoneId, int rows, int columns) {
        log.debug("Creating grid of locations in zone: {} with {}x{} grid", zoneId, rows, columns);
        
        // Find the zone
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", zoneId));
        
        List<Location> locations = new ArrayList<>();
        
        // Generate a grid of locations
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= columns; col++) {
                String locationCode = String.format("%s-R%dC%d", zone.getCode(), row, col);
                String position = String.format("R%dC%d", row, col);
                
                Location location = Location.builder()
                        .zone(zone)
                        .warehouse(zone.getWarehouse())
                        .locationCode(locationCode)
                        .aisle(String.valueOf(row))
                        .rack("R" + row)
                        .level("L1")
                        .position(position)
                        .barcode(generateBarcode(zone.getCode(), row, col))
                        .status(LocationStatus.AVAILABLE)
                        .build();
                
                locations.add(location);
            }
        }
        
        return locationRepository.saveAll(locations);
    }
    
    /**
     * Helper method to generate a barcode for a location
     */
    private String generateBarcode(String zoneCode, int row, int col) {
        return String.format("%s-LOC-%d%d-%s", 
                zoneCode, 
                row, 
                col, 
                UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * Generate location barcodes for a zone
     *
     * @param zone the zone
     * @param count the number of locations to generate
     * @return list of generated location barcodes
     */
    @Override
    public List<String> generateLocationBarcodes(Zone zone, int count) {
        log.debug("Generating {} location barcodes for zone: {}", count, zone.getCode());
        
        List<String> barcodes = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            String barcode = zone.getCode() + "-" + 
                    String.format("%04d", i) + "-" + 
                    UUID.randomUUID().toString().substring(0, 8);
            barcodes.add(barcode);
        }
        
        return barcodes;
    }
    
    /**
     * Get total number of locations by status in a warehouse
     *
     * @param warehouseId the warehouse ID
     * @return map of status to count
     */
    @Override
    public Map<LocationStatus, Long> getLocationCountByStatus(UUID warehouseId) {
        log.debug("Getting location count by status for warehouse: {}", warehouseId);
        
        String warehouseEntityId = warehouseId.toString();
        List<Location> locations = locationRepository.findByWarehouseId(warehouseEntityId);
        
        return locations.stream()
                .collect(Collectors.groupingBy(
                        Location::getStatus,
                        Collectors.counting()
                ));
    }

    @Override
    public LocationDTO findLocationByBarcode(String barcode) {
        log.info("Finding location by barcode: {}", barcode);
        
        Location location = locationRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with barcode: " + barcode));
        
        return toLocationDTO(location);
    }

    @Override
    public List<LocationDTO> findLocationsByWarehouseId(UUID warehouseId) {
        log.info("Finding locations by warehouse ID: {}", warehouseId);
        
        String warehouseEntityId = warehouseId.toString();
        List<Location> locations = locationRepository.findByWarehouseId(warehouseEntityId);
        return locations.stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Location entity to LocationDTO
     */
    private LocationDTO toLocationDTO(Location location) {
        return LocationDTO.builder()
                .id((UUID.fromString(location.getId())))
                .warehouseId(location.getWarehouseIdAsUUID())
                .zoneId(location.getZoneIdAsUUID())
                .locationCode(location.getLocationCode())
                .locationName(location.getDescription())
                .aisle(location.getAisle())
                .rack(location.getRack())
                .level(location.getLevel())
                .position(location.getPosition())
                .barcode(location.getBarcode())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
} 
