package com.exalt.warehousing.management.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an individual item in a return request
 */
@Entity
@Table(name = "return_items")
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;
    
    @Column(nullable = false)
    private UUID orderItemId;
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(length = 100)
    private String sku;
    
    @Column(length = 255)
    private String productName;
    
    private int quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnItemStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnReason returnReason;
    
    @Enumerated(EnumType.STRING)
    private ReturnItemCondition condition;
    
    @Column(length = 1000)
    private String inspectionNotes;
    
    /**
     * The inventory item ID that this return item should be reintegrated with
     */
    @Column(nullable = true)
    private UUID inventoryItemId;
    
    /**
     * Whether the inventory has been reintegrated
     */
    private boolean inventoryReintegrated;
    
    /**
     * The quantity that was actually reintegrated (may be less than requested quantity)
     */
    private Integer reintegratedQuantity;
    
    /**
     * The location where the item was reintegrated
     */
    @Column(nullable = true)
    private UUID reintegratedLocationId;
    
    /**
     * Whether a refund has been processed for this item
     */
    private boolean refundProcessed;
    
    /**
     * The refund amount that was processed
     */
    private Double refundAmount;
    
    /**
     * The original price of the item when purchased
     */
    private Double originalPrice;
    
    /**
     * Date when the item was assessed for quality
     */
    private LocalDateTime qualityAssessmentDate;
    
    /**
     * Staff member who performed the quality assessment
     */
    @Column(nullable = true)
    private UUID qualityAssessedByStaffId;
    
    /**
     * Creation timestamp
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * Optional notes for the return item
     */
    @Column(length = 1000)
    private String notes;

    // Constructors
    public ReturnItem() {
        // Default constructor
    }

    public ReturnItem(UUID id, ReturnRequest returnRequest, UUID orderItemId, UUID productId, String sku, String productName, int quantity, ReturnItemStatus status, ReturnReason returnReason, ReturnItemCondition condition, String inspectionNotes, UUID inventoryItemId, boolean inventoryReintegrated, Integer reintegratedQuantity, UUID reintegratedLocationId, boolean refundProcessed, Double refundAmount, Double originalPrice, LocalDateTime qualityAssessmentDate, UUID qualityAssessedByStaffId, LocalDateTime createdAt, LocalDateTime updatedAt, String notes) {
        this.id = id;
        this.returnRequest = returnRequest;
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
        this.originalPrice = originalPrice;
        this.qualityAssessmentDate = qualityAssessmentDate;
        this.qualityAssessedByStaffId = qualityAssessedByStaffId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.notes = notes;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public ReturnRequest getReturnRequest() {
        return returnRequest;
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

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public LocalDateTime getQualityAssessmentDate() {
        return qualityAssessmentDate;
    }

    public UUID getQualityAssessedByStaffId() {
        return qualityAssessedByStaffId;
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

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setReturnRequest(ReturnRequest returnRequest) {
        this.returnRequest = returnRequest;
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

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setQualityAssessmentDate(LocalDateTime qualityAssessmentDate) {
        this.qualityAssessmentDate = qualityAssessmentDate;
    }

    public void setQualityAssessedByStaffId(UUID qualityAssessedByStaffId) {
        this.qualityAssessedByStaffId = qualityAssessedByStaffId;
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

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private ReturnRequest returnRequest;
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
        private Double originalPrice;
        private LocalDateTime qualityAssessmentDate;
        private UUID qualityAssessedByStaffId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String notes;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder returnRequest(ReturnRequest returnRequest) {
            this.returnRequest = returnRequest;
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

        public Builder originalPrice(Double originalPrice) {
            this.originalPrice = originalPrice;
            return this;
        }

        public Builder qualityAssessmentDate(LocalDateTime qualityAssessmentDate) {
            this.qualityAssessmentDate = qualityAssessmentDate;
            return this;
        }

        public Builder qualityAssessedByStaffId(UUID qualityAssessedByStaffId) {
            this.qualityAssessedByStaffId = qualityAssessedByStaffId;
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

        public ReturnItem build() {
            return new ReturnItem(id, returnRequest, orderItemId, productId, sku, productName, quantity, status, returnReason, condition, inspectionNotes, inventoryItemId, inventoryReintegrated, reintegratedQuantity, reintegratedLocationId, refundProcessed, refundAmount, originalPrice, qualityAssessmentDate, qualityAssessedByStaffId, createdAt, updatedAt, notes);
        }
    }
}