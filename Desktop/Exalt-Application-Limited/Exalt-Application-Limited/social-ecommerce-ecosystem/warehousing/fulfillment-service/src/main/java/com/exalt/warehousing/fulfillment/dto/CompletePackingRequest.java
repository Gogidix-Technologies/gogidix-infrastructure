package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for completing packing tasks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletePackingRequest {
    private UUID orderId;
    private UUID packerId;
    private List<PackedItem> packedItems;
    private String packageType;
    private BigDecimal weight;
    private String dimensions;
    private String notes;
    private LocalDateTime completedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackedItem {
        private UUID itemId;
        private String sku;
        private Integer quantityPacked;
        private String packageId;
        private String condition;
    }
}

