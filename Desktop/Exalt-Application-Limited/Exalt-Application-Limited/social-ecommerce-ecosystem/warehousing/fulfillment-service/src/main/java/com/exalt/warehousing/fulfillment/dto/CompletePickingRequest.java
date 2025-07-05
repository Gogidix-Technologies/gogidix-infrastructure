package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for completing picking tasks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletePickingRequest {
    private UUID orderId;
    private UUID pickerId;
    private List<PickedItem> pickedItems;
    private String notes;
    private LocalDateTime completedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PickedItem {
        private UUID itemId;
        private String sku;
        private Integer quantityPicked;
        private String location;
        private String condition;
    }
}

