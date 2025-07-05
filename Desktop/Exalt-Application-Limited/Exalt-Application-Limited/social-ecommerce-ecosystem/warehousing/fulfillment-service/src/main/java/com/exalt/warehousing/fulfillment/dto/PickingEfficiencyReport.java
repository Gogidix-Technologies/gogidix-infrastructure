package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO for picking efficiency reports
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingEfficiencyReport {
    private String reportId;
    private String period;
    private Double averagePickTime;
    private Integer totalItems;
    private Double efficiency;
    private String status;
}

