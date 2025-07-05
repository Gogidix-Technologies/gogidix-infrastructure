package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.ReturnItemDTO;
import com.exalt.warehousing.management.dto.ReturnRequestDTO;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.mapper.ReturnItemMapper;
import com.exalt.warehousing.management.mapper.ReturnRequestMapper;
import com.exalt.warehousing.management.model.*;
import com.exalt.warehousing.management.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing return requests
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnRequestMapper returnRequestMapper;
    private final ReturnItemMapper returnItemMapper;
    // Commenting out until InventoryService is implemented
    // private final InventoryService inventoryService;

    /**
     * Create a new return request
     *
     * @param returnRequestDTO the return request data
     * @return the created return request
     */
    @Transactional
    public ReturnRequestDTO createReturnRequest(ReturnRequestDTO returnRequestDTO) {
        // Generate return code if not provided
        if (returnRequestDTO.getReturnCode() == null) {
            returnRequestDTO.setReturnCode(generateReturnCode());
        }

        // Set default status if not provided
        if (returnRequestDTO.getStatus() == null) {
            returnRequestDTO.setStatus(ReturnStatus.REQUESTED);
        }

        ReturnRequest returnRequest = returnRequestMapper.toEntity(returnRequestDTO);
        
        // Process return items if provided
        if (returnRequestDTO.getReturnItems() != null && !returnRequestDTO.getReturnItems().isEmpty()) {
            for (ReturnItemDTO itemDTO : returnRequestDTO.getReturnItems()) {
                ReturnItem item = returnItemMapper.toEntity(itemDTO);
                
                // Set default status if not provided
                if (item.getStatus() == null) {
                    item.setStatus(ReturnItemStatus.REQUESTED);
                }
                
                // Set default condition if not provided
                if (item.getCondition() == null) {
                    item.setCondition(ReturnItemCondition.NOT_ASSESSED);
                }
                
                returnRequest.addReturnItem(item);
            }
        }

        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("Created return request: id={}, code={}", savedReturnRequest.getId(), savedReturnRequest.getReturnCode());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Get a return request by ID
     *
     * @param id the return request ID
     * @return the return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public ReturnRequestDTO getReturnRequestById(UUID id) {
        ReturnRequest returnRequest = findReturnRequestById(id);
        return returnRequestMapper.toDTO(returnRequest);
    }

    /**
     * Update a return request
     *
     * @param id the return request ID
     * @param returnRequestDTO the updated return request data
     * @return the updated return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public ReturnRequestDTO updateReturnRequest(UUID id, ReturnRequestDTO returnRequestDTO) {
        ReturnRequest existingReturnRequest = findReturnRequestById(id);
        
        ReturnRequest updatedReturnRequest = returnRequestMapper.updateEntityFromDTO(existingReturnRequest, returnRequestDTO);
        ReturnRequest savedReturnRequest = returnRequestRepository.save(updatedReturnRequest);
        
        log.info("Updated return request: id={}, status={}", savedReturnRequest.getId(), savedReturnRequest.getStatus());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Delete a return request
     *
     * @param id the return request ID
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public void deleteReturnRequest(UUID id) {
        if (!returnRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Return request not found with id: " + id);
        }
        
        returnRequestRepository.deleteById(id);
        log.info("Deleted return request: id={}", id);
    }

    /**
     * Get all return requests for a warehouse with pagination
     *
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return page of return requests
     */
    @Transactional(readOnly = true)
    public Page<ReturnRequestDTO> getReturnRequestsByWarehouse(UUID warehouseId, Pageable pageable) {
        Page<ReturnRequest> returnRequests = returnRequestRepository.findByWarehouseId(warehouseId, pageable);
        return returnRequests.map(returnRequestMapper::toDTO);
    }

    /**
     * Get all return requests with a specific status for a warehouse with pagination
     *
     * @param warehouseId the warehouse ID
     * @param status the return status
     * @param pageable pagination information
     * @return page of return requests
     */
    @Transactional(readOnly = true)
    public Page<ReturnRequestDTO> getReturnRequestsByWarehouseAndStatus(UUID warehouseId, ReturnStatus status, Pageable pageable) {
        Page<ReturnRequest> returnRequests = returnRequestRepository.findByWarehouseIdAndStatus(warehouseId, status, pageable);
        return returnRequests.map(returnRequestMapper::toDTO);
    }

    /**
     * Get all return requests for an order
     *
     * @param orderId the order ID
     * @return list of return requests
     */
    @Transactional(readOnly = true)
    public List<ReturnRequestDTO> getReturnRequestsByOrder(UUID orderId) {
        List<ReturnRequest> returnRequests = returnRequestRepository.findByOrderId(orderId);
        return returnRequestMapper.toDTOList(returnRequests);
    }

    /**
     * Process a return request arrival at the warehouse
     *
     * @param id the return request ID
     * @return the updated return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public ReturnRequestDTO processReturnArrival(UUID id) {
        ReturnRequest returnRequest = findReturnRequestById(id);
        
        // Validate current status
        if (returnRequest.getStatus() != ReturnStatus.AUTHORIZED && 
            returnRequest.getStatus() != ReturnStatus.LABEL_GENERATED && 
            returnRequest.getStatus() != ReturnStatus.IN_TRANSIT &&
            returnRequest.getStatus() != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Return request is not in a valid state for arrival processing: " + returnRequest.getStatus());
        }
        
        // Update status
        returnRequest.setStatus(ReturnStatus.RECEIVED);
        returnRequest.setReceivedAt(LocalDateTime.now());
        
        // Update item statuses
        for (ReturnItem item : returnRequest.getReturnItems()) {
            item.setStatus(ReturnItemStatus.RECEIVED);
        }
        
        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("Processed return arrival: id={}, code={}", savedReturnRequest.getId(), savedReturnRequest.getReturnCode());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Process inspection of a return request
     *
     * @param id the return request ID
     * @param inspectionResults map of item IDs to inspection results (condition, notes)
     * @param qualityCheckNotes overall quality check notes
     * @return the updated return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public ReturnRequestDTO processReturnInspection(UUID id, Map<UUID, Map<String, Object>> inspectionResults, String qualityCheckNotes) {
        ReturnRequest returnRequest = findReturnRequestById(id);
        
        // Validate current status
        if (returnRequest.getStatus() != ReturnStatus.RECEIVED) {
            throw new IllegalStateException("Return request must be in RECEIVED status for inspection");
        }
        
        // Update status
        returnRequest.setStatus(ReturnStatus.INSPECTION);
        returnRequest.setQualityCheckNotes(qualityCheckNotes);
        
        // Process each item's inspection results
        boolean anyRejected = false;
        boolean anyAccepted = false;
        
        for (ReturnItem item : returnRequest.getReturnItems()) {
            if (inspectionResults.containsKey(item.getId())) {
                Map<String, Object> result = inspectionResults.get(item.getId());
                
                // Update condition
                if (result.containsKey("condition")) {
                    ReturnItemCondition condition = (ReturnItemCondition) result.get("condition");
                    item.setCondition(condition);
                }
                
                // Update inspection notes
                if (result.containsKey("notes")) {
                    String notes = (String) result.get("notes");
                    item.setInspectionNotes(notes);
                }
                
                // Update status based on decision
                if (result.containsKey("accepted") && (Boolean) result.get("accepted")) {
                    item.setStatus(ReturnItemStatus.ACCEPTED);
                    anyAccepted = true;
                } else {
                    item.setStatus(ReturnItemStatus.REJECTED);
                    anyRejected = true;
                }
            }
        }
        
        // Update overall status based on item statuses
        if (anyAccepted && anyRejected) {
            returnRequest.setStatus(ReturnStatus.PARTIALLY_ACCEPTED);
        } else if (anyAccepted) {
            returnRequest.setStatus(ReturnStatus.ACCEPTED);
        } else if (anyRejected) {
            returnRequest.setStatus(ReturnStatus.REJECTED);
        }
        
        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("Processed return inspection: id={}, status={}", savedReturnRequest.getId(), savedReturnRequest.getStatus());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Process inventory reintegration for a return request
     *
     * @param id the return request ID
     * @param reintegrationData map of item IDs to reintegration data (inventory item ID, location ID, quantity)
     * @return the updated return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public ReturnRequestDTO processInventoryReintegration(UUID id, Map<UUID, Map<String, Object>> reintegrationData) {
        ReturnRequest returnRequest = findReturnRequestById(id);
        
        // Validate current status
        if (returnRequest.getStatus() != ReturnStatus.ACCEPTED && 
            returnRequest.getStatus() != ReturnStatus.PARTIALLY_ACCEPTED) {
            throw new IllegalStateException("Return request must be in ACCEPTED or PARTIALLY_ACCEPTED status for inventory reintegration");
        }
        
        // Process each item's reintegration
        boolean allReintegrated = true;
        
        for (ReturnItem item : returnRequest.getReturnItems()) {
            // Only process accepted items
            if (item.getStatus() != ReturnItemStatus.ACCEPTED) {
                continue;
            }
            
            if (reintegrationData.containsKey(item.getId())) {
                Map<String, Object> data = reintegrationData.get(item.getId());
                
                UUID inventoryItemId = (UUID) data.get("inventoryItemId");
                UUID locationId = (UUID) data.get("locationId");
                Integer quantity = (Integer) data.get("quantity");
                
                // Validate quantity
                if (quantity > item.getQuantity()) {
                    throw new IllegalArgumentException("Reintegration quantity cannot exceed return quantity");
                }
                
                // Update inventory through inventory service
                try {
                    // In a real implementation, this would call the inventory service
                    // inventoryService.incrementInventory(inventoryItemId, quantity);
                    
                    // Update return item with reintegration details
                    item.setInventoryItemId(inventoryItemId);
                    item.setReintegratedLocationId(locationId);
                    item.setReintegratedQuantity(quantity);
                    item.setInventoryReintegrated(true);
                    item.setStatus(ReturnItemStatus.REINTEGRATED);
                } catch (Exception e) {
                    log.error("Error reintegrating inventory for item {}: {}", item.getId(), e.getMessage());
                    allReintegrated = false;
                }
            } else {
                allReintegrated = false;
            }
        }
        
        // Update return request status if all items were reintegrated
        if (allReintegrated) {
            returnRequest.setStatus(ReturnStatus.INVENTORY_REINTEGRATED);
            returnRequest.setInventoryReintegrated(true);
        }
        
        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("Processed inventory reintegration: id={}, status={}", savedReturnRequest.getId(), savedReturnRequest.getStatus());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Process refund for a return request
     *
     * @param id the return request ID
     * @param refundData map of item IDs to refund amounts
     * @return the updated return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public ReturnRequestDTO processRefund(UUID id, Map<UUID, Double> refundData) {
        ReturnRequest returnRequest = findReturnRequestById(id);
        
        // Validate current status
        if (returnRequest.getStatus() != ReturnStatus.ACCEPTED && 
            returnRequest.getStatus() != ReturnStatus.PARTIALLY_ACCEPTED && 
            returnRequest.getStatus() != ReturnStatus.INVENTORY_REINTEGRATED) {
            throw new IllegalStateException("Return request must be in an appropriate status for refund processing");
        }
        
        // Process each item's refund
        boolean allRefunded = true;
        
        for (ReturnItem item : returnRequest.getReturnItems()) {
            // Only process accepted items
            if (item.getStatus() != ReturnItemStatus.ACCEPTED && 
                item.getStatus() != ReturnItemStatus.REINTEGRATED) {
                continue;
            }
            
            if (refundData.containsKey(item.getId())) {
                Double refundAmount = refundData.get(item.getId());
                
                // In a real implementation, this would call the payment service
                // paymentService.processRefund(returnRequest.getOrderId(), item.getOrderItemId(), refundAmount);
                
                // Update return item with refund details
                item.setRefundAmount(refundAmount);
                item.setRefundProcessed(true);
            } else {
                allRefunded = false;
            }
        }
        
        // Update return request status if all items were refunded
        if (allRefunded) {
            returnRequest.setStatus(ReturnStatus.REFUND_PROCESSED);
            returnRequest.setRefundProcessed(true);
        }
        
        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("Processed refund: id={}, status={}", savedReturnRequest.getId(), savedReturnRequest.getStatus());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Complete a return request
     *
     * @param id the return request ID
     * @return the updated return request
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public ReturnRequestDTO completeReturnRequest(UUID id) {
        ReturnRequest returnRequest = findReturnRequestById(id);
        
        // Validate that all necessary steps have been completed
        boolean canComplete = returnRequest.getStatus() == ReturnStatus.REFUND_PROCESSED ||
                             returnRequest.getStatus() == ReturnStatus.INVENTORY_REINTEGRATED ||
                             returnRequest.getStatus() == ReturnStatus.REJECTED;
        
        if (!canComplete) {
            throw new IllegalStateException("Return request cannot be completed in its current state: " + returnRequest.getStatus());
        }
        
        // Update status
        returnRequest.setStatus(ReturnStatus.COMPLETED);
        
        // Update all items to completed status
        for (ReturnItem item : returnRequest.getReturnItems()) {
            if (item.getStatus() != ReturnItemStatus.REJECTED) {
                item.setStatus(ReturnItemStatus.COMPLETED);
            }
        }
        
        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("Completed return request: id={}", savedReturnRequest.getId());
        
        return returnRequestMapper.toDTO(savedReturnRequest);
    }

    /**
     * Generate a unique return code
     *
     * @return the generated return code
     */
    private String generateReturnCode() {
        // Format: RET-yyyyMMdd-XXXX where XXXX is a random 4-digit number
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "RET-" + datePart + "-" + randomPart;
    }

    /**
     * Find a return request by ID
     *
     * @param id the return request ID
     * @return the return request
     * @throws ResourceNotFoundException if not found
     */
    private ReturnRequest findReturnRequestById(UUID id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with id: " + id));
    }

    /**
     * Get all return requests that need processing
     *
     * @param warehouseId the warehouse ID
     * @return list of return requests
     */
    @Transactional(readOnly = true)
    public List<ReturnRequestDTO> getReturnsNeedingProcessing(UUID warehouseId) {
        List<ReturnRequest> returnRequests = returnRequestRepository.findReturnsNeedingProcessing(warehouseId);
        return returnRequestMapper.toDTOList(returnRequests);
    }

    /**
     * Get all return requests that need inventory reintegration
     *
     * @param warehouseId the warehouse ID
     * @return list of return requests
     */
    @Transactional(readOnly = true)
    public List<ReturnRequestDTO> getReturnsNeedingInventoryReintegration(UUID warehouseId) {
        List<ReturnRequest> returnRequests = returnRequestRepository.findReturnsNeedingInventoryReintegration(warehouseId);
        return returnRequestMapper.toDTOList(returnRequests);
    }

    /**
     * Get return request counts by status for reporting
     *
     * @param warehouseId the warehouse ID
     * @return map of status to count
     */
    @Transactional(readOnly = true)
    public Map<ReturnStatus, Long> getReturnCountsByStatus(UUID warehouseId) {
        List<Object[]> results = returnRequestRepository.countReturnsByStatusForWarehouse(warehouseId);
        
        Map<ReturnStatus, Long> counts = new EnumMap<>(ReturnStatus.class);
        for (Object[] result : results) {
            ReturnStatus status = (ReturnStatus) result[0];
            Long count = (Long) result[1];
            counts.put(status, count);
        }
        
        return counts;
    }
} 
