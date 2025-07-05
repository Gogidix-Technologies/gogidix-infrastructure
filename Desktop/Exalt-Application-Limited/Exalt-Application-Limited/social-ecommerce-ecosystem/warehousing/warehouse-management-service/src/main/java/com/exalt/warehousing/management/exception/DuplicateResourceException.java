package com.exalt.warehousing.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource that already exists
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message
     *
     * @param message the exception message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructor with resource name and field
     *
     * @param resourceName the resource name
     * @param fieldName the field name
     * @param fieldValue the field value
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue));
    }
} 
