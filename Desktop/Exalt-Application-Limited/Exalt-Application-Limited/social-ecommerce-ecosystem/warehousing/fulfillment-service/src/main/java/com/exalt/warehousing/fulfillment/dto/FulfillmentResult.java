package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Result object for fulfillment operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentResult {
    
    private boolean success;
    private String message;
    private UUID fulfillmentOrderId;
    private String status;
    private LocalDateTime timestamp;
    
    public FulfillmentResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}