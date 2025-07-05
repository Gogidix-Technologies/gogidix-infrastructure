package com.exalt.warehousing.fulfillment.repository;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for FulfillmentOrderItem entities
 */
@Repository
public interface FulfillmentOrderItemRepository extends JpaRepository<FulfillmentOrderItem, UUID> {

    /**
     * Find all items for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of items
     */
    List<FulfillmentOrderItem> findAllByFulfillmentOrderId(UUID fulfillmentOrderId);

    /**
     * Find item by order item ID
     *
     * @param orderItemId the order item ID
     * @return the item if found
     */
    Optional<FulfillmentOrderItem> findByOrderItemId(UUID orderItemId);

    /**
     * Find all items by product ID
     *
     * @param productId the product ID
     * @return list of items
     */
    List<FulfillmentOrderItem> findAllByProductId(UUID productId);

    /**
     * Find all items by SKU
     *
     * @param sku the SKU
     * @return list of items
     */
    List<FulfillmentOrderItem> findAllBySku(String sku);

    /**
     * Find all items by status
     *
     * @param status the status
     * @return list of items
     */
    List<FulfillmentOrderItem> findAllByStatus(ItemFulfillmentStatus status);

    /**
     * Find all items for a fulfillment order with a specific status
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param status the status
     * @return list of items
     */
    List<FulfillmentOrderItem> findAllByFulfillmentOrderIdAndStatus(
            UUID fulfillmentOrderId, ItemFulfillmentStatus status);

    /**
     * Find all items that need to be picked for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of items to pick
     */
    @Query("SELECT i FROM FulfillmentOrderItem i WHERE i.fulfillmentOrder.id = :fulfillmentOrderId " +
           "AND (i.status = com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PENDING " +
           "OR i.status = com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.ALLOCATED)")
    List<FulfillmentOrderItem> findItemsToPickForOrder(UUID fulfillmentOrderId);

    /**
     * Find all items that need to be packed for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of items to pack
     */
    @Query("SELECT i FROM FulfillmentOrderItem i WHERE i.fulfillmentOrder.id = :fulfillmentOrderId " +
           "AND i.status = com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PICKED")
    List<FulfillmentOrderItem> findItemsToPackForOrder(UUID fulfillmentOrderId);

    /**
     * Check if all items in a fulfillment order have been picked
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return true if all items have been picked
     */
    @Query("SELECT COUNT(i) = 0 FROM FulfillmentOrderItem i WHERE i.fulfillmentOrder.id = :fulfillmentOrderId " +
           "AND i.status NOT IN (com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PICKED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PACKED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.SHIPPED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.FULFILLED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.CANCELLED)")
    boolean areAllItemsPicked(UUID fulfillmentOrderId);

    /**
     * Check if all items in a fulfillment order have been packed
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return true if all items have been packed
     */
    @Query("SELECT COUNT(i) = 0 FROM FulfillmentOrderItem i WHERE i.fulfillmentOrder.id = :fulfillmentOrderId " +
           "AND i.status NOT IN (com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PACKED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.SHIPPED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.FULFILLED, " +
           "com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.CANCELLED)")
    boolean areAllItemsPacked(UUID fulfillmentOrderId);

    /**
     * Count items with a specific bin location
     *
     * @param binLocation the bin location
     * @return count of items in that location
     */
    long countByBinLocation(String binLocation);

    /**
     * Find items with special instructions
     *
     * @return list of items with special instructions
     */
    @Query("SELECT i FROM FulfillmentOrderItem i WHERE i.specialInstructions IS NOT NULL " +
           "AND LENGTH(i.specialInstructions) > 0")
    List<FulfillmentOrderItem> findItemsWithSpecialInstructions();
}
