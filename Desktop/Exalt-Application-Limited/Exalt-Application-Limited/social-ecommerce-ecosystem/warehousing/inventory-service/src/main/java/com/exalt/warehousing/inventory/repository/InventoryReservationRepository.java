package com.exalt.warehousing.inventory.repository;

import com.exalt.warehousing.inventory.model.InventoryReservation;
import com.exalt.warehousing.inventory.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for inventory reservation operations
 */
@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    /**
     * Find all active reservations for an inventory item
     * @param inventoryItemId the inventory item ID
     * @return list of active reservations
     */
    @Query("SELECT r FROM InventoryReservation r WHERE r.inventoryItemId = :inventoryItemId " +
           "AND r.status IN (com.ecosystem.warehousing.inventory.model.ReservationStatus.PENDING, " +
           "com.ecosystem.warehousing.inventory.model.ReservationStatus.CONFIRMED)")
    List<InventoryReservation> findActiveReservationsByInventoryItemId(UUID inventoryItemId);

    /**
     * Find all reservations for an order
     * @param orderId the order ID
     * @return list of reservations
     */
    List<InventoryReservation> findAllByOrderId(UUID orderId);

    /**
     * Find all reservations by status
     * @param status the reservation status
     * @return list of matching reservations
     */
    List<InventoryReservation> findAllByStatus(ReservationStatus status);

    /**
     * Find all expired reservations
     * @param currentTime the current time
     * @return list of expired reservations that are still active
     */
    @Query("SELECT r FROM InventoryReservation r WHERE r.expirationTime < :currentTime " +
           "AND r.status IN (com.ecosystem.warehousing.inventory.model.ReservationStatus.PENDING, " +
           "com.ecosystem.warehousing.inventory.model.ReservationStatus.CONFIRMED)")
    List<InventoryReservation> findExpiredReservations(LocalDateTime currentTime);

    /**
     * Update reservation status
     * @param id the reservation ID
     * @param status the new status
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryReservation r SET r.status = :status WHERE r.id = :id")
    int updateStatus(UUID id, ReservationStatus status);

    /**
     * Update all reservations for an order
     * @param orderId the order ID
     * @param status the new status
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryReservation r SET r.status = :status WHERE r.orderId = :orderId")
    int updateStatusByOrderId(UUID orderId, ReservationStatus status);

    /**
     * Extend expiration time
     * @param id the reservation ID
     * @param newExpirationTime the new expiration time
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE InventoryReservation r SET r.expirationTime = :newExpirationTime " +
           "WHERE r.id = :id AND r.status IN (com.ecosystem.warehousing.inventory.model.ReservationStatus.PENDING, " +
           "com.ecosystem.warehousing.inventory.model.ReservationStatus.CONFIRMED)")
    int extendExpirationTime(UUID id, LocalDateTime newExpirationTime);

    /**
     * Get total reserved quantity for an item
     * @param inventoryItemId the inventory item ID
     * @return total reserved quantity
     */
    @Query("SELECT SUM(r.quantity) FROM InventoryReservation r " +
           "WHERE r.inventoryItemId = :inventoryItemId " +
           "AND r.status IN (com.ecosystem.warehousing.inventory.model.ReservationStatus.PENDING, " +
           "com.ecosystem.warehousing.inventory.model.ReservationStatus.CONFIRMED)")
    Integer getTotalReservedQuantity(UUID inventoryItemId);

    /**
     * Get total reserved quantity for an item at a specific warehouse
     * @param inventoryItemId the inventory item ID
     * @param warehouseId the warehouse ID
     * @return total reserved quantity
     */
    @Query("SELECT SUM(r.quantity) FROM InventoryReservation r " +
           "WHERE r.inventoryItemId = :inventoryItemId " +
           "AND r.warehouseId = :warehouseId " +
           "AND r.status IN (com.ecosystem.warehousing.inventory.model.ReservationStatus.PENDING, " +
           "com.ecosystem.warehousing.inventory.model.ReservationStatus.CONFIRMED)")
    Integer getTotalReservedQuantityAtWarehouse(UUID inventoryItemId, UUID warehouseId);
}
