package com.exalt.warehousing.fulfillment.event;

import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import com.exalt.warehousing.fulfillment.enums.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event representing a change in a shipment package
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEvent {
    
    /**
     * Unique event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private String eventType;
    
    /**
     * ID of the shipment package
     */
    private UUID shipmentId;
    
    /**
     * ID of the related fulfillment order
     */
    private UUID fulfillmentOrderId;
    
    /**
     * Status of the shipment
     */
    private ShipmentStatus status;
    
    /**
     * Shipping method
     */
    private ShippingMethod shippingMethod;
    
    /**
     * Tracking number (if available)
     */
    private String trackingNumber;
    
    /**
     * Carrier (if available)
     */
    private String carrier;
    
    /**
     * Time the event was generated
     */
    private LocalDateTime timestamp;
} 
