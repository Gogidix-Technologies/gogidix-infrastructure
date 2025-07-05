package com.exalt.warehousing.management.model;

/**
 * Represents the possible statuses of a warehouse task
 */
public enum TaskStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    ON_HOLD,
    BLOCKED;

    /**
     * Determine if this status can legally transition to the provided target status.
     * The rules encode a normal warehouse-task life-cycle.
     */
    public boolean canTransitionTo(TaskStatus target) {
        if (target == null) return false;
        switch (this) {
            case PENDING:
                return target == ASSIGNED || target == ON_HOLD || target == CANCELLED;
            case ASSIGNED:
                return target == IN_PROGRESS || target == ON_HOLD || target == CANCELLED;
            case IN_PROGRESS:
                return target == COMPLETED || target == ON_HOLD || target == CANCELLED;
            case ON_HOLD:
                return target == IN_PROGRESS || target == CANCELLED;
            case BLOCKED:
                return target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED:
            case CANCELLED:
            default:
                return false; // terminal states
        }
    }

    /**
     * @return true if the status is terminal (no further transitions allowed)
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
} 
