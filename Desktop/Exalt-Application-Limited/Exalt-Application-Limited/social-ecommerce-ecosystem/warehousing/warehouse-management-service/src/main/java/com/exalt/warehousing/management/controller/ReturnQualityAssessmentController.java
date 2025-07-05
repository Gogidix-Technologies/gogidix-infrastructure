package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.ReturnQualityAssessmentDTO;
import com.exalt.warehousing.management.service.ReturnQualityAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controller for return quality assessment operations
 */
@RestController
@RequestMapping("/api/v1/warehousing/returns/quality-assessment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Return Quality Assessment", description = "Operations for assessing the quality of returned items")
public class ReturnQualityAssessmentController {

    private final ReturnQualityAssessmentService qualityAssessmentService;

    /**
     * Assess the quality of a returned item
     *
     * @param returnItemId the return item ID
     * @param images optional images of the returned item
     * @return the quality assessment result
     */
    @PostMapping(value = "/{returnItemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Assess return item quality",
               description = "Assesses the quality of a returned item, optionally using provided images",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Quality assessment completed successfully",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ReturnQualityAssessmentDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Return item not found"),
                   @ApiResponse(responseCode = "500", description = "Error assessing return item quality")
               })
    public ResponseEntity<ReturnQualityAssessmentDTO> assessReturnItemQuality(
            @PathVariable UUID returnItemId,
            @RequestParam(required = false) List<MultipartFile> images) {
        log.info("Assessing quality of return item: {} with {} images", 
                returnItemId, images != null ? images.size() : 0);
        
        ReturnQualityAssessmentDTO assessment = qualityAssessmentService.assessReturnItemQuality(returnItemId, images);
        
        return ResponseEntity.ok(assessment);
    }

    /**
     * Get the quality assessment for a returned item
     *
     * @param returnItemId the return item ID
     * @return the quality assessment result
     */
    @GetMapping("/{returnItemId}")
    @Operation(summary = "Get return item quality assessment",
               description = "Gets the quality assessment for a returned item",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Quality assessment retrieved successfully",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ReturnQualityAssessmentDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Return item not found or not assessed"),
                   @ApiResponse(responseCode = "500", description = "Error retrieving quality assessment")
               })
    public ResponseEntity<ReturnQualityAssessmentDTO> getReturnItemQualityAssessment(@PathVariable UUID returnItemId) {
        log.info("Getting quality assessment for return item: {}", returnItemId);
        
        // This would typically retrieve an existing assessment
        // For now, we'll just perform a new assessment without images
        ReturnQualityAssessmentDTO assessment = qualityAssessmentService.assessReturnItemQuality(returnItemId, null);
        
        return ResponseEntity.ok(assessment);
    }
} 
