package com.exalt.warehousing.management.exception;

/**
 * Exception thrown when shipping integration fails
 */
public class ShippingIntegrationException extends RuntimeException {

    /**
     * Constructs a new shipping integration exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ShippingIntegrationException(String message) {
        super(message);
    }

    /**
     * Constructs a new shipping integration exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ShippingIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
} 
