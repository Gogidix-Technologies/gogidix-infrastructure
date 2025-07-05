package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.management.exception.DuplicateResourceException;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.Warehouse;
import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.model.ZoneType;
import com.exalt.warehousing.management.model.ZoneSummary;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.repository.WarehouseRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.service.ZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ZoneService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public Zone createZone(Zone zone) {
        log.info("Creating new zone in warehouse: {}", zone.getWarehouse().getId());
        
        // Ensure warehouse exists
        Warehouse warehouse = warehouseRepository.findById(zone.getWarehouse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + zone.getWarehouse().getId()));
        
        // Validate zone code uniqueness within warehouse
        if (zoneRepository.existsByCodeAndWarehouseId(zone.getCode(), (UUID)zone.getWarehouse())) {
            throw new DuplicateResourceException("Zone with code " + zone.getCode() + 
                    " already exists in warehouse with id " + zone.getWarehouse().getId());
        }
        
        // Set created timestamp
        zone.setCreatedAt(LocalDateTime.now());
        zone.setUpdatedAt(LocalDateTime.now());
        
        return zoneRepository.save(zone);
    }

    @Transactional
    public List<Zone> createZones(List<Zone> zones) {
        log.info("Creating {} new zones", zones.size());
        
        // Set timestamps for all zones
        LocalDateTime now = LocalDateTime.now();
        zones.forEach(zone -> {
            zone.setCreatedAt(now);
            zone.setUpdatedAt(now);
            
            // Generate code if not provided
            if (zone.getCode() == null || zone.getCode().trim().isEmpty()) {
                zone.setCode(generateZoneCode((UUID)zone.getWarehouse(), zone.getType()));
            }
        });
        
        return zoneRepository.saveAll(zones);
    }

    @Override
    @Transactional
    public Zone updateZone(UUID id, Zone zoneDetails) {
        log.info("Updating zone with id: {}", id);
        
        Zone existingZone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
        
        // Check if code is being changed and if the new code already exists in the warehouse
        if (!existingZone.getCode().equals(zoneDetails.getCode()) && 
                zoneRepository.existsByCodeAndWarehouseId(zoneDetails.getCode(), (UUID)existingZone.getWarehouse())) {
            throw new DuplicateResourceException("Zone with code " + zoneDetails.getCode() + 
                    " already exists in warehouse with id " + existingZone.getWarehouse().getId());
        }
        
        // Update fields
        existingZone.setName(zoneDetails.getName());
        existingZone.setCode(zoneDetails.getCode());
        existingZone.setDescription(zoneDetails.getDescription());
        existingZone.setType(zoneDetails.getType());
        existingZone.setMetadata(zoneDetails.getMetadata());
        existingZone.setUpdatedAt(LocalDateTime.now());
        
        return zoneRepository.save(existingZone);
    }

    @Override
    public Optional<Zone> getZone(UUID id) {
        log.debug("Getting zone by id: {}", id);
        return zoneRepository.findById(id);
    }

    @Override
    public List<Zone> getZonesByWarehouseId(UUID warehouseId) {
        log.debug("Getting all zones in warehouse: {}", warehouseId);
        return zoneRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Zone> getZonesByType(ZoneType type) {
        log.debug("Getting all zones with type: {}", type);
        return zoneRepository.findByType(type);
    }

    @Override
    public List<Zone> getZonesByWarehouseIdAndType(UUID warehouseId, ZoneType type) {
        log.debug("Getting zones in warehouse {} with type: {}", warehouseId, type);
        return zoneRepository.findByWarehouseIdAndType(warehouseId, type);
    }

    @Override
    public Optional<Zone> getZoneByCodeAndWarehouseId(String code, UUID warehouseId) {
        log.debug("Getting zone by code: {} in warehouse: {}", code, warehouseId);
        return zoneRepository.findByCodeAndWarehouseId(code, warehouseId);
    }

    @Override
    @Transactional
    public void deleteZone(UUID id) {
        log.info("Deleting zone with id: {}", id);
        
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
        
        // Check if zone has locations
        List<Location> locations = locationRepository.findByZoneId(id.toString());
        if (!locations.isEmpty()) {
            throw new IllegalStateException("Cannot delete zone with existing locations. Delete the locations first.");
        }
        
        zoneRepository.delete(zone);
    }

    @Override
    public boolean isZoneCodeExistsInWarehouse(String code, UUID warehouseId) {
        return zoneRepository.existsByCodeAndWarehouseId(code, warehouseId);
    }

    @Override
    @Transactional
    public Zone setZoneActive(UUID id, boolean isActive) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
                
        zone.setIsActive(isActive);
        zone.setUpdatedAt(LocalDateTime.now());
        
        return zoneRepository.save(zone);
    }

    public String generateZoneCode(UUID warehouseId, ZoneType zoneType) {
        // Format: {warehousePrefix}-{zoneTypePrefix}-{sequentialNumber}
        String warehouseCode = warehouseRepository.findById(warehouseId.toString())
                .map(warehouse -> warehouse.getCode())
                .orElse("WH");
                
        String zoneTypePrefix = zoneType.name().substring(0, Math.min(3, zoneType.name().length()));
        
        // Get highest existing code number for this warehouse and zone type
        List<Zone> existingZones = zoneRepository.findByWarehouseIdAndType(warehouseId, zoneType);
        
        Optional<Integer> highestNumber = existingZones.stream()
                .map(zone -> zone.getCode())
                .filter(code -> code != null && code.startsWith(warehouseCode + "-" + zoneTypePrefix + "-"))
                .map(code -> {
                    try {
                        return Integer.parseInt(code.substring((warehouseCode + "-" + zoneTypePrefix + "-").length()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo);
        
        int nextNumber = highestNumber.orElse(0) + 1;
        
        return String.format("%s-%s-%03d", warehouseCode, zoneTypePrefix, nextNumber);
    }

    public List<ZoneSummary> getWarehouseZoneSummary(UUID warehouseId) {
        log.debug("Getting zone summary for warehouse: {}", warehouseId);
        
        // Get all zones in warehouse
        List<Zone> zones = zoneRepository.findByWarehouseId(warehouseId);
        
        // Get all locations for these zones
        List<String> zoneIds = zones.stream().map(Zone::getId).collect(Collectors.toList());
        List<Location> locations = locationRepository.findByZoneIdIn(zoneIds);
        
        // Group locations by zone ID
        Map<String, List<Location>> locationsByZone = locations.stream()
                .collect(Collectors.groupingBy(location -> location.getZone().getId()));
        
        // Create summaries
        return zones.stream().map(zone -> {
            List<Location> zoneLocations = locationsByZone.getOrDefault(zone.getId(), Collections.emptyList());
            
            long availableCount = zoneLocations.stream()
                    .filter(location -> LocationStatus.AVAILABLE.equals(location.getStatus()))
                    .count();
                    
            long occupiedCount = zoneLocations.stream()
                    .filter(location -> LocationStatus.OCCUPIED.equals(location.getStatus()))
                    .count();
            
            return new ZoneSummaryImpl(
                    (UUID.fromString(zone.getId())),
                    zone.getName(),
                    zone.getType(),
                    zone.getCode(),
                    zoneLocations.size(),
                    (int) availableCount,
                    (int) occupiedCount
            );
        }).collect(Collectors.toList());
    }
    
    /**
     * Implementation of ZoneSummary
     */
    private static class ZoneSummaryImpl implements ZoneSummary {
        private final UUID zoneId;
        private final String name;
        private final ZoneType type;
        private final String code;
        private final int totalLocations;
        private final int availableLocations;
        private final int occupiedLocations;
        
        public ZoneSummaryImpl(UUID zoneId, String name, ZoneType type, String code, 
                              int totalLocations, int availableLocations, int occupiedLocations) {
            this.zoneId = zoneId;
            this.name = name;
            this.type = type;
            this.code = code;
            this.totalLocations = totalLocations;
            this.availableLocations = availableLocations;
            this.occupiedLocations = occupiedLocations;
        }
        
        @Override
        public UUID getZoneId() {
            return zoneId;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public ZoneType getType() {
            return type;
        }
        
        @Override
        public String getCode() {
            return code;
        }
        
        @Override
        public int getTotalLocations() {
            return totalLocations;
        }
        
        @Override
        public int getAvailableLocations() {
            return availableLocations;
        }
        
        @Override
        public int getOccupiedLocations() {
            return occupiedLocations;
        }
    }
} 
