package com.exalt.warehousing.management.mapper;

import com.exalt.warehousing.management.dto.ReturnItemDTO;
import com.exalt.warehousing.management.dto.ReturnRequestDTO;
import com.exalt.warehousing.management.model.ReturnItem;
import com.exalt.warehousing.management.model.ReturnItemStatus;
import com.exalt.warehousing.management.model.ReturnRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between ReturnRequest entities and DTOs
 */
@Component
public class ReturnRequestMapper {

    private final ReturnItemMapper returnItemMapper;

    public ReturnRequestMapper(ReturnItemMapper returnItemMapper) {
        this.returnItemMapper = returnItemMapper;
    }

    /**
     * Convert a ReturnRequest entity to a ReturnRequestDTO
     *
     * @param returnRequest the entity to convert
     * @return the DTO
     */
    public ReturnRequestDTO toDTO(ReturnRequest returnRequest) {
        if (returnRequest == null) {
            return null;
        }

        List<ReturnItemDTO> returnItemDTOs = returnRequest.getReturnItems().stream()
                .map(returnItemMapper::toDTO)
                .collect(Collectors.toList());

        ReturnRequestDTO dto = ReturnRequestDTO.builder()
                .id(returnRequest.getId())
                .orderId(returnRequest.getOrderId())
                .customerId(returnRequest.getCustomerId())
                .warehouseId(returnRequest.getWarehouseId())
                .returnCode(returnRequest.getReturnCode())
                .returnReason(returnRequest.getReturnReason())
                .status(returnRequest.getStatus())
                .returnItems(returnItemDTOs)
                .qualityCheckNotes(returnRequest.getQualityCheckNotes())
                .refundProcessed(returnRequest.isRefundProcessed())
                .inventoryReintegrated(returnRequest.isInventoryReintegrated())
                .processedByStaffId(returnRequest.getProcessedByStaffId())
                .processedAt(returnRequest.getProcessedAt())
                .receivedAt(returnRequest.getReceivedAt())
                .createdAt(returnRequest.getCreatedAt())
                .updatedAt(returnRequest.getUpdatedAt())
                .returnTrackingNumber(returnRequest.getReturnTrackingNumber())
                .notes(returnRequest.getNotes())
                .totalItemCount(returnRequest.getReturnItems().size())
                .build();

        // Calculate metrics for display
        int acceptedItemCount = (int) returnRequest.getReturnItems().stream()
                .filter(item -> item.getStatus() == ReturnItemStatus.ACCEPTED || 
                               item.getStatus() == ReturnItemStatus.REINTEGRATED)
                .count();

        int rejectedItemCount = (int) returnRequest.getReturnItems().stream()
                .filter(item -> item.getStatus() == ReturnItemStatus.REJECTED)
                .count();

        Double totalRefundAmount = returnRequest.getReturnItems().stream()
                .filter(ReturnItem::isRefundProcessed)
                .mapToDouble(item -> item.getRefundAmount() != null ? item.getRefundAmount() : 0.0)
                .sum();

        dto.setAcceptedItemCount(acceptedItemCount);
        dto.setRejectedItemCount(rejectedItemCount);
        dto.setTotalRefundAmount(totalRefundAmount > 0 ? totalRefundAmount : null);

        return dto;
    }

    /**
     * Convert a ReturnRequestDTO to a ReturnRequest entity
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    public ReturnRequest toEntity(ReturnRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ReturnRequest returnRequest = ReturnRequest.builder()
                .id(dto.getId())
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .warehouseId(dto.getWarehouseId())
                .returnCode(dto.getReturnCode())
                .returnReason(dto.getReturnReason())
                .status(dto.getStatus())
                .qualityCheckNotes(dto.getQualityCheckNotes())
                .refundProcessed(dto.isRefundProcessed())
                .inventoryReintegrated(dto.isInventoryReintegrated())
                .processedByStaffId(dto.getProcessedByStaffId())
                .processedAt(dto.getProcessedAt())
                .receivedAt(dto.getReceivedAt())
                .returnTrackingNumber(dto.getReturnTrackingNumber())
                .notes(dto.getNotes())
                .build();

        // Timestamps usually set by JPA
        if (dto.getCreatedAt() != null) {
            returnRequest.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getUpdatedAt() != null) {
            returnRequest.setUpdatedAt(dto.getUpdatedAt());
        }

        return returnRequest;
    }

    /**
     * Update an entity from a DTO
     *
     * @param existingRequest the existing entity
     * @param dto the DTO with updated fields
     * @return the updated entity
     */
    public ReturnRequest updateEntityFromDTO(ReturnRequest existingRequest, ReturnRequestDTO dto) {
        if (dto == null) {
            return existingRequest;
        }

        // Only update fields that are present in the DTO
        if (dto.getOrderId() != null) {
            existingRequest.setOrderId(dto.getOrderId());
        }
        if (dto.getCustomerId() != null) {
            existingRequest.setCustomerId(dto.getCustomerId());
        }
        if (dto.getWarehouseId() != null) {
            existingRequest.setWarehouseId(dto.getWarehouseId());
        }
        if (dto.getReturnCode() != null) {
            existingRequest.setReturnCode(dto.getReturnCode());
        }
        if (dto.getReturnReason() != null) {
            existingRequest.setReturnReason(dto.getReturnReason());
        }
        if (dto.getStatus() != null) {
            existingRequest.setStatus(dto.getStatus());
        }
        if (dto.getQualityCheckNotes() != null) {
            existingRequest.setQualityCheckNotes(dto.getQualityCheckNotes());
        }
        existingRequest.setRefundProcessed(dto.isRefundProcessed());
        existingRequest.setInventoryReintegrated(dto.isInventoryReintegrated());
        if (dto.getProcessedByStaffId() != null) {
            existingRequest.setProcessedByStaffId(dto.getProcessedByStaffId());
        }
        if (dto.getProcessedAt() != null) {
            existingRequest.setProcessedAt(dto.getProcessedAt());
        }
        if (dto.getReceivedAt() != null) {
            existingRequest.setReceivedAt(dto.getReceivedAt());
        }
        if (dto.getReturnTrackingNumber() != null) {
            existingRequest.setReturnTrackingNumber(dto.getReturnTrackingNumber());
        }
        if (dto.getNotes() != null) {
            existingRequest.setNotes(dto.getNotes());
        }

        return existingRequest;
    }

    /**
     * Convert a list of ReturnRequest entities to DTOs
     *
     * @param returnRequests the list of entities
     * @return the list of DTOs
     */
    public List<ReturnRequestDTO> toDTOList(List<ReturnRequest> returnRequests) {
        if (returnRequests == null) {
            return List.of();
        }
        return returnRequests.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 
