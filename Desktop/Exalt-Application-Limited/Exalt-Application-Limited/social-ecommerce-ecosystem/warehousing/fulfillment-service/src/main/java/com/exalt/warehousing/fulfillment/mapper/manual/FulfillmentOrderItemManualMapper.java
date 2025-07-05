package com.exalt.warehousing.fulfillment.mapper.manual;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between FulfillmentOrderItem entity and DTO
 * This implementation avoids the need for MapStruct until it can be properly configured
 */
@Component
public class FulfillmentOrderItemManualMapper {

    /**
     * Convert entity to DTO
     * 
     * @param item the entity to convert
     * @return the DTO
     */
    public FulfillmentOrderItemDTO toDTO(FulfillmentOrderItem item) {
        if (item == null) {
            return null;
        }
        
        FulfillmentOrderItemDTO dto = new FulfillmentOrderItemDTO();
        dto.setId(stringToUuid(item.getId().toString()));
        
        if (item.getFulfillmentOrder() != null) {
            dto.setFulfillmentOrderId(stringToUuid(item.getFulfillmentOrder().getId().toString()));
        }
        
        dto.setOrderItemId(stringToUuid(item.getOrderItemId()));
        dto.setProductId(stringToUuid(item.getProductId()));
        dto.setSku(item.getSku());
        dto.setProductName(item.getProductName());
        dto.setProductImageUrl(item.getProductImageUrl());
        dto.setQuantity(item.getQuantity());
        dto.setQuantityFulfilled(item.getQuantityFulfilled());
        dto.setQuantityPicked(item.getQuantityPicked());
        dto.setQuantityPacked(item.getQuantityPacked());
        dto.setBinLocation(item.getBinLocation());
        dto.setSpecialInstructions(item.getSpecialInstructions());
        dto.setStatus(item.getStatus());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        
        // Calculated fields
        dto.setFullyFulfilled(item.isFullyFulfilled());
        dto.setPartiallyFulfilled(item.isPartiallyFulfilled());
        dto.setFullyPicked(item.isFullyPicked());
        dto.setRemainingQuantity(item.getRemainingQuantity());
        
        return dto;
    }

    /**
     * Convert DTO to entity
     * 
     * @param dto the DTO to convert
     * @return the entity
     */
    public FulfillmentOrderItem toEntity(FulfillmentOrderItemDTO dto) {
        if (dto == null) {
            return null;
        }
        
        FulfillmentOrderItem entity = new FulfillmentOrderItem();
        // ID will be auto-generated, don't set it from DTO
        // FulfillmentOrder must be set separately
        entity.setOrderItemId(uuidToString(dto.getOrderItemId()));
        entity.setProductId(uuidToString(dto.getProductId()));
        entity.setSku(dto.getSku());
        entity.setProductName(dto.getProductName());
        entity.setProductImageUrl(dto.getProductImageUrl());
        entity.setQuantity(dto.getQuantity());
        entity.setQuantityFulfilled(dto.getQuantityFulfilled());
        entity.setQuantityPicked(dto.getQuantityPicked());
        entity.setQuantityPacked(dto.getQuantityPacked());
        entity.setBinLocation(dto.getBinLocation());
        entity.setSpecialInstructions(dto.getSpecialInstructions());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        return entity;
    }

    /**
     * Update entity from DTO
     *
     * @param entity the entity to update
     * @param dto the DTO with new values
     * @return the updated entity
     */
    public FulfillmentOrderItem updateEntityFromDTO(FulfillmentOrderItem entity, FulfillmentOrderItemDTO dto) {
        if (dto == null) {
            return entity;
        }
        
        entity.setSku(dto.getSku());
        if (dto.getProductName() != null) {
            entity.setProductName(dto.getProductName());
        }
        entity.setProductImageUrl(dto.getProductImageUrl());
        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }
        entity.setQuantityFulfilled(dto.getQuantityFulfilled());
        entity.setQuantityPicked(dto.getQuantityPicked());
        entity.setQuantityPacked(dto.getQuantityPacked());
        entity.setBinLocation(dto.getBinLocation());
        entity.setSpecialInstructions(dto.getSpecialInstructions());
        entity.setStatus(dto.getStatus());
        
        return entity;
    }

    /**
     * Convert list of entities to list of DTOs
     *
     * @param items the list of entities
     * @return the list of DTOs
     */
    public List<FulfillmentOrderItemDTO> toDTOList(List<FulfillmentOrderItem> items) {
        return items.stream()
                .map(this::toDTO)
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
} 
