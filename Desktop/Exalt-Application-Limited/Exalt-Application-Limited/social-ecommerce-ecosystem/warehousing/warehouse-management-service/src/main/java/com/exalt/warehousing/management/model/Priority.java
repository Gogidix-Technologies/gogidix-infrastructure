package com.exalt.warehousing.management.model;

/**
 * Represents the priority levels for warehouse tasks
 */
public enum Priority {
    CRITICAL(1),
    HIGH(2),
    MEDIUM(3),
    LOW(4),
    ROUTINE(5);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
} 
