package com.exalt.warehousing.fulfillment.event;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Event tracking for fulfillment orders
 */
@Entity
@Table(name = "fulfillment_events", indexes = {
    @Index(name = "idx_fulfillment_event_order", columnList = "fulfillment_order_id"),
    @Index(name = "idx_fulfillment_event_type", columnList = "event_type"),
    @Index(name = "idx_fulfillment_event_timestamp", columnList = "event_timestamp")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FulfillmentEvent extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_order_id", nullable = false)
    private FulfillmentOrder fulfillmentOrder;
    
    @Column(name = "event_type", nullable = false, length = 50)
    @NotBlank(message = "Event type is required")
    @Size(max = 50, message = "Event type must not exceed 50 characters")
    private String eventType;
    
    @Column(name = "event_timestamp", nullable = false)
    @NotNull(message = "Event timestamp is required")
    private LocalDateTime eventTimestamp;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_name", length = 100)
    @Size(max = 100, message = "User name must not exceed 100 characters")
    private String userName;
    
    @Column(name = "old_status", length = 30)
    @Size(max = 30, message = "Old status must not exceed 30 characters")
    private String oldStatus;
    
    @Column(name = "new_status", length = 30)
    @Size(max = 30, message = "New status must not exceed 30 characters")
    private String newStatus;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @PrePersist
    public void prePersist() {
        super.prePersist();
        if (eventTimestamp == null) {
            eventTimestamp = LocalDateTime.now();
        }
    }
    
    // Explicit setter for compatibility
    public void setFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        this.fulfillmentOrder = fulfillmentOrder;
    }
}
