package com.exalt.warehousing.fulfillment.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Status values for fulfillment orders
 */
public enum FulfillmentStatus {
    NEW("New", "Order newly created", 0),
    PENDING("Pending", "Pending processing", 1),
    RECEIVED("Received", "Order received for fulfillment", 2),
    VALIDATED("Validated", "Order validated for processing", 3),
    INVENTORY_RESERVED("Inventory Reserved", "Inventory has been reserved", 4),
    PROCESSING("Processing", "Order is being processed", 5),
    ALLOCATED("Allocated", "Inventory allocated", 6),
    READY_FOR_PICKING("Ready for Picking", "Ready to be picked", 7),
    PICKING_ASSIGNED("Picking Assigned", "Assigned to picker", 8),
    PICKING("Picking", "Items being picked from warehouse", 9),
    PICKING_COMPLETE("Picking Complete", "All items have been picked", 10),
    PACKING("Packing", "Items being packed for shipment", 11),
    PACKING_COMPLETE("Packing Complete", "All items have been packed", 12),
    READY_TO_SHIP("Ready to Ship", "Ready for shipping", 13),
    COMPLETED("Completed", "Fulfillment completed", 14),
    SHIPPED("Shipped", "Order has been shipped", 15),
    DELIVERED("Delivered", "Order has been delivered", 16),
    CANCELLED("Cancelled", "Order has been cancelled", 17),
    ON_HOLD("On Hold", "Order is on hold", 18),
    RETURNED("Returned", "Order has been returned", 19),
    REFUNDED("Refunded", "Order has been refunded", 20),
    FAILED("Failed", "Fulfillment failed", 21),
    PENDING_INVENTORY("Pending Inventory", "Waiting for inventory", 22),
    BACKORDERED("Backordered", "Items are backordered", 23);

    private final String displayName;
    private final String description;
    private final int sequence;

    FulfillmentStatus(String displayName, String description, int sequence) {
        this.displayName = displayName;
        this.description = description;
        this.sequence = sequence;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getSequence() {
        return sequence;
    }

    /**
     * Check if this status represents an in-progress state
     */
    public boolean isInProgress() {
        return this == PROCESSING || this == PICKING || this == PACKING || 
               this == ALLOCATED || this == READY_FOR_PICKING;
    }

    /**
     * Check if this status represents a completed state
     */
    public boolean isCompleted() {
        return this == COMPLETED || this == SHIPPED || this == DELIVERED;
    }

    /**
     * Check if this status represents a terminal state
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == SHIPPED || this == DELIVERED || 
               this == CANCELLED || this == RETURNED || this == REFUNDED || this == FAILED;
    }

    /**
     * Check if this status can transition to another status
     */
    public boolean canTransitionTo(FulfillmentStatus targetStatus) {
        // Terminal states cannot transition to any other state
        if (this.isTerminal()) {
            return false;
        }
        
        // Check valid transitions based on sequence
        switch (this) {
            case NEW:
                return targetStatus == PENDING || targetStatus == CANCELLED;
            case PENDING:
                return targetStatus == RECEIVED || targetStatus == CANCELLED || targetStatus == ON_HOLD;
            case RECEIVED:
                return targetStatus == VALIDATED || targetStatus == PROCESSING || targetStatus == CANCELLED || targetStatus == ON_HOLD;
            case VALIDATED:
                return targetStatus == INVENTORY_RESERVED || targetStatus == PENDING_INVENTORY || targetStatus == CANCELLED;
            case INVENTORY_RESERVED:
                return targetStatus == PROCESSING || targetStatus == ALLOCATED || targetStatus == CANCELLED;
            case PROCESSING:
                return targetStatus == ALLOCATED || targetStatus == CANCELLED || targetStatus == ON_HOLD || targetStatus == FAILED;
            case ALLOCATED:
                return targetStatus == READY_FOR_PICKING || targetStatus == CANCELLED;
            case READY_FOR_PICKING:
                return targetStatus == PICKING_ASSIGNED || targetStatus == PICKING || targetStatus == CANCELLED;
            case PICKING_ASSIGNED:
                return targetStatus == PICKING || targetStatus == CANCELLED;
            case PICKING:
                return targetStatus == PICKING_COMPLETE || targetStatus == CANCELLED || targetStatus == ON_HOLD;
            case PICKING_COMPLETE:
                return targetStatus == PACKING || targetStatus == CANCELLED;
            case PACKING:
                return targetStatus == PACKING_COMPLETE || targetStatus == CANCELLED || targetStatus == ON_HOLD;
            case PACKING_COMPLETE:
                return targetStatus == READY_TO_SHIP || targetStatus == CANCELLED;
            case READY_TO_SHIP:
                return targetStatus == SHIPPED || targetStatus == CANCELLED;
            case COMPLETED:
                return targetStatus == SHIPPED || targetStatus == CANCELLED || targetStatus == RETURNED;
            case SHIPPED:
                return targetStatus == DELIVERED || targetStatus == RETURNED;
            case ON_HOLD:
                return targetStatus == PROCESSING || targetStatus == CANCELLED;
            case PENDING_INVENTORY:
                return targetStatus == INVENTORY_RESERVED || targetStatus == BACKORDERED || targetStatus == CANCELLED;
            case BACKORDERED:
                return targetStatus == INVENTORY_RESERVED || targetStatus == CANCELLED;
            default:
                return false;
        }
    }

    /**
     * Get statuses that represent active processing
     */
    public static List<FulfillmentStatus> getActiveStatuses() {
        return Arrays.asList(NEW, PENDING, RECEIVED, PROCESSING, ALLOCATED, 
                           READY_FOR_PICKING, PICKING, PICKING_COMPLETE, 
                           PACKING, PACKING_COMPLETE);
    }

    /**
     * Get statuses that represent completed states
     */
    public static List<FulfillmentStatus> getCompletedStatuses() {
        return Arrays.asList(COMPLETED, SHIPPED, DELIVERED);
    }
    
    /**
     * Get statuses that represent exception states
     */
    public static List<FulfillmentStatus> getExceptionStatuses() {
        return Arrays.asList(ON_HOLD, FAILED, PENDING_INVENTORY, BACKORDERED);
    }
}
