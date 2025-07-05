package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.dto.ReturnLabelDTO;
import com.exalt.warehousing.management.dto.ReturnRequestDTO;
import com.exalt.warehousing.management.exception.LabelGenerationException;
import com.exalt.warehousing.management.model.ReturnRequest;
import com.exalt.warehousing.management.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for generating return labels for return requests
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnLabelGenerationService {

    private final ReturnRequestRepository returnRequestRepository;
    private final QRCodeGenerationService qrCodeGenerationService;
    
    /**
     * Generate a return label for a return request
     *
     * @param returnRequestId the return request ID
     * @return the generated return label DTO
     * @throws LabelGenerationException if label generation fails
     */
    @Transactional
    public ReturnLabelDTO generateReturnLabel(UUID returnRequestId) {
        log.info("Generating return label for return request: {}", returnRequestId);
        
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new LabelGenerationException("Return request not found: " + returnRequestId));
        
        // Generate tracking number if not already present
        String trackingNumber = returnRequest.getReturnTrackingNumber();
        if (trackingNumber == null || trackingNumber.isEmpty()) {
            trackingNumber = generateTrackingNumber(returnRequest);
            returnRequest.setReturnTrackingNumber(trackingNumber);
            returnRequestRepository.save(returnRequest);
        }
        
        // Generate QR code data
        String qrCodeData = generateQRCodeData(returnRequest);
        byte[] qrCodeImage = qrCodeGenerationService.generateQRCode(qrCodeData, 300);
        
        // Build return label DTO
        return ReturnLabelDTO.builder()
                .id(UUID.randomUUID())
                .returnRequestId(returnRequestId)
                .customerName(returnRequest.getCustomerName())
                .customerAddress(returnRequest.getCustomerAddress())
                .warehouseAddress(returnRequest.getWarehouseAddress())
                .trackingNumber(trackingNumber)
                .qrCodeData(qrCodeData)
                .qrCodeImage(qrCodeImage)
                .generatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
    }
    
    /**
     * Generate a tracking number for a return request
     *
     * @param returnRequest the return request
     * @return the generated tracking number
     */
    private String generateTrackingNumber(ReturnRequest returnRequest) {
        String prefix = "RTN";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String suffix = returnRequest.getId().toString().substring(0, 8);
        
        return prefix + "-" + timestamp + "-" + suffix;
    }
    
    /**
     * Generate QR code data for a return request
     *
     * @param returnRequest the return request
     * @return the QR code data
     */
    private String generateQRCodeData(ReturnRequest returnRequest) {
        Map<String, String> qrData = new HashMap<>();
        qrData.put("id", returnRequest.getId().toString());
        qrData.put("trackingNumber", returnRequest.getTrackingNumber());
        qrData.put("customerId", returnRequest.getCustomerId().toString());
        qrData.put("orderId", returnRequest.getOrderId().toString());
        qrData.put("warehouseId", returnRequest.getWarehouseId().toString());
        qrData.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        // Convert map to JSON-like string
        return qrData.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                .reduce("{", (a, b) -> a + (a.length() > 1 ? "," : "") + b) + "}";
    }
}
