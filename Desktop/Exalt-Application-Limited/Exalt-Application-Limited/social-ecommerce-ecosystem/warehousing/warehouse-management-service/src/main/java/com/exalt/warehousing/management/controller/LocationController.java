package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.service.LocationService;
import com.exalt.warehousing.management.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Location management
 */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Location Management", description = "APIs for managing warehouse locations")
public class LocationController {

    private final LocationService locationService;
    private final ZoneService zoneService;

    @PostMapping
    @Operation(summary = "Create a new location")
    public ResponseEntity<Location> createLocation(@Valid @RequestBody Location location) {
        log.info("REST request to create a new location");
        Location createdLocation = locationService.createLocation(location);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create multiple locations")
    public ResponseEntity<List<Location>> createLocations(@Valid @RequestBody List<Location> locations) {
        log.info("REST request to create {} locations", locations.size());
        List<Location> createdLocations = locationService.createLocations(locations);
        return new ResponseEntity<>(createdLocations, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing location")
    public ResponseEntity<Location> updateLocation(@PathVariable("id") UUID id, @Valid @RequestBody Location location) {
        log.info("REST request to update location with id: {}", id);
        Location updatedLocation = locationService.updateLocation(id, location);
        return ResponseEntity.ok(updatedLocation);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a location by ID")
    public ResponseEntity<Location> getLocationById(@PathVariable("id") UUID id) {
        log.info("REST request to get location with id: {}", id);
        return locationService.getLocation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "Get all locations in a zone")
    public ResponseEntity<List<Location>> getLocationsByZone(@PathVariable UUID zoneId) {
        log.info("REST request to get all locations in zone: {}", zoneId);
        List<Location> locations = locationService.getLocationsByZoneId(zoneId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get all locations in a warehouse")
    public ResponseEntity<List<Location>> getLocationsByWarehouse(@PathVariable UUID warehouseId) {
        log.info("REST request to get all locations in warehouse: {}", warehouseId);
        List<Location> locations = locationService.getLocationsByWarehouseId(warehouseId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get all locations with a specific status")
    public ResponseEntity<List<Location>> getLocationsByStatus(@PathVariable LocationStatus status) {
        log.info("REST request to get all locations with status: {}", status);
        List<Location> locations = locationService.getLocationsByStatus(status);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/zone/{zoneId}/available")
    @Operation(summary = "Get all available locations in a zone")
    public ResponseEntity<List<Location>> getAvailableLocationsInZone(@PathVariable UUID zoneId) {
        log.info("REST request to get available locations in zone: {}", zoneId);
        List<Location> locations = locationService.getLocationsByZoneIdAndStatus(zoneId, LocationStatus.AVAILABLE);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Get location by barcode")
    public ResponseEntity<Location> getLocationByBarcode(@PathVariable String barcode) {
        log.info("REST request to get location with barcode: {}", barcode);
        Optional<Location> location = locationService.getLocationByBarcode(barcode);
        return location.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update location status")
    public ResponseEntity<Location> updateLocationStatus(
            @PathVariable("id") UUID id,
            @RequestParam LocationStatus status) {
        log.info("REST request to update location {} status to: {}", id, status);
        Location updatedLocation = locationService.updateLocationStatus(id, status);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a location")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") UUID id) {
        log.info("REST request to delete location with id: {}", id);
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-barcodes")
    @Operation(summary = "Generate location barcodes for a zone")
    public ResponseEntity<List<String>> generateLocationBarcodes(
            @RequestParam UUID zoneId,
            @RequestParam int count) {
        log.info("REST request to generate {} location barcodes for zone: {}", count, zoneId);
        
        // Get the zone first
        Optional<Zone> zoneOpt = zoneService.getZone(zoneId);
        if (zoneOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<String> barcodes = locationService.generateLocationBarcodes(zoneOpt.get(), count);
        return ResponseEntity.ok(barcodes);
    }

    @GetMapping("/warehouse/{warehouseId}/count-by-status")
    @Operation(summary = "Get total number of locations by status in a warehouse")
    public ResponseEntity<Map<LocationStatus, Long>> getLocationCountByStatus(@PathVariable UUID warehouseId) {
        log.info("REST request to get location count by status for warehouse: {}", warehouseId);
        Map<LocationStatus, Long> counts = locationService.getLocationCountByStatus(warehouseId);
        return ResponseEntity.ok(counts);
    }
} 
