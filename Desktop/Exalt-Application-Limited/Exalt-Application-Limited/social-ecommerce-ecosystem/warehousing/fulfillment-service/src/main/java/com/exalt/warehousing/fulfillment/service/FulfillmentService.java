package com.exalt.warehousing.fulfillment.service;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.dto.FulfillmentRequest;
import com.exalt.warehousing.fulfillment.dto.FulfillmentResult;
import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.dto.PackingTaskDTO;
import com.exalt.warehousing.fulfillment.dto.PickingTaskDTO;
import com.exalt.warehousing.fulfillment.dto.ShipmentPackageDTO;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for fulfillment operations
 */
public interface FulfillmentService {
    
    /**
     * Get a fulfillment order by ID
     *
     * @param id the fulfillment order ID
     * @return the fulfillment order
     */
    FulfillmentOrderDTO getFulfillmentOrder(UUID id);
    
    /**
     * Get a fulfillment order by order ID
     *
     * @param orderId the order ID
     * @return the fulfillment order
     */
    FulfillmentOrderDTO getFulfillmentOrderByOrderId(UUID orderId);
    
    /**
     * Get all fulfillment orders
     *
     * @param pageable pagination information
     * @return page of fulfillment orders
     */
    Page<FulfillmentOrderDTO> getAllFulfillmentOrders(Pageable pageable);
    
    /**
     * Get all fulfillment orders by status
     *
     * @param status the status
     * @param pageable pagination information
     * @return page of fulfillment orders
     */
    Page<FulfillmentOrderDTO> getFulfillmentOrdersByStatus(FulfillmentStatus status, Pageable pageable);
    
    /**
     * Get all fulfillment orders assigned to a warehouse
     *
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return page of fulfillment orders
     */
    Page<FulfillmentOrderDTO> getFulfillmentOrdersByWarehouse(UUID warehouseId, Pageable pageable);
    
    /**
     * Get all active fulfillment orders (not completed, delivered, or cancelled)
     *
     * @param pageable pagination information
     * @return page of active fulfillment orders
     */
    Page<FulfillmentOrderDTO> getActiveFulfillmentOrders(Pageable pageable);
    
    /**
     * Create a new fulfillment order
     *
     * @param fulfillmentOrderDTO the fulfillment order to create
     * @return the created fulfillment order
     */
    FulfillmentOrderDTO createFulfillmentOrder(FulfillmentOrderDTO fulfillmentOrderDTO);
    
    /**
     * Update a fulfillment order
     *
     * @param id the fulfillment order ID
     * @param fulfillmentOrderDTO the updated fulfillment order data
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO updateFulfillmentOrder(UUID id, FulfillmentOrderDTO fulfillmentOrderDTO);
    
    /**
     * Update the status of a fulfillment order
     *
     * @param id the fulfillment order ID
     * @param status the new status
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO updateFulfillmentOrderStatus(UUID id, FulfillmentStatus status);
    
    /**
     * Assign a fulfillment order to a warehouse
     *
     * @param id the fulfillment order ID
     * @param warehouseId the warehouse ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO assignToWarehouse(UUID id, UUID warehouseId);
    
    /**
     * Cancel a fulfillment order
     *
     * @param id the fulfillment order ID
     * @param reason the cancellation reason
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO cancelFulfillmentOrder(UUID id, String reason);
    
    /**
     * Process a fulfillment order - transition from RECEIVED to PROCESSING
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO processFulfillmentOrder(UUID id);
    
    /**
     * Allocate inventory for a fulfillment order - transition from PROCESSING to ALLOCATED
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO allocateInventory(UUID id);
    
    /**
     * Start picking for a fulfillment order - transition from ALLOCATED to PICKING
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order and created picking tasks
     */
    List<PickingTaskDTO> startPicking(UUID id);
    
    /**
     * Complete picking for a fulfillment order - transition from PICKING to PICKING_COMPLETE
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO completePicking(UUID id);
    
    /**
     * Start packing for a fulfillment order - transition from PICKING_COMPLETE to PACKING
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order and created packing tasks
     */
    List<PackingTaskDTO> startPacking(UUID id);
    
    /**
     * Complete packing for a fulfillment order - transition from PACKING to PACKING_COMPLETE
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO completePacking(UUID id);
    
    /**
     * Mark a fulfillment order as ready to ship - transition from PACKING_COMPLETE to READY_TO_SHIP
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO readyToShip(UUID id);
    
    /**
     * Ship a fulfillment order - transition from READY_TO_SHIP to SHIPPED
     *
     * @param id the fulfillment order ID
     * @param shipmentPackageDTO the shipment package information
     * @return the updated fulfillment order and shipment package
     */
    ShipmentPackageDTO shipFulfillmentOrder(UUID id, ShipmentPackageDTO shipmentPackageDTO);
    
