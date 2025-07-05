package com.exalt.warehousing.fulfillment.controller;

import com.exalt.warehousing.fulfillment.dto.ShipmentCreateRequestDTO;
import com.exalt.warehousing.fulfillment.dto.ShipmentPackageDTO;
import com.exalt.warehousing.fulfillment.dto.StatusUpdateDTO;
import com.exalt.warehousing.fulfillment.mapper.manual.ShipmentPackageManualMapper;
import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import com.exalt.warehousing.fulfillment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing shipments
 */
@RestController
@RequestMapping("/api/fulfillment/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Endpoints for managing shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentPackageManualMapper shipmentPackageMapper;

    @PostMapping
    @Operation(summary = "Create a new shipment for a fulfillment order")
    public ResponseEntity<List<ShipmentPackageDTO>> createShipment(
            @Valid @RequestBody ShipmentCreateRequestDTO createRequest) {
        List<ShipmentPackage> shipments = shipmentService.createShipment(
                createRequest.getFulfillmentOrderId().toString(), createRequest.getShippingMethod());
        
        List<ShipmentPackageDTO> shipmentDTOs = shipments.stream()
                .map(shipmentPackageMapper::toDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(shipmentDTOs, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a shipment by ID")
    public ResponseEntity<ShipmentPackageDTO> getShipment(@PathVariable UUID id) {
        ShipmentPackage shipment = shipmentService.getShipment(id.toString());
        return ResponseEntity.ok(shipmentPackageMapper.toDTO(shipment));
    }

    @GetMapping("/fulfillment-order/{fulfillmentOrderId}")
    @Operation(summary = "Get shipments by fulfillment order ID")
    public ResponseEntity<List<ShipmentPackageDTO>> getShipmentsByFulfillmentOrder(
            @PathVariable UUID fulfillmentOrderId) {
        List<ShipmentPackage> shipments = shipmentService.getShipmentsByFulfillmentOrder(fulfillmentOrderId.toString());
        
        List<ShipmentPackageDTO> shipmentDTOs = shipments.stream()
                .map(shipmentPackageMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(shipmentDTOs);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update shipment status")
    public ResponseEntity<ShipmentPackageDTO> updateShipmentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateDTO statusUpdate) {
        ShipmentStatus status = statusUpdate.getShipmentStatus();
        ShipmentPackage shipment = shipmentService.updateShipmentStatus(id.toString(), status);
        return ResponseEntity.ok(shipmentPackageMapper.toDTO(shipment));
    }

    @GetMapping("/{id}/label")
    @Operation(summary = "Generate a shipping label")
    public void generateShippingLabel(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"shipping-label-" + id + ".pdf\"");
        
        shipmentService.generateShippingLabel(id.toString(), response.getOutputStream());
    }

    @GetMapping("/{id}/tracking-url")
    @Operation(summary = "Get tracking URL for a shipment")
    public ResponseEntity<Map<String, String>> getTrackingUrl(@PathVariable UUID id) {
        String trackingUrl = shipmentService.getTrackingUrl(id.toString());
        return ResponseEntity.ok(Map.of("trackingUrl", trackingUrl));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a shipment")
    public ResponseEntity<ShipmentPackageDTO> cancelShipment(
            @PathVariable UUID id,
            @RequestParam String reason) {
        ShipmentPackage shipment = shipmentService.cancelShipment(id.toString(), reason);
        return ResponseEntity.ok(shipmentPackageMapper.toDTO(shipment));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get shipments by status")
    public ResponseEntity<List<ShipmentPackageDTO>> getShipmentsByStatus(
            @PathVariable ShipmentStatus status) {
        List<ShipmentPackage> shipments = shipmentService.getShipmentsByStatus(status);
        
        List<ShipmentPackageDTO> shipmentDTOs = shipments.stream()
                .map(shipmentPackageMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(shipmentDTOs);
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Find shipment by tracking number")
    public ResponseEntity<ShipmentPackageDTO> findByTrackingNumber(@PathVariable String trackingNumber) {
        ShipmentPackage shipment = shipmentService.findByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(shipmentPackageMapper.toDTO(shipment));
    }
} 
