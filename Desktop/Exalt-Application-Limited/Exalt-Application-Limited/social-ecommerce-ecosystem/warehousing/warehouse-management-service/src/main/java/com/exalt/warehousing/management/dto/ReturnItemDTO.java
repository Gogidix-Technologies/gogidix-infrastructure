package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.ReturnItemCondition;
import com.exalt.warehousing.management.model.ReturnItemStatus;
import com.exalt.warehousing.management.model.ReturnReason;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Return Item
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnItemDTO {
    
    private UUID id;
    private UUID returnRequestId;
    private UUID orderItemId;
    private UUID productId;
    private String sku;
    private String productName;
    private int quantity;
    private ReturnItemStatus status;
    private ReturnReason returnReason;
    private ReturnItemCondition condition;
    private String inspectionNotes;
    private UUID inventoryItemId;
    private boolean inventoryReintegrated;
    private Integer reintegratedQuantity;
    private UUID reintegratedLocationId;
    private boolean refundProcessed;
    private Double refundAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    
    // Additional fields for display
    private String locationCode;
    private String warehouseName;

    // Constructors
    public ReturnItemDTO() {
    }

    public ReturnItemDTO(UUID id, UUID returnRequestId, UUID orderItemId, UUID productId, String sku, String productName, int quantity, ReturnItemStatus status, ReturnReason returnReason, ReturnItemCondition condition, String inspectionNotes, UUID inventoryItemId, boolean inventoryReintegrated, Integer reintegratedQuantity, UUID reintegratedLocationId, boolean refundProcessed, Double refundAmount, LocalDateTime createdAt, LocalDateTime updatedAt, String notes, String locationCode, String warehouseName) {
        this.id = id;
        this.returnRequestId = returnRequestId;
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
        this.returnReason = returnReason;
        this.condition = condition;
        this.inspectionNotes = inspectionNotes;
        this.inventoryItemId = inventoryItemId;
        this.inventoryReintegrated = inventoryReintegrated;
        this.reintegratedQuantity = reintegratedQuantity;
        this.reintegratedLocationId = reintegratedLocationId;
        this.refundProcessed = refundProcessed;
        this.refundAmount = refundAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.notes = notes;
        this.locationCode = locationCode;
        this.warehouseName = warehouseName;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getReturnRequestId() {
        return returnRequestId;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public ReturnItemStatus getStatus() {
        return status;
    }

    public ReturnReason getReturnReason() {
        return returnReason;
    }

    public ReturnItemCondition getCondition() {
        return condition;
    }

    public String getInspectionNotes() {
        return inspectionNotes;
    }

    public UUID getInventoryItemId() {
        return inventoryItemId;
    }

    public boolean isInventoryReintegrated() {
        return inventoryReintegrated;
    }

    public Integer getReintegratedQuantity() {
        return reintegratedQuantity;
    }

    public UUID getReintegratedLocationId() {
        return reintegratedLocationId;
    }

    public boolean isRefundProcessed() {
        return refundProcessed;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setReturnRequestId(UUID returnRequestId) {
        this.returnRequestId = returnRequestId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(ReturnItemStatus status) {
        this.status = status;
    }

    public void setReturnReason(ReturnReason returnReason) {
        this.returnReason = returnReason;
    }

    public void setCondition(ReturnItemCondition condition) {
        this.condition = condition;
    }

    public void setInspectionNotes(String inspectionNotes) {
        this.inspectionNotes = inspectionNotes;
    }

    public void setInventoryItemId(UUID inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public void setInventoryReintegrated(boolean inventoryReintegrated) {
        this.inventoryReintegrated = inventoryReintegrated;
    }

    public void setReintegratedQuantity(Integer reintegratedQuantity) {
        this.reintegratedQuantity = reintegratedQuantity;
    }

    public void setReintegratedLocationId(UUID reintegratedLocationId) {
        this.reintegratedLocationId = reintegratedLocationId;
    }

    public void setRefundProcessed(boolean refundProcessed) {
        this.refundProcessed = refundProcessed;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID returnRequestId;
        private UUID orderItemId;
        private UUID productId;
        private String sku;
        private String productName;
        private int quantity;
        private ReturnItemStatus status;
        private ReturnReason returnReason;
        private ReturnItemCondition condition;
        private String inspectionNotes;
        private UUID inventoryItemId;
        private boolean inventoryReintegrated;
        private Integer reintegratedQuantity;
        private UUID reintegratedLocationId;
        private boolean refundProcessed;
        private Double refundAmount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String notes;
        private String locationCode;
        private String warehouseName;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder returnRequestId(UUID returnRequestId) {
            this.returnRequestId = returnRequestId;
            return this;
        }

        public Builder orderItemId(UUID orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder status(ReturnItemStatus status) {
            this.status = status;
            return this;
        }

        public Builder returnReason(ReturnReason returnReason) {
            this.returnReason = returnReason;
            return this;
        }

        public Builder condition(ReturnItemCondition condition) {
            this.condition = condition;
            return this;
        }

        public Builder inspectionNotes(String inspectionNotes) {
            this.inspectionNotes = inspectionNotes;
            return this;
        }

        public Builder inventoryItemId(UUID inventoryItemId) {
            this.inventoryItemId = inventoryItemId;
            return this;
        }

        public Builder inventoryReintegrated(boolean inventoryReintegrated) {
            this.inventoryReintegrated = inventoryReintegrated;
            return this;
        }

        public Builder reintegratedQuantity(Integer reintegratedQuantity) {
            this.reintegratedQuantity = reintegratedQuantity;
            return this;
        }

        public Builder reintegratedLocationId(UUID reintegratedLocationId) {
            this.reintegratedLocationId = reintegratedLocationId;
            return this;
        }

        public Builder refundProcessed(boolean refundProcessed) {
            this.refundProcessed = refundProcessed;
            return this;
        }

        public Builder refundAmount(Double refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder locationCode(String locationCode) {
            this.locationCode = locationCode;
            return this;
        }

        public Builder warehouseName(String warehouseName) {
            this.warehouseName = warehouseName;
            return this;
        }

        public ReturnItemDTO build() {
            return new ReturnItemDTO(id, returnRequestId, orderItemId, productId, sku, productName, quantity, status, returnReason, condition, inspectionNotes, inventoryItemId, inventoryReintegrated, reintegratedQuantity, reintegratedLocationId, refundProcessed, refundAmount, createdAt, updatedAt, notes, locationCode, warehouseName);
        }
    }
}