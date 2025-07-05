package com.exalt.warehousing.management.exception;

/**
 * Exception thrown when label generation fails
 */
public class LabelGenerationException extends RuntimeException {

    /**
     * Constructs a new label generation exception with the specified detail message.
     *
     * @param message the detail message
     */
    public LabelGenerationException(String message) {
        super(message);
    }

    /**
     * Constructs a new label generation exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public LabelGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
} 
