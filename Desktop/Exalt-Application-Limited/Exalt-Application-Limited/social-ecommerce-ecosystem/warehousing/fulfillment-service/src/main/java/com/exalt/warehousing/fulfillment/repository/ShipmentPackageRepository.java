package com.exalt.warehousing.fulfillment.repository;

import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ShipmentPackage entities
 */
@Repository
public interface ShipmentPackageRepository extends JpaRepository<ShipmentPackage, UUID> {
    
    /**
     * Find shipment packages by fulfillment order ID
     * 
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of shipment packages
     */
    List<ShipmentPackage> findByFulfillmentOrderId(UUID fulfillmentOrderId);
    
    /**
     * Find shipment packages by status
     * 
     * @param status the shipment status
     * @return list of shipment packages
     */
    List<ShipmentPackage> findByStatus(ShipmentStatus status);
    
    /**
     * Find shipment packages by tracking number
     * 
     * @param trackingNumber the tracking number
     * @return the shipment package if found
     */
    Optional<ShipmentPackage> findByTrackingNumber(String trackingNumber);
    
    /**
     * Find shipment packages by carrier
     * 
     * @param carrier the carrier name
     * @return list of shipment packages
     */
    List<ShipmentPackage> findByCarrier(String carrier);
    
    /**
     * Find shipment packages by fulfillment order ID and status
     * 
     * @param fulfillmentOrderId the fulfillment order ID
     * @param status the shipment status
     * @return list of shipment packages
     */
    List<ShipmentPackage> findByFulfillmentOrderIdAndStatus(UUID fulfillmentOrderId, ShipmentStatus status);
    
    /**
     * Find shipment packages created within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of shipment packages
     */
    List<ShipmentPackage> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find shipment packages by warehouse ID
     * 
     * @param warehouseId the warehouse ID
     * @return list of shipment packages
     */
    @Query("SELECT s FROM ShipmentPackage s JOIN s.fulfillmentOrder fo WHERE fo.warehouseId = :warehouseId")
    List<ShipmentPackage> findByWarehouseId(@Param("warehouseId") UUID warehouseId);
    
    /**
     * Count shipment packages by status
     * 
     * @param status the shipment status
     * @return count of shipment packages
     */
    long countByStatus(ShipmentStatus status);
    
    /**
     * Count shipment packages by fulfillment order ID
     * 
     * @param fulfillmentOrderId the fulfillment order ID
     * @return count of shipment packages
     */
    long countByFulfillmentOrderId(UUID fulfillmentOrderId);
    
    /**
     * Find shipment packages scheduled for delivery today
     * 
     * @return list of shipment packages
     */
    @Query("SELECT s FROM ShipmentPackage s WHERE s.estimatedDeliveryDate = CURRENT_DATE AND s.status = 'IN_TRANSIT'")
    List<ShipmentPackage> findScheduledForDeliveryToday();
} 
