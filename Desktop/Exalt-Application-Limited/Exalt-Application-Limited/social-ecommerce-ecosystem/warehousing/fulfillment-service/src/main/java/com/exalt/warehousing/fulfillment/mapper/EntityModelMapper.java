package com.exalt.warehousing.fulfillment.mapper;

// Use fully qualified class names to avoid import conflicts
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
// Don't import model.FulfillmentOrder - use fully qualified name instead
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.InventoryStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper to convert between entity and model objects to solve the architecture conflict
 * where both model and entity packages have overlapping classes.
 * 
 * This temporary solution allows the codebase to compile while preserving both
 * entity and model classes until a more permanent refactoring can be done.
 */
@Mapper(componentModel = "spring")
@Component
public interface EntityModelMapper {

    // Model mapping methods removed - no longer needed since model package has been deprecated
    // All functionality now uses entity classes directly

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
}
