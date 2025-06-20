package com.exalt.warehousing.inventory.enums;

/**
 * Inventory Status Enumeration
 * 
 * Defines the operational status of inventory items within the warehouse system.
 * Each status represents a specific state in the inventory lifecycle with
 * associated business rules and operational constraints.
 * 
 * Status Categories:
 * - OPERATIONAL: Items available for normal warehouse operations
 * - RESTRICTED: Items with limitations on use or movement
 * - MAINTENANCE: Items undergoing quality or system processes
 * - PROBLEM: Items with issues requiring resolution
 * 
 * @author Inventory Management Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum InventoryStatus {
    
    /**
     * Item is being received into the warehouse
     * - Not yet available for sale
     * - Being processed through receiving workflow
     * - Initial status for new inventory
     */
    RECEIVING("Receiving", "Item is being received into warehouse", false, false, true),
    
    /**
     * Item is available for sale and fulfillment
     * - Full access for reservations and allocations
     * - Appears in customer-facing inventory
     * - Normal warehouse operations allowed
     */
    AVAILABLE("Available", "Item is ready for sale and fulfillment", true, true, true),
    
    /**
     * Item is temporarily reserved for specific orders
     * - Cannot be allocated to new orders
     * - Still counts toward total inventory
     * - Awaiting fulfillment or release
     */
    RESERVED("Reserved", "Item is reserved for specific orders", false, true, true),
    
    /**
     * Item is allocated for active fulfillment
     * - Assigned to picking/packing process
     * - Cannot be reallocated
     * - In progress of being shipped
     */
    ALLOCATED("Allocated", "Item is allocated for active fulfillment", false, false, true),
    
    /**
     * Item is on hold due to quality concerns
     * - Cannot be sold or allocated
     * - Pending quality inspection
     * - May require quarantine
     */
    QUALITY_HOLD("Quality Hold", "Item on hold pending quality inspection", false, false, false),
    
    /**
     * Item is damaged and cannot be sold
     * - Removed from sellable inventory
     * - May be repairable or disposable
     * - Requires damage assessment
     */
    DAMAGED("Damaged", "Item is damaged and cannot be sold", false, false, false),
    
    /**
     * Item has expired and cannot be sold
     * - Removed from sellable inventory
     * - Requires proper disposal
     * - May have regulatory implications
     */
    EXPIRED("Expired", "Item has expired and cannot be sold", false, false, false),
    
    /**
     * Item is recalled by manufacturer
     * - Immediate removal from sale
     * - Special handling required
     * - May require return to vendor
     */
    RECALLED("Recalled", "Item is subject to manufacturer recall", false, false, false),
    
    /**
     * Item is lost or cannot be located
     * - Physical inventory discrepancy
     * - Requires investigation
     * - May need cycle count verification
     */
    MISSING("Missing", "Item cannot be located in warehouse", false, false, false),
    
    /**
     * Item is being counted during cycle count
     * - Temporarily unavailable for operations
     * - Physical verification in progress
     * - Operations resume after count completion
     */
    CYCLE_COUNT("Cycle Count", "Item being verified during cycle count", false, false, true),
    
    /**
     * Item is quarantined for investigation
     * - Complete isolation from operations
     * - Investigation or testing in progress
     * - Special authorization required for access
     */
    QUARANTINED("Quarantined", "Item is quarantined pending investigation", false, false, false),
    
    /**
     * Item is being transferred between locations
     * - Temporarily unavailable
     * - In transit within warehouse network
     * - Normal operations resume upon arrival
     */
    IN_TRANSIT("In Transit", "Item is being transferred between locations", false, false, true),
    
    /**
     * Item is discontinued and no longer sold
     * - No new reservations allowed
     * - Existing inventory may be sold down
     * - No replenishment orders
     */
    DISCONTINUED("Discontinued", "Item is discontinued and no longer stocked", false, true, false),
    
    /**
     * Item is inactive in the system
     * - No operations allowed
     * - Hidden from most views
     * - Requires reactivation for use
     */
    INACTIVE("Inactive", "Item is inactive in the system", false, false, false);

    private final String displayName;
    private final String description;
    private final boolean canReserve;
    private final boolean countTowardInventory;
    private final boolean allowMovement;

    /**
     * Constructs an InventoryStatus with specified properties.
     * 
     * @param displayName User-friendly name for the status
     * @param description Detailed description of the status
     * @param canReserve Whether items with this status can be reserved
     * @param countTowardInventory Whether items count toward total inventory
     * @param allowMovement Whether items can be moved in warehouse
     */
    InventoryStatus(String displayName, String description, boolean canReserve,
                   boolean countTowardInventory, boolean allowMovement) {
        this.displayName = displayName;
        this.description = description;
        this.canReserve = canReserve;
        this.countTowardInventory = countTowardInventory;
        this.allowMovement = allowMovement;
    }

    /**
     * Gets the user-friendly display name.
     * 
     * @return Display name for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the detailed description of the status.
     * 
     * @return Description of what this status means
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if items with this status can be reserved for orders.
     * 
     * @return true if reservations are allowed
     */
    public boolean canReserve() {
        return canReserve;
    }

    /**
     * Checks if items with this status count toward total inventory.
     * 
     * @return true if items count toward inventory totals
     */
    public boolean countTowardInventory() {
        return countTowardInventory;
    }

    /**
     * Checks if items with this status can be moved within warehouse.
     * 
     * @return true if warehouse movement is allowed
     */
    public boolean allowMovement() {
        return allowMovement;
    }

    /**
     * Checks if this status indicates a problem requiring attention.
     * 
     * @return true if status indicates issues
     */
    public boolean requiresAttention() {
        return this == QUALITY_HOLD || this == DAMAGED || this == RECALLED || 
               this == MISSING || this == QUARANTINED;
    }

    /**
     * Checks if this status indicates items available for customer purchase.
     * 
     * @return true if available for customer orders
     */
    public boolean isAvailableForSale() {
        return this == AVAILABLE;
    }

    /**
     * Checks if this status allows item allocation for fulfillment.
     * 
     * @return true if allocation is permitted
     */
    public boolean canAllocate() {
        return this == RESERVED;
    }

    /**
     * Checks if this status is temporary and may change automatically.
     * 
     * @return true if status is temporary
     */
    public boolean isTemporary() {
        return this == RESERVED || this == ALLOCATED || this == CYCLE_COUNT || this == IN_TRANSIT;
    }

    /**
     * Gets all statuses that are available for customer orders.
     * 
     * @return Array of customer-available statuses
     */
    public static InventoryStatus[] getCustomerAvailableStatuses() {
        return new InventoryStatus[]{AVAILABLE};
    }

    /**
     * Gets all statuses that count toward sellable inventory.
     * 
     * @return Array of sellable statuses
     */
    public static InventoryStatus[] getSellableStatuses() {
        return new InventoryStatus[]{AVAILABLE, RESERVED, ALLOCATED};
    }

    /**
     * Gets all statuses that require immediate attention.
     * 
     * @return Array of attention-required statuses
     */
    public static InventoryStatus[] getAttentionStatuses() {
        return new InventoryStatus[]{QUALITY_HOLD, DAMAGED, RECALLED, MISSING, QUARANTINED};
    }

    /**
     * Gets all statuses that indicate problems or issues.
     * 
     * @return Array of problem statuses
     */
    public static InventoryStatus[] getProblemStatuses() {
        return new InventoryStatus[]{DAMAGED, EXPIRED, RECALLED, MISSING, QUARANTINED};
    }

    /**
     * Gets all operational statuses (normal business operations).
     * 
     * @return Array of operational statuses
     */
    public static InventoryStatus[] getOperationalStatuses() {
        return new InventoryStatus[]{AVAILABLE, RESERVED, ALLOCATED, CYCLE_COUNT, IN_TRANSIT};
    }

    /**
     * Validates if transition from current status to target status is allowed.
     * 
     * @param targetStatus The status to transition to
     * @return true if transition is valid
     */
    public boolean canTransitionTo(InventoryStatus targetStatus) {
        return switch (this) {
            case RECEIVING -> targetStatus == AVAILABLE || targetStatus == QUALITY_HOLD ||
                           targetStatus == DAMAGED || targetStatus == QUARANTINED;
            
            case AVAILABLE -> targetStatus == RESERVED || targetStatus == QUALITY_HOLD ||
                            targetStatus == CYCLE_COUNT || targetStatus == IN_TRANSIT ||
                            targetStatus == DAMAGED || targetStatus == DISCONTINUED ||
                            targetStatus == INACTIVE;
            
            case RESERVED -> targetStatus == AVAILABLE || targetStatus == ALLOCATED ||
                           targetStatus == QUALITY_HOLD || targetStatus == MISSING;
            
            case ALLOCATED -> targetStatus == AVAILABLE || targetStatus == MISSING ||
                            targetStatus == DAMAGED; // After fulfillment or issues
            
            case QUALITY_HOLD -> targetStatus == AVAILABLE || targetStatus == DAMAGED ||
                               targetStatus == QUARANTINED || targetStatus == RECALLED;
            
            case DAMAGED -> targetStatus == AVAILABLE || targetStatus == INACTIVE; // After repair
            
            case EXPIRED -> targetStatus == INACTIVE; // Usually terminal, but can deactivate
            
            case RECALLED -> targetStatus == INACTIVE; // After recall processing
            
            case MISSING -> targetStatus == AVAILABLE || targetStatus == INACTIVE; // After found
            
            case CYCLE_COUNT -> targetStatus == AVAILABLE || targetStatus == MISSING ||
                              targetStatus == DAMAGED; // After count completion
            
            case QUARANTINED -> targetStatus == AVAILABLE || targetStatus == DAMAGED ||
                              targetStatus == RECALLED || targetStatus == INACTIVE;
            
            case IN_TRANSIT -> targetStatus == AVAILABLE || targetStatus == MISSING ||
                             targetStatus == DAMAGED; // After arrival
            
            case DISCONTINUED -> targetStatus == INACTIVE; // Gradual phase-out
            
            case INACTIVE -> targetStatus == AVAILABLE; // Reactivation
        };
    }

    /**
     * Gets the priority level for handling items with this status.
     * Higher numbers indicate higher priority.
     * 
     * @return Priority level (1-5)
     */
    public int getPriorityLevel() {
        return switch (this) {
            case RECALLED -> 5; // Highest priority - safety/legal
            case QUARANTINED -> 4; // High priority - potential safety issue
            case MISSING -> 4; // High priority - inventory discrepancy
            case DAMAGED -> 3; // Medium-high priority - affects sellability
            case QUALITY_HOLD -> 3; // Medium-high priority - quality concern
            case EXPIRED -> 2; // Medium priority - needs disposal
            case CYCLE_COUNT -> 2; // Medium priority - temporary state
            case IN_TRANSIT -> 2; // Medium priority - temporary state
            default -> 1; // Normal priority
        };
    }

    /**
     * Gets the recommended action for items with this status.
     * 
     * @return Suggested action to take
     */
    public String getRecommendedAction() {
        return switch (this) {
            case RECEIVING -> "Complete receiving process and update inventory";
            case AVAILABLE -> "Monitor stock levels and process orders normally";
            case RESERVED -> "Process allocated orders within SLA timeframes";
            case ALLOCATED -> "Complete fulfillment and shipping process";
            case QUALITY_HOLD -> "Conduct quality inspection and resolve issues";
            case DAMAGED -> "Assess damage and determine repair vs disposal";
            case EXPIRED -> "Remove from inventory and dispose per regulations";
            case RECALLED -> "Follow recall procedures and return to vendor";
            case MISSING -> "Conduct physical search and investigate discrepancy";
            case CYCLE_COUNT -> "Complete cycle count and reconcile discrepancies";
            case QUARANTINED -> "Complete investigation and determine next steps";
            case IN_TRANSIT -> "Confirm arrival and update location";
            case DISCONTINUED -> "Sell down remaining inventory and prevent reorders";
            case INACTIVE -> "Review for potential reactivation or removal";
        };
    }
}