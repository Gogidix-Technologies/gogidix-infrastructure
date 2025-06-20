package com.exalt.warehousing.fulfillment.entity;

import com.exalt.warehousing.fulfillment.enums.ShippingMethod;
import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import com.exalt.warehousing.shared.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a shipment package
 */
@Entity
@Table(name = "shipment_packages")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPackage extends BaseEntity {

    @NotNull
    @Column(name = "fulfillment_order_id", nullable = false)
    private UUID fulfillmentOrderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FulfillmentOrder fulfillmentOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "length")
    private Double length;

    @Column(name = "width")
    private Double width;

    @Column(name = "height")
    private Double height;

    @Column(name = "item_count")
    private Integer itemCount;

    @Column(name = "shipping_label_url")
    private String shippingLabelUrl;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "notes")
    private String notes;
    
    @Column(name = "service_level")
    private String serviceLevel;
    
    @Column(name = "shipping_cost")
    private Double shippingCost;
    
    @Column(name = "currency")
    private String currency;
    
    @Column(name = "shipping_label_format")
    private String shippingLabelFormat;
    
    @Column(name = "commercial_invoice_url")
    private String commercialInvoiceUrl;
    
    @Column(name = "customs_declaration_number")
    private String customsDeclarationNumber;

    /**
     * Set fulfillment order and update fulfillment order ID
     */
    public void setFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        this.fulfillmentOrder = fulfillmentOrder;
        if (fulfillmentOrder != null && fulfillmentOrder.getId() != null) {
            // Convert String ID to UUID for storage using helper method
            this.fulfillmentOrderId = convertStringToUuid(fulfillmentOrder.getId().toString());
        } else {
            this.fulfillmentOrderId = null;
        }
    }
    
    /**
     * Helper method to convert String ID to UUID
     */
    private UUID convertStringToUuid(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            // If not a valid UUID, create a deterministic UUID from the string
            return UUID.nameUUIDFromBytes(id.getBytes());
        }
    }

    /**
     * Check if the package has been shipped
     *
     * @return true if the package has been shipped
     */
    @Transient
    public boolean isShipped() {
        return status == ShipmentStatus.SHIPPED || status == ShipmentStatus.IN_TRANSIT 
               || status == ShipmentStatus.OUT_FOR_DELIVERY || status == ShipmentStatus.DELIVERED;
    }

    /**
     * Check if the package has been delivered
     *
     * @return true if the package has been delivered
     */
    @Transient
    public boolean isDelivered() {
        return status == ShipmentStatus.DELIVERED;
    }

    /**
     * Check if the package has been cancelled
     *
     * @return true if the package has been cancelled
     */
    @Transient
    public boolean isCancelled() {
        return status == ShipmentStatus.CANCELLED;
    }

    /**
     * Calculate the volume of the package
     *
     * @return the volume in cubic centimeters
     */
    @Transient
    public Double getVolume() {
        if (length != null && width != null && height != null) {
            return length * width * height;
        }
        return null;
    }
    
    // Alias methods for backward compatibility
    @Transient
    public Double getWeightKg() {
        return weight;
    }
    
    @Transient
    public Double getLengthCm() {
        return length;
    }
    
    @Transient
    public Double getWidthCm() {
        return width;
    }
    
    @Transient
    public Double getHeightCm() {
        return height;
    }
}