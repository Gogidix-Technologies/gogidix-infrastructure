package com.exalt.warehousing.fulfillment.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a return request
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

    @NotNull
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "rma_number", unique = true)
    private String rmaNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReturnReason reason;

    @Column(name = "customer_notes")
    private String customerNotes;

    @Column(name = "internal_notes")
    private String internalNotes;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "authorized_by")
    private UUID authorizedBy;

    @Column(name = "authorized_at")
    private LocalDateTime authorizedAt;

    @Column(name = "expected_arrival_date")
    private LocalDateTime expectedArrivalDate;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "refund_processed_at")
    private LocalDateTime refundProcessedAt;

    @Column(name = "restocking_fee")
    private Double restockingFee;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReturnItem> items = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Get total number of items in this return
     *
     * @return total item count
     */
    @Transient
    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(ReturnItem::getQuantity)
                .sum();
    }

    /**
     * Check if return has been received
     *
     * @return true if received
     */
    @Transient
    public boolean isReceived() {
        return receivedAt != null || 
               status == ReturnStatus.RECEIVED || 
               status == ReturnStatus.INSPECTING || 
               status == ReturnStatus.PROCESSED || 
               status == ReturnStatus.COMPLETED;
    }

    /**
     * Check if return is in a terminal state
     *
     * @return true if in terminal state
     */
    @Transient
    public boolean isCompleted() {
        return status == ReturnStatus.COMPLETED || 
               status == ReturnStatus.CANCELLED || 
               status == ReturnStatus.REJECTED;
    }

    /**
     * Add an item to this return request
     *
     * @param item the item to add
     */
    public void addItem(ReturnItem item) {
        items.add(item);
        item.setReturnRequest(this);
    }

    /**
     * Remove an item from this return request
     *
     * @param item the item to remove
     */
    public void removeItem(ReturnItem item) {
        items.remove(item);
        item.setReturnRequest(null);
    }

    /**
     * Authorize the return
     *
     * @param userId user authorizing the return
     * @param rmaNumber RMA number to assign
     */
    public void authorize(UUID userId, String rmaNumber) {
        if (status == ReturnStatus.PENDING) {
            this.status = ReturnStatus.AUTHORIZED;
            this.authorizedBy = userId;
            this.authorizedAt = LocalDateTime.now();
            this.rmaNumber = rmaNumber;
        }
    }

    /**
     * Mark the return as received
     */
    public void markAsReceived() {
        if (status == ReturnStatus.AUTHORIZED || status == ReturnStatus.IN_TRANSIT) {
            this.status = ReturnStatus.RECEIVED;
            this.receivedAt = LocalDateTime.now();
        }
    }

    /**
     * Process the return
     *
     * @param notes processing notes
     * @param refundAmount amount to refund
     * @param restockingFee restocking fee if applicable
     */
    public void process(String notes, Double refundAmount, Double restockingFee) {
        if (status == ReturnStatus.RECEIVED || status == ReturnStatus.INSPECTING) {
            this.status = ReturnStatus.PROCESSED;
            this.internalNotes = notes;
            this.refundAmount = refundAmount;
            this.restockingFee = restockingFee;
            this.processedAt = LocalDateTime.now();
        }
    }

    /**
     * Complete the return
     */
    public void complete() {
        if (status == ReturnStatus.PROCESSED) {
            this.status = ReturnStatus.COMPLETED;
            this.refundProcessedAt = LocalDateTime.now();
        }
    }

    /**
     * Reject the return
     *
     * @param reason rejection reason
     */
    public void reject(String reason) {
        this.status = ReturnStatus.REJECTED;
        this.internalNotes = reason;
    }
}

