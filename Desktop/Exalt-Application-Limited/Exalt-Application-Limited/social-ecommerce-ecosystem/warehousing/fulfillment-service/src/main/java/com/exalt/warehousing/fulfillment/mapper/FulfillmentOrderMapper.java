package com.exalt.warehousing.fulfillment.mapper;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import com.exalt.warehousing.fulfillment.enums.Priority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Mapper for converting between FulfillmentOrder entity and DTO
 */
@Mapper(componentModel = "spring")
public interface FulfillmentOrderMapper {

    /**
     * Convert entity to DTO
     * 
     * @param fulfillmentOrder the entity to convert
     * @return the DTO
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "itemCount", expression = "java(fulfillmentOrder.getOrderItems() != null ? fulfillmentOrder.getOrderItems().size() : 0)")
    @Mapping(target = "priority", expression = "java(fulfillmentOrder.getPriority() != null ? fulfillmentOrder.getPriority().ordinal() : 0)")
    @Mapping(target = "orderId", source = "externalOrderId", qualifiedByName = "stringToUuid")
    @Mapping(target = "orderReference", source = "orderNumber")
    @Mapping(target = "assignedWarehouseId", expression = "java(fulfillmentOrder.getWarehouseId() != null ? new java.util.UUID(fulfillmentOrder.getWarehouseId(), 0) : null)")
    @Mapping(target = "shippingAddressLine1", source = "shippingAddress.addressLine1")
    @Mapping(target = "shippingAddressLine2", source = "shippingAddress.addressLine2")
    @Mapping(target = "shippingCity", source = "shippingAddress.city")
    @Mapping(target = "shippingStateProvince", source = "shippingAddress.state")
    @Mapping(target = "shippingPostalCode", source = "shippingAddress.postalCode")
    @Mapping(target = "shippingCountry", source = "shippingAddress.country")
    @Mapping(target = "shippingInstructions", source = "shippingNotes")
    @Mapping(target = "shippingMethod", ignore = true) // Will need custom mapping
    @Mapping(target = "processingStartedAt", source = "pickStartedAt")
    @Mapping(target = "pickingStartedAt", source = "pickStartedAt")
    @Mapping(target = "pickingCompletedAt", source = "pickCompletedAt")
    @Mapping(target = "packingStartedAt", source = "packStartedAt")
    @Mapping(target = "packingCompletedAt", source = "packCompletedAt")
    @Mapping(target = "cancellationReason", source = "statusReason")
    @Mapping(target = "notes", source = "internalNotes")
    @Mapping(target = "completed", expression = "java(fulfillmentOrder.isCompleted())")
    @Mapping(target = "active", expression = "java(!fulfillmentOrder.isCompleted())")
    @Mapping(target = "status", qualifiedByName = "mapEntityStatusToDto")
    FulfillmentOrderDTO toDTO(FulfillmentOrder fulfillmentOrder);

    /**
     * Convert DTO to entity
     * 
     * @param dto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", source = "items")
    @Mapping(target = "priority", expression = "java(mapPriority(dto.getPriority()))")
    @Mapping(target = "externalOrderId", source = "orderId", qualifiedByName = "uuidToString")
    @Mapping(target = "orderNumber", source = "orderReference")
    @Mapping(target = "warehouseId", expression = "java(dto.getAssignedWarehouseId() != null ? dto.getAssignedWarehouseId().getMostSignificantBits() & Long.MAX_VALUE : null)")
    @Mapping(target = "shippingAddress.addressLine1", source = "shippingAddressLine1")
    @Mapping(target = "shippingAddress.addressLine2", source = "shippingAddressLine2")
    @Mapping(target = "shippingAddress.city", source = "shippingCity")
    @Mapping(target = "shippingAddress.state", source = "shippingStateProvince")
    @Mapping(target = "shippingAddress.postalCode", source = "shippingPostalCode")
    @Mapping(target = "shippingAddress.country", source = "shippingCountry")
    @Mapping(target = "shippingNotes", source = "shippingInstructions")
    @Mapping(target = "pickStartedAt", source = "pickingStartedAt")
    @Mapping(target = "pickCompletedAt", source = "pickingCompletedAt")
    @Mapping(target = "packStartedAt", source = "packingStartedAt")
    @Mapping(target = "packCompletedAt", source = "packingCompletedAt")
    @Mapping(target = "statusReason", source = "cancellationReason")
    @Mapping(target = "internalNotes", source = "notes")
    @Mapping(target = "status", qualifiedByName = "mapDtoStatusToEntity")
    FulfillmentOrder toEntity(FulfillmentOrderDTO dto);

    /**
     * Convert list of DTOs to set of entities
     * 
     * @param dtos list of DTOs
     * @return set of entities
     */
    Set<FulfillmentOrderItem> itemDTOsToEntities(List<FulfillmentOrderItemDTO> dtos);

    /**
     * Convert set of entities to list of DTOs
     * 
     * @param items set of entities
     * @return list of DTOs
     */
    List<FulfillmentOrderItemDTO> itemsToItemDTOs(Set<FulfillmentOrderItem> items);
    
    /**
     * Update entity from DTO
     * 
     * @param entity the entity to update
     * @param dto the DTO containing the updates
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true) // Don't update items
    @Mapping(target = "priority", expression = "java(mapPriority(dto.getPriority()))")
    @Mapping(target = "status", qualifiedByName = "mapDtoStatusToEntity")
    void updateEntityFromDTO(@MappingTarget FulfillmentOrder entity, FulfillmentOrderDTO dto);
    
    /**
     * Convert list of entities to list of DTOs
     * 
     * @param fulfillmentOrders list of entities
     * @return list of DTOs
     */
    List<FulfillmentOrderDTO> toDTOList(List<FulfillmentOrder> fulfillmentOrders);
    
    /**
     * Map entity FulfillmentStatus to DTO FulfillmentStatus
     */
    @Named("mapEntityStatusToDto")
    default com.exalt.warehousing.fulfillment.enums.FulfillmentStatus mapEntityStatusToDto(
            com.exalt.warehousing.fulfillment.enums.FulfillmentStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }
        
        // Map entity status to entity status (direct mapping for standardized entity usage)
        return entityStatus;
    }
    
    /**
     * Map DTO FulfillmentStatus to entity FulfillmentStatus
     */
    @Named("mapDtoStatusToEntity")
    default com.exalt.warehousing.fulfillment.enums.FulfillmentStatus mapDtoStatusToEntity(
            com.exalt.warehousing.fulfillment.enums.FulfillmentStatus dtoStatus) {
        if (dtoStatus == null) {
            return null;
        }
        
        // Direct mapping for standardized entity usage
        return dtoStatus;
    }
    
    /**
     * Map integer priority to Priority enum
     */
    default Priority mapPriority(Integer priority) {
        if (priority == null) {
            return Priority.STANDARD;
        }
        Priority[] values = Priority.values();
        if (priority >= 0 && priority < values.length) {
            return values[priority];
        }
        return Priority.STANDARD;
    }
    
    /**
     * Convert String to UUID
     */
    @Named("stringToUuid")
    default UUID stringToUuid(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(str.getBytes());
        }
    }
    
    /**
     * Convert UUID to String
     */
    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
} 
