package com.exalt.warehousing.fulfillment.service.impl;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.dto.PackingTaskDTO;
import com.exalt.warehousing.fulfillment.dto.PickingTaskDTO;
import com.exalt.warehousing.fulfillment.dto.ShipmentPackageDTO;
import com.exalt.warehousing.fulfillment.exception.FulfillmentException;
import com.exalt.warehousing.fulfillment.exception.ResourceNotFoundException;
import com.exalt.warehousing.fulfillment.mapper.FulfillmentOrderItemMapper;
import com.exalt.warehousing.fulfillment.mapper.FulfillmentOrderMapper;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.exalt.warehousing.fulfillment.entity.PackingTask;
import com.exalt.warehousing.fulfillment.entity.PickingTask;
import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import com.exalt.warehousing.fulfillment.enums.TaskStatus;
import com.exalt.warehousing.fulfillment.repository.FulfillmentOrderItemRepository;
import com.exalt.warehousing.fulfillment.repository.FulfillmentOrderRepository;
import com.exalt.warehousing.fulfillment.repository.PackingTaskRepository;
import com.exalt.warehousing.fulfillment.repository.PickingTaskRepository;
import com.exalt.warehousing.fulfillment.repository.ShipmentPackageRepository;
import com.exalt.warehousing.fulfillment.service.FulfillmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the FulfillmentService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FulfillmentServiceImpl implements FulfillmentService {

    private final FulfillmentOrderRepository fulfillmentOrderRepository;
    private final FulfillmentOrderItemRepository itemRepository;
    private final PickingTaskRepository pickingTaskRepository;
    private final PackingTaskRepository packingTaskRepository;
    private final ShipmentPackageRepository shipmentPackageRepository;
    private final FulfillmentOrderMapper orderMapper;
    private final FulfillmentOrderItemMapper itemMapper;
    
    @Override
    @Transactional(readOnly = true)
    public FulfillmentOrderDTO getFulfillmentOrder(UUID id) {
        log.debug("Getting fulfillment order with id: {}", id);
        return orderMapper.toDTO(getFulfillmentOrderEntity(id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public FulfillmentOrderDTO getFulfillmentOrderByOrderId(UUID orderId) {
        log.debug("Getting fulfillment order by order id: {}", orderId);
        return fulfillmentOrderRepository.findByExternalOrderId(orderId.toString())
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("FulfillmentOrder", "orderId", orderId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentOrderDTO> getAllFulfillmentOrders(Pageable pageable) {
        log.debug("Getting all fulfillment orders with pagination");
        return fulfillmentOrderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentOrderDTO> getFulfillmentOrdersByStatus(FulfillmentStatus status, Pageable pageable) {
        log.debug("Getting fulfillment orders by status: {} with pagination", status);
        return fulfillmentOrderRepository.findAllByStatus(status, pageable)
                .map(orderMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentOrderDTO> getFulfillmentOrdersByWarehouse(UUID warehouseId, Pageable pageable) {
        log.debug("Getting fulfillment orders by warehouse id: {} with pagination", warehouseId);
        Long warehouseIdLong = uuidToLong(warehouseId);
        return fulfillmentOrderRepository.findAllByWarehouseId(warehouseIdLong, pageable)
                .map(orderMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentOrderDTO> getActiveFulfillmentOrders(Pageable pageable) {
        log.debug("Getting active fulfillment orders with pagination");
        return fulfillmentOrderRepository.findAllActiveOrders(pageable)
                .map(orderMapper::toDTO);
    }
    
    @Override
    public FulfillmentOrderDTO createFulfillmentOrder(FulfillmentOrderDTO fulfillmentOrderDTO) {
        log.debug("Creating new fulfillment order: {}", fulfillmentOrderDTO);
        
        // Set initial status if not provided
        if (fulfillmentOrderDTO.getStatus() == null) {
            fulfillmentOrderDTO.setStatus(FulfillmentStatus.RECEIVED);
        }
        
        // Convert DTO to entity
        FulfillmentOrder order = orderMapper.toEntity(fulfillmentOrderDTO);
        
        // Save order first to get ID
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        // Add items with proper parent reference
        if (fulfillmentOrderDTO.getItems() != null && !fulfillmentOrderDTO.getItems().isEmpty()) {
            for (FulfillmentOrderItemDTO itemDTO : fulfillmentOrderDTO.getItems()) {
                FulfillmentOrderItem item = itemMapper.toEntity(itemDTO, savedOrder);
                
                // Set initial status if not provided
                if (item.getStatus() == null) {
                    item.setStatus(ItemFulfillmentStatus.PENDING);
                }
                
                // Initialize quantities if null
                if (item.getQuantityFulfilled() == null) {
                    item.setQuantityFulfilled(0);
                }
                if (item.getQuantityPicked() == null) {
                    item.setQuantityPicked(0);
                }
                if (item.getQuantityPacked() == null) {
                    item.setQuantityPacked(0);
                }
                
                savedOrder.addOrderItem(item);
            }
            
            // Save again with items
            savedOrder = fulfillmentOrderRepository.save(savedOrder);
        }
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO updateFulfillmentOrder(UUID id, FulfillmentOrderDTO fulfillmentOrderDTO) {
        log.debug("Updating fulfillment order with id: {}", id);
        
        FulfillmentOrder existingOrder = getFulfillmentOrderEntity(id);
        
        // Update fields from DTO
        orderMapper.updateEntityFromDTO(existingOrder, fulfillmentOrderDTO);
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(existingOrder);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO updateFulfillmentOrderStatus(UUID id, FulfillmentStatus status) {
        log.debug("Updating fulfillment order status with id: {} to {}", id, status);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Update status
        order.setStatus(status);
        
        // Update timestamps based on status
        updateTimestampsBasedOnStatus(order, status);
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO assignToWarehouse(UUID id, UUID warehouseId) {
        log.debug("Assigning fulfillment order with id: {} to warehouse: {}", id, warehouseId);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.isCompleted()) {
            throw new FulfillmentException("Cannot assign a completed fulfillment order to a warehouse");
        }
        
        // Update warehouse assignment
        Long warehouseIdLong = uuidToLong(warehouseId);
        order.setWarehouseId(warehouseIdLong);
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO cancelFulfillmentOrder(UUID id, String reason) {
        log.debug("Cancelling fulfillment order with id: {}, reason: {}", id, reason);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() == FulfillmentStatus.COMPLETED || 
            order.getStatus() == FulfillmentStatus.DELIVERED) {
            throw new FulfillmentException("Cannot cancel a delivered or completed fulfillment order");
        }
        
        // Update status and cancellation details
        order.setStatus(FulfillmentStatus.CANCELLED);
        order.setStatusReason(reason);
        order.setCancelledAt(LocalDateTime.now());
        
        // Update items status
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            item.setStatus(ItemFulfillmentStatus.CANCELLED);
        }
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO processFulfillmentOrder(UUID id) {
        log.debug("Processing fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.RECEIVED) {
            throw new FulfillmentException("Fulfillment order must be in RECEIVED status to be processed");
        }
        
        // Update status and processing timestamp
        order.setStatus(FulfillmentStatus.PROCESSING);
        // Note: Using pickStartedAt since there's no separate processingStartedAt field
        order.setPickStartedAt(LocalDateTime.now());
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO allocateInventory(UUID id) {
        log.debug("Allocating inventory for fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.PROCESSING) {
            throw new FulfillmentException("Fulfillment order must be in PROCESSING status to allocate inventory");
        }
        
        // Validate warehouse assignment
        if (order.getWarehouseId() == null) {
            throw new FulfillmentException("Fulfillment order must be assigned to a warehouse before allocating inventory");
        }
        
        // TODO: Implement integration with inventory service to allocate stock
        // This would typically involve calling an inventory service to reserve stock
        
        // Update status
        order.setStatus(FulfillmentStatus.ALLOCATED);
        
        // Update items status
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            item.setStatus(ItemFulfillmentStatus.ALLOCATED);
        }
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public List<PickingTaskDTO> startPicking(UUID id) {
        log.debug("Starting picking for fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.ALLOCATED) {
            throw new FulfillmentException("Fulfillment order must be in ALLOCATED status to start picking");
        }
        
        // Update status and timestamp
        order.setStatus(FulfillmentStatus.PICKING);
        order.setPickingStartedAt(LocalDateTime.now());
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        // Create picking tasks
        List<PickingTask> pickingTasks = createPickingTasks(savedOrder);
        
        // Convert to DTOs
        return pickingTasks.stream()
                .map(this::convertToPickingTaskDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Helper method to create picking tasks for a fulfillment order
     */
    private List<PickingTask> createPickingTasks(FulfillmentOrder order) {
        List<PickingTask> tasks = new ArrayList<>();
        
        // Create a picking task for each item or group items by zone for efficient picking
        // This is a simplified implementation - in a real system, this might be more complex
        // with batch picking, zone-based picking, etc.
        
        // For this implementation, we'll create one task per order
        PickingTask task = PickingTask.builder()
                .fulfillmentOrderId(stringToUuid(order.getId().toString()))
                .status(TaskStatus.PENDING)
                .priority(order.getPriority() != null ? order.getPriority().ordinal() : 1)
                .dueBy(LocalDateTime.now().plusHours(2)) // Set due time to 2 hours from now
                .instruction("Pick all items for order " + order.getOrderReference())
                .build();
        
        PickingTask savedTask = pickingTaskRepository.save(task);
        tasks.add(savedTask);
        
        return tasks;
    }
    
    /**
     * Helper method to convert a PickingTask entity to DTO
     */
    private PickingTaskDTO convertToPickingTaskDTO(PickingTask task) {
        return PickingTaskDTO.builder()
                .id(stringToUuid(task.getId().toString()))
                .fulfillmentOrderId(task.getFulfillmentOrderId())
                .assignedStaffId(task.getAssignedStaffId())
                .status(task.getStatus())
                .priority(task.getPriority())
                .batchId(task.getBatchId())
                .zone(task.getZone())
                .aisle(task.getAisle())
                .rack(task.getRack())
                .bin(task.getBin())
                .instruction(task.getInstruction())
                .dueBy(task.getDueBy())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .completionNotes(task.getCompletionNotes())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
    
    /**
     * Helper method to convert String to UUID with validation
     */
    private UUID stringToUuid(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(str.getBytes());
        }
    }
    
    @Override
    public FulfillmentOrderDTO completePicking(UUID id) {
        log.debug("Completing picking for fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.PICKING) {
            throw new FulfillmentException("Fulfillment order must be in PICKING status to complete picking");
        }
        
        // Verify all picking tasks are completed
        List<PickingTask> pickingTasks = pickingTaskRepository.findAllByFulfillmentOrderId(id);
        boolean allTasksCompleted = pickingTasks.stream()
                .allMatch(task -> task.getStatus() == TaskStatus.COMPLETED);
        
        if (!allTasksCompleted) {
            throw new FulfillmentException("Cannot complete picking as some picking tasks are not completed");
        }
        
        // Update order status and timestamp
        order.setStatus(FulfillmentStatus.PICKING_COMPLETE);
        order.setPickingCompletedAt(LocalDateTime.now());
        
        // Update items status
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            item.setStatus(ItemFulfillmentStatus.PICKED);
        }
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public List<PackingTaskDTO> startPacking(UUID id) {
        log.debug("Starting packing for fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.PICKING_COMPLETE) {
            throw new FulfillmentException("Fulfillment order must be in PICKING_COMPLETE status to start packing");
        }
        
        // Update status and timestamp
        order.setStatus(FulfillmentStatus.PACKING);
        order.setPackingStartedAt(LocalDateTime.now());
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        // Create packing tasks
        List<PackingTask> packingTasks = createPackingTasks(savedOrder);
        
        // Convert to DTOs
        return packingTasks.stream()
                .map(this::convertToPackingTaskDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Helper method to create packing tasks for a fulfillment order
     */
    private List<PackingTask> createPackingTasks(FulfillmentOrder order) {
        List<PackingTask> tasks = new ArrayList<>();
        
        // Create a packing task for the order
        // For simplicity, we'll create one task per order
        PackingTask task = PackingTask.builder()
                .fulfillmentOrderId(stringToUuid(order.getId().toString()))
                .status(TaskStatus.PENDING)
                .priority(order.getPriority() != null ? order.getPriority().ordinal() : 1)
                .packingStation("Pack-01") // This would be dynamically assigned in a real system
                .instruction("Pack all items for order " + order.getOrderReference())
                .dueBy(LocalDateTime.now().plusHours(2)) // Set due time to 2 hours from now
                .build();
        
        PackingTask savedTask = packingTaskRepository.save(task);
        tasks.add(savedTask);
        
        return tasks;
    }
    
    /**
     * Helper method to convert a PackingTask entity to DTO
     */
    private PackingTaskDTO convertToPackingTaskDTO(PackingTask task) {
        return PackingTaskDTO.builder()
                .id(stringToUuid(task.getId().toString()))
                .fulfillmentOrderId(task.getFulfillmentOrderId())
                .assignedStaffId(task.getAssignedStaffId())
                .status(task.getStatus())
                .priority(task.getPriority())
                .packingStation(task.getPackingStation())
                .instruction(task.getInstruction())
                .packagingType(task.getPackagingType())
                .weightKg(task.getWeightKg())
                .lengthCm(task.getLengthCm())
                .widthCm(task.getWidthCm())
                .heightCm(task.getHeightCm())
                .specialHandlingRequired(task.getSpecialHandlingRequired())
                .specialHandlingInstructions(task.getSpecialHandlingInstructions())
                .dueBy(task.getDueBy())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .completionNotes(task.getCompletionNotes())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
    
    @Override
    public FulfillmentOrderDTO completePacking(UUID id) {
        log.debug("Completing packing for fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.PACKING) {
            throw new FulfillmentException("Fulfillment order must be in PACKING status to complete packing");
        }
        
        // Verify all packing tasks are completed
        List<PackingTask> packingTasks = packingTaskRepository.findAllByFulfillmentOrderId(id);
        boolean allTasksCompleted = packingTasks.stream()
                .allMatch(task -> task.getStatus() == TaskStatus.COMPLETED);
        
        if (!allTasksCompleted) {
            throw new FulfillmentException("Cannot complete packing as some packing tasks are not completed");
        }
        
        // Update order status and timestamp
        order.setStatus(FulfillmentStatus.PACKING_COMPLETE);
        order.setPackingCompletedAt(LocalDateTime.now());
        
        // Update items status
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            item.setStatus(ItemFulfillmentStatus.PACKED);
            
            // Update packed quantity if not already set
            if (item.getQuantityPacked() == null || item.getQuantityPacked() == 0) {
                item.setQuantityPacked(item.getQuantity());
            }
        }
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO readyToShip(UUID id) {
        log.debug("Marking fulfillment order as ready to ship with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.PACKING_COMPLETE) {
            throw new FulfillmentException("Fulfillment order must be in PACKING_COMPLETE status to be ready to ship");
        }
        
        // Update status
        order.setStatus(FulfillmentStatus.READY_TO_SHIP);
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public ShipmentPackageDTO shipFulfillmentOrder(UUID id, ShipmentPackageDTO shipmentPackageDTO) {
        log.debug("Shipping fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.READY_TO_SHIP) {
            throw new FulfillmentException("Fulfillment order must be in READY_TO_SHIP status to be shipped");
        }
        
        // Set fulfillment order ID if not already set
        if (shipmentPackageDTO.getFulfillmentOrderId() == null) {
            shipmentPackageDTO.setFulfillmentOrderId(id);
        }
        
        // Create shipment package entity
        ShipmentPackage shipmentPackage = ShipmentPackage.builder()
                .fulfillmentOrderId(shipmentPackageDTO.getFulfillmentOrderId())
                .trackingNumber(shipmentPackageDTO.getTrackingNumber())
                .carrier(shipmentPackageDTO.getCarrier())
                .serviceLevel(shipmentPackageDTO.getServiceLevel())
                .weight(shipmentPackageDTO.getWeightKg())
                .length(shipmentPackageDTO.getLengthCm())
                .width(shipmentPackageDTO.getWidthCm())
                .height(shipmentPackageDTO.getHeightCm())
                .shippingCost(shipmentPackageDTO.getShippingCost())
                .currency(shipmentPackageDTO.getCurrency())
                .shippingLabelUrl(shipmentPackageDTO.getShippingLabelUrl())
                .shippingLabelFormat(shipmentPackageDTO.getShippingLabelFormat())
                .commercialInvoiceUrl(shipmentPackageDTO.getCommercialInvoiceUrl())
                .customsDeclarationNumber(shipmentPackageDTO.getCustomsDeclarationNumber())
                .estimatedDeliveryDate(shipmentPackageDTO.getEstimatedDeliveryDate())
                .shippedAt(LocalDateTime.now())
                .build();
        
        // Save shipment package
        ShipmentPackage savedPackage = shipmentPackageRepository.save(shipmentPackage);
        
        // Update order
        order.setStatus(FulfillmentStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        order.setTrackingNumber(shipmentPackageDTO.getTrackingNumber());
        order.setCarrier(shipmentPackageDTO.getCarrier());
        order.setEstimatedDeliveryDate(shipmentPackageDTO.getEstimatedDeliveryDate());
        
        // Update items status
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            item.setStatus(ItemFulfillmentStatus.SHIPPED);
        }
        
        // Save order changes
        fulfillmentOrderRepository.save(order);
        
        // Convert and return the saved package as DTO
        return convertToShipmentPackageDTO(savedPackage);
    }
    
    /**
     * Helper method to convert a ShipmentPackage entity to DTO
     */
    private ShipmentPackageDTO convertToShipmentPackageDTO(ShipmentPackage pkg) {
        return ShipmentPackageDTO.builder()
                .id(stringToUuid(pkg.getId().toString()))
                .fulfillmentOrderId(pkg.getFulfillmentOrderId())
                .trackingNumber(pkg.getTrackingNumber())
                .carrier(pkg.getCarrier())
                .serviceLevel(pkg.getServiceLevel())
                .weight(pkg.getWeightKg())
                .length(pkg.getLengthCm())
                .width(pkg.getWidthCm())
                .height(pkg.getHeightCm())
                .shippingCost(pkg.getShippingCost())
                .currency(pkg.getCurrency())
                .shippingLabelUrl(pkg.getShippingLabelUrl())
                .shippingLabelFormat(pkg.getShippingLabelFormat())
                .commercialInvoiceUrl(pkg.getCommercialInvoiceUrl())
                .customsDeclarationNumber(pkg.getCustomsDeclarationNumber())
                .estimatedDeliveryDate(pkg.getEstimatedDeliveryDate())
                .shippedAt(pkg.getShippedAt())
                .createdAt(pkg.getCreatedAt())
                .updatedAt(pkg.getUpdatedAt())
                .build();
    }
    
    @Override
    public FulfillmentOrderDTO markAsDelivered(UUID id) {
        log.debug("Marking fulfillment order as delivered with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.SHIPPED) {
            throw new FulfillmentException("Fulfillment order must be in SHIPPED status to be marked as delivered");
        }
        
        // Update status and timestamp
        order.setStatus(FulfillmentStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    @Override
    public FulfillmentOrderDTO completeFulfillmentOrder(UUID id) {
        log.debug("Completing fulfillment order with id: {}", id);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(id);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.DELIVERED) {
            throw new FulfillmentException("Fulfillment order must be in DELIVERED status to be completed");
        }
        
        // Update status
        order.setStatus(FulfillmentStatus.COMPLETED);
        
        // Update items status
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            item.setStatus(ItemFulfillmentStatus.FULFILLED);
            
            // Ensure fulfilled quantity matches ordered quantity
            item.setQuantityFulfilled(item.getQuantity());
        }
        
        // Save changes
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(order);
        
        return orderMapper.toDTO(savedOrder);
    }
    
    /**
     * Helper method to update order timestamps based on status
     */
    private void updateTimestampsBasedOnStatus(FulfillmentOrder order, FulfillmentStatus status) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (status) {
            case PROCESSING:
                order.setProcessingStartedAt(now);
                break;
            case PICKING:
                order.setPickingStartedAt(now);
                break;
            case PICKING_COMPLETE:
                order.setPickingCompletedAt(now);
                break;
            case PACKING:
                order.setPackingStartedAt(now);
                break;
            case PACKING_COMPLETE:
                order.setPackingCompletedAt(now);
                break;
            case SHIPPED:
                order.setShippedAt(now);
                break;
            case DELIVERED:
                order.setDeliveredAt(now);
                break;
            case CANCELLED:
                order.setCancelledAt(now);
                break;
            default:
                // No timestamp updates for other statuses
                break;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FulfillmentOrderItemDTO> getFulfillmentOrderItems(UUID fulfillmentOrderId) {
        log.debug("Getting items for fulfillment order with id: {}", fulfillmentOrderId);
        
        List<FulfillmentOrderItem> items = itemRepository.findAllByFulfillmentOrderId(fulfillmentOrderId);
        return itemMapper.toDTOList(items);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FulfillmentOrderItemDTO getFulfillmentOrderItem(UUID itemId) {
        log.debug("Getting fulfillment order item with id: {}", itemId);
        
        return itemRepository.findById(itemId)
                .map(itemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("FulfillmentOrderItem", "id", itemId));
    }
    
    @Override
    public FulfillmentOrderItemDTO updateFulfillmentOrderItem(UUID itemId, FulfillmentOrderItemDTO itemDTO) {
        log.debug("Updating fulfillment order item with id: {}", itemId);
        
        FulfillmentOrderItem existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("FulfillmentOrderItem", "id", itemId));
        
        // Update fields from DTO
        itemMapper.updateEntityFromDTO(existingItem, itemDTO);
        
        // Save changes
        FulfillmentOrderItem savedItem = itemRepository.save(existingItem);
        
        return itemMapper.toDTO(savedItem);
    }
    
    @Override
    public FulfillmentOrderItemDTO updateItemStatus(UUID itemId, ItemFulfillmentStatus status) {
        log.debug("Updating fulfillment order item status with id: {} to {}", itemId, status);
        
        FulfillmentOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("FulfillmentOrderItem", "id", itemId));
        
        // Update status
        item.setStatus(status);
        
        // Save changes
        FulfillmentOrderItem savedItem = itemRepository.save(item);
        
        return itemMapper.toDTO(savedItem);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FulfillmentOrderDTO> getOrdersReadyForPicking() {
        log.debug("Getting orders ready for picking");
        
        List<FulfillmentOrder> orders = fulfillmentOrderRepository.findAllOrdersReadyForPicking();
        return orderMapper.toDTOList(orders);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FulfillmentOrderDTO> getOrdersReadyForPacking() {
        log.debug("Getting orders ready for packing");
        
        List<FulfillmentOrder> orders = fulfillmentOrderRepository.findAllOrdersReadyForPacking();
        return orderMapper.toDTOList(orders);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PickingTaskDTO> getPickingTasks(UUID fulfillmentOrderId) {
        log.debug("Getting picking tasks for fulfillment order with id: {}", fulfillmentOrderId);
        
        List<PickingTask> tasks = pickingTaskRepository.findAllByFulfillmentOrderId(fulfillmentOrderId);
        return tasks.stream()
                .map(this::convertToPickingTaskDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PackingTaskDTO> getPackingTasks(UUID fulfillmentOrderId) {
        log.debug("Getting packing tasks for fulfillment order with id: {}", fulfillmentOrderId);
        
        List<PackingTask> tasks = packingTaskRepository.findAllByFulfillmentOrderId(fulfillmentOrderId);
        return tasks.stream()
                .map(this::convertToPackingTaskDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PickingTaskDTO assignPickingTask(UUID taskId, UUID staffId) {
        log.debug("Assigning picking task with id: {} to staff: {}", taskId, staffId);
        
        PickingTask task = pickingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("PickingTask", "id", taskId));
        
        // Validate status
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new FulfillmentException("Only tasks in PENDING status can be assigned");
        }
        
        // Update task
        task.setAssignedStaffId(staffId);
        
        // Save changes
        PickingTask savedTask = pickingTaskRepository.save(task);
        
        return convertToPickingTaskDTO(savedTask);
    }
    
    @Override
    public PickingTaskDTO startPickingTask(UUID taskId) {
        log.debug("Starting picking task with id: {}", taskId);
        
        PickingTask task = pickingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("PickingTask", "id", taskId));
        
        // Validate status and assignment
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new FulfillmentException("Only tasks in PENDING status can be started");
        }
        
        if (task.getAssignedStaffId() == null) {
            throw new FulfillmentException("Task must be assigned to a staff member before starting");
        }
        
        // Update task
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        
        // Save changes
        PickingTask savedTask = pickingTaskRepository.save(task);
        
        return convertToPickingTaskDTO(savedTask);
    }
    
    @Override
    public PickingTaskDTO completePickingTask(UUID taskId, String notes) {
        log.debug("Completing picking task with id: {}", taskId);
        
        PickingTask task = pickingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("PickingTask", "id", taskId));
        
        // Validate status
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new FulfillmentException("Only tasks in IN_PROGRESS status can be completed");
        }
        
        // Update task
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletionNotes(notes);
        
        // Save changes
        PickingTask savedTask = pickingTaskRepository.save(task);
        
        // Update order items to reflect picking progress
        FulfillmentOrder order = getFulfillmentOrderEntity(task.getFulfillmentOrderId());
        
        for (FulfillmentOrderItem item : order.getOrderItems()) {
            if (item.getQuantityPicked() == null || item.getQuantityPicked() == 0) {
                item.setQuantityPicked(item.getQuantity());
            }
        }
        
        fulfillmentOrderRepository.save(order);
        
        return convertToPickingTaskDTO(savedTask);
    }
    
    @Override
    public PackingTaskDTO assignPackingTask(UUID taskId, UUID staffId) {
        log.debug("Assigning packing task with id: {} to staff: {}", taskId, staffId);
        
        PackingTask task = packingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("PackingTask", "id", taskId));
        
        // Validate status
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new FulfillmentException("Only tasks in PENDING status can be assigned");
        }
        
        // Update task
        task.setAssignedStaffId(staffId);
        
        // Save changes
        PackingTask savedTask = packingTaskRepository.save(task);
        
        return convertToPackingTaskDTO(savedTask);
    }
    
    @Override
    public PackingTaskDTO startPackingTask(UUID taskId) {
        log.debug("Starting packing task with id: {}", taskId);
        
        PackingTask task = packingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("PackingTask", "id", taskId));
        
        // Validate status and assignment
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new FulfillmentException("Only tasks in PENDING status can be started");
        }
        
        if (task.getAssignedStaffId() == null) {
            throw new FulfillmentException("Task must be assigned to a staff member before starting");
        }
        
        // Update task
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        
        // Save changes
        PackingTask savedTask = packingTaskRepository.save(task);
        
        return convertToPackingTaskDTO(savedTask);
    }
    
    @Override
    public PackingTaskDTO completePackingTask(UUID taskId, String notes, Double weightKg,
                                          Double lengthCm, Double widthCm, Double heightCm) {
        log.debug("Completing packing task with id: {}", taskId);
        
        PackingTask task = packingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("PackingTask", "id", taskId));
        
        // Validate status
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new FulfillmentException("Only tasks in IN_PROGRESS status can be completed");
        }
        
        // Update task with dimensions
        task.complete(notes, weightKg, lengthCm, widthCm, heightCm);
        
        // Save changes
        PackingTask savedTask = packingTaskRepository.save(task);
        
        return convertToPackingTaskDTO(savedTask);
    }
    
    @Override
    public String generateShippingLabel(UUID fulfillmentOrderId) {
        log.debug("Generating shipping label for fulfillment order with id: {}", fulfillmentOrderId);
        
        FulfillmentOrder order = getFulfillmentOrderEntity(fulfillmentOrderId);
        
        // Validate status
        if (order.getStatus() != FulfillmentStatus.PACKING_COMPLETE && 
            order.getStatus() != FulfillmentStatus.READY_TO_SHIP) {
            throw new FulfillmentException(
                "Fulfillment order must be in PACKING_COMPLETE or READY_TO_SHIP status to generate shipping label");
        }
        
        // Get or create the shipment package
        List<ShipmentPackage> shipments = shipmentPackageRepository.findByFulfillmentOrderId(fulfillmentOrderId);
        if (shipments.isEmpty()) {
            throw new ResourceNotFoundException("ShipmentPackage", "fulfillmentOrderId", fulfillmentOrderId.toString());
        }
        ShipmentPackage shipment = shipments.get(0); // Get the first/only shipment package
        
        // Integration with shipping carrier API to generate the label
        try {
            String carrier = shipment.getCarrier().toLowerCase();
            String labelUrl;
            String trackingNumber;
            
            // For development purposes, we'll create a mock implementation
            // In a real implementation, this would be based on environment profiles
            // but using a simple boolean flag for now
            boolean isDevelopment = true; // In production, this would be determined by environment
            
            if (isDevelopment) {
                // Create mock tracking number if not already set
                if (shipment.getTrackingNumber() == null) {
                    trackingNumber = carrier.toUpperCase().substring(0, 3) + "-" + 
                            UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    shipment.setTrackingNumber(trackingNumber);
                }
                
                // Create mock label URL
                labelUrl = "https://shipping-labels.example.com/" + carrier + "/" + shipment.getId() + ".pdf";
            } else {
                // In production, we would integrate with the actual shipping carrier APIs
                switch (carrier) {
                    case "fedex":
                        // FedEx API integration would go here
                        labelUrl = "https://shipping-labels.prod.example.com/fedex/" + shipment.getId() + ".pdf";
                        trackingNumber = "FDX" + System.currentTimeMillis();
                        break;
                    case "ups":
                        // UPS API integration would go here
                        labelUrl = "https://shipping-labels.prod.example.com/ups/" + shipment.getId() + ".pdf";
                        trackingNumber = "1Z" + System.currentTimeMillis();
                        break;
                    case "usps":
                        // USPS API integration would go here
                        labelUrl = "https://shipping-labels.prod.example.com/usps/" + shipment.getId() + ".pdf";
                        trackingNumber = "9400" + System.currentTimeMillis();
                        break;
                    case "dhl":
                        // DHL API integration would go here
                        labelUrl = "https://shipping-labels.prod.example.com/dhl/" + shipment.getId() + ".pdf";
                        trackingNumber = "DHL" + System.currentTimeMillis();
                        break;
                    default:
                        // Generic shipping label
                        labelUrl = "https://shipping-labels.prod.example.com/generic/" + shipment.getId() + ".pdf";
                        trackingNumber = "GEN" + System.currentTimeMillis();
                }
                
                if (shipment.getTrackingNumber() == null) {
                    shipment.setTrackingNumber(trackingNumber);
                }
            }
            
            // Update the shipment with the label URL
            shipment.setShippingLabelUrl(labelUrl);
            shipmentPackageRepository.save(shipment);
        
        return labelUrl;
        } catch (Exception e) {
            log.error("Error generating shipping label: {}", e.getMessage(), e);
            throw new FulfillmentException("Error generating shipping label: " + e.getMessage());
        }
    }
    
    @Override
    public String trackShipment(String trackingNumber) {
        log.debug("Tracking shipment with tracking number: {}", trackingNumber);
        
        // TODO: Implement integration with shipping carrier API to get real tracking information
        // This is a mock implementation
        return "{\n" +
               "  \"carrier\": \"MOCK\",\n" +
               "  \"trackingNumber\": \"" + trackingNumber + "\",\n" +
               "  \"status\": \"In Transit\",\n" +
               "  \"estimatedDelivery\": \"" + LocalDateTime.now().plusDays(2).toLocalDate() + "\",\n" +
               "  \"events\": [\n" +
               "    {\n" +
               "      \"timestamp\": \"" + LocalDateTime.now().minusDays(1) + "\",\n" +
               "      \"location\": \"Sorting Facility\",\n" +
               "      \"description\": \"Package processed\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"timestamp\": \"" + LocalDateTime.now().minusHours(12) + "\",\n" +
               "      \"location\": \"Distribution Center\",\n" +
               "      \"description\": \"Package in transit\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"timestamp\": \"" + LocalDateTime.now().minusHours(2) + "\",\n" +
               "      \"location\": \"Local Facility\",\n" +
               "      \"description\": \"Out for delivery\"\n" +
               "    }\n" +
               "  ]\n" +
               "}";
    }
    
    @Override
    public int checkActiveOrdersForItem(String sku, UUID warehouseId) {
        log.info("Checking active orders for item with SKU: {} in warehouse: {}", sku, warehouseId);
        
        // Get all active orders in the warehouse that contain the SKU
        Long warehouseIdLong = uuidToLong(warehouseId);
        List<FulfillmentOrder> activeOrders = fulfillmentOrderRepository.findActiveOrdersWithSku(warehouseIdLong, sku);
        
        if (activeOrders.isEmpty()) {
            log.debug("No active orders found for SKU: {} in warehouse: {}", sku, warehouseId);
            return 0;
        }
        
        int ordersAffected = 0;
        
        for (FulfillmentOrder order : activeOrders) {
            // Skip orders not in a status that would be affected by inventory changes
            if (!isStatusAffectedByInventory(order.getStatus())) {
                continue;
            }
            
            // Get current inventory levels for this item
            boolean inventoryAvailable = checkItemInventoryAvailability(sku, warehouseId, 
                    getOrderQuantityForSku(order, sku));
            
            if (!inventoryAvailable && order.getStatus() == FulfillmentStatus.ALLOCATED) {
                // If we're allocated but inventory is now insufficient, we need to take action
                log.warn("Inventory no longer sufficient for allocated order: {} (SKU: {})", 
                        order.getId(), sku);
                
                // Try to find an alternative warehouse with sufficient inventory
                UUID alternateWarehouse = findAlternateWarehouseForSku(sku, 
                        getOrderQuantityForSku(order, sku), warehouseId);
                
                if (alternateWarehouse != null) {
                    log.info("Reassigning order: {} from warehouse: {} to warehouse: {}", 
                            order.getId(), warehouseId, alternateWarehouse);
                    
                    // Update the warehouse assignment
                    order.setAssignedWarehouseId(alternateWarehouse);
                    order.setReassignedAt(LocalDateTime.now());
                    order.setReassignmentReason("Inventory no longer available in original warehouse");
                    fulfillmentOrderRepository.save(order);
                    
                    // Re-allocate inventory in the new warehouse
                    // In a real implementation, this would release inventory in the old warehouse
                    // and allocate in the new one
                    
                    ordersAffected++;
                } else {
                    // No alternate warehouse available, mark the order as backordered
                    log.warn("No alternate warehouse available for order: {} (SKU: {}). Marking as BACKORDERED.", 
                            order.getId(), sku);
                    
                    order.setStatus(FulfillmentStatus.BACKORDERED);
                    order.setBackorderedAt(LocalDateTime.now());
                    order.setBackorderReason("Inventory no longer available");
                    fulfillmentOrderRepository.save(order);
                    
                    ordersAffected++;
                }
            }
        }
        
        log.info("Completed checking active orders for SKU: {}. Orders affected: {}", sku, ordersAffected);
        return ordersAffected;
    }
    
    @Override
    public void handleLowStockAlert(String sku, UUID warehouseId) {
        log.info("Handling low stock alert for SKU: {} in warehouse: {}", sku, warehouseId);
        
        // 1. Get all pending orders that contain this SKU
        List<FulfillmentOrder> pendingOrders = fulfillmentOrderRepository.findPendingOrdersWithSku(sku);
        
        if (pendingOrders.isEmpty()) {
            log.debug("No pending orders found for low stock SKU: {}", sku);
            return;
        }
        
        // 2. Sort orders by priority (based on order date, customer tier, etc.)
        pendingOrders.sort(this::compareOrderPriority);
        
        // 3. Get current available inventory
        Integer availableInventory = getAvailableInventory(sku, warehouseId);
        
        if (availableInventory == null || availableInventory <= 0) {
            log.warn("No inventory available for SKU: {} in warehouse: {}", sku, warehouseId);
            // Mark all pending orders as backordered
            for (FulfillmentOrder order : pendingOrders) {
                order.setStatus(FulfillmentStatus.BACKORDERED);
                order.setBackorderedAt(LocalDateTime.now());
                order.setBackorderReason("Insufficient inventory");
                fulfillmentOrderRepository.save(order);
            }
            return;
        }
        
        // 4. Allocate remaining inventory to highest priority orders
        int remainingInventory = availableInventory;
        
        for (FulfillmentOrder order : pendingOrders) {
            int orderQuantity = getOrderQuantityForSku(order, sku);
            
            if (remainingInventory >= orderQuantity) {
                // We have enough inventory for this order
                remainingInventory -= orderQuantity;
                
                // If order is in RECEIVED status, move it to PROCESSING
                if (order.getStatus() == FulfillmentStatus.RECEIVED) {
                    order.setStatus(FulfillmentStatus.PROCESSING);
                    order.setProcessingStartedAt(LocalDateTime.now());
                    fulfillmentOrderRepository.save(order);
                }
            } else {
                // Not enough inventory for this order
                order.setStatus(FulfillmentStatus.BACKORDERED);
                order.setBackorderedAt(LocalDateTime.now());
                order.setBackorderReason("Insufficient inventory due to low stock");
                fulfillmentOrderRepository.save(order);
            }
            
            if (remainingInventory <= 0) {
                break;
            }
        }
        
        log.info("Completed handling low stock alert for SKU: {}. Remaining inventory: {}", 
                sku, remainingInventory);
    }
    
    @Override
    public int reassignOrdersFromWarehouse(UUID warehouseId) {
        log.info("Reassigning orders from warehouse: {}", warehouseId);
        
        // Find all orders assigned to the warehouse that haven't been shipped yet
        Long warehouseIdLong = uuidToLong(warehouseId);
        List<FulfillmentOrder> ordersToReassign = fulfillmentOrderRepository
                .findByAssignedWarehouseIdAndStatusIn(
                        warehouseIdLong, 
                        Arrays.asList(
                                FulfillmentStatus.RECEIVED,
                                FulfillmentStatus.PROCESSING,
                                FulfillmentStatus.ALLOCATED,
                                FulfillmentStatus.PICKING,
                                FulfillmentStatus.PICKING_COMPLETE,
                                FulfillmentStatus.PACKING,
                                FulfillmentStatus.PACKING_COMPLETE,
                                FulfillmentStatus.READY_TO_SHIP
                        )
                );
        
        if (ordersToReassign.isEmpty()) {
            log.info("No orders to reassign from warehouse: {}", warehouseId);
            return 0;
        }
        
        int reassignedCount = 0;
        
        for (FulfillmentOrder order : ordersToReassign) {
            // Find the best alternate warehouse for this order
            UUID alternateWarehouse = findBestAlternateWarehouse(order, warehouseId);
            
            if (alternateWarehouse != null) {
                // We found an alternate warehouse
                log.info("Reassigning order: {} from warehouse: {} to warehouse: {}", 
                        order.getId(), warehouseId, alternateWarehouse);
                
                // Update the warehouse assignment
                order.setAssignedWarehouseId(alternateWarehouse);
                order.setReassignedAt(LocalDateTime.now());
                order.setReassignmentReason("Original warehouse unavailable");
                
                // Reset status based on current status
                if (order.getStatus().ordinal() > FulfillmentStatus.PROCESSING.ordinal()) {
                    // If we were already past PROCESSING, go back to PROCESSING
                    order.setStatus(FulfillmentStatus.PROCESSING);
                    order.setProcessingStartedAt(LocalDateTime.now());
                }
                
                fulfillmentOrderRepository.save(order);
                reassignedCount++;
            } else {
                // No alternate warehouse available
                log.warn("No alternate warehouse available for order: {}. Marking as ON_HOLD.", 
                        order.getId());
                
                order.setStatus(FulfillmentStatus.ON_HOLD);
                order.setHoldStartedAt(LocalDateTime.now());
                order.setHoldReason("Original warehouse unavailable and no alternate found");
                fulfillmentOrderRepository.save(order);
            }
        }
        
        log.info("Completed reassigning orders from warehouse: {}. Orders reassigned: {}", 
                warehouseId, reassignedCount);
        
        return reassignedCount;
    }
    
    // Helper methods
    
    /**
     * Check if a fulfillment status is affected by inventory changes
     */
    private boolean isStatusAffectedByInventory(FulfillmentStatus status) {
        return status == FulfillmentStatus.RECEIVED ||
               status == FulfillmentStatus.PROCESSING ||
               status == FulfillmentStatus.ALLOCATED;
    }
    
    /**
     * Get the order quantity for a specific SKU in an order
     */
    private int getOrderQuantityForSku(FulfillmentOrder order, String sku) {
        return order.getItems().stream()
                .filter(item -> item.getSku().equals(sku))
                .mapToInt(item -> item.getQuantity() - item.getQuantityFulfilled())
                .sum();
    }
    
    /**
     * Find an alternate warehouse for a specific SKU
     */
    private UUID findAlternateWarehouseForSku(String sku, int quantity, UUID currentWarehouseId) {
        // In a real implementation, this would query the Inventory Service
        // to find warehouses with sufficient inventory
        
        // Mock implementation for development
        // In production, this should be replaced with actual inventory service calls
        return UUID.randomUUID(); // Simulate finding an alternate warehouse
    }
    
    /**
     * Compare order priority for sorting
     * Higher priority orders should come first
     */
    private int compareOrderPriority(FulfillmentOrder o1, FulfillmentOrder o2) {
        // In a real implementation, this would consider multiple factors:
        // - Customer tier (premium, standard)
        // - Order date (older orders first)
        // - Order value (higher value first)
        // - Shipping method (express before standard)
        
        // For this implementation, we'll just use the order date
        return o1.getCreatedAt().compareTo(o2.getCreatedAt());
    }
    
    /**
     * Find the best alternate warehouse for an order
     */
    private UUID findBestAlternateWarehouse(FulfillmentOrder order, UUID currentWarehouseId) {
        // In a real implementation, this would:
        // 1. Find warehouses with sufficient inventory for all items
        // 2. Choose the warehouse closest to the delivery address
        // 3. Consider warehouse capacity and current workload
        
        // Mock implementation for development
        return UUID.randomUUID(); // Simulate finding an alternate warehouse
    }
    
    /**
     * Check if sufficient inventory is available for a specific SKU in a warehouse
     * 
     * @param sku the product SKU
     * @param warehouseId the warehouse ID
     * @param quantity the required quantity
     * @return true if sufficient inventory is available, false otherwise
     */
    private boolean checkItemInventoryAvailability(String sku, UUID warehouseId, int quantity) {
        int available = getAvailableInventory(sku, warehouseId);
        return available >= quantity;
    }
    
    /**
     * Get available inventory for a SKU in a warehouse
     *
     * @param sku the SKU to check
     * @param warehouseId the warehouse ID
     * @return the available quantity
     */
    private int getAvailableInventory(String sku, UUID warehouseId) {
        // In a real implementation, this would call the inventory service
        // For this demo, we'll simulate inventory availability
        return 100;  // Simulate having 100 units available
    }
    
    /**
     * Convert UUID to Long for entity ID mapping
     */
    private Long uuidToLong(UUID uuid) {
        return uuid != null ? uuid.getMostSignificantBits() & Long.MAX_VALUE : null;
    }
    
    /**
     * Convert Long to UUID for DTO ID mapping
     */
    private UUID longToUuid(Long id) {
        return id != null ? new UUID(id, 0) : null;
    }
    
    /**
     * Get FulfillmentOrder entity by UUID, handling ID conversion
     */
    private FulfillmentOrder getFulfillmentOrderEntity(UUID id) {
        String entityId = id.toString();
        return fulfillmentOrderRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("FulfillmentOrder", "id", id));
    }
} 
