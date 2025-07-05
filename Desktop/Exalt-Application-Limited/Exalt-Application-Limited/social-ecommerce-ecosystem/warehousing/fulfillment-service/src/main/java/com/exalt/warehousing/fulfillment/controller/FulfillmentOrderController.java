package com.exalt.warehousing.fulfillment.controller;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.dto.StatusUpdateDTO;
import com.exalt.warehousing.fulfillment.dto.WarehouseAssignmentDTO;
import com.exalt.warehousing.fulfillment.mapper.manual.FulfillmentOrderManualMapper;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.exalt.warehousing.fulfillment.service.FulfillmentOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing fulfillment orders
 */
@RestController
@RequestMapping("/api/fulfillment/orders")
@RequiredArgsConstructor
@Tag(name = "Fulfillment Orders", description = "Endpoints for managing fulfillment orders")
public class FulfillmentOrderController {

    private final FulfillmentOrderService fulfillmentOrderService;
    private final FulfillmentOrderManualMapper fulfillmentOrderMapper;

    @PostMapping
    @Operation(summary = "Create a new fulfillment order")
    public ResponseEntity<FulfillmentOrderDTO> createFulfillmentOrder(
            @Valid @RequestBody FulfillmentOrderDTO fulfillmentOrderDTO) {
        FulfillmentOrder order = fulfillmentOrderMapper.toEntity(fulfillmentOrderDTO);
        FulfillmentOrder savedOrder = fulfillmentOrderService.createFulfillmentOrder(order);
        return new ResponseEntity<>(fulfillmentOrderMapper.toDTO(savedOrder), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a fulfillment order by ID")
    public ResponseEntity<FulfillmentOrderDTO> getFulfillmentOrder(@PathVariable UUID id) {
        FulfillmentOrder order = fulfillmentOrderService.getFulfillmentOrder(id.toString());
        return ResponseEntity.ok(fulfillmentOrderMapper.toDTO(order));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get fulfillment orders by order ID")
    public ResponseEntity<List<FulfillmentOrderDTO>> getFulfillmentOrdersByOrderId(@PathVariable UUID orderId) {
        List<FulfillmentOrder> orders = fulfillmentOrderService.getFulfillmentOrdersByOrderId(orderId.toString());
        List<FulfillmentOrderDTO> orderDTOs = orders.stream()
                .map(fulfillmentOrderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update fulfillment order status")
    public ResponseEntity<FulfillmentOrderDTO> updateFulfillmentOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateDTO statusUpdate) {
        FulfillmentStatus status = statusUpdate.getStatus();
        FulfillmentOrder order = fulfillmentOrderService.updateFulfillmentOrderStatus(id.toString(), status);
        return ResponseEntity.ok(fulfillmentOrderMapper.toDTO(order));
    }

    @PutMapping("/{id}/items/{itemId}/status")
    @Operation(summary = "Update item status in a fulfillment order")
    public ResponseEntity<FulfillmentOrderDTO> updateItemStatus(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @Valid @RequestBody StatusUpdateDTO statusUpdate) {
        ItemFulfillmentStatus status = statusUpdate.getItemStatus();
        FulfillmentOrder order = fulfillmentOrderService.updateItemStatus(id.toString(), itemId.toString(), status);
        return ResponseEntity.ok(fulfillmentOrderMapper.toDTO(order));
    }

    @PostMapping("/process-pending")
    @Operation(summary = "Process pending fulfillment orders")
    public ResponseEntity<Map<String, Integer>> processPendingOrders() {
        int processed = fulfillmentOrderService.processPendingOrders();
        return ResponseEntity.ok(Map.of("processed", processed));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a fulfillment order")
    public ResponseEntity<FulfillmentOrderDTO> cancelFulfillmentOrder(
            @PathVariable UUID id,
            @RequestParam String reason) {
        FulfillmentOrder order = fulfillmentOrderService.cancelFulfillmentOrder(id.toString(), reason);
        return ResponseEntity.ok(fulfillmentOrderMapper.toDTO(order));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get fulfillment orders by status")
    public ResponseEntity<List<FulfillmentOrderDTO>> getFulfillmentOrdersByStatus(
            @PathVariable FulfillmentStatus status) {
        List<FulfillmentOrder> orders = fulfillmentOrderService.getFulfillmentOrdersByStatus(status);
        List<FulfillmentOrderDTO> orderDTOs = orders.stream()
                .map(fulfillmentOrderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get fulfillment orders by warehouse")
    public ResponseEntity<List<FulfillmentOrderDTO>> getFulfillmentOrdersByWarehouse(
            @PathVariable UUID warehouseId) {
        List<FulfillmentOrder> orders = fulfillmentOrderService.getFulfillmentOrdersByWarehouse(Long.valueOf(warehouseId.hashCode() & Integer.MAX_VALUE));
        List<FulfillmentOrderDTO> orderDTOs = orders.stream()
                .map(fulfillmentOrderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get fulfillment statistics for a date range")
    public ResponseEntity<Map<String, Object>> getFulfillmentStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> statistics = fulfillmentOrderService.getFulfillmentStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/{id}/split")
    @Operation(summary = "Split a fulfillment order into multiple orders for multi-warehouse fulfillment")
    public ResponseEntity<List<FulfillmentOrderDTO>> splitFulfillmentOrder(@PathVariable UUID id) {
        List<FulfillmentOrder> orders = fulfillmentOrderService.splitFulfillmentOrder(id.toString());
        List<FulfillmentOrderDTO> orderDTOs = orders.stream()
                .map(fulfillmentOrderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @PutMapping("/{id}/assign-warehouse")
    @Operation(summary = "Assign a fulfillment order to a warehouse")
    public ResponseEntity<FulfillmentOrderDTO> assignToWarehouse(
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseAssignmentDTO assignment) {
        FulfillmentOrder order = fulfillmentOrderService.assignToWarehouse(id.toString(), Long.valueOf(assignment.getWarehouseId().hashCode() & Integer.MAX_VALUE));
        return ResponseEntity.ok(fulfillmentOrderMapper.toDTO(order));
    }
} 
