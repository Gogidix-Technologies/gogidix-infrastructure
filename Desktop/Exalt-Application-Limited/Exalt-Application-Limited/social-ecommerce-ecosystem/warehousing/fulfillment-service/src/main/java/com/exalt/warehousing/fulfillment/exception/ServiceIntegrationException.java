package com.exalt.warehousing.fulfillment.exception;

/**
 * Exception thrown when there are issues integrating with external services
 */
public class ServiceIntegrationException extends RuntimeException {
    
    public ServiceIntegrationException(String message) {
        super(message);
    }
    
    public ServiceIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceIntegrationException(Throwable cause) {
        super(cause);
    }
}
