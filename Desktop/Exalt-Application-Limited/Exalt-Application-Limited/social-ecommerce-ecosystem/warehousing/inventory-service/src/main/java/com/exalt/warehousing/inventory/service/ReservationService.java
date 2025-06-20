package com.exalt.warehousing.inventory.service;

import java.util.Optional;

import com.exalt.warehousing.inventory.model.InventoryReservation;
import com.exalt.warehousing.inventory.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing inventory reservations
 */
public interface ReservationService {

    /**
     * Create a new inventory reservation
     * @param inventoryItemId the inventory item ID
     * @param warehouseId the warehouse ID (optional)
     * @param quantity the quantity to reserve
     * @param orderId the order ID
     * @param expirationMinutes expiration time in minutes
     * @return the created reservation
     */
    InventoryReservation createReservation(UUID inventoryItemId, UUID warehouseId, 
                                         int quantity, UUID orderId, int expirationMinutes);

    /**
     * Get a reservation by ID
     * @param id the reservation ID
     * @return the reservation
     */
    InventoryReservation getReservationById(UUID id);

    /**
     * Get all reservations for an order
     * @param orderId the order ID
     * @return list of reservations
     */
    List<InventoryReservation> getReservationsByOrderId(UUID orderId);

    /**
     * Get all active reservations for an inventory item
     * @param inventoryItemId the inventory item ID
     * @return list of active reservations
     */
    List<InventoryReservation> getActiveReservationsByItemId(UUID inventoryItemId);

    /**
     * Update reservation status
     * @param id the reservation ID
     * @param status the new status
     * @return the updated reservation
     */
    InventoryReservation updateReservationStatus(UUID id, ReservationStatus status);

    /**
     * Update all reservations for an order
     * @param orderId the order ID
     * @param status the new status
     * @return the number of updated reservations
     */
    int updateReservationStatusByOrderId(UUID orderId, ReservationStatus status);

    /**
     * Extend reservation expiration time
     * @param id the reservation ID
     * @param minutes additional minutes
     * @return the updated reservation
     */
    InventoryReservation extendReservation(UUID id, int minutes);

    /**
     * Complete reservation and convert to sales transaction
     * @param orderId the order ID
     * @param userId the user processing the order
     * @return true if successful
     */
    boolean completeReservation(UUID orderId, UUID userId);

    /**
     * Cancel reservation and release inventory
     * @param orderId the order ID
     * @return true if successful
     */
    boolean cancelReservation(UUID orderId);

    /**
     * Process expired reservations
     * @param currentTime the current time
     * @return number of processed reservations
     */
    int processExpiredReservations(LocalDateTime currentTime);

    /**
     * Check if there are any active reservations for an order
     * @param orderId the order ID
     * @return true if active reservations exist
     */
    boolean hasActiveReservations(UUID orderId);

    /**
     * Get total reserved quantity for an item
     * @param inventoryItemId the inventory item ID
     * @return total reserved quantity
     */
    int getTotalReservedQuantity(UUID inventoryItemId);

    /**
     * Get total reserved quantity for an item at a specific warehouse
     * @param inventoryItemId the inventory item ID
     * @param warehouseId the warehouse ID
     * @return total reserved quantity
     */
    int getTotalReservedQuantityAtWarehouse(UUID inventoryItemId, UUID warehouseId);
}

