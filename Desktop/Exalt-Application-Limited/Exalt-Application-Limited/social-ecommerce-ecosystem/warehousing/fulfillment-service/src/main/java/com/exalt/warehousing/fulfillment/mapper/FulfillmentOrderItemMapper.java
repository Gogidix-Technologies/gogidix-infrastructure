package com.exalt.warehousing.fulfillment.mapper;

import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderItemDTO;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting between FulfillmentOrderItem entity and DTO
 */
@Mapper(componentModel = "spring")
public interface FulfillmentOrderItemMapper {

    /**
     * Convert entity to DTO
     * 
     * @param item the entity to convert
     * @return the DTO
     */
    @Mapping(source = "fulfillmentOrder.id", target = "fulfillmentOrderId", qualifiedByName = "stringToUuid")
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "orderItemId", source = "orderItemId", qualifiedByName = "stringToUuid")
    @Mapping(target = "productId", source = "productId", qualifiedByName = "stringToUuid")
    @Mapping(target = "status", qualifiedByName = "mapEntityItemStatusToDto")
    @Mapping(target = "fullyFulfilled", expression = "java(item.isFullyFulfilled())")
    @Mapping(target = "partiallyFulfilled", expression = "java(item.isPartiallyFulfilled())")
    @Mapping(target = "fullyPicked", expression = "java(item.isFullyPicked())")
    @Mapping(target = "remainingQuantity", expression = "java(item.getRemainingQuantity())")
    FulfillmentOrderItemDTO toDTO(FulfillmentOrderItem item);

    /**
     * Convert DTO to entity
     * 
     * @param dto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "fulfillmentOrder", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItemId", source = "orderItemId", qualifiedByName = "uuidToString")
    @Mapping(target = "productId", source = "productId", qualifiedByName = "uuidToString")
    @Mapping(target = "status", qualifiedByName = "mapDtoItemStatusToEntity")
    FulfillmentOrderItem toEntity(FulfillmentOrderItemDTO dto);

    /**
     * Convert DTO to entity with fulfillment order
     * 
     * @param dto the DTO to convert
     * @param fulfillmentOrder the fulfillment order to associate
     * @return the entity
     */
    @Mapping(target = "fulfillmentOrder", source = "fulfillmentOrder")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", source = "dto.status")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FulfillmentOrderItem toEntity(FulfillmentOrderItemDTO dto, FulfillmentOrder fulfillmentOrder);

    /**
     * Update entity from DTO
     *
     * @param entity the entity to update
     * @param dto the DTO with new values
     */
    @Mapping(target = "fulfillmentOrder", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(@MappingTarget FulfillmentOrderItem entity, FulfillmentOrderItemDTO dto);

    /**
     * Convert list of entities to list of DTOs
     *
     * @param items the list of entities
     * @return the list of DTOs
     */
    List<FulfillmentOrderItemDTO> toDTOList(List<FulfillmentOrderItem> items);
    
    /**
     * Map entity ItemFulfillmentStatus to DTO ItemFulfillmentStatus
     */
    @Named("mapEntityItemStatusToDto")
    default com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus mapEntityItemStatusToDto(
            com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }
        
        // Map entity status to entity status
        switch (entityStatus) {
            case READY_FOR_PICKING:
            case PICKING:
                return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PICKED;
            case PACKING:
                return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PACKED;
            case DAMAGED:
                return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.CANCELLED;
            case RETURNED:
                return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.CANCELLED;
            default:
                // Try to match by name
                try {
                    return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.valueOf(entityStatus.name());
                } catch (IllegalArgumentException e) {
                    // If no exact match, return a default
                    return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PENDING;
                }
        }
    }
    
    /**
     * Map DTO ItemFulfillmentStatus to entity ItemFulfillmentStatus
     */
    @Named("mapDtoItemStatusToEntity")
    default com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus mapDtoItemStatusToEntity(
            com.exalt.warehousing.fulfillment.enums.ItemStatus dtoStatus) {
        if (dtoStatus == null) {
            return null;
        }
        
        // Map DTO status to entity status
        switch (dtoStatus) {
            case SHIPPED:
                return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.SHIPPED;
            case SUBSTITUTED:
                return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.CANCELLED;
            default:
                // Try to match by name
                try {
                    return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.valueOf(dtoStatus.name());
                } catch (IllegalArgumentException e) {
                    // If no exact match, return a default
                    return com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus.PENDING;
                }
        }
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
