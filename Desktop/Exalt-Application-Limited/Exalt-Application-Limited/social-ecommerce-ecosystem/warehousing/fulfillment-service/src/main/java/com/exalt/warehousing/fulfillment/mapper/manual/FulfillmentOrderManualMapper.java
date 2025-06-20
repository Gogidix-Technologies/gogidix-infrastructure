package com.exalt.warehousing.fulfillment.mapper.manual;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between FulfillmentOrder entity and DTO
 * This implementation avoids the need for MapStruct until it can be properly configured
 */
@Component
public class FulfillmentOrderManualMapper {

    private final FulfillmentOrderItemManualMapper itemMapper;

    @Autowired
    public FulfillmentOrderManualMapper(FulfillmentOrderItemManualMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    /**
     * Convert entity to DTO
     * 
     * @param fulfillmentOrder the entity to convert
     * @return the DTO
     */
    public FulfillmentOrderDTO toDTO(FulfillmentOrder fulfillmentOrder) {
        if (fulfillmentOrder == null) {
            return null;
        }
        
        FulfillmentOrderDTO dto = new FulfillmentOrderDTO();
        dto.setId(stringToUuid(fulfillmentOrder.getId().toString()));
        dto.setOrderId(stringToUuid(fulfillmentOrder.getExternalOrderId()));
        dto.setOrderReference(fulfillmentOrder.getOrderNumber());
        dto.setStatus(fulfillmentOrder.getStatus());
        dto.setAssignedWarehouseId(fulfillmentOrder.getWarehouseId() != null ? 
            stringToUuid(fulfillmentOrder.getWarehouseId().toString()) : null);
        dto.setPriority(fulfillmentOrder.getPriority() != null ? fulfillmentOrder.getPriority().ordinal() : 0);
        dto.setCustomerName(fulfillmentOrder.getCustomerName());
        dto.setCustomerEmail(fulfillmentOrder.getCustomerEmail());
        dto.setCustomerPhone(fulfillmentOrder.getCustomerPhone());
        
        // Map embedded address to individual fields
        if (fulfillmentOrder.getShippingAddress() != null) {
            dto.setShippingAddressLine1(fulfillmentOrder.getShippingAddress().getAddressLine1());
            dto.setShippingAddressLine2(fulfillmentOrder.getShippingAddress().getAddressLine2());
            dto.setShippingCity(fulfillmentOrder.getShippingAddress().getCity());
            dto.setShippingStateProvince(fulfillmentOrder.getShippingAddress().getState());
            dto.setShippingPostalCode(fulfillmentOrder.getShippingAddress().getPostalCode());
            dto.setShippingCountry(fulfillmentOrder.getShippingAddress().getCountry());
        }
        
        dto.setShippingInstructions(fulfillmentOrder.getShippingInstructions());
        dto.setShippingMethod(parseShippingMethod(fulfillmentOrder.getShippingMethod()));
        dto.setCarrier(fulfillmentOrder.getCarrier());
        dto.setTrackingNumber(fulfillmentOrder.getTrackingNumber());
        dto.setProcessingStartedAt(fulfillmentOrder.getPickStartedAt()); // Map to available field
        dto.setPickingStartedAt(fulfillmentOrder.getPickStartedAt());
        dto.setPickingCompletedAt(fulfillmentOrder.getPickCompletedAt());
        dto.setPackingStartedAt(fulfillmentOrder.getPackStartedAt());
        dto.setPackingCompletedAt(fulfillmentOrder.getPackCompletedAt());
        dto.setShippedAt(fulfillmentOrder.getShippedAt());
        dto.setEstimatedDeliveryDate(fulfillmentOrder.getEstimatedDeliveryDate());
        dto.setDeliveredAt(fulfillmentOrder.getDeliveredAt());
        dto.setCancelledAt(fulfillmentOrder.getCancelledAt());
        dto.setCancellationReason(fulfillmentOrder.getStatusReason());
        dto.setNotes(fulfillmentOrder.getInternalNotes());
        dto.setCreatedAt(fulfillmentOrder.getCreatedAt());
        dto.setUpdatedAt(fulfillmentOrder.getUpdatedAt());
        
        // Map items if available
        if (fulfillmentOrder.getOrderItems() != null && !fulfillmentOrder.getOrderItems().isEmpty()) {
            List<FulfillmentOrderItemDTO> itemDTOs = fulfillmentOrder.getOrderItems().stream()
                    .map(itemMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        } else {
            dto.setItems(new ArrayList<>());
        }
        
        // Add calculated fields
        dto.setItemCount(fulfillmentOrder.getOrderItems() != null ? fulfillmentOrder.getOrderItems().size() : 0);
        dto.setCompleted(com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.DELIVERED.equals(fulfillmentOrder.getStatus()) ||
                        com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.COMPLETED.equals(fulfillmentOrder.getStatus()));
        dto.setActive(!com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.CANCELLED.equals(fulfillmentOrder.getStatus()) &&
                     !com.exalt.warehousing.fulfillment.enums.FulfillmentStatus.DELIVERED.equals(fulfillmentOrder.getStatus()));
        
        return dto;
    }

    /**
     * Convert DTO to entity
     * 
     * @param dto the DTO to convert
     * @return the entity
     */
    public FulfillmentOrder toEntity(FulfillmentOrderDTO dto) {
        if (dto == null) {
            return null;
        }
        
        FulfillmentOrder entity = new FulfillmentOrder();
        // ID will be auto-generated, don't set it from DTO
        entity.setExternalOrderId(uuidToString(dto.getOrderId()));
        entity.setOrderNumber(dto.getOrderReference());
        entity.setStatus(dto.getStatus());
        entity.setWarehouseId(dto.getAssignedWarehouseId() != null ? 
            dto.getAssignedWarehouseId().getMostSignificantBits() & Long.MAX_VALUE : null);
        entity.setPriority(mapPriority(dto.getPriority()));
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerEmail(dto.getCustomerEmail());
        entity.setCustomerPhone(dto.getCustomerPhone());
        
        // Map individual address fields to embedded Address object
        if (dto.getShippingAddressLine1() != null || dto.getShippingCity() != null) {
            com.exalt.warehousing.fulfillment.entity.Address address = new com.exalt.warehousing.fulfillment.entity.Address();
            address.setAddressLine1(dto.getShippingAddressLine1());
            address.setAddressLine2(dto.getShippingAddressLine2());
            address.setCity(dto.getShippingCity());
            address.setState(dto.getShippingStateProvince());
            address.setPostalCode(dto.getShippingPostalCode());
            address.setCountry(dto.getShippingCountry());
            entity.setShippingAddress(address);
        }
        
        entity.setShippingInstructions(dto.getShippingInstructions());
        entity.setShippingMethod(dto.getShippingMethod() != null ? dto.getShippingMethod().name() : null);
        entity.setCarrier(dto.getCarrier());
        entity.setTrackingNumber(dto.getTrackingNumber());
        entity.setPickStartedAt(dto.getPickingStartedAt());
        entity.setPickCompletedAt(dto.getPickingCompletedAt());
        entity.setPackStartedAt(dto.getPackingStartedAt());
        entity.setPackCompletedAt(dto.getPackingCompletedAt());
        entity.setShippedAt(dto.getShippedAt());
        entity.setEstimatedDeliveryDate(dto.getEstimatedDeliveryDate());
        entity.setDeliveredAt(dto.getDeliveredAt());
        entity.setCancelledAt(dto.getCancelledAt());
        entity.setStatusReason(dto.getCancellationReason());
        entity.setInternalNotes(dto.getNotes());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        // Items need to be set separately to avoid circular references
        
        return entity;
    }

    /**
     * Convert DTOs to entities
     * 
     * @param dtos list of DTOs to convert
     * @return set of entities
     */
    public Set<FulfillmentOrderItem> itemDTOsToEntities(List<FulfillmentOrderItemDTO> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        
        return dtos.stream()
                .map(itemMapper::toEntity)
                .collect(Collectors.toSet());
    }

    /**
     * Convert entities to DTOs
     * 
     * @param items set of entities to convert
     * @return list of DTOs
     */
    public List<FulfillmentOrderItemDTO> itemsToItemDTOs(Set<FulfillmentOrderItem> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        
        return items.stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert String to UUID
     */
    private java.util.UUID stringToUuid(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return java.util.UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return java.util.UUID.nameUUIDFromBytes(str.getBytes());
        }
    }
    
    /**
     * Convert UUID to String
     */
    private String uuidToString(java.util.UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
    
    /**
     * Parse shipping method string to enum
     */
    private com.exalt.warehousing.fulfillment.enums.ShippingMethod parseShippingMethod(String method) {
        if (method == null || method.trim().isEmpty()) {
            return com.exalt.warehousing.fulfillment.enums.ShippingMethod.STANDARD;
        }
        try {
            return com.exalt.warehousing.fulfillment.enums.ShippingMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return com.exalt.warehousing.fulfillment.enums.ShippingMethod.STANDARD;
        }
    }
    
    /**
     * Map integer priority to Priority enum
     */
    private com.exalt.warehousing.fulfillment.enums.Priority mapPriority(Integer priority) {
        if (priority == null) {
            return com.exalt.warehousing.fulfillment.enums.Priority.STANDARD;
        }
        com.exalt.warehousing.fulfillment.enums.Priority[] values = com.exalt.warehousing.fulfillment.enums.Priority.values();
        if (priority >= 0 && priority < values.length) {
            return values[priority];
        }
        return com.exalt.warehousing.fulfillment.enums.Priority.STANDARD;
    }
} 
