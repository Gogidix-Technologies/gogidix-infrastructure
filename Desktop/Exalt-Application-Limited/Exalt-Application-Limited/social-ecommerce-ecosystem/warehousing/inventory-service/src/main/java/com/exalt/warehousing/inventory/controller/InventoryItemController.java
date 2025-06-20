package com.exalt.warehousing.inventory.controller;

import java.util.Map;

import com.exalt.warehousing.inventory.dto.InventoryItemDTO;
import com.exalt.warehousing.inventory.entity.InventoryItem;
import com.exalt.warehousing.inventory.enums.InventoryStatus;
import com.exalt.warehousing.inventory.service.InventoryService;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for inventory item operations
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Items", description = "APIs for managing inventory items")
public class InventoryItemController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by ID", description = "Retrieves detailed information about a specific inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory item found", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> getInventoryItem(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id) {
        log.debug("REST request to get inventory item with ID: {}", id);
        InventoryItem item = inventoryService.getInventoryItemById(id);
        return ResponseEntity.ok(convertToDto(item));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get inventory item by SKU", description = "Retrieves detailed information about a specific inventory item by its SKU")
    @ApiResponse(responseCode = "200", description = "Inventory item found", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> getInventoryItemBySku(
            @Parameter(description = "Inventory item SKU", required = true) @PathVariable String sku) {
        log.debug("REST request to get inventory item with SKU: {}", sku);
        InventoryItem item = inventoryService.getInventoryItemBySku(sku);
        return ResponseEntity.ok(convertToDto(item));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory item by product ID", description = "Retrieves detailed information about a specific inventory item by its associated product ID")
    @ApiResponse(responseCode = "200", description = "Inventory item found", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> getInventoryItemByProductId(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId) {
        log.debug("REST request to get inventory item for product with ID: {}", productId);
        InventoryItem item = inventoryService.getInventoryItemByProductId(productId);
        return ResponseEntity.ok(convertToDto(item));
    }

    @PostMapping
    @Operation(summary = "Create a new inventory item", description = "Creates a new inventory item with the provided data")
    @ApiResponse(responseCode = "201", description = "Inventory item created", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<InventoryItemDTO> createInventoryItem(
            @Parameter(description = "Inventory item data", required = true) @Valid @RequestBody InventoryItemDTO inventoryItemDTO) {
        log.debug("REST request to create inventory item: {}", inventoryItemDTO);
        InventoryItem item = convertToEntity(inventoryItemDTO);
        InventoryItem createdItem = inventoryService.createInventoryItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(createdItem));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing inventory item", description = "Updates an existing inventory item with the provided data")
    @ApiResponse(responseCode = "200", description = "Inventory item updated", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> updateInventoryItem(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Updated inventory item data", required = true) @Valid @RequestBody InventoryItemDTO inventoryItemDTO) {
        log.debug("REST request to update inventory item with ID: {}", id);
        InventoryItem item = convertToEntity(inventoryItemDTO);
        InventoryItem updatedItem = inventoryService.updateInventoryItem(id, item);
        return ResponseEntity.ok(convertToDto(updatedItem));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an inventory item", description = "Marks an inventory item as discontinued")
    @ApiResponse(responseCode = "204", description = "Inventory item deleted")
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<Void> deleteInventoryItem(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id) {
        log.debug("REST request to delete inventory item with ID: {}", id);
        inventoryService.deleteInventoryItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all inventory items", description = "Retrieves a paginated list of inventory items")
    @ApiResponse(responseCode = "200", description = "List of inventory items returned")
    public ResponseEntity<Page<InventoryItemDTO>> getAllInventoryItems(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("REST request to get all inventory items");
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<InventoryItem> itemsPage = inventoryService.getAllInventoryItems(pageable);
        Page<InventoryItemDTO> dtoPage = itemsPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active inventory items", description = "Retrieves a paginated list of active inventory items")
    @ApiResponse(responseCode = "200", description = "List of active inventory items returned")
    public ResponseEntity<Page<InventoryItemDTO>> getActiveInventoryItems(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("REST request to get active inventory items");
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<InventoryItem> itemsPage = inventoryService.getActiveInventoryItems(pageable);
        Page<InventoryItemDTO> dtoPage = itemsPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/search")
    @Operation(summary = "Search inventory items", description = "Searches for inventory items by name or SKU")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<Page<InventoryItemDTO>> searchInventoryItems(
            @Parameter(description = "Search term") @RequestParam String term,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.debug("REST request to search inventory items with term: {}", term);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> itemsPage = inventoryService.searchInventoryItems(term, pageable);
        Page<InventoryItemDTO> dtoPage = itemsPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get inventory items by status", description = "Retrieves a paginated list of inventory items with the specified status")
    @ApiResponse(responseCode = "200", description = "List of inventory items returned")
    public ResponseEntity<Page<InventoryItemDTO>> getInventoryItemsByStatus(
            @Parameter(description = "Inventory status", required = true) @PathVariable InventoryStatus status,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.debug("REST request to get inventory items with status: {}", status);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> itemsPage = inventoryService.getInventoryItemsByStatus(status, pageable);
        Page<InventoryItemDTO> dtoPage = itemsPage.map(this::convertToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items", description = "Retrieves a list of inventory items with quantity below their low stock threshold")
    @ApiResponse(responseCode = "200", description = "List of low stock items returned")
    public ResponseEntity<List<InventoryItemDTO>> getLowStockItems() {
        log.debug("REST request to get low stock inventory items");
        
        List<InventoryItem> items = inventoryService.getLowStockItems();
        List<InventoryItemDTO> dtos = items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/critical-stock")
    @Operation(summary = "Get critical stock items", description = "Retrieves a list of inventory items with quantity below their safety stock level")
    @ApiResponse(responseCode = "200", description = "List of critical stock items returned")
    public ResponseEntity<List<InventoryItemDTO>> getCriticalStockItems() {
        log.debug("REST request to get critical stock inventory items");
        
        List<InventoryItem> items = inventoryService.getCriticalStockItems();
        List<InventoryItemDTO> dtos = items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update inventory item status", description = "Updates the status of an inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory item status updated", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> updateInventoryStatus(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id,
            @Parameter(description = "New status", required = true) @RequestParam InventoryStatus status) {
        log.debug("REST request to update status of inventory item ID: {} to {}", id, status);
        
        InventoryItem updatedItem = inventoryService.updateInventoryStatus(id, status);
        return ResponseEntity.ok(convertToDto(updatedItem));
    }

    @PatchMapping("/{id}/thresholds")
    @Operation(summary = "Update inventory thresholds", description = "Updates the low stock threshold and safety stock level of an inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory thresholds updated", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> updateInventoryThresholds(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Low stock threshold", required = true) @RequestParam int lowStockThreshold,
            @Parameter(description = "Safety stock level", required = true) @RequestParam int safetyStockLevel) {
        log.debug("REST request to update thresholds of inventory item ID: {}", id);
        
        InventoryItem updatedItem = inventoryService.updateInventoryThresholds(id, lowStockThreshold, safetyStockLevel);
        return ResponseEntity.ok(convertToDto(updatedItem));
    }

    @PatchMapping("/{id}/adjust")
    @Operation(summary = "Adjust inventory quantity", description = "Adjusts the quantity of an inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory quantity adjusted", content = @Content(schema = @Schema(implementation = InventoryItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemDTO> adjustInventory(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Quantity adjustment (positive or negative)", required = true) @RequestParam int adjustment,
            @Parameter(description = "Reason for adjustment", required = true) @RequestParam String reason,
            @Parameter(description = "User ID", required = true) @RequestParam UUID userId) {
        log.debug("REST request to adjust inventory for item ID: {} by {}", id, adjustment);
        
        InventoryItem updatedItem = inventoryService.adjustInventory(id, adjustment, reason, userId);
        return ResponseEntity.ok(convertToDto(updatedItem));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check inventory availability", description = "Checks if the specified quantity of an item is available")
    @ApiResponse(responseCode = "200", description = "Availability check result")
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<Boolean> checkAvailability(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Quantity to check", required = true) @RequestParam int quantity) {
        log.debug("REST request to check availability of item ID: {} for quantity: {}", id, quantity);
        
        boolean available = inventoryService.checkAvailability(id, quantity);
        return ResponseEntity.ok(available);
    }

    /**
     * Converts entity to DTO
     * @param item the inventory item entity
     * @return the DTO representation
     */
    private InventoryItemDTO convertToDto(InventoryItem item) {
        return InventoryItemDTO.builder()
                .id(UUID.fromString(item.getId()))
                .productId(UUID.fromString(String.valueOf(item.getProductId())))
                .sku(item.getSku())
                .name(item.getName())
                .description(item.getDescription())
                .totalQuantity(item.getTotalQuantity() != null ? item.getTotalQuantity().intValue() : 0)
                .reservedQuantity(item.getReservedQuantity() != null ? item.getReservedQuantity().intValue() : 0)
                .lowStockThreshold(item.getLowStockThreshold() != null ? item.getLowStockThreshold().intValue() : 0)
                .safetyStockLevel(item.getSafetyStockLevel() != null ? item.getSafetyStockLevel().intValue() : 0)
                .isActive(item.getIsActive())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .availableQuantity(item.getAvailableQuantity() != null ? item.getAvailableQuantity().intValue() : 0)
                .isLowStock(item.isLowStock())
                .isCriticallyLowStock(item.isCriticallyLowStock())
                .isOutOfStock(item.isOutOfStock())
                .build();
    }

    /**
     * Converts DTO to entity
     * @param dto the inventory item DTO
     * @return the entity representation
     */
    private InventoryItem convertToEntity(InventoryItemDTO dto) {
        return InventoryItem.builder()
                .id(dto.getId() != null ? dto.getId().toString() : null)
                .productId(dto.getProductId() != null ? Long.parseLong(dto.getProductId().toString().replace("-", "").substring(0, 8), 16) : null)
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .totalQuantity(dto.getTotalQuantity() != null ? BigDecimal.valueOf(dto.getTotalQuantity()) : BigDecimal.ZERO)
                .reservedQuantity(dto.getReservedQuantity() != null ? BigDecimal.valueOf(dto.getReservedQuantity()) : BigDecimal.ZERO)
                .lowStockThreshold(dto.getLowStockThreshold() != null ? BigDecimal.valueOf(dto.getLowStockThreshold()) : null)
                .safetyStockLevel(dto.getSafetyStockLevel() != null ? BigDecimal.valueOf(dto.getSafetyStockLevel()) : null)
                .isActive(dto.getIsActive())
                .status(dto.getStatus())
                .build();
    }
}


