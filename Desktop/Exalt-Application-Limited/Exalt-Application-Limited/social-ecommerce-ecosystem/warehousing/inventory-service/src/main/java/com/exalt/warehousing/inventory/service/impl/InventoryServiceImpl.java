package com.exalt.warehousing.inventory.service.impl;

import com.exalt.warehousing.inventory.exception.InventoryNotFoundException;
import com.exalt.warehousing.inventory.entity.InventoryItem;
import com.exalt.warehousing.inventory.enums.InventoryStatus;
import com.exalt.warehousing.inventory.entity.InventoryTransaction;
import com.exalt.warehousing.inventory.enums.TransactionType;
import com.exalt.warehousing.inventory.repository.InventoryItemRepository;
import com.exalt.warehousing.inventory.repository.InventoryTransactionRepository;
import com.exalt.warehousing.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the inventory service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Override
    public InventoryItem getInventoryItemById(UUID id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory item not found with ID: " + id));
    }

    @Override
    public InventoryItem getInventoryItemBySku(String sku) {
        return inventoryItemRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory item not found with SKU: " + sku));
    }

    @Override
    public InventoryItem getInventoryItemByProductId(Long productId) {
        return inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory item not found for product ID: " + productId));
    }

    @Override
    @Transactional
    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        log.info("Creating new inventory item with SKU: {}", inventoryItem.getSku());
        
        // Set default values if not provided
        if (inventoryItem.getReservedQuantity() == null) {
            inventoryItem.setReservedQuantity(BigDecimal.ZERO);
        }
        
        if (inventoryItem.getIsActive() == null) {
            inventoryItem.setIsActive(true);
        }
        
        if (inventoryItem.getStatus() == null) {
            inventoryItem.setStatus(determineInitialStatus(inventoryItem));
        }
        
        return inventoryItemRepository.save(inventoryItem);
    }

    @Override
    @Transactional
    public InventoryItem updateInventoryItem(UUID id, InventoryItem updatedItem) {
        log.info("Updating inventory item with ID: {}", id);
        
        InventoryItem existingItem = getInventoryItemById(id);
        
        // Update fields
        existingItem.setSku(updatedItem.getSku());
        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setLowStockThreshold(updatedItem.getLowStockThreshold());
        existingItem.setSafetyStockLevel(updatedItem.getSafetyStockLevel());
        existingItem.setIsActive(updatedItem.getIsActive());
        
        // Update status if provided, otherwise recalculate based on quantities
        if (updatedItem.getStatus() != null) {
            existingItem.setStatus(updatedItem.getStatus());
        } else {
            existingItem.setStatus(determineStatus(existingItem));
        }
        
        return inventoryItemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public void deleteInventoryItem(UUID id) {
        log.info("Deleting inventory item with ID: {}", id);
        
        InventoryItem item = getInventoryItemById(id);
        
        // Soft delete by deactivating
        item.setIsActive(false);
        item.setStatus(InventoryStatus.DISCONTINUED);
        
        inventoryItemRepository.save(item);
        
        log.info("Inventory item with ID: {} has been marked as discontinued", id);
    }

    @Override
    public Page<InventoryItem> getAllInventoryItems(Pageable pageable) {
        return inventoryItemRepository.findAll(pageable);
    }

    @Override
    public Page<InventoryItem> getActiveInventoryItems(Pageable pageable) {
        return inventoryItemRepository.findAllByIsActiveTrue(pageable);
    }

    @Override
    public Page<InventoryItem> searchInventoryItems(String searchTerm, Pageable pageable) {
        return inventoryItemRepository.searchByNameOrSku(searchTerm, pageable);
    }

    @Override
    @Transactional
    public InventoryItem updateInventoryStatus(UUID id, InventoryStatus status) {
        log.info("Updating status of inventory item ID: {} to {}", id, status);
        
        InventoryItem item = getInventoryItemById(id);
        item.setStatus(status);
        
        return inventoryItemRepository.save(item);
    }

    @Override
    public List<InventoryItem> getLowStockItems() {
        return inventoryItemRepository.findAllLowStockItems();
    }

    @Override
    public List<InventoryItem> getCriticalStockItems() {
        return inventoryItemRepository.findAllCriticalStockItems();
    }

    @Override
    public boolean checkAvailability(UUID itemId, int quantity) {
        InventoryItem item = getInventoryItemById(itemId);
        return item.getAvailableQuantity().compareTo(BigDecimal.valueOf(quantity)) >= 0;
    }

    @Override
    public Page<InventoryItem> getInventoryItemsByStatus(InventoryStatus status, Pageable pageable) {
        return inventoryItemRepository.findAllByStatus(status, pageable);
    }

    @Override
    @Transactional
    public InventoryItem updateInventoryThresholds(UUID id, int lowStockThreshold, int safetyStockLevel) {
        log.info("Updating thresholds for inventory item ID: {}", id);
        
        InventoryItem item = getInventoryItemById(id);
        item.setLowStockThreshold(BigDecimal.valueOf(lowStockThreshold));
        item.setSafetyStockLevel(BigDecimal.valueOf(safetyStockLevel));
        
        // Recalculate status based on new thresholds
        item.setStatus(determineStatus(item));
        
        return inventoryItemRepository.save(item);
    }

    @Override
    @Transactional
    public InventoryItem adjustInventory(UUID id, int adjustment, String reason, UUID userId) {
        log.info("Adjusting inventory for item ID: {} by {}", id, adjustment);
        
        InventoryItem item = getInventoryItemById(id);
        
        // Create transaction record
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryItemId(id)
                .warehouseId(UUID.randomUUID()) // TODO: Get actual warehouse ID
                .quantity(adjustment)
                .type(adjustment > 0 ? TransactionType.ADJUSTMENT_INCREASE : TransactionType.ADJUSTMENT_DECREASE)
                .userId(userId)
                .notes(reason)
                .build();
        
        transactionRepository.save(transaction);
        
        // Update inventory quantity
        item.setTotalQuantity(item.getTotalQuantity().add(BigDecimal.valueOf(adjustment)));
        
        // Update status based on new quantity
        item.setStatus(determineStatus(item));
        
        return inventoryItemRepository.save(item);
    }
    
    /**
     * Determines the initial status for a new inventory item
     * @param item the inventory item
     * @return the appropriate status
     */
    private InventoryStatus determineInitialStatus(InventoryItem item) {
        if (!item.getIsActive()) {
            return InventoryStatus.DISCONTINUED;
        }
        
        // New items start as RECEIVING
        return InventoryStatus.RECEIVING;
    }
    
    @Override
    @Transactional
    public InventoryItem markForQualityCheck(UUID id, String reason) {
        InventoryItem item = getInventoryItemById(id);
        
        log.info("Marking inventory item {} for quality check. Reason: {}", id, reason);
        
        item.setStatus(InventoryStatus.QUALITY_HOLD);
        item = inventoryItemRepository.save(item);
        
        // Create transaction record
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryItemId(UUID.fromString(item.getId()))
                .warehouseId(UUID.randomUUID()) // TODO: Get actual warehouse ID
                .type(TransactionType.SYSTEM_CORRECTION)
                .quantity(0) // No quantity change
                .userId(getCurrentUserId())
                .notes(reason)
                .build();
        transactionRepository.save(transaction);
        
        return item;
    }

    @Override
    @Transactional
    public InventoryItem quarantineItem(UUID id, String reason) {
        InventoryItem item = getInventoryItemById(id);
        
        log.info("Quarantining inventory item {}. Reason: {}", id, reason);
        
        item.setStatus(InventoryStatus.QUARANTINED);
        item = inventoryItemRepository.save(item);
        
        // Create transaction record
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryItemId(UUID.fromString(item.getId()))
                .warehouseId(UUID.randomUUID()) // TODO: Get actual warehouse ID
                .type(TransactionType.SYSTEM_CORRECTION)
                .quantity(0) // No quantity change
                .userId(getCurrentUserId())
                .notes(reason)
                .build();
        transactionRepository.save(transaction);
        
        return item;
    }

    @Override
    public List<InventoryItem> getItemsByLocation(String location) {
        log.debug("Getting inventory items by location: {}", location);
        return inventoryItemRepository.findByLocation(location);
    }

    @Override
    public List<InventoryItem> getItemsByZone(String zone) {
        log.debug("Getting inventory items by zone: {}", zone);
        return inventoryItemRepository.findByZone(zone);
    }

    /**
     * Get current user ID (placeholder implementation)
     * In a real application, this would get the user from security context
     */
    private UUID getCurrentUserId() {
        // TODO: Implement proper user context retrieval
        return UUID.randomUUID(); // Placeholder
    }

    /**
     * Determines the appropriate status based on inventory levels
     * @param item the inventory item
     * @return the appropriate status
     */
    private InventoryStatus determineStatus(InventoryItem item) {
        if (!item.getIsActive()) {
            return InventoryStatus.DISCONTINUED;
        }
        
        // Items stay AVAILABLE even when out of stock - quantity determines availability
        return InventoryStatus.AVAILABLE;
    }
}
