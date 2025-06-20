package com.exalt.warehousing.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a warehouse cannot be found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class WarehouseNotFoundException extends RuntimeException {
    
    public WarehouseNotFoundException(String message) {
        super(message);
    }
    
    public WarehouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
