package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.ReturnRequestDTO;
import com.exalt.warehousing.management.model.ReturnStatus;
import com.exalt.warehousing.management.service.ReturnRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for return request operations
 */
@RestController
@RequestMapping("/api/v1/return-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Return Request", description = "Return request operations")
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    /**
     * Create a new return request
     *
     * @param returnRequestDTO the return request data
     * @return the created return request
     */
    @PostMapping
    @Operation(summary = "Create a new return request", 
               description = "Creates a new return request with the provided data")
    public ResponseEntity<ReturnRequestDTO> createReturnRequest(@RequestBody ReturnRequestDTO returnRequestDTO) {
        log.info("Creating return request for order: {}", returnRequestDTO.getOrderId());
        ReturnRequestDTO createdReturnRequest = returnRequestService.createReturnRequest(returnRequestDTO);
        return new ResponseEntity<>(createdReturnRequest, HttpStatus.CREATED);
    }

    /**
     * Get a return request by ID
     *
     * @param id the return request ID
     * @return the return request
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a return request by ID", 
               description = "Retrieves a return request by its ID")
    public ResponseEntity<ReturnRequestDTO> getReturnRequestById(
            @Parameter(description = "Return request ID") @PathVariable UUID id) {
        log.info("Getting return request with ID: {}", id);
        ReturnRequestDTO returnRequest = returnRequestService.getReturnRequestById(id);
        return ResponseEntity.ok(returnRequest);
    }

    /**
     * Update a return request
     *
     * @param id the return request ID
     * @param returnRequestDTO the updated return request data
     * @return the updated return request
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a return request", 
               description = "Updates an existing return request with the provided data")
    public ResponseEntity<ReturnRequestDTO> updateReturnRequest(
            @Parameter(description = "Return request ID") @PathVariable UUID id,
            @RequestBody ReturnRequestDTO returnRequestDTO) {
        log.info("Updating return request with ID: {}", id);
        ReturnRequestDTO updatedReturnRequest = returnRequestService.updateReturnRequest(id, returnRequestDTO);
        return ResponseEntity.ok(updatedReturnRequest);
    }

    /**
     * Delete a return request
     *
     * @param id the return request ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a return request", 
               description = "Deletes a return request by its ID")
    public ResponseEntity<Void> deleteReturnRequest(
            @Parameter(description = "Return request ID") @PathVariable UUID id) {
        log.info("Deleting return request with ID: {}", id);
        returnRequestService.deleteReturnRequest(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all return requests for a warehouse with pagination
     *
     * @param warehouseId the warehouse ID
     * @param pageable pagination information
     * @return page of return requests
     */
    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get return requests for a warehouse", 
               description = "Retrieves all return requests for a specific warehouse with pagination")
    public ResponseEntity<Page<ReturnRequestDTO>> getReturnRequestsByWarehouse(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting return requests for warehouse: {}", warehouseId);
        Page<ReturnRequestDTO> returnRequests = returnRequestService.getReturnRequestsByWarehouse(warehouseId, pageable);
        return ResponseEntity.ok(returnRequests);
    }

    /**
     * Get all return requests with a specific status for a warehouse with pagination
     *
     * @param warehouseId the warehouse ID
     * @param status the return status
     * @param pageable pagination information
     * @return page of return requests
     */
    @GetMapping("/warehouse/{warehouseId}/status/{status}")
    @Operation(summary = "Get return requests by status for a warehouse", 
               description = "Retrieves all return requests with a specific status for a warehouse with pagination")
    public ResponseEntity<Page<ReturnRequestDTO>> getReturnRequestsByWarehouseAndStatus(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Return status") @PathVariable ReturnStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting return requests for warehouse: {} with status: {}", warehouseId, status);
        Page<ReturnRequestDTO> returnRequests = returnRequestService.getReturnRequestsByWarehouseAndStatus(warehouseId, status, pageable);
        return ResponseEntity.ok(returnRequests);
    }

    /**
     * Get all return requests for an order
     *
     * @param orderId the order ID
     * @return list of return requests
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get return requests for an order", 
               description = "Retrieves all return requests for a specific order")
    public ResponseEntity<List<ReturnRequestDTO>> getReturnRequestsByOrder(
            @Parameter(description = "Order ID") @PathVariable UUID orderId) {
        log.info("Getting return requests for order: {}", orderId);
        List<ReturnRequestDTO> returnRequests = returnRequestService.getReturnRequestsByOrder(orderId);
        return ResponseEntity.ok(returnRequests);
    }

    /**
     * Process a return request arrival at the warehouse
     *
     * @param id the return request ID
     * @return the updated return request
     */
    @PostMapping("/{id}/arrival")
    @Operation(summary = "Process return arrival", 
               description = "Processes the arrival of a return request at the warehouse")
    public ResponseEntity<ReturnRequestDTO> processReturnArrival(
            @Parameter(description = "Return request ID") @PathVariable UUID id) {
        log.info("Processing arrival for return request: {}", id);
        ReturnRequestDTO updatedReturnRequest = returnRequestService.processReturnArrival(id);
        return ResponseEntity.ok(updatedReturnRequest);
    }

    /**
     * Process inspection of a return request
     *
     * @param id the return request ID
     * @param request map with inspection results and quality check notes
     * @return the updated return request
     */
    @PostMapping("/{id}/inspection")
    @Operation(summary = "Process return inspection", 
               description = "Processes the inspection of a return request")
    public ResponseEntity<ReturnRequestDTO> processReturnInspection(
            @Parameter(description = "Return request ID") @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        log.info("Processing inspection for return request: {}", id);
        
        @SuppressWarnings("unchecked")
        Map<UUID, Map<String, Object>> inspectionResults = (Map<UUID, Map<String, Object>>) request.get("inspectionResults");
        String qualityCheckNotes = (String) request.get("qualityCheckNotes");
        
        ReturnRequestDTO updatedReturnRequest = returnRequestService.processReturnInspection(id, inspectionResults, qualityCheckNotes);
        return ResponseEntity.ok(updatedReturnRequest);
    }

    /**
     * Process inventory reintegration for a return request
     *
     * @param id the return request ID
     * @param reintegrationData map of item IDs to reintegration data
     * @return the updated return request
     */
    @PostMapping("/{id}/reintegration")
    @Operation(summary = "Process inventory reintegration", 
               description = "Processes the inventory reintegration for a return request")
    public ResponseEntity<ReturnRequestDTO> processInventoryReintegration(
            @Parameter(description = "Return request ID") @PathVariable UUID id,
            @RequestBody Map<UUID, Map<String, Object>> reintegrationData) {
        log.info("Processing inventory reintegration for return request: {}", id);
        ReturnRequestDTO updatedReturnRequest = returnRequestService.processInventoryReintegration(id, reintegrationData);
        return ResponseEntity.ok(updatedReturnRequest);
    }

    /**
     * Process refund for a return request
     *
     * @param id the return request ID
     * @param refundData map of item IDs to refund amounts
     * @return the updated return request
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "Process refund", 
               description = "Processes the refund for a return request")
    public ResponseEntity<ReturnRequestDTO> processRefund(
            @Parameter(description = "Return request ID") @PathVariable UUID id,
            @RequestBody Map<UUID, Double> refundData) {
        log.info("Processing refund for return request: {}", id);
        ReturnRequestDTO updatedReturnRequest = returnRequestService.processRefund(id, refundData);
        return ResponseEntity.ok(updatedReturnRequest);
    }

    /**
     * Complete a return request
     *
     * @param id the return request ID
     * @return the updated return request
     */
    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a return request", 
               description = "Completes a return request")
    public ResponseEntity<ReturnRequestDTO> completeReturnRequest(
            @Parameter(description = "Return request ID") @PathVariable UUID id) {
        log.info("Completing return request: {}", id);
        ReturnRequestDTO updatedReturnRequest = returnRequestService.completeReturnRequest(id);
        return ResponseEntity.ok(updatedReturnRequest);
    }

    /**
     * Get all return requests that need processing
     *
     * @param warehouseId the warehouse ID
     * @return list of return requests
     */
    @GetMapping("/warehouse/{warehouseId}/processing-queue")
    @Operation(summary = "Get return requests needing processing", 
               description = "Retrieves all return requests that need processing for a warehouse")
    public ResponseEntity<List<ReturnRequestDTO>> getReturnsNeedingProcessing(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId) {
        log.info("Getting returns needing processing for warehouse: {}", warehouseId);
        List<ReturnRequestDTO> returnRequests = returnRequestService.getReturnsNeedingProcessing(warehouseId);
        return ResponseEntity.ok(returnRequests);
    }

    /**
     * Get all return requests that need inventory reintegration
     *
     * @param warehouseId the warehouse ID
     * @return list of return requests
     */
    @GetMapping("/warehouse/{warehouseId}/reintegration-queue")
    @Operation(summary = "Get return requests needing inventory reintegration", 
               description = "Retrieves all return requests that need inventory reintegration for a warehouse")
    public ResponseEntity<List<ReturnRequestDTO>> getReturnsNeedingInventoryReintegration(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId) {
        log.info("Getting returns needing inventory reintegration for warehouse: {}", warehouseId);
        List<ReturnRequestDTO> returnRequests = returnRequestService.getReturnsNeedingInventoryReintegration(warehouseId);
        return ResponseEntity.ok(returnRequests);
    }

    /**
     * Get return request counts by status for reporting
     *
     * @param warehouseId the warehouse ID
     * @return map of status to count
     */
    @GetMapping("/warehouse/{warehouseId}/status-counts")
    @Operation(summary = "Get return request counts by status", 
               description = "Retrieves counts of return requests grouped by status for a warehouse")
    public ResponseEntity<Map<ReturnStatus, Long>> getReturnCountsByStatus(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId) {
        log.info("Getting return counts by status for warehouse: {}", warehouseId);
        Map<ReturnStatus, Long> counts = returnRequestService.getReturnCountsByStatus(warehouseId);
        return ResponseEntity.ok(counts);
    }
} 
