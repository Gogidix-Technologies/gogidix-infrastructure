package com.exalt.warehousing.fulfillment.client.impl;

import com.exalt.warehousing.fulfillment.client.InventoryClient;
import com.exalt.warehousing.fulfillment.dto.inventory.*;
// import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of the InventoryClient for testing
 * This implementation simulates inventory operations without a real inventory service
 */
@Service
@Profile({"dev", "test", "local"})
// @Slf4j

public class MockInventoryClientImpl implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(MockInventoryClientImpl.class);

    // Mock warehouses with IDs
    private static final Map<UUID, String> WAREHOUSES = new HashMap<>();
    // Mock inventory data: Map<ProductId, Map<WarehouseId, Quantity>>
    private static final Map<UUID, Map<UUID, Integer>> INVENTORY = new HashMap<>();
    // Mock reservations
    private static final Map<UUID, InventoryReservationResponse> RESERVATIONS = new HashMap<>();
    // Mock allocations
    private static final Map<UUID, InventoryAllocationResponse> ALLOCATIONS = new HashMap<>();

    static {
        // Initialize mock warehouses
        UUID warehouse1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID warehouse2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID warehouse3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        
        WAREHOUSES.put(warehouse1, "East Coast Warehouse");
        WAREHOUSES.put(warehouse2, "West Coast Warehouse");
        WAREHOUSES.put(warehouse3, "Central Warehouse");
        
        // Initialize mock inventory
        // Product 1 available in all warehouses
        UUID product1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Map<UUID, Integer> product1Inventory = new HashMap<>();
        product1Inventory.put(warehouse1, 50);
        product1Inventory.put(warehouse2, 25);
        product1Inventory.put(warehouse3, 100);
        INVENTORY.put(product1, product1Inventory);
        
        // Product 2 available in two warehouses
        UUID product2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Map<UUID, Integer> product2Inventory = new HashMap<>();
        product2Inventory.put(warehouse1, 10);
        product2Inventory.put(warehouse3, 40);
        INVENTORY.put(product2, product2Inventory);
        
        // Product 3 available in one warehouse
        UUID product3 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        Map<UUID, Integer> product3Inventory = new HashMap<>();
        product3Inventory.put(warehouse2, 75);
        INVENTORY.put(product3, product3Inventory);
    }

    @Override
    public InventoryCheckResponse checkInventoryAvailability(InventoryCheckRequest request) {
        log.debug("Checking inventory availability for order: {}", request.getOrderId());
        
        // Check availability across all warehouses
        List<InventoryCheckResponse.InventoryItemAvailabilityDTO> itemAvailability = new ArrayList<>();
        boolean allAvailable = true;
        
        for (InventoryCheckRequest.InventoryItemDTO requestItem : request.getItems()) {
            // Get product inventory
            Map<UUID, Integer> productInventory = INVENTORY.getOrDefault(requestItem.getProductId(), Collections.emptyMap());
            
            // Calculate total available across warehouses
            int totalAvailable = productInventory.values().stream().mapToInt(Integer::intValue).sum();
            boolean itemAvailable = totalAvailable >= requestItem.getQuantity();
            
            if (!itemAvailable) {
                allAvailable = false;
            }
            
            // Find best warehouse for this product
            UUID bestWarehouse = null;
            int maxQuantity = 0;
            for (Map.Entry<UUID, Integer> entry : productInventory.entrySet()) {
                if (entry.getValue() > maxQuantity) {
                    bestWarehouse = entry.getKey();
                    maxQuantity = entry.getValue();
                }
            }
            
            // Add item availability info
            InventoryCheckResponse.InventoryItemAvailabilityDTO availabilityDTO = 
                InventoryCheckResponse.InventoryItemAvailabilityDTO.builder()
                    .productId(requestItem.getProductId())
                    .sku(requestItem.getSku())
                    .requestedQuantity(requestItem.getQuantity())
                    .availableQuantity(totalAvailable)
                    .available(itemAvailable)
                    .warehouseId(bestWarehouse)
                    .unavailableReason(itemAvailable ? null : "Insufficient stock")
                    .binLocation(itemAvailable ? "MOCK-BIN-" + bestWarehouse.toString().substring(0, 4) : null)
                    .build();
            
            itemAvailability.add(availabilityDTO);
        }
        
        // Determine best warehouse for entire order if all items available
        UUID recommendedWarehouse = null;
        if (allAvailable) {
            recommendedWarehouse = getRecommendedWarehouseInternal(request);
        }
        
        return InventoryCheckResponse.builder()
                .orderId(request.getOrderId())
                .allItemsAvailable(allAvailable)
                .items(itemAvailability)
                .recommendedWarehouseId(recommendedWarehouse)
                .build();
    }

    @Override
    public InventoryCheckResponse checkWarehouseInventory(UUID warehouseId, InventoryCheckRequest request) {
        log.debug("Checking inventory in warehouse {} for order: {}", warehouseId, request.getOrderId());
        
        // Check availability in specific warehouse
        List<InventoryCheckResponse.InventoryItemAvailabilityDTO> itemAvailability = new ArrayList<>();
        boolean allAvailable = true;
        
        for (InventoryCheckRequest.InventoryItemDTO requestItem : request.getItems()) {
            // Get product inventory
            Map<UUID, Integer> productInventory = INVENTORY.getOrDefault(requestItem.getProductId(), Collections.emptyMap());
            
            // Get available quantity in specified warehouse
            int available = productInventory.getOrDefault(warehouseId, 0);
            boolean itemAvailable = available >= requestItem.getQuantity();
            
            if (!itemAvailable) {
                allAvailable = false;
            }
            
            // Add item availability info
            InventoryCheckResponse.InventoryItemAvailabilityDTO availabilityDTO = 
                InventoryCheckResponse.InventoryItemAvailabilityDTO.builder()
                    .productId(requestItem.getProductId())
                    .sku(requestItem.getSku())
                    .requestedQuantity(requestItem.getQuantity())
                    .availableQuantity(available)
                    .available(itemAvailable)
                    .warehouseId(warehouseId)
                    .unavailableReason(itemAvailable ? null : "Insufficient stock in warehouse")
                    .binLocation(itemAvailable ? "MOCK-BIN-" + warehouseId.toString().substring(0, 4) : null)
                    .build();
            
            itemAvailability.add(availabilityDTO);
        }
        
        return InventoryCheckResponse.builder()
                .orderId(request.getOrderId())
                .allItemsAvailable(allAvailable)
                .items(itemAvailability)
                .recommendedWarehouseId(warehouseId)
                .build();
    }

    @Override
    public UUID getRecommendedWarehouse(InventoryCheckRequest request) {
        log.debug("Getting recommended warehouse for order: {}", request.getOrderId());
        return getRecommendedWarehouseInternal(request);
    }

    @Override
    public InventoryReservationResponse reserveInventory(InventoryReservationRequest request) {
        log.debug("Reserving inventory for order: {}", request.getOrderId());
        
        UUID reservationId = UUID.randomUUID();
        List<InventoryReservationResponse.ReservedItemDTO> reservedItems = new ArrayList<>();
        boolean allSuccess = true;
        
        // Get recommended warehouse
        InventoryCheckRequest checkRequest = new InventoryCheckRequest();
        checkRequest.setOrderId(request.getOrderId());
        checkRequest.setItems(request.getItems().stream()
                .map(item -> new InventoryCheckRequest.InventoryItemDTO(
                        item.getProductId(), item.getSku(), item.getQuantity()))
                .collect(Collectors.toList()));
        
        UUID warehouseId = getRecommendedWarehouseInternal(checkRequest);
        
        // Reserve each item
        for (InventoryReservationRequest.ReservationItemDTO requestItem : request.getItems()) {
            // Check if item is available in warehouse
            Map<UUID, Integer> productInventory = INVENTORY.getOrDefault(requestItem.getProductId(), Collections.emptyMap());
            int available = productInventory.getOrDefault(warehouseId, 0);
            boolean success = available >= requestItem.getQuantity();
            
            // Create reserved item
            InventoryReservationResponse.ReservedItemDTO reservedItem = 
                InventoryReservationResponse.ReservedItemDTO.builder()
                    .productId(requestItem.getProductId())
                    .sku(requestItem.getSku())
                    .quantity(requestItem.getQuantity())
                    .reservationItemId(UUID.randomUUID())
                    .warehouseId(warehouseId)
                    .binLocation(success ? "MOCK-BIN-" + warehouseId.toString().substring(0, 4) : null)
                    .success(success)
                    .errorMessage(success ? null : "Insufficient stock in warehouse")
                    .build();
            
            reservedItems.add(reservedItem);
            
            if (!success) {
                allSuccess = false;
            } else {
                // Reduce available quantity (simulate reservation)
                productInventory.put(warehouseId, available - requestItem.getQuantity());
            }
        }
        
        // Create reservation response
        InventoryReservationResponse response = InventoryReservationResponse.builder()
                .reservationId(reservationId)
                .orderId(request.getOrderId())
                .fulfillmentOrderId(request.getFulfillmentOrderId())
                .success(allSuccess)
                .errorMessage(allSuccess ? null : "Some items could not be reserved")
                .expiresAt(LocalDateTime.now().plusHours(24))
                .items(reservedItems)
                .build();
        
        // Store reservation
        if (allSuccess) {
            RESERVATIONS.put(reservationId, response);
        }
        
        return response;
    }

    @Override
    public boolean releaseInventory(InventoryReleaseRequest request) {
        log.debug("Releasing inventory for order: {}", request.getOrderId());
        
        if (request.getReservationId() != null) {
            // Release entire reservation
            InventoryReservationResponse reservation = RESERVATIONS.remove(request.getReservationId());
            if (reservation != null) {
                // Return quantities to inventory
                restoreInventoryFromReservation(reservation);
                return true;
            }
        } else if (request.getReservationItemIds() != null && !request.getReservationItemIds().isEmpty()) {
            // Release specific items
            // This is simplified - in a real implementation we would need to track individual item reservations
            return true;
        }
        
        return false;
    }

    @Override
    public InventoryAllocationResponse allocateInventory(InventoryAllocationRequest request) {
        log.debug("Allocating inventory for order: {}", request.getOrderId());
        
        UUID allocationId = UUID.randomUUID();
        List<InventoryAllocationResponse.AllocatedItemDTO> allocatedItems = new ArrayList<>();
        boolean allSuccess = true;
        
        // Get reservation
        InventoryReservationResponse reservation = RESERVATIONS.get(request.getReservationId());
        if (reservation == null) {
            return InventoryAllocationResponse.builder()
                    .allocationId(allocationId)
                    .orderId(request.getOrderId())
                    .fulfillmentOrderId(request.getFulfillmentOrderId())
                    .reservationId(request.getReservationId())
                    .success(false)
                    .errorMessage("Reservation not found")
                    .items(Collections.emptyList())
                    .build();
        }
        
        // Allocate each reserved item
        for (InventoryReservationResponse.ReservedItemDTO reservedItem : reservation.getItems()) {
            // Create allocated item
            InventoryAllocationResponse.AllocatedItemDTO allocatedItem = 
                InventoryAllocationResponse.AllocatedItemDTO.builder()
                    .productId(reservedItem.getProductId())
                    .sku(reservedItem.getSku())
                    .quantity(reservedItem.getQuantity())
                    .reservationItemId(reservedItem.getReservationItemId())
                    .allocationItemId(UUID.randomUUID())
                    .warehouseId(reservedItem.getWarehouseId())
                    .binLocation(reservedItem.getBinLocation())
                    .success(true)
                    .build();
            
            allocatedItems.add(allocatedItem);
        }
        
        // Create allocation response
        InventoryAllocationResponse response = InventoryAllocationResponse.builder()
                .allocationId(allocationId)
                .orderId(request.getOrderId())
                .fulfillmentOrderId(request.getFulfillmentOrderId())
                .reservationId(request.getReservationId())
                .success(allSuccess)
                .items(allocatedItems)
                .build();
        
        // Store allocation
        ALLOCATIONS.put(allocationId, response);
        
        // Remove reservation
        RESERVATIONS.remove(request.getReservationId());
        
        return response;
    }
    
    // Helper methods
    
    private UUID getRecommendedWarehouseInternal(InventoryCheckRequest request) {
        // This is a simplified recommendation algorithm
        // In a real implementation, this would consider:
        // - Inventory availability
        // - Warehouse proximity to delivery address
        // - Shipping costs
        // - Order priority
        
        // Count number of items available in each warehouse
        Map<UUID, Integer> warehouseScores = new HashMap<>();
        
        for (InventoryCheckRequest.InventoryItemDTO requestItem : request.getItems()) {
            Map<UUID, Integer> productInventory = INVENTORY.getOrDefault(requestItem.getProductId(), Collections.emptyMap());
            
            for (Map.Entry<UUID, Integer> entry : productInventory.entrySet()) {
                UUID warehouseId = entry.getKey();
                int quantity = entry.getValue();
                
                if (quantity >= requestItem.getQuantity()) {
                    // This warehouse can fulfill this item
                    warehouseScores.put(warehouseId, warehouseScores.getOrDefault(warehouseId, 0) + 1);
                }
            }
        }
        
        // Find warehouse with highest score
        return warehouseScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    private void restoreInventoryFromReservation(InventoryReservationResponse reservation) {
        for (InventoryReservationResponse.ReservedItemDTO reservedItem : reservation.getItems()) {
            UUID productId = reservedItem.getProductId();
            UUID warehouseId = reservedItem.getWarehouseId();
            int quantity = reservedItem.getQuantity();
            
            Map<UUID, Integer> productInventory = INVENTORY.getOrDefault(productId, new HashMap<>());
            int currentQuantity = productInventory.getOrDefault(warehouseId, 0);
            productInventory.put(warehouseId, currentQuantity + quantity);
            INVENTORY.put(productId, productInventory);
        }
    }
} 
