package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for vendor fulfillment comparison
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorFulfillmentComparison {
    private String vendorId;
    private String vendorName;
    private Double performanceScore;
    private Integer ordersProcessed;
    private String status;
}

