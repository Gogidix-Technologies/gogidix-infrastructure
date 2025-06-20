package com.exalt.warehousing.fulfillment.service;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**
 * Service interface for fulfillment order management
 */
public interface FulfillmentOrderService {

    /**
     * Create a new fulfillment order
     *
     * @param fulfillmentOrder the fulfillment order to create
     * @return the created fulfillment order
     */
    FulfillmentOrder createFulfillmentOrder(FulfillmentOrder fulfillmentOrder);

    /**
     * Get a fulfillment order by ID
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return the fulfillment order if found
     */
    FulfillmentOrder getFulfillmentOrder(String fulfillmentOrderId);

    /**
     * Get fulfillment orders by order ID
     *
     * @param orderId the order ID
     * @return list of fulfillment orders for the given order
     */
    List<FulfillmentOrder> getFulfillmentOrdersByOrderId(String orderId);

    /**
     * Update fulfillment order status
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param status the new status
     * @return the updated fulfillment order
     */
    FulfillmentOrder updateFulfillmentOrderStatus(String fulfillmentOrderId, FulfillmentStatus status);

    /**
     * Update item status
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param itemId the order item ID
     * @param status the new status
     * @return the updated fulfillment order
     */
    FulfillmentOrder updateItemStatus(String fulfillmentOrderId, String itemId, ItemFulfillmentStatus status);

    /**
     * Process pending fulfillment orders
     *
     * @return number of orders processed
     */
    int processPendingOrders();

    /**
     * Cancel a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param reason the cancellation reason
     * @return the cancelled fulfillment order
     */
    FulfillmentOrder cancelFulfillmentOrder(String fulfillmentOrderId, String reason);

    /**
     * Get fulfillment orders by status
     *
     * @param status the fulfillment status
     * @return list of fulfillment orders with the given status
     */
    List<FulfillmentOrder> getFulfillmentOrdersByStatus(FulfillmentStatus status);

    /**
     * Get fulfillment orders by warehouse
     *
     * @param warehouseId the warehouse ID
     * @return list of fulfillment orders for the given warehouse
     */
    List<FulfillmentOrder> getFulfillmentOrdersByWarehouse(Long warehouseId);

    /**
     * Get fulfillment statistics for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return map of statistics
     */
    Map<String, Object> getFulfillmentStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Split a fulfillment order into multiple orders (for multi-warehouse fulfillment)
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of new fulfillment orders created
     */
    List<FulfillmentOrder> splitFulfillmentOrder(String fulfillmentOrderId);

    /**
     * Assign a fulfillment order to a warehouse
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param warehouseId the warehouse ID
     * @return the updated fulfillment order
     */
    FulfillmentOrder assignToWarehouse(String fulfillmentOrderId, Long warehouseId);
    
    /**
     * Process the next stage in a fulfillment order's lifecycle after inventory allocation
     *
     * @param fulfillmentOrder the fulfillment order to process
     */
    void proceedToNextFulfillmentStage(FulfillmentOrder fulfillmentOrder);
    
    /**
     * Handle inventory reservation cancellation
     *
     * @param fulfillmentOrder the fulfillment order
     * @param reason the cancellation reason
     */
    void handleInventoryCancellation(FulfillmentOrder fulfillmentOrder, String reason);
    
    /**
     * Handle inventory reservation expiration
     *
     * @param fulfillmentOrder the fulfillment order
     */
    void handleInventoryExpiration(FulfillmentOrder fulfillmentOrder);
}

