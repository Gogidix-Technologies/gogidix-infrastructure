package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.ReturnItemCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for return quality assessment results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnQualityAssessmentDTO {
    
    /**
     * ID of the return item being assessed
     */
    private UUID returnItemId;
    
    /**
     * The assessed condition of the return item
     */
    private ReturnItemCondition assessedCondition;
    
    /**
     * When the assessment was performed
     */
    private LocalDateTime assessmentDate;
    
    /**
     * Calculated resale value of the item
     */
    private double resaleValue;
    
    /**
     * Resale value as a percentage of original price
     */
    private double resaleValuePercentage;
    
    /**
     * Recommended action based on the assessment
     */
    private String recommendedAction;
    
    /**
     * Results of image analysis if images were provided
     */
    private List<String> imageAnalysisResults;
} 
