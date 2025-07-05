package com.exalt.warehousing.management.repository;

import com.exalt.warehousing.management.model.OrderItemReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for accessing order items from the warehouse management service
 * This is a simplified interface for integration with the Order Service
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemReference, String> {

    /**
     * Find all order items by order ID
     *
     * @param orderId the order ID
     * @return list of order items for the order
     */
    List<OrderItemReference> findAllByOrderId(UUID orderId);

    /**
     * Find all item IDs associated with an order
     *
     * @param orderId the order ID
     * @return list of inventory item IDs in this order
     */
    @Query("SELECT o.inventoryItemId FROM OrderItemReference o WHERE o.orderId = :orderId")
    List<UUID> findItemIdsByOrderId(UUID orderId);

    /**
     * Find the order ID associated with an item
     *
     * @param itemId the inventory item ID
     * @return the order ID this item belongs to
     */
    @Query("SELECT o.orderId FROM OrderItemReference o WHERE o.inventoryItemId = :itemId")
    UUID findOrderIdByItemId(UUID itemId);

    /**
     * Find all orders that contain items in a specific location
     *
     * @param locationId the location ID
     * @return list of order IDs that have items in this location
     */
    @Query("SELECT DISTINCT o.orderId FROM OrderItemReference o " +
           "JOIN InventoryItemReference i ON o.inventoryItemId = i.id " +
           "WHERE i.locationId = :locationId")
    List<UUID> findOrdersByLocationId(UUID locationId);

    /**
     * Find all orders that contain items in a specific zone
     *
     * @param zoneId the zone ID
     * @return list of order IDs that have items in this zone
     */
    @Query("SELECT DISTINCT o.orderId FROM OrderItemReference o " +
           "JOIN InventoryItemReference i ON o.inventoryItemId = i.id " +
           "JOIN Location l ON i.locationId = l.id " +
           "WHERE l.zoneId = :zoneId")
    List<UUID> findOrdersByZoneId(UUID zoneId);
} 
