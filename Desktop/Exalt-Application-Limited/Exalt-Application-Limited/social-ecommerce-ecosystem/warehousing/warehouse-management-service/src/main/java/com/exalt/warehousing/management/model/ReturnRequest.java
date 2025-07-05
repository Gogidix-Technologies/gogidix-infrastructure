package com.exalt.warehousing.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a return request in the warehouse management system
 */
@Entity
@Table(name = "return_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;
    
    @Column(nullable = true)
    private UUID customerId;
    
    @Column(nullable = false)
    private UUID warehouseId;
    
    @Column(length = 100)
    private String returnCode;
    
    @Column(length = 255)
    private String returnReason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status;
    
    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReturnItem> returnItems = new HashSet<>();
    
    /**
     * Quality check notes from inspection
     */
    @Column(length = 1000)
    private String qualityCheckNotes;
    
    /**
     * Whether a refund has been processed
     */
    private boolean refundProcessed;
    
    /**
     * Whether items have been reintegrated into inventory
     */
    private boolean inventoryReintegrated;
    
    /**
     * The staff member who processed the return
     */
    @Column(nullable = true)
    private UUID processedByStaffId;
    
    /**
     * The date and time when the return was processed
     */
    private LocalDateTime processedAt;
    
    /**
     * The date and time when the return was received at the warehouse
     */
    private LocalDateTime receivedAt;
    
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
     * Optional tracking information for the return shipment
     */
    @Column(length = 100)
    private String returnTrackingNumber;
    
    /**
     * Optional notes for the return
     */
    @Column(length = 1000)
    private String notes;

    // Lombok @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor will generate constructors, getters, and setters
    
    /**
     * Adds a return item to this return request
     * 
     * @param returnItem the item to add
     * @return this return request
     */
    public ReturnRequest addReturnItem(ReturnItem returnItem) {
        if (returnItems == null) {
            returnItems = new HashSet<>();
        }
        returnItems.add(returnItem);
        returnItem.setReturnRequest(this);
        return this;
    }
    
    /**
     * Removes a return item from this return request
     * 
     * @param returnItem the item to remove
     * @return this return request
     */
    public ReturnRequest removeReturnItem(ReturnItem returnItem) {
        if (returnItems != null) {
            returnItems.remove(returnItem);
            returnItem.setReturnRequest(null);
        }
        return this;
    }
    
    /**
     * Get customer name for labels (placeholder implementation)
     * TODO: Implement customer service lookup
     */
    public String getCustomerName() {
        return customerId != null ? "Customer-" + customerId.toString().substring(0, 8) : "Unknown Customer";
    }
    
    /**
     * Get customer address for labels (placeholder implementation)
     * TODO: Implement customer service lookup
     */
    public String getCustomerAddress() {
        return customerId != null ? "Customer Address for " + customerId.toString().substring(0, 8) : "Unknown Address";
    }
    
    /**
     * Get warehouse address for labels (placeholder implementation)
     * TODO: Implement warehouse service lookup
     */
    public String getWarehouseAddress() {
        return warehouseId != null ? "Warehouse Address for " + warehouseId.toString().substring(0, 8) : "Unknown Warehouse";
    }
    
    /**
     * Get tracking number (alias for returnTrackingNumber)
     */
    public String getTrackingNumber() {
        return returnTrackingNumber;
    }
}