    /**
     * Mark a fulfillment order as delivered - transition from SHIPPED to DELIVERED
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO markAsDelivered(UUID id);
    
    /**
     * Complete a fulfillment order - transition from DELIVERED to COMPLETED
     *
     * @param id the fulfillment order ID
     * @return the updated fulfillment order
     */
    FulfillmentOrderDTO completeFulfillmentOrder(UUID id);
    
    /**
     * Get all items for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of items
     */
    List<FulfillmentOrderItemDTO> getFulfillmentOrderItems(UUID fulfillmentOrderId);
    
    /**
     * Get fulfillment order item by ID
     *
     * @param itemId the item ID
     * @return the fulfillment order item
     */
    FulfillmentOrderItemDTO getFulfillmentOrderItem(UUID itemId);
    
    /**
     * Update a fulfillment order item
     *
     * @param itemId the item ID
     * @param itemDTO the updated item data
     * @return the updated item
     */
    FulfillmentOrderItemDTO updateFulfillmentOrderItem(UUID itemId, FulfillmentOrderItemDTO itemDTO);
    
    /**
     * Update the status of a fulfillment order item
     *
     * @param itemId the item ID
     * @param status the new status
     * @return the updated item
     */
    FulfillmentOrderItemDTO updateItemStatus(UUID itemId, ItemFulfillmentStatus status);
    
    /**
     * Get all orders ready for picking
     *
     * @return list of orders ready for picking
     */
    List<FulfillmentOrderDTO> getOrdersReadyForPicking();
    
    /**
     * Get all orders ready for packing
     *
     * @return list of orders ready for packing
     */
    List<FulfillmentOrderDTO> getOrdersReadyForPacking();
    
    /**
     * Get all picking tasks for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of picking tasks
     */
    List<PickingTaskDTO> getPickingTasks(UUID fulfillmentOrderId);
    
    /**
     * Get all packing tasks for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of packing tasks
     */
    List<PackingTaskDTO> getPackingTasks(UUID fulfillmentOrderId);
    
    /**
     * Assign a picking task to a staff member
     *
     * @param taskId the picking task ID
     * @param staffId the staff member ID
     * @return the updated picking task
     */
    PickingTaskDTO assignPickingTask(UUID taskId, UUID staffId);
    
    /**
     * Start a picking task
     *
     * @param taskId the picking task ID
     * @return the updated picking task
     */
    PickingTaskDTO startPickingTask(UUID taskId);
    
    /**
     * Complete a picking task
     *
     * @param taskId the picking task ID
     * @param notes completion notes
     * @return the updated picking task
     */
    PickingTaskDTO completePickingTask(UUID taskId, String notes);
    
    /**
     * Assign a packing task to a staff member
     *
     * @param taskId the packing task ID
     * @param staffId the staff member ID
     * @return the updated packing task
     */
    PackingTaskDTO assignPackingTask(UUID taskId, UUID staffId);
    
    /**
     * Start a packing task
     *
     * @param taskId the packing task ID
     * @return the updated packing task
     */
    PackingTaskDTO startPackingTask(UUID taskId);
    
    /**
     * Complete a packing task
     *
     * @param taskId the packing task ID
     * @param notes completion notes
     * @param weightKg package weight
     * @param lengthCm package length
     * @param widthCm package width
     * @param heightCm package height
     * @return the updated packing task
     */
    PackingTaskDTO completePackingTask(UUID taskId, String notes, Double weightKg,
                                      Double lengthCm, Double widthCm, Double heightCm);
    
    /**
     * Generate a shipping label for a fulfillment order
     *
     * @param id the fulfillment order ID
     * @return the shipping label
     */
    String generateShippingLabel(UUID id);
    
    /**
     * Track a shipment by tracking number
     *
     * @param trackingNumber the tracking number
     * @return the tracking information
     */
    String trackShipment(String trackingNumber);

    /**
     * Process fulfillment for a customer request
     *
     * @param request the fulfillment request
     * @return the fulfillment result
     */
    FulfillmentResult processFulfillment(FulfillmentRequest request);
    
    /**
     * Update the status of a fulfillment order
     *
     * @param fulfillmentId the fulfillment ID
     * @param status the new status
     */
    void updateFulfillmentStatus(String fulfillmentId, String status);

    /**
     * Check active orders for a specific inventory item
     * This is used when inventory changes are detected via events
     *
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     * @return number of orders affected
     */
    int checkActiveOrdersForItem(String sku, UUID warehouseId);

    /**
     * Handle low stock alerts
     * This is triggered when inventory levels fall below thresholds
     *
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     */
    void handleLowStockAlert(String sku, UUID warehouseId);
    
    /**
     * Reassign orders from a warehouse
     * This is used when a warehouse becomes unavailable
     *
     * @param warehouseId the warehouse ID
     * @return number of orders reassigned
     */
    int reassignOrdersFromWarehouse(UUID warehouseId);
} 
