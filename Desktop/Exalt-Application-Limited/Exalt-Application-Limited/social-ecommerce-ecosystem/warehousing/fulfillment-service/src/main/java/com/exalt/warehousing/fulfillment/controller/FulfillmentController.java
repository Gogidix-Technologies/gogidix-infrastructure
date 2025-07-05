package com.exalt.warehousing.fulfillment.controller;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.dto.PackingTaskDTO;
import com.exalt.warehousing.fulfillment.dto.PickingTaskDTO;
import com.exalt.warehousing.fulfillment.dto.ShipmentPackageDTO;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.exalt.warehousing.fulfillment.service.FulfillmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;


/**
 * REST controller for fulfillment operations
 */
@RestController
@RequestMapping("/api/v1/fulfillment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fulfillment", description = "Fulfillment API for order fulfillment operations")
public class FulfillmentController {
    
    private final FulfillmentService fulfillmentService;
    
    @GetMapping("/orders/{id}")
    @Operation(summary = "Get fulfillment order by ID", description = "Retrieves a fulfillment order by its ID")
    @ApiResponse(responseCode = "200", description = "Fulfillment order found")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    public ResponseEntity<FulfillmentOrderDTO> getFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.getFulfillmentOrder(UUID.fromString(id)));
    }
    
    @GetMapping("/orders")
    @Operation(summary = "Get all fulfillment orders", description = "Retrieves all fulfillment orders with pagination")
    @ApiResponse(responseCode = "200", description = "Fulfillment orders found")
    public ResponseEntity<Page<FulfillmentOrderDTO>> getAllFulfillmentOrders(Pageable pageable) {
        return ResponseEntity.ok(fulfillmentService.getAllFulfillmentOrders(pageable));
    }
    
    @GetMapping("/orders/status/{status}")
    @Operation(summary = "Get fulfillment orders by status", description = "Retrieves fulfillment orders by status with pagination")
    @ApiResponse(responseCode = "200", description = "Fulfillment orders found")
    public ResponseEntity<Page<FulfillmentOrderDTO>> getFulfillmentOrdersByStatus(
            @Parameter(description = "Fulfillment status") @PathVariable FulfillmentStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(fulfillmentService.getFulfillmentOrdersByStatus(status, pageable));
    }
    
    @GetMapping("/orders/warehouse/{warehouseId}")
    @Operation(summary = "Get fulfillment orders by warehouse", description = "Retrieves fulfillment orders by warehouse with pagination")
    @ApiResponse(responseCode = "200", description = "Fulfillment orders found")
    public ResponseEntity<Page<FulfillmentOrderDTO>> getFulfillmentOrdersByWarehouse(
            @Parameter(description = "Warehouse ID") @PathVariable String warehouseId,
            Pageable pageable) {
        return ResponseEntity.ok(fulfillmentService.getFulfillmentOrdersByWarehouse(UUID.fromString(warehouseId), pageable));
    }
    
    @GetMapping("/orders/active")
    @Operation(summary = "Get active fulfillment orders", description = "Retrieves active fulfillment orders with pagination")
    @ApiResponse(responseCode = "200", description = "Fulfillment orders found")
    public ResponseEntity<Page<FulfillmentOrderDTO>> getActiveFulfillmentOrders(Pageable pageable) {
        return ResponseEntity.ok(fulfillmentService.getActiveFulfillmentOrders(pageable));
    }
    
    @PostMapping("/orders")
    @Operation(summary = "Create fulfillment order", description = "Creates a new fulfillment order")
    @ApiResponse(responseCode = "201", description = "Fulfillment order created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> createFulfillmentOrder(
            @Parameter(description = "Fulfillment order data", required = true) 
            @Valid @RequestBody FulfillmentOrderDTO fulfillmentOrderDTO) {
        return new ResponseEntity<>(fulfillmentService.createFulfillmentOrder(fulfillmentOrderDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/orders/{id}")
    @Operation(summary = "Update fulfillment order", description = "Updates an existing fulfillment order")
    @ApiResponse(responseCode = "200", description = "Fulfillment order updated")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> updateFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id,
            @Parameter(description = "Updated fulfillment order data", required = true) 
            @Valid @RequestBody FulfillmentOrderDTO fulfillmentOrderDTO) {
        return ResponseEntity.ok(fulfillmentService.updateFulfillmentOrder(UUID.fromString(id), fulfillmentOrderDTO));
    }
    
    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Update fulfillment order status", description = "Updates the status of a fulfillment order")
    @ApiResponse(responseCode = "200", description = "Fulfillment order status updated")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> updateFulfillmentOrderStatus(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id,
            @Parameter(description = "New status", required = true) @RequestParam FulfillmentStatus status) {
        return ResponseEntity.ok(fulfillmentService.updateFulfillmentOrderStatus(UUID.fromString(id), status));
    }
    
    @PutMapping("/orders/{id}/warehouse/{warehouseId}")
    @Operation(summary = "Assign fulfillment order to warehouse", description = "Assigns a fulfillment order to a warehouse")
    @ApiResponse(responseCode = "200", description = "Fulfillment order assigned")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> assignToWarehouse(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id,
            @Parameter(description = "Warehouse ID") @PathVariable String warehouseId) {
        return ResponseEntity.ok(fulfillmentService.assignToWarehouse(UUID.fromString(id), UUID.fromString(warehouseId)));
    }
    
    @PutMapping("/orders/{id}/cancel")
    @Operation(summary = "Cancel fulfillment order", description = "Cancels a fulfillment order")
    @ApiResponse(responseCode = "200", description = "Fulfillment order cancelled")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> cancelFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id,
            @Parameter(description = "Cancellation reason", required = true) @RequestParam String reason) {
        return ResponseEntity.ok(fulfillmentService.cancelFulfillmentOrder(UUID.fromString(id), reason));
    }
    
    @PutMapping("/orders/{id}/process")
    @Operation(summary = "Process fulfillment order", description = "Process a fulfillment order (RECEIVED â†’ PROCESSING)")
    @ApiResponse(responseCode = "200", description = "Fulfillment order processed")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> processFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.processFulfillmentOrder(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/allocate")
    @Operation(summary = "Allocate inventory", description = "Allocate inventory for a fulfillment order (PROCESSING â†’ ALLOCATED)")
    @ApiResponse(responseCode = "200", description = "Inventory allocated")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> allocateInventory(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.allocateInventory(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/picking/start")
    @Operation(summary = "Start picking", description = "Start picking for a fulfillment order (ALLOCATED â†’ PICKING)")
    @ApiResponse(responseCode = "200", description = "Picking started")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<List<PickingTaskDTO>> startPicking(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.startPicking(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/picking/complete")
    @Operation(summary = "Complete picking", description = "Complete picking for a fulfillment order (PICKING â†’ PICKING_COMPLETE)")
    @ApiResponse(responseCode = "200", description = "Picking completed")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> completePicking(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.completePicking(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/packing/start")
    @Operation(summary = "Start packing", description = "Start packing for a fulfillment order (PICKING_COMPLETE â†’ PACKING)")
    @ApiResponse(responseCode = "200", description = "Packing started")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<List<PackingTaskDTO>> startPacking(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.startPacking(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/packing/complete")
    @Operation(summary = "Complete packing", description = "Complete packing for a fulfillment order (PACKING â†’ PACKING_COMPLETE)")
    @ApiResponse(responseCode = "200", description = "Packing completed")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> completePacking(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.completePacking(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/ready-to-ship")
    @Operation(summary = "Ready to ship", description = "Mark fulfillment order as ready to ship (PACKING_COMPLETE â†’ READY_TO_SHIP)")
    @ApiResponse(responseCode = "200", description = "Marked as ready to ship")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> readyToShip(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.readyToShip(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/ship")
    @Operation(summary = "Ship order", description = "Ship a fulfillment order (READY_TO_SHIP â†’ SHIPPED)")
    @ApiResponse(responseCode = "200", description = "Order shipped")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<ShipmentPackageDTO> shipFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id,
            @Parameter(description = "Shipment package data", required = true) 
            @Valid @RequestBody ShipmentPackageDTO shipmentPackageDTO) {
        return ResponseEntity.ok(fulfillmentService.shipFulfillmentOrder(UUID.fromString(id), shipmentPackageDTO));
    }
    
    @PutMapping("/orders/{id}/delivered")
    @Operation(summary = "Mark as delivered", description = "Mark fulfillment order as delivered (SHIPPED â†’ DELIVERED)")
    @ApiResponse(responseCode = "200", description = "Marked as delivered")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> markAsDelivered(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.markAsDelivered(UUID.fromString(id)));
    }
    
    @PutMapping("/orders/{id}/complete")
    @Operation(summary = "Complete fulfillment order", description = "Complete a fulfillment order (DELIVERED â†’ COMPLETED)")
    @ApiResponse(responseCode = "200", description = "Fulfillment order completed")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderDTO> completeFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.completeFulfillmentOrder(UUID.fromString(id)));
    }
    
    @GetMapping("/orders/{id}/items")
    @Operation(summary = "Get order items", description = "Retrieves all items for a fulfillment order")
    @ApiResponse(responseCode = "200", description = "Items found")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    public ResponseEntity<List<FulfillmentOrderItemDTO>> getFulfillmentOrderItems(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.getFulfillmentOrderItems(UUID.fromString(id)));
    }
    
    @GetMapping("/items/{id}")
    @Operation(summary = "Get order item", description = "Retrieves a fulfillment order item by ID")
    @ApiResponse(responseCode = "200", description = "Item found")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<FulfillmentOrderItemDTO> getFulfillmentOrderItem(
            @Parameter(description = "Item ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.getFulfillmentOrderItem(UUID.fromString(id)));
    }
    
    @PutMapping("/items/{id}")
    @Operation(summary = "Update order item", description = "Updates a fulfillment order item")
    @ApiResponse(responseCode = "200", description = "Item updated")
    @ApiResponse(responseCode = "404", description = "Item not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderItemDTO> updateFulfillmentOrderItem(
            @Parameter(description = "Item ID") @PathVariable String id,
            @Parameter(description = "Updated item data", required = true) 
            @Valid @RequestBody FulfillmentOrderItemDTO itemDTO) {
        return ResponseEntity.ok(fulfillmentService.updateFulfillmentOrderItem(UUID.fromString(id), itemDTO));
    }
    
    @PutMapping("/items/{id}/status")
    @Operation(summary = "Update item status", description = "Updates the status of a fulfillment order item")
    @ApiResponse(responseCode = "200", description = "Item status updated")
    @ApiResponse(responseCode = "404", description = "Item not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<FulfillmentOrderItemDTO> updateItemStatus(
            @Parameter(description = "Item ID") @PathVariable String id,
            @Parameter(description = "New status", required = true) @RequestParam ItemFulfillmentStatus status) {
        return ResponseEntity.ok(fulfillmentService.updateItemStatus(UUID.fromString(id), status));
    }
    
    @GetMapping("/picking-tasks/order/{id}")
    @Operation(summary = "Get picking tasks", description = "Retrieves all picking tasks for a fulfillment order")
    @ApiResponse(responseCode = "200", description = "Tasks found")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    public ResponseEntity<List<PickingTaskDTO>> getPickingTasks(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.getPickingTasks(UUID.fromString(id)));
    }
    
    @GetMapping("/packing-tasks/order/{id}")
    @Operation(summary = "Get packing tasks", description = "Retrieves all packing tasks for a fulfillment order")
    @ApiResponse(responseCode = "200", description = "Tasks found")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    public ResponseEntity<List<PackingTaskDTO>> getPackingTasks(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.getPackingTasks(UUID.fromString(id)));
    }
    
    @PutMapping("/picking-tasks/{id}/assign/{staffId}")
    @Operation(summary = "Assign picking task", description = "Assigns a picking task to a staff member")
    @ApiResponse(responseCode = "200", description = "Task assigned")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<PickingTaskDTO> assignPickingTask(
            @Parameter(description = "Task ID") @PathVariable String id,
            @Parameter(description = "Staff ID") @PathVariable String staffId) {
        return ResponseEntity.ok(fulfillmentService.assignPickingTask(UUID.fromString(id), UUID.fromString(staffId)));
    }
    
    @PutMapping("/picking-tasks/{id}/start")
    @Operation(summary = "Start picking task", description = "Starts a picking task")
    @ApiResponse(responseCode = "200", description = "Task started")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<PickingTaskDTO> startPickingTask(
            @Parameter(description = "Task ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.startPickingTask(UUID.fromString(id)));
    }
    
    @PutMapping("/picking-tasks/{id}/complete")
    @Operation(summary = "Complete picking task", description = "Completes a picking task")
    @ApiResponse(responseCode = "200", description = "Task completed")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<PickingTaskDTO> completePickingTask(
            @Parameter(description = "Task ID") @PathVariable String id,
            @Parameter(description = "Completion notes") @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(fulfillmentService.completePickingTask(UUID.fromString(id), notes));
    }
    
    @PutMapping("/packing-tasks/{id}/assign/{staffId}")
    @Operation(summary = "Assign packing task", description = "Assigns a packing task to a staff member")
    @ApiResponse(responseCode = "200", description = "Task assigned")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<PackingTaskDTO> assignPackingTask(
            @Parameter(description = "Task ID") @PathVariable String id,
            @Parameter(description = "Staff ID") @PathVariable String staffId) {
        return ResponseEntity.ok(fulfillmentService.assignPackingTask(UUID.fromString(id), UUID.fromString(staffId)));
    }
    
    @PutMapping("/packing-tasks/{id}/start")
    @Operation(summary = "Start packing task", description = "Starts a packing task")
    @ApiResponse(responseCode = "200", description = "Task started")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<PackingTaskDTO> startPackingTask(
            @Parameter(description = "Task ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.startPackingTask(UUID.fromString(id)));
    }
    
    @PutMapping("/packing-tasks/{id}/complete")
    @Operation(summary = "Complete packing task", description = "Completes a packing task")
    @ApiResponse(responseCode = "200", description = "Task completed")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<PackingTaskDTO> completePackingTask(
            @Parameter(description = "Task ID") @PathVariable String id,
            @Parameter(description = "Completion notes") @RequestParam(required = false) String notes,
            @Parameter(description = "Package weight (kg)") @RequestParam Double weightKg,
            @Parameter(description = "Package length (cm)") @RequestParam Double lengthCm,
            @Parameter(description = "Package width (cm)") @RequestParam Double widthCm,
            @Parameter(description = "Package height (cm)") @RequestParam Double heightCm) {
        return ResponseEntity.ok(fulfillmentService.completePackingTask(UUID.fromString(id), notes, weightKg, lengthCm, widthCm, heightCm));
    }
    
    @GetMapping("/orders/{id}/shipping-label")
    @Operation(summary = "Generate shipping label", description = "Generates a shipping label for a fulfillment order")
    @ApiResponse(responseCode = "200", description = "Shipping label generated")
    @ApiResponse(responseCode = "404", description = "Fulfillment order not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<String> generateShippingLabel(
            @Parameter(description = "Fulfillment order ID") @PathVariable String id) {
        return ResponseEntity.ok(fulfillmentService.generateShippingLabel(UUID.fromString(id)));
    }
    
    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Track shipment", description = "Tracks a shipment by tracking number")
    @ApiResponse(responseCode = "200", description = "Tracking information found")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<String> trackShipment(
            @Parameter(description = "Tracking number") @PathVariable String trackingNumber) {
        return ResponseEntity.ok(fulfillmentService.trackShipment(trackingNumber));
    }
} 

