package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for return labels
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnLabelDTO {
    
    /**
     * Unique identifier for the return label
     */
    private UUID id;
    
    /**
     * ID of the associated return request
     */
    private UUID returnRequestId;
    
    /**
     * Name of the customer
     */
    private String customerName;
    
    /**
     * Address of the customer
     */
    private String customerAddress;
    
    /**
     * Address of the warehouse
     */
    private String warehouseAddress;
    
    /**
     * Tracking number for the return
     */
    private String trackingNumber;
    
    /**
     * Data encoded in the QR code
     */
    private String qrCodeData;
    
    /**
     * QR code image as byte array
     */
    private byte[] qrCodeImage;
    
    /**
     * Base64 encoded QR code image
     */
    private String qrCodeBase64;
    
    /**
     * When the label was generated
     */
    private LocalDateTime generatedAt;
    
    /**
     * When the label expires
     */
    private LocalDateTime expiresAt;
} 
