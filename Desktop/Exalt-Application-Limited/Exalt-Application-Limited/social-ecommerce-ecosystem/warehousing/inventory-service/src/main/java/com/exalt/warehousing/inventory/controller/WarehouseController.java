package com.exalt.warehousing.inventory.controller;

import java.util.Map;

import com.exalt.warehousing.inventory.dto.WarehouseLocationDTO;
import com.exalt.warehousing.inventory.model.WarehouseLocation;
import com.exalt.warehousing.inventory.model.WarehouseType;
import com.exalt.warehousing.inventory.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for warehouse operations
 */
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouses", description = "APIs for managing warehouse locations")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID", description = "Retrieves detailed information about a specific warehouse")
    @ApiResponse(responseCode = "200", description = "Warehouse found", content = @Content(schema = @Schema(implementation = WarehouseLocationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseLocationDTO> getWarehouse(
            @Parameter(description = "Warehouse ID", required = true) @PathVariable UUID id) {
        log.debug("REST request to get warehouse with ID: {}", id);
        WarehouseLocation warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(convertToDto(warehouse));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get warehouse by code", description = "Retrieves detailed information about a specific warehouse by its code")
    @ApiResponse(responseCode = "200", description = "Warehouse found", content = @Content(schema = @Schema(implementation = WarehouseLocationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseLocationDTO> getWarehouseByCode(
            @Parameter(description = "Warehouse code", required = true) @PathVariable String code) {
        log.debug("REST request to get warehouse with code: {}", code);
        WarehouseLocation warehouse = warehouseService.getWarehouseByCode(code);
        return ResponseEntity.ok(convertToDto(warehouse));
    }

    @PostMapping
    @Operation(summary = "Create a new warehouse", description = "Creates a new warehouse with the provided data")
    @ApiResponse(responseCode = "201", description = "Warehouse created", content = @Content(schema = @Schema(implementation = WarehouseLocationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<WarehouseLocationDTO> createWarehouse(
            @Parameter(description = "Warehouse data", required = true) @Valid @RequestBody WarehouseLocationDTO warehouseDTO) {
        log.debug("REST request to create warehouse: {}", warehouseDTO);
        WarehouseLocation warehouse = convertToEntity(warehouseDTO);
        WarehouseLocation createdWarehouse = warehouseService.createWarehouse(warehouse);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(createdWarehouse));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing warehouse", description = "Updates an existing warehouse with the provided data")
    @ApiResponse(responseCode = "200", description = "Warehouse updated", content = @Content(schema = @Schema(implementation = WarehouseLocationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseLocationDTO> updateWarehouse(
            @Parameter(description = "Warehouse ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Updated warehouse data", required = true) @Valid @RequestBody WarehouseLocationDTO warehouseDTO) {
        log.debug("REST request to update warehouse with ID: {}", id);
        WarehouseLocation warehouse = convertToEntity(warehouseDTO);
        WarehouseLocation updatedWarehouse = warehouseService.updateWarehouse(id, warehouse);
        return ResponseEntity.ok(convertToDto(updatedWarehouse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a warehouse", description = "Marks a warehouse as inactive")
    @ApiResponse(responseCode = "204", description = "Warehouse deleted")
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<Void> deleteWarehouse(
            @Parameter(description = "Warehouse ID", required = true) @PathVariable UUID id) {
        log.debug("REST request to delete warehouse with ID: {}", id);
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all warehouses", description = "Retrieves a paginated list of warehouses")
    @ApiResponse(responseCode = "200", description = "List of warehouses returned")
    public ResponseEntity<Page<WarehouseLocationDTO>> getAllWarehouses(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("REST request to get all warehouses");
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<WarehouseLocation> warehousesPage = warehouseService.getAllWarehouses(pageable);
        Page<WarehouseLocationDTO> dtoPage = warehousesPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active warehouses", description = "Retrieves a list of active warehouses")
    @ApiResponse(responseCode = "200", description = "List of active warehouses returned")
    public ResponseEntity<List<WarehouseLocationDTO>> getActiveWarehouses() {
        log.debug("REST request to get active warehouses");
        
        List<WarehouseLocation> warehouses = warehouseService.getActiveWarehouses();
        List<WarehouseLocationDTO> dtos = warehouses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    @Operation(summary = "Search warehouses", description = "Searches for warehouses by name, code, or city")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<Page<WarehouseLocationDTO>> searchWarehouses(
            @Parameter(description = "Search term") @RequestParam String term,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.debug("REST request to search warehouses with term: {}", term);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarehouseLocation> warehousesPage = warehouseService.searchWarehouses(term, pageable);
        Page<WarehouseLocationDTO> dtoPage = warehousesPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get warehouses by type", description = "Retrieves a paginated list of warehouses with the specified type")
    @ApiResponse(responseCode = "200", description = "List of warehouses returned")
    public ResponseEntity<Page<WarehouseLocationDTO>> getWarehousesByType(
            @Parameter(description = "Warehouse type", required = true) @PathVariable WarehouseType type,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.debug("REST request to get warehouses with type: {}", type);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarehouseLocation> warehousesPage = warehouseService.getWarehousesByType(type, pageable);
        Page<WarehouseLocationDTO> dtoPage = warehousesPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/type/{type}/active")
    @Operation(summary = "Get active warehouses by type", description = "Retrieves a list of active warehouses with the specified type")
    @ApiResponse(responseCode = "200", description = "List of active warehouses returned")
    public ResponseEntity<List<WarehouseLocationDTO>> getActiveWarehousesByType(
            @Parameter(description = "Warehouse type", required = true) @PathVariable WarehouseType type) {
        log.debug("REST request to get active warehouses with type: {}", type);
        
        List<WarehouseLocation> warehouses = warehouseService.getActiveWarehousesByType(type);
        List<WarehouseLocationDTO> dtos = warehouses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby warehouses", description = "Finds warehouses near a specific location")
    @ApiResponse(responseCode = "200", description = "List of nearby warehouses returned")
    public ResponseEntity<List<WarehouseLocationDTO>> findNearbyWarehouses(
            @Parameter(description = "Latitude", required = true) @RequestParam Double latitude,
            @Parameter(description = "Longitude", required = true) @RequestParam Double longitude,
            @Parameter(description = "Search radius in kilometers", required = true) @RequestParam Double radiusKm) {
        log.debug("REST request to find warehouses near location: {}, {}", latitude, longitude);
        
        List<WarehouseLocation> warehouses = warehouseService.findNearbyWarehouses(latitude, longitude, radiusKm);
        List<WarehouseLocationDTO> dtos = warehouses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Converts entity to DTO
     * @param warehouse the warehouse entity
     * @return the DTO representation
     */
    private WarehouseLocationDTO convertToDto(WarehouseLocation warehouse) {
        return WarehouseLocationDTO.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .code(warehouse.getCode())
                .addressLine1(warehouse.getAddressLine1())
                .addressLine2(warehouse.getAddressLine2())
                .city(warehouse.getCity())
                .stateProvince(warehouse.getStateProvince())
                .postalCode(warehouse.getPostalCode())
                .country(warehouse.getCountry())
                .latitude(warehouse.getLatitude())
                .longitude(warehouse.getLongitude())
                .contactPhone(warehouse.getContactPhone())
                .contactEmail(warehouse.getContactEmail())
                .isActive(warehouse.getIsActive())
                .type(warehouse.getType())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }

    /**
     * Converts DTO to entity
     * @param dto the warehouse DTO
     * @return the entity representation
     */
    private WarehouseLocation convertToEntity(WarehouseLocationDTO dto) {
        return WarehouseLocation.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .stateProvince(dto.getStateProvince())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .isActive(dto.getIsActive())
                .type(dto.getType())
                .build();
    }
}


