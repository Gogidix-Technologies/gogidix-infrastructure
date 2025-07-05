package com.exalt.warehousing.inventory.repository;

import com.exalt.warehousing.inventory.model.InventoryAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for inventory allocation operations
 */
@Repository
public interface InventoryAllocationRepository extends JpaRepository<InventoryAllocation, UUID> {

    /**
     * Find allocation by inventory item and warehouse
     * @param inventoryItemId the inventory item ID
     * @param warehouseId the warehouse ID
     * @return the allocation if found
     */
    Optional<InventoryAllocation> findByInventoryItemIdAndWarehouseId(
            UUID inventoryItemId, UUID warehouseId);

    /**
     * Find all allocations for an inventory item
     * @param inventoryItemId the inventory item ID
     * @return list of allocations across warehouses
     */
    List<InventoryAllocation> findAllByInventoryItemId(UUID inventoryItemId);

    /**
     * Find all allocations in a warehouse
     * @param warehouseId the warehouse ID
     * @return list of all inventory in the warehouse
     */
    List<InventoryAllocation> findAllByWarehouseId(UUID warehouseId);

    /**
     * Find allocations with available stock for an item
     * @param inventoryItemId the inventory item ID
     * @return list of allocations with positive available quantity
     */
    @Query("SELECT a FROM InventoryAllocation a WHERE a.inventoryItemId = :inventoryItemId " +
           "AND (a.quantity - a.reservedQuantity) > 0")
    List<InventoryAllocation> findAvailableAllocations(UUID inventoryItemId);

    /**
     * Reserve inventory at a specific warehouse
     * @param id the allocation ID
     * @param amount the amount to reserve
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryAllocation a SET a.reservedQuantity = a.reservedQuantity + :amount " +
           "WHERE a.id = :id AND (a.quantity - a.reservedQuantity) >= :amount")
    int reserveInventory(UUID id, int amount);

    /**
     * Release previously reserved inventory
     * @param id the allocation ID
     * @param amount the amount to release
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryAllocation a SET a.reservedQuantity = a.reservedQuantity - :amount " +
           "WHERE a.id = :id AND a.reservedQuantity >= :amount")
    int releaseReservation(UUID id, int amount);

    /**
     * Commit a reservation by reducing both total and reserved quantity
     * @param id the allocation ID
     * @param amount the amount to commit
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryAllocation a SET " +
           "a.quantity = a.quantity - :amount, " +
           "a.reservedQuantity = a.reservedQuantity - :amount " +
           "WHERE a.id = :id AND a.reservedQuantity >= :amount")
    int commitReservation(UUID id, int amount);

    /**
     * Add inventory to a warehouse
     * @param id the allocation ID
     * @param amount the amount to add
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryAllocation a SET a.quantity = a.quantity + :amount " +
           "WHERE a.id = :id")
    int addInventory(UUID id, int amount);

    /**
     * Find allocations with low or zero stock
     * @param threshold minimum acceptable stock level
     * @return list of allocations with low stock
     */
    @Query("SELECT a FROM InventoryAllocation a WHERE " +
           "(a.quantity - a.reservedQuantity) <= :threshold")
    List<InventoryAllocation> findLowStockAllocations(int threshold);
}
