package com.exalt.warehousing.fulfillment.enums;

/**
 * Enum representing the possible statuses of a task
 */
public enum TaskStatus {
    PENDING("Task is pending"),
    ASSIGNED("Task has been assigned but not started"),
    IN_PROGRESS("Task is in progress"),
    COMPLETED("Task has been completed"),
    CANCELLED("Task has been cancelled"),
    ON_HOLD("Task is on hold");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}