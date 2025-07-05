package com.exalt.warehousing.inventory.enums;

/**
 * Enumeration for different types of inventory transactions
 */
public enum TransactionType {
    
    // Incoming transactions (positive quantity)
    PURCHASE("Purchase", "Items received from supplier", true),
    RETURN_FROM_CUSTOMER("Return from Customer", "Items returned by customers", true),
    TRANSFER_IN("Transfer In", "Items transferred from another location", true),
    ADJUSTMENT_INCREASE("Adjustment Increase", "Manual adjustment increasing stock", true),
    PRODUCTION_OUTPUT("Production Output", "Items produced internally", true),
    FOUND("Found", "Items found during cycle count", true),
    
    // Outgoing transactions (negative quantity)
    SALE("Sale", "Items sold to customers", false),
    RETURN_TO_SUPPLIER("Return to Supplier", "Items returned to supplier", false),
    TRANSFER_OUT("Transfer Out", "Items transferred to another location", false),
    ADJUSTMENT_DECREASE("Adjustment Decrease", "Manual adjustment decreasing stock", false),
    PRODUCTION_INPUT("Production Input", "Items used in production", false),
    DAMAGE("Damage", "Items damaged and removed from stock", false),
    LOSS("Loss", "Items lost or stolen", false),
    EXPIRED("Expired", "Items removed due to expiration", false),
    
    // Special transactions
    CYCLE_COUNT("Cycle Count", "Inventory count adjustment", null),
    RESERVATION("Reservation", "Items reserved for orders", false),
    UNRESERVATION("Unreservation", "Reserved items released back to stock", true),
    PICK("Pick", "Items picked for order fulfillment", false),
    UNPICK("Unpick", "Picked items returned to stock", true),
    
    // System transactions
    SYSTEM_CORRECTION("System Correction", "System-generated correction", null),
    MIGRATION("Migration", "Data migration transaction", null),
    RECONCILIATION("Reconciliation", "Inventory reconciliation", null);

    private final String displayName;
    private final String description;
    private final Boolean isIncoming; // true = incoming, false = outgoing, null = neutral

    TransactionType(String displayName, String description, Boolean isIncoming) {
        this.displayName = displayName;
        this.description = description;
        this.isIncoming = isIncoming;
    }

    /**
     * Get the display name for UI purposes
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the transaction type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Check if this transaction type represents incoming inventory
     */
    public Boolean isIncoming() {
        return isIncoming;
    }

    /**
     * Check if this transaction type represents outgoing inventory
     */
    public Boolean isOutgoing() {
        return isIncoming != null && !isIncoming;
    }

    /**
     * Check if this transaction type is neutral (doesn't affect stock)
     */
    public Boolean isNeutral() {
        return isIncoming == null;
    }

    /**
     * Get the expected quantity sign for this transaction type
     * @return 1 for incoming, -1 for outgoing, 0 for neutral
     */
    public int getQuantitySign() {
        if (isIncoming == null) return 0;
        return isIncoming ? 1 : -1;
    }

    /**
     * Get all incoming transaction types
     */
    public static TransactionType[] getIncomingTypes() {
        return new TransactionType[]{
            PURCHASE, RETURN_FROM_CUSTOMER, TRANSFER_IN, ADJUSTMENT_INCREASE,
            PRODUCTION_OUTPUT, FOUND, UNRESERVATION, UNPICK
        };
    }

    /**
     * Get all outgoing transaction types
     */
    public static TransactionType[] getOutgoingTypes() {
        return new TransactionType[]{
            SALE, RETURN_TO_SUPPLIER, TRANSFER_OUT, ADJUSTMENT_DECREASE,
            PRODUCTION_INPUT, DAMAGE, LOSS, EXPIRED, RESERVATION, PICK
        };
    }

    /**
     * Get all neutral transaction types
     */
    public static TransactionType[] getNeutralTypes() {
        return new TransactionType[]{
            CYCLE_COUNT, SYSTEM_CORRECTION, MIGRATION, RECONCILIATION
        };
    }

    /**
     * Check if the transaction type requires a reference ID
     */
    public boolean requiresReference() {
        return this == SALE || this == PURCHASE || this == RETURN_FROM_CUSTOMER ||
               this == RETURN_TO_SUPPLIER || this == TRANSFER_IN || this == TRANSFER_OUT ||
               this == RESERVATION || this == UNRESERVATION || this == PICK || this == UNPICK;
    }

    /**
     * Check if the transaction type allows manual creation
     */
    public boolean allowsManualCreation() {
        return this != SYSTEM_CORRECTION && this != MIGRATION && this != RECONCILIATION;
    }

    /**
     * Get the opposite transaction type if applicable
     */
    public TransactionType getOpposite() {
        switch (this) {
            case RESERVATION: return UNRESERVATION;
            case UNRESERVATION: return RESERVATION;
            case PICK: return UNPICK;
            case UNPICK: return PICK;
            case TRANSFER_IN: return TRANSFER_OUT;
            case TRANSFER_OUT: return TRANSFER_IN;
            case ADJUSTMENT_INCREASE: return ADJUSTMENT_DECREASE;
            case ADJUSTMENT_DECREASE: return ADJUSTMENT_INCREASE;
            default: return null;
        }
    }
}