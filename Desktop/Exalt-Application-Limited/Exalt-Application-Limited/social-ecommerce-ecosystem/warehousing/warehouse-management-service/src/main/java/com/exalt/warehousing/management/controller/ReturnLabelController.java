package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.ReturnLabelDTO;
import com.exalt.warehousing.management.service.QRCodeGenerationService;
import com.exalt.warehousing.management.service.ReturnLabelGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for return label generation
 */
@RestController
@RequestMapping("/api/v1/warehousing/returns/labels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Return Labels", description = "Operations for generating and managing return labels")
public class ReturnLabelController {

    private final ReturnLabelGenerationService labelGenerationService;
    private final QRCodeGenerationService qrCodeGenerationService;

    /**
     * Generate a return label for a return request
     *
     * @param returnRequestId the return request ID
     * @return the generated return label DTO
     */
    @GetMapping("/{returnRequestId}")
    @Operation(summary = "Generate return label",
               description = "Generates a return label for a return request",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Return label generated successfully",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ReturnLabelDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Return request not found"),
                   @ApiResponse(responseCode = "500", description = "Error generating return label")
               })
    public ResponseEntity<ReturnLabelDTO> generateReturnLabel(@PathVariable UUID returnRequestId) {
        log.info("Generating return label for return request: {}", returnRequestId);
        
        ReturnLabelDTO label = labelGenerationService.generateReturnLabel(returnRequestId);
        
        // Add base64 encoded QR code
        if (label.getQrCodeImage() != null) {
            String base64QrCode = java.util.Base64.getEncoder().encodeToString(label.getQrCodeImage());
            label.setQrCodeBase64(base64QrCode);
        }
        
        return ResponseEntity.ok(label);
    }

    /**
     * Get QR code image for a return label
     *
     * @param returnRequestId the return request ID
     * @return the QR code image
     */
    @GetMapping("/{returnRequestId}/qrcode")
    @Operation(summary = "Get QR code image",
               description = "Gets the QR code image for a return label",
               responses = {
                   @ApiResponse(responseCode = "200", description = "QR code image retrieved successfully",
                                content = @Content(mediaType = "image/png")),
                   @ApiResponse(responseCode = "404", description = "Return request not found"),
                   @ApiResponse(responseCode = "500", description = "Error retrieving QR code image")
               })
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable UUID returnRequestId) {
        log.info("Getting QR code image for return request: {}", returnRequestId);
        
        ReturnLabelDTO label = labelGenerationService.generateReturnLabel(returnRequestId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        
        return new ResponseEntity<>(label.getQrCodeImage(), headers, HttpStatus.OK);
    }

    /**
     * Generate a PDF return label for a return request
     *
     * @param returnRequestId the return request ID
     * @return the PDF return label
     */
    @GetMapping("/{returnRequestId}/pdf")
    @Operation(summary = "Generate PDF return label",
               description = "Generates a PDF return label for a return request",
               responses = {
                   @ApiResponse(responseCode = "200", description = "PDF return label generated successfully",
                                content = @Content(mediaType = "application/pdf")),
                   @ApiResponse(responseCode = "404", description = "Return request not found"),
                   @ApiResponse(responseCode = "500", description = "Error generating PDF return label")
               })
    public ResponseEntity<byte[]> generatePDFReturnLabel(@PathVariable UUID returnRequestId) {
        log.info("Generating PDF return label for return request: {}", returnRequestId);
        
        // This would typically call a PDF generation service
        // For now, we'll just return a placeholder message
        String message = "PDF generation not yet implemented for return request: " + returnRequestId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        
        return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.OK);
    }
} 
