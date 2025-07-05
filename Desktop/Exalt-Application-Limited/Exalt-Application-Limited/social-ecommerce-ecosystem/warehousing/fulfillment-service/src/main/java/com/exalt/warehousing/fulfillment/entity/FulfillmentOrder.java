package com.exalt.warehousing.fulfillment.entity;

import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.FulfillmentType;
import com.exalt.warehousing.fulfillment.enums.Priority;
import com.exalt.warehousing.fulfillment.enums.InventoryStatus;
import com.exalt.warehousing.shared.common.BaseEntity;
import com.exalt.warehousing.fulfillment.event.FulfillmentEvent;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Enhanced Fulfillment Order Entity
 * 
 * Represents a fulfillment order in the warehouse management system.
 * Tracks the complete lifecycle from order placement through delivery.
 */
@Entity
@Table(name = "fulfillment_orders", indexes = {
    @Index(name = "idx_fulfillment_order_external_id", columnList = "external_order_id"),
    @Index(name = "idx_fulfillment_order_status", columnList = "status"),
    @Index(name = "idx_fulfillment_order_warehouse", columnList = "warehouse_id"),
    @Index(name = "idx_fulfillment_order_priority", columnList = "priority"),
    @Index(name = "idx_fulfillment_order_created", columnList = "created_at"),
    @Index(name = "idx_fulfillment_order_customer", columnList = "customer_id"),
    @Index(name = "idx_fulfillment_order_type", columnList = "fulfillment_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"orderItems", "events"})
public class FulfillmentOrder extends BaseEntity {

    // Order Identification
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderNumber;
    
    @Column(name = "external_order_id", nullable = false, unique = true, length = 100)
    @NotBlank(message = "External order ID is required")
    @Size(max = 100, message = "External order ID must not exceed 100 characters")
    private String externalOrderId;

    @Column(name = "order_date", nullable = false)
    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "customer_id", nullable = false, length = 100)
    @NotBlank(message = "Customer ID is required")
    @Size(max = 100, message = "Customer ID must not exceed 100 characters")
    private String customerId;

    @Column(name = "customer_name", length = 255)
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    private String customerName;

    @Column(name = "customer_email", length = 255)
    @Email(message = "Customer email must be valid")
    @Size(max = 255, message = "Customer email must not exceed 255 characters")
    private String customerEmail;

    @Column(name = "customer_phone", length = 50)
    @Size(max = 50, message = "Customer phone must not exceed 50 characters")
    private String customerPhone;

    // Order Type & Priority
    @Column(name = "fulfillment_type", length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Fulfillment type is required")
    @Builder.Default
    private FulfillmentType fulfillmentType = FulfillmentType.WAREHOUSE;

    @Column(name = "priority", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Priority is required")
    @Builder.Default
    private Priority priority = Priority.STANDARD;

    @Column(name = "priority_score")
    @Min(value = 0, message = "Priority score must be non-negative")
    @Max(value = 1000, message = "Priority score must not exceed 1000")
    private Integer priorityScore;

    // Order Status
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Builder.Default
    private FulfillmentStatus status = FulfillmentStatus.PENDING;

    @Column(name = "status_reason", length = 500)
    @Size(max = 500, message = "Status reason must not exceed 500 characters")
    private String statusReason;

    @Column(name = "previous_status", length = 30)
    @Enumerated(EnumType.STRING)
    private FulfillmentStatus previousStatus;

    // Order Value
    @Column(name = "total_amount", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Total amount must be non-negative")
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private BigDecimal taxAmount;

    @Column(name = "shipping_amount", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Shipping amount must be non-negative")
    private BigDecimal shippingAmount;
    
    @Column(name = "shipping_cost", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Shipping cost must be non-negative")
    private BigDecimal shippingCost;

    @Column(name = "discount_amount", precision = 19, scale = 4)
    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount;

    @Column(name = "currency", length = 3)
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "USD";

    // Warehouse Assignment
    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "warehouse_name", length = 255)
    @Size(max = 255, message = "Warehouse name must not exceed 255 characters")
    private String warehouseName;

    @Column(name = "warehouse_zone", length = 50)
    @Size(max = 50, message = "Warehouse zone must not exceed 50 characters")
    private String warehouseZone;

    @Column(name = "storage_location", length = 100)
    @Size(max = 100, message = "Storage location must not exceed 100 characters")
    private String storageLocation;
    
    // Vendor Information
    @Column(name = "vendor_id")
    private Long vendorId;
    
    @Column(name = "vendor_location_id")
    private Long vendorLocationId;
    
    @Column(name = "fulfillment_zone", length = 50)
    @Size(max = 50, message = "Fulfillment zone must not exceed 50 characters")
    private String fulfillmentZone;

    // Shipping Information
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "ship_address_line1")),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "ship_address_line2")),
        @AttributeOverride(name = "city", column = @Column(name = "ship_city")),
        @AttributeOverride(name = "state", column = @Column(name = "ship_state")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "ship_postal_code")),
        @AttributeOverride(name = "country", column = @Column(name = "ship_country"))
    })
    private Address shippingAddress;

    @Column(name = "shipping_method", length = 100)
    @Size(max = 100, message = "Shipping method must not exceed 100 characters")
    private String shippingMethod;

    @Column(name = "carrier", length = 100)
    @Size(max = 100, message = "Carrier must not exceed 100 characters")
    private String carrier;

    @Column(name = "tracking_number", length = 255)
    @Size(max = 255, message = "Tracking number must not exceed 255 characters")
    private String trackingNumber;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    // Workflow Timestamps
    @Column(name = "pick_started_at")
    private LocalDateTime pickStartedAt;

    @Column(name = "pick_completed_at")
    private LocalDateTime pickCompletedAt;

    @Column(name = "pack_started_at")
    private LocalDateTime packStartedAt;

    @Column(name = "pack_completed_at")
    private LocalDateTime packCompletedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Processing Information
    @Column(name = "picking_user_id")
    private Long pickingUserId;

    @Column(name = "packing_user_id")
    private Long packingUserId;

    @Column(name = "shipping_user_id")
    private Long shippingUserId;
    
    @Column(name = "assigned_picker_id")
    private Long assignedPickerId;
    
    @Column(name = "assigned_packer_id")
    private Long assignedPackerId;

    @Column(name = "picking_time_minutes")
    @Min(value = 0, message = "Picking time must be non-negative")
    private Integer pickingTimeMinutes;

    @Column(name = "packing_time_minutes")
    @Min(value = 0, message = "Packing time must be non-negative")
    private Integer packingTimeMinutes;

    @Column(name = "total_weight", precision = 10, scale = 3)
    @DecimalMin(value = "0.0", message = "Total weight must be non-negative")
    private BigDecimal totalWeight;

    @Column(name = "weight_unit", length = 10)
    @Size(max = 10, message = "Weight unit must not exceed 10 characters")
    private String weightUnit = "KG";
    
    @Column(name = "total_items")
    @Min(value = 0, message = "Total items must be non-negative")
    private Integer totalItems;

    // SLA and Performance
    @Column(name = "sla_deadline")
    private LocalDateTime slaDeadline;

    @Column(name = "sla_status", length = 20)
    @Size(max = 20, message = "SLA status must not exceed 20 characters")
    private String slaStatus;

    @Column(name = "priority_reason", length = 500)
    @Size(max = 500, message = "Priority reason must not exceed 500 characters")
    private String priorityReason;

    // Quality Control
    @Column(name = "quality_check_required", nullable = false)
    private Boolean qualityCheckRequired = false;

    @Column(name = "quality_check_completed", nullable = false)
    private Boolean qualityCheckCompleted = false;

    @Column(name = "quality_check_user_id")
    private Long qualityCheckUserId;

    @Column(name = "quality_check_date")
    private LocalDateTime qualityCheckDate;

    @Column(name = "quality_notes", columnDefinition = "TEXT")
    private String qualityNotes;

    @Column(name = "quality_status", length = 30)
    @Size(max = 30, message = "Quality status must not exceed 30 characters")
    private String qualityStatus;

    // Inventory Status Fields
    @Column(name = "inventory_status", length = 30)
    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus = InventoryStatus.PENDING;
    
    @Column(name = "inventory_reservation_id")
    private String inventoryReservationId;
    
    @Column(name = "inventory_reservation_expires_at")
    private LocalDateTime inventoryReservationExpiresAt;
    
    // Configuration Flags
    @Column(name = "is_gift", nullable = false)
    private Boolean isGift = false;

    @Column(name = "gift_message", columnDefinition = "TEXT")
    private String giftMessage;

    @Column(name = "requires_signature", nullable = false)
    private Boolean requiresSignature = false;

    @Column(name = "is_fragile", nullable = false)
    private Boolean isFragile = false;

    @Column(name = "requires_age_verification", nullable = false)
    private Boolean requiresAgeVerification = false;

    @Column(name = "is_hazmat", nullable = false)
    private Boolean isHazmat = false;

    @Column(name = "hazmat_class", length = 50)
    @Size(max = 50, message = "Hazmat class must not exceed 50 characters")
    private String hazmatClass;
    
    @Column(name = "is_expedited", nullable = false)
    private Boolean isExpedited = false;

    // Return Information
    @Column(name = "is_return", nullable = false)
    private Boolean isReturn = false;

    @Column(name = "original_order_id", length = 100)
    @Size(max = 100, message = "Original order ID must not exceed 100 characters")
    private String originalOrderId;

    @Column(name = "return_reason", length = 500)
    @Size(max = 500, message = "Return reason must not exceed 500 characters")
    private String returnReason;

    @Column(name = "return_label_url", length = 1000)
    @Size(max = 1000, message = "Return label URL must not exceed 1000 characters")
    private String returnLabelUrl;

    // Notes and Comments
    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "shipping_notes", columnDefinition = "TEXT")
    private String shippingNotes;
    
    @Column(name = "picking_instructions", columnDefinition = "TEXT")
    private String pickingInstructions;
    
    @Column(name = "packing_instructions", columnDefinition = "TEXT")
    private String packingInstructions;
    
    @Column(name = "shipping_instructions", columnDefinition = "TEXT")
    private String shippingInstructions;

    // Metadata
    @Column(name = "source_system", length = 50)
    @Size(max = 50, message = "Source system must not exceed 50 characters")
    private String sourceSystem;

    @Column(name = "source_channel", length = 50)
    @Size(max = 50, message = "Source channel must not exceed 50 characters")
    private String sourceChannel;

    @Column(name = "tags", columnDefinition = "JSON")
    @Convert(converter = JsonAttributeConverter.class)
    private Map<String, String> tags;

    @Column(name = "custom_attributes", columnDefinition = "JSON")
    @Convert(converter = JsonAttributeConverter.class)
    private Map<String, Object> customAttributes;

    // Relationships
    @OneToMany(mappedBy = "fulfillmentOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FulfillmentOrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "fulfillmentOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("eventTime DESC")
    @Builder.Default
    private List<FulfillmentEvent> events = new ArrayList<>();

    // Helper methods
    public void addOrderItem(FulfillmentOrderItem item) {
        orderItems.add(item);
        item.setFulfillmentOrder(this);
    }

    public void addEvent(FulfillmentEvent event) {
        events.add(event);
        event.setFulfillmentOrder(this);
    }

    public BigDecimal calculateTotalWeight() {
        return orderItems.stream()
                .map(item -> item.getWeight() != null && item.getQuantity() != null 
                    ? item.getWeight().multiply(BigDecimal.valueOf(item.getQuantity())) 
                    : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean requiresSpecialHandling() {
        return isFragile || requiresAgeVerification || 
               orderItems.stream().anyMatch(item -> item.getRequiresSpecialHandling());
    }
    
    public void setInventoryReservationId(UUID reservationId) {
        this.inventoryReservationId = reservationId != null ? reservationId.toString() : null;
    }
    
    public UUID getInventoryReservationId() {
        return inventoryReservationId != null ? UUID.fromString(inventoryReservationId) : null;
    }
    
    public void addNote(String note) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.toString();
        String newNote = "[" + timestamp + "] " + note;
        
        if (this.internalNotes == null || this.internalNotes.isEmpty()) {
            this.internalNotes = newNote;
        } else {
            this.internalNotes = this.internalNotes + "\n" + newNote;
        }
    }
    
    // Missing getter methods - removed duplicate getId() and getCreatedAt() as they're in BaseEntity
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public String getExternalOrderId() {
        return externalOrderId;
    }
    
    public FulfillmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(FulfillmentStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public List<FulfillmentOrderItem> getOrderItems() {
        return orderItems;
    }
    
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }
    
    public void setPickStartedAt(LocalDateTime pickStartedAt) {
        this.pickStartedAt = pickStartedAt;
    }
    
    public void setPickCompletedAt(LocalDateTime pickCompletedAt) {
        this.pickCompletedAt = pickCompletedAt;
    }
    
    public void setPackStartedAt(LocalDateTime packStartedAt) {
        this.packStartedAt = packStartedAt;
    }
    
    public void setPackCompletedAt(LocalDateTime packCompletedAt) {
        this.packCompletedAt = packCompletedAt;
    }
    
    // Additional setter methods for compatibility
    public void setPickingStartedAt(LocalDateTime pickingStartedAt) {
        this.pickStartedAt = pickingStartedAt;
    }
    
    public void setPickingCompletedAt(LocalDateTime pickingCompletedAt) {
        this.pickCompletedAt = pickingCompletedAt;
    }
    
    public void setPackingStartedAt(LocalDateTime packingStartedAt) {
        this.packStartedAt = packingStartedAt;
    }
    
    public void setPackingCompletedAt(LocalDateTime packingCompletedAt) {
        this.packCompletedAt = packingCompletedAt;
    }
    
    // Order reference getter (using order number as reference)
    public String getOrderReference() {
        return orderNumber;
    }
    
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public Long getWarehouseId() {
        return warehouseId;
    }
    
    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }
    
    // Helper method to check completion
    public boolean isCompleted() {
        return status == FulfillmentStatus.COMPLETED || 
               status == FulfillmentStatus.DELIVERED || 
               status == FulfillmentStatus.SHIPPED;
    }
    
    // Check if order is ready for picking
    public boolean isReadyForPicking() {
        return status == FulfillmentStatus.READY_FOR_PICKING || 
               status == FulfillmentStatus.ALLOCATED;
    }
    
    // Get and set inventory status
    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }
    
    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }
    
    // Set inventory expiration
    public void setInventoryReservationExpiresAt(LocalDateTime expiresAt) {
        this.inventoryReservationExpiresAt = expiresAt;
    }
    
    // Setter for assigned picker ID
    public void setAssignedPickerId(Long assignedPickerId) {
        this.assignedPickerId = assignedPickerId;
    }
    
    // Additional methods needed by service layer
    public void setAssignedWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId != null ? warehouseId.getMostSignificantBits() & Long.MAX_VALUE : null;
    }
    
    public void setReassignedAt(LocalDateTime reassignedAt) {
        // Using internal notes to track reassignment
        addNote("Reassigned at: " + reassignedAt);
    }
    
    public void setReassignmentReason(String reason) {
        // Using internal notes to track reassignment reason
        addNote("Reassignment reason: " + reason);
    }
    
    public void setBackorderedAt(LocalDateTime backorderedAt) {
        addNote("Backordered at: " + backorderedAt);
    }
    
    public void setBackorderReason(String reason) {
        addNote("Backorder reason: " + reason);
    }
    
    public void setHoldStartedAt(LocalDateTime holdStartedAt) {
        addNote("Hold started at: " + holdStartedAt);
    }
    
    public void setHoldReason(String reason) {
        addNote("Hold reason: " + reason);
    }
    
    public void setProcessingStartedAt(LocalDateTime processingStartedAt) {
        // Using pickStartedAt field for processing started timestamp
        this.pickStartedAt = processingStartedAt;
    }
    
    public List<FulfillmentOrderItem> getItems() {
        return this.orderItems;
    }
    
    // Convenience methods for shipping address access
    public String getShippingCity() {
        return shippingAddress != null ? shippingAddress.getCity() : null;
    }
    
    public String getShippingAddressLine1() {
        return shippingAddress != null ? shippingAddress.getAddressLine1() : null;
    }
    
    public String getShippingStateProvince() {
        return shippingAddress != null ? shippingAddress.getState() : null;
    }
    
    public String getShippingPostalCode() {
        return shippingAddress != null ? shippingAddress.getPostalCode() : null;
    }
    
    public String getShippingCountry() {
        return shippingAddress != null ? shippingAddress.getCountry() : null;
    }
    
    // Calculate total item count
    public Integer getTotalItemCount() {
        return orderItems.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
    }
}

/**
 * JSON Attribute Converter for Map storage
 */
@Converter
class JsonAttributeConverter implements AttributeConverter<Map<String, Object>, String> {
    // Implementation would go here - using Jackson or similar
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        // Convert Map to JSON string
        return "{}"; // Placeholder
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        // Convert JSON string to Map
        return new java.util.HashMap<>(); // Placeholder
    }
}
