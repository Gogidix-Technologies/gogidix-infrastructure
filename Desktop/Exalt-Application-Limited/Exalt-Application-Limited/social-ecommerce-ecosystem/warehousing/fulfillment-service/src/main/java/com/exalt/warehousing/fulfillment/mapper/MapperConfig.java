package com.exalt.warehousing.fulfillment.mapper;

import com.exalt.warehousing.fulfillment.enums.Priority;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * Shared configuration for all mappers
 */
@org.mapstruct.MapperConfig(componentModel = "spring")
public interface MapperConfig {

    /**
     * Convert Long to UUID
     */
    @Named("longToUuid")
    default UUID longToUuid(Long id) {
        if (id == null) {
            return null;
        }
        // Create a deterministic UUID from the Long value
        return new UUID(id, 0);
    }

    /**
     * Convert UUID to Long
     */
    @Named("uuidToLong")
    default Long uuidToLong(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        // Use the most significant bits as a unique Long value
        return uuid.getMostSignificantBits() & Long.MAX_VALUE;
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
            // If not a valid UUID, generate a deterministic UUID from the string
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

    /**
     * Map integer priority to Priority enum
     */
    @Named("mapPriority")
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
     * Map entity ItemFulfillmentStatus to DTO ItemFulfillmentStatus
     */
    @Named("mapEntityItemStatusToDto")
    default com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus mapEntityItemStatusToDto(
            com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }
        
        // Both entity and DTO now use the same enum from enums package
        return entityStatus;
    }
    
    /**
     * Map DTO ItemFulfillmentStatus to entity ItemFulfillmentStatus
     */
    @Named("mapDtoItemStatusToEntity")
    default com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus mapDtoItemStatusToEntity(
            com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus dtoStatus) {
        if (dtoStatus == null) {
            return null;
        }
        
        // Both entity and DTO now use the same enum from enums package
        return dtoStatus;
    }
}
