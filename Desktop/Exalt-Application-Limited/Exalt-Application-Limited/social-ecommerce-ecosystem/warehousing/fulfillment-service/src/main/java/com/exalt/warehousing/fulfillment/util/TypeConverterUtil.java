package com.exalt.warehousing.fulfillment.util;

import java.util.UUID;

/**
 * Utility class for type conversions to avoid code duplication
 */
public class TypeConverterUtil {
    
    private TypeConverterUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert String to UUID safely
     */
    public static UUID stringToUuid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Convert UUID to String safely
     */
    public static String uuidToString(UUID value) {
        return value != null ? value.toString() : null;
    }
    
    /**
     * Convert Long to UUID (for legacy compatibility)
     * This is a placeholder - in production you'd use a proper ID mapping
     */
    public static UUID longToUuid(Long value) {
        if (value == null) {
            return null;
        }
        // Create a UUID from the long value
        return new UUID(0L, value);
    }
    
    /**
     * Convert UUID to Long (for legacy compatibility)
     * This is a placeholder - in production you'd use a proper ID mapping
     */
    public static Long uuidToLong(UUID value) {
        if (value == null) {
            return null;
        }
        // Extract the least significant bits as Long
        return value.getLeastSignificantBits();
    }
}
