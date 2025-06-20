package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for quality check operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityCheckRequest {
    private UUID orderId;
    private UUID checkerId;
    private List<QualityCheckItem> items;
    private String overallStatus;
    private String notes;
    private LocalDateTime checkedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualityCheckItem {
        private UUID itemId;
        private String sku;
        private String condition;
        private Boolean passedQuality;
        private String defectDescription;
        private String action; // ACCEPT, REJECT, RETURN
    }
}

