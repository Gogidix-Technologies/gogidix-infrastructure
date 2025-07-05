package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for order tracking information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackingResponse {
    private UUID orderId;
    private String orderNumber;
    private String status;
    private String trackingNumber;
    private String courierCode;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime lastUpdate;
    private List<TrackingEvent> events;
    private String currentLocation;
    private String notes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEvent {
        private LocalDateTime timestamp;
        private String status;
        private String location;
        private String description;
        private String updatedBy;
    }
}

