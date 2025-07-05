package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.WarehouseDTO;
import com.exalt.warehousing.management.mapper.WarehouseMapper;
import com.exalt.warehousing.management.model.Warehouse;
import com.exalt.warehousing.management.model.WarehouseStatus;
import com.exalt.warehousing.management.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

import java.util.stream.Collectors;

/**
 * REST controller for managing warehouse operations
 */
@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Management", description = "API for warehouse operations")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;
    
    /**
     * Create a new warehouse
     *
     * @param warehouseDTO the warehouse to create
     * @return the created warehouse
     */
    @PostMapping
    @Operation(summary = "Create a new warehouse", 
            description = "Creates a new warehouse and returns it")
    @ApiResponse(responseCode = "201", description = "Warehouse created successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<WarehouseDTO> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(warehouseMapper.toDTO(createdWarehouse));
    }
    
    /**
     * Update an existing warehouse
     *
     * @param id the warehouse id
     * @param warehouseDTO the warehouse data to update
     * @return the updated warehouse
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a warehouse", 
            description = "Updates an existing warehouse's details")
    @ApiResponse(responseCode = "200", description = "Warehouse updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseDTO> updateWarehouse(@PathVariable String id, 
                                                       @Valid @RequestBody WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, warehouse);
        return ResponseEntity.ok(warehouseMapper.toDTO(updatedWarehouse));
    }
    
    /**
     * Get a warehouse by id
     *
     * @param id the warehouse id
     * @return the warehouse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID", 
            description = "Returns a warehouse based on its ID")
    @ApiResponse(responseCode = "200", description = "Warehouse retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseDTO> getWarehouse(@PathVariable String id) {
        return warehouseService.getWarehouse(id)
                .map(warehouse -> ResponseEntity.ok(warehouseMapper.toDTO(warehouse)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found with id " + id));
    }
    
    /**
     * Get a warehouse by code
     *
     * @param code the warehouse code
     * @return the warehouse
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get warehouse by code", 
            description = "Returns a warehouse based on its unique code")
    @ApiResponse(responseCode = "200", description = "Warehouse retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseDTO> getWarehouseByCode(@PathVariable String code) {
        return warehouseService.getWarehouseByCode(code)
                .map(warehouse -> ResponseEntity.ok(warehouseMapper.toDTO(warehouse)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found with code " + code));
    }
    
    /**
     * Get all warehouses
     *
     * @return list of all warehouses
     */
    @GetMapping
    @Operation(summary = "Get all warehouses", 
            description = "Returns a list of all warehouses in the system")
    @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses().stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(warehouses);
    }
    
    /**
     * Get warehouses by status
     *
     * @param status the warehouse status
     * @return list of warehouses with the given status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get warehouses by status", 
            description = "Returns warehouses based on their operational status")
    @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    public ResponseEntity<List<WarehouseDTO>> getWarehousesByStatus(@PathVariable WarehouseStatus status) {
        List<WarehouseDTO> warehouses = warehouseService.getWarehousesByStatus(status).stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(warehouses);
    }
    
    /**
     * Get warehouses by region
     *
     * @param region the region to filter by
     * @return list of warehouses in the given region
     */
    @GetMapping("/region/{region}")
    public ResponseEntity<List<WarehouseDTO>> getWarehousesByRegion(@PathVariable String region) {
        List<WarehouseDTO> warehouses = warehouseService.getWarehousesByRegion(region).stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(warehouses);
    }
    
    /**
     * Search warehouses by name
     *
     * @param name the name to search for
     * @return list of warehouses matching the search criteria
     */
    @GetMapping("/search")
    @Operation(summary = "Search warehouses by name", 
            description = "Returns warehouses based on their name")
    @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    public ResponseEntity<List<WarehouseDTO>> searchWarehousesByName(@RequestParam String name) {
        List<WarehouseDTO> warehouses = warehouseService.searchWarehousesByName(name).stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(warehouses);
    }
    
    /**
     * Change the status of a warehouse
     *
     * @param id the warehouse id
     * @param status the new status
     * @return the updated warehouse
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Change warehouse status", 
            description = "Updates the operational status of a warehouse")
    @ApiResponse(responseCode = "200", description = "Warehouse status updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = WarehouseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<WarehouseDTO> changeWarehouseStatus(@PathVariable String id, 
                                                             @RequestParam WarehouseStatus status) {
        Warehouse updatedWarehouse = warehouseService.changeWarehouseStatus(id, status);
        return ResponseEntity.ok(warehouseMapper.toDTO(updatedWarehouse));
    }
    
    /**
     * Delete a warehouse
     *
     * @param id the warehouse id
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a warehouse", 
            description = "Removes a warehouse from the system")
    @ApiResponse(responseCode = "204", description = "Warehouse deleted successfully")
    @ApiResponse(responseCode = "404", description = "Warehouse not found")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
} 

