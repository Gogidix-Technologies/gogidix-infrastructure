package com.exalt.warehousing.fulfillment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is an error with shipment processing
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShipmentException extends RuntimeException {

    public ShipmentException(String message) {
        super(message);
    }

    public ShipmentException(String message, Throwable cause) {
        super(message, cause);
    }
} 
