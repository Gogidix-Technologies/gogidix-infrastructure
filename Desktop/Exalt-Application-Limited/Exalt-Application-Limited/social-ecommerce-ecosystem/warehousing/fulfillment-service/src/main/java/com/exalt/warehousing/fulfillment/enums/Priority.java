package com.exalt.warehousing.fulfillment.enums;

/**
 * Priority levels for fulfillment orders
 */
public enum Priority {
    CRITICAL("Critical", "Highest priority - immediate attention required", 1),
    HIGH("High", "High priority - process as soon as possible", 2),
    STANDARD("Standard", "Normal priority - standard processing", 3),
    LOW("Low", "Lower priority - process when convenient", 4),
    BULK("Bulk", "Bulk order - lowest priority", 5);

    private final String displayName;
    private final String description;
    private final int level;

    Priority(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Check if this priority is higher than another
     */
    public boolean isHigherThan(Priority other) {
        return this.level < other.level;
    }

    /**
     * Get priority by level
     */
    public static Priority getByLevel(int level) {
        for (Priority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return STANDARD; // Default
    }
}
