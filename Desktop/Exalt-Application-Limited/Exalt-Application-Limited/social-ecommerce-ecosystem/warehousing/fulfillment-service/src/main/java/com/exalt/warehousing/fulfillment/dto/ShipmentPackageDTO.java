package com.exalt.warehousing.fulfillment.dto;

import com.exalt.warehousing.fulfillment.enums.ShipmentStatus;
import com.exalt.warehousing.fulfillment.enums.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for shipment packages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPackageDTO {

    private UUID id;
    private UUID fulfillmentOrderId;
    private String orderReference;
    private ShipmentStatus status;
    private ShippingMethod shippingMethod;
    private String carrier;
    private String trackingNumber;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private Double volume;
    private Integer itemCount;
    private String shippingLabelUrl;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime estimatedDeliveryDate;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private String notes;
    private String serviceLevel;
    private Double shippingCost;
    private String currency;
    private String shippingLabelFormat;
    private String commercialInvoiceUrl;
    private String customsDeclarationNumber;
    
    // Shipping address fields (from fulfillment order)
    private String customerName;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingStateProvince;
    private String shippingPostalCode;
    private String shippingCountry;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Explicit getters and setters to ensure availability during compilation
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }
    
    public void setFulfillmentOrderId(UUID fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }
    
    public String getOrderReference() {
        return orderReference;
    }
    
    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
    
    public ShipmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
    
    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }
    
    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
    
    public String getCarrier() {
        return carrier;
    }
    
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public Double getLength() {
        return length;
    }
    
    public void setLength(Double length) {
        this.length = length;
    }
    
    public Double getWidth() {
        return width;
    }
    
    public void setWidth(Double width) {
        this.width = width;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getVolume() {
        return volume;
    }
    
    public void setVolume(Double volume) {
        this.volume = volume;
    }
    
    public Integer getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
    
    public String getShippingLabelUrl() {
        return shippingLabelUrl;
    }
    
    public void setShippingLabelUrl(String shippingLabelUrl) {
        this.shippingLabelUrl = shippingLabelUrl;
    }
    
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }
    
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }
    
    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getServiceLevel() {
        return serviceLevel;
    }
    
    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }
    
    public Double getShippingCost() {
        return shippingCost;
    }
    
    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getShippingLabelFormat() {
        return shippingLabelFormat;
    }
    
    public void setShippingLabelFormat(String shippingLabelFormat) {
        this.shippingLabelFormat = shippingLabelFormat;
    }
    
    public String getCommercialInvoiceUrl() {
        return commercialInvoiceUrl;
    }
    
    public void setCommercialInvoiceUrl(String commercialInvoiceUrl) {
        this.commercialInvoiceUrl = commercialInvoiceUrl;
    }
    
    public String getCustomsDeclarationNumber() {
        return customsDeclarationNumber;
    }
    
    public void setCustomsDeclarationNumber(String customsDeclarationNumber) {
        this.customsDeclarationNumber = customsDeclarationNumber;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getShippingAddressLine1() {
        return shippingAddressLine1;
    }
    
    public void setShippingAddressLine1(String shippingAddressLine1) {
        this.shippingAddressLine1 = shippingAddressLine1;
    }
    
    public String getShippingAddressLine2() {
        return shippingAddressLine2;
    }
    
    public void setShippingAddressLine2(String shippingAddressLine2) {
        this.shippingAddressLine2 = shippingAddressLine2;
    }
    
    public String getShippingCity() {
        return shippingCity;
    }
    
    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }
    
    public String getShippingStateProvince() {
        return shippingStateProvince;
    }
    
    public void setShippingStateProvince(String shippingStateProvince) {
        this.shippingStateProvince = shippingStateProvince;
    }
    
    public String getShippingPostalCode() {
        return shippingPostalCode;
    }
    
    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }
    
    public String getShippingCountry() {
        return shippingCountry;
    }
    
    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Alias getter methods for backward compatibility
    public Double getWeightKg() {
        return weight;
    }
    
    public void setWeightKg(Double weightKg) {
        this.weight = weightKg;
    }
    
    public Double getLengthCm() {
        return length;
    }
    
    public Double getWidthCm() {
        return width;
    }
    
    public Double getHeightCm() {
        return height;
    }
} 
