package com.exalt.warehousing.fulfillment.model;

/**
 * Enum representing the possible statuses of a return request
 */
public enum ReturnStatus {
    PENDING("Return request submitted, awaiting approval"),
    AUTHORIZED("Return has been authorized"),
    REJECTED("Return request has been rejected"),
    IN_TRANSIT("Return items are in transit to warehouse"),
    RECEIVED("Return items have been received at warehouse"),
    INSPECTING("Return items are being inspected"),
    PROCESSED("Return has been processed"),
    COMPLETED("Return has been completed and refund issued"),
    CANCELLED("Return has been cancelled"),
    PARTIAL("Return was partially accepted");

    private final String description;

    ReturnStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
