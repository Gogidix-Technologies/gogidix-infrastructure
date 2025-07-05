package com.exalt.warehousing.management.mapper;

import com.exalt.warehousing.management.dto.ReturnItemDTO;
import com.exalt.warehousing.management.model.ReturnItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between ReturnItem entities and DTOs
 */
@Component
public class ReturnItemMapper {

    /**
     * Convert a ReturnItem entity to a ReturnItemDTO
     *
     * @param returnItem the entity to convert
     * @return the DTO
     */
    public ReturnItemDTO toDTO(ReturnItem returnItem) {
        if (returnItem == null) {
            return null;
        }

        return ReturnItemDTO.builder()
                .id(returnItem.getId())
                .returnRequestId(returnItem.getReturnRequest() != null ? returnItem.getReturnRequest().getId() : null)
                .orderItemId(returnItem.getOrderItemId())
                .productId(returnItem.getProductId())
                .sku(returnItem.getSku())
                .productName(returnItem.getProductName())
                .quantity(returnItem.getQuantity())
                .status(returnItem.getStatus())
                .returnReason(returnItem.getReturnReason())
                .condition(returnItem.getCondition())
                .inspectionNotes(returnItem.getInspectionNotes())
                .inventoryItemId(returnItem.getInventoryItemId())
                .inventoryReintegrated(returnItem.isInventoryReintegrated())
                .reintegratedQuantity(returnItem.getReintegratedQuantity())
                .reintegratedLocationId(returnItem.getReintegratedLocationId())
                .refundProcessed(returnItem.isRefundProcessed())
                .refundAmount(returnItem.getRefundAmount())
                .createdAt(returnItem.getCreatedAt())
                .updatedAt(returnItem.getUpdatedAt())
                .notes(returnItem.getNotes() != null && !returnItem.getNotes().isEmpty() ? 
                    String.join("; ", returnItem.getNotes()) : null)
                .build();
    }

    /**
     * Convert a ReturnItemDTO to a ReturnItem entity
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    public ReturnItem toEntity(ReturnItemDTO dto) {
        if (dto == null) {
            return null;
        }

        return ReturnItem.builder()
                .id(dto.getId())
                .orderItemId(dto.getOrderItemId())
                .productId(dto.getProductId())
                .sku(dto.getSku())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .returnReason(dto.getReturnReason())
                .condition(dto.getCondition())
                .inspectionNotes(dto.getInspectionNotes())
                .inventoryItemId(dto.getInventoryItemId())
                .inventoryReintegrated(dto.isInventoryReintegrated())
                .reintegratedQuantity(dto.getReintegratedQuantity())
                .reintegratedLocationId(dto.getReintegratedLocationId())
                .refundProcessed(dto.isRefundProcessed())
                .refundAmount(dto.getRefundAmount())
                .notes(dto.getNotes() != null ? 
                    String.join("; ", java.util.Arrays.asList(dto.getNotes().split(";\\s*"))) : null)
                .build();
    }

    /**
     * Update an entity from a DTO
     *
     * @param existingItem the existing entity
     * @param dto the DTO with updated fields
     * @return the updated entity
     */
    public ReturnItem updateEntityFromDTO(ReturnItem existingItem, ReturnItemDTO dto) {
        if (dto == null) {
            return existingItem;
        }

        // Only update fields that are present in the DTO
        if (dto.getOrderItemId() != null) {
            existingItem.setOrderItemId(dto.getOrderItemId());
        }
        if (dto.getProductId() != null) {
            existingItem.setProductId(dto.getProductId());
        }
        if (dto.getSku() != null) {
            existingItem.setSku(dto.getSku());
        }
        if (dto.getProductName() != null) {
            existingItem.setProductName(dto.getProductName());
        }
        if (dto.getQuantity() > 0) {
            existingItem.setQuantity(dto.getQuantity());
        }
        if (dto.getStatus() != null) {
            existingItem.setStatus(dto.getStatus());
        }
        if (dto.getReturnReason() != null) {
            existingItem.setReturnReason(dto.getReturnReason());
        }
        if (dto.getCondition() != null) {
            existingItem.setCondition(dto.getCondition());
        }
        if (dto.getInspectionNotes() != null) {
            existingItem.setInspectionNotes(dto.getInspectionNotes());
        }
        if (dto.getInventoryItemId() != null) {
            existingItem.setInventoryItemId(dto.getInventoryItemId());
        }
        existingItem.setInventoryReintegrated(dto.isInventoryReintegrated());
        if (dto.getReintegratedQuantity() != null) {
            existingItem.setReintegratedQuantity(dto.getReintegratedQuantity());
        }
        if (dto.getReintegratedLocationId() != null) {
            existingItem.setReintegratedLocationId(dto.getReintegratedLocationId());
        }
        existingItem.setRefundProcessed(dto.isRefundProcessed());
        if (dto.getRefundAmount() != null) {
            existingItem.setRefundAmount(dto.getRefundAmount());
        }
        if (dto.getNotes() != null) {
            existingItem.setNotes(String.join("; ", java.util.Arrays.asList(dto.getNotes().split(";\\s*"))));
        }

        return existingItem;
    }

    /**
     * Convert a list of ReturnItem entities to DTOs
     *
     * @param returnItems the list of entities
     * @return the list of DTOs
     */
    public List<ReturnItemDTO> toDTOList(List<ReturnItem> returnItems) {
        if (returnItems == null) {
            return List.of();
        }
        return returnItems.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 
