package com.exalt.warehousing.inventory.controller;

import java.util.Optional;
import java.util.Map;

import com.exalt.warehousing.inventory.dto.InventoryReservationDTO;
import com.exalt.warehousing.inventory.model.InventoryReservation;
import com.exalt.warehousing.inventory.model.ReservationStatus;
import com.exalt.warehousing.inventory.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for inventory reservation operations
 */
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Reservations", description = "APIs for managing inventory reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new inventory reservation", description = "Creates a new reservation for inventory items")
    @ApiResponse(responseCode = "201", description = "Reservation created", content = @Content(schema = @Schema(implementation = InventoryReservationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Insufficient inventory")
    public ResponseEntity<InventoryReservationDTO> createReservation(
            @Parameter(description = "Inventory item ID", required = true) @RequestParam UUID inventoryItemId,
            @Parameter(description = "Warehouse ID (optional)") @RequestParam(required = false) UUID warehouseId,
            @Parameter(description = "Quantity to reserve", required = true) @RequestParam int quantity,
            @Parameter(description = "Order ID", required = true) @RequestParam UUID orderId,
            @Parameter(description = "Expiration time in minutes", required = true) @RequestParam(defaultValue = "30") int expirationMinutes) {
        log.debug("REST request to create reservation for item: {}, order: {}", inventoryItemId, orderId);
        
        InventoryReservation reservation = reservationService.createReservation(
                inventoryItemId, warehouseId, quantity, orderId, expirationMinutes);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(reservation));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID", description = "Retrieves detailed information about a specific reservation")
    @ApiResponse(responseCode = "200", description = "Reservation found", content = @Content(schema = @Schema(implementation = InventoryReservationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<InventoryReservationDTO> getReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable UUID id) {
        log.debug("REST request to get reservation with ID: {}", id);
        InventoryReservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(convertToDto(reservation));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get reservations by order ID", description = "Retrieves all reservations for a specific order")
    @ApiResponse(responseCode = "200", description = "List of reservations returned")
    public ResponseEntity<List<InventoryReservationDTO>> getReservationsByOrderId(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID orderId) {
        log.debug("REST request to get reservations for order: {}", orderId);
        
        List<InventoryReservation> reservations = reservationService.getReservationsByOrderId(orderId);
        List<InventoryReservationDTO> dtos = reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/item/{inventoryItemId}/active")
    @Operation(summary = "Get active reservations by item ID", description = "Retrieves all active reservations for a specific inventory item")
    @ApiResponse(responseCode = "200", description = "List of active reservations returned")
    public ResponseEntity<List<InventoryReservationDTO>> getActiveReservationsByItemId(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID inventoryItemId) {
        log.debug("REST request to get active reservations for item: {}", inventoryItemId);
        
        List<InventoryReservation> reservations = reservationService.getActiveReservationsByItemId(inventoryItemId);
        List<InventoryReservationDTO> dtos = reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update reservation status", description = "Updates the status of a reservation")
    @ApiResponse(responseCode = "200", description = "Reservation status updated", content = @Content(schema = @Schema(implementation = InventoryReservationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<InventoryReservationDTO> updateReservationStatus(
            @Parameter(description = "Reservation ID", required = true) @PathVariable UUID id,
            @Parameter(description = "New status", required = true) @RequestParam ReservationStatus status) {
        log.debug("REST request to update status of reservation ID: {} to {}", id, status);
        
        InventoryReservation updatedReservation = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(convertToDto(updatedReservation));
    }

    @PatchMapping("/order/{orderId}/status")
    @Operation(summary = "Update status of all reservations for an order", description = "Updates the status of all reservations for a specific order")
    @ApiResponse(responseCode = "200", description = "Reservation statuses updated")
    public ResponseEntity<Integer> updateReservationStatusByOrderId(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID orderId,
            @Parameter(description = "New status", required = true) @RequestParam ReservationStatus status) {
        log.debug("REST request to update status of all reservations for order: {} to {}", orderId, status);
        
        int updatedCount = reservationService.updateReservationStatusByOrderId(orderId, status);
        return ResponseEntity.ok(updatedCount);
    }

    @PatchMapping("/{id}/extend")
    @Operation(summary = "Extend reservation expiration time", description = "Extends the expiration time of a reservation")
    @ApiResponse(responseCode = "200", description = "Reservation extended", content = @Content(schema = @Schema(implementation = InventoryReservationDTO.class)))
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<InventoryReservationDTO> extendReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Additional minutes", required = true) @RequestParam int minutes) {
        log.debug("REST request to extend reservation ID: {} by {} minutes", id, minutes);
        
        InventoryReservation extendedReservation = reservationService.extendReservation(id, minutes);
        return ResponseEntity.ok(convertToDto(extendedReservation));
    }

    @PostMapping("/order/{orderId}/complete")
    @Operation(summary = "Complete reservation", description = "Completes the reservation and converts it to a sales transaction")
    @ApiResponse(responseCode = "200", description = "Reservation completed")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<Boolean> completeReservation(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID orderId,
            @Parameter(description = "User ID", required = true) @RequestParam UUID userId) {
        log.debug("REST request to complete reservation for order: {}", orderId);
        
        boolean completed = reservationService.completeReservation(orderId, userId);
        return ResponseEntity.ok(completed);
    }

    @PostMapping("/order/{orderId}/cancel")
    @Operation(summary = "Cancel reservation", description = "Cancels the reservation and releases the inventory")
    @ApiResponse(responseCode = "200", description = "Reservation cancelled")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<Boolean> cancelReservation(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID orderId) {
        log.debug("REST request to cancel reservation for order: {}", orderId);
        
        boolean cancelled = reservationService.cancelReservation(orderId);
        return ResponseEntity.ok(cancelled);
    }

    @PostMapping("/process-expired")
    @Operation(summary = "Process expired reservations", description = "Processes all expired reservations and releases the inventory")
    @ApiResponse(responseCode = "200", description = "Expired reservations processed")
    public ResponseEntity<Integer> processExpiredReservations() {
        log.debug("REST request to process expired reservations");
        
        int processedCount = reservationService.processExpiredReservations(LocalDateTime.now());
        return ResponseEntity.ok(processedCount);
    }

    @GetMapping("/order/{orderId}/has-active")
    @Operation(summary = "Check if order has active reservations", description = "Checks if there are any active reservations for an order")
    @ApiResponse(responseCode = "200", description = "Check result returned")
    public ResponseEntity<Boolean> hasActiveReservations(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID orderId) {
        log.debug("REST request to check if order has active reservations: {}", orderId);
        
        boolean hasActive = reservationService.hasActiveReservations(orderId);
        return ResponseEntity.ok(hasActive);
    }

    @GetMapping("/item/{inventoryItemId}/total-reserved")
    @Operation(summary = "Get total reserved quantity for an item", description = "Retrieves the total reserved quantity for a specific inventory item")
    @ApiResponse(responseCode = "200", description = "Total reserved quantity returned")
    public ResponseEntity<Integer> getTotalReservedQuantity(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID inventoryItemId) {
        log.debug("REST request to get total reserved quantity for item: {}", inventoryItemId);
        
        int totalReserved = reservationService.getTotalReservedQuantity(inventoryItemId);
        return ResponseEntity.ok(totalReserved);
    }

    @GetMapping("/item/{inventoryItemId}/warehouse/{warehouseId}/total-reserved")
    @Operation(summary = "Get total reserved quantity for an item at a warehouse", 
               description = "Retrieves the total reserved quantity for a specific inventory item at a specific warehouse")
    @ApiResponse(responseCode = "200", description = "Total reserved quantity returned")
    public ResponseEntity<Integer> getTotalReservedQuantityAtWarehouse(
            @Parameter(description = "Inventory item ID", required = true) @PathVariable UUID inventoryItemId,
            @Parameter(description = "Warehouse ID", required = true) @PathVariable UUID warehouseId) {
        log.debug("REST request to get total reserved quantity for item: {} at warehouse: {}", 
                 inventoryItemId, warehouseId);
        
        int totalReserved = reservationService.getTotalReservedQuantityAtWarehouse(inventoryItemId, warehouseId);
        return ResponseEntity.ok(totalReserved);
    }

    /**
     * Converts entity to DTO
     * @param reservation the reservation entity
     * @return the DTO representation
     */
    private InventoryReservationDTO convertToDto(InventoryReservation reservation) {
        return InventoryReservationDTO.builder()
                .id(reservation.getId())
                .inventoryItemId(reservation.getInventoryItemId())
                .warehouseId(reservation.getWarehouseId())
                .quantity(reservation.getQuantity())
                .orderId(reservation.getOrderId())
                .expirationTime(reservation.getExpirationTime())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .isExpired(reservation.isExpired())
                .isFinalized(reservation.isFinalized())
                .build();
    }
}

