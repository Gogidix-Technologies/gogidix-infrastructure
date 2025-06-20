package com.exalt.warehousing.fulfillment.service;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import com.exalt.warehousing.fulfillment.enums.ShippingMethod;

import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for shipment management
 */
public interface ShipmentService {

    /**
     * Create a shipment for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param shippingMethod the shipping method
     * @return the created shipment packages
     */
    List<ShipmentPackage> createShipment(String fulfillmentOrderId, ShippingMethod shippingMethod);

    /**
     * Update shipment status
     *
     * @param shipmentId the shipment ID
     * @param status the new status
     * @return the updated shipment
     */
    ShipmentPackage updateShipmentStatus(String shipmentId, ShipmentStatus status);

    /**
     * Get a shipment by ID
     *
     * @param shipmentId the shipment ID
     * @return the shipment if found
     */
    ShipmentPackage getShipment(String shipmentId);

    /**
     * Get all shipments for a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of shipments for the order
     */
    List<ShipmentPackage> getShipmentsByFulfillmentOrder(String fulfillmentOrderId);

    /**
     * Generate a shipping label for a shipment
     *
     * @param shipmentId the shipment ID
     * @param outputStream the stream to write the label to
     */
    void generateShippingLabel(String shipmentId, OutputStream outputStream);

    /**
     * Cancel a shipment
     *
     * @param shipmentId the shipment ID
     * @param reason the cancellation reason
     * @return the cancelled shipment
     */
    ShipmentPackage cancelShipment(String shipmentId, String reason);

    /**
     * Get shipment tracking URL
     *
     * @param shipmentId the shipment ID
     * @return the tracking URL
     */
    String getTrackingUrl(String shipmentId);

    /**
     * Get shipments by status
     *
     * @param status the shipment status
     * @return list of shipments with the given status
     */
    List<ShipmentPackage> getShipmentsByStatus(ShipmentStatus status);

    /**
     * Search shipments by tracking number
     *
     * @param trackingNumber the tracking number
     * @return the shipment if found
     */
    ShipmentPackage findByTrackingNumber(String trackingNumber);

    /**
     * Optimize shipment packaging for a fulfillment order
     *
     * @param fulfillmentOrder the fulfillment order
     * @return the optimized shipment plan
     */
    List<ShipmentPackage> optimizeShipmentPackaging(FulfillmentOrder fulfillmentOrder);
} 
