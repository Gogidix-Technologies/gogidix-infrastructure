package com.exalt.warehousing.fulfillment.mapper.manual;

import com.exalt.warehousing.fulfillment.dto.ShipmentPackageDTO;
import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Manual mapper for converting between ShipmentPackage entity and DTO
 * Using reflection to safely access fields when needed
 */
@Component
public class ShipmentPackageManualMapper {

    /**
     * Convert entity to DTO
     * 
     * @param shipmentPackage the entity to convert
     * @return the DTO
     */
    public ShipmentPackageDTO toDTO(ShipmentPackage shipmentPackage) {
        if (shipmentPackage == null) {
            return null;
        }
        
        ShipmentPackageDTO dto = new ShipmentPackageDTO();
        
        // Use reflection to safely access fields
        try {
            // Set basic properties
            Class<?> entityClass = shipmentPackage.getClass();
            
            // Try to get ID directly from the getter method we know exists
            dto.setId(shipmentPackage.getId() != null ? UUID.fromString(shipmentPackage.getId().toString()) : null);
            
            // Use the transient method for fulfillmentOrderId we know exists
            dto.setFulfillmentOrderId(shipmentPackage.getFulfillmentOrderId());
            
            // Use reflection for other fields
            copyFieldValue(shipmentPackage, dto, "status");
            copyFieldValue(shipmentPackage, dto, "shippingMethod");
            copyFieldValue(shipmentPackage, dto, "carrier");
            copyFieldValue(shipmentPackage, dto, "trackingNumber");
            copyFieldValue(shipmentPackage, dto, "weight");
            copyFieldValue(shipmentPackage, dto, "length");
            copyFieldValue(shipmentPackage, dto, "width");
            copyFieldValue(shipmentPackage, dto, "height");
            
            // Calculate volume using the method we know exists
            dto.setVolume(shipmentPackage.getVolume());
            
            copyFieldValue(shipmentPackage, dto, "itemCount");
            copyFieldValue(shipmentPackage, dto, "shippingLabelUrl");
            copyFieldValue(shipmentPackage, dto, "shippedAt");
            copyFieldValue(shipmentPackage, dto, "deliveredAt");
            copyFieldValue(shipmentPackage, dto, "estimatedDeliveryDate");
            copyFieldValue(shipmentPackage, dto, "cancellationReason");
            copyFieldValue(shipmentPackage, dto, "cancelledAt");
            copyFieldValue(shipmentPackage, dto, "notes");
            copyFieldValue(shipmentPackage, dto, "serviceLevel");
            copyFieldValue(shipmentPackage, dto, "shippingCost");
            copyFieldValue(shipmentPackage, dto, "currency");
            copyFieldValue(shipmentPackage, dto, "shippingLabelFormat");
            copyFieldValue(shipmentPackage, dto, "commercialInvoiceUrl");
            copyFieldValue(shipmentPackage, dto, "customsDeclarationNumber");
            copyFieldValue(shipmentPackage, dto, "createdAt");
            copyFieldValue(shipmentPackage, dto, "updatedAt");
            
        } catch (Exception e) {
            // Log the error but return what we have so far
            System.err.println("Error during DTO mapping: " + e.getMessage());
        }
        
        return dto;
    }

    /**
     * Convert DTO to entity
     * 
     * @param dto the DTO to convert
     * @return the entity
     */
    public ShipmentPackage toEntity(ShipmentPackageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        ShipmentPackage entity = new ShipmentPackage();
        
        try {
            // Set basic properties using reflection
            setFieldValue(entity, "id", dto.getId());
            // FulfillmentOrder must be set separately via service layer
            setFieldValue(entity, "status", dto.getStatus());
            setFieldValue(entity, "shippingMethod", dto.getShippingMethod());
            setFieldValue(entity, "carrier", dto.getCarrier());
            setFieldValue(entity, "trackingNumber", dto.getTrackingNumber());
            setFieldValue(entity, "weight", dto.getWeight());
            setFieldValue(entity, "length", dto.getLength());
            setFieldValue(entity, "width", dto.getWidth());
            setFieldValue(entity, "height", dto.getHeight());
            setFieldValue(entity, "itemCount", dto.getItemCount());
            setFieldValue(entity, "shippingLabelUrl", dto.getShippingLabelUrl());
            setFieldValue(entity, "shippedAt", dto.getShippedAt());
            setFieldValue(entity, "deliveredAt", dto.getDeliveredAt());
            setFieldValue(entity, "estimatedDeliveryDate", dto.getEstimatedDeliveryDate());
            setFieldValue(entity, "cancellationReason", dto.getCancellationReason());
            setFieldValue(entity, "cancelledAt", dto.getCancelledAt());
            setFieldValue(entity, "notes", dto.getNotes());
            
            // Additional properties if available
            if (dto.getServiceLevel() != null) {
                setFieldValue(entity, "serviceLevel", dto.getServiceLevel());
            }
            if (dto.getShippingCost() != null) {
                setFieldValue(entity, "shippingCost", dto.getShippingCost());
            }
            if (dto.getCurrency() != null) {
                setFieldValue(entity, "currency", dto.getCurrency());
            }
            if (dto.getShippingLabelFormat() != null) {
                setFieldValue(entity, "shippingLabelFormat", dto.getShippingLabelFormat());
            }
            if (dto.getCommercialInvoiceUrl() != null) {
                setFieldValue(entity, "commercialInvoiceUrl", dto.getCommercialInvoiceUrl());
            }
            if (dto.getCustomsDeclarationNumber() != null) {
                setFieldValue(entity, "customsDeclarationNumber", dto.getCustomsDeclarationNumber());
            }
        } catch (Exception e) {
            // Log the error but return what we have so far
            System.err.println("Error during entity mapping: " + e.getMessage());
        }
        
        return entity;
    }
    
    /**
     * Copy a field value from source object to target object using reflection
     */
    private void copyFieldValue(Object source, Object target, String fieldName) throws Exception {
        try {
            Field sourceField = getField(source.getClass(), fieldName);
            Field targetField = getField(target.getClass(), fieldName);
            
            if (sourceField != null && targetField != null) {
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                Object value = sourceField.get(source);
                targetField.set(target, value);
            }
        } catch (Exception e) {
            System.err.println("Error copying field " + fieldName + ": " + e.getMessage());
        }
    }
    
    /**
     * Set a field value on target object using reflection
     */
    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        try {
            Field field = getField(target.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(target, value);
            }
        } catch (Exception e) {
            System.err.println("Error setting field " + fieldName + ": " + e.getMessage());
        }
    }
    
    /**
     * Get a field from a class or its superclasses
     */
    private Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getField(superClass, fieldName);
            }
            return null;
        }
    }
}
