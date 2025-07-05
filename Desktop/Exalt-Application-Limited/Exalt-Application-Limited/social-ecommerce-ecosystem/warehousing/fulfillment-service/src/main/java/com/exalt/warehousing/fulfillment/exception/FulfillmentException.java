package com.exalt.warehousing.fulfillment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is an error with fulfillment processing
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FulfillmentException extends RuntimeException {

    /**
     * Constructor for FulfillmentException
     *
     * @param message the error message
     */
    public FulfillmentException(String message) {
        super(message);
    }

    /**
     * Constructor for FulfillmentException with cause
     *
     * @param message the error message
     * @param cause the cause
     */
    public FulfillmentException(String message, Throwable cause) {
        super(message, cause);
    }
} 
