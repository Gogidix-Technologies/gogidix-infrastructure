package com.exalt.warehousing.management.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating QR codes
 */
@Service
@Slf4j
public class QRCodeGenerationService {

    /**
     * Generate a QR code image from the provided data
     *
     * @param data the data to encode in the QR code
     * @param size the size of the QR code in pixels
     * @return byte array containing the QR code image in PNG format
     */
    public byte[] generateQRCode(String data, int size) {
        log.info("Generating QR code of size {} for data length: {}", size, data.length());
        
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate a QR code as a base64 encoded string
     *
     * @param data the data to encode in the QR code
     * @param size the size of the QR code in pixels
     * @return base64 encoded string representing the QR code image
     */
    public String generateQRCodeAsBase64(String data, int size) {
        byte[] qrCodeBytes = generateQRCode(data, size);
        return java.util.Base64.getEncoder().encodeToString(qrCodeBytes);
    }
} 
