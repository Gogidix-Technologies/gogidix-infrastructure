package com.exalt.warehousing.fulfillment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an item in a return request
 */
@Entity
@Table(name = "return_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @NotNull
    @Column(name = "order_item_id", nullable = false)
    private UUID orderItemId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "sku")
    private String sku;

    @NotNull
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Min(0)
    @Column(name = "price")
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private ReturnReason reason;

    @Column(name = "customer_notes")
    private String customerNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition")
    private ReturnItemCondition condition;

    @Column(name = "inspection_notes")
    private String inspectionNotes;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "disposition")
    private ReturnDisposition disposition;

    @Column(name = "processed_by")
    private UUID processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculate total item value
     *
     * @return total value (price * quantity)
     */
    @Transient
    public Double getTotalValue() {
        return price != null ? price * quantity : null;
    }

    /**
     * Calculate refund percentage
     *
     * @return percentage of original price refunded
     */
    @Transient
    public Double getRefundPercentage() {
        if (price != null && refundAmount != null && price > 0) {
            return (refundAmount / price) * 100;
        }
        return null;
    }

    /**
     * Process this return item
     *
     * @param condition the condition of the returned item
     * @param disposition how to dispose of the item
     * @param refundAmount amount to refund
     * @param notes inspection notes
     * @param processedBy user who processed the item
     */
    public void process(ReturnItemCondition condition, ReturnDisposition disposition, 
                      Double refundAmount, String notes, UUID processedBy) {
        this.condition = condition;
        this.disposition = disposition;
        this.refundAmount = refundAmount;
        this.inspectionNotes = notes;
        this.processedBy = processedBy;
        this.processedAt = LocalDateTime.now();
    }
}
