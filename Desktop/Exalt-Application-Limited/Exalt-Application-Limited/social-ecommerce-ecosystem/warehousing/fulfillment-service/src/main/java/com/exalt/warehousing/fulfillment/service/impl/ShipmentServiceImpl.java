package com.exalt.warehousing.fulfillment.service.impl;

import com.exalt.warehousing.fulfillment.exception.ResourceNotFoundException;
import com.exalt.warehousing.fulfillment.exception.ShipmentException;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import com.exalt.warehousing.fulfillment.enums.ShippingMethod;
import com.exalt.warehousing.fulfillment.repository.FulfillmentOrderRepository;
import com.exalt.warehousing.fulfillment.repository.ShipmentPackageRepository;
import com.exalt.warehousing.fulfillment.service.FulfillmentOrderService;
import com.exalt.warehousing.fulfillment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ShipmentService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentPackageRepository shipmentPackageRepository;
    private final FulfillmentOrderRepository fulfillmentOrderRepository;
    private final FulfillmentOrderService fulfillmentOrderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public List<ShipmentPackage> createShipment(String fulfillmentOrderId, ShippingMethod shippingMethod) {
        log.info("Creating shipment for fulfillment order: {} with method: {}", fulfillmentOrderId, shippingMethod);
        
        // Get the fulfillment order
        FulfillmentOrder order = fulfillmentOrderRepository.findById(fulfillmentOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment order not found with ID: " + fulfillmentOrderId));
        
        // Validate order status - must be ready to ship
        if (order.getStatus() != FulfillmentStatus.READY_TO_SHIP && order.getStatus() != FulfillmentStatus.PACKING_COMPLETE) {
            throw new ShipmentException("Fulfillment order must be in READY_TO_SHIP or PACKING_COMPLETE status to create shipment");
        }
        
        // Create shipment package(s) for the order
        List<ShipmentPackage> shipments = optimizeShipmentPackaging(order);
        
        // Set shipping method and carrier based on shipping method
        shipments.forEach(shipment -> {
            shipment.setServiceLevel(shippingMethod.toString());
            
            // Determine carrier based on shipping method and package weight
            String carrier = determineCarrier(shippingMethod, shipment.getWeight());
            shipment.setCarrier(carrier);
            
            // Generate tracking number
            shipment.setTrackingNumber(generateTrackingNumber(carrier));
            
            // Set status to created
            shipment.setStatus(ShipmentStatus.CREATED);
            
            // Set created timestamp
            shipment.setCreatedAt(LocalDateTime.now());
            shipment.setUpdatedAt(LocalDateTime.now());
        });
        
        // Save the shipments
        List<ShipmentPackage> savedShipments = shipmentPackageRepository.saveAll(shipments);
        
        // Update fulfillment order status to SHIPPED
        fulfillmentOrderService.updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.SHIPPED);
        
        // Update order tracking number with the first shipment's tracking number if multiple shipments
        if (!savedShipments.isEmpty()) {
            order.setTrackingNumber(savedShipments.get(0).getTrackingNumber());
            order.setCarrier(savedShipments.get(0).getCarrier());
            order.setShippedAt(LocalDateTime.now());
            fulfillmentOrderRepository.save(order);
        }
        
        // Publish shipment created events
        savedShipments.forEach(this::publishShipmentEvent);
        
        return savedShipments;
    }

    @Override
    @Transactional
    public ShipmentPackage updateShipmentStatus(String shipmentId, ShipmentStatus status) {
        log.info("Updating shipment {} status to: {}", shipmentId, status);
        
        ShipmentPackage shipment = shipmentPackageRepository.findById(UUID.fromString(shipmentId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
        
        shipment.setStatus(status);
        shipment.setUpdatedAt(LocalDateTime.now());
        
        // Set appropriate timestamps based on status
        if (status == ShipmentStatus.SHIPPED) {
            shipment.setShippedAt(LocalDateTime.now());
        } else if (status == ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(LocalDateTime.now());
            
            // Also update the fulfillment order if all shipments are delivered
            checkAndUpdateFulfillmentOrderDeliveryStatus(UUID.fromString(shipment.getFulfillmentOrder().getId().toString()));
        }
        
        // Save and publish event
        ShipmentPackage updatedShipment = shipmentPackageRepository.save(shipment);
        publishShipmentEvent(updatedShipment);
        
        return updatedShipment;
    }

    @Override
    public ShipmentPackage getShipment(String shipmentId) {
        log.debug("Getting shipment with ID: {}", shipmentId);
        return shipmentPackageRepository.findById(UUID.fromString(shipmentId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
    }

    @Override
    public List<ShipmentPackage> getShipmentsByFulfillmentOrder(String fulfillmentOrderId) {
        log.debug("Getting shipments for fulfillment order: {}", fulfillmentOrderId);
        return shipmentPackageRepository.findByFulfillmentOrderId(UUID.fromString(fulfillmentOrderId));
    }

    @Override
    public void generateShippingLabel(String shipmentId, OutputStream outputStream) {
        log.info("Generating shipping label for shipment: {}", shipmentId);
        
        ShipmentPackage shipment = shipmentPackageRepository.findById(UUID.fromString(shipmentId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
        
        // In a real implementation, this would generate a PDF shipping label and write to the output stream
        // For this implementation, we'll just log the request
        log.info("Generating label for shipment {} to {}, carrier: {}", 
                shipmentId, shipment.getFulfillmentOrder().getShippingCity(), shipment.getCarrier());
        
        // Mock implementation - in a real system this would call the shipping carrier's API to generate a label
        try {
            String labelContent = "MOCK SHIPPING LABEL\n" +
                    "Tracking: " + shipment.getTrackingNumber() + "\n" +
                    "Carrier: " + shipment.getCarrier() + "\n" +
                    "To: " + shipment.getFulfillmentOrder().getCustomerName() + "\n" +
                    "Address: " + shipment.getFulfillmentOrder().getShippingAddressLine1() + "\n" +
                    shipment.getFulfillmentOrder().getShippingCity() + ", " + 
                    shipment.getFulfillmentOrder().getShippingStateProvince() + " " +
                    shipment.getFulfillmentOrder().getShippingPostalCode() + "\n" +
                    shipment.getFulfillmentOrder().getShippingCountry();
            
            outputStream.write(labelContent.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            throw new ShipmentException("Error generating shipping label: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ShipmentPackage cancelShipment(String shipmentId, String reason) {
        log.info("Cancelling shipment: {} with reason: {}", shipmentId, reason);
        
        ShipmentPackage shipment = shipmentPackageRepository.findById(UUID.fromString(shipmentId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
        
        // Can only cancel if not already shipped
        if (shipment.getStatus() == ShipmentStatus.SHIPPED || shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new ShipmentException("Cannot cancel shipment that has already been shipped or delivered");
        }
        
        shipment.setStatus(ShipmentStatus.CANCELLED);
        shipment.setCancellationReason(reason);
        shipment.setCancelledAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());
        
        // Save and publish event
        ShipmentPackage cancelledShipment = shipmentPackageRepository.save(shipment);
        publishShipmentEvent(cancelledShipment);
        
        return cancelledShipment;
    }

    @Override
    public String getTrackingUrl(String shipmentId) {
        log.debug("Getting tracking URL for shipment: {}", shipmentId);
        
        ShipmentPackage shipment = shipmentPackageRepository.findById(UUID.fromString(shipmentId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
        
        // Generate tracking URL based on carrier
        String carrier = shipment.getCarrier();
        String trackingNumber = shipment.getTrackingNumber();
        
        // This would be replaced with actual carrier tracking URLs in a real implementation
        if (carrier.equalsIgnoreCase("UPS")) {
            return "https://www.ups.com/track?tracknum=" + trackingNumber;
        } else if (carrier.equalsIgnoreCase("FedEx")) {
            return "https://www.fedex.com/fedextrack/?trknbr=" + trackingNumber;
        } else if (carrier.equalsIgnoreCase("DHL")) {
            return "https://www.dhl.com/en/express/tracking.html?AWB=" + trackingNumber;
        } else {
            return "https://tracking.example.com?carrier=" + carrier + "&tracking=" + trackingNumber;
        }
    }

    @Override
    public List<ShipmentPackage> getShipmentsByStatus(ShipmentStatus status) {
        log.debug("Getting shipments with status: {}", status);
        return shipmentPackageRepository.findByStatus(status);
    }

    @Override
    public ShipmentPackage findByTrackingNumber(String trackingNumber) {
        log.debug("Finding shipment by tracking number: {}", trackingNumber);
        return shipmentPackageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking number: " + trackingNumber));
    }

    @Override
    public List<ShipmentPackage> optimizeShipmentPackaging(FulfillmentOrder fulfillmentOrder) {
        log.info("Optimizing shipment packaging for fulfillment order: {}", fulfillmentOrder.getId());
        
        // For simple implementation, we'll create a single shipment for all items
        // In a real implementation, this would use a more advanced algorithm to optimize packaging
        
        ShipmentPackage shipment = new ShipmentPackage();
        shipment.setFulfillmentOrder(fulfillmentOrder);
        
        // Set dimensions and weight based on order items
        // This is a simple calculation - in a real system this would be more sophisticated
        double totalWeight = fulfillmentOrder.getItems().stream()
                .mapToDouble(item -> 0.5 * item.getQuantity()) // Assuming 0.5 kg per item as a placeholder
                .sum();
        
        shipment.setWeight(totalWeight);
        
        // Set default dimensions - in a real system this would be calculated based on item dimensions
        shipment.setLength(30.0); // cm
        shipment.setWidth(20.0);  // cm
        shipment.setHeight(15.0); // cm
        
        // Add all items to this shipment
        shipment.setItemCount(fulfillmentOrder.getTotalItemCount());
        
        // For orders with many items or heavy items, split into multiple shipments
        List<ShipmentPackage> shipments = new ArrayList<>();
        
        // If weight exceeds 20kg, split into multiple packages
        if (totalWeight > 20.0) {
            int numPackages = (int) Math.ceil(totalWeight / 20.0);
            double weightPerPackage = totalWeight / numPackages;
            
            for (int i = 0; i < numPackages; i++) {
                ShipmentPackage pkg = new ShipmentPackage();
                pkg.setFulfillmentOrder(fulfillmentOrder);
                pkg.setWeight(weightPerPackage);
                pkg.setLength(30.0);
                pkg.setWidth(20.0);
                pkg.setHeight(15.0);
                
                // Distribute items evenly (simplified)
                int itemsPerPackage = fulfillmentOrder.getTotalItemCount() / numPackages;
                pkg.setItemCount(i < numPackages - 1 ? itemsPerPackage : 
                        fulfillmentOrder.getTotalItemCount() - (itemsPerPackage * (numPackages - 1)));
                
                shipments.add(pkg);
            }
        } else {
            shipments.add(shipment);
        }
        
        return shipments;
    }
    
    //-------------------------
    // Helper methods
    //-------------------------
    
    /**
     * Determine the carrier to use based on shipping method and package weight
     */
    private String determineCarrier(ShippingMethod shippingMethod, double weight) {
        // In a real implementation, this would determine the best carrier based on
        // shipping method, package weight, dimensions, destination, and business rules
        
        switch (shippingMethod) {
            case EXPRESS:
                return weight > 10.0 ? "FedEx" : "DHL";
            case STANDARD:
                return "UPS";
            case ECONOMY:
                return "USPS";
            case OVERNIGHT:
                return "FedEx";
            case SAME_DAY:
                return "Local Courier";
            case PICKUP:
                return "N/A";
            default:
                return "UPS";
        }
    }
    
    /**
     * Generate a tracking number for a shipment
     */
    private String generateTrackingNumber(String carrier) {
        // In a real implementation, this would generate a tracking number according to the carrier's format
        // For this implementation, we'll just generate a random number with a carrier prefix
        
        String prefix;
        if (carrier.equalsIgnoreCase("UPS")) {
            prefix = "1Z";
        } else if (carrier.equalsIgnoreCase("FedEx")) {
            prefix = "FDX";
        } else if (carrier.equalsIgnoreCase("DHL")) {
            prefix = "DHL";
        } else {
            prefix = "TRK";
        }
        
        // Generate a random 10-digit number
        String randomPart = String.format("%010d", new Random().nextInt(1000000000));
        
        return prefix + randomPart;
    }
    
    /**
     * Check if all shipments for a fulfillment order are delivered and update the order status if needed
     */
    private void checkAndUpdateFulfillmentOrderDeliveryStatus(UUID fulfillmentOrderId) {
        List<ShipmentPackage> shipments = shipmentPackageRepository.findByFulfillmentOrderId(fulfillmentOrderId);
        
        // Check if all shipments are delivered
        boolean allDelivered = shipments.stream()
                .allMatch(shipment -> shipment.getStatus() == ShipmentStatus.DELIVERED);
        
        if (allDelivered && !shipments.isEmpty()) {
            // Update fulfillment order status to DELIVERED
            fulfillmentOrderService.updateFulfillmentOrderStatus(fulfillmentOrderId.toString(), FulfillmentStatus.DELIVERED);
        }
    }
    
    /**
     * Publish a shipment event to Kafka
     */
    private void publishShipmentEvent(ShipmentPackage shipment) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "SHIPMENT_" + shipment.getStatus());
        event.put("shipmentId", shipment.getId());
        event.put("fulfillmentOrderId", shipment.getFulfillmentOrder().getId());
        event.put("trackingNumber", shipment.getTrackingNumber());
        event.put("carrier", shipment.getCarrier());
        event.put("status", shipment.getStatus());
        event.put("timestamp", LocalDateTime.now());
        
        kafkaTemplate.send("shipment-events", shipment.getId().toString(), event);
        log.debug("Published shipment event for shipment {}", shipment.getId());
    }
} 
