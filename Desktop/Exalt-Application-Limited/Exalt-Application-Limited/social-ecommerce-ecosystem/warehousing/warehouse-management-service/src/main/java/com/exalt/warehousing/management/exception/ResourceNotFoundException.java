package com.exalt.warehousing.management.exception;

import java.util.UUID;

// Note: Although a shared BaseResourceNotFoundException exists, the IDE linter
// in the warehouse-management module sometimes fails to resolve the cross-module
// dependency during partial builds.  To avoid blocking compilation we fall back
// to extending RuntimeException directly in this module; once the multi-module
// build is wired up in CI the class can again delegate to the shared base.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(String.format("%s not found with id: %s", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
} 
