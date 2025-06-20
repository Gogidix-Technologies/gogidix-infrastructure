package com.exalt.warehousing.inventory.service.impl;

import com.exalt.warehousing.inventory.client.OrderServiceClient;
import com.exalt.warehousing.inventory.event.InventoryEventPublisher;
import com.exalt.warehousing.inventory.exception.InsufficientInventoryException;
import com.exalt.warehousing.inventory.exception.ReservationNotFoundException;
import com.exalt.warehousing.inventory.entity.*;
import com.exalt.warehousing.inventory.model.InventoryReservation;
import com.exalt.warehousing.inventory.model.InventoryAllocation;
import com.exalt.warehousing.inventory.model.ReservationItem;
import com.exalt.warehousing.inventory.model.ReservationStatus;
import com.exalt.warehousing.inventory.enums.TransactionType;
import com.exalt.warehousing.inventory.repository.InventoryAllocationRepository;
import com.exalt.warehousing.inventory.repository.InventoryReservationRepository;
import com.exalt.warehousing.inventory.repository.InventoryTransactionRepository;
import com.exalt.warehousing.inventory.service.InventoryService;
import com.exalt.warehousing.inventory.service.ReservationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the reservation service
 */
@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final InventoryReservationRepository reservationRepository;
    private final InventoryAllocationRepository allocationRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final InventoryService inventoryService;
    private final OrderServiceClient orderServiceClient;
    private final InventoryEventPublisher eventPublisher;
    
    public ReservationServiceImpl(
            InventoryReservationRepository reservationRepository,
            InventoryAllocationRepository allocationRepository,
            InventoryTransactionRepository transactionRepository,
            InventoryService inventoryService,
            OrderServiceClient orderServiceClient,
            InventoryEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.allocationRepository = allocationRepository;
        this.transactionRepository = transactionRepository;
        this.inventoryService = inventoryService;
        this.orderServiceClient = orderServiceClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public InventoryReservation createReservation(UUID inventoryItemId, UUID warehouseId, 
                                               int quantity, UUID orderId, int expirationMinutes) {
        log.info("Creating reservation for inventory item: {}, quantity: {}, order: {}", 
                inventoryItemId, quantity, orderId);
        
        // Check if sufficient inventory is available
        if (!inventoryService.checkAvailability(inventoryItemId, quantity)) {
            log.warn("Insufficient inventory for item: {}, required: {}", inventoryItemId, quantity);
            throw new InsufficientInventoryException("Insufficient inventory available");
        }
        
        // If warehouse specified, check availability in that specific warehouse
        if (warehouseId != null) {
            InventoryAllocation allocation = allocationRepository
                    .findByInventoryItemIdAndWarehouseId(inventoryItemId, warehouseId)
                    .orElseThrow(() -> new InsufficientInventoryException("No inventory allocation found at specified warehouse"));
            
            if (allocation.getAvailableQuantity() < quantity) {
                log.warn("Insufficient inventory at warehouse: {}, required: {}, available: {}", 
                        warehouseId, quantity, allocation.getAvailableQuantity());
                throw new InsufficientInventoryException("Insufficient inventory at specified warehouse");
            }
            
            // Reserve in the specific warehouse
            boolean reserved = allocation.reserve(quantity);
            if (!reserved) {
                throw new InsufficientInventoryException("Failed to reserve inventory");
            }
            allocationRepository.save(allocation);
        } else {
            // Auto-allocate across warehouses (simplified implementation)
            List<InventoryAllocation> availableAllocations = allocationRepository.findAvailableAllocations(inventoryItemId);
            
            int remainingQuantity = quantity;
            for (InventoryAllocation allocation : availableAllocations) {
                int quantityToReserve = Math.min(remainingQuantity, allocation.getAvailableQuantity());
                
                if (quantityToReserve > 0) {
                    boolean reserved = allocation.reserve(quantityToReserve);
                    if (reserved) {
                        allocationRepository.save(allocation);
                        remainingQuantity -= quantityToReserve;
                    }
                }
                
                if (remainingQuantity == 0) {
                    break;
                }
            }
            
            if (remainingQuantity > 0) {
                throw new InsufficientInventoryException("Could not allocate full quantity across warehouses");
            }
            
            // Use the first warehouse for the reservation record if none specified
            if (warehouseId == null && !availableAllocations.isEmpty()) {
                warehouseId = availableAllocations.get(0).getWarehouseId();
            }
        }
        
        // Create the reservation
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);
        
        InventoryReservation reservation = InventoryReservation.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(quantity)
                .orderId(orderId)
                .expirationTime(expirationTime)
                .status(ReservationStatus.CONFIRMED)
                .build();
        
        InventoryReservation savedReservation = reservationRepository.save(reservation);
        
        // Create a transaction record for the reservation
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryItemId(inventoryItemId)
                .warehouseId(warehouseId)
                .quantity(-quantity)  // Negative for reservation
                .type(TransactionType.RESERVATION)
                .referenceId(orderId)
                .referenceType("ORDER")
                .userId(UUID.randomUUID())  // Use system user ID
                .notes("Reservation for order: " + orderId)
                .build();
        
        transactionRepository.save(transaction);
        
        // Publish event for the reservation creation
        eventPublisher.publishReservationCreated(savedReservation);
        
        // Notify order service
        try {
            notifyOrderService(orderId, true, "Inventory successfully reserved");
        } catch (Exception e) {
            log.error("Failed to notify order service about successful reservation", e);
            // Continue with the reservation process despite notification failure
        }
        
        return savedReservation;
    }

    @Override
    public InventoryReservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + id));
    }

    @Override
    public List<InventoryReservation> getReservationsByOrderId(UUID orderId) {
        return reservationRepository.findAllByOrderId(orderId);
    }

    @Override
    public List<InventoryReservation> getActiveReservationsByItemId(UUID inventoryItemId) {
        return reservationRepository.findActiveReservationsByInventoryItemId(inventoryItemId);
    }

    @Override
    @Transactional
    public InventoryReservation updateReservationStatus(UUID id, ReservationStatus status) {
        InventoryReservation reservation = getReservationById(id);
        
        // If already in a terminal state, don't update
        if (reservation.isFinalized()) {
            log.warn("Reservation {} is already in terminal state: {}", id, reservation.getStatus());
            return reservation;
        }
        
        // Store the previous status before updating
        ReservationStatus previousStatus = reservation.getStatus();
        
        // Update the status
        reservation.setStatus(status);
        InventoryReservation updatedReservation = reservationRepository.save(reservation);
        
        // Publish status change event
        eventPublisher.publishReservationStatusChanged(
                updatedReservation,
                previousStatus,
                "Status manually updated via API call"
        );
        
        return updatedReservation;
    }

    @Override
    @Transactional
    public int updateReservationStatusByOrderId(UUID orderId, ReservationStatus status) {
        return reservationRepository.updateStatusByOrderId(orderId, status);
    }

    @Override
    @Transactional
    public InventoryReservation extendReservation(UUID id, int minutes) {
        InventoryReservation reservation = getReservationById(id);
        
        // If already in a terminal state, don't extend
        if (reservation.isFinalized()) {
            log.warn("Cannot extend reservation {} because it is in terminal state: {}", 
                    id, reservation.getStatus());
            return reservation;
        }
        
        // If already expired, don't extend
        if (reservation.isExpired()) {
            log.warn("Cannot extend reservation {} because it is already expired", id);
            return reservation;
        }
        
        LocalDateTime newExpirationTime = reservation.getExpirationTime().plusMinutes(minutes);
        reservationRepository.extendExpirationTime(id, newExpirationTime);
        
        reservation.setExpirationTime(newExpirationTime);
        return reservation;
    }

    @Override
    @Transactional
    public boolean completeReservation(UUID orderId, UUID userId) {
        log.info("Completing reservation for order: {}", orderId);
        
        List<InventoryReservation> reservations = reservationRepository.findAllByOrderId(orderId);
        
        if (reservations.isEmpty()) {
            log.warn("No reservations found for order: {}", orderId);
            return false;
        }
        
        boolean allCompleted = true;
        
        for (InventoryReservation reservation : reservations) {
            // Skip if already completed
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                continue;
            }
            
            // Skip if not in CONFIRMED state
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                log.warn("Reservation {} for order {} is in unexpected state: {}", 
                        reservation.getId(), orderId, reservation.getStatus());
                allCompleted = false;
                continue;
            }
            
            // Find the allocation
            InventoryAllocation allocation = allocationRepository
                    .findByInventoryItemIdAndWarehouseId(reservation.getInventoryItemId(), reservation.getWarehouseId())
                    .orElse(null);
            
            if (allocation == null) {
                log.error("Allocation not found for reservation: {}", reservation.getId());
                allCompleted = false;
                continue;
            }
            
            // Commit the reservation
            boolean committed = allocation.commitReservation(reservation.getQuantity());
            
            if (!committed) {
                log.error("Failed to commit reservation: {}", reservation.getId());
                allCompleted = false;
                continue;
            }
            
            allocationRepository.save(allocation);
            
            // Store the previous status
            ReservationStatus previousStatus = reservation.getStatus();
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.COMPLETED);
            InventoryReservation updatedReservation = reservationRepository.save(reservation);
            
            // Publish completion event
            eventPublisher.publishReservationCompleted(updatedReservation);
            
            // Also publish a status change event
            eventPublisher.publishReservationStatusChanged(
                    updatedReservation,
                    previousStatus,
                    "Reservation completed for order: " + orderId
            );
            
            // Create a transaction record for the sale
            InventoryTransaction transaction = InventoryTransaction.createSaleTransaction(
                    reservation.getInventoryItemId(),
                    reservation.getWarehouseId(),
                    reservation.getQuantity(),
                    orderId,
                    userId);
            
            transactionRepository.save(transaction);
        }
        
        // Notify order service
        try {
            notifyOrderService(orderId, allCompleted, 
                    allCompleted ? "Inventory allocation completed" : "Partial inventory allocation completed");
        } catch (Exception e) {
            log.error("Failed to notify order service about allocation completion", e);
            // Continue with the process despite notification failure
        }
        
        return allCompleted;
    }

    @Override
    @Transactional
    public boolean cancelReservation(UUID orderId) {
        log.info("Cancelling reservation for order: {}", orderId);
        
        List<InventoryReservation> reservations = reservationRepository.findAllByOrderId(orderId);
        
        if (reservations.isEmpty()) {
            log.warn("No reservations found for order: {}", orderId);
            return false;
        }
        
        boolean allCancelled = true;
        
        for (InventoryReservation reservation : reservations) {
            // Skip if already cancelled or expired
            if (reservation.getStatus() == ReservationStatus.CANCELLED || 
                reservation.getStatus() == ReservationStatus.EXPIRED) {
                continue;
            }
            
            // Skip if already completed
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                log.warn("Cannot cancel completed reservation: {}", reservation.getId());
                allCancelled = false;
                continue;
            }
            
            // Find the allocation
            InventoryAllocation allocation = allocationRepository
                    .findByInventoryItemIdAndWarehouseId(reservation.getInventoryItemId(), reservation.getWarehouseId())
                    .orElse(null);
            
            if (allocation == null) {
                log.error("Allocation not found for reservation: {}", reservation.getId());
                allCancelled = false;
                continue;
            }
            
            // Release the reservation
            boolean released = allocation.release(reservation.getQuantity());
            
            if (!released) {
                log.error("Failed to release reservation: {}", reservation.getId());
                allCancelled = false;
                continue;
            }
            
            allocationRepository.save(allocation);
            
            // Store previous status
            ReservationStatus previousStatus = reservation.getStatus();
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.CANCELLED);
            InventoryReservation updatedReservation = reservationRepository.save(reservation);
            
            // Publish cancellation event
            eventPublisher.publishReservationCancelled(updatedReservation, "Cancelled by user or system request");
            
            // Also publish status change event
            eventPublisher.publishReservationStatusChanged(
                    updatedReservation,
                    previousStatus,
                    "Reservation cancelled for order: " + orderId
            );
            
            // Create a transaction record for the release
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .inventoryItemId(reservation.getInventoryItemId())
                    .warehouseId(reservation.getWarehouseId())
                    .quantity(reservation.getQuantity())  // Positive for release
                    .type(TransactionType.UNRESERVATION)
                    .referenceId(orderId)
                    .referenceType("ORDER")
                    .userId(UUID.randomUUID())  // Use system user ID
                    .notes("Cancelled reservation for order: " + orderId)
                    .build();
            
            transactionRepository.save(transaction);
        }
        
        return allCancelled;
    }

    @Override
    @Scheduled(fixedRate = 60000)  // Run every minute
    @Transactional
    public int processExpiredReservations(LocalDateTime currentTime) {
        if (currentTime == null) {
            currentTime = LocalDateTime.now();
        }
        
        log.info("Processing expired reservations as of: {}", currentTime);
        
        List<InventoryReservation> expiredReservations = reservationRepository.findExpiredReservations(currentTime);
        
        if (expiredReservations.isEmpty()) {
            return 0;
        }
        
        int processedCount = 0;
        
        for (InventoryReservation reservation : expiredReservations) {
            // Find the allocation
            InventoryAllocation allocation = allocationRepository
                    .findByInventoryItemIdAndWarehouseId(reservation.getInventoryItemId(), reservation.getWarehouseId())
                    .orElse(null);
            
            if (allocation == null) {
                log.error("Allocation not found for expired reservation: {}", reservation.getId());
                continue;
            }
            
            // Release the reservation
            boolean released = allocation.release(reservation.getQuantity());
            
            if (!released) {
                log.error("Failed to release expired reservation: {}", reservation.getId());
                continue;
            }
            
            allocationRepository.save(allocation);
            
            // Store previous status
            ReservationStatus previousStatus = reservation.getStatus();
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.EXPIRED);
            InventoryReservation updatedReservation = reservationRepository.save(reservation);
            
            // Publish expiration event
            eventPublisher.publishReservationExpired(updatedReservation, "Reservation timed out");
            
            // Also publish status change event
            eventPublisher.publishReservationStatusChanged(
                    updatedReservation,
                    previousStatus,
                    "Reservation expired at " + currentTime
            );
            
            // Create a transaction record for the release
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .inventoryItemId(reservation.getInventoryItemId())
                    .warehouseId(reservation.getWarehouseId())
                    .quantity(reservation.getQuantity())  // Positive for release
                    .type(TransactionType.UNRESERVATION)
                    .referenceId(reservation.getOrderId())
                    .referenceType("ORDER")
                    .userId(UUID.randomUUID())  // Use system user ID
                    .notes("Released expired reservation: " + reservation.getId())
                    .build();
            
            transactionRepository.save(transaction);
            
            // Notify order service
            try {
                Map<String, Object> statusDetails = new HashMap<>();
                statusDetails.put("status", "EXPIRED");
                statusDetails.put("message", "Inventory reservation expired");
                
                orderServiceClient.updateInventoryReservation(reservation.getOrderId(), statusDetails);
            } catch (Exception e) {
                log.error("Failed to notify order service about expired reservation", e);
                // Continue processing despite notification failure
            }
            
            processedCount++;
        }
        
        log.info("Processed {} expired reservations", processedCount);
        return processedCount;
    }

    @Override
    public boolean hasActiveReservations(UUID orderId) {
        List<InventoryReservation> reservations = reservationRepository.findAllByOrderId(orderId);
        
        return reservations.stream()
                .anyMatch(r -> r.getStatus() == ReservationStatus.PENDING || 
                          r.getStatus() == ReservationStatus.CONFIRMED);
    }

    @Override
    public int getTotalReservedQuantity(UUID inventoryItemId) {
        Integer total = reservationRepository.getTotalReservedQuantity(inventoryItemId);
        return total != null ? total : 0;
    }

    @Override
    public int getTotalReservedQuantityAtWarehouse(UUID inventoryItemId, UUID warehouseId) {
        Integer total = reservationRepository.getTotalReservedQuantityAtWarehouse(inventoryItemId, warehouseId);
        return total != null ? total : 0;
    }

    /**
     * Notifies the Order Service about inventory reservation/allocation status
     * @param orderId the order ID
     * @param success whether the operation was successful
     * @param message status message
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "notifyOrderServiceFallback")
    private void notifyOrderService(UUID orderId, boolean success, String message) {
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("status", success ? "SUCCESS" : "FAILURE");
        statusDetails.put("message", message);
        
        orderServiceClient.updateInventoryReservation(orderId, statusDetails);
    }

    /**
     * Fallback method for order service notification
     */
    private void notifyOrderServiceFallback(UUID orderId, boolean success, String message, Exception e) {
        log.error("Failed to notify order service, using fallback. Order: {}, Success: {}, Error: {}", 
                orderId, success, e.getMessage());
        // In a real implementation, consider adding to a retry queue
    }
}
